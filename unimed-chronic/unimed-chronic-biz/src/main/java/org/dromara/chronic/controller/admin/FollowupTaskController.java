package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChFollowupSubmitBo;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
public class FollowupTaskController {

    private final IChFollowupService followupService;
    private final FollowupManager followupManager;

    @Operation(summary = "分页查询随访任务")
    @SaCheckPermission("chronic:followup-task:list")
    @GetMapping("/chronic/admin/followup-task/page")
    public TableDataInfo<ChFollowupTaskVo> page(@Parameter(description = "患者ID") @RequestParam(required = false) Long patientId,
                                                @Parameter(description = "指派人ID") @RequestParam(required = false) Long assigneeUserId,
                                                @Parameter(description = "任务状态") @RequestParam(required = false) String taskStatus,
                                                @Parameter(description = "随访方式") @RequestParam(required = false) String visitType,
                                                PageQuery pageQuery) {
        return followupService.queryTaskPage(patientId, assigneeUserId, taskStatus, visitType, pageQuery);
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

    @Operation(summary = "指派随访任务")
    @SaCheckPermission("chronic:followup-task:assign")
    @Log(title = "指派随访任务", businessType = BusinessType.UPDATE)
    @PutMapping("/chronic/admin/followup-task/{taskId}/assign")
    public R<Void> assign(@Parameter(description = "任务ID") @PathVariable Long taskId,
                          @Parameter(description = "指派人用户ID") @RequestParam Long assigneeUserId) {
        followupService.assignTask(taskId, assigneeUserId);
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

    /**
     * R14: 完成随访任务 —— 管理员代填，执行人取登录用户上下文
     */
    @Operation(summary = "完成随访任务")
    @SaCheckPermission("chronic:followup-task:complete")
    @Log(title = "完成随访任务", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PostMapping("/chronic/admin/followup-task/{taskId}/complete")
    public R<Long> complete(@Parameter(description = "任务ID") @PathVariable Long taskId,
                            @Validated @RequestBody ChFollowupSubmitBo bo) {
        return R.ok(followupManager.completeTask(taskId, bo, null, null,
            LoginHelper.getUserId(), "ADMIN_PROXY"));
    }
}
