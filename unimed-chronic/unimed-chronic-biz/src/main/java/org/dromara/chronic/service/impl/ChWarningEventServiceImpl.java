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
import org.dromara.chronic.domain.vo.ChWarningActionVo;
import org.dromara.chronic.domain.vo.ChWarningEventVo;
import org.dromara.chronic.mapper.ChWarningActionMapper;
import org.dromara.chronic.mapper.ChWarningEventMapper;
import org.dromara.chronic.service.IChWarningEventService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

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
        return TableDataInfo.build(page);
    }

    @Override
    public List<ChWarningEventVo> queryByPatientId(Long patientId) {
        return eventMapper.selectVoList(
            Wrappers.<ChWarningEvent>lambdaQuery()
                .eq(ChWarningEvent::getPatientId, patientId)
                .orderByDesc(ChWarningEvent::getWarningTime)
        );
    }

    @Override
    public Void updateStatus(Long warningId, String newStatus) {
        if (!VALID_STATUSES.contains(newStatus)) {
            throw new ServiceException("无效的预警事件状态: " + newStatus);
        }
        ChWarningEvent entity = eventMapper.selectById(warningId);
        if (ObjectUtil.isNull(entity)) {
            throw new ServiceException("预警事件不存在");
        }
        entity.setEventStatus(newStatus);
        eventMapper.updateById(entity);
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
