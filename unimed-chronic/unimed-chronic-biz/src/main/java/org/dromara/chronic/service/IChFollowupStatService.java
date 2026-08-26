package org.dromara.chronic.service;

import org.dromara.chronic.domain.vo.ChFollowupStatVo;

import java.util.Date;
import java.util.List;

/**
 * 随访统计服务接口
 *
 * @author unimed
 */
public interface IChFollowupStatService {

    /**
     * 获取随访统计全量看板数据
     */
    ChFollowupStatVo getFullStatDashboard(String areaCode, Long orgId);

    /**
     * 获取随访总览指标
     */
    ChFollowupStatVo.Overview getOverview(String areaCode, Long orgId);

    /**
     * 获取随访趋势统计
     *
     * @param days 最近天数（如 7 或 30）
     */
    List<ChFollowupStatVo.TrendItem> getTrend(int days, String areaCode, Long orgId);

    /**
     * 获取随访方式分布
     */
    List<ChFollowupStatVo.TypeDistributionItem> getTypeDistribution(String areaCode, Long orgId);

    /**
     * 获取执行人工作量排行榜
     */
    List<ChFollowupStatVo.AssigneeRankItem> getAssigneeRanking(int limit, String areaCode, Long orgId);

    /**
     * 获取病种随访统计
     */
    List<ChFollowupStatVo.DiseaseStatItem> getDiseaseStats(String areaCode, Long orgId);

    /**
     * 获取随访结论分布
     */
    List<ChFollowupStatVo.ResultDistributionItem> getResultDistribution(String areaCode, Long orgId);

    /**
     * 获取康复评级分布
     */
    List<ChFollowupStatVo.RehabDistributionItem> getRehabDistribution(String areaCode, Long orgId);

    /**
     * 获取任务状态分布
     */
    List<ChFollowupStatVo.StatusDistributionItem> getStatusDistribution(String areaCode, Long orgId);

    /**
     * 获取任务来源拆解
     */
    List<ChFollowupStatVo.TaskTypeDistributionItem> getTaskTypeDistribution(String areaCode, Long orgId);

    /**
     * 获取失访/取消原因分布
     */
    List<ChFollowupStatVo.LostReasonItem> getLostReasonStats(String areaCode, Long orgId);

    /**
     * 获取控制/逾期趋势统计
     */
    List<ChFollowupStatVo.RateTrendItem> getControlledTrend(int days, String areaCode, Long orgId);

    /**
     * 跑批聚合某天的随访日统计数据
     */
    void aggregateDailyStat(Date statDate);
}
