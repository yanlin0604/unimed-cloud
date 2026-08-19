package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChArchiveShareApply;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 调档申请业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "调档申请业务对象")
@AutoMapper(target = ChArchiveShareApply.class, reverseConvertGenerate = false)
public class ChArchiveShareApplyBo extends BaseEntity {

    @Schema(description = "患者ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    @Schema(description = "申请机构ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "申请机构不能为空")
    private Long applyOrgId;

    @Schema(description = "目标机构ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "目标机构不能为空")
    private Long targetOrgId;

    @Schema(description = "申请原因", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "申请原因不能为空")
    private String applyReason;

    @Schema(description = "审批状态")
    private String approvalStatus;

    @Schema(description = "审批意见")
    private String approvalOpinion;
}
