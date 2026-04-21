package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChAssessmentRule;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 风险评估规则业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "风险评估规则业务对象")
@AutoMapper(target = ChAssessmentRule.class, reverseConvertGenerate = false)
public class ChAssessmentRuleBo extends BaseEntity {

    @Schema(description = "规则ID")
    private Long ruleId;

    @Schema(description = "病种编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "病种编码不能为空")
    private String diseaseCode;

    @Schema(description = "评估维度", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "评估维度不能为空")
    private String dimensionName;

    @Schema(description = "维度权重", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "维度权重不能为空")
    private Integer dimensionWeight;

    @Schema(description = "阈值配置", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "阈值配置不能为空")
    private String thresholdConfig;

    @Schema(description = "是否启用")
    private Boolean isActive;
}
