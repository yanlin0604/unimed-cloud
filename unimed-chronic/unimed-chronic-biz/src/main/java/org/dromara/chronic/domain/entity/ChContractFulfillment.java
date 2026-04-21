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
 * 签约履约记录对象 ch_contract_fulfillment
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_contract_fulfillment")
public class ChContractFulfillment extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private Long contractId;

    private String serviceItem;

    private Date planDate;

    private Date actualDate;

    private String fulfillmentStatus;

    private Boolean slaViolation;

    @TableLogic
    private String delFlag;
}
