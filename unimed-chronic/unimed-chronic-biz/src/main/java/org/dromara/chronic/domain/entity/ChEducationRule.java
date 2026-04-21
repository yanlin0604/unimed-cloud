package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 宣教规则对象 ch_education_rule
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_education_rule")
public class ChEducationRule extends TenantEntity {

    @TableId(value = "rule_id")
    private Long ruleId;

    private String conditionExpression;

    private Long templateId;

    private String pushChannel;

    private Boolean isActive;

    @TableLogic
    private String delFlag;
}
