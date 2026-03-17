package org.dromara.dhcore.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 财务趋势点视图对�? */
@Data
public class DhFinanceTrendVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 日期标签
     */
    private String date;

    /**
     * 充值金�?     */
    private BigDecimal topup;

    /**
     * 消费金额
     */
    private BigDecimal consume;

    /**
     * 退款金�?     */
    private BigDecimal refund;
}
