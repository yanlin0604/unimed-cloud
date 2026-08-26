package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
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

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Long assigneeUserId;

    private Integer cycleDays;

    private Integer totalRounds;

    private Integer currentRound;

    @TableField("status")
    private String planStatus;

    /**
     * 管理等级: LOW/MEDIUM/HIGH/VERY_HIGH
     */
    private String managementLevel;

    /**
     * 是否多病共管: 0-否 1-是
     */
    private Boolean isMultiDisease;

    /**
     * 多病共管合并病种(JSON数组)
     */
    private String mergedDiseaseCodes;

    @TableLogic
    private String delFlag;
}
