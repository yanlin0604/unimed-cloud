package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

import java.util.Date;

/**
 * 随访任务对象 ch_followup_task
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_followup_task")
public class ChFollowupTask extends TenantEntity {

    @TableId(value = "task_id")
    private Long taskId;

    private Long patientId;

    private Long planId;

    private Integer taskRound;

    private Date planDueDate;

    private String taskStatus;

    private String visitType;

    private Long assigneeUserId;

    @TableLogic
    private String delFlag;
}
