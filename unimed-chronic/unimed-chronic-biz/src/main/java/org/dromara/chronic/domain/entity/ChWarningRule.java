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
     * 临床处置建议
     * <p>
     * 命中该规则后给医生的具体处置指引，例如危急值规则会写明
     * 「立即复测确认并安排急诊评估、24 小时内完成靶器官损害评估」等。
     * 线上 8 条规则该列全部有值，此前实体未映射导致这些建议医生完全看不到。
     */
    private String clinicalAdvice;

    /**
     * 要求响应时限（小时）
     * <p>
     * 预警需在多少小时内被处置，危急值通常为 2 小时、一般高值为 24 小时。
     * 是 SLA 违约判定的依据，此前实体未映射导致 SLA 监控无从实现。
     */
    private Integer responseSlaHours;

    /**
     * 机构ID
     */
    private Long orgId;

    @TableLogic
    private String delFlag;
}
