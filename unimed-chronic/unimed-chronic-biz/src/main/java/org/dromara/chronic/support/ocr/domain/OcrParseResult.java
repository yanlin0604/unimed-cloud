package org.dromara.chronic.support.ocr.domain;

import lombok.Data;
import org.dromara.chronic.domain.bo.ChHealthExamBo;
import org.dromara.chronic.domain.bo.ChHealthExamItemBo;
import org.dromara.chronic.domain.bo.ChHealthMetricRecordBo;
import org.dromara.chronic.domain.bo.ChPatientDiseaseBo;
import org.dromara.chronic.domain.bo.ChPatientProfileBo;
import org.dromara.chronic.domain.entity.ChOcrArchiveDraft;
import org.dromara.chronic.domain.entity.ChOcrMetricItem;
import org.dromara.chronic.domain.entity.ChOcrReportItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 医疗文档OCR解析结果
 *
 * @author unimed
 */
@Data
public class OcrParseResult {

    private ChOcrArchiveDraft archiveDraft;

    private ChPatientProfileBo profileDraft;

    private List<ChPatientDiseaseBo> diseaseDrafts = new ArrayList<>();

    private ChHealthExamBo reportDraft;

    private List<ChOcrMetricItem> metricItems = new ArrayList<>();

    private List<ChOcrReportItem> reportItems = new ArrayList<>();

    private List<ChHealthMetricRecordBo> metricBos = new ArrayList<>();

    private List<ChHealthExamItemBo> reportItemBos = new ArrayList<>();

    private String rawOcrJson;
}
