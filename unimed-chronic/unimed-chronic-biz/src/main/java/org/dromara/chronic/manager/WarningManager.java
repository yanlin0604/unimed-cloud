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
import org.dromara.chronic.domain.entity.ChFollowupPlan;
import org.dromara.chronic.domain.entity.ChFollowupTask;
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
    private final org.dromara.chronic.mapper.ChFollowupTaskMapper followupTaskMapper;
    private final org.dromara.chronic.mapper.ChFollowupPlanMapper followupPlanMapper;

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

        // 远程监测预警联动：二级及以上严重预警（HIGH/VERY_HIGH）自动插入临时紧急随访干预任务。
        // 仅适用于患者自测/设备/OCR 等"尚无医生介入"的数据来源；随访现场由医生当面测量并已给出
        // 临床结论的指标不在此列（该场景的后续任务由 FollowupDynamicAdjuster 依医生结论决定），
        // 否则会出现"医生刚提交随访 → 系统给同一个医生派一条电话干预任务"的自触发闭环。
        if (Set.of("HIGH", "VERY_HIGH").contains(rule.getWarningLevel())) {
            if ("FOLLOWUP".equals(record.getMeasureScene())) {
                log.info("随访现场指标已由医生当面处置, 跳过紧急干预任务生成: patientId={}, metricType={}",
                    record.getPatientId(), record.getMetricType());
            } else {
                createEmergencyFollowupTask(record, rule, bo.getAssigneeUserId());
            }
        }
    }

    /**
     * 该患者是否已存在未完结的紧急干预随访任务。
     * 预警事件本身会按活跃事件去重（见 ChWarningEventServiceImpl.createEvent），但紧急任务此前是裸 insert，
     * 导致同一个未处理预警每上报一次指标就多一条待办，医生待办被雪崩式污染。
     */
    private boolean hasOpenEmergencyTask(Long patientId) {
        return followupTaskMapper.exists(
            Wrappers.<ChFollowupTask>lambdaQuery()
                .eq(ChFollowupTask::getPatientId, patientId)
                .eq(ChFollowupTask::getTaskType, "EMERGENCY")
                .notIn(ChFollowupTask::getTaskStatus, List.of("DONE", "CANCELLED")));
    }

    private void createEmergencyFollowupTask(ChHealthMetricRecord record, ChWarningRule rule, Long assigneeUserId) {
        if (followupTaskMapper == null || record.getPatientId() == null) return;
        try {
            if (hasOpenEmergencyTask(record.getPatientId())) {
                log.info("已存在未完结的紧急干预随访任务, 跳过重复生成: patientId={}", record.getPatientId());
                return;
            }
            ChFollowupPlan plan = followupPlanMapper != null ? followupPlanMapper.selectOne(
                Wrappers.<ChFollowupPlan>lambdaQuery()
                    .eq(ChFollowupPlan::getPatientId, record.getPatientId())
                    .eq(ChFollowupPlan::getPlanStatus, "ACTIVE")
                    .orderByDesc(ChFollowupPlan::getCreateTime)
                    .last("limit 1")
            ) : null;
            ChFollowupTask task = new ChFollowupTask();
            task.setPatientId(record.getPatientId());
            task.setPlanId(plan != null ? plan.getPlanId() : null);
            // 紧急任务不属于计划轮次: 此前恒置 1 会与计划内 round1 撞键(污染 FollowupTaskGenJob 的
            // planId+round 去重, 且前端把它显示成"第 1 轮"), 故留空
            task.setTaskRound(null);
            task.setTaskType("EMERGENCY");
            task.setVisitType("PHONE");
            task.setIsFaceToFace(false);
            task.setPlanDueDate(new Date());
            task.setTaskStatus("PENDING");
            task.setAssigneeUserId(assigneeUserId);
            followupTaskMapper.insert(task);
            log.info("预警联动: 已自动生成紧急干预随访任务 taskId={}, patientId={}, warningLevel={}",
                task.getTaskId(), record.getPatientId(), rule.getWarningLevel());
        } catch (Exception e) {
            log.warn("预警联动生成紧急随访任务失败 patientId={}, err={}", record.getPatientId(), e.getMessage());
        }
    }
}
