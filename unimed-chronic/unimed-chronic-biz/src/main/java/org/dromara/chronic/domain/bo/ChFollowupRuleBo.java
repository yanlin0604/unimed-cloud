package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChFollowupRule;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 慢病随访排期规则配置业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "随访排期规则业务对象")
@AutoMapper(target = ChFollowupRule.class, reverseConvertGenerate = false)
public class ChFollowupRuleBo extends BaseEntity {

    @Schema(description = "规则ID")
    private Long id;

    @Schema(description = "病种编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "病种编码不能为空")
    private String diseaseCode;

    @Schema(description = "风险/管理等级", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "风险等级不能为空")
    private String riskLevel;

    @Schema(description = "随访周期(天)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "随访周期不能为空")
    private Integer cycleDays;

    @Schema(description = "总轮次", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "总轮次不能为空")
    private Integer totalRounds;

    @Schema(description = "首轮到期天数")
    private Integer firstDueDays;

    @Schema(description = "默认随访方式")
    private String defaultVisitType;

    @Schema(description = "面对面随访最少轮次")
    private Integer requireFaceToFaceRounds;

    @Schema(description = "方案建议文案")
    private String summaryAdvice;

    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "启用状态不能为空")
    private Boolean isActive;
}