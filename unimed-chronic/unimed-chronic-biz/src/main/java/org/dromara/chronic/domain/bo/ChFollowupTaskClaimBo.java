package org.dromara.chronic.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 随访任务认领业务对象
 *
 * @author unimed
 */
@Data
@Schema(description = "随访任务批量认领业务对象")
public class ChFollowupTaskClaimBo {

    @Schema(description = "任务ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "认领任务列表不能为空")
    private List<Long> taskIds;
}
