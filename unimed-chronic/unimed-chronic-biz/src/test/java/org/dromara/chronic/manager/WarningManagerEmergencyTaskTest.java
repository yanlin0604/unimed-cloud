package org.dromara.chronic.manager;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.dromara.chronic.domain.bo.ChWarningEventBo;
import org.dromara.chronic.domain.entity.ChFollowupPlan;
import org.dromara.chronic.domain.entity.ChFollowupTask;
import org.dromara.chronic.domain.entity.ChHealthMetricRecord;
import org.dromara.chronic.domain.entity.ChManagePlan;
import org.dromara.chronic.domain.entity.ChPatientDisease;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.domain.entity.ChWarningRule;
import org.dromara.chronic.mapper.*;
import org.dromara.chronic.service.IChWarningEventService;
import org.dromara.chronic.support.rule.WarningRuleEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * 预警联动生成紧急干预随访任务的范围与幂等测试
 *
 * @author unimed
 */
@Tag("chronic-dev")
public class WarningManagerEmergencyTaskTest {

    private WarningRuleEngine ruleEngine;
    private IChWarningEventService warningEventService;
    private ChWarningRuleMapper warningRuleMapper;
    private ChPatientProfileMapper patientProfileMapper;
    private ChPatientDiseaseMapper patientDiseaseMapper;
    private ChManagePlanMapper managePlanMapper;
    private ChFollowupTaskMapper followupTaskMapper;
    private ChFollowupPlanMapper followupPlanMapper;
    private WarningManager warningManager;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ChPatientDisease.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ChManagePlan.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ChWarningRule.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ChFollowupTask.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ChFollowupPlan.class);

        ruleEngine = mock(WarningRuleEngine.class);
        warningEventService = mock(IChWarningEventService.class);
        warningRuleMapper = mock(ChWarningRuleMapper.class);
        patientProfileMapper = mock(ChPatientProfileMapper.class);
        patientDiseaseMapper = mock(ChPatientDiseaseMapper.class);
        managePlanMapper = mock(ChManagePlanMapper.class);
        followupTaskMapper = mock(ChFollowupTaskMapper.class);
        followupPlanMapper = mock(ChFollowupPlanMapper.class);

        warningManager = new WarningManager(
            ruleEngine,
            warningEventService,
            warningRuleMapper,
            mock(ChHealthMetricRecordMapper.class),
            patientProfileMapper,
            patientDiseaseMapper,
            managePlanMapper,
            mock(ChSosRecordMapper.class),
            followupTaskMapper,
            followupPlanMapper);

        // 患者：机构 1001，已确诊高血压
        ChPatientProfile profile = new ChPatientProfile();
        profile.setPatientId(100L);
        profile.setOrgId(1001L);
        profile.setDoctorUserId(2001L);
        when(patientProfileMapper.selectById(anyLong())).thenReturn(profile);

        ChPatientDisease disease = new ChPatientDisease();
        disease.setPatientId(100L);
        disease.setDiseaseCode("HTN");
        when(patientDiseaseMapper.selectList(any())).thenReturn(List.of(disease));
        when(managePlanMapper.selectList(any())).thenReturn(Collections.emptyList());

        // 规则：舒张压 HIGH 级预警
        ChWarningRule rule = new ChWarningRule();
        rule.setRuleId(29L);
        rule.setRuleName("舒张压高危预警");
        rule.setDiseaseCode("HTN");
        rule.setMetricType("BP_DIASTOLIC");
        rule.setWarningLevel("HIGH");
        when(warningRuleMapper.selectList(any())).thenReturn(List.of(rule));

        when(ruleEngine.evaluate(any(), any())).thenReturn(true);
        when(warningEventService.createEvent(any(ChWarningEventBo.class))).thenReturn(9001L);
        when(followupPlanMapper.selectOne(any())).thenReturn(null);
    }

    private ChHealthMetricRecord record(String measureScene) {
        ChHealthMetricRecord record = new ChHealthMetricRecord();
        record.setPatientId(100L);
        record.setMetricType("BP_DIASTOLIC");
        record.setMetricValue("123");
        record.setMeasureScene(measureScene);
        return record;
    }

    @Test
    @DisplayName("随访现场体征：仍记录预警事件，但不再派发紧急电话干预任务")
    void testFollowupSceneSkipsEmergencyTask() {
        when(followupTaskMapper.exists(any())).thenReturn(false);

        warningManager.checkAndTrigger(record("FOLLOWUP"));

        // 预警事件仍应落库（保留预警看板与病历价值）
        verify(warningEventService, times(1)).createEvent(any(ChWarningEventBo.class));
        // 医生刚当面处置过，不应再给同一个医生派电话干预任务
        verify(followupTaskMapper, never()).insert(any(ChFollowupTask.class));
    }

    @Test
    @DisplayName("患者自测体征：仍派发紧急干预任务，且不占用计划轮次")
    void testSelfTestSceneCreatesEmergencyTask() {
        when(followupTaskMapper.exists(any())).thenReturn(false);

        warningManager.checkAndTrigger(record("SELF_TEST"));

        ArgumentCaptor<ChFollowupTask> captor = ArgumentCaptor.forClass(ChFollowupTask.class);
        verify(followupTaskMapper, times(1)).insert(captor.capture());
        ChFollowupTask task = captor.getValue();
        assertEquals("EMERGENCY", task.getTaskType());
        assertEquals("PHONE", task.getVisitType());
        assertEquals("PENDING", task.getTaskStatus());
        assertEquals(2001L, task.getAssigneeUserId());
        assertNull(task.getTaskRound(), "紧急任务不属于计划轮次，taskRound 应为空以免与 round1 撞键");
    }

    @Test
    @DisplayName("已存在未完结紧急任务：不重复派发，避免待办雪崩")
    void testDoesNotDuplicateOpenEmergencyTask() {
        when(followupTaskMapper.exists(any())).thenReturn(true);

        warningManager.checkAndTrigger(record("DEVICE"));

        verify(warningEventService, times(1)).createEvent(any(ChWarningEventBo.class));
        verify(followupTaskMapper, never()).insert(any(ChFollowupTask.class));
    }
}
