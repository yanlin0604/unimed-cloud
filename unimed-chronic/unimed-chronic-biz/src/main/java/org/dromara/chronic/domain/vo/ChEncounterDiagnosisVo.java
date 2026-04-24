package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChEncounterDiagnosis;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;

/**
 * 诊疗诊断视图对象
 *
 * @author unimed
 */
@Schema(description = "诊疗诊断视图对象")
@Data
@AutoMapper(target = ChEncounterDiagnosis.class)
public class ChEncounterDiagnosisVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long id;
    @Schema(description = "诊疗记录ID")
    private Long encounterId;
    @Schema(description = "患者ID")
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
    @Schema(description = "创建时间")
    private java.time.LocalDateTime createTime;

    @Schema(description = "诊断类型名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "diagnosisType", other = ChronicDictTypeConstant.CHRONIC_DIAGNOSIS_TYPE)
    private String diagnosisTypeName;
}