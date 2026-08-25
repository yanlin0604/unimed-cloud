package org.dromara.chronic.manager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChWarningEventBo;
import org.dromara.chronic.domain.entity.ChHealthMetricRecord;
import org.dromara.chronic.domain.entity.ChManagePlan;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.domain.entity.ChSosRecord;
import org.dromara.chronic.domain.entity.ChWarningRule;
import org.dromara.chronic.domain.vo.ChWarningEventVo;
import org.dromara.chronic.mapper.ChHealthMetricRecordMapper;
import org.dromara.chronic.domain.entity.ChPatientDisease;
import org.dromara.chronic.mapper.ChManagePlanMapper;
import org.dromara.chronic.mapper.ChPatientDiseaseMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.chronic.mapper.ChSosRecordMapper;
import org.dromara.chronic.mapper.ChWarningRuleMapper;
import org.dromara.chronic.service.IChWarningEventService;
import org.dromara.chronic.support.rule.WarningRuleEngine;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final ChPatientProfileMapper patientProfileMapper;
    private final ChPatientDiseaseMapper patientDiseaseMapper;
    private final ChManagePlanMapper managePlanMapper;
    private final ChSosRecordMapper sosRecordMapper;

    /**
     * 指标上报后检查所有匹配规则，触发预警事件（精准按患者专病过滤）
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW,
        rollbackFor = Exception.class)
    public void checkAndTrigger(ChHealthMetricRecord record) {
        if (record == null || record.getPatientId() == null) {
            return;
        }

        ChPatientProfile patientProfile = patientProfileMapper.selectById(record.getPatientId());
        Long patientOrgId = patientProfile == null ? null : patientProfile.getOrgId();

        // 查询患者已确诊/管理的专病编码列表
        List<ChPatientDisease> patientDiseases = patientDiseaseMapper.selectList(
            Wrappers.<ChPatientDisease>lambdaQuery()
                .eq(ChPatientDisease::getPatientId, record.getPatientId())
                .eq(ChPatientDisease::getEnableStatus, true)
                .eq(ChPatientDisease::getDelFlag, "0")
        );
        Set<String> patientDiseaseCodes = patientDiseases.stream()
            .map(ChPatientDisease::getDiseaseCode)
            .filter(StringUtils::isNotBlank)
            .map(this::normalizeDiseaseCode)
            .collect(Collectors.toSet());

        // 补充：从该患者当前 ACTIVE 的管理方案中获取 diseaseCode（取并集，避免专病未录入时漏匹配）
        List<ChManagePlan> activePlans = managePlanMapper.selectList(
            Wrappers.<ChManagePlan>lambdaQuery()
                .eq(ChManagePlan::getPatientId, record.getPatientId())
                .eq(ChManagePlan::getPlanStatus, "ACTIVE")
                .eq(ChManagePlan::getDelFlag, "0")
        );
        activePlans.stream()
            .map(ChManagePlan::getDiseaseCode)
            .filter(StringUtils::isNotBlank)
            .map(this::normalizeDiseaseCode)
            .forEach(patientDiseaseCodes::add);

        var ruleQuery = Wrappers.<ChWarningRule>lambdaQuery()
            .eq(ChWarningRule::getDelFlag, "0");
        if (patientOrgId != null) {
            ruleQuery.and(wrapper -> wrapper.isNull(ChWarningRule::getOrgId)
                .or()
                .eq(ChWarningRule::getOrgId, patientOrgId));
        } else {
            // 没有机构归属的患者只能命中租户级通用规则，避免跨机构规则泄漏。
            ruleQuery.isNull(ChWarningRule::getOrgId);
        }
        List<ChWarningRule> rules = warningRuleMapper.selectList(ruleQuery);

        for (ChWarningRule rule : rules) {
            // 专病过滤：如果规则限定了专病，且患者未患该病种，则跳过
            String ruleDisease = normalizeDiseaseCode(rule.getDiseaseCode());
            if (StringUtils.isNotBlank(ruleDisease) && !"*".equals(ruleDisease) && !"ALL".equals(ruleDisease)) {
                if (!patientDiseaseCodes.contains(ruleDisease)) {
                    continue;
                }
            }

            if (!WarningRuleEngine.isMetricTypeMatch(rule.getMetricType(), record.getMetricType())) {
                continue;
            }

            if (ruleEngine.evaluate(rule, record)) {
                createWarningEvent(record, rule);
            } else if (!ruleEngine.isCurrentValueAbnormal(rule, record)) {
                warningEventService.resolveActiveEvents(record.getPatientId(), "RULE", rule.getRuleId(),
                    "指标已恢复到预警规则正常范围");
            }
        }
    }

    private String normalizeDiseaseCode(String diseaseCode) {
        return diseaseCode == null ? null : diseaseCode.trim().toUpperCase(Locale.ROOT);
    }

    /**
     * 处置预警事件（状态流转+记录动作+SOS记录联动闭环）
     * <p>
     * C11: 状态更新与动作记录统一由 {@code updateStatus} 完成，
     * 此处仅负责映射 actionType→newStatus 并注入操作人上下文。
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
        // updateStatus 内部完成状态校验 + 变更 + 自动写入 action 记录
        warningEventService.updateStatus(warningId, newStatus, actionUserId, actionDetail);

        // 如果是 SOS 紧急预警处置，联动同步更新 ch_sos_record
        try {
            ChWarningEventVo event = warningEventService.queryById(warningId);
            if (event != null && event.getPatientId() != null &&
                (event.getWarningValue() != null && event.getWarningValue().contains("SOS"))) {
                String sosEventStatus = switch (newStatus) {
                    case "RESOLVED" -> "RESOLVED";
                    case "PROCESSING", "ESCALATED" -> "HANDLING";
                    default -> null;
                };
                if (sosEventStatus != null) {
                    List<ChSosRecord> activeSosList = sosRecordMapper.selectList(
                        Wrappers.<ChSosRecord>lambdaQuery()
                            .eq(ChSosRecord::getPatientId, event.getPatientId())
                            .in(ChSosRecord::getEventStatus, List.of("NEW", "HANDLING"))
                    );
                    for (ChSosRecord sos : activeSosList) {
                        sos.setEventStatus(sosEventStatus);
                        sos.setHandlerUserId(actionUserId);
                        sos.setHandleTime(new Date());
                        if (actionDetail != null) {
                            sos.setHandleRemark(actionDetail);
                        }
                        sosRecordMapper.updateById(sos);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("同步更新SOS记录状态失败: warningId={}, error={}", warningId, e.getMessage());
        }

        return null;
    }

    /**
     * 手动触发预警事件
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createWarningEvent(Long patientId, Long ruleId) {
        ChWarningRule rule = warningRuleMapper.selectById(ruleId);
        if (rule == null) {
            throw new org.dromara.common.core.exception.ServiceException("预警规则不存在: " + ruleId);
        }
        ChWarningEventBo bo = new ChWarningEventBo();
        bo.setPatientId(patientId);
        bo.setRuleId(ruleId);
        bo.setWarningLevel(rule.getWarningLevel());
        bo.setEventStatus("NEW");
        if (patientId != null) {
            ChPatientProfile profile = patientProfileMapper.selectById(patientId);
            if (profile != null && profile.getDoctorUserId() != null) {
                bo.setAssigneeUserId(profile.getDoctorUserId());
            }
        }
        Long warningId = warningEventService.createEvent(bo);
        log.info("手动触发预警: patientId={}, ruleId={}, level={}, assigneeUserId={}", patientId, ruleId, rule.getWarningLevel(), bo.getAssigneeUserId());
        return warningId;
    }

    public ChWarningEventVo queryDetail(Long warningId) {
        return warningEventService.queryById(warningId);
    }

    private void createWarningEvent(ChHealthMetricRecord record, ChWarningRule rule) {
        ChWarningEventBo bo = new ChWarningEventBo();
        bo.setPatientId(record.getPatientId());
        bo.setRuleId(rule.getRuleId());
        bo.setEventSource("RULE");
        bo.setSourceId(rule.getRuleId());
        bo.setMetricType(WarningRuleEngine.normalizeMetricType(record.getMetricType()));
        bo.setWarningLevel(rule.getWarningLevel());
        bo.setWarningValue(record.getMetricValue());
        bo.setEventStatus("NEW");
        if (record.getPatientId() != null) {
            ChPatientProfile profile = patientProfileMapper.selectById(record.getPatientId());
            if (profile != null && profile.getDoctorUserId() != null) {
                bo.setAssigneeUserId(profile.getDoctorUserId());
            }
        }
        warningEventService.createEvent(bo);
        log.info("预警触发: patientId={}, metricType={}, level={}, assigneeUserId={}",
            record.getPatientId(), record.getMetricType(), rule.getWarningLevel(), bo.getAssigneeUserId());
    }
}
