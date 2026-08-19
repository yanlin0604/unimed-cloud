package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChArchiveShareApply;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 调档申请视图对象
 *
 * @author unimed
 */
@Schema(description = "调档申请视图对象")
@Data
@AutoMapper(target = ChArchiveShareApply.class)
public class ChArchiveShareApplyVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "患者ID")
    private Long patientId;
    @Schema(description = "申请机构ID")
    private Long applyOrgId;
    @Schema(description = "目标机构ID")
    private Long targetOrgId;
    @Schema(description = "申请原因")
    private String applyReason;
    @Schema(description = "审批状态")
    private String approvalStatus;
    @Schema(description = "审批意见")
    private String approvalOpinion;
    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "审批状态名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "approvalStatus", other = ChronicDictTypeConstant.CHRONIC_APPROVAL_STATUS)
    private String approvalStatusName;

    @Schema(description = "申请机构名称")
    private String applyOrgName;

    @Schema(description = "目标机构名称")
    private String targetOrgName;
}
