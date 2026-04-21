package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChScreeningRecord;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 义诊筛查记录业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "筛查记录业务对象")
@AutoMapper(target = ChScreeningRecord.class, reverseConvertGenerate = false)
public class ChScreeningRecordBo extends BaseEntity {

    @Schema(description = "记录ID")
    private Long recordId;

    @Schema(description = "批次ID")
    private Long batchId;

    @Schema(description = "离线标识", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "离线标识不能为空")
    private String offlineUuid;

    @Schema(description = "患者姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "患者姓名不能为空")
    private String patientName;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "性别")
    private String gender;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "症状")
    private String symptoms;

    @Schema(description = "生命体征")
    private String vitals;

    @Schema(description = "风险等级")
    private String riskLevel;

    @Schema(description = "筛查结论")
    private String conclusion;

    @Schema(description = "入组状态")
    private String enrollStatus;

    @Schema(description = "入组患者ID")
    private Long enrolledPatientId;
}
