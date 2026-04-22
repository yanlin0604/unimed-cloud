package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChWarningActionBo;
import org.dromara.chronic.domain.bo.ChWarningEventBo;
import org.dromara.chronic.domain.vo.ChWarningActionVo;
import org.dromara.chronic.domain.vo.ChWarningEventVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 预警事件服务
 *
 * @author unimed
 */
public interface IChWarningEventService {

    Long createEvent(ChWarningEventBo bo);

    ChWarningEventVo queryById(Long warningId);

    TableDataInfo<ChWarningEventVo> queryPageList(ChWarningEventBo bo, PageQuery pageQuery);

    List<ChWarningEventVo> queryByPatientId(Long patientId);

    Void updateStatus(Long warningId, String newStatus);

    /**
     * 更新预警事件状态，同时写入操作人上下文的 action 记录
     *
     * @param warningId   预警事件ID
     * @param newStatus   目标状态
     * @param actionUserId 操作人用户ID
     * @param actionDetail 操作详情
     */
    Void updateStatus(Long warningId, String newStatus, Long actionUserId, String actionDetail);

    Long addAction(ChWarningActionBo bo);

    List<ChWarningActionVo> queryActionsByWarningId(Long warningId);
}
