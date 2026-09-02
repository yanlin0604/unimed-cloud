package org.dromara.chronic.support.rule;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.entity.ChFollowupPlan;
import org.dromara.chronic.domain.entity.ChFollowupPlanItem;
import org.dromara.chronic.domain.entity.ChFollowupTask;
import org.dromara.chronic.mapper.ChFollowupPlanItemMapper;
import org.dromara.chronic.mapper.ChFollowupTaskMapper;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Locale;

/**
 * 随访轮次任务生成器（逐轮生成的唯一写入点）
 * <p>
 * 随访计划不再一次性按 totalRounds 预生成全年度任务：任何创建入口只生成首轮，
 * 后续轮次由医生在完成随访时填写「下次随访日期」驱动生成。本类负责按轮次幂等地
 * 装配并写入计划内常规(NORMAL)随访任务，避免入组、建计划、完成随访、补偿 Job 四处
 * 各自实现字段装配与去重逻辑造成行为漂移。
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FollowupRoundTaskGenerator {

    private static final String DEFAULT_VISIT_TYPE = "PHONE";
    private static final String DEFAULT_TENANT_ID = "000000";
    private static final Long DEFAULT_CREATE_DEPT = 103L;

    private final ChFollowupTaskMapper followupTaskMapper;
    private final ChFollowupPlanItemMapper followupPlanItemMapper;
    private final FollowupRuleEngine ruleEngine;

    /**
     * 幂等确保计划内某一轮次任务存在
     *
     * @param plan        随访计划
     * @param round       轮次（从 1 开始）
     * @param planDueDate 计划到期日（为空则取当前时间）
     * @param visitType   随访方式（为空则回落规则 default_visit_type，再回落 PHONE）
     * @return 该轮次任务（已存在则返回既有任务，不重复插入）
     */
    public ChFollowupTask ensureRound(ChFollowupPlan plan, int round, Date planDueDate, String visitType) {
        if (plan == null || plan.getPlanId() == null || plan.getPatientId() == null || round < 1) {
            throw new ServiceException("随访轮次任务参数不完整");
        }
        ChFollowupTask existing = followupTaskMapper.selectOne(
            Wrappers.<ChFollowupTask>lambdaQuery()
                .eq(ChFollowupTask::getPlanId, plan.getPlanId())
                .eq(ChFollowupTask::getPatientId, plan.getPatientId())
                .eq(ChFollowupTask::getTaskRound, round)
                .last("limit 1")
        );
        if (existing != null) {
            log.debug("随访任务已存在, 跳过生成 planId={}, round={}", plan.getPlanId(), round);
            return existing;
        }

        String finalVisitType = resolveVisitType(plan, visitType);
        ChFollowupTask task = new ChFollowupTask();
        task.setPlanId(plan.getPlanId());
        task.setPatientId(plan.getPatientId());
        task.setTaskRound(round);
        task.setPlanDueDate(planDueDate != null ? planDueDate : new Date());
        task.setTaskStatus("PENDING");
        task.setTaskType("NORMAL");
        task.setVisitType(finalVisitType);
        // 面对面标记由随访方式推导, 与 FollowupDynamicAdjuster 口径一致, 不再有独立机制
        task.setIsFaceToFace("OFFLINE".equalsIgnoreCase(finalVisitType));
        task.setAssigneeUserId(plan.getAssigneeUserId());
        task.setTenantId(StringUtils.isNotBlank(plan.getTenantId()) ? plan.getTenantId() : DEFAULT_TENANT_ID);
        task.setCreateDept(plan.getCreateDept() != null ? plan.getCreateDept() : DEFAULT_CREATE_DEPT);
        task.setCreateTime(new Date());
        task.setDelFlag("0");
        followupTaskMapper.insert(task);
        log.info("随访轮次任务已生成 planId={}, patientId={}, round={}, dueDate={}, visitType={}",
            plan.getPlanId(), plan.getPatientId(), round, task.getPlanDueDate(), finalVisitType);
        return task;
    }

    /**
     * 完成随访后按医生填写的「下次随访日期」生成下一轮任务
     *
     * @param finishedTask     本轮已完成的随访任务
     * @param plan             所属随访计划
     * @param nextFollowupDate 医生填写的下次随访日期
     * @return 下一轮任务；计划外任务（轮次为空）返回 null 表示不生成
     */
    public ChFollowupTask generateNextRound(ChFollowupTask finishedTask, ChFollowupPlan plan, Date nextFollowupDate) {
        if (finishedTask == null || finishedTask.getTaskRound() == null) {
            // 紧急/动态/转诊跟踪等计划外临时任务不参与轮次推进
            return null;
        }
        if (nextFollowupDate == null) {
            throw new ServiceException("下次随访日期格式不正确");
        }
        if (nextFollowupDate.before(DateUtil.beginOfDay(new Date()))) {
            throw new ServiceException("下次随访日期不能早于今天");
        }
        return ensureRound(plan, finishedTask.getTaskRound() + 1, nextFollowupDate, finishedTask.getVisitType());
    }

    /**
     * 推导计划首轮到期日：优先取计划项配置的到期日，其次按规则 first_due_days 从建档时间外推
     */
    public Date resolveFirstDueDate(ChFollowupPlan plan) {
        ChFollowupPlanItem item = followupPlanItemMapper.selectOne(
            Wrappers.<ChFollowupPlanItem>lambdaQuery()
                .eq(ChFollowupPlanItem::getPlanId, plan.getPlanId())
                .isNotNull(ChFollowupPlanItem::getDueDate)
                .orderByAsc(ChFollowupPlanItem::getDueDate)
                .last("limit 1")
        );
        if (item != null && item.getDueDate() != null) {
            return item.getDueDate();
        }
        int firstDueDays = ruleEngine.generateProposal(plan.getDiseaseCode(), plan.getManagementLevel()).firstDueDays();
        Date base = plan.getCreateTime() != null ? plan.getCreateTime() : new Date();
        return DateUtil.offsetDay(base, firstDueDays);
    }

    /**
     * 随访方式回落链：入参 → 规则 default_visit_type → PHONE
     */
    private String resolveVisitType(ChFollowupPlan plan, String visitType) {
        if (StringUtils.isNotBlank(visitType)) {
            return visitType.toUpperCase(Locale.ROOT);
        }
        try {
            return ruleEngine.generateProposal(plan.getDiseaseCode(), plan.getManagementLevel()).defaultVisitType();
        } catch (Exception e) {
            log.warn("推导规则随访方式失败, 回落 {} planId={}, err={}", DEFAULT_VISIT_TYPE, plan.getPlanId(), e.getMessage());
            return DEFAULT_VISIT_TYPE;
        }
    }
}
