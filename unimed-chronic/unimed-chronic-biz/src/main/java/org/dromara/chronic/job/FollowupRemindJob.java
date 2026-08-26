package org.dromara.chronic.job;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.common.log.SnailJobLog;
import com.aizuda.snailjob.model.dto.ExecuteResult;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.chronic.domain.entity.ChFollowupTask;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.domain.entity.ChPatientTimeline;
import org.dromara.chronic.mapper.ChFollowupTaskMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.chronic.mapper.ChPatientTimelineMapper;
import org.dromara.chronic.service.IChNotificationTemplateService;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.resource.api.RemoteMessageService;
import org.dromara.resource.api.RemoteSmsService;
import org.dromara.system.api.RemoteUserService;
import org.dromara.system.api.domain.vo.RemoteUserVo;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 随访到期前提醒定时任务
 * <p>
 * R2: 扫描 PENDING 任务——
 * 1. planDueDate < now → 置 OVERDUE
 * 2. planDueDate < now + 3d → 置 REMINDING 并推送通知给 assigneeUserId 及患者
 * 3. 已 REMINDING 且 planDueDate < now → 置 OVERDUE
 *
 * @author unimed
 */
@Slf4j
@Component
@JobExecutor(name = "followupRemindJob")
@RequiredArgsConstructor
public class FollowupRemindJob {

    private final ChFollowupTaskMapper followupTaskMapper;
    private final ChPatientProfileMapper patientProfileMapper;
    private final ChPatientTimelineMapper patientTimelineMapper;
    private final IChNotificationTemplateService notificationTemplateService;
    @DubboReference(mock = "org.dromara.resource.api.RemoteMessageServiceStub")
    private RemoteMessageService remoteMessageService;
    @DubboReference(mock = "true")
    private RemoteSmsService remoteSmsService;
    @DubboReference
    private RemoteUserService remoteUserService;

    /** 到期前 N 天开始提醒 */
    private static final long REMIND_DAYS = 3;

    /** 随访提醒文案模板编码（ch_notification_template.template_code） */
    private static final String TPL_FOLLOWUP_REMIND = "FOLLOWUP_REMIND";

    public ExecuteResult jobExecute(JobArgs jobArgs) {
        SnailJobLog.LOCAL.info("随访提醒开始");
        int reminded = 0;
        int overdue = 0;

        Date now = new Date();
        Date remindBefore = new Date(System.currentTimeMillis() + REMIND_DAYS * 24 * 60 * 60 * 1000);

        // 1. 将已过期的 PENDING/REMINDING 任务置为 OVERDUE
        List<ChFollowupTask> expiredTasks = followupTaskMapper.selectList(
            Wrappers.<ChFollowupTask>lambdaQuery()
                .in(ChFollowupTask::getTaskStatus, "PENDING", "REMINDING")
                .lt(ChFollowupTask::getPlanDueDate, now)
        );
        for (ChFollowupTask task : expiredTasks) {
            task.setTaskStatus("OVERDUE");
            followupTaskMapper.updateById(task);
            overdue++;
        }

        // 2. 将即将到期的 PENDING 任务置为 REMINDING 并发送提醒
        List<ChFollowupTask> upcomingTasks = followupTaskMapper.selectList(
            Wrappers.<ChFollowupTask>lambdaQuery()
                .eq(ChFollowupTask::getTaskStatus, "PENDING")
                .le(ChFollowupTask::getPlanDueDate, remindBefore)
                .ge(ChFollowupTask::getPlanDueDate, now)
        );
        for (ChFollowupTask task : upcomingTasks) {
            task.setTaskStatus("REMINDING");
            followupTaskMapper.updateById(task);
            reminded++;
            // R2: 多通道推送通知（消息推送失败不影响状态更新）
            sendRemindNotification(task);
        }

        SnailJobLog.REMOTE.info("随访提醒完成, 提醒任务数: {}, 过期任务数: {}", reminded, overdue);
        return ExecuteResult.success("提醒" + reminded + "条, 过期" + overdue + "条");
    }

