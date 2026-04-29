package org.dromara.chronic.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 指标值工具类
 * <p>
 * 支持简单指标（字符串数字，如"6.5"）和复合指标（JSON，如{"systolic":120,"diastolic":80}）
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
     * @param metricValue 指标值（简单指标为字符串数字，复合指标为JSON）
     * @param metricType  指标类型
     * @return 提取后的数值，解析失败返回null
     */
    public static BigDecimal extractPrimaryValue(String metricValue, String metricType) {
        if (metricValue == null || metricValue.isBlank()) {
            return null;
        }
        try {
            String trimmed = metricValue.trim();
            // 简单指标：纯数字字符串（可能带负号、小数点）
            if (isSimpleNumber(trimmed)) {
                return new BigDecimal(trimmed);
            }
            // 复合指标：JSON 对象
            if (trimmed.startsWith("{")) {
                JSONObject json = JSONUtil.parseObj(trimmed);
                return extractFromJson(json, metricType);
            }
            // 兜底：尝试直接解析
            return new BigDecimal(trimmed);
        } catch (Exception e) {
            log.warn("指标值解析失败: metricValue={}, metricType={}", metricValue, metricType, e);
            return null;
        }
    }

    private static boolean isSimpleNumber(String str) {
        return str.matches("^-?\\d+(\\.\\d+)?$");
    }

    private static BigDecimal extractFromJson(JSONObject json, String metricType) {
        return switch (metricType) {
            case "BP_SYSTOLIC" -> getBigDecimal(json, "systolic");
            case "BP_DIASTOLIC" -> getBigDecimal(json, "diastolic");
            default -> {
                // 未知复合指标类型，尝试取 "value" 字段
                BigDecimal val = getBigDecimal(json, "value");
                if (val != null) {
                    yield val;
                }
                // 再尝试取第一个数值型字段
                for (String key : json.keySet()) {
                    Object v = json.getObj(key);
                    if (v instanceof Number num) {
                        yield BigDecimal.valueOf(num.doubleValue());
                    }
                }
                yield null;
            }
        };
    }

    private static BigDecimal getBigDecimal(JSONObject json, String key) {
        Object val = json.getObj(key);
        if (val == null) {
            return null;
        }
        if (val instanceof Number num) {
            return BigDecimal.valueOf(num.doubleValue());
        }
        try {
            return new BigDecimal(val.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
