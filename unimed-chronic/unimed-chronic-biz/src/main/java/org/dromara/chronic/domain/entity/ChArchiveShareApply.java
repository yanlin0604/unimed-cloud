package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 调档申请对象 ch_archive_share_apply
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_archive_share_apply")
public class ChArchiveShareApply extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private Long patientId;

    private Long applyOrgId;

    private Long targetOrgId;

    private String applyReason;

    /**
     * 审批状态: PENDING/APPROVING/APPROVED/REJECTED/WITHDRAWN
     */
    private String approvalStatus;

    /**
     * 工作流实例ID（启动审批流程后回填）
     */
    private Long workflowInstanceId;

    @TableLogic
    private String delFlag;
}
