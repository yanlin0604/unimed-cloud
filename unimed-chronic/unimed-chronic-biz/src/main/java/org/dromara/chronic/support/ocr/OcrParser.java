package org.dromara.chronic.support.ocr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChHealthExamBo;
import org.dromara.chronic.domain.bo.ChHealthExamItemBo;
import org.dromara.chronic.domain.bo.ChHealthMetricRecordBo;
import org.dromara.chronic.domain.bo.ChPatientDiseaseBo;
import org.dromara.chronic.domain.bo.ChPatientProfileBo;
import org.dromara.chronic.domain.entity.ChOcrMetricItem;
import org.dromara.chronic.domain.entity.ChOcrReportItem;
import org.dromara.chronic.support.ocr.domain.BaiduOcrResponse;
import org.dromara.chronic.support.ocr.domain.OcrParseResult;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 医疗文档OCR解析器
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OcrParser {

    private final ObjectMapper objectMapper;
    private final OcrArchiveMapper archiveMapper;
    private final OcrMetricMapper metricMapper;

    public OcrParseResult parse(BaiduOcrResponse response, String documentType, Long patientId) {
        OcrParseResult result = new OcrParseResult();
        result.setRawOcrJson(response.getRawJson());
        try {
            JsonNode root = objectMapper.readTree(response.getRawJson());
            ChPatientProfileBo profile = archiveMapper.mapProfile(root);
            List<ChPatientDiseaseBo> diseases = archiveMapper.mapDiseases(root);
            result.setProfileDraft(profile);
            result.setDiseaseDrafts(diseases);
            result.setArchiveDraft(archiveMapper.buildArchiveDraft(root, profile, diseases));
            ChHealthExamBo exam = new ChHealthExamBo();
            exam.setPatientId(patientId);
            exam.setExamType(resolveExamType(documentType));
            exam.setExamDate(new Date());
            result.setReportDraft(exam);
            List<ChOcrMetricItem> metricItems = metricMapper.mapItems(root);
            result.setMetricItems(metricItems);
            for (ChOcrMetricItem item : metricItems) {
                ChHealthMetricRecordBo metricBo = metricMapper.toMetricBo(patientId, item);
                result.getMetricBos().add(metricBo);
                ChOcrReportItem reportItem = toReportItem(item);
                result.getReportItems().add(reportItem);
                result.getReportItemBos().add(toReportItemBo(reportItem));
            }
        } catch (Exception e) {
            // 不再吞掉异常：即使 raw JSON 已保留，仍需记录解析失败原因，避免运维误以为成功
            log.warn("医疗文档OCR结果解析失败, documentType={}, patientId={}", documentType, patientId, e);
            result.setRawOcrJson(response.getRawJson());
        }
        return result;
    }

    private String resolveExamType(String documentType) {
        if (documentType == null) {
            return "REGULAR_TEST";
        }
        return switch (documentType) {
            case "EXAM_REPORT" -> "SPECIAL_TEST";
            case "MEDICAL_RECORD_HOME", "DISCHARGE_SUMMARY" -> "ANNUAL_CHECKUP";
            // LAB_REPORT / DIAGNOSIS_REPORT 等都按常规检查处理
            default -> "REGULAR_TEST";
        };
    }

    private ChOcrReportItem toReportItem(ChOcrMetricItem metricItem) {
        ChOcrReportItem item = new ChOcrReportItem();
        item.setItemName(metricItem.getOriginalName());
        item.setResultValue(metricItem.getMetricValue());
        item.setUnit(metricItem.getUnit());
        item.setReferenceRange(metricItem.getReferenceRange());
        item.setIsAbnormal(metricItem.getIsAbnormal());
        item.setNeedConfirm(metricItem.getNeedConfirm());
        item.setRawItemJson(metricItem.getRawItemJson());
        return item;
    }

    private ChHealthExamItemBo toReportItemBo(ChOcrReportItem item) {
        ChHealthExamItemBo bo = new ChHealthExamItemBo();
        bo.setItemName(item.getItemName());
        bo.setResultValue(item.getResultValue());
        bo.setReferenceRange(item.getReferenceRange());
        bo.setIsAbnormal(item.getIsAbnormal());
        return bo;
    }
}
