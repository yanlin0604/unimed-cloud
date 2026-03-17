package org.dromara.dhcore.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 运营看板指标视图对象
 */
@Data
public class DhDashboardMetricsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 新增订单�?     */
    private Integer newOrders;

    /**
     * 已完成订单数
     */
    private Integer completedOrders;

    /**
     * 平均交付时长（小时）
     */
    private BigDecimal avgDeliveryHours;

    /**
     * 待确认充值单�?     */
    private Integer pendingTopups;

    /**
     * 超时订单�?     */
    private Integer timeoutOrders;

    /**
     * 当日收入
     */
    private BigDecimal todayRevenue;

    /**
     * 趋势数据
     */
    private List<DhDashboardTrendVo> trend;
}
