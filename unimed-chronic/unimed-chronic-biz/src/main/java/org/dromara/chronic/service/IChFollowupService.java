package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChFollowupPlanBo;
import org.dromara.chronic.domain.bo.ChFollowupRecordBo;
import org.dromara.chronic.domain.vo.ChFollowupPlanVo;
import org.dromara.chronic.domain.vo.ChFollowupRecordVo;
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

    TableDataInfo<ChFollowupTaskVo> queryTaskPage(Long assigneeUserId, String taskStatus, PageQuery pageQuery);

    Long completeTask(ChFollowupRecordBo bo);

    List<ChFollowupRecordVo> queryRecordList(Long patientId);

    List<ChFollowupTaskVo> queryTodoTasks(Long assigneeUserId);

    ChFollowupPlanVo queryCurrentPlan(Long patientId);

    List<ChFollowupTaskVo> queryPatientTasks(Long patientId);
}
