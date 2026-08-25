package org.dromara.chronic.job;

import cn.hutool.core.date.DateUtil;
import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.common.log.SnailJobLog;
import com.aizuda.snailjob.model.dto.ExecuteResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.service.IChFollowupStatService;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 随访日统计跑批定时任务
 * <p>
 * 每日凌晨自动跑批统计前一日的随访总量、完成量、逾期量及完成率，写入 ch_stat_followup_day 表
 *
 * @author unimed
 */
@Slf4j
@Component
@JobExecutor(name = "followupStatJob")
@RequiredArgsConstructor
public class FollowupStatJob {

    private final IChFollowupStatService followupStatService;

    public ExecuteResult jobExecute(JobArgs jobArgs) {
        SnailJobLog.LOCAL.info("随访日统计跑批开始");

        Date yesterday = DateUtil.offsetDay(new Date(), -1);
        followupStatService.aggregateDailyStat(yesterday);

        SnailJobLog.REMOTE.info("随访日统计跑批完成, 统计日期: {}", DateUtil.formatDate(yesterday));
        return ExecuteResult.success("随访统计跑批完成: " + DateUtil.formatDate(yesterday));
    }
}
