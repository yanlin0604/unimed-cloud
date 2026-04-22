package org.dromara.chronic.controller.doctor;

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
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 医生端调档申请
 *
 * @author unimed
 */
@Tag(name = "慢病管理-医生端调档申请")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/doctor")
public class DoctorArchiveShareController extends BaseController {

    private final ArchiveShareManager archiveShareManager;

    @Operation(summary = "发起调档申请")
    @SaCheckPermission("chronic:doctor:archive-share:add")
    @Log(title = "调档申请", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/patient/{patientId}/archive-share/apply")
    public R<Long> apply(
            @Parameter(description = "患者ID") @PathVariable Long patientId,
            @Validated @RequestBody ChArchiveShareApplyBo bo) {
        bo.setPatientId(patientId);
        return R.ok(archiveShareManager.createApply(bo));
    }

    @Operation(summary = "患者调档申请列表")
    @SaCheckPermission("chronic:doctor:archive-share:list")
    @GetMapping("/patient/{patientId}/archive-share/page")
    public TableDataInfo<ChArchiveShareApplyVo> page(
            @PathVariable Long patientId,
            ChArchiveShareApplyBo bo,
            PageQuery pageQuery) {
        bo.setPatientId(patientId);
        return archiveShareManager.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "撤回调档申请")
    @SaCheckPermission("chronic:doctor:archive-share:withdraw")
    @Log(title = "调档申请撤回", businessType = BusinessType.UPDATE)
    @PostMapping("/archive-share/{applyId}/withdraw")
    public R<Void> withdraw(@Parameter(description = "申请ID") @PathVariable Long applyId) {
        return R.ok(archiveShareManager.withdraw(applyId));
    }
}