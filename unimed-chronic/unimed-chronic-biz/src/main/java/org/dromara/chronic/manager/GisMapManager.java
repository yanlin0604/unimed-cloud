package org.dromara.chronic.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

    /**
     * 获取县域全景 GIS 空间网格分布与热力数据
     */
    public Map<String, Object> getCountyGisOverview(String countyCode) {
        Map<String, Object> result = new LinkedHashMap<>();

        // 县域中心坐标 (默认以典型县域示范中心为基准: 116.407526, 39.904030)
        result.put("centerLng", 116.407526);
        result.put("centerLat", 39.904030);
        result.put("totalChronicPatients", 38450);
        result.put("totalControlledRate", 81.65);
        result.put("activeWarningCount", 142);

        // 1. 各街道/乡镇慢病空间网格统计
        List<Map<String, Object>> grids = new ArrayList<>();
        grids.add(createGrid("grid_01", "城关街道", 116.4075, 39.9040, 10240, 48, 83.2, "GREEN"));
        grids.add(createGrid("grid_02", "龙山镇", 116.4350, 39.9210, 6890, 26, 79.5, "YELLOW"));
        grids.add(createGrid("grid_03", "清溪镇", 116.3810, 39.8920, 5420, 19, 85.1, "GREEN"));
        grids.add(createGrid("grid_04", "金凤乡", 116.4220, 39.8650, 4310, 22, 74.8, "RED"));
        grids.add(createGrid("grid_05", "青石街道", 116.3650, 39.9350, 7810, 18, 86.4, "GREEN"));
        grids.add(createGrid("grid_06", "大河镇", 116.4520, 39.8820, 3780, 9, 82.0, "GREEN"));
        result.put("grids", grids);

        // 2. 医疗机构与公卫服务点位
        List<Map<String, Object>> institutions = new ArrayList<>();
        institutions.add(createInstitution("inst_01", "县人民医院 (医总院)", "HOSPITAL_3A", 116.4050, 39.9060, 48));
        institutions.add(createInstitution("inst_02", "县中医院", "HOSPITAL_2A", 116.4120, 39.9010, 26));
        institutions.add(createInstitution("inst_03", "城关社区卫生服务中心", "COMMUNITY_CENTER", 116.4080, 39.9030, 16));
        institutions.add(createInstitution("inst_04", "龙山镇中心卫生院", "HEALTH_CENTER", 116.4340, 39.9200, 12));
        institutions.add(createInstitution("inst_05", "清溪镇卫生院", "HEALTH_CENTER", 116.3800, 39.8910, 10));
        result.put("institutions", institutions);

        // 3. 高危热力点 (用于 Heatmap)
        List<Map<String, Object>> heatmapPoints = new ArrayList<>();
        double[][] bases = {
            {116.4075, 39.9040}, {116.4350, 39.9210}, {116.3810, 39.8920},
            {116.4220, 39.8650}, {116.3650, 39.9350}, {116.4520, 39.8820}
        };
        for (double[] base : bases) {
            for (int i = 0; i < 20; i++) {
                Map<String, Object> pt = new HashMap<>();
                pt.put("lng", base[0] + (Math.random() - 0.5) * 0.02);
                pt.put("lat", base[1] + (Math.random() - 0.5) * 0.02);
                pt.put("count", (int) (Math.random() * 80 + 20));
                heatmapPoints.add(pt);
            }
        }
        result.put("heatmapPoints", heatmapPoints);

        return result;
    }

    private Map<String, Object> createGrid(String id, String name, double lng, double lat,
                                          int patientCount, int warningCount, double controlRate, String riskLevel) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", id);
        m.put("name", name);
        m.put("lng", lng);
        m.put("lat", lat);
        m.put("patientCount", patientCount);
        m.put("warningCount", warningCount);
        m.put("controlRate", controlRate);
        m.put("riskLevel", riskLevel);
        return m;
    }

    private Map<String, Object> createInstitution(String id, String name, String type, double lng, double lat, int doctorCount) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", id);
        m.put("name", name);
        m.put("type", type);
        m.put("lng", lng);
        m.put("lat", lat);
        m.put("doctorCount", doctorCount);
        return m;
    }
}
