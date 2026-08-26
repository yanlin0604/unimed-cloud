package org.dromara.chronic.support.rule;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.chronic.domain.bo.ChFollowupSubmitBo;
import org.dromara.chronic.domain.entity.*;
import org.dromara.chronic.mapper.*;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.resource.api.RemoteMessageService;
import org.dromara.resource.api.RemoteSmsService;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 随访结果评估与动态调整状态机
 * <p>
 * 随访完成提交后，根据控制效果与不良反应自动动态调整随访路径：
 * 1. 控制满意(CONTROLLED/IMPROVING)：维持原周期
 * 2. 首次不满意(UNCONTROLLED/DETERIORATING)或存在药物不良反应(ADR)：自动插入14天动态核查随访任务
 * 3. 连续两次不满意或建议转诊(REFERRAL)：自动生成建议转诊意向单并生成14天转诊追踪随访任务
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FollowupDynamicAdjuster {

    /**
     * 随访结论(chronic_followup_result)中判定为"控制不满意"的取值。
     * 该字典仅有 CONTROLLED/IMPROVING/UNCONTROLLED/DETERIORATING/REFERRAL —— REFERRAL 由
     * isExplicitReferral 单独处理。此前用 contains 模糊匹配且含 "POOR"，而 POOR 属于康复评级
     * (chronic_rehab_level: EXCELLENT/GOOD/FAIR/POOR)，一旦前端把 rehabLevel 传错位置就会误触发。
     */
    private static final Set<String> UNSATISFACTORY_RESULTS = Set.of("UNCONTROLLED", "DETERIORATING");

    private final ChFollowupTaskMapper followupTaskMapper;
    private final ChFollowupRecordMapper followupRecordMapper;
    private final ChPatientProfileMapper patientProfileMapper;
    private final ChPatientTimelineMapper patientTimelineMapper;
    private final ChReferralRecordMapper referralRecordMapper;

    @DubboReference(mock = "org.dromara.resource.api.RemoteMessageServiceStub")
    private RemoteMessageService remoteMessageService;

    @DubboReference(mock = "true")
    private RemoteSmsService remoteSmsService;

    /**
     * 动态调整结果类型
     */
    public enum AdjustmentAction {
        MAINTAIN,           // 维持原计划
        DYNAMIC_CHECK_14D,  // 14天内核查随访
        REFERRAL_TRACK_14D  // 建议转诊及14天跟踪
    }

    public record AdjustmentResult(
        AdjustmentAction action,
        String reason,
        Long generatedTaskId,
        Long generatedReferralId
    ) {}

    /**
     * 评估随访结果并执行动态调整
     */
    public AdjustmentResult evaluateAndAdjust(ChFollowupTask currentTask, ChFollowupRecord record, ChFollowupSubmitBo bo) {
        if (currentTask == null || record == null) {
            return new AdjustmentResult(AdjustmentAction.MAINTAIN, "缺少任务或记录上下文", null, null);
        }

        Long patientId = currentTask.getPatientId();
        String result = record.getFollowupResult();
        boolean isCurrentUncontrolled = isUnsatisfactoryResult(result);
        boolean hasAdr = StringUtils.isNotBlank(bo.getAdrDescription());
        boolean isExplicitReferral = "REFERRAL".equalsIgnoreCase(result) || Boolean.TRUE.equals(bo.getIsReferralSuggested());

        // 1. 判断是否连续两次控制不满意 或 明确触发转诊建议
        if (isExplicitReferral || (isCurrentUncontrolled && isPreviousUncontrolled(patientId, currentTask.getTaskId()))) {
            String referralReason = isExplicitReferral
                ? "随访评估建议转诊: " + (StringUtils.isNotBlank(bo.getAdvice()) ? bo.getAdvice() : "病情波动需上级医院诊治")
                : "连续两次随访控制不满意，触发转诊评估机制";
            
            Long referralId = createReferralSuggestion(patientId, currentTask, referralReason);
            Long trackTaskId = createReferralTrackTask(patientId, currentTask);
            
            notifyDoctor(currentTask.getAssigneeUserId(), patientId, "【高优先级】患者因连续随访不满意/病情需要已生成建议转诊单，请及时跟进处理。");
            recordTimeline(patientId, "REFERRAL_SUGGESTION", "建议转诊触发", referralReason);

            log.info("动态调整: 患者 patientId={} 触发连续不达标/转诊建议, referralId={}, trackTaskId={}",
                patientId, referralId, trackTaskId);
            return new AdjustmentResult(AdjustmentAction.REFERRAL_TRACK_14D, referralReason, trackTaskId, referralId);
        }

        // 2. 判断是否首次控制不满意 或 出现药物不良反应
        if (isCurrentUncontrolled || hasAdr) {
            String adjustReason = hasAdr
                ? "出现药物不良反应（" + bo.getAdrDescription() + "），需2周内用药复查"
                : "本次随访控制不满意，需2周内动态复查";

            Long dynamicTaskId = createDynamicCheckTask(patientId, currentTask, adjustReason);

            notifyDoctor(currentTask.getAssigneeUserId(), patientId,
                "【随访预警】患者本次随访指标控制不满意/出现不良反应，系统已自动排期14天内动态核查随访。");
            notifyPatientSms(patientId, "尊敬的慢病患者，您本次随访提示指标有波动或需调整用药，医护人员将于2周内进行电话/面对面核查复访，请按医嘱规范管理。");
            recordTimeline(patientId, "DYNAMIC_FOLLOWUP", "动态缩期核查", adjustReason);

            log.info("动态调整: 患者 patientId={} 触发首次不达标/ADR, 生成14天核查任务 dynamicTaskId={}",
                patientId, dynamicTaskId);
            return new AdjustmentResult(AdjustmentAction.DYNAMIC_CHECK_14D, adjustReason, dynamicTaskId, null);
        }

        // 3. 达标/满意：维持原计划
        return new AdjustmentResult(AdjustmentAction.MAINTAIN, "控制满意，维持既定随访计划", null, null);
    }

    private boolean isUnsatisfactoryResult(String result) {
        if (StringUtils.isBlank(result)) return false;
        return UNSATISFACTORY_RESULTS.contains(result.trim().toUpperCase(Locale.ROOT));
    }

    private boolean isPreviousUncontrolled(Long patientId, Long currentTaskId) {
        var wrapper = Wrappers.<ChFollowupRecord>lambdaQuery()
            .eq(ChFollowupRecord::getPatientId, patientId);
        if (currentTaskId != null) {
            wrapper.ne(ChFollowupRecord::getTaskId, currentTaskId);
        }
        ChFollowupRecord lastRec = followupRecordMapper.selectOne(
            wrapper.orderByDesc(ChFollowupRecord::getVisitDate).last("limit 1"));
        if (lastRec == null) {
            return false;
        }
        return isUnsatisfactoryResult(lastRec.getFollowupResult());
    }

    private Long createDynamicCheckTask(Long patientId, ChFollowupTask currentTask, String reason) {
        ChFollowupTask task = new ChFollowupTask();
        task.setPatientId(patientId);
        task.setPlanId(currentTask.getPlanId());
        task.setTaskRound(null);
        task.setTaskType("DYNAMIC");
        applyVisitType(task, currentTask);
        task.setPlanDueDate(DateUtil.offsetDay(new Date(), 14));
        task.setTaskStatus("PENDING");
        task.setAssigneeUserId(currentTask.getAssigneeUserId());
        followupTaskMapper.insert(task);
        return task.getTaskId();
    }

    private Long createReferralTrackTask(Long patientId, ChFollowupTask currentTask) {
        ChFollowupTask task = new ChFollowupTask();
        task.setPatientId(patientId);
        task.setPlanId(currentTask.getPlanId());
        task.setTaskRound(null);
        task.setTaskType("REFERRAL_TRACK");
        applyVisitType(task, currentTask);
        task.setPlanDueDate(DateUtil.offsetDay(new Date(), 14));
        task.setTaskStatus("PENDING");
        task.setAssigneeUserId(currentTask.getAssigneeUserId());
        followupTaskMapper.insert(task);
        return task.getTaskId();
    }

    /**
     * 计划外临时任务的随访方式继承当前任务(缺省回落电话),保持该患者既有的随访形态；
     * isFaceToFace 由 visitType 推导，避免出现 visitType=PHONE 却 isFaceToFace=true 的自相矛盾数据。
     * <p>
     * taskRound 一并置空：此前复制当前轮次会与计划内同轮 NORMAL 任务撞键，污染
     * FollowupTaskGenJob 的 planId+round 去重，且在医生端被显示成名不副实的"第 N 轮"。
     */
    private void applyVisitType(ChFollowupTask task, ChFollowupTask currentTask) {
        String visitType = StringUtils.isNotBlank(currentTask.getVisitType())
            ? currentTask.getVisitType().trim().toUpperCase(Locale.ROOT)
            : "PHONE";
        task.setVisitType(visitType);
        task.setIsFaceToFace("OFFLINE".equals(visitType));
    }

    private Long createReferralSuggestion(Long patientId, ChFollowupTask currentTask, String reason) {
        try {
            ChReferralRecord referral = new ChReferralRecord();
            referral.setPatientId(patientId);
            referral.setReferralReason(reason);
            referral.setReferralCategory("FOLLOWUP_UNSATISFIED");
            referral.setReferralStatus("PENDING");
            referral.setReferralType("UPWARD");
            referral.setReferralTime(new Date());
            referralRecordMapper.insert(referral);
            return referral.getReferralId();
        } catch (Exception e) {
            log.warn("自动创建转诊意向单失败 patientId={}, err={}", patientId, e.getMessage());
            return null;
        }
    }

    private void notifyDoctor(Long doctorUserId, Long patientId, String message) {
        if (doctorUserId == null || remoteMessageService == null) return;
        try {
            remoteMessageService.publishMessage(List.of(doctorUserId), message);
        } catch (Exception e) {
            log.warn("向责任医生推送动态调整消息失败 doctorId={}, err={}", doctorUserId, e.getMessage());
        }
    }

    private void notifyPatientSms(Long patientId, String content) {
        if (patientId == null || remoteSmsService == null) return;
        try {
            ChPatientProfile patient = patientProfileMapper.selectById(patientId);
            if (patient != null && StringUtils.isNotBlank(patient.getPhone())) {
                remoteSmsService.sendMessageAsync(patient.getPhone(), content);
            }
        } catch (Exception e) {
            log.warn("向患者发送动态调整提醒短信失败 patientId={}, err={}", patientId, e.getMessage());
        }
    }

    private void recordTimeline(Long patientId, String eventType, String title, String detail) {
        if (patientTimelineMapper == null || patientId == null) return;
        try {
            ChPatientTimeline timeline = new ChPatientTimeline();
            timeline.setPatientId(patientId);
            timeline.setEventType(eventType);
            timeline.setEventTitle(title);
            timeline.setEventDetail(detail);
            timeline.setEventTime(new Date());
            patientTimelineMapper.insert(timeline);
        } catch (Exception e) {
            log.warn("向患者时间线沉淀动态调整事件失败 patientId={}, err={}", patientId, e.getMessage());
        }
    }
}
