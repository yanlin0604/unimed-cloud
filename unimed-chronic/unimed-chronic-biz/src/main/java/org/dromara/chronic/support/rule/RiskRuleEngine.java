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
        String directLevel = null;
        
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
            
            // 累加分数
            if (rule.getDimensionWeight() != null) {
                totalScore += rule.getDimensionWeight().intValue();
            }
            
            // 判断是否直接触发高危评级
            String level = threshold.getStr("directLevel");
            if (level != null) {
                directLevel = maxLevel(directLevel, level);
            }
            
            ChRiskFactorItem item = new ChRiskFactorItem();
            item.setFactorName(rule.getDimensionName());
            item.setFactorValue(String.valueOf(currentValue));
            item.setFactorWeight(rule.getDimensionWeight());
            factorItems.add(item);
        }
        
        String finalLevel = resolveLevel(totalScore, directLevel);
        return new Result(finalLevel, totalScore, factorItems);
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

    private String resolveLevel(int totalScore, String directLevel) {
        String scoreLevel = "LOW";
        if (totalScore >= 80) {
            scoreLevel = "VERY_HIGH";
        } else if (totalScore >= 60) {
            scoreLevel = "HIGH";
        } else if (totalScore >= 30) {
            scoreLevel = "MEDIUM";
        }
        return maxLevel(scoreLevel, directLevel);
    }
    
    private String maxLevel(String level1, String level2) {
        if (level1 == null) return level2;
        if (level2 == null) return level1;
        
        List<String> levels = List.of("LOW", "MEDIUM", "HIGH", "VERY_HIGH");
        int idx1 = levels.indexOf(level1);
        int idx2 = levels.indexOf(level2);
        
        // 如果遇到未知的level，保守起见返回原值
        if (idx1 == -1) return level2;
        if (idx2 == -1) return level1;
        
        return idx1 > idx2 ? level1 : level2;
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
