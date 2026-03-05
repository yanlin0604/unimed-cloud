package org.dromara.dhcore.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.dhcore.domain.DhOrder;
import org.dromara.dhcore.domain.vo.DhDashboardMetricsVo;
import org.dromara.dhcore.domain.vo.DhDashboardTrendVo;
import org.dromara.dhcore.mapper.DhOrderMapper;
import org.dromara.dhcore.service.IDhDashboardService;
import org.dromara.dhcore.service.support.DhOrderStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 数字人口播看板服务实现
 */
@RequiredArgsConstructor
@Service
public class DhDashboardServiceImpl implements IDhDashboardService {

    private final DhOrderMapper orderMapper;

    @Override
    public DhDashboardMetricsVo queryMetrics(String range) {
        String normalizedRange = normalizeRange(range);
        Date now = new Date();
        Date[] rangeDates = resolveRange(normalizedRange, now);
        Date start = rangeDates[0];
        Date end = rangeDates[1];

        List<DhOrder> relatedOrders = orderMapper.selectList(
            Wrappers.<DhOrder>lambdaQuery().and(w ->
                w.between(DhOrder::getCreateTime, start, end)
                    .or()
                    .between(DhOrder::getCompletedTime, start, end)
            )
        );

        List<DhOrder> openOrders = orderMapper.selectList(
            Wrappers.<DhOrder>lambdaQuery().notIn(DhOrder::getStatus,
                DhOrderStatus.COMPLETED,
                DhOrderStatus.CANCELLED,
                DhOrderStatus.REJECTED)
        );

        DhDashboardMetricsVo vo = new DhDashboardMetricsVo();
        vo.setNewOrders((int) relatedOrders.stream().filter(o -> inRange(o.getCreateTime(), start, end)).count());

        List<DhOrder> completedOrders = relatedOrders.stream()
            .filter(order -> DhOrderStatus.COMPLETED.equals(order.getStatus()) && inRange(order.getCompletedTime(), start, end))
            .toList();
        vo.setCompletedOrders(completedOrders.size());

        vo.setAvgDeliveryHours(calcAvgDeliveryHours(completedOrders));
        vo.setPendingTopups(0);
        vo.setTimeoutOrders((int) openOrders.stream().filter(this::isTimeoutOrder).count());
        vo.setTodayRevenue(sumRevenue(completedOrders));
        vo.setTrend(buildTrend(normalizedRange, start, end, relatedOrders));
        return vo;
    }

    private String normalizeRange(String range) {
        if ("today".equals(range) || "7d".equals(range) || "30d".equals(range)) {
            return range;
        }
        throw new ServiceException("不支持的时间范围");
    }

    private Date[] resolveRange(String range, Date now) {
        if ("today".equals(range)) {
            return new Date[]{DateUtil.beginOfDay(now), DateUtil.endOfDay(now)};
        }
        if ("7d".equals(range)) {
            Date start = DateUtil.beginOfDay(DateUtil.offsetDay(now, -6));
            return new Date[]{start, DateUtil.endOfDay(now)};
        }
        Date start = DateUtil.beginOfDay(DateUtil.offsetDay(now, -29));
        return new Date[]{start, DateUtil.endOfDay(now)};
    }

    private boolean inRange(Date target, Date start, Date end) {
        if (target == null) {
            return false;
        }
        return !target.before(start) && !target.after(end);
    }

    private boolean isTimeoutOrder(DhOrder order) {
        if (order.getCreateTime() == null || order.getExpectDeliveryHours() == null) {
            return false;
        }
        Date deadline = DateUtil.offset(order.getCreateTime(), DateField.HOUR, order.getExpectDeliveryHours());
        return deadline.before(new Date());
    }

    private BigDecimal calcAvgDeliveryHours(List<DhOrder> completedOrders) {
        BigDecimal totalHours = BigDecimal.ZERO;
        int count = 0;
        for (DhOrder order : completedOrders) {
            if (order.getClaimTime() == null || order.getCompletedTime() == null) {
                continue;
            }
            long millis = order.getCompletedTime().getTime() - order.getClaimTime().getTime();
            if (millis < 0) {
                continue;
            }
            BigDecimal hours = BigDecimal.valueOf(millis)
                .divide(BigDecimal.valueOf(1000 * 60 * 60L), 2, RoundingMode.HALF_UP);
            totalHours = totalHours.add(hours);
            count++;
        }
        if (count == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return totalHours.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal sumRevenue(List<DhOrder> orders) {
        return orders.stream()
            .map(DhOrder::getActualAmount)
            .filter(amount -> amount != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);
    }

    private List<DhDashboardTrendVo> buildTrend(String range, Date start, Date end, List<DhOrder> orders) {
        List<DhDashboardTrendVo> trendList = new ArrayList<>();
        if ("today".equals(range)) {
            DhDashboardTrendVo trend = new DhDashboardTrendVo();
            trend.setDate("今日");
            trend.setOrders((int) orders.stream().filter(o -> inRange(o.getCreateTime(), start, end)).count());
            List<DhOrder> completedOrders = orders.stream()
                .filter(o -> DhOrderStatus.COMPLETED.equals(o.getStatus()) && inRange(o.getCompletedTime(), start, end))
                .toList();
            trend.setCompleted(completedOrders.size());
            trend.setRevenue(sumRevenue(completedOrders));
            trendList.add(trend);
            return trendList;
        }

        Date cursor = start;
        while (!cursor.after(end)) {
            Date dayStart = DateUtil.beginOfDay(cursor);
            Date dayEnd = DateUtil.endOfDay(cursor);

            DhDashboardTrendVo trend = new DhDashboardTrendVo();
            trend.setDate(DateUtil.format(dayStart, "MM-dd"));
            trend.setOrders((int) orders.stream().filter(o -> inRange(o.getCreateTime(), dayStart, dayEnd)).count());

            List<DhOrder> completedOrders = orders.stream()
                .filter(o -> DhOrderStatus.COMPLETED.equals(o.getStatus()) && inRange(o.getCompletedTime(), dayStart, dayEnd))
                .toList();
            trend.setCompleted(completedOrders.size());
            trend.setRevenue(sumRevenue(completedOrders));

            trendList.add(trend);
            cursor = DateUtil.offsetDay(cursor, 1);
        }
        return trendList;
    }
}
