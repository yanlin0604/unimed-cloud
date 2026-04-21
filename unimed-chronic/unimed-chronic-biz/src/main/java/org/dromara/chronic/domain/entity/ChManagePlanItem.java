package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 管理方案子项对象 ch_manage_plan_item
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_manage_plan_item")
public class ChManagePlanItem extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private Long planId;

    private String itemType;

    /**
     * 子项内容 JSON
     */
    private String itemContent;

    private Long orgId;

    @TableLogic
    private String delFlag;
}
