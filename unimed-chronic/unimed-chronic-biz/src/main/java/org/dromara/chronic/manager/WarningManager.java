package org.dromara.chronic.manager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChWarningActionBo;
import org.dromara.chronic.domain.bo.ChWarningEventBo;
import org.dromara.chronic.domain.entity.ChHealthMetricRecord;
import org.dromara.chronic.domain.entity.ChWarningRule;
import org.dromara.chronic.domain.vo.ChWarningEventVo;
import org.dromara.chronic.mapper.ChHealthMetricRecordMapper;
import org.dromara.chronic.mapper.ChWarningRuleMapper;
import org.dromara.chronic.service.IChWarningEventService;
import org.dromara.chronic.support.rule.WarningRuleEngine;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 预警管理器：规则匹配→触发事件→通知→处置→闭环
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WarningManager {

    private final WarningRuleEngine ruleEngine;
    private final IChWarningEventService warningEventService;
    private final ChWarningRuleMapper warningRuleMapper;
    private final ChHealthMetricRecordMapper metricRecordMapper;

    /**
     * 指标上报后检查所有匹配规则，触发预警事件
     */
    @Transactional(rollbackFor = Exception.class)
    public void checkAndTrigger(ChHealthMetricRecord record) {
        List<ChWarningRule> rules = warningRuleMapper.selectList(
            Wrappers.<ChWarningRule>lambdaQuery()
                .eq(ChWarningRule::getMetricType, record.getMetricType())
        );
        for (ChWarningRule rule : rules) {
            if (ruleEngine.evaluate(rule, record)) {
                createWarningEvent(record, rule);
            }
        }
    }

    /**
     * 处置预警事件（状态流转+记录动作）
     */
    @Transactional(rollbackFor = Exception.class)
    public Void handleEvent(Long warningId, String actionType, String actionDetail, Long actionUserId) {
        String newStatus;
        switch (actionType) {
            case "CONFIRM" -> newStatus = "CONFIRMED";
            case "HANDLE" -> newStatus = "PROCESSING";
            case "ESCALATE" -> newStatus = "ESCALATED";
            case "RESOLVE" -> newStatus = "RESOLVED";
            default -> throw new org.dromara.common.core.exception.ServiceException("不支持的处置类型: " + actionType);
        }
        warningEventService.updateStatus(warningId, newStatus);
        ChWarningActionBo actionBo = new ChWarningActionBo();
        actionBo.setWarningId(warningId);
        actionBo.setActionType(actionType);
        actionBo.setActionDetail(actionDetail);
        actionBo.setActionUserId(actionUserId);
        warningEventService.addAction(actionBo);
        return null;
    }

    public ChWarningEventVo queryDetail(Long warningId) {
        return warningEventService.queryById(warningId);
    }

    private void createWarningEvent(ChHealthMetricRecord record, ChWarningRule rule) {
        ChWarningEventBo bo = new ChWarningEventBo();
        bo.setPatientId(record.getPatientId());
        bo.setRuleId(rule.getRuleId());
        bo.setWarningLevel(rule.getWarningLevel());
        bo.setWarningValue(record.getMetricValue());
        bo.setEventStatus("NEW");
        warningEventService.createEvent(bo);
        log.info("预警触发: patientId={}, metricType={}, level={}", record.getPatientId(), record.getMetricType(), rule.getWarningLevel());
    }
}
