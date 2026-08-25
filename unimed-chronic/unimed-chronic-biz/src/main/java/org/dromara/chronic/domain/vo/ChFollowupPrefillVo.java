package org.dromara.chronic.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 随访智能预填与参考数据
 *
 * @author unimed
 */
@Data
@Schema(description = "随访智能预填与参考数据")
public class ChFollowupPrefillVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "患者最新生命体征数据 (如 systolicBp, diastolicBp, fastingGlucose, heartRate, weight, bmi 等)")
    private Map<String, Object> latestMetrics;

    @Schema(description = "最新体征测量/采集时间")
    private Date latestMetricTime;

    @Schema(description = "当前正在服用的慢病药物记录列表")
    private List<ChMedicationRecordVo> activeMedications;

    @Schema(description = "格式化当前用药方案描述文本 (如: 氨氯地平片 5mg qd, 二甲双胍 0.5g tid)")
    private String medicationDescription;

    @Schema(description = "上一次随访记录概要")
    private ChFollowupRecordVo lastRecord;

    @Schema(description = "上一次随访答卷列表")
    private List<ChFollowupAnswerVo> lastAnswers;
}
