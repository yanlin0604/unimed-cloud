package org.dromara.chronic.support;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.entity.ChFollowupTask;
import org.dromara.chronic.mapper.ChFollowupTaskMapper;
import org.dromara.common.redis.utils.RedisUtils;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * 随访逾期状态刷新器
 * <p>
 * FollowupRemindJob（SnailJob）负责"提醒 + 逾期"的完整流程；
 * 但在调度器未部署或漏跑时，任务状态会停留在 PENDING/REMINDING 导致列表看不到逾期。
 * 本类在随访列表查询入口做一次带节流的批量刷新，保证状态与日期始终一致。
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FollowupOverdueRefresher {

    /**
     * 节流键：同一节流窗口内只刷新一次，避免每次列表查询都写库
     */
    private static final String THROTTLE_KEY = "chronic:followup:overdue-refresh";

    private static final Duration THROTTLE = Duration.ofMinutes(5);

    private final ChFollowupTaskMapper followupTaskMapper;

    /**
     * 将计划日期已过的未完成任务批量置为 OVERDUE（节流窗口内最多执行一次）
     */
    public void refreshIfNeeded() {
        try {
            if (Boolean.TRUE.equals(RedisUtils.hasKey(THROTTLE_KEY))) {
                return;
            }
            RedisUtils.setCacheObject(THROTTLE_KEY, String.valueOf(System.currentTimeMillis()), THROTTLE);
            int changed = followupTaskMapper.update(null,
                Wrappers.<ChFollowupTask>lambdaUpdate()
                    .set(ChFollowupTask::getTaskStatus, "OVERDUE")
                    .in(ChFollowupTask::getTaskStatus, List.of("PENDING", "REMINDING"))
                    .lt(ChFollowupTask::getPlanDueDate, todayStart()));
            if (changed > 0) {
                log.info("随访逾期刷新: {} 条任务置为 OVERDUE", changed);
            }
        } catch (Exception e) {
            // 刷新失败不能影响列表查询
            log.warn("随访逾期刷新失败: {}", e.getMessage());
        }
    }

    private Date todayStart() {
        return java.sql.Date.valueOf(LocalDate.now());
    }
}
