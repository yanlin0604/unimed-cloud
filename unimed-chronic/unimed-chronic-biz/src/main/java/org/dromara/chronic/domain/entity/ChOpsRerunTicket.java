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
 * 运维任务重跑工单对象 ch_ops_rerun_ticket
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_ops_rerun_ticket")
public class ChOpsRerunTicket extends TenantEntity {

    /** 工单ID */
    @TableId(value = "ticket_id")
    private Long ticketId;

    /** 重跑任务编码 */
    private String taskCode;

    /** 申请人用户ID */
    private Long applyUserId;

    /** 申请理由 */
    private String applyReason;

    /** 审批状态(PENDING/APPROVED/REJECTED) */
    private String auditStatus;

    /** 审批人用户ID */
    private Long auditorUserId;

    /** 审批时间 */
    private Date auditTime;

    /** 审批备注 */
    private String auditRemark;

    /** 执行状态(NOT_STARTED/RUNNING/SUCCESS/FAILED) */
    private String execStatus;

    /** 执行开始时间 */
    private Date execStartTime;

    /** 执行结束时间 */
    private Date execEndTime;

    /** 执行结果摘要 */
    private String execResult;

    /** 影响数据范围 */
    private String affectedRange;

    /** 删除标志(0存在 1删除) */
    @TableLogic
    private String delFlag;
}
