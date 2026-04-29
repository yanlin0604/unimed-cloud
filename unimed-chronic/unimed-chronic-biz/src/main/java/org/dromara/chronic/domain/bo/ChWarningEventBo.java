package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChWarningEvent;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 预警事件业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "预警事件业务对象")
@AutoMapper(target = ChWarningEvent.class, reverseConvertGenerate = false)
public class ChWarningEventBo extends BaseEntity {

    @Schema(description = "预警ID")
    private Long warningId;

    @Schema(description = "患者ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    @Schema(description = "规则ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "规则ID不能为空")
    private Long ruleId;

    @Schema(description = "预警级别")
    private String warningLevel;

    @Schema(description = "预警值（同指标值格式，简单指标为字符串数字，复合指标为JSON）")
    private String warningValue;

    @Schema(description = "事件状态")
    private String eventStatus;

    @Schema(description = "处理人ID")
    private Long assigneeUserId;
}
