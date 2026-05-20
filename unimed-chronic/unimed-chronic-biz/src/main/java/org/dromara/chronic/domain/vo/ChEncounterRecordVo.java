package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChEncounterRecord;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 诊疗记录视图对象
 *
 * @author unimed
 */
@Schema(description = "诊疗记录视图对象")
@Data
@AutoMapper(target = ChEncounterRecord.class)
public class ChEncounterRecordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long id;
    @Schema(description = "患者ID")
    private Long patientId;
    @Schema(description = "病种编码")
    private String diseaseCode;
    @Schema(description = "就诊类型(INITIAL/FOLLOWUP)")
    private String encounterType;
    @Schema(description = "就诊时间")
    private LocalDateTime encounterTime;
    @Schema(description = "主诉")
    private String complaint;
    @Schema(description = "现病史")
    private String presentHistory;
    @Schema(description = "体格检查摘要")
    private String physicalExamSummary;
    @Schema(description = "辅助检查摘要")
    private String auxiliaryExamSummary;
    @Schema(description = "处理方案")
    private String treatmentPlan;
    @Schema(description = "复诊建议")
    private String revisitAdvice;
    @Schema(description = "当前用药快照")
    private String medicationSnapshot;
    @Schema(description = "风险因素快照")
    private String riskFactorSnapshot;
    @Schema(description = "来源类型(DOCTOR/ADMIN/HIS)")
    private String sourceType;
    @Schema(description = "外部单号或门诊号")
    private String sourceBizNo;
    @Schema(description = "提交状态(DRAFT/SUBMITTED)")
    private String submitStatus;
    @Schema(description = "提交时间")
    private LocalDateTime submittedTime;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "诊断列表")
    private List<ChEncounterDiagnosisVo> diagnosisList;

    @Schema(description = "就诊类型名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "encounterType", other = ChronicDictTypeConstant.CHRONIC_ENCOUNTER_TYPE)
    private String encounterTypeName;

    @Schema(description = "来源类型名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "sourceType", other = ChronicDictTypeConstant.CHRONIC_SOURCE_TYPE)
    private String sourceTypeName;

    @Schema(description = "提交状态名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "submitStatus", other = ChronicDictTypeConstant.CHRONIC_SUBMIT_STATUS)
    private String submitStatusName;

    @Schema(description = "病种名称")
    private String diseaseName;

    @Schema(description = "患者姓名")
    private String patientName;
}