package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChEducationRule;
import org.dromara.common.mybatis.core.domain.BaseEntity;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "宣教规则业务对象")
@AutoMapper(target = ChEducationRule.class, reverseConvertGenerate = false)
public class ChEducationRuleBo extends BaseEntity {

    @Schema(description = "规则ID")
    private Long ruleId;

    @Schema(description = "条件表达式", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "条件表达式不能为空")
    private String conditionExpression;

    @Schema(description = "模板ID")
    private Long templateId;

    @Schema(description = "推送通道", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "推送通道不能为空")
    private String pushChannel;

    @Schema(description = "是否启用")
    private Boolean isActive;

}
