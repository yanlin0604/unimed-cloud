package org.dromara.chronic.service.impl;

import org.dromara.chronic.domain.bo.ChWarningEventBo;
import org.dromara.chronic.domain.entity.ChWarningEvent;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.chronic.mapper.ChWarningActionMapper;
import org.dromara.chronic.mapper.ChWarningEventMapper;
import org.dromara.chronic.mapper.ChWarningRuleMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 预警事件来源去重测试。
 */
@Tag("chronic-dev")
public class ChWarningEventServiceImplTest {

    @Test
    public void createEventShouldRefreshExistingActiveEventWithSameSource() {
        ChWarningEventMapper eventMapper = mock(ChWarningEventMapper.class);
        ChWarningActionMapper actionMapper = mock(ChWarningActionMapper.class);
        ChWarningRuleMapper warningRuleMapper = mock(ChWarningRuleMapper.class);
        ChPatientProfileMapper patientProfileMapper = mock(ChPatientProfileMapper.class);
        ChWarningEventServiceImpl service = new ChWarningEventServiceImpl(
            eventMapper, actionMapper, warningRuleMapper, patientProfileMapper);

        ChWarningEvent activeEvent = new ChWarningEvent();
        activeEvent.setWarningId(99L);
        activeEvent.setPatientId(100L);
        activeEvent.setEventSource("PLAN");
        activeEvent.setSourceId(200L);
        activeEvent.setEventStatus("NEW");
        activeEvent.setWarningTime(new Date(1L));
        when(eventMapper.selectOne(any())).thenReturn(activeEvent);

        ChWarningEventBo eventBo = new ChWarningEventBo();
        eventBo.setPatientId(100L);
        eventBo.setRuleId(0L);
        eventBo.setEventSource("plan");
        eventBo.setSourceId(200L);
        eventBo.setPlanId(300L);
        eventBo.setMetricType("BP_SYSTOLIC");
        eventBo.setWarningLevel("LOW");
        eventBo.setWarningValue("方案目标偏离: 当前值=160");

        Long warningId = service.createEvent(eventBo);

        assertEquals(99L, warningId);
        assertEquals("PLAN", eventBo.getEventSource());
        assertEquals("方案目标偏离: 当前值=160", activeEvent.getWarningValue());
        assertEquals(300L, activeEvent.getPlanId());
        assertEquals("BP_SYSTOLIC", activeEvent.getMetricType());
        verify(eventMapper).updateById(activeEvent);
        verify(eventMapper, never()).insert(any(ChWarningEvent.class));
    }
}
