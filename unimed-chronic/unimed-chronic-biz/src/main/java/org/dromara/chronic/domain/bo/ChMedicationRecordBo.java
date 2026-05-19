package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChMedicationRecord;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

/**
 * 用药记录业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用药记录业务对象")
@AutoMapper(target = ChMedicationRecord.class, reverseConvertGenerate = false)
public class ChMedicationRecordBo extends BaseEntity {

    @Schema(description = "用药记录ID")
    private Long medId;

    @Schema(description = "患者ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    @Schema(description = "药品名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "药品名称不能为空")
    private String drugName;

    @Schema(description = "药品编码")
    private String drugCode;

    @Schema(description = "剂量")
    private String dosage;

    @Schema(description = "用药频率")
    private String frequency;

    @Schema(description = "给药途径")
    private String route;

    @Schema(description = "开始日期")
    private Date startDate;

    @Schema(description = "停药日期")
    private Date stopDate;

    @Schema(description = "配药数量")
    private String dispenseQuantity;

    @Schema(description = "处方周期")
    private String prescriptionPeriod;

    @Schema(description = "开方医生ID")
    private Long prescriberUserId;

    @Schema(description = "开方医生已验证")
    private Boolean prescriberVerified;

    @Schema(description = "用药状态")
    private String status;

    @Schema(description = "用药依从性 GOOD/FAIR/POOR（字典 chronic_compliance_level）")
    private String compliance;

    @Schema(description = "处方依据")
    private String prescriptionBasis;

    @Schema(description = "用药备注")
    private String remark;
}
