package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.OcrTaskBo;
import org.dromara.chronic.domain.vo.OcrTaskVo;
import org.dromara.chronic.support.ocr.domain.OcrParseResult;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 医疗文档OCR任务服务
 *
 * @author unimed
 */
public interface IOcrService {

    Long createTask(OcrTaskBo bo);

    /**
     * 根据 fileMd5 + patientId 命中同患者已成功的 OCR 任务，用于去重避免重复扣百度配额
     */
    OcrTaskVo querySuccessByFileMd5(Long patientId, String fileMd5);

    OcrTaskVo queryById(Long taskId);

    TableDataInfo<OcrTaskVo> queryPageList(OcrTaskBo bo, PageQuery pageQuery);

    Void markRecognizing(Long taskId);

    Void markSuccess(Long taskId, OcrParseResult result);

    Void markFailed(Long taskId, String errorCode, String errorMsg, String rawOcrJson);

    Void markConfirmed(Long taskId, Long patientId, Integer metricCount, Long examId);

    Void discard(Long taskId);
}
