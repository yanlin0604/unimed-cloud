package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChAuditLogBo;
import org.dromara.chronic.domain.bo.ChFileAttachmentBo;
import org.dromara.chronic.domain.vo.ChAuditLogVo;
import org.dromara.chronic.domain.vo.ChFileAttachmentVo;
import org.dromara.chronic.service.IChAuditLogService;
import org.dromara.chronic.service.IChFileAttachmentService;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 审计日志与附件管理后台
 *
 * @author unimed
 */
@Tag(name = "慢病管理-审计日志与附件")
@Validated
@RestController
@RequiredArgsConstructor
public class AuditLogController extends BaseController {

    private final IChAuditLogService auditLogService;
    private final IChFileAttachmentService fileAttachmentService;

    // ===================== 审计日志 =====================

    @SaCheckPermission("chronic:audit:list")
    @Operation(summary = "审计日志分页查询")
    @GetMapping("/chronic/admin/audit-log/page")
    public TableDataInfo<ChAuditLogVo> auditLogPage(ChAuditLogBo bo, PageQuery pageQuery) {
        return auditLogService.queryPageList(bo, pageQuery);
    }

    @SaCheckPermission("chronic:audit:query")
    @Operation(summary = "审计日志详情")
    @GetMapping("/chronic/admin/audit-log/{id}")
    public R<ChAuditLogVo> auditLogDetail(@PathVariable @Parameter(description = "审计ID") Long id) {
        return R.ok(auditLogService.queryById(id));
    }

    // ===================== 附件管理 =====================

    @SaCheckPermission("chronic:attachment:list")
    @Operation(summary = "附件分页查询")
    @GetMapping("/chronic/admin/attachment/page")
    public TableDataInfo<ChFileAttachmentVo> attachmentPage(ChFileAttachmentBo bo, PageQuery pageQuery) {
        return fileAttachmentService.queryPageList(bo, pageQuery);
    }

    @SaCheckPermission("chronic:attachment:query")
    @Operation(summary = "附件详情")
    @GetMapping("/chronic/admin/attachment/{fileId}")
    public R<ChFileAttachmentVo> attachmentDetail(@PathVariable @Parameter(description = "文件ID") Long fileId) {
        return R.ok(fileAttachmentService.queryById(fileId));
    }

    @SaCheckPermission("chronic:attachment:add")
    @Operation(summary = "新增附件")
    @PostMapping("/chronic/admin/attachment")
    public R<Void> addAttachment(@Validated @RequestBody ChFileAttachmentBo bo) {
        return toAjax(fileAttachmentService.insertByBo(bo) != null);
    }

    @SaCheckPermission("chronic:attachment:edit")
    @Operation(summary = "修改附件")
    @PutMapping("/chronic/admin/attachment")
    public R<Void> editAttachment(@Validated @RequestBody ChFileAttachmentBo bo) {
        return toAjax(fileAttachmentService.updateByBo(bo));
    }

    @SaCheckPermission("chronic:attachment:remove")
    @Operation(summary = "删除附件")
    @DeleteMapping("/chronic/admin/attachment/{fileId}")
    public R<Void> removeAttachment(@PathVariable @Parameter(description = "文件ID") Long fileId) {
        return toAjax(fileAttachmentService.deleteById(fileId));
    }

    @SaCheckPermission("chronic:attachment:list")
    @Operation(summary = "根据业务查询附件")
    @GetMapping("/chronic/admin/attachment/biz")
    public R<List<ChFileAttachmentVo>> listByBiz(
        @RequestParam @Parameter(description = "业务类型") String bizType,
        @RequestParam @Parameter(description = "业务ID") Long bizId) {
        return R.ok(fileAttachmentService.queryByBiz(bizType, bizId));
    }
}
