package org.dromara.chronic.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.linpeilie.Converter;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.dromara.chronic.common.helper.DiseaseNameHelper;
import org.dromara.chronic.domain.bo.ChFollowupAnswerInputBo;
import org.dromara.chronic.domain.bo.ChFollowupPlanBo;
import org.dromara.chronic.domain.bo.ChFollowupPlanItemBo;
import org.dromara.chronic.domain.bo.ChFollowupSubmitBo;
import org.dromara.chronic.domain.entity.ChFollowupPlan;
import org.dromara.chronic.domain.entity.ChFollowupPlanItem;
import org.dromara.chronic.domain.entity.ChFollowupQuestionnaire;
import org.dromara.chronic.domain.entity.ChFollowupRecord;
import org.dromara.chronic.domain.entity.ChFollowupTask;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.domain.entity.ChPatientTimeline;
import org.dromara.chronic.domain.vo.*;
import org.dromara.chronic.manager.HealthMetricManager;
import org.dromara.chronic.mapper.*;
import org.dromara.chronic.service.IChNotificationTemplateService;
import org.dromara.chronic.support.FollowupOverdueRefresher;
import org.dromara.common.core.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 随访闭环核心校验与功能调整单元测试
 * <p>
 * 覆盖：执行人全量术语支持、随访任务池与认领/批量指派/退回、健康体征自动入库、
 * 任务归属/状态/幂等校验、问卷必填与题目合法性校验、
 * 任务完成后计划进度推进、任务取消与计划状态流转
 *
 * @author unimed
 */
@Tag("chronic-dev")
public class ChFollowupServiceImplTest {

    private ChFollowupPlanMapper planMapper;
    private ChFollowupPlanItemMapper planItemMapper;
    private ChFollowupTaskMapper taskMapper;
    private ChFollowupRecordMapper recordMapper;
    private ChFollowupQuestionnaireMapper questionnaireMapper;
    private ChFollowupAnswerMapper answerMapper;
    private ChPatientProfileMapper patientProfileMapper;
    private ChPatientTimelineMapper patientTimelineMapper;
    private ChHealthMetricRecordMapper healthMetricRecordMapper;
    private ChMedicationRecordMapper medicationRecordMapper;
    private IChNotificationTemplateService notificationTemplateService;
    private HealthMetricManager healthMetricManager;
    private DiseaseNameHelper diseaseNameHelper;
    private FollowupOverdueRefresher overdueRefresher;
    private org.dromara.chronic.service.IChMessageSessionService messageSessionService;

    private ChFollowupServiceImpl service;

