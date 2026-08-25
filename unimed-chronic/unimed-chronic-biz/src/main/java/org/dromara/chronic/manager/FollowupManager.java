package org.dromara.chronic.manager;

import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChFollowupPlanBatchBo;
import org.dromara.chronic.domain.bo.ChFollowupPlanBo;
import org.dromara.chronic.domain.bo.ChFollowupSubmitBo;
import org.dromara.chronic.domain.vo.ChFollowupTaskVo;
import org.dromara.chronic.service.IChFollowupService;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 随访编排层
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class FollowupManager {

    private final IChFollowupService followupService;

    public Long createPlan(ChFollowupPlanBo bo) {
        return followupService.createPlan(bo);
    }

    public List<Long> createBatchPlans(ChFollowupPlanBatchBo bo) {
        return followupService.createBatchPlans(bo);
    }

    public void updatePlan(ChFollowupPlanBo bo) {
        followupService.updatePlan(bo);
    }

    public void updateBatchPlans(List<ChFollowupPlanBo> planList) {
        followupService.updateBatchPlans(planList);
    }

    public void updateBatchPlanStatus(List<Long> planIds, String planStatus) {
        followupService.updateBatchPlanStatus(planIds, planStatus);
    }

    public TableDataInfo<ChFollowupTaskVo> queryTaskPoolPage(String diseaseCode, String visitType, PageQuery pageQuery) {
        return followupService.queryTaskPoolPage(diseaseCode, visitType, pageQuery);
    }

    public void claimTask(Long taskId, Long userId) {
        followupService.claimTask(taskId, userId);
    }

    public void batchClaimTasks(List<Long> taskIds, Long userId) {
        followupService.batchClaimTasks(taskIds, userId);
    }

    public void assignTask(Long taskId, Long assigneeUserId) {
        followupService.assignTask(taskId, assigneeUserId);
    }

    public void batchAssignTasks(List<Long> taskIds, Long assigneeUserId) {
        followupService.batchAssignTasks(taskIds, assigneeUserId);
    }

    public void releaseTask(Long taskId, Long userId) {
        followupService.releaseTask(taskId, userId);
    }

    public Long completeTask(Long taskId, ChFollowupSubmitBo bo, Long expectedPatientId,
                             Long expectedAssigneeUserId, Long visitorUserId, String forcedVisitType) {
        return followupService.completeTask(taskId, bo, expectedPatientId, expectedAssigneeUserId,
            visitorUserId, forcedVisitType);
    }
}
