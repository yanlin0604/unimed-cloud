package org.dromara.chronic.support.rule;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.dromara.chronic.domain.entity.ChFollowupPlan;
import org.dromara.chronic.domain.entity.ChFollowupPlanItem;
import org.dromara.chronic.domain.entity.ChFollowupTask;
import org.dromara.chronic.mapper.ChFollowupPlanItemMapper;
import org.dromara.chronic.mapper.ChFollowupQuestionnaireMapper;
import org.dromara.chronic.mapper.ChFollowupRuleMapper;
import org.dromara.chronic.mapper.ChFollowupTaskMapper;
import org.dromara.common.core.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 随访逐轮任务生成器单元测试
 *
 * @author unimed
 */
@Tag("chronic-dev")
class FollowupRoundTaskGeneratorTest {

    private ChFollowupTaskMapper taskMapper;
    private ChFollowupPlanItemMapper planItemMapper;
    private FollowupRoundTaskGenerator generator;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ChFollowupTask.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ChFollowupPlanItem.class);
        taskMapper = mock(ChFollowupTaskMapper.class);
        planItemMapper = mock(ChFollowupPlanItemMapper.class);
        ChFollowupRuleMapper ruleMapper = mock(ChFollowupRuleMapper.class);
        ChFollowupQuestionnaireMapper questionnaireMapper = mock(ChFollowupQuestionnaireMapper.class);
        when(ruleMapper.selectList(any())).thenReturn(Collections.emptyList());
        generator = new FollowupRoundTaskGenerator(
            taskMapper, planItemMapper, new FollowupRuleEngine(questionnaireMapper, ruleMapper));
    }

    private ChFollowupPlan plan() {
        ChFollowupPlan plan = new ChFollowupPlan();
        plan.setPlanId(10L);
        plan.setPatientId(100L);
        plan.setDiseaseCode("HTN");
        plan.setManagementLevel("LOW");
        plan.setAssigneeUserId(200L);
        plan.setTotalRounds(4);
        plan.setCurrentRound(0);
        plan.setPlanStatus("ACTIVE");
        plan.setCreateTime(new Date());
        return plan;
    }

    @Test
    @DisplayName("首轮任务按 OFFLINE 推导面对面标记并回落租户与部门")
    void ensureRoundAssemblesFields() {
        when(taskMapper.selectOne(any())).thenReturn(null);

        Date due = new Date();
        ChFollowupTask task = generator.ensureRound(plan(), 1, due, "OFFLINE");

        ArgumentCaptor<ChFollowupTask> captor = ArgumentCaptor.forClass(ChFollowupTask.class);
        verify(taskMapper, times(1)).insert(captor.capture());
        ChFollowupTask saved = captor.getValue();
        assertSame(saved, task);
        assertEquals(10L, saved.getPlanId());
        assertEquals(100L, saved.getPatientId());
        assertEquals(1, saved.getTaskRound());
        assertEquals("PENDING", saved.getTaskStatus());
        assertEquals("NORMAL", saved.getTaskType());
        assertEquals("OFFLINE", saved.getVisitType());
        assertTrue(saved.getIsFaceToFace());
        assertEquals(200L, saved.getAssigneeUserId());
        assertEquals("000000", saved.getTenantId());
        assertEquals(103L, saved.getCreateDept());
        assertEquals("0", saved.getDelFlag());
        assertEquals(due, saved.getPlanDueDate());
    }

    @Test
    @DisplayName("同轮次已存在任务时幂等跳过，不重复插入")
    void ensureRoundIsIdempotent() {
        ChFollowupTask existing = new ChFollowupTask();
        existing.setTaskId(999L);
        existing.setTaskRound(1);
        when(taskMapper.selectOne(any())).thenReturn(existing);

        ChFollowupTask result = generator.ensureRound(plan(), 1, new Date(), "PHONE");

        assertSame(existing, result);
        verify(taskMapper, never()).insert(any(ChFollowupTask.class));
    }

    @Test
    @DisplayName("随访方式为空时回落规则默认方式(HTN 默认 PHONE)")
    void ensureRoundFallsBackToRuleVisitType() {
        when(taskMapper.selectOne(any())).thenReturn(null);

        generator.ensureRound(plan(), 1, new Date(), null);

        ArgumentCaptor<ChFollowupTask> captor = ArgumentCaptor.forClass(ChFollowupTask.class);
        verify(taskMapper).insert(captor.capture());
        assertEquals("PHONE", captor.getValue().getVisitType());
        assertFalse(captor.getValue().getIsFaceToFace());
    }

    @Test
    @DisplayName("计划主键或患者缺失时拒绝生成，避免孤儿任务")
    void ensureRoundRejectsIncompletePlan() {
        ChFollowupPlan bad = plan();
        bad.setPlanId(null);
        assertThrows(ServiceException.class, () -> generator.ensureRound(bad, 1, new Date(), "PHONE"));

        ChFollowupPlan noPatient = plan();
        noPatient.setPatientId(null);
        assertThrows(ServiceException.class, () -> generator.ensureRound(noPatient, 1, new Date(), "PHONE"));
        verify(taskMapper, never()).insert(any(ChFollowupTask.class));
    }

    @Test
    @DisplayName("下一轮沿用本轮随访方式且轮次加一")
    void generateNextRoundInheritsRoundAndVisitType() {
        ChFollowupTask finished = new ChFollowupTask();
        finished.setTaskId(1L);
        finished.setPlanId(10L);
        finished.setPatientId(100L);
        finished.setTaskRound(2);
        finished.setVisitType("VIDEO");
        when(taskMapper.selectOne(any())).thenReturn(null);

        Date next = cn.hutool.core.date.DateUtil.offsetDay(new Date(), 30);
        ChFollowupTask created = generator.generateNextRound(finished, plan(), next);

        ArgumentCaptor<ChFollowupTask> captor = ArgumentCaptor.forClass(ChFollowupTask.class);
        verify(taskMapper).insert(captor.capture());
        assertEquals(3, captor.getValue().getTaskRound());
        assertEquals("VIDEO", captor.getValue().getVisitType());
        assertEquals(created, captor.getValue());
    }

    @Test
    @DisplayName("计划外临时任务(taskRound 为空)不推进轮次")
    void generateNextRoundSkipsOutOfPlanTask() {
        ChFollowupTask emergency = new ChFollowupTask();
        emergency.setTaskId(1L);
        emergency.setPlanId(10L);
        emergency.setTaskRound(null);
        emergency.setTaskType("EMERGENCY");

        assertNull(generator.generateNextRound(emergency, plan(), cn.hutool.core.date.DateUtil.offsetDay(new Date(), 30)));
        verify(taskMapper, never()).insert(any(ChFollowupTask.class));
    }

    @Test
    @DisplayName("下次随访日期早于今天被拒绝")
    void generateNextRoundRejectsPastDate() {
        ChFollowupTask finished = new ChFollowupTask();
        finished.setTaskRound(1);
        finished.setPlanId(10L);
        finished.setPatientId(100L);

        assertThrows(ServiceException.class,
            () -> generator.generateNextRound(finished, plan(), java.sql.Date.valueOf("2000-01-01")));
        verify(taskMapper, never()).insert(any(ChFollowupTask.class));
    }

    @Test
    @DisplayName("首轮到期日优先取计划项配置，缺失时按规则 first_due_days 外推")
    void resolveFirstDueDate() {
        ChFollowupPlan plan = plan();
        Date createTime = java.sql.Date.valueOf("2026-01-01");
        plan.setCreateTime(createTime);

        ChFollowupPlanItem item = new ChFollowupPlanItem();
        Date configured = java.sql.Date.valueOf("2026-02-01");
        item.setDueDate(configured);
        when(planItemMapper.selectOne(any())).thenReturn(item);
        assertEquals(configured, generator.resolveFirstDueDate(plan));

        when(planItemMapper.selectOne(any())).thenReturn(null);
        Date derived = generator.resolveFirstDueDate(plan);
        assertNotNull(derived);
        // HTN 内置默认 first_due_days = 7
        assertEquals(cn.hutool.core.date.DateUtil.offsetDay(createTime, 7), derived);
    }
}
