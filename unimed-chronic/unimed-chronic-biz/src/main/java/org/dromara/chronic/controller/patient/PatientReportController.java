package org.dromara.chronic.controller.patient;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.vo.ChReportInstanceVo;
import org.dromara.chronic.service.IChReportService;
import org.dromara.common.core.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 患者端健康报告
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者端报告")
@SaCheckLogin
@Validated
@RestController
@RequiredArgsConstructor
public class PatientReportController {

    private final IChReportService reportService;

    @Operation(summary = "查询我的报告列表")
    @GetMapping("/chronic/patient/report/list")
    public R<List<ChReportInstanceVo>> myList(@Parameter(description = "患者ID") @RequestParam Long patientId) {
        return R.ok(reportService.queryByPatientId(patientId));
    }

    @Operation(summary = "报告详情")
    @GetMapping("/chronic/patient/report/{reportId}")
    public R<ChReportInstanceVo> detail(@Parameter(description = "报告ID") @PathVariable Long reportId) {
        return R.ok(reportService.queryReportById(reportId));
    }
}
