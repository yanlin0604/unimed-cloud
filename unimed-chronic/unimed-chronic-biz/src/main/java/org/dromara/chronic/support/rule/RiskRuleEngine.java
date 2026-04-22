package org.dromara.chronic.support.rule;

import cn.hutool.core.lang.Dict;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.entity.ChAssessmentRule;
import org.dromara.chronic.domain.entity.ChRiskFactorItem;
import org.dromara.common.json.utils.JsonUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 风险评估规则引擎
 *
 * @author unimed
 */
@Component
@RequiredArgsConstructor
public class RiskRuleEngine {

    public Result evaluate(List<ChAssessmentRule> rules, Dict metricData, Dict factorData) {
        List<ChRiskFactorItem> factorItems = new ArrayList<>();
        int totalScore = 0;
        for (ChAssessmentRule rule : rules) {
            Dict threshold = JsonUtils.parseMap(rule.getThresholdConfig());
            if (threshold == null) {
                continue;
            }
            Object currentValue = metricData.get(rule.getDimensionName());
            if (currentValue == null) {
                currentValue = factorData.get(rule.getDimensionName());
            }
            if (currentValue == null) {
                continue;
            }
            boolean hit = hitThreshold(currentValue, threshold);
            if (!hit) {
                continue;
            }
            totalScore += rule.getDimensionWeight() == null ? 0 : rule.getDimensionWeight().intValue();
            ChRiskFactorItem item = new ChRiskFactorItem();
            item.setFactorName(rule.getDimensionName());
            item.setFactorValue(String.valueOf(currentValue));
            item.setFactorWeight(rule.getDimensionWeight());
            factorItems.add(item);
        }
        String level = resolveLevel(totalScore, metricData);
        return new Result(level, totalScore, factorItems);
    }

    private boolean hitThreshold(Object currentValue, Dict threshold) {
        Double value = parseDouble(currentValue);
        Double min = parseDouble(threshold.get("min"));
        Double max = parseDouble(threshold.get("max"));
        if (value != null) {
            boolean geMin = min == null || value >= min;
            boolean leMax = max == null || value <= max;
            return geMin && leMax;
        }
        String equals = threshold.getStr("equals");
        return equals != null && equals.equalsIgnoreCase(String.valueOf(currentValue));
    }

    private String resolveLevel(int totalScore, Dict metricData) {
        Double systolic = parseDouble(metricData.get("systolic"));
        Double diastolic = parseDouble(metricData.get("diastolic"));
        Double glucose = parseDouble(metricData.get("glucose"));
        if ((systolic != null && systolic >= 180D) || (diastolic != null && diastolic >= 110D) || (glucose != null && glucose >= 16.7D)) {
            return "VERY_HIGH";
        }
        if (totalScore >= 80) {
            return "VERY_HIGH";
        }
        if (totalScore >= 60) {
            return "HIGH";
        }
        if (totalScore >= 30) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private Double parseDouble(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public record Result(String riskLevel, int totalScore, List<ChRiskFactorItem> factorItems) {
    }
}
