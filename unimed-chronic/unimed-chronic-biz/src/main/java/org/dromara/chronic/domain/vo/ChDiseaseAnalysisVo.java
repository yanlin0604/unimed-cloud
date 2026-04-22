package org.dromara.chronic.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 专病分析视图对象
 *
 * @author unimed
 */
@Schema(description = "专病分析视图对象")
@Data
public class ChDiseaseAnalysisVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "病种编码")
    private String diseaseCode;
    @Schema(description = "病种名称")
    private String diseaseName;
    @Schema(description = "患者总数")
    private Long totalPatientCount;
    @Schema(description = "控制良好数")
    private Long controlledCount;
    @Schema(description = "控制率")
    private BigDecimal controlRate;
    @Schema(description = "重点预警数")
    private Long warningCount;
    @Schema(description = "随访完成数")
    private Long followupCompletedCount;
    @Schema(description = "随访完成率")
    private BigDecimal followupRate;
    @Schema(description = "新增患者数")
    private Long newPatientCount;
    @Schema(description = "统计周期")
    private String statPeriod;
}