package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChFollowupTask;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

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
    @Schema(description = "计划到期日期")
    private Date planDueDate;
    @Schema(description = "任务状态")
    private String taskStatus;
    @Schema(description = "指派用户ID")
    private Long assigneeUserId;

    @Schema(description = "随访方式")
    private String visitType;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "问卷ID")
    private Long questionnaireId;

    @Schema(description = "任务状态名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "taskStatus", other = ChronicDictTypeConstant.CHRONIC_FOLLOWUP_TASK_STATUS)
    private String taskStatusName;

    @Schema(description = "随访方式名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "visitType", other = ChronicDictTypeConstant.CHRONIC_VISIT_TYPE)
    private String visitTypeName;

    @Schema(description = "执行人昵称")
    @Translation(type = TransConstant.USER_ID_TO_NICKNAME, mapper = "assigneeUserId")
    private String assigneeNickName;

    @Schema(description = "专病编码")
    private String diseaseCode;

    @Schema(description = "专病名称")
    private String diseaseName;
}
