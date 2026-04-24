package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChLifestyleRecord;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 生活方式记录视图对象
 *
 * @author unimed
 */
@Schema(description = "生活方式记录视图对象")
@Data
@AutoMapper(target = ChLifestyleRecord.class)
public class ChLifestyleRecordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "患者ID")
    private Long patientId;
    @Schema(description = "吸烟状态")
    private String smokingStatus;
    @Schema(description = "饮酒状态")
    private String drinkingStatus;
    @Schema(description = "运动频率")
    private String exerciseFreq;
    @Schema(description = "饮食习惯")
    private String dietHabit;
    @Schema(description = "心理状态")
    private String psychologicalStatus;
    @Schema(description = "依从性等级")
    private String complianceLevel;
    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "吸烟状态名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "smokingStatus", other = ChronicDictTypeConstant.CHRONIC_SMOKING_STATUS)
    private String smokingStatusName;

    @Schema(description = "饮酒状态名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "drinkingStatus", other = ChronicDictTypeConstant.CHRONIC_DRINKING_STATUS)
    private String drinkingStatusName;

    @Schema(description = "运动频率名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "exerciseFreq", other = ChronicDictTypeConstant.CHRONIC_EXERCISE_FREQ)
    private String exerciseFreqName;

    @Schema(description = "心理状态名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "psychologicalStatus", other = ChronicDictTypeConstant.CHRONIC_PSYCHOLOGICAL_STATUS)
    private String psychologicalStatusName;

    @Schema(description = "依从性等级名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "complianceLevel", other = ChronicDictTypeConstant.CHRONIC_COMPLIANCE_LEVEL)
    private String complianceLevelName;
}
