package org.dromara.chronic.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 医疗文档OCR确认结果
 *
 * @author unimed
 */
@Data
@Schema(description = "医疗文档OCR确认结果")
public class OcrConfirmResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "确认后的患者ID")
    private Long patientId;

    @Schema(description = "确认后的指标ID列表")
    private List<Long> metricIds;

    @Schema(description = "确认后的检查检验报告ID")
    private Long examId;
}
