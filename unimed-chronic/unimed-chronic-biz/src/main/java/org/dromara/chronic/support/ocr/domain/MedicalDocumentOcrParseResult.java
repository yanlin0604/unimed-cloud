package org.dromara.chronic.support.ocr.domain;

import lombok.Data;
import org.dromara.chronic.domain.bo.ChHealthExamBo;
import org.dromara.chronic.domain.bo.ChHealthExamItemBo;
import org.dromara.chronic.domain.bo.ChHealthMetricRecordBo;
import org.dromara.chronic.domain.bo.ChPatientDiseaseBo;
import org.dromara.chronic.domain.bo.ChPatientProfileBo;
import org.dromara.chronic.domain.entity.ChMedicalDocumentOcrArchiveDraft;
import org.dromara.chronic.domain.entity.ChMedicalDocumentOcrMetricItem;
import org.dromara.chronic.domain.entity.ChMedicalDocumentOcrReportItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 医疗文档OCR解析结果
 *
 * @author unimed
 */
@Data
public class MedicalDocumentOcrParseResult {

    private ChMedicalDocumentOcrArchiveDraft archiveDraft;

    private ChPatientProfileBo profileDraft;

    private List<ChPatientDiseaseBo> diseaseDrafts = new ArrayList<>();

    private ChHealthExamBo reportDraft;

    private List<ChMedicalDocumentOcrMetricItem> metricItems = new ArrayList<>();

    private List<ChMedicalDocumentOcrReportItem> reportItems = new ArrayList<>();

    private List<ChHealthMetricRecordBo> metricBos = new ArrayList<>();

    private List<ChHealthExamItemBo> reportItemBos = new ArrayList<>();

    private String rawOcrJson;
}
