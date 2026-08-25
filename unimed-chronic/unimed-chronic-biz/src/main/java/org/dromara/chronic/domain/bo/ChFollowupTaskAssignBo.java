package org.dromara.chronic.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 随访任务批量指派业务对象
 *
 * @author unimed
 */
@Data
@Schema(description = "随访任务批量指派业务对象")
public class ChFollowupTaskAssignBo {

    @Schema(description = "任务ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "指派任务列表不能为空")
    private List<Long> taskIds;

    @Schema(description = "执行人用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "执行人用户ID不能为空")
    private Long assigneeUserId;
}
