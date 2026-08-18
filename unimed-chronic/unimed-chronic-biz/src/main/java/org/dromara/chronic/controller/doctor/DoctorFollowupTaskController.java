package org.dromara.chronic.controller.doctor;

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
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 医生端随访任务控制器
 *
 * @author unimed
 */
@Tag(name = "慢病管理-医生端随访任务")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/doctor/followup-task")
public class DoctorFollowupTaskController {

    private final IChFollowupService followupService;
    private final FollowupManager followupManager;

    @Operation(summary = "查询待办随访任务")
    @SaCheckPermission("chronic:doctor:followup-task:list")
    @GetMapping("/todo")
    public R<List<ChFollowupTaskVo>> todo(@Parameter(description = "任务状态") @RequestParam(required = false) String taskStatus) {
        // 执行人身份取自登录上下文，禁止前端传入
        return R.ok(followupService.queryTodoTasks(LoginHelper.getUserId(), taskStatus));
    }

    @Operation(summary = "查询随访任务详情")
    @SaCheckPermission("chronic:doctor:followup-task:list")
    @GetMapping("/{taskId}")
    public R<ChFollowupTaskDetailVo> detail(@Parameter(description = "任务ID", required = true) @PathVariable Long taskId) {
        return R.ok(followupService.queryTaskDetail(taskId, null, LoginHelper.getUserId()));
    }

    @Operation(summary = "完成随访")
    @SaCheckPermission("chronic:doctor:followup-task:visit")
    @Log(title = "医生随访", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PostMapping("/{taskId}/visit")
    public R<Long> visit(@Parameter(description = "任务ID", required = true) @PathVariable Long taskId,
                         @Validated @RequestBody ChFollowupSubmitBo bo) {
        Long doctorUserId = LoginHelper.getUserId();
        // 仅允许完成指派给当前医生的任务
        return R.ok(followupManager.completeTask(taskId, bo, null, doctorUserId, doctorUserId, null));
    }
}
