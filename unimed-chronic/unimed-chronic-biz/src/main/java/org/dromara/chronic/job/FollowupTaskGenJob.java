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
import org.dromara.chronic.support.rule.FollowupRuleEngine;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 随访任务补偿生成定时任务
 * <p>
 * 扫描生效的随访计划，仅补齐缺失轮次的随访任务（createPlan 为主生成点，Job 仅做补偿）
 *
 * @author unimed
 */
@Component
@JobExecutor(name = "followupTaskGenJob")
@RequiredArgsConstructor
public class FollowupTaskGenJob {

    private final ChFollowupPlanMapper followupPlanMapper;
    private final ChFollowupTaskMapper followupTaskMapper;
    private final FollowupRuleEngine ruleEngine;

    public ExecuteResult jobExecute(JobArgs jobArgs) {
        SnailJobLog.LOCAL.info("随访任务补偿生成开始");
        int generated = 0;

        List<ChFollowupPlan> activePlans = followupPlanMapper.selectList(
            Wrappers.<ChFollowupPlan>lambdaQuery()
                .eq(ChFollowupPlan::getPlanStatus, "ACTIVE")
        );

        for (ChFollowupPlan plan : activePlans) {
            for (int round = 1; round <= plan.getTotalRounds(); round++) {
                Long existing = followupTaskMapper.selectCount(
                    Wrappers.<ChFollowupTask>lambdaQuery()
                        .eq(ChFollowupTask::getPlanId, plan.getPlanId())
                        .eq(ChFollowupTask::getTaskRound, round)
                        .eq(ChFollowupTask::getPatientId, plan.getPatientId())
                );
                if (existing > 0) {
                    continue;
                }

                ChFollowupTask task = new ChFollowupTask();
                task.setPlanId(plan.getPlanId());
                task.setPatientId(plan.getPatientId());
                task.setTaskRound(round);
                task.setTaskStatus("PENDING");
                task.setPlanDueDate(computeDueDate(plan, round));
                task.setAssigneeUserId(plan.getAssigneeUserId());
                task.setTaskType("NORMAL");
                // 所有轮次统一使用规则 default_visit_type,不再有独立的"面对面"机制
                FollowupRuleEngine.FollowupPlanProposal proposal = ruleEngine.generateProposal(
                    plan.getDiseaseCode(), plan.getManagementLevel());
                task.setIsFaceToFace("OFFLINE".equalsIgnoreCase(proposal.defaultVisitType()));
                task.setVisitType(proposal.defaultVisitType());
                task.setTenantId(plan.getTenantId() != null ? plan.getTenantId() : "000000");
                task.setCreateDept(plan.getCreateDept());
                task.setCreateTime(new Date());
                task.setDelFlag("0");
                followupTaskMapper.insert(task);
                generated++;
            }
        }

        SnailJobLog.REMOTE.info("随访任务补偿生成完成, 补齐任务数: {}", generated);
        return ExecuteResult.success("补偿随访任务" + generated + "条");
    }

    private Date computeDueDate(ChFollowupPlan plan, int round) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(plan.getCreateTime() != null ? plan.getCreateTime() : new Date());
        calendar.add(Calendar.DAY_OF_MONTH, plan.getCycleDays() * (round - 1));
        return calendar.getTime();
    }
}
