package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChEncounterDiagnosis;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 诊疗诊断业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "诊疗诊断业务对象")
@AutoMapper(target = ChEncounterDiagnosis.class, reverseConvertGenerate = false)
public class ChEncounterDiagnosisBo extends BaseEntity {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "诊疗记录ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "诊疗记录ID不能为空")
    private Long encounterId;

    @Schema(description = "患者ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    @Schema(description = "诊断类型(PRIMARY/SECONDARY)")
    private String diagnosisType;

    @Schema(description = "ICD编码")
    private String diagnosisCode;

    @Schema(description = "诊断名称")
    private String diagnosisName;

    @Schema(description = "诊断依据")
    private String diagnosisBasis;

    @Schema(description = "风险因素编码")
    private String riskFactorCode;

    @Schema(description = "风险因素名称")
    private String riskFactorName;

    @Schema(description = "是否并发症(Y/N)")
    private String complicationFlag;
}