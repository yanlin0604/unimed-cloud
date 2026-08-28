package org.dromara.chronic.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/** 患者用药打卡统计 */
@Data
@Schema(description = "患者用药打卡统计")
public class ChMedicationCheckinStatVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer consecutiveDays;
    private Integer weekCompletedDays;
    private Integer weekExpectedDays;
    private Integer weekAchievementRate;
    private Boolean checkedInToday;
    private Boolean hasActiveMedication;
    private List<MedicationTodayVo> medications;

    @Data
    public static class MedicationTodayVo implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private Long medId;
        private String drugName;
        private String dosage;
        private String frequency;
        @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "frequency", other = ChronicDictTypeConstant.CHRONIC_FREQUENCY)
        private String frequencyName;
        private Boolean checkedInToday;
    }
}
