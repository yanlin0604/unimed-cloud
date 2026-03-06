package org.dromara.dhcore.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.math.BigDecimal;

/**
 * 数字人口播充值档位配置对象 dh_payment_price_config
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dh_payment_price_config")
public class DhPaymentPriceConfig extends TenantEntity {

    /**
     * 充值档位配置ID
     */
    @TableId(value = "config_id")
    private Long configId;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 适用会员等级
     */
    private String memberLevel;

    /**
     * 支付类型
     */
    private String payType;

    /**
     * 充值金额
     */
    private BigDecimal amount;

    /**
     * 赠送金额
     */
    private BigDecimal bonusAmount;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态（0启用 1停用）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标志
     */
    @TableLogic
    private String delFlag;
}
