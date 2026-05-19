package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChPatientCloseApply;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 患者结案申请视图对象
 *
 * @author unimed
 */
@Schema(description = "患者结案申请视图对象")
@Data
@AutoMapper(target = ChPatientCloseApply.class)
public class ChPatientCloseApplyVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "申请ID")
    private Long applyId;

    @Schema(description = "患者ID")
    private Long patientId;

    @Schema(description = "结案类型")
    private String closeType;

    @Schema(description = "申请理由")
    private String applyReason;

    @Schema(description = "证据附件文件ID")
    private Long evidenceFileId;

    @Schema(description = "快照JSON")
    private String snapshotJson;

    @Schema(description = "申请人用户ID")
    private Long applicantUserId;

    @Schema(description = "申请人昵称")
    @Translation(type = TransConstant.USER_ID_TO_NICKNAME, mapper = "applicantUserId")
    private String applicantNickName;

    @Schema(description = "申请发起端")
    private String applySource;

    @Schema(description = "审核状态")
    private String auditStatus;

    @Schema(description = "审核人用户ID")
    private Long auditorUserId;

    @Schema(description = "审核人昵称")
    @Translation(type = TransConstant.USER_ID_TO_NICKNAME, mapper = "auditorUserId")
    private String auditorNickName;

    @Schema(description = "审核时间")
    private Date auditTime;

    @Schema(description = "审核备注")
    private String auditRemark;

    @Schema(description = "驳回理由")
    private String rejectReason;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;
}
