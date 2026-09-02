package org.dromara.chronic.job;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.common.log.SnailJobLog;
import com.aizuda.snailjob.model.dto.ExecuteResult;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.entity.ChFollowupPlan;
import org.dromara.chronic.domain.entity.ChFollowupTask;
import org.dromara.chronic.mapper.ChFollowupPlanMapper;
import org.dromara.chronic.mapper.ChFollowupTaskMapper;
import org.dromara.chronic.support.rule.FollowupRoundTaskGenerator;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 随访任务补偿生成定时任务
 * <p>
 * 逐轮模型下随访任务由「入组生成首轮 + 医生完成每轮后决定是否继续」驱动，本 Job 只做兜底：
 * 扫描生效计划，若其完全没有计划内轮次任务（首轮写入失败等异常场景），补齐首轮任务；
 * 已有任何轮次任务的计划一律不再外推未来轮次，避免把医生尚未决定的随访提前派出去。
 *
 * @author unimed
 */
@Component
@JobExecutor(name = "followupTaskGenJob")
@RequiredArgsConstructor
public class FollowupTaskGenJob {

    /** 计划外临时任务类型，不参与"计划是否已有轮次任务"的判定 */
    private static final List<String> OUT_OF_PLAN_TASK_TYPES = List.of("EMERGENCY", "DYNAMIC", "REFERRAL_TRACK");

    private final ChFollowupPlanMapper followupPlanMapper;
    private final ChFollowupTaskMapper followupTaskMapper;
    private final FollowupRoundTaskGenerator roundTaskGenerator;

    public ExecuteResult jobExecute(JobArgs jobArgs) {
        SnailJobLog.LOCAL.info("随访首轮任务补偿开始");
        int generated = 0;

        List<ChFollowupPlan> activePlans = followupPlanMapper.selectList(
            Wrappers.<ChFollowupPlan>lambdaQuery()
                .eq(ChFollowupPlan::getPlanStatus, "ACTIVE")
        );

        for (ChFollowupPlan plan : activePlans) {
            Long existing = followupTaskMapper.selectCount(
                Wrappers.<ChFollowupTask>lambdaQuery()
                    .eq(ChFollowupTask::getPlanId, plan.getPlanId())
                    .and(w -> w.isNull(ChFollowupTask::getTaskType)
                        .or().notIn(ChFollowupTask::getTaskType, OUT_OF_PLAN_TASK_TYPES))
            );
            if (existing != null && existing > 0) {
                continue;
            }
            roundTaskGenerator.ensureRound(plan, 1, roundTaskGenerator.resolveFirstDueDate(plan), null);
            generated++;
        }

        SnailJobLog.REMOTE.info("随访首轮任务补偿完成, 补齐首轮任务数: {}", generated);
        return ExecuteResult.success("补齐首轮随访任务" + generated + "条");
    }
}
