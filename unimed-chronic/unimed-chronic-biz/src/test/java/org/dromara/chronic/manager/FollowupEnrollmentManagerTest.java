package org.dromara.chronic.manager;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.dromara.chronic.domain.entity.*;
import org.dromara.chronic.mapper.*;
import org.dromara.chronic.support.rule.FollowupRoundTaskGenerator;
import org.dromara.chronic.support.rule.FollowupRuleEngine;
import org.dromara.chronic.support.rule.MultiDiseaseFollowupMerger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 随访自动入组编排层单元测试
 *
 * @author unimed
 */
@Tag("chronic-dev")
public class FollowupEnrollmentManagerTest {

    private ChFollowupPlanMapper planMapper;
    private ChFollowupPlanItemMapper planItemMapper;
    private ChFollowupTaskMapper taskMapper;
    private ChPatientDiseaseMapper diseaseMapper;
    private ChRiskAssessmentMapper riskMapper;
    private ChPatientProfileMapper profileMapper;
    private ChPatientTimelineMapper timelineMapper;
    private MultiDiseaseFollowupMerger multiDiseaseMerger;
    private FollowupRoundTaskGenerator roundTaskGenerator;

    private FollowupEnrollmentManager enrollmentManager;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ChFollowupTask.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ChFollowupPlan.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ChFollowupPlanItem.class);

        planMapper = mock(ChFollowupPlanMapper.class);
        planItemMapper = mock(ChFollowupPlanItemMapper.class);
        taskMapper = mock(ChFollowupTaskMapper.class);
        diseaseMapper = mock(ChPatientDiseaseMapper.class);
        riskMapper = mock(ChRiskAssessmentMapper.class);
        profileMapper = mock(ChPatientProfileMapper.class);
        timelineMapper = mock(ChPatientTimelineMapper.class);

        ChFollowupQuestionnaireMapper questionnaireMapper = mock(ChFollowupQuestionnaireMapper.class);
        ChFollowupRuleMapper followupRuleMapper = mock(ChFollowupRuleMapper.class);
        when(followupRuleMapper.selectList(any())).thenReturn(Collections.emptyList());
        FollowupRuleEngine ruleEngine = new FollowupRuleEngine(questionnaireMapper, followupRuleMapper);
        multiDiseaseMerger = new MultiDiseaseFollowupMerger(ruleEngine, new ObjectMapper());
        roundTaskGenerator = new FollowupRoundTaskGenerator(taskMapper, planItemMapper, ruleEngine);

        // 模拟 MyBatis-Plus 插入时回填雪花主键, 否则生成器会因 planId 为空拒绝写任务
        doAnswer(inv -> {
            inv.<ChFollowupPlan>getArgument(0).setPlanId(1L);
            return 1;
        }).when(planMapper).insert(any(ChFollowupPlan.class));

        enrollmentManager = new FollowupEnrollmentManager(
            planMapper, planItemMapper, roundTaskGenerator, diseaseMapper, riskMapper, profileMapper,
            timelineMapper, multiDiseaseMerger, new ObjectMapper()
        );
    }

    @Test
    @DisplayName("确诊慢病自动入组：生成高血压随访计划与首轮任务（后续逐轮由医生决定）")
    void testAutoEnrollmentGeneratesPlanAndTasks() {
        Long patientId = 100L;
        String diseaseCode = "HTN";
        Long doctorUserId = 200L;

        // 模拟无现有计划
        when(planMapper.selectOne(any())).thenReturn(null);
        when(diseaseMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(riskMapper.selectOne(any())).thenReturn(null);

        Long planId = enrollmentManager.autoEnrollAndGeneratePlan(patientId, diseaseCode, doctorUserId);

        // 验证插入随访计划
        ArgumentCaptor<ChFollowupPlan> planCaptor = ArgumentCaptor.forClass(ChFollowupPlan.class);
        verify(planMapper, times(1)).insert(planCaptor.capture());
        ChFollowupPlan plan = planCaptor.getValue();
        assertEquals("HTN", plan.getDiseaseCode());
        assertEquals("ACTIVE", plan.getPlanStatus());
        assertEquals(90, plan.getCycleDays());
        assertEquals(1, plan.getTotalRounds());

        // 验证仅生成首轮任务, 不再预生成全年度轮次
        ArgumentCaptor<ChFollowupTask> taskCaptor = ArgumentCaptor.forClass(ChFollowupTask.class);
        verify(taskMapper, times(1)).insert(taskCaptor.capture());
        List<ChFollowupTask> tasks = taskCaptor.getAllValues();
        assertEquals(1, tasks.size());
        assertEquals(1, tasks.get(0).getTaskRound());
        assertEquals("NORMAL", tasks.get(0).getTaskType());
        assertEquals("PENDING", tasks.get(0).getTaskStatus());
        // 面对面机制已废弃, isFaceToFace 仅由 visitType 推导; HTN 默认 PHONE 故为 false
        assertEquals("PHONE", tasks.get(0).getVisitType());
        assertFalse(tasks.get(0).getIsFaceToFace());

        // 验证写入时间线
        verify(timelineMapper, times(1)).insert(any(ChPatientTimeline.class));
    }

    @Test
    @DisplayName("幂等性测试：患者已有生效中计划直接复用返回")
    void testAutoEnrollmentIdempotent() {
        Long patientId = 100L;
        String diseaseCode = "HTN";

        ChFollowupPlan existing = new ChFollowupPlan();
        existing.setPlanId(999L);
        existing.setPatientId(patientId);
        existing.setDiseaseCode(diseaseCode);
        existing.setPlanStatus("ACTIVE");
        when(planMapper.selectOne(any())).thenReturn(existing);

        Long planId = enrollmentManager.autoEnrollAndGeneratePlan(patientId, diseaseCode, 200L);
        assertEquals(999L, planId);

        verify(planMapper, never()).insert(any(ChFollowupPlan.class));
        verify(taskMapper, never()).insert(any(ChFollowupTask.class));
    }
}
