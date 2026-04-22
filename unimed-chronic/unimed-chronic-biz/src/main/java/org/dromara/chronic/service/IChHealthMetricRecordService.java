package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChHealthMetricRecordBo;
import org.dromara.chronic.domain.entity.ChHealthMetricRecord;
import org.dromara.chronic.domain.vo.ChHealthMetricRecordVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 健康指标记录服务
 *
 * @author unimed
 */
public interface IChHealthMetricRecordService {

    Long reportMetric(ChHealthMetricRecordBo bo);

    ChHealthMetricRecord getById(Long metricId);

    ChHealthMetricRecordVo queryById(Long metricId);

    TableDataInfo<ChHealthMetricRecordVo> queryPageList(ChHealthMetricRecordBo bo, PageQuery pageQuery);

    List<ChHealthMetricRecordVo> queryTrend(Long patientId, String metricType, Integer limit);

    Void updateMetric(ChHealthMetricRecordBo bo);

    Void deleteMetric(Long metricId);
}
