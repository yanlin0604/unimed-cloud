package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.MedicalDocumentOcrTaskBo;
import org.dromara.chronic.domain.vo.MedicalDocumentOcrTaskVo;
import org.dromara.chronic.support.ocr.domain.MedicalDocumentOcrParseResult;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 医疗文档OCR任务服务
 *
 * @author unimed
 */
public interface IMedicalDocumentOcrService {

    Long createTask(MedicalDocumentOcrTaskBo bo);

    /**
     * 根据 fileMd5 + patientId 命中同患者已成功的 OCR 任务，用于去重避免重复扣百度配额
     */
    MedicalDocumentOcrTaskVo querySuccessByFileMd5(Long patientId, String fileMd5);

    MedicalDocumentOcrTaskVo queryById(Long taskId);

    TableDataInfo<MedicalDocumentOcrTaskVo> queryPageList(MedicalDocumentOcrTaskBo bo, PageQuery pageQuery);

    Void markRecognizing(Long taskId);

    Void markSuccess(Long taskId, MedicalDocumentOcrParseResult result);

    Void markFailed(Long taskId, String errorCode, String errorMsg, String rawOcrJson);

    Void markConfirmed(Long taskId, Long patientId, Integer metricCount, Long examId);

    Void discard(Long taskId);
}
