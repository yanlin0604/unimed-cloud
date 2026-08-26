package org.dromara.chronic.job;

import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import org.dromara.chronic.manager.FollowupAutoDispatchManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 随访任务自动分发 Job 参数解析测试
 *
 * @author unimed
 */
@Tag("chronic-dev")
class FollowupAutoDispatchJobTest {

    private FollowupAutoDispatchManager autoDispatchManager;
    private FollowupAutoDispatchJob job;

    @BeforeEach
    void setUp() {
        autoDispatchManager = mock(FollowupAutoDispatchManager.class);
        when(autoDispatchManager.autoDispatch(org.mockito.ArgumentMatchers.anyString(),
            org.mockito.ArgumentMatchers.anyInt())).thenReturn(0);
        job = new FollowupAutoDispatchJob(autoDispatchManager);
    }

    @Test
    @DisplayName("合法JSON使用配置策略和上限")
    void testValidJsonParams() {
        execute("{\"strategy\":\"DISEASE_MATCH\",\"maxCount\":300}");
        verify(autoDispatchManager).autoDispatch("DISEASE_MATCH", 300);
    }

    @Test
    @DisplayName("非法JSON回退默认策略和上限")
    void testInvalidJsonFallback() {
        execute("{bad-json");
        verify(autoDispatchManager).autoDispatch("LEAST_LOADED", 200);
    }

    @Test
    @DisplayName("空白参数回退默认策略和上限")
    void testBlankParamsFallback() {
        execute("  ");
        verify(autoDispatchManager).autoDispatch("LEAST_LOADED", 200);
    }

    @Test
    @DisplayName("非法策略整体回退默认策略和默认上限")
    void testInvalidStrategyFallback() {
        execute("{\"strategy\":\"UNKNOWN\",\"maxCount\":5000}");
        verify(autoDispatchManager).autoDispatch("LEAST_LOADED", 200);
    }

    @Test
    @DisplayName("合法策略的上限钳制到1000")
    void testMaxCountClamp() {
        execute("{\"strategy\":\"ROUND_ROBIN\",\"maxCount\":5000}");
        verify(autoDispatchManager).autoDispatch("ROUND_ROBIN", 1000);
    }

    @Test
    @DisplayName("小于最小值的上限钳制到1")
    void testMinCountClamp() {
        execute("{\"strategy\":\"RANDOM\",\"maxCount\":0}");
        verify(autoDispatchManager).autoDispatch("RANDOM", 1);
    }

    private void execute(Object params) {
        JobArgs args = new JobArgs();
        args.setJobParams(params);
        job.jobExecute(args);
    }
}
