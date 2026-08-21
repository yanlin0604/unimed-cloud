package org.dromara.chronic.manager;

import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChFollowupPlanBatchBo;
import org.dromara.chronic.domain.bo.ChFollowupPlanBo;
import org.dromara.chronic.domain.bo.ChFollowupSubmitBo;
import org.dromara.chronic.service.IChFollowupService;
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

    public Long completeTask(Long taskId, ChFollowupSubmitBo bo, Long expectedPatientId,
                             Long expectedAssigneeUserId, Long visitorUserId, String forcedVisitType) {
        return followupService.completeTask(taskId, bo, expectedPatientId, expectedAssigneeUserId,
            visitorUserId, forcedVisitType);
    }
}
