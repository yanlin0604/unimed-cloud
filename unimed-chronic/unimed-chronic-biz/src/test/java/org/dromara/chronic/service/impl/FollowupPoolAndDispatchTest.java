package org.dromara.chronic.service.impl;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.dromara.chronic.domain.entity.ChFollowupPlan;
import org.dromara.chronic.domain.entity.ChFollowupTask;
import org.dromara.chronic.manager.FollowupAutoDispatchManager;
import org.dromara.chronic.mapper.ChFollowupPlanMapper;
import org.dromara.chronic.mapper.ChFollowupTaskMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 随访任务池与自动分发策略单元测试
 *
 * @author unimed
 */
@Tag("chronic-dev")
public class FollowupPoolAndDispatchTest {

    private ChFollowupTaskMapper taskMapper;
    private ChFollowupPlanMapper planMapper;
    private FollowupAutoDispatchManager dispatchManager;

    @BeforeEach
    public void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ChFollowupTask.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ChFollowupPlan.class);
        taskMapper = mock(ChFollowupTaskMapper.class);
        planMapper = mock(ChFollowupPlanMapper.class);
        dispatchManager = new FollowupAutoDispatchManager(taskMapper, planMapper);
    }

    @Test
    public void testAutoDispatchEmptyPool() {
        when(taskMapper.selectList(any())).thenReturn(List.of());
        int dispatched = dispatchManager.autoDispatch("LEAST_LOADED", 100);
        assertEquals(0, dispatched);
    }

    @Test
    public void testAutoDispatchRoundRobin() {
        List<ChFollowupTask> poolTasks = new ArrayList<>();
        for (long i = 1; i <= 3; i++) {
            ChFollowupTask t = new ChFollowupTask();
            t.setTaskId(i);
            t.setTaskStatus("PENDING");
            poolTasks.add(t);
        }

        // 模拟池中有3个任务
        when(taskMapper.selectList(any())).thenReturn(poolTasks);

        int dispatched = dispatchManager.autoDispatch("ROUND_ROBIN", 100);
        assertEquals(3, dispatched);
        for (ChFollowupTask t : poolTasks) {
            assertNotNull(t.getAssigneeUserId());
        }
        verify(taskMapper, times(3)).updateById(any(ChFollowupTask.class));
    }
}
