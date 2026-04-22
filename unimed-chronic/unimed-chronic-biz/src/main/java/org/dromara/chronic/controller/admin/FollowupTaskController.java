package org.dromara.chronic.controller.admin;

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
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public TableDataInfo<ChFollowupTaskVo> page(@Parameter(description = "指派人ID") @RequestParam(required = false) Long assigneeUserId,
                                                @Parameter(description = "任务状态") @RequestParam(required = false) String taskStatus,
                                                PageQuery pageQuery) {
        return followupService.queryTaskPage(assigneeUserId, taskStatus, pageQuery);
    }

    /**
     * R14: 完成随访任务 —— 注入登录用户上下文
     */
    @Operation(summary = "完成随访任务")
    @SaCheckPermission("chronic:followup-task:complete")
    @Log(title = "完成随访任务", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PostMapping("/chronic/admin/followup-task/{taskId}/complete")
    public R<Long> complete(@Parameter(description = "任务ID") @PathVariable Long taskId, @Validated @RequestBody ChFollowupRecordBo bo) {
        bo.setTaskId(taskId);
        bo.setVisitorUserId(LoginHelper.getUserId());
        return R.ok(followupManager.completeTask(bo));
    }
}
