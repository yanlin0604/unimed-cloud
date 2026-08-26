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

import java.util.Date;
import java.util.List;

/**
 * 随访服务接口
 *
 * @author unimed
 */
public interface IChFollowupService {

    Long createPlan(ChFollowupPlanBo bo);

    List<Long> createBatchPlans(ChFollowupPlanBatchBo bo);

    void updatePlan(ChFollowupPlanBo bo);

    void updateBatchPlans(List<ChFollowupPlanBo> planList);

    TableDataInfo<ChFollowupPlanVo> queryPlanPage(Long patientId, String diseaseCode, Long assigneeUserId,
                                                   String planStatus, PageQuery pageQuery);

    void updatePlanStatus(Long planId, String planStatus);

    void updateBatchPlanStatus(List<Long> planIds, String planStatus);

    TableDataInfo<ChFollowupTaskVo> queryTaskPage(Long patientId, Long assigneeUserId, String taskStatus,
                                                  String visitType, Date beginDate, Date endDate, PageQuery pageQuery);

    /**
     * 分页查询随访任务池（未分配执行人的待办任务）
     */
    TableDataInfo<ChFollowupTaskVo> queryTaskPoolPage(String diseaseCode, String visitType, PageQuery pageQuery);

    /**
     * 单个认领随访任务
     */
    void claimTask(Long taskId, Long userId);

    /**
     * 批量认领随访任务
     */
    void batchClaimTasks(List<Long> taskIds, Long userId);

    /**
     * 单个指派随访任务
     */
    void assignTask(Long taskId, Long assigneeUserId);

    /**
     * 批量指派随访任务
     */
    void batchAssignTasks(List<Long> taskIds, Long assigneeUserId);

    /**
     * 释放任务退回随访任务池
     */
    void releaseTask(Long taskId, Long userId);

    void cancelTask(Long taskId);

    void cancelTask(Long taskId, String cancelReasonCode, String cancelReasonDesc);

    Long completeTask(Long taskId, ChFollowupSubmitBo bo, Long expectedPatientId,
                      Long expectedAssigneeUserId, Long visitorUserId, String forcedVisitType);

    /**
     * 患者自填:仅采集体征/问卷/小结并保存为任务摘录,任务进入 PATIENT_FILLED 待医生评估,不标记完成。
     *
     * @param taskId   随访任务ID
     * @param bo       患者自填提交内容
     * @param patientId 患者ID(登录上下文取,禁止前端传入)
     * @param accountId 患者账号ID
     * @param forcedVisitType 强制随访方式(患者端按任务自身 visitType 提交: ONLINE/OFFLINE/PHONE/VIDEO)
     * @return 无意义占位返回 0
     */
    Long submitSelfFill(Long taskId, ChFollowupSubmitBo bo, Long patientId, Long accountId, String forcedVisitType);

    List<ChFollowupRecordVo> queryRecordList(Long patientId);

    TableDataInfo<ChFollowupRecordVo> queryRecordPage(Long patientId, String visitType, PageQuery pageQuery);

    ChFollowupRecordVo queryRecordDetail(Long recordId);

    List<ChFollowupTaskVo> queryTodoTasks(Long assigneeUserId, String taskStatus);

    ChFollowupTaskDetailVo queryTaskDetail(Long taskId, Long expectedPatientId, Long expectedAssigneeUserId);

    ChFollowupPlanVo queryCurrentPlan(Long patientId);

    List<ChFollowupTaskVo> queryPatientTasks(Long patientId);

    /**
     * 发送随访任务提醒 (向患者和执行人推送短信/通知)
     *
     * @param taskId 随访任务ID
     * @param operatorUserId 操作人ID (医生/管理员)
     */
    void sendTaskRemind(Long taskId, Long operatorUserId);
}
