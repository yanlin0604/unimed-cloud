package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChFollowupTask;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 随访任务视图对象
 *
 * @author unimed
 */
@Schema(description = "随访任务视图对象")
@Data
@AutoMapper(target = ChFollowupTask.class)
public class ChFollowupTaskVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "任务ID")
    private Long taskId;
    @Schema(description = "患者ID")
    private Long patientId;
    @Schema(description = "方案ID")
    private Long planId;
    @Schema(description = "任务轮次")
    private Integer taskRound;
    @Schema(description = "计划日期")
    private Date planDate;
    @Schema(description = "任务状态")
    private String taskStatus;
    @Schema(description = "指派用户ID")
    private Long assigneeUserId;
}
