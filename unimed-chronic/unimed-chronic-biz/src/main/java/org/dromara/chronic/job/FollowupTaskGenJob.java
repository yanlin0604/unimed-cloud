package org.dromara.chronic.job;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.common.log.SnailJobLog;
import com.aizuda.snailjob.model.dto.ExecuteResult;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.entity.ChFollowupPlan;
import org.dromara.chronic.domain.entity.ChFollowupPlanItem;
import org.dromara.chronic.domain.entity.ChFollowupTask;
import org.dromara.chronic.mapper.ChFollowupPlanItemMapper;
import org.dromara.chronic.mapper.ChFollowupPlanMapper;
import org.dromara.chronic.mapper.ChFollowupTaskMapper;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 随访任务批量生成定时任务
 * <p>
 * 扫描生效的随访计划，根据计划子项批量生成待执行随访任务
 *
 * @author unimed
 */
@Component
@JobExecutor(name = "followupTaskGenJob")
@RequiredArgsConstructor
public class FollowupTaskGenJob {

    private final ChFollowupPlanMapper followupPlanMapper;
    private final ChFollowupPlanItemMapper followupPlanItemMapper;
    private final ChFollowupTaskMapper followupTaskMapper;

    public ExecuteResult jobExecute(JobArgs jobArgs) {
        SnailJobLog.LOCAL.info("随访任务生成开始");
        int generated = 0;

        List<ChFollowupPlan> activePlans = followupPlanMapper.selectList(
            Wrappers.<ChFollowupPlan>lambdaQuery()
                .eq(ChFollowupPlan::getPlanStatus, "ACTIVE")
        );

        for (ChFollowupPlan plan : activePlans) {
            List<ChFollowupPlanItem> items = followupPlanItemMapper.selectList(
                Wrappers.<ChFollowupPlanItem>lambdaQuery()
                    .eq(ChFollowupPlanItem::getPlanId, plan.getPlanId())
            );
            for (ChFollowupPlanItem item : items) {
                // 幂等：同一plan+round不重复生成
                Long existing = followupTaskMapper.selectCount(
                    Wrappers.<ChFollowupTask>lambdaQuery()
                        .eq(ChFollowupTask::getPlanId, plan.getPlanId())
                        .eq(ChFollowupTask::getTaskRound, plan.getCycleDays())
                        .eq(ChFollowupTask::getPatientId, plan.getPatientId())
                );
                if (existing > 0) {
                    continue;
                }

                ChFollowupTask task = new ChFollowupTask();
                task.setPlanId(plan.getPlanId());
                task.setPatientId(plan.getPatientId());
                task.setTaskRound(plan.getCycleDays());
                task.setTaskStatus("PENDING");
                task.setPlanDate(new Date());
                task.setCreateTime(new Date());
                followupTaskMapper.insert(task);
                generated++;
            }
        }

        SnailJobLog.REMOTE.info("随访任务生成完成, 生成任务数: {}", generated);
        return ExecuteResult.success("生成随访任务" + generated + "条");
    }
}
