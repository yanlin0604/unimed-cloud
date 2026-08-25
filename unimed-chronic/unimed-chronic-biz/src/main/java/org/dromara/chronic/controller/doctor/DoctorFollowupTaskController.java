package org.dromara.chronic.controller.doctor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChFollowupSubmitBo;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 执行人/医生端随访任务控制器
 *
 * @author unimed
 */
@Tag(name = "慢病管理-执行人随访任务")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/doctor/followup-task")
public class DoctorFollowupTaskController extends BaseController {

    private final IChFollowupService followupService;
    private final FollowupManager followupManager;

    @Operation(summary = "查询待办随访任务")
    @SaCheckPermission("chronic:doctor:followup-task:list")
    @GetMapping("/todo")
    public R<List<ChFollowupTaskVo>> todo(@Parameter(description = "任务状态") @RequestParam(required = false) String taskStatus) {
        return R.ok(followupService.queryTodoTasks(LoginHelper.getUserId(), taskStatus));
    }

    @Operation(summary = "分页查询随访任务池")
    @SaCheckPermission("chronic:doctor:followup-task:list")
    @GetMapping("/pool")
    public TableDataInfo<ChFollowupTaskVo> pool(@Parameter(description = "病种编码") @RequestParam(required = false) String diseaseCode,
                                                @Parameter(description = "随访方式(ONLINE/OFFLINE/PHONE)") @RequestParam(required = false) String visitType,
                                                PageQuery pageQuery) {
        return followupManager.queryTaskPoolPage(diseaseCode, visitType, pageQuery);
    }

    @Operation(summary = "从任务池认领任务")
    @SaCheckPermission("chronic:doctor:followup-task:list")
    @Log(title = "执行人认领随访任务", businessType = BusinessType.UPDATE)
    @PostMapping("/{taskId}/claim")
    public R<Void> claim(@Parameter(description = "任务ID", required = true) @PathVariable Long taskId) {
        followupManager.claimTask(taskId, LoginHelper.getUserId());
        return R.ok();
    }

    @Operation(summary = "批量认领随访任务")
    @SaCheckPermission("chronic:doctor:followup-task:list")
    @Log(title = "执行人批量认领随访任务", businessType = BusinessType.UPDATE)
    @PostMapping("/batch-claim")
    public R<Void> batchClaim(@Validated @RequestBody ChFollowupTaskClaimBo bo) {
        followupManager.batchClaimTasks(bo.getTaskIds(), LoginHelper.getUserId());
        return R.ok();
    }

    @Operation(summary = "退回随访任务池")
    @SaCheckPermission("chronic:doctor:followup-task:list")
    @Log(title = "执行人释放随访任务", businessType = BusinessType.UPDATE)
    @PutMapping("/{taskId}/release")
    public R<Void> release(@Parameter(description = "任务ID", required = true) @PathVariable Long taskId) {
        followupManager.releaseTask(taskId, LoginHelper.getUserId());
        return R.ok();
    }

    @Operation(summary = "查询随访任务详情")
    @SaCheckPermission("chronic:doctor:followup-task:list")
    @GetMapping("/{taskId}")
    public R<ChFollowupTaskDetailVo> detail(@Parameter(description = "任务ID", required = true) @PathVariable Long taskId) {
        return R.ok(followupService.queryTaskDetail(taskId, null, LoginHelper.getUserId()));
    }

    @Operation(summary = "完成随访")
    @SaCheckPermission("chronic:doctor:followup-task:visit")
    @Log(title = "执行人随访", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PostMapping("/{taskId}/visit")
    public R<Long> visit(@Parameter(description = "任务ID", required = true) @PathVariable Long taskId,
                         @Validated @RequestBody ChFollowupSubmitBo bo) {
        Long executorUserId = LoginHelper.getUserId();
        return R.ok(followupManager.completeTask(taskId, bo, null, executorUserId, executorUserId, null));
    }

    @Operation(summary = "向患者发送随访提醒")
    @SaCheckPermission("chronic:doctor:followup-task:list")
    @Log(title = "医生发送随访提醒", businessType = BusinessType.UPDATE)
    @PostMapping("/{taskId}/remind")
    public R<Void> remind(@Parameter(description = "任务ID", required = true) @PathVariable Long taskId) {
        followupService.sendTaskRemind(taskId, LoginHelper.getUserId());
        return R.ok();
    }
}
