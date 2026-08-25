package org.dromara.chronic.support.rule;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.entity.ChHealthMetricRecord;
import org.dromara.chronic.domain.entity.ChWarningRule;
import org.dromara.chronic.mapper.ChHealthMetricRecordMapper;
import org.dromara.chronic.utils.MetricValueUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * 预警规则引擎：连续N次超标窗口判定
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WarningRuleEngine {

    private final ChHealthMetricRecordMapper metricRecordMapper;

    /** 方案、规则和指标上报统一使用的标准指标编码。 */
    public static String normalizeMetricType(String metricType) {
        if (metricType == null || metricType.isBlank()) {
            return null;
        }
        return switch (metricType.trim().toUpperCase(Locale.ROOT)) {
            case "SBP", "SYSTOLIC_BP", "BP_SYSTOLIC" -> "BP_SYSTOLIC";
            case "DBP", "DIASTOLIC_BP", "BP_DIASTOLIC" -> "BP_DIASTOLIC";
            case "FBG", "FASTING_GLUCOSE", "BLOOD_GLUCOSE" -> "BLOOD_GLUCOSE";
            case "HR", "HEART_RATE" -> "HEART_RATE";
            default -> metricType.trim().toUpperCase(Locale.ROOT);
        };
    }

    /**
     * 返回标准指标编码对应的全部历史别名，用于兼容存量指标记录。
     */
    public static Set<String> getMetricTypeAliases(String metricType) {
        String normalizedMetricType = normalizeMetricType(metricType);
        if (normalizedMetricType == null) {
            return Set.of();
        }
        return switch (normalizedMetricType) {
            case "BP_SYSTOLIC" -> Set.of("SBP", "SYSTOLIC_BP", "BP_SYSTOLIC");
            case "BP_DIASTOLIC" -> Set.of("DBP", "DIASTOLIC_BP", "BP_DIASTOLIC");
            case "BLOOD_GLUCOSE" -> Set.of("FBG", "FASTING_GLUCOSE", "BLOOD_GLUCOSE");
            case "HEART_RATE" -> Set.of("HR", "HEART_RATE");
            default -> Set.of(normalizedMetricType);
        };
    }

    /**
     * 判定指标类型是否匹配，兼容存量数据中的历史别名。
     */
    public static boolean isMetricTypeMatch(String ruleMetricType, String recordMetricType) {
        String normalizedRuleMetricType = normalizeMetricType(ruleMetricType);
        String normalizedRecordMetricType = normalizeMetricType(recordMetricType);
        return normalizedRuleMetricType != null && normalizedRuleMetricType.equals(normalizedRecordMetricType);
    }

    /**
     * 判定是否触发预警（连续 consecutiveWindow 次超标）
     */
    public boolean evaluate(ChWarningRule rule, ChHealthMetricRecord currentRecord) {
        if (!isMetricTypeMatch(rule.getMetricType(), currentRecord.getMetricType())) {
            return false;
        }
        BigDecimal currentValue = MetricValueUtils.extractPrimaryValue(currentRecord.getMetricValue(), currentRecord.getMetricType());
        if (currentValue == null) {
            return false;
        }
        boolean currentAbnormal = isAbnormal(rule, currentValue);
        if (!currentAbnormal) {
            return false;
        }
        // 校验时间窗口（如晨峰高血压 06:00~09:00、夜间低血糖 00:00~06:00）
        if (rule.getTimeWindowStart() != null && rule.getTimeWindowEnd() != null) {
            java.time.LocalTime recordTime = currentRecord.getCreateTime() != null
                ? currentRecord.getCreateTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime()
                : java.time.LocalTime.now();
            java.time.LocalTime start = toLocalTime(rule.getTimeWindowStart());
            java.time.LocalTime end = toLocalTime(rule.getTimeWindowEnd());
            if (start != null && end != null) {
                if (start.isBefore(end)) {
                    if (recordTime.isBefore(start) || recordTime.isAfter(end)) {
                        return false;
                    }
                } else {
                    // 跨午夜时间段 (例如 22:00 ~ 06:00)
                    if (recordTime.isBefore(start) && recordTime.isAfter(end)) {
                        return false;
                    }
                }
            }
        }

        // TODO [P3] recoveryRule 恢复规则：用于判定患者指标是否从异常恢复到正常，
        // 预期在预警事件自动关闭（auto-resolve）或降级功能中使用。
        // 待业务规则明确后实现 JSON 解析与恢复判定逻辑。

        if (rule.getConsecutiveWindow() == null || rule.getConsecutiveWindow() <= 1) {
            return true;
        }
        int requiredPreviousRecordCount = rule.getConsecutiveWindow() - 1;
        int queryLimit = Math.max(requiredPreviousRecordCount * 4, requiredPreviousRecordCount);
        List<ChHealthMetricRecord> recentRecords = metricRecordMapper.selectList(
            Wrappers.<ChHealthMetricRecord>lambdaQuery()
                .eq(ChHealthMetricRecord::getPatientId, currentRecord.getPatientId())
                .in(ChHealthMetricRecord::getMetricType, getMetricTypeAliases(currentRecord.getMetricType()))
                .ne(ChHealthMetricRecord::getMetricId, currentRecord.getMetricId())
                .orderByDesc(ChHealthMetricRecord::getCreateTime)
                .last("LIMIT " + queryLimit)
        );
        List<ChHealthMetricRecord> matchingRecentRecords = recentRecords.stream()
            .filter(record -> isMetricTypeMatch(currentRecord.getMetricType(), record.getMetricType()))
            .limit(requiredPreviousRecordCount)
            .toList();
        if (matchingRecentRecords.size() < requiredPreviousRecordCount) {
            return false;
        }
        for (ChHealthMetricRecord record : matchingRecentRecords) {
            BigDecimal value = MetricValueUtils.extractPrimaryValue(record.getMetricValue(), record.getMetricType());
            if (value == null || !isAbnormal(rule, value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 仅判断当前记录是否仍处于规则阈值异常范围，用于自动解决已恢复的事件。
     */
    public boolean isCurrentValueAbnormal(ChWarningRule rule, ChHealthMetricRecord currentRecord) {
        if (!isMetricTypeMatch(rule.getMetricType(), currentRecord.getMetricType())) {
            return false;
        }
        BigDecimal currentValue = MetricValueUtils.extractPrimaryValue(
            currentRecord.getMetricValue(), currentRecord.getMetricType());
        return currentValue != null && isAbnormal(rule, currentValue);
    }

    /**
     * 阈值超标判定：
     * 1. 双边界区间：[thresholdMin, thresholdMax]（如 160 ~ 179.99 属于中危预警）
     * 2. 单下限：value >= thresholdMin（如收缩压 >= 180，心率 >= 120）
     * 3. 单上限：value <= thresholdMax（如血糖 <= 3.9 低血糖，血氧 <= 90%）
     */
    private boolean isAbnormal(ChWarningRule rule, BigDecimal value) {
        BigDecimal min = rule.getThresholdMin();
        BigDecimal max = rule.getThresholdMax();

        if (min != null && max != null) {
            return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
        }
        if (min != null && max == null) {
            return value.compareTo(min) >= 0;
        }
        if (min == null && max != null) {
            return value.compareTo(max) <= 0;
        }
        return false;
    }

    /**
     * 将 Date（实际为 java.sql.Time）安全转换为 LocalTime
     */
    private static java.time.LocalTime toLocalTime(Date date) {
        if (date == null) {
            return null;
        }
        if (date instanceof java.sql.Time sqlTime) {
            return sqlTime.toLocalTime();
        }
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();
    }
}
