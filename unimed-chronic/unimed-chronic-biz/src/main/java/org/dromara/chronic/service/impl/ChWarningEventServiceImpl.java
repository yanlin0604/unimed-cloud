package org.dromara.chronic.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChWarningActionBo;
import org.dromara.chronic.domain.bo.ChWarningEventBo;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.domain.entity.ChWarningAction;
import org.dromara.chronic.domain.entity.ChWarningEvent;
import org.dromara.chronic.domain.entity.ChWarningRule;
import org.dromara.chronic.domain.vo.ChWarningActionVo;
import org.dromara.chronic.domain.vo.ChWarningEventVo;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.chronic.mapper.ChWarningActionMapper;
import org.dromara.chronic.mapper.ChWarningEventMapper;
import org.dromara.chronic.mapper.ChWarningRuleMapper;
import org.dromara.chronic.service.IChWarningEventService;
import org.dromara.chronic.support.rule.WarningStatusTransitionValidator;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 预警事件服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChWarningEventServiceImpl implements IChWarningEventService {

    private static final Set<String> VALID_STATUSES = Set.of("NEW", "CONFIRMED", "PROCESSING", "ESCALATED", "RESOLVED", "ARCHIVED");
    private static final List<String> ACTIVE_STATUSES = List.of("NEW", "CONFIRMED", "PROCESSING", "ESCALATED");
    private static final long LEGACY_PLAN_RULE_ID = 0L;

    private final ChWarningEventMapper eventMapper;
    private final ChWarningActionMapper actionMapper;
    private final ChWarningRuleMapper warningRuleMapper;
    private final ChPatientProfileMapper patientProfileMapper;

    /**
     * 批量回填事件来源名称和规则处置信息。
     */
    private void fillRuleDetails(Collection<ChWarningEventVo> vos) {
        if (vos == null || vos.isEmpty()) {
            return;
        }
        Set<Long> ruleIds = vos.stream()
            .filter(vo -> "RULE".equals(resolveEventSource(vo)))
            .map(ChWarningEventVo::getRuleId)
            .filter(ruleId -> ruleId != null && ruleId > 0)
            .collect(Collectors.toSet());
        Map<Long, ChWarningRule> ruleById = new HashMap<>();
        if (!ruleIds.isEmpty()) {
            for (ChWarningRule rule : warningRuleMapper.selectByIds(ruleIds)) {
                ruleById.put(rule.getRuleId(), rule);
            }
        }
        for (ChWarningEventVo vo : vos) {
            String eventSource = resolveEventSource(vo);
            vo.setEventSource(eventSource);
            if ("PLAN".equals(eventSource)) {
                vo.setRuleName("管理方案目标偏离");
            } else if ("SOS".equals(eventSource)) {
                vo.setRuleName("SOS紧急求助");
            } else if ("SLA".equals(eventSource)) {
                vo.setRuleName("签约服务SLA提醒");
            } else if ("MANUAL".equals(eventSource) && (vo.getRuleId() == null || vo.getRuleId() <= 0)) {
                vo.setRuleName("手动预警");
            } else {
                ChWarningRule rule = ruleById.get(vo.getRuleId());
                if (rule != null) {
                    vo.setRuleName(StringUtils.isNotBlank(rule.getRuleName()) ? rule.getRuleName() : rule.getDescription());
                    vo.setClinicalAdvice(rule.getClinicalAdvice());
                    vo.setResponseSlaHours(rule.getResponseSlaHours());
                }
            }
        }
    }

    private String resolveEventSource(ChWarningEventVo vo) {
        if (StringUtils.isNotBlank(vo.getEventSource())) {
            return vo.getEventSource();
        }
        if (Objects.equals(vo.getRuleId(), LEGACY_PLAN_RULE_ID)
            || (vo.getWarningValue() != null && vo.getWarningValue().startsWith("方案目标偏离"))) {
            return "PLAN";
        }
        if (vo.getWarningValue() != null && vo.getWarningValue().contains("SOS")) {
            return "SOS";
        }
        if ("SLA_VIOLATION".equals(vo.getWarningValue())) {
            return "SLA";
        }
        return vo.getRuleId() != null && vo.getRuleId() > 0 ? "RULE" : "MANUAL";
    }

    /**
     * 批量回填患者姓名
     */
    private void fillPatientName(Collection<ChWarningEventVo> vos) {
        if (vos == null || vos.isEmpty()) {
            return;
        }
        Set<Long> patientIds = vos.stream()
            .map(ChWarningEventVo::getPatientId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        if (patientIds.isEmpty()) {
            return;
        }
        Map<Long, String> patientNames = patientProfileMapper.selectByIds(patientIds).stream()
            .collect(Collectors.toMap(ChPatientProfile::getPatientId, ChPatientProfile::getName, (a, b) -> a));
        vos.forEach(vo -> vo.setPatientName(patientNames.get(vo.getPatientId())));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Long createEvent(ChWarningEventBo bo) {
        normalizeEventSource(bo);
        ChPatientProfile profile = bo.getPatientId() == null ? null : patientProfileMapper.selectById(bo.getPatientId());
        if (profile != null) {
            if (bo.getAssigneeUserId() == null) {
                bo.setAssigneeUserId(profile.getDoctorUserId());
            }
            if (bo.getOrgId() == null) {
                bo.setOrgId(profile.getOrgId());
            }
        }

        ChWarningEvent activeEvent = findActiveEvent(bo);
        if (activeEvent != null) {
            activeEvent.setWarningLevel(bo.getWarningLevel());
            activeEvent.setWarningValue(bo.getWarningValue());
            activeEvent.setWarningTime(new Date());
            activeEvent.setMetricType(bo.getMetricType());
            activeEvent.setPlanId(bo.getPlanId());
            activeEvent.setOrgId(bo.getOrgId());
            if (bo.getAssigneeUserId() != null) {
                activeEvent.setAssigneeUserId(bo.getAssigneeUserId());
            }
            eventMapper.updateById(activeEvent);
            return activeEvent.getWarningId();
        }

        ChWarningEvent entity = MapstructUtils.convert(bo, ChWarningEvent.class);
        if (entity.getEventStatus() == null) {
            entity.setEventStatus("NEW");
        }
        entity.setWarningTime(new Date());
        eventMapper.insert(entity);
        return entity.getWarningId();
    }

    private void normalizeEventSource(ChWarningEventBo bo) {
        if (StringUtils.isBlank(bo.getEventSource())) {
            if (Objects.equals(bo.getRuleId(), LEGACY_PLAN_RULE_ID)) {
                bo.setEventSource("PLAN");
            } else if (bo.getRuleId() != null && bo.getRuleId() > 0) {
                bo.setEventSource("RULE");
            } else {
                bo.setEventSource("MANUAL");
            }
        }
        bo.setEventSource(bo.getEventSource().trim().toUpperCase());
        if (bo.getSourceId() == null && "RULE".equals(bo.getEventSource())) {
            bo.setSourceId(bo.getRuleId());
        }
    }

    private ChWarningEvent findActiveEvent(ChWarningEventBo bo) {
        LambdaQueryWrapper<ChWarningEvent> query = Wrappers.lambdaQuery();
        query.eq(ChWarningEvent::getPatientId, bo.getPatientId())
            .eq(ChWarningEvent::getEventSource, bo.getEventSource())
            .in(ChWarningEvent::getEventStatus, ACTIVE_STATUSES)
            .eq(ChWarningEvent::getDelFlag, "0")
            .orderByDesc(ChWarningEvent::getWarningTime)
            .last("LIMIT 1");
        if (bo.getSourceId() != null) {
            query.eq(ChWarningEvent::getSourceId, bo.getSourceId());
        } else if (bo.getRuleId() != null) {
            query.eq(ChWarningEvent::getRuleId, bo.getRuleId());
        } else if (StringUtils.isNotBlank(bo.getMetricType())) {
            query.eq(ChWarningEvent::getMetricType, bo.getMetricType());
        } else {
            return null;
        }
        return eventMapper.selectOne(query);
    }

    @Override
    public ChWarningEventVo queryById(Long warningId) {
        ChWarningEventVo vo = eventMapper.selectVoById(warningId);
        if (vo != null) {
            vo.setActions(queryActionsByWarningId(warningId));
            fillRuleDetails(List.of(vo));
            fillPatientName(List.of(vo));
        }
        return vo;
    }

    @Override
    public TableDataInfo<ChWarningEventVo> queryPageList(ChWarningEventBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChWarningEvent> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getPatientId()), ChWarningEvent::getPatientId, bo.getPatientId());
        lqw.eq(ObjectUtil.isNotNull(bo.getRuleId()), ChWarningEvent::getRuleId, bo.getRuleId());
        lqw.eq(StringUtils.isNotBlank(bo.getEventSource()), ChWarningEvent::getEventSource, bo.getEventSource());
        lqw.eq(ObjectUtil.isNotNull(bo.getSourceId()), ChWarningEvent::getSourceId, bo.getSourceId());
        lqw.eq(StringUtils.isNotBlank(bo.getMetricType()), ChWarningEvent::getMetricType, bo.getMetricType());
        lqw.eq(ObjectUtil.isNotNull(bo.getPlanId()), ChWarningEvent::getPlanId, bo.getPlanId());
        lqw.eq(ObjectUtil.isNotNull(bo.getOrgId()), ChWarningEvent::getOrgId, bo.getOrgId());
        lqw.eq(ObjectUtil.isNotNull(bo.getAssigneeUserId()), ChWarningEvent::getAssigneeUserId, bo.getAssigneeUserId());
        lqw.eq(StringUtils.isNotBlank(bo.getWarningLevel()), ChWarningEvent::getWarningLevel, bo.getWarningLevel());
        lqw.eq(StringUtils.isNotBlank(bo.getEventStatus()), ChWarningEvent::getEventStatus, bo.getEventStatus());
        lqw.orderByDesc(ChWarningEvent::getWarningTime);
        Page<ChWarningEventVo> page = eventMapper.selectVoPage(pageQuery.build(), lqw);
        fillRuleDetails(page.getRecords());
        fillPatientName(page.getRecords());
        return TableDataInfo.build(page);
    }

    @Override
    public List<ChWarningEventVo> queryTodoByAssignee(Long assigneeUserId) {
        if (assigneeUserId == null) {
            throw new ServiceException("未获取当前医生身份");
        }
        List<ChWarningEventVo> list = eventMapper.selectVoList(
            Wrappers.<ChWarningEvent>lambdaQuery()
                .eq(ChWarningEvent::getAssigneeUserId, assigneeUserId)
                .notIn(ChWarningEvent::getEventStatus, List.of("RESOLVED", "ARCHIVED"))
                .orderByDesc(ChWarningEvent::getWarningTime));
        fillRuleDetails(list);
        fillPatientName(list);
        return list;
    }

    @Override
    public List<ChWarningEventVo> queryByPatientId(Long patientId) {
        List<ChWarningEventVo> list = eventMapper.selectVoList(
            Wrappers.<ChWarningEvent>lambdaQuery()
                .eq(ChWarningEvent::getPatientId, patientId)
                .orderByDesc(ChWarningEvent::getWarningTime)
        );
        fillRuleDetails(list);
        fillPatientName(list);
        return list;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public int resolveActiveEvents(Long patientId, String eventSource, Long sourceId, String detail) {
        if (patientId == null || StringUtils.isBlank(eventSource) || sourceId == null) {
            return 0;
        }
        List<ChWarningEvent> activeEvents = eventMapper.selectList(
            Wrappers.<ChWarningEvent>lambdaQuery()
                .eq(ChWarningEvent::getPatientId, patientId)
                .eq(ChWarningEvent::getEventSource, eventSource)
                .eq(ChWarningEvent::getSourceId, sourceId)
                .in(ChWarningEvent::getEventStatus, ACTIVE_STATUSES)
        );
        for (ChWarningEvent activeEvent : activeEvents) {
            updateStatus(activeEvent.getWarningId(), "RESOLVED", null,
                StringUtils.isNotBlank(detail) ? detail : "指标已恢复正常，系统自动解决");
        }
        return activeEvents.size();
    }

    @Override
    public Void updateStatus(Long warningId, String newStatus) {
        // 委托到含操作人上下文的重载方法，保持状态机校验一致性
        return updateStatus(warningId, newStatus, null, null);
    }

    /**
     * 将目标状态映射为对应的 action_type
     */
    private String mapStatusToActionType(String targetStatus) {
        return switch (targetStatus) {
            case "CONFIRMED" -> "CONFIRM";
            case "PROCESSING" -> "HANDLE";
            case "ESCALATED" -> "ESCALATE";
            case "RESOLVED" -> "RESOLVE";
            case "ARCHIVED" -> "ARCHIVE";
            default -> "STATUS_CHANGE";
        };
    }

    /**
     * R4+R7: 更新预警事件状态（含操作人上下文），校验迁移合法性 + 自动写入 action
     */
    @Override
    public Void updateStatus(Long warningId, String newStatus, Long actionUserId, String actionDetail) {
        if (!VALID_STATUSES.contains(newStatus)) {
            throw new ServiceException("无效的预警事件状态: " + newStatus);
        }
        ChWarningEvent entity = eventMapper.selectById(warningId);
        if (ObjectUtil.isNull(entity)) {
            throw new ServiceException("预警事件不存在");
        }
        WarningStatusTransitionValidator.validate(entity.getEventStatus(), newStatus);
        String previousStatus = entity.getEventStatus();
        entity.setEventStatus(newStatus);
        // R4: 如果提供了 assigneeUserId 则更新处理人
        if (actionUserId != null) {
            entity.setAssigneeUserId(actionUserId);
        }
        eventMapper.updateById(entity);
        // 自动写入 action 记录（含操作人上下文）
        if (!previousStatus.equals(newStatus)) {
            ChWarningAction action = new ChWarningAction();
            action.setWarningId(warningId);
            action.setActionType(mapStatusToActionType(newStatus));
            action.setActionDetail(actionDetail != null ? actionDetail : "状态变更: " + previousStatus + " → " + newStatus);
            action.setActionUserId(actionUserId);
            action.setActionTime(new Date());
            actionMapper.insert(action);
        }
        return null;
    }

    @Override
    public Long addAction(ChWarningActionBo bo) {
        ChWarningAction entity = MapstructUtils.convert(bo, ChWarningAction.class);
        entity.setActionTime(new Date());
        actionMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public List<ChWarningActionVo> queryActionsByWarningId(Long warningId) {
        return actionMapper.selectVoList(
            Wrappers.<ChWarningAction>lambdaQuery()
                .eq(ChWarningAction::getWarningId, warningId)
                .orderByAsc(ChWarningAction::getActionTime)
        );
    }
}
