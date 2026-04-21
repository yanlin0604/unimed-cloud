package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChWarningAction;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 预警处置动作业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "预警处置业务对象")
@AutoMapper(target = ChWarningAction.class, reverseConvertGenerate = false)
public class ChWarningActionBo extends BaseEntity {

    @Schema(description = "预警事件ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "预警事件ID不能为空")
    private Long warningId;

    @Schema(description = "动作类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "动作类型不能为空")
    private String actionType;

    @Schema(description = "动作详情")
    private String actionDetail;

    @Schema(description = "执行人ID")
    private Long actionUserId;
}
