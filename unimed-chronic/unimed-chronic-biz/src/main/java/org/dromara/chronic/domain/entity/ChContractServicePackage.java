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
 * 签约服务包对象 ch_contract_service_package
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_contract_service_package")
public class ChContractServicePackage extends TenantEntity {

    @TableId(value = "package_id")
    private Long packageId;

    private String packageName;

    private String packageType;

    /**
     * 服务项 JSON / 逗号分隔文本
     */
    private String serviceItems;

    private BigDecimal price;

    private Boolean isActive;

    @TableLogic
    private String delFlag;
}