    @BeforeAll
    public static void setUpMapstructConverter() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerSingleton("converter", new Converter());
        new SpringUtil().postProcessBeanFactory(beanFactory);
    }

    @BeforeEach
    public void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ChFollowupTask.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ChFollowupPlan.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ChFollowupPlanItem.class);
        planMapper = mock(ChFollowupPlanMapper.class);
        planItemMapper = mock(ChFollowupPlanItemMapper.class);
        taskMapper = mock(ChFollowupTaskMapper.class);
        recordMapper = mock(ChFollowupRecordMapper.class);
        questionnaireMapper = mock(ChFollowupQuestionnaireMapper.class);
        answerMapper = mock(ChFollowupAnswerMapper.class);
        patientProfileMapper = mock(ChPatientProfileMapper.class);
        patientTimelineMapper = mock(ChPatientTimelineMapper.class);
        healthMetricRecordMapper = mock(ChHealthMetricRecordMapper.class);
        medicationRecordMapper = mock(ChMedicationRecordMapper.class);
        notificationTemplateService = mock(IChNotificationTemplateService.class);
        healthMetricManager = mock(HealthMetricManager.class);
        diseaseNameHelper = mock(DiseaseNameHelper.class);
        overdueRefresher = mock(FollowupOverdueRefresher.class);
        messageSessionService = mock(org.dromara.chronic.service.IChMessageSessionService.class);
        org.dromara.chronic.support.rule.FollowupDynamicAdjuster dynamicAdjuster = mock(org.dromara.chronic.support.rule.FollowupDynamicAdjuster.class);
        service = new ChFollowupServiceImpl(planMapper, planItemMapper, taskMapper, recordMapper,
            questionnaireMapper, answerMapper, patientProfileMapper, patientTimelineMapper,
            healthMetricRecordMapper, medicationRecordMapper, notificationTemplateService,
            healthMetricManager, diseaseNameHelper, overdueRefresher, messageSessionService, dynamicAdjuster, new ObjectMapper());
    }

    private ChFollowupTask pendingTask() {
        ChFollowupTask task = new ChFollowupTask();
        task.setTaskId(1L);
        task.setPlanId(10L);
        task.setPatientId(100L);
        task.setTaskRound(1);
        task.setTaskStatus("PENDING");
        task.setVisitType("PHONE");
        task.setAssigneeUserId(200L);
        return task;
    }

    private ChFollowupSubmitBo submitBo() {
        ChFollowupSubmitBo bo = new ChFollowupSubmitBo();
        bo.setVisitContent("随访正常");
        bo.setFollowupResult("CONTROLLED");
        bo.setRehabLevel("GOOD");
        bo.setFeedbackAdvice("继续保持清淡饮食，按时服药");
        bo.setVitalSigns(Map.of(
            "systolicBp", 125,
            "diastolicBp", 82,
            "fastingGlucose", 5.8,
            "heartRate", 72
        ));
        return bo;
    }

    private ChFollowupPlanBo followupPlanBo(Long planId, int totalRounds) {
        ChFollowupPlanBo bo = new ChFollowupPlanBo();
        bo.setPlanId(planId);
        bo.setPatientId(101L);
        bo.setDiseaseCode("HTN");
        bo.setAssigneeUserId(301L);
        bo.setCycleDays(30);
        bo.setTotalRounds(totalRounds);
        bo.setPlanStatus("ACTIVE");
        ChFollowupPlanItemBo item = new ChFollowupPlanItemBo();
        item.setItemType("FOLLOWUP");
        item.setVisitType("PHONE");
        item.setDueDate(new Date());
        item.setItemConfig("{}");
        bo.setItemList(List.of(item));
        return bo;
    }

    // ==================== completeTask 校验与健康数据自动沉淀 ====================

    @Test
    public void completeTaskShouldRejectMissingTask() {
        when(taskMapper.selectOne(any())).thenReturn(null);
        assertThrows(ServiceException.class,
            () -> service.completeTask(1L, submitBo(), null, null, 200L, null));
    }

    @Test
    public void completeTaskShouldRejectOtherPatient() {
        when(taskMapper.selectOne(any())).thenReturn(pendingTask());
        assertThrows(ServiceException.class,
            () -> service.completeTask(1L, submitBo(), 999L, null, 999L, "ONLINE"));
    }

    @Test
    public void completeTaskShouldRejectOtherExecutor() {
        when(taskMapper.selectOne(any())).thenReturn(pendingTask());
        assertThrows(ServiceException.class,
            () -> service.completeTask(1L, submitBo(), null, 999L, 999L, null));
    }

    @Test
    public void completeTaskShouldRejectFinishedTask() {
        ChFollowupTask task = pendingTask();
        task.setTaskStatus("DONE");
        when(taskMapper.selectOne(any())).thenReturn(task);
        assertThrows(ServiceException.class,
            () -> service.completeTask(1L, submitBo(), null, null, 200L, null));
    }

    @Test
    public void completeTaskShouldRejectDuplicateRecord() {
        when(taskMapper.selectOne(any())).thenReturn(pendingTask());
        when(recordMapper.selectOne(any())).thenReturn(new ChFollowupRecord());
        assertThrows(ServiceException.class,
            () -> service.completeTask(1L, submitBo(), null, null, 200L, null));
    }

    @Test
    public void completeTaskShouldMarkDoneAndPersistMetricsAndAdvancePlan() {
        ChFollowupTask task = pendingTask();
        when(taskMapper.selectOne(any())).thenReturn(task);
        when(recordMapper.selectOne(any())).thenReturn(null);
        when(planItemMapper.selectList(any())).thenReturn(Collections.emptyList());
        ChFollowupPlan plan = new ChFollowupPlan();
        plan.setPlanId(10L);
        plan.setPlanStatus("ACTIVE");
        plan.setCurrentRound(0);
        when(planMapper.selectById(10L)).thenReturn(plan);
        when(taskMapper.selectCount(any())).thenReturn(0L);

        ChFollowupSubmitBo bo = submitBo();
        service.completeTask(1L, bo, 100L, 200L, 300L, "ONLINE");

        ArgumentCaptor<ChFollowupRecord> recordCaptor = ArgumentCaptor.forClass(ChFollowupRecord.class);
        verify(recordMapper).insert(recordCaptor.capture());
        assertEquals("ONLINE", recordCaptor.getValue().getVisitType());
        assertEquals(100L, recordCaptor.getValue().getPatientId());
        assertEquals(300L, recordCaptor.getValue().getVisitorUserId());
        assertEquals("CONTROLLED", recordCaptor.getValue().getFollowupResult());
        assertEquals("GOOD", recordCaptor.getValue().getRehabLevel());

        // 验证健康指标自动入库调用
        verify(healthMetricManager, times(1)).reportAndCheckBatch(any());
        // 验证时间线插入调用
        verify(patientTimelineMapper, times(1)).insert(any(ChPatientTimeline.class));

        assertEquals("DONE", task.getTaskStatus());
        verify(taskMapper).updateById(task);
        assertEquals("COMPLETED", plan.getPlanStatus());
        assertEquals(1, plan.getCurrentRound());
        verify(planMapper).updateById(plan);
    }

    // ==================== 随访任务池与认领/指派/释放 ====================

    @Test
    public void claimTaskShouldSucceedWhenTaskInPool() {
        ChFollowupTask poolTask = pendingTask();
        poolTask.setAssigneeUserId(null);
        when(taskMapper.selectOne(any())).thenReturn(poolTask);

        service.claimTask(1L, 888L);

        assertEquals(888L, poolTask.getAssigneeUserId());
        verify(taskMapper).updateById(poolTask);
    }

    @Test
    public void claimTaskShouldRejectAlreadyClaimedTask() {
        ChFollowupTask claimedTask = pendingTask();
        claimedTask.setAssigneeUserId(555L);
        when(taskMapper.selectOne(any())).thenReturn(claimedTask);

        assertThrows(ServiceException.class, () -> service.claimTask(1L, 888L));
    }

    @Test
    public void batchAssignTasksShouldUpdateAllAssignees() {
        ChFollowupTask task1 = pendingTask();
        task1.setTaskId(1L);
        ChFollowupTask task2 = pendingTask();
        task2.setTaskId(2L);
        when(taskMapper.selectById(1L)).thenReturn(task1);
        when(taskMapper.selectById(2L)).thenReturn(task2);

        service.batchAssignTasks(List.of(1L, 2L), 999L);

        assertEquals(999L, task1.getAssigneeUserId());
        assertEquals(999L, task2.getAssigneeUserId());
        verify(taskMapper, times(2)).updateById(any(ChFollowupTask.class));
    }

    @Test
    public void releaseTaskShouldResetAssigneeToNull() {
        ChFollowupTask task = pendingTask();
        task.setAssigneeUserId(200L);
        when(taskMapper.selectById(1L)).thenReturn(task);

        service.releaseTask(1L, 200L);

        assertNull(task.getAssigneeUserId());
        verify(taskMapper).updateById(task);
    }

    // ==================== 任务取消 / 计划状态 ====================

    @Test
    public void cancelTaskShouldRejectDoneTask() {
        ChFollowupTask task = pendingTask();
        task.setTaskStatus("DONE");
        when(taskMapper.selectById(1L)).thenReturn(task);
        assertThrows(ServiceException.class, () -> service.cancelTask(1L));
    }

    @Test
    public void cancelTaskShouldBeIdempotentForCancelled() {
        ChFollowupTask task = pendingTask();
        task.setTaskStatus("CANCELLED");
        when(taskMapper.selectById(1L)).thenReturn(task);
        service.cancelTask(1L);
        verify(taskMapper, never()).updateById(any(ChFollowupTask.class));
    }

    @Test
    public void createPlanWithoutAssigneeShouldCreatePoolTasks() {
        ChFollowupPlanBo bo = followupPlanBo(null, 2);
        bo.setAssigneeUserId(null); // 执行人为空，放入公共任务池

        service.createPlan(bo);

        ArgumentCaptor<ChFollowupPlan> planCaptor = ArgumentCaptor.forClass(ChFollowupPlan.class);
        verify(planMapper).insert(planCaptor.capture());
        assertNull(planCaptor.getValue().getAssigneeUserId());

        ArgumentCaptor<ChFollowupTask> taskCaptor = ArgumentCaptor.forClass(ChFollowupTask.class);
        verify(taskMapper, times(2)).insert(taskCaptor.capture());
        assertTrue(taskCaptor.getAllValues().stream()
            .allMatch(task -> task.getAssigneeUserId() == null));
    }

    @Test
    public void queryTaskDetailShouldAssemblePrefillData() {
        ChFollowupTask task = pendingTask();
        when(taskMapper.selectById(1L)).thenReturn(task);

        ChFollowupTaskVo taskVo = new ChFollowupTaskVo();
        taskVo.setTaskId(1L);
        taskVo.setPatientId(100L);
        taskVo.setPlanId(10L);
        when(taskMapper.selectVoById(1L)).thenReturn(taskVo);

        // 模拟最新体征数据
        ChHealthMetricRecordVo metric = new ChHealthMetricRecordVo();
        metric.setMetricType("SYSTOLIC_BP");
        metric.setMetricValue("135");
        metric.setCreateTime(new Date());
        when(healthMetricRecordMapper.selectLatestByPatientId(100L)).thenReturn(List.of(metric));

        // 模拟当前在服药物
        ChMedicationRecordVo med = new ChMedicationRecordVo();
        med.setDrugName("氨氯地平片");
        med.setDosage("5mg");
        med.setFrequency("qd");
        when(medicationRecordMapper.selectVoList(any())).thenReturn(List.of(med));

        ChFollowupTaskDetailVo detail = service.queryTaskDetail(1L, null, null);
        assertTrue(detail.getPrefillData() != null);
        assertEquals("135", detail.getPrefillData().getLatestMetrics().get("systolicBp"));
        assertTrue(detail.getPrefillData().getMedicationDescription().contains("氨氯地平片"));
    }

    @Test
    public void sendTaskRemindShouldUpdateStatusToReminding() {
        ChFollowupTask task = pendingTask();
        task.setTaskStatus("PENDING");
        when(taskMapper.selectById(1L)).thenReturn(task);

        ChPatientProfile patient = new ChPatientProfile();
        patient.setPatientId(100L);
        patient.setName("张三");
        patient.setPhone("13800000000");
        when(patientProfileMapper.selectById(100L)).thenReturn(patient);

        service.sendTaskRemind(1L, 200L);

        ArgumentCaptor<ChFollowupTask> taskCaptor = ArgumentCaptor.forClass(ChFollowupTask.class);
        verify(taskMapper).updateById(taskCaptor.capture());
        assertEquals("REMINDING", taskCaptor.getValue().getTaskStatus());
    }

    @Test
    public void sendTaskRemindShouldThrowWhenTaskFinished() {
        ChFollowupTask task = pendingTask();
        task.setTaskStatus("DONE");
        when(taskMapper.selectById(1L)).thenReturn(task);

        assertThrows(ServiceException.class, () -> service.sendTaskRemind(1L, 200L));
    }

    // ==================== 患者自填待医生评估 (submitSelfFill) ====================

    private ChFollowupTask onlineNormalTask() {
        ChFollowupTask task = new ChFollowupTask();
        task.setTaskId(1L);
        task.setPlanId(10L);
        task.setPatientId(100L);
        task.setTaskRound(1);
        task.setTaskStatus("PENDING");
        task.setVisitType("ONLINE");
        task.setTaskType("NORMAL");
        task.setAssigneeUserId(200L);
        return task;
    }

    @Test
    public void submitSelfFillShouldMarkPatientFilledWithoutRecord() {
        ChFollowupTask task = onlineNormalTask();
        when(taskMapper.selectOne(any())).thenReturn(task);

        ChFollowupSubmitBo bo = new ChFollowupSubmitBo();
        bo.setVisitContent("血压偏高");
        bo.setVitalSigns(Map.of("systolicBp", 150, "diastolicBp", 95));

        Long ret = service.submitSelfFill(1L, bo, 100L, 300L, "ONLINE");
        assertEquals(0L, ret);
        assertEquals("PATIENT_FILLED", task.getTaskStatus());
        assertNotNull(task.getPatientFillContent());
        assertNotNull(task.getPatientFillTime());
        // 患者自填不写完成记录、不触发动态调整
        verify(recordMapper, never()).insert(any(ChFollowupRecord.class));
        ArgumentCaptor<ChFollowupTask> captor = ArgumentCaptor.forClass(ChFollowupTask.class);
        verify(taskMapper).updateById(captor.capture());
        assertEquals("PATIENT_FILLED", captor.getValue().getTaskStatus());
    }

    @Test
    public void submitSelfFillShouldRejectDoctorOwnedTask() {
        ChFollowupTask task = onlineNormalTask();
        task.setTaskType("DYNAMIC");
        task.setVisitType("PHONE");
        when(taskMapper.selectOne(any())).thenReturn(task);

        ChFollowupSubmitBo bo = new ChFollowupSubmitBo();
        bo.setVisitContent("x");

        assertThrows(ServiceException.class,
            () -> service.submitSelfFill(1L, bo, 100L, 300L, "ONLINE"));
        verify(taskMapper, never()).updateById(any(ChFollowupTask.class));
    }

    @Test
    public void submitSelfFillShouldRejectFinishedTask() {
        ChFollowupTask task = onlineNormalTask();
        task.setTaskStatus("DONE");
        when(taskMapper.selectOne(any())).thenReturn(task);
        ChFollowupSubmitBo bo = new ChFollowupSubmitBo();
        bo.setVisitContent("x");
        assertThrows(ServiceException.class,
            () -> service.submitSelfFill(1L, bo, 100L, 300L, "ONLINE"));
    }

    @Test
    public void submitSelfFillShouldRejectOtherPatient() {
        ChFollowupTask task = onlineNormalTask();
        when(taskMapper.selectOne(any())).thenReturn(task);
        ChFollowupSubmitBo bo = new ChFollowupSubmitBo();
        bo.setVisitContent("x");
        assertThrows(ServiceException.class,
            () -> service.submitSelfFill(1L, bo, 999L, 300L, "ONLINE"));
    }

    /**
     * 修复验证: 患者可自填所有常规轮次任务(线下门诊前预填/电话回访预填等),
     * 不再以 visitType==ONLINE 为硬性拦截条件, 仅排除医生专属 taskType。
     */
    @Test
    public void submitSelfFillShouldAllowNormalPhoneAndOfflineTask() {
        // PHONE 常规任务可自填(电话回访预填)
        ChFollowupTask phoneTask = onlineNormalTask();
        phoneTask.setTaskId(2L);
        phoneTask.setTaskStatus("PENDING");
        phoneTask.setVisitType("PHONE");
        phoneTask.setTaskType("NORMAL");
        when(taskMapper.selectOne(any())).thenReturn(phoneTask);

        ChFollowupSubmitBo bo = new ChFollowupSubmitBo();
        bo.setVisitContent("电话自填主诉");
        bo.setVitalSigns(Map.of("systolicBp", 130, "diastolicBp", 85));

        Long ret = service.submitSelfFill(2L, bo, 100L, 300L, "PHONE");
        assertEquals(0L, ret);
        assertEquals("PATIENT_FILLED", phoneTask.getTaskStatus());
        assertNotNull(phoneTask.getPatientFillContent());
        assertNotNull(phoneTask.getPatientFillTime());

        // OFFLINE 常规任务可自填(门诊就诊前预填)
        ChFollowupTask offlineTask = onlineNormalTask();
        offlineTask.setTaskId(3L);
        offlineTask.setTaskStatus("PENDING");
        offlineTask.setVisitType("OFFLINE");
        offlineTask.setTaskType("NORMAL");
        when(taskMapper.selectOne(any())).thenReturn(offlineTask);

        ChFollowupSubmitBo bo2 = new ChFollowupSubmitBo();
        bo2.setVisitContent("门诊预填主诉");

        Long ret2 = service.submitSelfFill(3L, bo2, 100L, 300L, "OFFLINE");
        assertEquals(0L, ret2);
        assertEquals("PATIENT_FILLED", offlineTask.getTaskStatus());
    }

    @Test
    public void completeTaskMergesPatientFillWhenPatientFilled() {
        ChFollowupTask task = onlineNormalTask();
        task.setTaskStatus("PATIENT_FILLED");
        task.setPatientFillContent("{\"summary\":\"患者自填小结\",\"vitalSigns\":{\"systolicBp\":150},\"answers\":[{\"questionId\":\"q1\",\"answerValue\":\"是\"}]}");
        when(taskMapper.selectOne(any())).thenReturn(task);
        when(recordMapper.selectOne(any())).thenReturn(null);
        when(planItemMapper.selectList(any())).thenReturn(Collections.emptyList());
        ChFollowupPlan plan = new ChFollowupPlan();
        plan.setPlanId(10L);
        plan.setPlanStatus("ACTIVE");
        plan.setCurrentRound(0);
        when(planMapper.selectById(10L)).thenReturn(plan);
        when(taskMapper.selectCount(any())).thenReturn(0L);

        ChFollowupSubmitBo bo = new ChFollowupSubmitBo();
        bo.setVisitContent("医生评估小结");
        bo.setFollowupResult("UNCONTROLLED");
        bo.setRehabLevel("FAIR");
        bo.setVitalSigns(Map.of("diastolicBp", 96));

        service.completeTask(1L, bo, null, 200L, 200L, null);

        assertEquals("DONE", task.getTaskStatus());
        // 合并后的体征入库
        verify(healthMetricManager, times(1)).reportAndCheckBatch(any());
    }
}
