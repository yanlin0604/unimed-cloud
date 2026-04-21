package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChAssessmentRule;

import java.io.Serial;
import java.io.Serializable;

/**
 * 风险评估规则视图对象
 *
 * @author unimed
 */
@Schema(description = "评估规则视图对象")
@Data
@AutoMapper(target = ChAssessmentRule.class)
public class ChAssessmentRuleVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "规则ID")
    private Long ruleId;
    @Schema(description = "病种编码")
    private String diseaseCode;
    @Schema(description = "维度名称")
    private String dimensionName;
    @Schema(description = "维度权重")
    private Integer dimensionWeight;
    @Schema(description = "阈值配置")
    private String thresholdConfig;
    @Schema(description = "是否启用")
    private Boolean isActive;
}
