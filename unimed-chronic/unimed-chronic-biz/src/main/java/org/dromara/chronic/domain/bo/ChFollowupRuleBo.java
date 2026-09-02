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

    /**
     * 逐轮生成模型下由服务端统一置 1（见 ChFollowupRuleServiceImpl#validateRule），不再作为运营可配项。
     * 因此不加 @NotNull：管理端表单已移除该字段，若保留必填校验，
     * Bean Validation 会在进入 service 之前直接拒绝请求（setTotalRounds(1) 来不及生效）。
     */
    @Schema(description = "总轮次（服务端固定为1，无需传入）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer totalRounds;

    @Schema(description = "首轮到期天数")
    private Integer firstDueDays;

    @Schema(description = "默认随访方式")
    private String defaultVisitType;

    @Schema(description = "方案建议文案")
    private String summaryAdvice;

    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "启用状态不能为空")
    private Boolean isActive;
}