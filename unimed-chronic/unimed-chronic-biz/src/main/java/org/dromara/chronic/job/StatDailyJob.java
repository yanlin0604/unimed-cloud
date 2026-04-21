package org.dromara.chronic.job;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.common.log.SnailJobLog;
import com.aizuda.snailjob.model.dto.ExecuteResult;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.entity.ChStatAreaDay;
import org.dromara.chronic.mapper.ChFollowupTaskMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.chronic.mapper.ChStatAreaDayMapper;
import org.dromara.chronic.mapper.ChWarningEventMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * 日统计汇总定时任务
 * <p>
 * 汇总患者数/预警数/随访完成数等指标写入ch_stat_area_day
 * 幂等：同一天数据覆盖写入
 *
 * @author unimed
 */
@Component
@JobExecutor(name = "statDailyJob")
@RequiredArgsConstructor
public class StatDailyJob {

    private final ChPatientProfileMapper patientProfileMapper;
    private final ChWarningEventMapper warningEventMapper;
    private final ChFollowupTaskMapper followupTaskMapper;
    private final ChStatAreaDayMapper statAreaDayMapper;

    public ExecuteResult jobExecute(JobArgs jobArgs) {
        SnailJobLog.LOCAL.info("日统计汇总开始");
        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        long totalPatients = patientProfileMapper.selectCount(null);
        long newWarnings = warningEventMapper.selectCount(
            Wrappers.<org.dromara.chronic.domain.entity.ChWarningEvent>lambdaQuery()
                .ge(org.dromara.chronic.domain.entity.ChWarningEvent::getWarningTime, today)
        );
        long doneFollowups = followupTaskMapper.selectCount(
            Wrappers.<org.dromara.chronic.domain.entity.ChFollowupTask>lambdaQuery()
                .eq(org.dromara.chronic.domain.entity.ChFollowupTask::getTaskStatus, "DONE")
                .ge(org.dromara.chronic.domain.entity.ChFollowupTask::getUpdateTime, today)
        );

        // 幂等：先删后插（同一stat_date只保留一条）
        statAreaDayMapper.delete(
            Wrappers.<ChStatAreaDay>lambdaQuery()
                .eq(ChStatAreaDay::getStatDate, today)
        );

        ChStatAreaDay stat = new ChStatAreaDay();
        stat.setStatDate(today);
        stat.setPatientCount(totalPatients);
        stat.setWarningCount(newWarnings);
        stat.setFollowupCount(doneFollowups);
        statAreaDayMapper.insert(stat);

        SnailJobLog.REMOTE.info("日统计汇总完成, 患者数:{}, 新增预警:{}, 完成随访:{}",
            totalPatients, newWarnings, doneFollowups);
        return ExecuteResult.success("日统计汇总完成");
    }
}
