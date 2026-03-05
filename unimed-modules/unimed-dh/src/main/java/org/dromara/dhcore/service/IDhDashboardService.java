package org.dromara.dhcore.service;

import org.dromara.dhcore.domain.vo.DhDashboardMetricsVo;

/**
 * 数字人口播看板服务接口
 */
public interface IDhDashboardService {

    /**
     * 查询看板指标
     *
     * @param range 时间范围 today/7d/30d
     * @return 看板指标
     */
    DhDashboardMetricsVo queryMetrics(String range);
}
