package org.dromara.chronic.job;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.common.log.SnailJobLog;
import com.aizuda.snailjob.model.dto.ExecuteResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.manager.FollowupAutoDispatchManager;
import org.springframework.stereotype.Component;

/**
 * 随访任务池自动分发定时任务
 * <p>
 * 周期性扫描随访任务池中的待分配任务，按配置策略（负载均衡/轮询/随机等）自动分发给执行人员池
 *
 * @author unimed
 */
@Slf4j
@Component
@JobExecutor(name = "followupAutoDispatchJob")
@RequiredArgsConstructor
public class FollowupAutoDispatchJob {

    private final FollowupAutoDispatchManager autoDispatchManager;

    public ExecuteResult jobExecute(JobArgs jobArgs) {
        SnailJobLog.LOCAL.info("随访任务自动分发跑批开始");

        // 默认使用最少待办负载均衡策略，单次最大分发 200 条
        String strategy = "LEAST_LOADED";
        int maxCount = 200;

        int dispatched = autoDispatchManager.autoDispatch(strategy, maxCount);

        SnailJobLog.REMOTE.info("随访任务自动分发跑批完成, 策略: {}, 分发任务数: {}", strategy, dispatched);
        return ExecuteResult.success("自动分发随访任务" + dispatched + "条");
    }
}
