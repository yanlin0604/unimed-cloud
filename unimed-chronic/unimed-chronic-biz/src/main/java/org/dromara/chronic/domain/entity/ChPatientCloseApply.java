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
 * 患者结案申请对象 ch_patient_close_apply
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_patient_close_apply")
public class ChPatientCloseApply extends TenantEntity {

    @TableId(value = "apply_id")
    private Long applyId;

    /**
     * 患者ID
     */
    private Long patientId;

    /**
     * 结案类型 VOLUNTARY/TRANSFER/LOST/DEATH（兼容前端扩展值 RECOVERED/MOVED_OUT/OTHER）
     */
    private String closeType;

    /**
     * 申请理由
     */
    private String applyReason;

    /**
     * 证据附件文件ID
     */
    private Long evidenceFileId;

    /**
     * 审核通过后用于追溯的快照（JSON 字符串）
     */
    private String snapshotJson;

    /**
     * 申请人用户ID
     */
    private Long applicantUserId;

    /**
     * 申请发起端 ADMIN/DOCTOR/PATIENT
     */
    private String applySource;

    /**
     * 审核状态 PENDING/APPROVED/REJECTED/WITHDRAWN
     */
    private String auditStatus;

    /**
     * 审核人用户ID
     */
    private Long auditorUserId;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 审核备注
     */
    private String auditRemark;

    /**
     * 驳回理由（仅驳回时填写）
     */
    private String rejectReason;

    @TableLogic
    private String delFlag;
}
