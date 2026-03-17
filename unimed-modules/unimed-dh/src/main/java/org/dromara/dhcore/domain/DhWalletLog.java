package org.dromara.dhcore.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.math.BigDecimal;

/**
 * 数字人口播钱包流水对�?dh_wallet_log
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dh_wallet_log")
public class DhWalletLog extends TenantEntity {

    /**
     * 钱包流水ID
     */
    @TableId(value = "log_id")
    private Long logId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名快�?     */
    private String userName;

    /**
     * 流水类型
     */
    private String type;

    /**
     * 变动金额
     */
    private BigDecimal amount;

    /**
     * 变动后余�?     */
    private BigDecimal balanceAfter;

    /**
     * 关联订单ID
     */
    private Long relatedOrderId;

    /**
     * 操作�?     */
    private String operatorName;

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
