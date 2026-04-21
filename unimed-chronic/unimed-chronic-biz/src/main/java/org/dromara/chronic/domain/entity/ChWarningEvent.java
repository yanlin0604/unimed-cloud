package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

import java.math.BigDecimal;
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

    private String warningLevel;

    private BigDecimal warningValue;

    private Date warningTime;

    /**
     * 事件状态: NEW/CONFIRMED/PROCESSING/ESCALATED/RESOLVED/ARCHIVED
     */
    private String eventStatus;

    private Long assigneeUserId;

    @TableLogic
    private String delFlag;
}
