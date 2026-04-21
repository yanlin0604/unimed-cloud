package org.dromara.chronic.support.rule;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.entity.ChHealthMetricRecord;
import org.dromara.chronic.domain.entity.ChWarningRule;
import org.dromara.chronic.mapper.ChHealthMetricRecordMapper;
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
     * 判定是否触发预警（连续 consecutiveWindow 次超标）
     */
    public boolean evaluate(ChWarningRule rule, ChHealthMetricRecord currentRecord) {
        if (!rule.getMetricType().equals(currentRecord.getMetricType())) {
            return false;
        }
        boolean currentAbnormal = isAbnormal(rule, currentRecord.getMetricValue());
        if (!currentAbnormal) {
            return false;
        }
        if (rule.getConsecutiveWindow() == null || rule.getConsecutiveWindow() <= 1) {
            return true;
        }
        List<ChHealthMetricRecord> recentRecords = metricRecordMapper.selectList(
            Wrappers.<ChHealthMetricRecord>lambdaQuery()
                .eq(ChHealthMetricRecord::getPatientId, currentRecord.getPatientId())
                .eq(ChHealthMetricRecord::getMetricType, rule.getMetricType())
                .ne(ChHealthMetricRecord::getMetricId, currentRecord.getMetricId())
                .orderByDesc(ChHealthMetricRecord::getCreateTime)
                .last("LIMIT " + (rule.getConsecutiveWindow() - 1))
        );
        if (recentRecords.size() < rule.getConsecutiveWindow() - 1) {
            return false;
        }
        for (ChHealthMetricRecord record : recentRecords) {
            if (!isAbnormal(rule, record.getMetricValue())) {
                return false;
            }
        }
        return true;
    }

    private boolean isAbnormal(ChWarningRule rule, BigDecimal value) {
        boolean aboveMax = rule.getThresholdMax() != null && value.compareTo(rule.getThresholdMax()) > 0;
        boolean belowMin = rule.getThresholdMin() != null && value.compareTo(rule.getThresholdMin()) < 0;
        return aboveMax || belowMin;
    }
}
