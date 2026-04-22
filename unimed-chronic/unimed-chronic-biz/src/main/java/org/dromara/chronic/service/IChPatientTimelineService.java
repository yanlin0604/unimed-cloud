package org.dromara.chronic.service;

import org.dromara.chronic.domain.vo.ChPatientTimelineVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 患者时间线服务接口
 *
 * @author unimed
 */
public interface IChPatientTimelineService {

    void recordEvent(Long patientId, String eventType, String eventTitle, String eventDetail, java.time.LocalDateTime eventTime);

    TableDataInfo<ChPatientTimelineVo> queryPageList(Long patientId, PageQuery pageQuery);

    TableDataInfo<ChPatientTimelineVo> queryPageListByEventTypes(Long patientId, List<String> eventTypes, PageQuery pageQuery);

    List<ChPatientTimelineVo> queryList(Long patientId, String eventType, Integer limit);
}