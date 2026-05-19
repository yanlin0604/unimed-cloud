package org.dromara.chronic.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChWarningActionBo;
import org.dromara.chronic.domain.bo.ChWarningEventBo;
import org.dromara.chronic.domain.entity.ChWarningAction;
import org.dromara.chronic.domain.entity.ChWarningEvent;
import org.dromara.chronic.domain.entity.ChWarningRule;
import org.dromara.chronic.domain.vo.ChWarningActionVo;
import org.dromara.chronic.domain.vo.ChWarningEventVo;
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

    private final ChWarningEventMapper eventMapper;
    private final ChWarningActionMapper actionMapper;
    private final ChWarningRuleMapper warningRuleMapper;

    /**
     * 批量回填规则名称：取 ch_warning_rule.rule_name 作为 ruleName
     */
    private void fillRuleName(Collection<ChWarningEventVo> vos) {
        if (vos == null || vos.isEmpty()) {
            return;
        }
        Set<Long> ruleIds = vos.stream()
            .map(ChWarningEventVo::getRuleId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        if (ruleIds.isEmpty()) {
            return;
        }
        List<ChWarningRule> rules = warningRuleMapper.selectByIds(ruleIds);
        Map<Long, String> ruleIdToName = new HashMap<>(rules.size());
        for (ChWarningRule rule : rules) {
            ruleIdToName.put(rule.getRuleId(), rule.getRuleName());
        }
        vos.forEach(vo -> vo.setRuleName(ruleIdToName.get(vo.getRuleId())));
    }

    @Override
    public Long createEvent(ChWarningEventBo bo) {
        ChWarningEvent entity = MapstructUtils.convert(bo, ChWarningEvent.class);
        if (entity.getEventStatus() == null) {
            entity.setEventStatus("NEW");
        }
        entity.setWarningTime(new Date());
        eventMapper.insert(entity);
        return entity.getWarningId();
    }

    @Override
    public ChWarningEventVo queryById(Long warningId) {
        ChWarningEventVo vo = eventMapper.selectVoById(warningId);
        if (vo != null) {
            vo.setActions(queryActionsByWarningId(warningId));
            fillRuleName(List.of(vo));
        }
        return vo;
    }

    @Override
    public TableDataInfo<ChWarningEventVo> queryPageList(ChWarningEventBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChWarningEvent> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getPatientId()), ChWarningEvent::getPatientId, bo.getPatientId());
        lqw.eq(ObjectUtil.isNotNull(bo.getRuleId()), ChWarningEvent::getRuleId, bo.getRuleId());
        lqw.eq(StringUtils.isNotBlank(bo.getWarningLevel()), ChWarningEvent::getWarningLevel, bo.getWarningLevel());
        lqw.eq(StringUtils.isNotBlank(bo.getEventStatus()), ChWarningEvent::getEventStatus, bo.getEventStatus());
        lqw.orderByDesc(ChWarningEvent::getWarningTime);
        Page<ChWarningEventVo> page = eventMapper.selectVoPage(pageQuery.build(), lqw);
        fillRuleName(page.getRecords());
        return TableDataInfo.build(page);
    }

    @Override
    public List<ChWarningEventVo> queryByPatientId(Long patientId) {
        List<ChWarningEventVo> list = eventMapper.selectVoList(
            Wrappers.<ChWarningEvent>lambdaQuery()
                .eq(ChWarningEvent::getPatientId, patientId)
                .orderByDesc(ChWarningEvent::getWarningTime)
        );
        fillRuleName(list);
        return list;
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
