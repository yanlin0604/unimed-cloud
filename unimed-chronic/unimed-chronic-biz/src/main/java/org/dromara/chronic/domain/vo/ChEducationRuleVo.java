package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChEducationRule;

import java.io.Serial;
import java.io.Serializable;

@Schema(description = "宣教规则视图对象")
@Data
@AutoMapper(target = ChEducationRule.class)
public class ChEducationRuleVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "规则ID")
    private Long ruleId;

    @Schema(description = "条件表达式")
    private String conditionExpression;

    @Schema(description = "模板ID")
    private Long templateId;

    @Schema(description = "推送渠道")
    private String pushChannel;

    @Schema(description = "是否启用")
    private Boolean isActive;

}
