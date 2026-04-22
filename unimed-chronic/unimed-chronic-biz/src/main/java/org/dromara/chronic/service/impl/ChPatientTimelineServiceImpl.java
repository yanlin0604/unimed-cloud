package org.dromara.chronic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.entity.ChPatientTimeline;
import org.dromara.chronic.domain.vo.ChPatientTimelineVo;
import org.dromara.chronic.mapper.ChPatientTimelineMapper;
import org.dromara.chronic.service.IChPatientTimelineService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 患者时间线服务实现
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChPatientTimelineServiceImpl implements IChPatientTimelineService {

    private final ChPatientTimelineMapper baseMapper;

    @Override
    public void recordEvent(Long patientId, String eventType, String eventTitle, String eventDetail, LocalDateTime eventTime) {
        try {
            ChPatientTimeline timeline = new ChPatientTimeline();
            timeline.setPatientId(patientId);
            timeline.setEventType(eventType);
            timeline.setEventTitle(eventTitle);
            timeline.setEventDetail(eventDetail);
            timeline.setEventTime(eventTime != null ? Date.from(eventTime.atZone(java.time.ZoneId.systemDefault()).toInstant()) : new Date());
            baseMapper.insert(timeline);
        } catch (Exception e) {
            log.warn("记录患者时间线事件失败, patientId={}, eventType={}", patientId, eventType, e);
        }
    }

    @Override
    public TableDataInfo<ChPatientTimelineVo> queryPageList(Long patientId, PageQuery pageQuery) {
        LambdaQueryWrapper<ChPatientTimeline> lqw = Wrappers.lambdaQuery();
        lqw.eq(ChPatientTimeline::getPatientId, patientId);
        lqw.orderByDesc(ChPatientTimeline::getEventTime);
        Page<ChPatientTimelineVo> page = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public TableDataInfo<ChPatientTimelineVo> queryPageListByEventTypes(Long patientId, List<String> eventTypes, PageQuery pageQuery) {
        LambdaQueryWrapper<ChPatientTimeline> lqw = Wrappers.lambdaQuery();
        lqw.eq(ChPatientTimeline::getPatientId, patientId);
        if (eventTypes != null && !eventTypes.isEmpty()) {
            lqw.in(ChPatientTimeline::getEventType, eventTypes);
        }
        lqw.orderByDesc(ChPatientTimeline::getEventTime);
        Page<ChPatientTimelineVo> page = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public List<ChPatientTimelineVo> queryList(Long patientId, String eventType, Integer limit) {
        LambdaQueryWrapper<ChPatientTimeline> lqw = Wrappers.lambdaQuery();
        lqw.eq(ChPatientTimeline::getPatientId, patientId);
        if (eventType != null) {
            lqw.eq(ChPatientTimeline::getEventType, eventType);
        }
        lqw.orderByDesc(ChPatientTimeline::getEventTime);
        if (limit != null && limit > 0) {
            lqw.last("LIMIT " + limit);
        }
        return baseMapper.selectVoList(lqw);
    }
}