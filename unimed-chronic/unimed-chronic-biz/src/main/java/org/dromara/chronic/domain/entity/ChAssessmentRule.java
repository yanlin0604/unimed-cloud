package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

import java.math.BigDecimal;

/**
 * 风险评估规则对象 ch_assessment_rule
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_assessment_rule")
public class ChAssessmentRule extends TenantEntity {

    @TableId(value = "rule_id")
    private Long ruleId;

    private String diseaseCode;

    private String dimensionName;

    private BigDecimal dimensionWeight;

    /**
     * 阈值配置 JSON
     */
    private String thresholdConfig;

    private Boolean isActive;

    @TableLogic
    private String delFlag;
}