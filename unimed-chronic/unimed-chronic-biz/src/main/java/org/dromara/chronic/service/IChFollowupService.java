package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChFollowupPlanBatchBo;
import org.dromara.chronic.domain.bo.ChFollowupPlanBo;
import org.dromara.chronic.domain.bo.ChFollowupSubmitBo;
import org.dromara.chronic.domain.vo.ChFollowupPlanVo;
import org.dromara.chronic.domain.vo.ChFollowupRecordVo;
import org.dromara.chronic.domain.vo.ChFollowupTaskDetailVo;
import org.dromara.chronic.domain.vo.ChFollowupTaskVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 随访服务
 *
 * @author unimed
 */
public interface IChFollowupService {

    Long createPlan(ChFollowupPlanBo bo);

    List<Long> createBatchPlans(ChFollowupPlanBatchBo bo);

    TableDataInfo<ChFollowupPlanVo> queryPlanPage(Long patientId, String diseaseCode, String planStatus,
                                                   PageQuery pageQuery);

    void updatePlanStatus(Long planId, String planStatus);

    void updateBatchPlanStatus(List<Long> planIds, String planStatus);

    TableDataInfo<ChFollowupTaskVo> queryTaskPage(Long patientId, Long assigneeUserId, String taskStatus,
                                                  String visitType, PageQuery pageQuery);

    void assignTask(Long taskId, Long assigneeUserId);

    void cancelTask(Long taskId);

    Long completeTask(Long taskId, ChFollowupSubmitBo bo, Long expectedPatientId,
                      Long expectedAssigneeUserId, Long visitorUserId, String forcedVisitType);

    List<ChFollowupRecordVo> queryRecordList(Long patientId);

    TableDataInfo<ChFollowupRecordVo> queryRecordPage(Long patientId, String visitType, PageQuery pageQuery);

    ChFollowupRecordVo queryRecordDetail(Long recordId);

    List<ChFollowupTaskVo> queryTodoTasks(Long assigneeUserId, String taskStatus);

    ChFollowupTaskDetailVo queryTaskDetail(Long taskId, Long expectedPatientId, Long expectedAssigneeUserId);

    ChFollowupPlanVo queryCurrentPlan(Long patientId);

    List<ChFollowupTaskVo> queryPatientTasks(Long patientId);
}
