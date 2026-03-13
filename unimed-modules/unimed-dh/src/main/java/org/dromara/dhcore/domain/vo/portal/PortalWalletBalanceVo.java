package org.dromara.dhcore.domain.vo.portal;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * C端钱包余额视图对象
 *
 * @author AI
 */
@Data
public class PortalWalletBalanceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前余额
     */
    private BigDecimal balance;

    /**
     * 累计充值
     */
    private BigDecimal totalTopup;

    /**
     * 累计消费
     */
    private BigDecimal totalConsume;

    /**
     * 累计退款
     */
    private BigDecimal totalRefund;
}
