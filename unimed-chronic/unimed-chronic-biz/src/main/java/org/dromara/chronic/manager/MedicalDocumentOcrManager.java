package org.dromara.chronic.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.config.BaiduOcrProperties;
import org.dromara.chronic.domain.bo.MedicalDocumentOcrConfirmBo;
import org.dromara.chronic.domain.bo.MedicalDocumentOcrTaskBo;
import org.dromara.chronic.domain.vo.MedicalDocumentOcrConfirmResult;
import org.dromara.chronic.domain.vo.MedicalDocumentOcrTaskVo;
import org.dromara.chronic.service.IMedicalDocumentOcrService;
import org.dromara.chronic.support.ocr.BaiduOcrClient;
import org.dromara.chronic.support.ocr.MedicalDocumentOcrParser;
import org.dromara.chronic.support.ocr.domain.BaiduOcrRequest;
import org.dromara.chronic.support.ocr.domain.BaiduOcrResponse;
import org.dromara.chronic.support.ocr.domain.MedicalDocumentOcrParseResult;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 医疗文档OCR业务编排
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MedicalDocumentOcrManager {

    private final IMedicalDocumentOcrService ocrService;
    private final BaiduOcrClient baiduOcrClient;
    private final MedicalDocumentOcrParser parser;
    private final MedicalDocumentOcrConfirmRouter confirmRouter;
    private final BaiduOcrProperties ocrProperties;

    /**
     * 识别医疗文档：拆分事务边界——createTask、markRecognizing、markSuccess、markFailed
     * 各自由 service 层独立事务提交，OCR 远程网络调用置于事务之外，避免长事务占用 DB 连接，
     * 也避免失败时 markFailed 写入随外层事务一并回滚导致任务永远停留在 RECOGNIZING 状态。
     */
    public Long recognize(MedicalDocumentOcrTaskBo bo) {
        // 1. 文件大小校验（防止超大 base64 透传到百度）
        validateFileSize(bo);
        // 2. fileMd5 去重：相同患者 + 相同 md5 已成功识别过，直接复用旧任务
        if (StringUtils.isNotBlank(bo.getFileMd5())) {
            MedicalDocumentOcrTaskVo existed = ocrService.querySuccessByFileMd5(bo.getPatientId(), bo.getFileMd5());
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
            MedicalDocumentOcrParseResult result = parser.parse(response, bo.getDocumentType(), bo.getPatientId());
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

    public MedicalDocumentOcrTaskVo retry(Long taskId) {
        MedicalDocumentOcrTaskVo task = ocrService.queryById(taskId);
        if (task == null) {
            throw new ServiceException("OCR任务不存在");
        }
        MedicalDocumentOcrTaskBo bo = new MedicalDocumentOcrTaskBo();
        bo.setPatientId(task.getPatientId());
        bo.setSourceType(task.getSourceType());
        bo.setDocumentType(task.getDocumentType());
        bo.setInputType(task.getInputType());
        bo.setOssId(task.getOssId());
        bo.setFileUrl(task.getFileUrl());
        bo.setFileMd5(task.getFileMd5());
        // 直接调用即可：recognize 已不再带 @Transactional，
        // 即便后续重新加上事务，也应通过 self-injection / AopContext 调用代理
        recognize(bo);
        return task;
    }

    @Transactional(rollbackFor = Exception.class)
    public MedicalDocumentOcrConfirmResult confirm(Long taskId, MedicalDocumentOcrConfirmBo bo) {
        MedicalDocumentOcrConfirmResult result = confirmRouter.confirm(taskId, bo);
        int metricCount = result.getMetricIds() == null ? 0 : result.getMetricIds().size();
        ocrService.markConfirmed(taskId, result.getPatientId(), metricCount, result.getExamId());
        return result;
    }

    /**
     * 校验上传内容总字节数不得超过 maxFileSize。base64 字符串长度按 3/4 估算原始字节数。
     */
    private void validateFileSize(MedicalDocumentOcrTaskBo bo) {
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
