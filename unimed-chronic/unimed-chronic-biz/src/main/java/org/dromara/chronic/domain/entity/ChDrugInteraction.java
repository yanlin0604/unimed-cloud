package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 药物相互作用规则对象 ch_drug_interaction
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_drug_interaction")
public class ChDrugInteraction extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private String drugCodeA;

    private String drugCodeB;

    private String interactionLevel;

    private String description;

    private String clinicalAdvice;

    @TableLogic
    private String delFlag;
}
