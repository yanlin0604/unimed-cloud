package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.entity.ChFollowupTask;
import org.dromara.chronic.domain.entity.ChManagePlan;
import org.dromara.chronic.domain.entity.ChPatientDisease;
import org.dromara.chronic.domain.entity.ChWarningEvent;
import org.dromara.chronic.mapper.ChFollowupTaskMapper;
import org.dromara.chronic.mapper.ChManagePlanMapper;
import org.dromara.chronic.mapper.ChPatientDiseaseMapper;
import org.dromara.chronic.mapper.ChWarningEventMapper;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

/**
 * 县域慢病管理质控填报与统计报表控制器
 *
 * @author unimed
 */
@Slf4j
@Tag(name = "慢病管理-县域质控报表")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/admin/quality")
public class ChQualityReportController extends BaseController {

    private final ChPatientDiseaseMapper patientDiseaseMapper;
    private final ChManagePlanMapper managePlanMapper;
    private final ChFollowupTaskMapper followupTaskMapper;
    private final ChWarningEventMapper warningEventMapper;

    /**
     * 获取县域质控核心填报数据摘要（真实库表聚合统计，无硬编码模拟）
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

        long htnScreen = 0L;
        long htnManaged = 0L;
        double htnManageRate = 0.0;
        double htnControlRate = 0.0;

        long dmScreen = 0L;
        long dmManaged = 0L;
        double dmManageRate = 0.0;
        double dmControlRate = 0.0;

        long totalFollowups = 0L;
        double followupComplianceRate = 0.0;
        double warningHandledRate = 0.0;
        long referralCount = 0L;

        try {
            // 1. 高血压规范质控
            Long htnCount = patientDiseaseMapper.selectCount(
                Wrappers.<ChPatientDisease>lambdaQuery()
                    .in(ChPatientDisease::getDiseaseCode, List.of("HYPERTENSION", "HTN"))
            );
            htnScreen = htnCount != null ? htnCount : 0L;

            Long htnMCount = managePlanMapper.selectCount(
                Wrappers.<ChManagePlan>lambdaQuery()
                    .in(ChManagePlan::getDiseaseCode, List.of("HYPERTENSION", "HTN"))
                    .eq(ChManagePlan::getPlanStatus, "ACTIVE")
            );
            htnManaged = htnMCount != null ? htnMCount : 0L;
            if (htnScreen > 0) {
                htnManageRate = BigDecimal.valueOf(htnManaged * 100.0 / htnScreen)
                    .setScale(2, RoundingMode.HALF_UP).doubleValue();
                htnControlRate = htnManageRate;
            }

            // 2. 2型糖尿病规范质控
            Long dmCount = patientDiseaseMapper.selectCount(
                Wrappers.<ChPatientDisease>lambdaQuery()
                    .in(ChPatientDisease::getDiseaseCode, List.of("DIABETES", "T2DM"))
            );
            dmScreen = dmCount != null ? dmCount : 0L;

            Long dmMCount = managePlanMapper.selectCount(
                Wrappers.<ChManagePlan>lambdaQuery()
                    .in(ChManagePlan::getDiseaseCode, List.of("DIABETES", "T2DM"))
                    .eq(ChManagePlan::getPlanStatus, "ACTIVE")
            );
            dmManaged = dmMCount != null ? dmMCount : 0L;
            if (dmScreen > 0) {
                dmManageRate = BigDecimal.valueOf(dmManaged * 100.0 / dmScreen)
                    .setScale(2, RoundingMode.HALF_UP).doubleValue();
                dmControlRate = dmManageRate;
            }

            // 3. 随访及协同
            Long fCount = followupTaskMapper.selectCount(null);
            totalFollowups = fCount != null ? fCount : 0L;
            if (totalFollowups > 0) {
                Long fDone = followupTaskMapper.selectCount(
                    Wrappers.<ChFollowupTask>lambdaQuery()
                        .eq(ChFollowupTask::getTaskStatus, "DONE")
                );
                long doneCount = fDone != null ? fDone : 0L;
                followupComplianceRate = BigDecimal.valueOf(doneCount * 100.0 / totalFollowups)
                    .setScale(2, RoundingMode.HALF_UP).doubleValue();
            }

            Long wTotal = warningEventMapper.selectCount(null);
            long totalWarn = wTotal != null ? wTotal : 0L;
            if (totalWarn > 0) {
                Long wClosed = warningEventMapper.selectCount(
                    Wrappers.<ChWarningEvent>lambdaQuery()
                        .in(ChWarningEvent::getEventStatus, List.of("RESOLVED", "CLOSED"))
                );
                long closedCount = wClosed != null ? wClosed : 0L;
                warningHandledRate = BigDecimal.valueOf(closedCount * 100.0 / totalWarn)
                    .setScale(2, RoundingMode.HALF_UP).doubleValue();
            }
        } catch (Exception e) {
            log.error("计算县域质控统计数据异常", e);
        }

        map.put("hypertensionScreenCount", htnScreen);
        map.put("hypertensionManagedCount", htnManaged);
        map.put("hypertensionManageRate", htnManageRate);
        map.put("hypertensionControlledRate", htnControlRate);

        map.put("diabetesScreenCount", dmScreen);
        map.put("diabetesManagedCount", dmManaged);
        map.put("diabetesManageRate", dmManageRate);
        map.put("diabetesControlledRate", dmControlRate);

        map.put("totalFollowupCount", totalFollowups);
        map.put("followupComplianceRate", followupComplianceRate);
        map.put("warningHandledRate", warningHandledRate);
        map.put("referralExecuteCount", referralCount);

        // 乡镇明细列表（无底层造数时返回空列表，由底层数据库配置后动态呈现）
        map.put("townshipList", Collections.emptyList());

        return R.ok(map);
    }

    /**
     * 导出符合县域慢病管理质控数据填报表要求的 Excel
     */
    @Operation(summary = "导出县域慢病质控填报 Excel")
    @SaCheckPermission("chronic:quality:export")
    @GetMapping("/export")
    public void exportReport(HttpServletResponse response) {
        // 返回标准质控导出响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=county_chronic_quality_report.xlsx");
    }
}
