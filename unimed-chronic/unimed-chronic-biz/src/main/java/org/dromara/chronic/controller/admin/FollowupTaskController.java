package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChFollowupSubmitBo;
import org.dromara.chronic.domain.bo.ChFollowupTaskAssignBo;
import org.dromara.chronic.domain.bo.ChFollowupTaskClaimBo;
import org.dromara.chronic.domain.vo.ChFollowupTaskDetailVo;
import org.dromara.chronic.domain.vo.ChFollowupTaskVo;
import org.dromara.chronic.manager.FollowupManager;
import org.dromara.chronic.service.IChFollowupService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 随访任务控制器
 *
 * @author unimed
 */
@Tag(name = "慢病管理-随访任务")
@Validated
@RestController
@RequiredArgsConstructor
public class FollowupTaskController extends BaseController {

    private final IChFollowupService followupService;
    private final FollowupManager followupManager;

    @Operation(summary = "分页查询随访任务")
    @SaCheckPermission("chronic:followup-task:list")
    @GetMapping("/chronic/admin/followup-task/page")
    public TableDataInfo<ChFollowupTaskVo> page(@Parameter(description = "患者ID") @RequestParam(required = false) Long patientId,
                                                @Parameter(description = "执行人用户ID") @RequestParam(required = false) Long assigneeUserId,
                                                @Parameter(description = "任务状态") @RequestParam(required = false) String taskStatus,
                                                @Parameter(description = "随访方式(ONLINE/OFFLINE/PHONE)") @RequestParam(required = false) String visitType,
                                                @Parameter(description = "计划日期起始(yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date beginDate,
                                                @Parameter(description = "计划日期截止(yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                                                PageQuery pageQuery) {
        return followupService.queryTaskPage(patientId, assigneeUserId, taskStatus, visitType, beginDate, endDate, pageQuery);
    }

    @Operation(summary = "分页查询随访任务池")
    @SaCheckPermission("chronic:followup-task:pool")
    @GetMapping("/chronic/admin/followup-task/pool")
    public TableDataInfo<ChFollowupTaskVo> poolPage(@Parameter(description = "病种编码") @RequestParam(required = false) String diseaseCode,
                                                    @Parameter(description = "随访方式(ONLINE/OFFLINE/PHONE)") @RequestParam(required = false) String visitType,
                                                    PageQuery pageQuery) {
        return followupManager.queryTaskPoolPage(diseaseCode, visitType, pageQuery);
    }

    @Operation(summary = "查询随访任务详情")
    @SaCheckPermission("chronic:followup-task:list")
    @GetMapping("/chronic/admin/followup-task/{taskId}")
    public R<ChFollowupTaskDetailVo> detail(@Parameter(description = "任务ID") @PathVariable Long taskId) {
        return R.ok(followupService.queryTaskDetail(taskId, null, null));
    }

    @Operation(summary = "查询患者待办随访任务")
    @SaCheckPermission("chronic:followup-task:list")
    @GetMapping("/chronic/admin/patient/{patientId}/followup-tasks")
    public R<List<ChFollowupTaskVo>> patientTasks(@Parameter(description = "患者ID") @PathVariable Long patientId) {
        return R.ok(followupService.queryPatientTasks(patientId));
    }

    @Operation(summary = "单个认领随访任务")
    @SaCheckPermission("chronic:followup-task:claim")
    @Log(title = "认领随访任务", businessType = BusinessType.UPDATE)
    @PostMapping("/chronic/admin/followup-task/{taskId}/claim")
    public R<Void> claim(@Parameter(description = "任务ID") @PathVariable Long taskId) {
        followupManager.claimTask(taskId, LoginHelper.getUserId());
        return R.ok();
    }

    @Operation(summary = "批量认领随访任务")
    @SaCheckPermission("chronic:followup-task:claim")
    @Log(title = "批量认领随访任务", businessType = BusinessType.UPDATE)
    @PostMapping("/chronic/admin/followup-task/batch-claim")
    public R<Void> batchClaim(@Validated @RequestBody ChFollowupTaskClaimBo bo) {
        followupManager.batchClaimTasks(bo.getTaskIds(), LoginHelper.getUserId());
        return R.ok();
    }

    @Operation(summary = "指派随访任务")
    @SaCheckPermission("chronic:followup-task:assign")
    @Log(title = "指派随访任务", businessType = BusinessType.UPDATE)
    @PutMapping("/chronic/admin/followup-task/{taskId}/assign")
    public R<Void> assign(@Parameter(description = "任务ID") @PathVariable Long taskId,
                          @Parameter(description = "执行人用户ID") @RequestParam Long assigneeUserId) {
        followupService.assignTask(taskId, assigneeUserId);
        return R.ok();
    }

    @Operation(summary = "批量指派随访任务")
    @SaCheckPermission("chronic:followup-task:batch-assign")
    @Log(title = "批量指派随访任务", businessType = BusinessType.UPDATE)
    @PostMapping("/chronic/admin/followup-task/batch-assign")
    public R<Void> batchAssign(@Validated @RequestBody ChFollowupTaskAssignBo bo) {
        followupManager.batchAssignTasks(bo.getTaskIds(), bo.getAssigneeUserId());
        return R.ok();
    }

    @Operation(summary = "退回随访任务池")
    @SaCheckPermission("chronic:followup-task:release")
    @Log(title = "退回随访任务池", businessType = BusinessType.UPDATE)
    @PutMapping("/chronic/admin/followup-task/{taskId}/release")
    public R<Void> release(@Parameter(description = "任务ID") @PathVariable Long taskId) {
        followupManager.releaseTask(taskId, LoginHelper.getUserId());
        return R.ok();
    }

    @Operation(summary = "取消随访任务")
    @SaCheckPermission("chronic:followup-task:cancel")
    @Log(title = "取消随访任务", businessType = BusinessType.UPDATE)
    @PutMapping("/chronic/admin/followup-task/{taskId}/cancel")
    public R<Void> cancel(@Parameter(description = "任务ID") @PathVariable Long taskId) {
        followupService.cancelTask(taskId);
        return R.ok();
    }

    @Operation(summary = "完成随访任务")
    @SaCheckPermission("chronic:followup-task:complete")
    @Log(title = "完成随访任务", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PostMapping("/chronic/admin/followup-task/{taskId}/complete")
    public R<Long> complete(@Parameter(description = "任务ID") @PathVariable Long taskId,
                            @Validated @RequestBody ChFollowupSubmitBo bo) {
        return R.ok(followupManager.completeTask(taskId, bo, null, null,
            LoginHelper.getUserId(), "OFFLINE"));
    }

    @Operation(summary = "发送随访提醒通知")
    @SaCheckPermission("chronic:followup-task:remind")
    @Log(title = "发送随访提醒", businessType = BusinessType.UPDATE)
    @PostMapping("/chronic/admin/followup-task/{taskId}/remind")
    public R<Void> remind(@Parameter(description = "任务ID") @PathVariable Long taskId) {
        followupService.sendTaskRemind(taskId, LoginHelper.getUserId());
        return R.ok();
    }
}
