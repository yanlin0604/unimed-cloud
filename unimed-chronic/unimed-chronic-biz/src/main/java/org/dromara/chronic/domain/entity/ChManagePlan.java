package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 管理方案对象 ch_manage_plan
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_manage_plan")
public class ChManagePlan extends TenantEntity {

    @TableId(value = "plan_id")
    private Long planId;

    private Long patientId;

    private String diseaseCode;

    private String planStatus;

    private String planName;

    private String planRemark;

    private Long orgId;

    @TableLogic
    private String delFlag;
}
