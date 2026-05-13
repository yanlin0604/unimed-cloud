package org.dromara.chronic.utils;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 指标值工具类
 * <p>
 * 统一规范：所有健康指标 metric_value 为简单数字字符串（如 "120" / "6.5"）。
 * 血压拆为 BP_SYSTOLIC + BP_DIASTOLIC 两条独立记录，每条 metric_value 为单一数字。
 *
 * @author unimed
 */
@Slf4j
public final class MetricValueUtils {

    private MetricValueUtils() {
    }

    /**
     * 从指标值字符串中提取用于比较的主数值
     *
     * @param metricValue 指标值（简单数字字符串）
     * @param metricType  指标类型（保留参数用于扩展，当前无类型相关解析）
     * @return 提取后的数值，解析失败返回 null
     */
    public static BigDecimal extractPrimaryValue(String metricValue, String metricType) {
        if (metricValue == null || metricValue.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(metricValue.trim());
        } catch (NumberFormatException e) {
            log.warn("指标值解析失败: metricValue={}, metricType={}", metricValue, metricType);
            return null;
        }
    }
}
