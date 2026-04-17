package org.dromara.dhcore.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 方言采集录音审核 BO（B端）
 *
 * @author unimed
 */
@Data
@Schema(description = "录音审核参数")
public class DhDialectRecordAuditBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 录音记录ID
     */
    @NotNull(message = "录音记录ID不能为空")
    @Schema(description = "录音记录ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long recordId;

    /**
     * 审核状态 APPROVED/REJECTED
     */
    @NotBlank(message = "审核状态不能为空")
    @Schema(description = "审核状态 APPROVED/REJECTED", requiredMode = Schema.RequiredMode.REQUIRED)
    private String auditStatus;

    /**
     * 审核备注
     */
    @Schema(description = "审核备注")
    private String auditRemark;
}
