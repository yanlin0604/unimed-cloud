package org.dromara.chronic.controller.admin;

import org.dromara.common.web.core.BaseController;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChReportTemplateBo;
import org.dromara.chronic.domain.vo.ChReportTemplateVo;
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
 * 报告模板管理
 *
 * @author unimed
 */
@Tag(name = "慢病管理-报告模板")
@Validated
@RestController
@RequiredArgsConstructor
public class ReportTemplateController extends BaseController {

    private final IChReportService reportService;

    @Operation(summary = "新增报告模板")
    @SaCheckPermission("chronic:report-template:add")
    @Log(title = "报告模板", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/admin/report-template")
    public R<Long> addTemplate(@Validated @RequestBody ChReportTemplateBo bo) {
        return R.ok(reportService.createTemplate(bo));
    }

    @Operation(summary = "修改报告模板")
    @SaCheckPermission("chronic:report-template:edit")
    @Log(title = "报告模板", businessType = BusinessType.UPDATE)
    @PutMapping("/chronic/admin/report-template/{templateId}")
    public R<Void> editTemplate(@Parameter(description = "模板ID") @PathVariable Long templateId, @Validated @RequestBody ChReportTemplateBo bo) {
        bo.setTemplateId(templateId);
        return R.ok(reportService.updateTemplate(bo));
    }

    @Operation(summary = "分页查询报告模板")
    @SaCheckPermission("chronic:report-template:list")
    @GetMapping("/chronic/admin/report-template/page")
    public TableDataInfo<ChReportTemplateVo> templatePage(ChReportTemplateBo bo, PageQuery pageQuery) {
        return reportService.queryTemplatePageList(bo, pageQuery);
    }

    @Operation(summary = "报告模板详情")
    @SaCheckPermission("chronic:report-template:query")
    @GetMapping("/chronic/admin/report-template/{templateId}")
    public R<ChReportTemplateVo> templateDetail(@Parameter(description = "模板ID") @PathVariable Long templateId) {
        return R.ok(reportService.queryTemplateById(templateId));
    }
}
