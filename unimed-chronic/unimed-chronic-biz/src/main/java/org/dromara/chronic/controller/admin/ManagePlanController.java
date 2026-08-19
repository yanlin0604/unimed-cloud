package org.dromara.chronic.controller.admin;

import org.dromara.common.web.core.BaseController;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChManagePlanBo;
import org.dromara.chronic.domain.vo.ChManagePlanVo;
import org.dromara.chronic.service.IChManagePlanService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理方案控制器
 *
 * @author unimed
 */
@Tag(name = "慢病管理-管理方案")
@Validated
@RestController
@RequiredArgsConstructor
public class ManagePlanController extends BaseController {

    private final IChManagePlanService managePlanService;

    @Operation(summary = "新建管理方案")
    @SaCheckPermission("chronic:plan:add")
    @Log(title = "管理方案", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/admin/patient/{patientId}/plans")
    public R<Long> create(@Parameter(description = "患者ID") @PathVariable Long patientId, @Validated @RequestBody ChManagePlanBo bo) {
        bo.setPatientId(patientId);
        return R.ok(managePlanService.createPlan(bo));
    }

    @Operation(summary = "修改管理方案")
    @SaCheckPermission("chronic:plan:edit")
    @Log(title = "管理方案", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping("/chronic/admin/plans/{planId}")
    public R<Void> update(@Parameter(description = "方案ID") @PathVariable Long planId, @Validated @RequestBody ChManagePlanBo bo) {
        bo.setPlanId(planId);
        return managePlanService.updatePlan(bo) ? R.ok() : R.fail();
    }

    @Operation(summary = "启用管理方案")
    @SaCheckPermission("chronic:plan:enable")
    @Log(title = "启用管理方案", businessType = BusinessType.UPDATE)
    @PostMapping("/chronic/admin/plans/{planId}/enable")
    public R<Void> enable(@Parameter(description = "方案ID") @PathVariable Long planId) {
        return managePlanService.enablePlan(planId) ? R.ok() : R.fail();
    }

    @Operation(summary = "停用管理方案")
    @SaCheckPermission("chronic:plan:disable")
    @Log(title = "停用管理方案", businessType = BusinessType.UPDATE)
    @PostMapping("/chronic/admin/plans/{planId}/disable")
    public R<Void> disable(@Parameter(description = "方案ID") @PathVariable Long planId) {
        return managePlanService.disablePlan(planId) ? R.ok() : R.fail();
    }

    @Operation(summary = "查询患者管理方案列表")
    @SaCheckPermission("chronic:plan:list")
    @GetMapping("/chronic/admin/patient/{patientId}/plans")
    public R<List<ChManagePlanVo>> list(@Parameter(description = "患者ID") @PathVariable Long patientId) {
        return R.ok(managePlanService.queryByPatientId(patientId));
    }
}
