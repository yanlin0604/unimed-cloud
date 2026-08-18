package org.dromara.chronic.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.config.BaiduOcrProperties;
import org.dromara.chronic.domain.bo.OcrConfirmBo;
import org.dromara.chronic.domain.bo.OcrTaskBo;
import org.dromara.chronic.domain.vo.OcrConfirmResult;
import org.dromara.chronic.domain.vo.OcrTaskVo;
import org.dromara.chronic.service.IOcrService;
import org.dromara.chronic.support.ocr.BaiduOcrClient;
import org.dromara.chronic.support.ocr.OcrParser;
import org.dromara.chronic.support.ocr.domain.BaiduOcrRequest;
import org.dromara.chronic.support.ocr.domain.BaiduOcrResponse;
import org.dromara.chronic.support.ocr.domain.OcrParseResult;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * 医疗文档OCR业务编排
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OcrManager {

    /** 输入类型白名单，与字典 chronic_ocr_input_type 一致 */
    private static final Set<String> VALID_INPUT_TYPES = Set.of("IMAGE_BASE64", "IMAGE_URL", "OSS_FILE", "PDF_FILE");

    private static final String INPUT_TYPE_HINT = "，可选值：IMAGE_BASE64/IMAGE_URL/OSS_FILE/PDF_FILE";

    /** 仅识别失败的任务允许重试 */
    private static final String STATUS_FAILED = "FAILED";

    private final IOcrService ocrService;
    private final BaiduOcrClient baiduOcrClient;
    private final OcrParser parser;
    private final OcrConfirmRouter confirmRouter;
    private final BaiduOcrProperties ocrProperties;

    /**
     * 识别医疗文档：拆分事务边界——createTask、markRecognizing、markSuccess、markFailed
     * 各自由 service 层独立事务提交，OCR 远程网络调用置于事务之外，避免长事务占用 DB 连接，
     * 也避免失败时 markFailed 写入随外层事务一并回滚导致任务永远停留在 PROCESSING 状态。
     */
    public Long recognize(OcrTaskBo bo) {
        // 1. inputType 白名单校验（BaiduOcrClient 依据 inputType 分派识别通道，非法值必须提前拒绝）
        validateInputType(bo);
        // 2. 文件大小校验（防止超大 base64 透传到百度）
        validateFileSize(bo);
        // 3. fileMd5 去重：相同患者 + 相同 md5 已成功识别过，直接复用旧任务
        //    注意：querySuccessByFileMd5 只匹配 status=SUCCESS 的任务，
        //    因此 FAILED 任务重试时即使沿用同一 fileMd5，也不会被自身的历史记录命中。
        if (StringUtils.isNotBlank(bo.getFileMd5())) {
            OcrTaskVo existed = ocrService.querySuccessByFileMd5(bo.getPatientId(), bo.getFileMd5());
            if (existed != null) {
                log.info("命中已成功OCR任务，复用结果, taskId={}, fileMd5={}", existed.getTaskId(), bo.getFileMd5());
                return existed.getTaskId();
            }
        }
        Long taskId = ocrService.createTask(bo);
        ocrService.markRecognizing(taskId);
        try {
            BaiduOcrRequest request = new BaiduOcrRequest();
            request.setDocumentType(bo.getDocumentType());
            request.setInputType(bo.getInputType());
            request.setImageBase64(bo.getImageBase64());
            request.setPdfBase64(bo.getPdfBase64());
            request.setFileUrl(bo.getFileUrl());
            BaiduOcrResponse response = baiduOcrClient.recognize(request);
            if (!response.isSuccess()) {
                ocrService.markFailed(taskId, response.getErrorCode(), response.getErrorMsg(), response.getRawJson());
                throw new ServiceException(response.getErrorMsg());
            }
            OcrParseResult result = parser.parse(response, bo.getDocumentType(), bo.getPatientId());
            ocrService.markSuccess(taskId, result);
            return taskId;
        } catch (ServiceException e) {
            ocrService.markFailed(taskId, "OCR_ERROR", e.getMessage(), null);
            throw e;
        } catch (Exception e) {
            log.warn("医疗文档OCR识别失败, taskId={}", taskId, e);
            ocrService.markFailed(taskId, "OCR_ERROR", "OCR识别失败", null);
            throw new ServiceException("OCR识别失败");
        }
    }

    /**
     * 重试识别：以原任务的文件引用重新发起一次识别，返回新任务详情。
     * <p>
     * 约束（原实现的两个隐患已修复）：
     * <ol>
     *   <li>只有 FAILED 任务可重试，避免对 SUCCESS/CONFIRMED 任务重复消耗百度配额；</li>
     *   <li>imageBase64 / pdfBase64 不落库，因此必须存在 fileUrl 或 ossId 才能重试，
     *       否则必然命中 BaiduOcrClient 的"识别内容不能为空"；</li>
     *   <li>fileMd5 沿用原值即可：去重查询只匹配 status=SUCCESS，FAILED 任务不会命中自身。</li>
     * </ol>
     */
    public OcrTaskVo retry(Long taskId) {
        OcrTaskVo task = ocrService.queryById(taskId);
        if (task == null) {
            throw new ServiceException("OCR任务不存在");
        }
        if (!STATUS_FAILED.equals(task.getStatus())) {
            throw new ServiceException("仅识别失败的任务可重试");
        }
        if (StringUtils.isBlank(task.getFileUrl()) && task.getOssId() == null) {
            throw new ServiceException("原任务未保存文件，请重新上传识别");
        }
        if (StringUtils.isBlank(task.getFileUrl())) {
            throw new ServiceException("原任务仅保存了 ossId 未保存可访问地址，请重新上传识别");
        }
        OcrTaskBo bo = new OcrTaskBo();
        bo.setPatientId(task.getPatientId());
        bo.setSourceType(task.getSourceType());
        bo.setDocumentType(task.getDocumentType());
        bo.setInputType(resolveRetryInputType(task));
        bo.setOssId(task.getOssId());
        bo.setFileUrl(task.getFileUrl());
        bo.setFileMd5(task.getFileMd5());
        // 直接调用即可：recognize 已不再带 @Transactional，
        // 即便后续重新加上事务，也应通过 self-injection / AopContext 调用代理
        Long newTaskId = recognize(bo);
        return ocrService.queryById(newTaskId);
    }

    @Transactional(rollbackFor = Exception.class)
    public OcrConfirmResult confirm(Long taskId, OcrConfirmBo bo) {
        OcrConfirmResult result = confirmRouter.confirm(taskId, bo);
        int metricCount = result.getMetricIds() == null ? 0 : result.getMetricIds().size();
        ocrService.markConfirmed(taskId, result.getPatientId(), metricCount, result.getExamId());
        return result;
    }

    /**
     * 重试时的输入通道换算：原任务只保留了 ossId/fileUrl，base64 载荷不可复用。
     */
    private String resolveRetryInputType(OcrTaskVo task) {
        String inputType = task.getInputType();
        if ("PDF_FILE".equals(inputType)) {
            // 百度 PDF 端点只接受 pdf_file base64，URL 通道无法替代
            throw new ServiceException("PDF 任务未保存原始文件内容，无法重试，请重新上传识别");
        }
        if ("IMAGE_BASE64".equals(inputType)) {
            // 原始 base64 未落库，降级走 URL 通道（IMAGE_URL / OSS_FILE 最终都命中 recognizeByImageUrl）
            return task.getOssId() == null ? "IMAGE_URL" : "OSS_FILE";
        }
        // IMAGE_URL / OSS_FILE 原样复用；历史空值按 URL 通道兜底，其余非法值交由 validateInputType 拒绝
        return StringUtils.isBlank(inputType) ? "IMAGE_URL" : inputType;
    }

    /**
     * 校验 inputType 落在 chronic_ocr_input_type 白名单内。
     * inputType 决定 BaiduOcrClient 使用哪个识别通道，非法值会导致请求无法分派。
     */
    private void validateInputType(OcrTaskBo bo) {
        if (StringUtils.isBlank(bo.getInputType()) || !VALID_INPUT_TYPES.contains(bo.getInputType())) {
            throw new ServiceException("非法的输入类型：" + bo.getInputType() + INPUT_TYPE_HINT);
        }
    }

    /**
     * 校验上传内容总字节数不得超过 maxFileSize。base64 字符串长度按 3/4 估算原始字节数。
     */
    private void validateFileSize(OcrTaskBo bo) {
        Long max = ocrProperties.getMaxFileSize();
        if (max == null || max <= 0) {
            return;
        }
        long approxBytes = approxBase64Bytes(bo.getImageBase64()) + approxBase64Bytes(bo.getPdfBase64());
        if (approxBytes > max) {
            throw new ServiceException("上传文件超出 OCR 最大限制：" + max + " 字节");
        }
    }

    private long approxBase64Bytes(String base64) {
        if (StringUtils.isBlank(base64)) {
            return 0L;
        }
        return (long) (base64.length() * 0.75);
    }
}
