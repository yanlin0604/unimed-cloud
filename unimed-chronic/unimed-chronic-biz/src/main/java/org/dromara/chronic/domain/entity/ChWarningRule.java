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
 * 预警规则对象 ch_warning_rule
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_warning_rule")
public class ChWarningRule extends TenantEntity {

    @TableId(value = "rule_id")
    private Long ruleId;

    /**
     * 规则名称（前端 ruleName 取此字段）
     */
    private String ruleName;

    private String diseaseCode;

    private String metricType;

    /**
     * 预警级别: LOW/MEDIUM/HIGH/CRITICAL
     */
    private String warningLevel;

    private BigDecimal thresholdMin;

    private BigDecimal thresholdMax;

    /**
     * 连续N次超标窗口
     */
    private Integer consecutiveWindow;

    private Date timeWindowStart;

    private Date timeWindowEnd;

    /**
     * 恢复规则 JSON
     */
    private String recoveryRule;

    /**
     * 规则描述（详细备注，与 ruleName 互补）
     */
    private String description;

    /**
     * 机构ID
     */
    private Long orgId;

    @TableLogic
    private String delFlag;
}
