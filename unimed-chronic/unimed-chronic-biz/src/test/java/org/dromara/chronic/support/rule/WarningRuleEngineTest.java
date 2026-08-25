package org.dromara.chronic.support.rule;

import org.dromara.chronic.domain.entity.ChHealthMetricRecord;
import org.dromara.chronic.domain.entity.ChWarningRule;
import org.dromara.chronic.mapper.ChHealthMetricRecordMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * 预警规则指标编码兼容与阈值判定测试。
 */
@Tag("chronic-dev")
public class WarningRuleEngineTest {

    private final WarningRuleEngine ruleEngine = new WarningRuleEngine(mock(ChHealthMetricRecordMapper.class));

    @Test
    public void normalizeMetricTypeShouldMapHistoricalAliasesToCanonicalCodes() {
        assertEquals("BP_SYSTOLIC", WarningRuleEngine.normalizeMetricType("SBP"));
        assertEquals("BP_SYSTOLIC", WarningRuleEngine.normalizeMetricType("systolic_bp"));
        assertEquals("BP_DIASTOLIC", WarningRuleEngine.normalizeMetricType("DBP"));
        assertEquals("BLOOD_GLUCOSE", WarningRuleEngine.normalizeMetricType("fasting_glucose"));
        assertEquals("HEART_RATE", WarningRuleEngine.normalizeMetricType("HR"));
    }

    @Test
    public void evaluateShouldMatchCanonicalRuleAgainstHistoricalMetricAlias() {
        ChWarningRule rule = createRule("BP_SYSTOLIC", new BigDecimal("140"), null);
        ChHealthMetricRecord record = createMetric("SBP", "155");

        assertTrue(ruleEngine.evaluate(rule, record));
    }

    @Test
    public void evaluateShouldSupportSingleUpperBoundaryRules() {
        ChWarningRule rule = createRule("BLOOD_GLUCOSE", null, new BigDecimal("3.9"));

        assertTrue(ruleEngine.evaluate(rule, createMetric("FBG", "3.5")));
        assertFalse(ruleEngine.evaluate(rule, createMetric("FASTING_GLUCOSE", "5.6")));
    }

    private ChWarningRule createRule(String metricType, BigDecimal thresholdMin, BigDecimal thresholdMax) {
        ChWarningRule rule = new ChWarningRule();
        rule.setMetricType(metricType);
        rule.setThresholdMin(thresholdMin);
        rule.setThresholdMax(thresholdMax);
        rule.setConsecutiveWindow(1);
        return rule;
    }

    private ChHealthMetricRecord createMetric(String metricType, String metricValue) {
        ChHealthMetricRecord record = new ChHealthMetricRecord();
        record.setMetricId(1L);
        record.setPatientId(100L);
        record.setMetricType(metricType);
        record.setMetricValue(metricValue);
        return record;
    }
}
