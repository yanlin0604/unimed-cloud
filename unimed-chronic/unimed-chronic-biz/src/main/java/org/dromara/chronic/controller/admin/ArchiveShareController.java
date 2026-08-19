package org.dromara.chronic.controller.admin;

import org.dromara.common.web.core.BaseController;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChArchiveShareApplyBo;
import org.dromara.chronic.domain.vo.ChArchiveShareApplyVo;
import org.dromara.chronic.manager.ArchiveShareManager;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 调档申请管理后台
 *
 * @author unimed
 */
@Tag(name = "慢病管理-调档申请")
@Validated
@RestController
@RequiredArgsConstructor
public class ArchiveShareController extends BaseController {

    private final ArchiveShareManager archiveShareManager;

    @Operation(summary = "发起调档申请")
    @SaCheckPermission("chronic:archive-share:add")
    @Log(title = "调档申请", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/admin/archive-share/apply")
    public R<Long> apply(@Validated @RequestBody ChArchiveShareApplyBo bo) {
        return R.ok(archiveShareManager.createApply(bo));
    }

    @Operation(summary = "分页查询调档申请")
    @SaCheckPermission("chronic:archive-share:list")
    @GetMapping("/chronic/admin/archive-share/page")
    public TableDataInfo<ChArchiveShareApplyVo> page(ChArchiveShareApplyBo bo, PageQuery pageQuery) {
        return archiveShareManager.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "调档申请详情")
    @SaCheckPermission("chronic:archive-share:query")
    @GetMapping("/chronic/admin/archive-share/{applyId}")
    public R<ChArchiveShareApplyVo> detail(@Parameter(description = "申请ID") @PathVariable Long applyId) {
        return R.ok(archiveShareManager.queryById(applyId));
    }

    @Operation(summary = "撤回调档申请")
    @SaCheckPermission("chronic:archive-share:withdraw")
    @Log(title = "调档申请撤回", businessType = BusinessType.UPDATE)
    @PostMapping("/chronic/admin/archive-share/{applyId}/withdraw")
    public R<Void> withdraw(@Parameter(description = "申请ID") @PathVariable Long applyId) {
        return R.ok(archiveShareManager.withdraw(applyId));
    }

    @Operation(summary = "审批调档申请")
    @SaCheckPermission("chronic:archive-share:approve")
    @Log(title = "调档申请审批", businessType = BusinessType.UPDATE)
    @PutMapping("/chronic/admin/archive-share/{applyId}/approve")
    public R<Void> approve(
            @Parameter(description = "申请ID", required = true) @PathVariable Long applyId,
            @Parameter(description = "审批状态(APPROVED/REJECTED)", required = true) @RequestParam String approvalStatus,
            @Parameter(description = "审批意见") @RequestParam(required = false) String approvalOpinion) {
        return R.ok(archiveShareManager.approve(applyId, approvalStatus, approvalOpinion));
    }

    @Operation(summary = "工作流回调")
    @SaCheckPermission("chronic:archive-share:callback")
    @Log(title = "工作流回调", businessType = BusinessType.UPDATE)
    @PostMapping("/chronic/admin/archive-share/workflow-callback")
    public R<Void> workflowCallback(@RequestBody Map<String, Object> params) {
        Long applyId = Long.valueOf(params.get("applyId").toString());
        String status = params.get("status").toString();
        return R.ok(archiveShareManager.workflowCallback(applyId, status));
    }
}