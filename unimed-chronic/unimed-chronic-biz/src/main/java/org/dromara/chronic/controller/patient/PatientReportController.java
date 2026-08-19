package org.dromara.chronic.controller.patient;

import org.dromara.common.web.core.BaseController;
import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.vo.ChReportInstanceVo;
import org.dromara.chronic.service.IChReportService;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.chronic.support.PatientContextHelper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
public class PatientReportController extends BaseController {

    private final IChReportService reportService;
    private final PatientContextHelper patientContextHelper;

    @Operation(summary = "查询我的报告列表")
    @GetMapping("/chronic/patient/report/list")
    public R<List<ChReportInstanceVo>> myList() {
        Long patientId = patientContextHelper.getCurrentPatientId();
        return R.ok(reportService.queryByPatientId(patientId));
    }

    @Operation(summary = "报告详情")
    @GetMapping("/chronic/patient/report/{reportId}")
    public R<ChReportInstanceVo> detail(@Parameter(description = "报告ID") @PathVariable Long reportId) {
        // 归属校验与下方 download 保持一致：原实现直接按 reportId 返回，可枚举读取他人健康报告
        Long patientId = patientContextHelper.getCurrentPatientId();
        ChReportInstanceVo report = reportService.queryReportById(reportId);
        if (report == null || !patientId.equals(report.getPatientId())) {
            throw new ServiceException("报告不存在或无权访问");
        }
        return R.ok(report);
    }

    @Operation(summary = "下载报告PDF")
    @Log(title = "报告下载", businessType = BusinessType.EXPORT)
    @GetMapping("/chronic/patient/report/{reportId}/download")
    public void download(@Parameter(description = "报告ID") @PathVariable Long reportId, HttpServletResponse response) throws Exception {
        Long patientId = patientContextHelper.getCurrentPatientId();
        ChReportInstanceVo report = reportService.queryReportById(reportId);
        if (report == null || !patientId.equals(report.getPatientId())) {
            throw new ServiceException("报告不存在或无权访问");
        }
        if (report.getPdfUrl() == null) {
            throw new ServiceException("报告未关联PDF文件");
        }
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=report_" + reportId + ".pdf");
        try (java.io.InputStream in = new java.net.URL(report.getPdfUrl()).openStream();
             java.io.OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}
