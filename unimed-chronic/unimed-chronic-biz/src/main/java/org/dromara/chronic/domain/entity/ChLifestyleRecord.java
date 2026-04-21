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

    @TableLogic
    private String delFlag;
}
