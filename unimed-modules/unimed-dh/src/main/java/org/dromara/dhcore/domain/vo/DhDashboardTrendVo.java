package org.dromara.dhcore.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 看板趋势项视图对�? */
@Data
public class DhDashboardTrendVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 日期标签
     */
    private String date;

    /**
     * 订单�?     */
    private Integer orders;

    /**
     * 完成�?     */
    private Integer completed;

    /**
     * 收入金额
     */
    private BigDecimal revenue;
}
