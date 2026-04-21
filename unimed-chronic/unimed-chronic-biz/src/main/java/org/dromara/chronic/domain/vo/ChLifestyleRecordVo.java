package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChLifestyleRecord;

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
}
