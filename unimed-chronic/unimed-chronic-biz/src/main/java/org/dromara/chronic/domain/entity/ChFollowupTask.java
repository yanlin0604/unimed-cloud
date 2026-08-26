package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
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

    /**
     * 任务类型: NORMAL-常规/DYNAMIC-动态调整/REFERRAL_TRACK-转诊追踪/EMERGENCY-预警临时
     */
    private String taskType;

    /**
     * 是否面对面随访: 0-否 1-是
     */
    private Boolean isFaceToFace;

    /**
     * 取消/失访原因: LOST/REFUSED/RELOCATED/DECEASED/OTHER
     */
    private String cancelReasonCode;

    /**
     * 取消原因补充说明
     */
    private String cancelReasonDesc;

    /**
     * 患者自填内容(体征/问卷/小结, JSON)。患者自填进入 PATIENT_FILLED 待医生评估时写入。
     */
    private String patientFillContent;

    /**
     * 患者自填提交时间
     */
    private Date patientFillTime;

    /** 执行人用户ID(为 NULL 时处于随访任务池待认领/待分发) */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long assigneeUserId;

    @TableLogic
    private String delFlag;
}
