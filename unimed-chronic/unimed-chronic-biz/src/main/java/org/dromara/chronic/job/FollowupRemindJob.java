package org.dromara.chronic.job;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.common.log.SnailJobLog;
import com.aizuda.snailjob.model.dto.ExecuteResult;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.entity.ChFollowupTask;
import org.dromara.chronic.mapper.ChFollowupTaskMapper;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 随访到期前提醒定时任务
 * <p>
 * 扫描即将到期的PENDING状态随访任务，标记为REMINDING状态
 *
 * @author unimed
 */
@Component
@JobExecutor(name = "followupRemindJob")
@RequiredArgsConstructor
public class FollowupRemindJob {

    private final ChFollowupTaskMapper followupTaskMapper;

    public ExecuteResult jobExecute(JobArgs jobArgs) {
        SnailJobLog.LOCAL.info("随访提醒开始");
        int reminded = 0;

        // 提前3天提醒（到期日-3天 <= 当前日期）
        Date remindBefore = new Date(System.currentTimeMillis() + 3L * 24 * 60 * 60 * 1000);

        List<ChFollowupTask> pendingTasks = followupTaskMapper.selectList(
            Wrappers.<ChFollowupTask>lambdaQuery()
                .eq(ChFollowupTask::getTaskStatus, "PENDING")
                .le(ChFollowupTask::getPlanDate, remindBefore)
        );

        for (ChFollowupTask task : pendingTasks) {
            task.setTaskStatus("REMINDING");
            followupTaskMapper.updateById(task);
            reminded++;
            // TODO: 发送提醒通知（微信/短信）
        }

        SnailJobLog.REMOTE.info("随访提醒完成, 提醒任务数: {}", reminded);
        return ExecuteResult.success("提醒随访任务" + reminded + "条");
    }
}
