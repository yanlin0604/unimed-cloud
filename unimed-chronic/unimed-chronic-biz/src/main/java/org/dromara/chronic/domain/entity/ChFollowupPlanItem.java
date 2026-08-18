package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

import java.util.Date;

/**
 * 随访计划项对象 ch_followup_plan_item
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_followup_plan_item")
public class ChFollowupPlanItem extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private Long planId;

    private String itemType;

    private String visitType;

    private Date dueDate;

    /**
     * 计划项配置 JSON
     */
    private String itemConfig;

    @TableLogic
    private String delFlag;
}
