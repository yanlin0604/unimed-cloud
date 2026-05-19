package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChPatientCloseApply;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

/**
 * 患者结案申请业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "患者结案申请业务对象")
@AutoMapper(target = ChPatientCloseApply.class, reverseConvertGenerate = false)
public class ChPatientCloseApplyBo extends BaseEntity {

    @Schema(description = "申请ID（审核场景必填）")
    private Long applyId;

    @Schema(description = "患者ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "患者ID不能为空", groups = ApplyGroup.class)
    private Long patientId;

    @Schema(description = "结案类型 VOLUNTARY/TRANSFER/LOST/DEATH/RECOVERED/MOVED_OUT/OTHER")
    @NotBlank(message = "结案类型不能为空", groups = ApplyGroup.class)
    @Size(max = 20, message = "结案类型长度不能超过{max}个字符")
    private String closeType;

    @Schema(description = "申请理由（含详细说明）")
    @NotBlank(message = "申请理由不能为空", groups = ApplyGroup.class)
    @Size(max = 2000, message = "申请理由长度不能超过{max}个字符")
    private String applyReason;

    @Schema(description = "结案日期（YYYY-MM-DD，写入 snapshot_json）")
    private String closeDate;

    @Schema(description = "转出机构（康复转出场景，写入 snapshot_json）")
    private String transferOrg;

    @Schema(description = "证据附件文件ID")
    private Long evidenceFileId;

    @Schema(description = "联动：同步解除签约")
    private Boolean terminateContract;

    @Schema(description = "联动：同步终止随访")
    private Boolean terminateFollowup;

    @Schema(description = "联动：同步归档预警")
    private Boolean archiveAlert;

    @Schema(description = "联动：通知家属/紧急联系人")
    private Boolean notifyContact;

    @Schema(description = "申请发起端 ADMIN/DOCTOR/PATIENT，默认 ADMIN")
    private String applySource;

    // ============ 审核场景字段 ============

    @Schema(description = "审核状态 PENDING/APPROVED/REJECTED/WITHDRAWN")
    @NotBlank(message = "审核状态不能为空", groups = AuditGroup.class)
    private String auditStatus;

    @Schema(description = "审核备注/意见")
    @Size(max = 500, message = "审核备注长度不能超过{max}个字符")
    private String auditRemark;

    @Schema(description = "驳回理由（仅驳回时填写）")
    @Size(max = 500, message = "驳回理由长度不能超过{max}个字符")
    private String rejectReason;

    @Schema(description = "审核时间")
    private Date auditTime;

    @Schema(description = "审核人用户ID")
    private Long auditorUserId;

    public interface ApplyGroup {}

    public interface AuditGroup {}
}
