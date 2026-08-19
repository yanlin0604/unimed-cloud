package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 生活方式记录对象 ch_lifestyle_record
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_lifestyle_record")
public class ChLifestyleRecord extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private Long patientId;

    private String smokingStatus;

    private String drinkingStatus;

    private String exerciseFreq;

    private String dietHabit;

    private String psychologicalStatus;

    private String complianceLevel;

    /** 记录日期 */
    private java.util.Date recordDate;

    /** 饮食评分(0-100) */
    private Integer dietScore;

    /** 睡眠时长(小时) */
    private java.math.BigDecimal sleepHours;

    /** 情绪评分(0-100) */
    private Integer moodScore;

    /** 运动时长(分钟) */
    private Integer exerciseMinutes;

    /** 生活方式明细JSON */
    private String lifestyleDetail;

    @TableLogic
    private String delFlag;
}
