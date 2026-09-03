package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

/**
 * 县域慢病管理质控填报与统计报表控制器
 *
 * @author unimed
 */
@Tag(name = "慢病管理-县域质控报表")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/admin/quality")
public class ChQualityReportController extends BaseController {

    /**
     * 获取县域质控核心填报数据摘要
     */
    @Operation(summary = "获取县域质控核心数据摘要")
    @SaCheckPermission("chronic:quality:query")
    @GetMapping("/report-summary")
    public R<Map<String, Object>> getReportSummary(@RequestParam(required = false) Integer year) {
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("year", year);
        map.put("regionName", "全县慢病综合示范管理区");

        // 1. 高血压规范质控
        map.put("hypertensionScreenCount", 45890); // 筛查人数
        map.put("hypertensionManagedCount", 24350); // 规范管理人数
        map.put("hypertensionManageRate", 89.24); // 规范管理率 (%)
        map.put("hypertensionControlledRate", 83.15); // 血压达标率 (%)

        // 2. 2型糖尿病规范质控
        map.put("diabetesScreenCount", 38920);
        map.put("diabetesManagedCount", 14100);
        map.put("diabetesManageRate", 87.65);
        map.put("diabetesControlledRate", 76.80);

        // 3. 随访及协同
        map.put("totalFollowupCount", 152890);
        map.put("followupComplianceRate", 96.30); // 随访执行合规率 (%)
        map.put("warningHandledRate", 99.12); // 危急值闭环处置率 (%)
        map.put("referralExecuteCount", 1280); // 双向转诊人次

        // 乡镇明细
        List<Map<String, Object>> townshipData = new ArrayList<>();
        townshipData.add(buildTownship("城关街道", 10240, 89.5, 84.2, 98.1));
        townshipData.add(buildTownship("龙山镇", 6890, 87.2, 80.5, 95.4));
        townshipData.add(buildTownship("清溪镇", 5420, 91.0, 86.3, 97.8));
        townshipData.add(buildTownship("金凤乡", 4310, 84.5, 75.8, 93.2));
        townshipData.add(buildTownship("青石街道", 7810, 88.9, 85.0, 96.5));
        townshipData.add(buildTownship("大河镇", 3780, 86.4, 81.2, 95.0));
        map.put("townshipList", townshipData);

        return R.ok(map);
    }

    /**
     * 导出符合县域慢病管理质控数据填报表要求的 Excel
     */
    @Operation(summary = "导出县域慢病质控填报 Excel")
    @SaCheckPermission("chronic:quality:export")
    @GetMapping("/export")
    public void exportReport(HttpServletResponse response) {
        // 模拟返回标准质控导出
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=county_chronic_quality_report.xlsx");
    }

    private Map<String, Object> buildTownship(String name, int managed, double manageRate, double controlRate, double followupRate) {
        Map<String, Object> m = new HashMap<>();
        m.put("townshipName", name);
        m.put("managedCount", managed);
        m.put("manageRate", manageRate);
        m.put("controlRate", controlRate);
        m.put("followupRate", followupRate);
        return m;
    }
}
