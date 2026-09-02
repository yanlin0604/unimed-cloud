package org.dromara.chronic.job;

import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.dromara.chronic.domain.entity.ChFollowupPlan;
import org.dromara.chronic.domain.entity.ChFollowupTask;
import org.dromara.chronic.mapper.ChFollowupPlanMapper;
import org.dromara.chronic.mapper.ChFollowupTaskMapper;
import org.dromara.chronic.support.rule.FollowupRoundTaskGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 随访首轮任务补偿 Job 测试
 * <p>
 * 逐轮模型下 Job 只补首轮，不得再外推未来轮次。
 *
 * @author unimed
 */
@Tag("chronic-dev")
class FollowupTaskGenJobTest {

    private ChFollowupPlanMapper planMapper;
    private ChFollowupTaskMapper taskMapper;
    private FollowupRoundTaskGenerator roundTaskGenerator;
    private FollowupTaskGenJob job;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ChFollowupTask.class);
        planMapper = mock(ChFollowupPlanMapper.class);
        taskMapper = mock(ChFollowupTaskMapper.class);
        roundTaskGenerator = mock(FollowupRoundTaskGenerator.class);
        when(roundTaskGenerator.resolveFirstDueDate(any())).thenReturn(new Date());
        job = new FollowupTaskGenJob(planMapper, taskMapper, roundTaskGenerator);
    }

    private ChFollowupPlan activePlan() {
        ChFollowupPlan plan = new ChFollowupPlan();
        plan.setPlanId(10L);
        plan.setPatientId(100L);
        plan.setDiseaseCode("HTN");
        plan.setManagementLevel("LOW");
        plan.setCycleDays(90);
        plan.setTotalRounds(4);
        plan.setCurrentRound(0);
        plan.setPlanStatus("ACTIVE");
        plan.setCreateTime(new Date());
        return plan;
    }

    private void run() {
        assertNotNull(job.jobExecute(mock(JobArgs.class)));
    }

    @Test
    @DisplayName("计划完全没有轮次任务时补齐首轮")
    void shouldBackfillFirstRoundWhenNoTask() {
        ChFollowupPlan plan = activePlan();
        when(planMapper.selectList(any())).thenReturn(List.of(plan));
        when(taskMapper.selectCount(any())).thenReturn(0L);

        run();

        verify(roundTaskGenerator, times(1)).ensureRound(eq(plan), eq(1), any(Date.class), isNull());
    }

    @Test
    @DisplayName("已有轮次任务的计划不再外推未来轮次")
    void shouldNotGenerateFutureRounds() {
        when(planMapper.selectList(any())).thenReturn(List.of(activePlan()));
        // 首轮已完成，totalRounds=4 —— 旧逻辑会在此处补出 2/3/4 轮
        when(taskMapper.selectCount(any())).thenReturn(1L);

        run();

        verify(roundTaskGenerator, never()).ensureRound(any(), anyInt(), any(), any());
    }

    @Test
    @DisplayName("仅有计划外临时任务的计划仍视为缺首轮")
    void shouldTreatOutOfPlanTaskAsMissingRound() {
        when(planMapper.selectList(any())).thenReturn(List.of(activePlan()));
        when(taskMapper.selectCount(any())).thenReturn(0L);

        run();

        verify(roundTaskGenerator, times(1)).ensureRound(any(), eq(1), any(Date.class), isNull());
    }

    @Test
    @DisplayName("无生效计划时不做任何生成")
    void shouldSkipWhenNoActivePlans() {
        when(planMapper.selectList(any())).thenReturn(Collections.emptyList());

        run();

        verify(roundTaskGenerator, never()).ensureRound(any(), anyInt(), any(), any());
    }
}
