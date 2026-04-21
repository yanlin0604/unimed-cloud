package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 随访计划对象 ch_followup_plan
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_followup_plan")
public class ChFollowupPlan extends TenantEntity {

    @TableId(value = "plan_id")
    private Long planId;

    private Long patientId;

    private String diseaseCode;

    private Integer cycleDays;

    private Integer totalRounds;

    private String planStatus;

    @TableLogic
    private String delFlag;
}
