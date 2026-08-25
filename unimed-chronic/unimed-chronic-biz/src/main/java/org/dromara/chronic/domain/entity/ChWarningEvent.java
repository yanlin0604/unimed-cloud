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
 * 预警事件对象 ch_warning_event
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_warning_event")
public class ChWarningEvent extends TenantEntity {

    @TableId(value = "warning_id")
    private Long warningId;

    private Long patientId;

    private Long ruleId;

    /** 事件来源：RULE/PLAN/SOS/SLA/MANUAL。 */
    private String eventSource;

    /** 来源业务记录ID，例如规则ID、方案子项ID、SOS ID。 */
    private Long sourceId;

    /** 触发事件的标准指标类型。 */
    private String metricType;

    /** 方案软提醒关联的管理方案ID。 */
    private Long planId;

    /** 机构ID。 */
    private Long orgId;

    private String warningLevel;

    private String warningValue;

    private Date warningTime;

    /**
     * 事件状态: NEW/CONFIRMED/PROCESSING/ESCALATED/RESOLVED/ARCHIVED
     */
    private String eventStatus;

    private Long assigneeUserId;

    @TableLogic
    private String delFlag;
}