    /**
     * R2: 向随访执行人及患者推送提醒通知
     * <p>
     * 消息推送失败不影响任务状态更新，仅记录 warn 日志。
     * 同一任务不重复发提醒（状态已变为 REMINDING，不会再次命中 PENDING 查询）。
     */
    private void sendRemindNotification(ChFollowupTask task) {
        String message = buildRemindMessage(task);

        // 1. 向执行人推送站内信与短信
        if (task.getAssigneeUserId() != null) {
            try {
                // 站内消息推送（SSE/WebSocket）
                if (remoteMessageService != null) {
                    remoteMessageService.publishMessage(List.of(task.getAssigneeUserId()), message);
                }
            } catch (Exception e) {
                log.warn("随访提醒站内消息推送失败 assigneeUserId={} taskId={} msg={}",
                    task.getAssigneeUserId(), task.getTaskId(), e.getMessage());
            }
            try {
                // 短信推送（异步，不阻塞）—— 需先查询操作人手机号
                String phone = lookupPhone(task.getAssigneeUserId());
                if (phone != null && remoteSmsService != null) {
                    remoteSmsService.sendMessageAsync(phone, message);
                }
            } catch (Exception e) {
                log.warn("随访提醒短信推送失败 assigneeUserId={} taskId={} msg={}",
                    task.getAssigneeUserId(), task.getTaskId(), e.getMessage());
            }
        }

        // 2. 向患者推送随访短信提醒并记录到时间线
        if (task.getPatientId() != null) {
            String dueDateStr = task.getPlanDueDate() != null ? String.valueOf(task.getPlanDueDate()) : "近期";
            String patientMsg = message;
            if (patientProfileMapper != null) {
                try {
                    ChPatientProfile patient = patientProfileMapper.selectById(task.getPatientId());
                    if (patient != null && StringUtils.isNotBlank(patient.getPhone()) && remoteSmsService != null) {
                        remoteSmsService.sendMessageAsync(patient.getPhone(), patientMsg);
                    }
                } catch (Exception e) {
                    log.warn("随访提醒向患者推送短信失败 patientId={} taskId={} msg={}",
                        task.getPatientId(), task.getTaskId(), e.getMessage());
                }
            }
            if (patientTimelineMapper != null) {
                try {
                    ChPatientTimeline timeline = new ChPatientTimeline();
                    timeline.setPatientId(task.getPatientId());
                    timeline.setEventType("FOLLOWUP_REMIND");
                    timeline.setEventTitle("随访提醒通知");
                    timeline.setEventDetail(String.format("您的慢病管理团队向您发送了第%s轮随访提醒（到期日：%s），请按期自填完成随访。",
                        task.getTaskRound() != null ? task.getTaskRound() : "1", dueDateStr));
                    timeline.setEventTime(new Date());
                    timeline.setTenantId(task.getTenantId());
                    patientTimelineMapper.insert(timeline);
                } catch (Exception e) {
                    log.warn("随访提醒记录患者时间线失败 patientId={} taskId={}", task.getPatientId(), task.getTaskId());
                }
            }
        }
    }

    /**
     * 构造随访提醒文案：优先取 ch_notification_template 中 FOLLOWUP_REMIND 模板渲染，
     * 模板不存在 / 已停用 / 渲染失败时退回原硬编码文案（行为不退化）。
     * <p>
     * 支持占位符：{patientId} {dueDate} {taskId}
     */
    private String buildRemindMessage(ChFollowupTask task) {
        String fallback = "您有一条随访任务即将到期，患者ID: " + task.getPatientId()
            + "，到期日: " + task.getPlanDueDate();
        try {
            Map<String, String> params = new HashMap<>(4);
            params.put("patientId", String.valueOf(task.getPatientId()));
            params.put("dueDate", String.valueOf(task.getPlanDueDate()));
            params.put("taskId", String.valueOf(task.getTaskId()));
            String rendered = notificationTemplateService.render(TPL_FOLLOWUP_REMIND, null, params);
            if (StringUtils.isNotBlank(rendered)) {
                return rendered;
            }
        } catch (Exception e) {
            log.warn("随访提醒文案模板渲染失败 taskId={} msg={}", task.getTaskId(), e.getMessage());
        }
        return fallback;
    }

    /**
     * 通过 RemoteUserService 查询操作人手机号
     */
    private String lookupPhone(Long userId) {
        try {
            List<RemoteUserVo> users = remoteUserService.selectListByIds(List.of(userId));
            if (users != null && !users.isEmpty()) {
                return users.get(0).getPhonenumber();
            }
        } catch (Exception e) {
            log.warn("查询用户手机号失败 userId={} msg={}", userId, e.getMessage());
        }
        return null;
    }
}
