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

    Long addAction(ChWarningActionBo bo);

    List<ChWarningActionVo> queryActionsByWarningId(Long warningId);
}
