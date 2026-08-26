package org.dromara.chronic.controller.doctor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChFollowupSubmitBo;
import org.dromara.chronic.domain.bo.ChFollowupTaskClaimBo;
import org.dromara.chronic.domain.bo.ChMessageContentBo;
import org.dromara.chronic.domain.entity.ChFollowupTask;
import org.dromara.chronic.domain.vo.ChFollowupTaskDetailVo;
import org.dromara.chronic.domain.vo.ChFollowupTaskVo;
import org.dromara.chronic.domain.vo.ChMessageContentVo;
import org.dromara.chronic.domain.vo.ChMessageSessionVo;
import org.dromara.chronic.manager.FollowupManager;
import org.dromara.chronic.mapper.ChFollowupTaskMapper;
import org.dromara.chronic.service.IChFollowupService;
import org.dromara.chronic.service.IChMessageSessionService;
import org.dromara.common.core.exception.ServiceException;
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
    private final IChMessageSessionService messageSessionService;
    private final ChFollowupTaskMapper followupTaskMapper;

    /**
     * 校验当前执行人对该随访任务的可见性(指派给自己或任务池中的待认领任务)。
     */
    private ChFollowupTask requireVisibleTask(Long taskId) {
        ChFollowupTask task = followupTaskMapper.selectById(taskId);
        if (task == null) {
            throw new ServiceException("随访任务不存在");
        }
        Long userId = LoginHelper.getUserId();
        boolean visible = (task.getAssigneeUserId() != null && task.getAssigneeUserId().equals(userId))
            || task.getAssigneeUserId() == null;
        if (!visible) {
            throw new ServiceException("无权访问该随访任务");
        }
        return task;
    }

    /**
     * 规范化对话消息: 仅允许 TEXT/IMAGE/VOICE, 图片与语音必须携带 OSS 文件ID。
     */
    private void normalizeChatContent(ChMessageContentBo bo) {
        String type = bo.getContentType() == null ? "TEXT" : bo.getContentType().toUpperCase();
        if (!List.of("TEXT", "IMAGE", "VOICE").contains(type)) {
            throw new ServiceException("不支持的消息类型");
        }
        if ("TEXT".equals(type)) {
            if (bo.getContent() == null || bo.getContent().isBlank()) {
                throw new ServiceException("消息内容不能为空");
            }
        } else if (bo.getFileId() == null) {
            throw new ServiceException("图片或语音消息缺少文件ID");
        }
        bo.setContentType(type);
    }

    @Operation(summary = "获取基于随访任务的医患会话(ID, 不存在则创建)")
    @SaCheckPermission("chronic:doctor:followup-task:list")
    @GetMapping("/{taskId}/chat/session")
    public R<Long> taskSession(@Parameter(description = "任务ID") @PathVariable Long taskId) {
        ChFollowupTask task = requireVisibleTask(taskId);
        return R.ok(messageSessionService.getOrCreateTaskSession(
            task.getPatientId(), task.getAssigneeUserId(), taskId));
    }

    @Operation(summary = "任务会话详情(含历史消息)")
    @SaCheckPermission("chronic:doctor:followup-task:list")
    @GetMapping("/{taskId}/chat")
    public R<ChMessageSessionVo> taskChat(@Parameter(description = "任务ID") @PathVariable Long taskId) {
        ChFollowupTask task = requireVisibleTask(taskId);
        Long sessionId = messageSessionService.getOrCreateTaskSession(
            task.getPatientId(), task.getAssigneeUserId(), taskId);
        return R.ok(messageSessionService.queryById(sessionId));
    }

    @Operation(summary = "医生发送任务对话消息")
    @SaCheckPermission("chronic:doctor:followup-task:list")
    @PostMapping("/{taskId}/chat/send")
    public R<Long> taskChatSend(@Parameter(description = "任务ID") @PathVariable Long taskId,
                                @Validated @RequestBody ChMessageContentBo bo) {
        ChFollowupTask task = requireVisibleTask(taskId);
        Long sessionId = messageSessionService.getOrCreateTaskSession(
            task.getPatientId(), task.getAssigneeUserId(), taskId);
        if (bo.getSessionId() == null || !bo.getSessionId().equals(sessionId)) {
            throw new ServiceException("会话不存在或无权访问");
        }
        bo.setSenderType("DOCTOR");
        normalizeChatContent(bo);
        return R.ok(messageSessionService.sendMessage(bo));
    }

    @Operation(summary = "查询任务会话消息历史(传 sinceId 则增量拉取新消息)")
    @SaCheckPermission("chronic:doctor:followup-task:list")
    @GetMapping("/{taskId}/chat/history")
    public R<List<ChMessageContentVo>> taskChatHistory(@Parameter(description = "任务ID") @PathVariable Long taskId,
                                                       @Parameter(description = "已拉取到的最大消息ID") @RequestParam(required = false) Long sinceId) {
        ChFollowupTask task = requireVisibleTask(taskId);
        Long sessionId = messageSessionService.getOrCreateTaskSession(
            task.getPatientId(), task.getAssigneeUserId(), taskId);
        return R.ok(messageSessionService.queryMessagesBySessionId(sessionId, sinceId));
    }

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
