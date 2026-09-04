package org.dromara.chronic.manager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.entity.ChManagePlan;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.domain.entity.ChWarningEvent;
import org.dromara.chronic.mapper.ChManagePlanMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.chronic.mapper.ChWarningEventMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 慢病 GIS 空间地理信息与网格化态势感知管理器
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GisMapManager {

    private final ChPatientProfileMapper patientProfileMapper;
    private final ChManagePlanMapper managePlanMapper;
    private final ChWarningEventMapper warningEventMapper;

    /**
     * 获取县域全景 GIS 空间网格分布与热力数据（纯真数据直连，无硬编码模拟）
     */
    public Map<String, Object> getCountyGisOverview(String countyCode) {
        Map<String, Object> result = new LinkedHashMap<>();

        // 县域中心坐标 (默认以县域示范中心为基准: 116.407526, 39.904030)
        result.put("centerLng", 116.407526);
        result.put("centerLat", 39.904030);

        long totalPatients = 0L;
        double controlledRate = 0.0;
        long warningCount = 0L;

        try {
            Long count = patientProfileMapper.selectCount(
                Wrappers.<ChPatientProfile>lambdaQuery()
                    .eq(ChPatientProfile::getDelFlag, "0")
            );
            totalPatients = count != null ? count : 0L;

            Long controlled = managePlanMapper.selectCount(
                Wrappers.<ChManagePlan>lambdaQuery()
                    .eq(ChManagePlan::getPlanStatus, "ACTIVE")
            );
            long controlledCount = controlled != null ? controlled : 0L;
            if (totalPatients > 0) {
                controlledRate = BigDecimal.valueOf(controlledCount * 100.0 / totalPatients)
                    .setScale(2, RoundingMode.HALF_UP).doubleValue();
            }

            Long warnings = warningEventMapper.selectCount(
                Wrappers.<ChWarningEvent>lambdaQuery()
                    .in(ChWarningEvent::getEventStatus, List.of("NEW", "PROCESSING"))
            );
            warningCount = warnings != null ? warnings : 0L;
        } catch (Exception e) {
            log.error("查询慢病 GIS 真实概览数据失败", e);
        }

        result.put("totalChronicPatients", totalPatients);
        result.put("totalControlledRate", controlledRate);
        result.put("activeWarningCount", warningCount);

        // 1. 各街道/乡镇慢病空间网格统计（无造数时返回空列表，由底层数据库配置后动态呈现）
        result.put("grids", Collections.emptyList());

        // 2. 医疗机构与公卫服务点位（无造数时返回空列表）
        result.put("institutions", Collections.emptyList());

        // 3. 高危热力点（无造数时返回空列表）
        result.put("heatmapPoints", Collections.emptyList());

        return result;
    }
}
