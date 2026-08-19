package org.dromara.chronic.controller.admin;

import org.dromara.common.web.core.BaseController;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChArchiveShareApplyBo;
import org.dromara.chronic.domain.bo.ChReferralRecordBo;
import org.dromara.chronic.domain.vo.ChArchiveShareApplyVo;
import org.dromara.chronic.domain.vo.ChReferralRecordVo;
import org.dromara.chronic.service.IChReferralService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 转诊管理后台
 *
 * @author unimed
 */
@Tag(name = "慢病管理-转诊管理")
@Validated
@RestController
@RequiredArgsConstructor
public class ReferralController extends BaseController {

    private final IChReferralService referralService;

    /**
     * R4: 新增转诊 —— 注入登录用户上下文
     */
    @Operation(summary = "新增转诊")
    @SaCheckPermission("chronic:referral:add")
    @Log(title = "转诊", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/admin/referral")
    public R<Long> add(@Validated @RequestBody ChReferralRecordBo bo) {
        bo.setReferralUserId(LoginHelper.getUserId());
        return R.ok(referralService.createReferral(bo));
    }

    @Operation(summary = "分页查询转诊")
    @SaCheckPermission("chronic:referral:list")
    @GetMapping("/chronic/admin/referral/page")
    public TableDataInfo<ChReferralRecordVo> page(ChReferralRecordBo bo, PageQuery pageQuery) {
        return referralService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "转诊详情")
    @SaCheckPermission("chronic:referral:query")
    @GetMapping("/chronic/admin/referral/{referralId}")
    public R<ChReferralRecordVo> detail(@Parameter(description = "转诊ID") @PathVariable Long referralId) {
        return R.ok(referralService.queryById(referralId));
    }

    @Operation(summary = "更新转诊状态")
    @SaCheckPermission("chronic:referral:status")
    @Log(title = "转诊状态", businessType = BusinessType.UPDATE)
    @PutMapping("/chronic/admin/referral/{referralId}/status")
    public R<Void> updateStatus(@Parameter(description = "转诊ID") @PathVariable Long referralId, @Parameter(description = "状态") @RequestParam String status) {
        return R.ok(referralService.updateStatus(referralId, status));
    }

    @Operation(summary = "申请调档")
    @SaCheckPermission("chronic:archive-share:add")
    @Log(title = "调档申请", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/admin/archive-share")
    public R<Long> applyArchiveShare(@Validated @RequestBody ChArchiveShareApplyBo bo) {
        return R.ok(referralService.applyArchiveShare(bo));
    }

    @Operation(summary = "审批调档申请")
    @SaCheckPermission("chronic:archive-share:approve")
    @Log(title = "调档审批", businessType = BusinessType.UPDATE)
    @PutMapping("/chronic/admin/archive-share/{id}/approve")
    public R<Void> approveArchiveShare(@Parameter(description = "申请ID") @PathVariable Long id, @Parameter(description = "审批状态") @RequestParam String approvalStatus) {
        return R.ok(referralService.approveArchiveShare(id, approvalStatus));
    }
}
