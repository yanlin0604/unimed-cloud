package org.dromara.chronic.controller.admin;

import org.dromara.common.web.core.BaseController;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChReportInstanceBo;
import org.dromara.chronic.domain.vo.ChReportInstanceVo;
import org.dromara.chronic.manager.ReportGenerateManager;
import org.dromara.chronic.service.IChReportService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 报告实例管理
 *
 * @author unimed
 */
@Tag(name = "慢病管理-报告实例")
@Validated
@RestController
@RequiredArgsConstructor
public class ReportController extends BaseController {

    private final ReportGenerateManager reportGenerateManager;
    private final IChReportService reportService;

    @SaCheckPermission("chronic:report:generate")
    @Log(title = "报告生成", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @Operation(summary = "生成报告")
    @PostMapping("/chronic/admin/report/generate")
    public R<Long> generate(@Validated @RequestBody ChReportInstanceBo bo) {
        return R.ok(reportGenerateManager.generateAndSign(bo));
    }

    @SaCheckPermission("chronic:report:list")
    @Operation(summary = "分页查询报告列表")
    @GetMapping("/chronic/admin/report/page")
    public TableDataInfo<ChReportInstanceVo> page(ChReportInstanceBo bo, PageQuery pageQuery) {
        return reportService.queryReportPageList(bo, pageQuery);
    }

    @SaCheckPermission("chronic:report:query")
    @Operation(summary = "获取报告详情")
    @GetMapping("/chronic/admin/report/{reportId}")
    public R<ChReportInstanceVo> detail(@Parameter(description = "报告ID") @PathVariable Long reportId) {
        return R.ok(reportGenerateManager.queryDetail(reportId));
    }

    @SaCheckPermission("chronic:report:push")
    @Log(title = "报告推送", businessType = BusinessType.UPDATE)
    @Operation(summary = "推送报告")
    @PutMapping("/chronic/admin/report/{reportId}/push")
    public R<Void> push(@Parameter(description = "报告ID") @PathVariable Long reportId,
                        @Parameter(description = "推送渠道") @RequestParam(defaultValue = "WECHAT") String channel) {
        return R.ok(reportService.pushReport(reportId, channel));
    }
}
