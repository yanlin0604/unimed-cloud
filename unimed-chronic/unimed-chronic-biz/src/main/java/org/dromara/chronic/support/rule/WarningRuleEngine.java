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
import java.util.List;

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

    /**
     * 判定指标类型是否匹配（支持别名如 SBP 与 BP_SYSTOLIC 互通）
     */
    public static boolean isMetricTypeMatch(String ruleMetricType, String recordMetricType) {
        if (ruleMetricType == null || recordMetricType == null) {
            return false;
        }
        if (ruleMetricType.equalsIgnoreCase(recordMetricType)) {
            return true;
        }
        if (("SBP".equalsIgnoreCase(ruleMetricType) || "BP_SYSTOLIC".equalsIgnoreCase(ruleMetricType))
            && ("SBP".equalsIgnoreCase(recordMetricType) || "BP_SYSTOLIC".equalsIgnoreCase(recordMetricType))) {
            return true;
        }
        if (("DBP".equalsIgnoreCase(ruleMetricType) || "BP_DIASTOLIC".equalsIgnoreCase(ruleMetricType))
            && ("DBP".equalsIgnoreCase(recordMetricType) || "BP_DIASTOLIC".equalsIgnoreCase(recordMetricType))) {
            return true;
        }
        if (("FBG".equalsIgnoreCase(ruleMetricType) || "BLOOD_GLUCOSE".equalsIgnoreCase(ruleMetricType))
            && ("FBG".equalsIgnoreCase(recordMetricType) || "BLOOD_GLUCOSE".equalsIgnoreCase(recordMetricType))) {
            return true;
        }
        if (("HR".equalsIgnoreCase(ruleMetricType) || "HEART_RATE".equalsIgnoreCase(ruleMetricType))
            && ("HR".equalsIgnoreCase(recordMetricType) || "HEART_RATE".equalsIgnoreCase(recordMetricType))) {
            return true;
        }
        return false;
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
        if (rule.getConsecutiveWindow() == null || rule.getConsecutiveWindow() <= 1) {
            return true;
        }
        List<ChHealthMetricRecord> recentRecords = metricRecordMapper.selectList(
            Wrappers.<ChHealthMetricRecord>lambdaQuery()
                .eq(ChHealthMetricRecord::getPatientId, currentRecord.getPatientId())
                .eq(ChHealthMetricRecord::getMetricType, currentRecord.getMetricType())
                .ne(ChHealthMetricRecord::getMetricId, currentRecord.getMetricId())
                .orderByDesc(ChHealthMetricRecord::getCreateTime)
                .last("LIMIT " + (rule.getConsecutiveWindow() - 1))
        );
        if (recentRecords.size() < rule.getConsecutiveWindow() - 1) {
            return false;
        }
        for (ChHealthMetricRecord record : recentRecords) {
            BigDecimal value = MetricValueUtils.extractPrimaryValue(record.getMetricValue(), record.getMetricType());
            if (value == null || !isAbnormal(rule, value)) {
                return false;
            }
        }
        return true;
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
}
