package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChEncounterRecord;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 诊疗记录业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "诊疗记录业务对象")
@AutoMapper(target = ChEncounterRecord.class, reverseConvertGenerate = false)
public class ChEncounterRecordBo extends BaseEntity {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "患者ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "患者ID不能为空")
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

    @Schema(description = "当前用药快照(JSON)")
    private String medicationSnapshot;

    @Schema(description = "风险因素快照(JSON)")
    private String riskFactorSnapshot;

    @Schema(description = "来源类型(DOCTOR/ADMIN/HIS)")
    private String sourceType;

    @Schema(description = "外部单号或门诊号")
    private String sourceBizNo;

    @Schema(description = "提交状态(DRAFT/SUBMITTED)")
    private String submitStatus;

    @Schema(description = "提交时间")
    private LocalDateTime submittedTime;

    @Schema(description = "诊断列表")
    private List<ChEncounterDiagnosisBo> diagnosisList;

    @Schema(description = "就诊时间范围-起始(仅用于查询)")
    private LocalDateTime encounterTimeStart;

    @Schema(description = "就诊时间范围-结束(仅用于查询)")
    private LocalDateTime encounterTimeEnd;
}