package org.dromara.chronic.manager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.entity.ChStatAreaDay;
import org.dromara.chronic.domain.vo.ChAreaDictVo;
import org.dromara.chronic.domain.vo.ChKpiDefinitionVo;
import org.dromara.chronic.domain.vo.ChStatAreaDayVo;
import org.dromara.chronic.mapper.ChAreaDictMapper;
import org.dromara.chronic.mapper.ChKpiDefinitionMapper;
import org.dromara.chronic.mapper.ChStatAreaDayMapper;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 看板管理器：区域统计聚合+KPI计算+大屏数据
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardManager {

    private final ChAreaDictMapper areaDictMapper;
    private final ChStatAreaDayMapper statAreaDayMapper;
    private final ChKpiDefinitionMapper kpiDefinitionMapper;

    /**
     * 递归构建行政区划树
     */
    public List<ChAreaDictVo> buildAreaTree(String parentAreaCode) {
        List<ChAreaDictVo> allAreas = areaDictMapper.selectVoList(
            Wrappers.<org.dromara.chronic.domain.entity.ChAreaDict>lambdaQuery()
                .orderByAsc(org.dromara.chronic.domain.entity.ChAreaDict::getAreaLevel)
                .orderByAsc(org.dromara.chronic.domain.entity.ChAreaDict::getAreaCode)
        );
        Map<String, List<ChAreaDictVo>> childrenMap = allAreas.stream()
            .filter(a -> a.getParentAreaCode() != null)
            .collect(Collectors.groupingBy(ChAreaDictVo::getParentAreaCode));
        List<ChAreaDictVo> roots;
        if (parentAreaCode == null) {
            roots = allAreas.stream().filter(a -> a.getParentAreaCode() == null || "0".equals(a.getParentAreaCode())).toList();
        } else {
            roots = allAreas.stream().filter(a -> parentAreaCode.equals(a.getAreaCode())).toList();
        }
        for (ChAreaDictVo root : allAreas) {
            root.setChildren(childrenMap.getOrDefault(root.getAreaCode(), List.of()));
        }
        return roots;
    }

    /**
     * 查询区域日统计
     */
    public List<ChStatAreaDayVo> queryAreaStats(String areaCode, Date statDate) {
        var lqw = Wrappers.<ChStatAreaDay>lambdaQuery();
        lqw.eq(areaCode != null, ChStatAreaDay::getAreaCode, areaCode);
        lqw.eq(statDate != null, ChStatAreaDay::getStatDate, statDate);
        lqw.orderByDesc(ChStatAreaDay::getStatDate);
        return statAreaDayMapper.selectVoList(lqw);
    }

    /**
     * 查询KPI定义
     */
    public List<ChKpiDefinitionVo> queryKpiList() {
        return kpiDefinitionMapper.selectVoList(
            Wrappers.<org.dromara.chronic.domain.entity.ChKpiDefinition>lambdaQuery()
                .orderByAsc(org.dromara.chronic.domain.entity.ChKpiDefinition::getKpiCategory)
        );
    }

    /**
     * 大屏专用端点：聚合关键指标
     */
    public Map<String, Object> bigScreenSummary(String areaCode) {
        List<ChStatAreaDayVo> stats = queryAreaStats(areaCode, null);
        Map<String, Object> result = new java.util.HashMap<>();
        if (!stats.isEmpty()) {
            ChStatAreaDayVo latest = stats.get(0);
            result.put("patientCount", latest.getPatientCount());
            result.put("managedCount", latest.getManagedCount());
            result.put("warningCount", latest.getWarningCount());
            result.put("followupCount", latest.getFollowupCount());
            result.put("statDate", latest.getStatDate());
        }
        return result;
    }
}
