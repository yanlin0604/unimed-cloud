package org.dromara.chronic.service.impl;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.dromara.chronic.common.helper.DiseaseNameHelper;
import org.dromara.chronic.domain.bo.ChFollowupAnswerInputBo;
import org.dromara.chronic.domain.bo.ChFollowupSubmitBo;
import org.dromara.chronic.domain.entity.ChFollowupPlan;
import org.dromara.chronic.domain.entity.ChFollowupPlanItem;
import org.dromara.chronic.domain.entity.ChFollowupQuestionnaire;
import org.dromara.chronic.domain.entity.ChFollowupRecord;
import org.dromara.chronic.domain.entity.ChFollowupTask;
import org.dromara.chronic.mapper.ChFollowupAnswerMapper;
import org.dromara.chronic.mapper.ChFollowupPlanItemMapper;
import org.dromara.chronic.mapper.ChFollowupPlanMapper;
import org.dromara.chronic.mapper.ChFollowupQuestionnaireMapper;
import org.dromara.chronic.mapper.ChFollowupRecordMapper;
import org.dromara.chronic.mapper.ChFollowupTaskMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.chronic.support.FollowupOverdueRefresher;
import org.dromara.common.core.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 随访闭环核心校验单元测试
 * <p>
 * 覆盖：任务归属/状态/幂等校验、问卷必填与题目合法性校验、
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
    private DiseaseNameHelper diseaseNameHelper;
    private FollowupOverdueRefresher overdueRefresher;

    private ChFollowupServiceImpl service;

    @BeforeEach
    public void setUp() {
        // 无 Spring 上下文时初始化 MyBatis-Plus lambda 缓存（LambdaUpdateWrapper 依赖）
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ChFollowupTask.class);
        planMapper = mock(ChFollowupPlanMapper.class);
        planItemMapper = mock(ChFollowupPlanItemMapper.class);
        taskMapper = mock(ChFollowupTaskMapper.class);
        recordMapper = mock(ChFollowupRecordMapper.class);
        questionnaireMapper = mock(ChFollowupQuestionnaireMapper.class);
        answerMapper = mock(ChFollowupAnswerMapper.class);
        patientProfileMapper = mock(ChPatientProfileMapper.class);
        diseaseNameHelper = mock(DiseaseNameHelper.class);
        overdueRefresher = mock(FollowupOverdueRefresher.class);
        service = new ChFollowupServiceImpl(planMapper, planItemMapper, taskMapper, recordMapper,
            questionnaireMapper, answerMapper, patientProfileMapper, diseaseNameHelper, overdueRefresher,
            new ObjectMapper());
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
        return bo;
    }

    // ==================== completeTask 校验 ====================

    @Test
    public void completeTaskShouldRejectMissingTask() {
        when(taskMapper.selectOne(any())).thenReturn(null);
        assertThrows(ServiceException.class,
            () -> service.completeTask(1L, submitBo(), null, null, 200L, null));
    }

    @Test
    public void completeTaskShouldRejectOtherPatient() {
        when(taskMapper.selectOne(any())).thenReturn(pendingTask());
        // 期望患者 999 与任务归属患者 100 不一致
        assertThrows(ServiceException.class,
            () -> service.completeTask(1L, submitBo(), 999L, null, 999L, "SELF_FILL"));
    }

    @Test
    public void completeTaskShouldRejectOtherDoctor() {
        when(taskMapper.selectOne(any())).thenReturn(pendingTask());
        // 期望执行医生 999 与任务指派人 200 不一致
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
    public void completeTaskShouldRejectInactiveQuestionnaire() {
        when(taskMapper.selectOne(any())).thenReturn(pendingTask());
        when(recordMapper.selectOne(any())).thenReturn(null);
        ChFollowupPlanItem item = new ChFollowupPlanItem();
        item.setPlanId(10L);
        item.setItemConfig("{\"questionnaireId\": 9001}");
        when(planItemMapper.selectList(any())).thenReturn(List.of(item));
        ChFollowupQuestionnaire questionnaire = new ChFollowupQuestionnaire();
        questionnaire.setQuestionnaireId(9001L);
        questionnaire.setIsActive(false);
        when(questionnaireMapper.selectById(9001L)).thenReturn(questionnaire);
        assertThrows(ServiceException.class,
            () -> service.completeTask(1L, submitBo(), null, null, 200L, null));
    }

    @Test
    public void completeTaskShouldRejectMissingRequiredAnswer() {
        when(taskMapper.selectOne(any())).thenReturn(pendingTask());
        when(recordMapper.selectOne(any())).thenReturn(null);
        ChFollowupPlanItem item = new ChFollowupPlanItem();
        item.setPlanId(10L);
        item.setItemConfig("{\"questionnaireId\": 9001}");
        when(planItemMapper.selectList(any())).thenReturn(List.of(item));
        ChFollowupQuestionnaire questionnaire = new ChFollowupQuestionnaire();
        questionnaire.setQuestionnaireId(9001L);
        questionnaire.setIsActive(true);
        questionnaire.setQuestions("[{\"id\":\"q1\",\"type\":\"number\",\"title\":\"收缩压\",\"required\":true}]");
        when(questionnaireMapper.selectById(9001L)).thenReturn(questionnaire);
        // 未提交任何答案但问卷含必填题
        assertThrows(ServiceException.class,
            () -> service.completeTask(1L, submitBo(), null, null, 200L, null));
    }

    @Test
    public void completeTaskShouldRejectInvalidQuestionId() {
        when(taskMapper.selectOne(any())).thenReturn(pendingTask());
        when(recordMapper.selectOne(any())).thenReturn(null);
        ChFollowupPlanItem item = new ChFollowupPlanItem();
        item.setPlanId(10L);
        item.setItemConfig("{\"questionnaireId\": 9001}");
        when(planItemMapper.selectList(any())).thenReturn(List.of(item));
        ChFollowupQuestionnaire questionnaire = new ChFollowupQuestionnaire();
        questionnaire.setQuestionnaireId(9001L);
        questionnaire.setIsActive(true);
        questionnaire.setQuestions("[{\"id\":\"q1\",\"type\":\"number\",\"title\":\"收缩压\"}]");
        when(questionnaireMapper.selectById(9001L)).thenReturn(questionnaire);

        ChFollowupSubmitBo bo = submitBo();
        bo.setQuestionnaireId(9001L);
        ChFollowupAnswerInputBo answer = new ChFollowupAnswerInputBo();
        answer.setQuestionId("q99");
        answer.setAnswerValue("120");
        bo.setAnswers(List.of(answer));
        assertThrows(ServiceException.class,
            () -> service.completeTask(1L, bo, null, null, 200L, null));
    }

    @Test
    public void completeTaskShouldMarkDoneAndAdvancePlan() {
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

        service.completeTask(1L, submitBo(), 100L, 200L, 300L, "ADMIN_PROXY");

        ArgumentCaptor<ChFollowupRecord> recordCaptor = ArgumentCaptor.forClass(ChFollowupRecord.class);
        verify(recordMapper).insert(recordCaptor.capture());
        assertEquals("ADMIN_PROXY", recordCaptor.getValue().getVisitType());
        assertEquals(100L, recordCaptor.getValue().getPatientId());
        assertEquals(300L, recordCaptor.getValue().getVisitorUserId());
        assertTrue(recordCaptor.getValue().getVisitContent().contains("随访正常"));

        assertEquals("DONE", task.getTaskStatus());
        verify(taskMapper).updateById(task);
        // 最后一轮完成后计划应标记 COMPLETED，轮次推进
        assertEquals("COMPLETED", plan.getPlanStatus());
        assertEquals(1, plan.getCurrentRound());
        verify(planMapper).updateById(plan);
    }

    // ==================== 任务取消 / 指派 / 计划状态 ====================

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
    public void cancelLastTaskShouldCompletePlan() {
        ChFollowupTask task = pendingTask();
        when(taskMapper.selectById(1L)).thenReturn(task);
        ChFollowupPlan plan = new ChFollowupPlan();
        plan.setPlanId(10L);
        plan.setPlanStatus("ACTIVE");
        when(planMapper.selectById(10L)).thenReturn(plan);
        when(taskMapper.selectCount(any())).thenReturn(0L);

        service.cancelTask(1L);

        assertEquals("CANCELLED", task.getTaskStatus());
        assertEquals("COMPLETED", plan.getPlanStatus());
    }

    @Test
    public void assignTaskShouldRejectFinishedTask() {
        ChFollowupTask task = pendingTask();
        task.setTaskStatus("CANCELLED");
        when(taskMapper.selectById(1L)).thenReturn(task);
        assertThrows(ServiceException.class, () -> service.assignTask(1L, 300L));
    }

    @Test
    public void assignTaskShouldUpdateAssignee() {
        ChFollowupTask task = pendingTask();
        when(taskMapper.selectById(1L)).thenReturn(task);
        service.assignTask(1L, 300L);
        assertEquals(300L, task.getAssigneeUserId());
        verify(taskMapper).updateById(task);
    }

    @Test
    public void updatePlanStatusShouldRejectUnsupportedStatus() {
        assertThrows(ServiceException.class, () -> service.updatePlanStatus(10L, "HISTORY"));
    }

    @Test
    public void disablePlanShouldCancelUnfinishedTasks() {
        ChFollowupPlan plan = new ChFollowupPlan();
        plan.setPlanId(10L);
        plan.setPlanStatus("ACTIVE");
        when(planMapper.selectById(10L)).thenReturn(plan);

        service.updatePlanStatus(10L, "DISABLED");

        assertEquals("DISABLED", plan.getPlanStatus());
        verify(planMapper).updateById(plan);
        // 停用级联取消未完成任务
        verify(taskMapper).update(isNull(), any());
    }
}
