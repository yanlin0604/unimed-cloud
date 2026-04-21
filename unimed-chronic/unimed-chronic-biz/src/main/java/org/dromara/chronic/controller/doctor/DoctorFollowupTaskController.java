package org.dromara.chronic.controller.doctor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChFollowupRecordBo;
import org.dromara.chronic.domain.vo.ChFollowupTaskVo;
import org.dromara.chronic.manager.FollowupManager;
import org.dromara.chronic.service.IChFollowupService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
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
    public R<List<ChFollowupTaskVo>> todo(@Parameter(description = "执行人用户ID") @RequestParam(required = false) Long assigneeUserId) {
        return R.ok(followupService.queryTodoTasks(assigneeUserId));
    }

    @Operation(summary = "完成随访")
    @SaCheckPermission("chronic:doctor:followup-task:visit")
    @Log(title = "医生随访", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PostMapping("/{taskId}/visit")
    public R<Long> visit(@Parameter(description = "任务ID", required = true) @PathVariable Long taskId, @Validated @RequestBody ChFollowupRecordBo bo) {
        bo.setTaskId(taskId);
        return R.ok(followupManager.completeTask(bo));
    }
}
