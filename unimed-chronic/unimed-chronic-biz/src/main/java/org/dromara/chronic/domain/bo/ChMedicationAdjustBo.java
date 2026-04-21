package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChMedicationAdjust;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

/**
 * 用药调整业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用药调整业务对象")
@AutoMapper(target = ChMedicationAdjust.class, reverseConvertGenerate = false)
public class ChMedicationAdjustBo extends BaseEntity {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用药记录ID")
    private Long medId;

    @Schema(description = "患者ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    @Schema(description = "调整类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "调整类型不能为空")
    private String adjustType;

    @Schema(description = "调整原因")
    private String adjustReason;

    @Schema(description = "不良反应")
    private String adverseReaction;

    @Schema(description = "预览已确认")
    private Boolean previewConfirmed;

    @Schema(description = "PIN验证时间")
    private Date pinVerifiedAt;

    @Schema(description = "调整人ID")
    private Long adjusterUserId;

    /**
     * 新药编码，便于调药时做冲突检测
     */
    @Schema(description = "新药编码")
    private String targetDrugCode;

    /**
     * 新药名称，换药/加药时展示
     */
    @Schema(description = "新药名称")
    private String targetDrugName;
}
