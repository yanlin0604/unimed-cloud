package org.dromara.chronic.controller.admin;

import org.dromara.common.web.core.BaseController;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChFollowupPlanBatchBo;
import org.dromara.chronic.domain.bo.ChFollowupPlanBo;
import org.dromara.chronic.domain.vo.ChFollowupPlanVo;
import org.dromara.chronic.domain.vo.ChFollowupRecordVo;
import org.dromara.chronic.manager.FollowupManager;
import org.dromara.chronic.service.IChFollowupService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 随访计划控制器
 *
 * @author unimed
 */
@Tag(name = "慢病管理-随访计划")
@Validated
@RestController
@RequiredArgsConstructor
public class FollowupPlanController extends BaseController {

    private final FollowupManager followupManager;
    private final IChFollowupService followupService;

    @Operation(summary = "新建随访计划")
    @SaCheckPermission("chronic:followup-plan:add")
    @Log(title = "随访计划", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/admin/followup-plan")
    public R<Long> create(@Validated @RequestBody ChFollowupPlanBo bo) {
        return R.ok(followupManager.createPlan(bo));
    }

    @Operation(summary = "修改随访计划")
    @SaCheckPermission("chronic:followup-plan:edit")
    @Log(title = "随访计划", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping("/chronic/admin/followup-plan/{planId}")
    public R<Void> update(@Parameter(description = "计划ID") @PathVariable Long planId,
                          @Validated @RequestBody ChFollowupPlanBo bo) {
        bo.setPlanId(planId);
        followupManager.updatePlan(bo);
        return R.ok();
    }

    @Operation(summary = "批量修改随访计划")
    @SaCheckPermission("chronic:followup-plan:edit")
    @Log(title = "批量随访计划", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping("/chronic/admin/followup-plan/batch")
    public R<Void> updateBatch(@RequestBody @NotEmpty(message = "随访计划列表不能为空")
                               List<@Valid ChFollowupPlanBo> planList) {
        followupManager.updateBatchPlans(planList);
        return R.ok();
    }

    @Operation(summary = "批量新建随访计划")
    @SaCheckPermission("chronic:followup-plan:add")
    @Log(title = "批量随访计划", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/admin/followup-plan/batch")
    public R<List<Long>> createBatch(@Validated @RequestBody ChFollowupPlanBatchBo bo) {
        return R.ok(followupManager.createBatchPlans(bo));
    }

    @Operation(summary = "查询当前随访计划")
    @SaCheckPermission("chronic:followup-plan:list")
    @GetMapping("/chronic/admin/patient/{patientId}/followup-plans")
    public R<ChFollowupPlanVo> current(@Parameter(description = "患者ID") @PathVariable Long patientId) {
        return R.ok(followupService.queryCurrentPlan(patientId));
    }

    @Operation(summary = "分页查询随访计划")
    @SaCheckPermission("chronic:followup-plan:list")
    @GetMapping("/chronic/admin/followup-plan/page")
    public TableDataInfo<ChFollowupPlanVo> page(@Parameter(description = "患者ID") @RequestParam(required = false) Long patientId,
                                                @Parameter(description = "病种编码") @RequestParam(required = false) String diseaseCode,
                                                @Parameter(description = "执行医生用户ID") @RequestParam(required = false) Long assigneeUserId,
                                                @Parameter(description = "计划状态") @RequestParam(required = false) String planStatus,
                                                PageQuery pageQuery) {
        return followupService.queryPlanPage(patientId, diseaseCode, assigneeUserId, planStatus, pageQuery);
    }

    @Operation(summary = "更新随访计划状态")
    @SaCheckPermission("chronic:followup-plan:status")
    @Log(title = "随访计划状态", businessType = BusinessType.UPDATE)
    @PutMapping("/chronic/admin/followup-plan/{planId}/status")
    public R<Void> status(@Parameter(description = "计划ID") @PathVariable Long planId,
                          @Parameter(description = "计划状态") @RequestParam String planStatus) {
        followupService.updatePlanStatus(planId, planStatus);
        return R.ok();
    }

    @Operation(summary = "批量更新随访计划状态")
    @SaCheckPermission("chronic:followup-plan:status")
    @Log(title = "批量随访计划状态", businessType = BusinessType.UPDATE)
    @PutMapping("/chronic/admin/followup-plan/batch/status")
    public R<Void> batchStatus(@Parameter(description = "计划ID列表") @RequestParam List<Long> planIds,
                               @Parameter(description = "计划状态") @RequestParam String planStatus) {
        followupManager.updateBatchPlanStatus(planIds, planStatus);
        return R.ok();
    }

    @Operation(summary = "查询随访记录列表")
    @SaCheckPermission("chronic:followup-record:list")
    @GetMapping("/chronic/admin/patient/{patientId}/followup-records")
    public R<List<ChFollowupRecordVo>> records(@Parameter(description = "患者ID") @PathVariable Long patientId) {
        return R.ok(followupService.queryRecordList(patientId));
    }

    @Operation(summary = "分页查询随访记录")
    @SaCheckPermission("chronic:followup-record:list")
    @GetMapping("/chronic/admin/followup-record/page")
    public TableDataInfo<ChFollowupRecordVo> recordPage(@Parameter(description = "患者ID") @RequestParam(required = false) Long patientId,
                                                        @Parameter(description = "随访方式") @RequestParam(required = false) String visitType,
                                                        PageQuery pageQuery) {
        return followupService.queryRecordPage(patientId, visitType, pageQuery);
    }

    @Operation(summary = "随访记录详情")
    @SaCheckPermission("chronic:followup-record:list")
    @GetMapping("/chronic/admin/followup-record/{recordId}")
    public R<ChFollowupRecordVo> recordDetail(@Parameter(description = "记录ID") @PathVariable Long recordId) {
        return R.ok(followupService.queryRecordDetail(recordId));
    }
}
