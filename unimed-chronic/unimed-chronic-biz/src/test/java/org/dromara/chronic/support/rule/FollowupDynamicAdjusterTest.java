package org.dromara.chronic.support.rule;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.dromara.chronic.domain.bo.ChFollowupSubmitBo;
import org.dromara.chronic.domain.entity.ChFollowupRecord;
import org.dromara.chronic.domain.entity.ChFollowupTask;
import org.dromara.chronic.domain.entity.ChReferralRecord;
import org.dromara.chronic.mapper.*;
import org.dromara.chronic.support.rule.FollowupDynamicAdjuster.AdjustmentAction;
import org.dromara.chronic.support.rule.FollowupDynamicAdjuster.AdjustmentResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 随访结果动态调整状态机单元测试
 *
 * @author unimed
 */
@Tag("chronic-dev")
public class FollowupDynamicAdjusterTest {

    private ChFollowupTaskMapper taskMapper;
    private ChFollowupRecordMapper recordMapper;
    private ChPatientProfileMapper patientProfileMapper;
    private ChPatientTimelineMapper timelineMapper;
    private ChReferralRecordMapper referralMapper;
    private FollowupDynamicAdjuster adjuster;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ChFollowupTask.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ChFollowupRecord.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ChReferralRecord.class);

        taskMapper = mock(ChFollowupTaskMapper.class);
        recordMapper = mock(ChFollowupRecordMapper.class);
        patientProfileMapper = mock(ChPatientProfileMapper.class);
        timelineMapper = mock(ChPatientTimelineMapper.class);
        referralMapper = mock(ChReferralRecordMapper.class);

        adjuster = new FollowupDynamicAdjuster(taskMapper, recordMapper, patientProfileMapper, timelineMapper, referralMapper);
    }

    @Test
    @DisplayName("控制满意：维持既定计划")
    void testMaintainOnControlled() {
        ChFollowupTask task = new ChFollowupTask();
        task.setTaskId(1L);
        task.setPatientId(100L);
        task.setPlanId(10L);
        task.setTaskRound(1);

        ChFollowupRecord record = new ChFollowupRecord();
        record.setFollowupResult("CONTROLLED");

        ChFollowupSubmitBo bo = new ChFollowupSubmitBo();
        bo.setFollowupResult("CONTROLLED");

        AdjustmentResult result = adjuster.evaluateAndAdjust(task, record, bo);
        assertEquals(AdjustmentAction.MAINTAIN, result.action());
        verify(taskMapper, never()).insert(any(ChFollowupTask.class));
        verify(referralMapper, never()).insert(any(ChReferralRecord.class));
    }

    @Test
    @DisplayName("首次控制不满意：自动插入14天动态核查随访任务")
    void testFirstUncontrolledInsertsDynamicTask() {
        ChFollowupTask task = new ChFollowupTask();
        task.setTaskId(1L);
        task.setPatientId(100L);
        task.setPlanId(10L);
        task.setTaskRound(1);
        task.setAssigneeUserId(200L);
        task.setVisitType("OFFLINE");

        ChFollowupRecord record = new ChFollowupRecord();
        record.setFollowupResult("UNCONTROLLED");

        ChFollowupSubmitBo bo = new ChFollowupSubmitBo();
        bo.setFollowupResult("UNCONTROLLED");
        bo.setUnsatisfiedReason("血压未达标");

        // 上次无记录（即首次）
        when(recordMapper.selectOne(any())).thenReturn(null);

        AdjustmentResult result = adjuster.evaluateAndAdjust(task, record, bo);
        assertEquals(AdjustmentAction.DYNAMIC_CHECK_14D, result.action());

        ArgumentCaptor<ChFollowupTask> captor = ArgumentCaptor.forClass(ChFollowupTask.class);
        verify(taskMapper, times(1)).insert(captor.capture());
        ChFollowupTask dynamicTask = captor.getValue();
        assertEquals("DYNAMIC", dynamicTask.getTaskType());
        assertEquals("PENDING", dynamicTask.getTaskStatus());
        assertEquals(100L, dynamicTask.getPatientId());
        assertNotNull(dynamicTask.getPlanDueDate());
        // 随访方式继承当前任务, isFaceToFace 由 visitType 推导, 不再硬编码 PHONE
        assertEquals("OFFLINE", dynamicTask.getVisitType());
        assertTrue(dynamicTask.getIsFaceToFace());
        // 计划外临时任务不占用计划轮次, 避免与计划内同轮 NORMAL 任务撞键
        assertNull(dynamicTask.getTaskRound());
    }

    @Test
    @DisplayName("随访方式缺省时动态核查任务回落电话，且不标记面对面")
    void testDynamicTaskFallsBackToPhone() {
        ChFollowupTask task = new ChFollowupTask();
        task.setTaskId(1L);
        task.setPatientId(100L);
        task.setPlanId(10L);
        task.setAssigneeUserId(200L);

        ChFollowupRecord record = new ChFollowupRecord();
        record.setFollowupResult("DETERIORATING");

        ChFollowupSubmitBo bo = new ChFollowupSubmitBo();
        bo.setFollowupResult("DETERIORATING");

        when(recordMapper.selectOne(any())).thenReturn(null);

        adjuster.evaluateAndAdjust(task, record, bo);

        ArgumentCaptor<ChFollowupTask> captor = ArgumentCaptor.forClass(ChFollowupTask.class);
        verify(taskMapper, times(1)).insert(captor.capture());
        assertEquals("PHONE", captor.getValue().getVisitType());
        assertFalse(captor.getValue().getIsFaceToFace());
    }

    @Test
    @DisplayName("康复评级 POOR 误传进随访结论时不再误判为控制不满意")
    void testRehabLevelPoorDoesNotTriggerAdjustment() {
        ChFollowupTask task = new ChFollowupTask();
        task.setTaskId(1L);
        task.setPatientId(100L);
        task.setPlanId(10L);
        task.setTaskRound(1);

        // POOR 属于 chronic_rehab_level, 不是 chronic_followup_result 的合法取值
        ChFollowupRecord record = new ChFollowupRecord();
        record.setFollowupResult("POOR");

        ChFollowupSubmitBo bo = new ChFollowupSubmitBo();
        bo.setFollowupResult("POOR");

        AdjustmentResult result = adjuster.evaluateAndAdjust(task, record, bo);
        assertEquals(AdjustmentAction.MAINTAIN, result.action());
        verify(taskMapper, never()).insert(any(ChFollowupTask.class));
        verify(referralMapper, never()).insert(any(ChReferralRecord.class));
    }

    @Test
    @DisplayName("连续两次控制不满意：自动生成转诊意向单并生成14天转诊追踪随访任务")
    void testContinuousUncontrolledTriggersReferral() {
        ChFollowupTask task = new ChFollowupTask();
        task.setTaskId(2L);
        task.setPatientId(100L);
        task.setPlanId(10L);
        task.setTaskRound(2);
        task.setAssigneeUserId(200L);

        ChFollowupRecord record = new ChFollowupRecord();
        record.setFollowupResult("UNCONTROLLED");

        ChFollowupSubmitBo bo = new ChFollowupSubmitBo();
        bo.setFollowupResult("UNCONTROLLED");

        // 模拟上一轮随访也是 UNCONTROLLED
        ChFollowupRecord previousRecord = new ChFollowupRecord();
        previousRecord.setRecordId(101L);
        previousRecord.setPatientId(100L);
        previousRecord.setFollowupResult("UNCONTROLLED");
        previousRecord.setVisitDate(new Date(System.currentTimeMillis() - 30L * 24 * 3600 * 1000));
        when(recordMapper.selectOne(any())).thenReturn(previousRecord);

        AdjustmentResult result = adjuster.evaluateAndAdjust(task, record, bo);
        assertEquals(AdjustmentAction.REFERRAL_TRACK_14D, result.action());

        // 验证转诊单创建与转诊跟踪任务生成
        verify(referralMapper, times(1)).insert(any(ChReferralRecord.class));
        ArgumentCaptor<ChFollowupTask> captor = ArgumentCaptor.forClass(ChFollowupTask.class);
        verify(taskMapper, times(1)).insert(captor.capture());
        ChFollowupTask trackTask = captor.getValue();
        assertEquals("REFERRAL_TRACK", trackTask.getTaskType());
        assertNull(trackTask.getTaskRound());
    }
}
