package org.dromara.chronic.controller.patient;

import org.dromara.common.web.core.BaseController;
import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChFollowupSubmitBo;
import org.dromara.chronic.domain.bo.ChMessageContentBo;
import org.dromara.chronic.domain.entity.ChFollowupTask;
import org.dromara.chronic.domain.vo.ChFollowupPlanVo;
import org.dromara.chronic.domain.vo.ChFollowupTaskDetailVo;
import org.dromara.chronic.domain.vo.ChFollowupTaskVo;
import org.dromara.chronic.domain.vo.ChMessageContentVo;
import org.dromara.chronic.domain.vo.ChMessageSessionVo;
import org.dromara.chronic.manager.FollowupManager;
import org.dromara.chronic.mapper.ChFollowupTaskMapper;
import org.dromara.chronic.service.IChFollowupService;
import org.dromara.chronic.service.IChMessageSessionService;
import org.dromara.chronic.support.PatientContextHelper;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 患者端随访控制器
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者端随访")
@SaCheckLogin
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/patient/followup")
public class PatientFollowupController extends BaseController {

    private final IChFollowupService followupService;
    private final FollowupManager followupManager;
    private final PatientContextHelper patientContextHelper;
    private final IChMessageSessionService messageSessionService;
    private final ChFollowupTaskMapper followupTaskMapper;

    /**
     * 校验当前患者对该随访任务的归属, 返回任务; 失败抛出无差异提示避免会话枚举。
     */
    private ChFollowupTask requireOwnTask(Long taskId) {
        Long patientId = patientContextHelper.getCurrentPatientId();
        ChFollowupTask task = followupTaskMapper.selectById(taskId);
        if (task == null || task.getPatientId() == null || !task.getPatientId().equals(patientId)) {
            throw new ServiceException("任务不存在或无权访问");
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
    @GetMapping("/task/{taskId}/chat/session")
    public R<Long> taskSession(@Parameter(description = "任务ID") @PathVariable Long taskId) {
        ChFollowupTask task = requireOwnTask(taskId);
        return R.ok(messageSessionService.getOrCreateTaskSession(
            task.getPatientId(), task.getAssigneeUserId(), taskId));
    }

    @Operation(summary = "任务会话详情(含历史消息)")
    @GetMapping("/task/{taskId}/chat")
    public R<ChMessageSessionVo> taskChat(@Parameter(description = "任务ID") @PathVariable Long taskId) {
        ChFollowupTask task = requireOwnTask(taskId);
        Long sessionId = messageSessionService.getOrCreateTaskSession(
            task.getPatientId(), task.getAssigneeUserId(), taskId);
        return R.ok(messageSessionService.queryById(sessionId));
    }

    @Operation(summary = "患者发送任务对话消息")
    @RepeatSubmit
    @PostMapping("/task/{taskId}/chat/send")
    public R<Long> taskChatSend(@Parameter(description = "任务ID") @PathVariable Long taskId,
                                @Validated @RequestBody ChMessageContentBo bo) {
        ChFollowupTask task = requireOwnTask(taskId);
        Long sessionId = messageSessionService.getOrCreateTaskSession(
            task.getPatientId(), task.getAssigneeUserId(), taskId);
        if (bo.getSessionId() == null || !bo.getSessionId().equals(sessionId)) {
            throw new ServiceException("会话不存在或无权访问");
        }
        bo.setSenderType("PATIENT");
        normalizeChatContent(bo);
        return R.ok(messageSessionService.sendMessage(bo));
    }

    @Operation(summary = "查询任务会话消息历史(传 sinceId 则增量拉取新消息)")
    @GetMapping("/task/{taskId}/chat/history")
    public R<List<ChMessageContentVo>> taskChatHistory(@Parameter(description = "任务ID") @PathVariable Long taskId,
                                                       @Parameter(description = "已拉取到的最大消息ID") @RequestParam(required = false) Long sinceId) {
        ChFollowupTask task = requireOwnTask(taskId);
        Long sessionId = messageSessionService.getOrCreateTaskSession(
            task.getPatientId(), task.getAssigneeUserId(), taskId);
        return R.ok(messageSessionService.queryMessagesBySessionId(sessionId, sinceId));
    }

    @Operation(summary = "查询随访计划")
    @GetMapping("/plan")
    public R<ChFollowupPlanVo> plan() {
        Long patientId = patientContextHelper.getCurrentPatientId();
        return R.ok(followupService.queryCurrentPlan(patientId));
    }

    @Operation(summary = "查询随访任务")
    @GetMapping("/task")
    public R<List<ChFollowupTaskVo>> task() {
        Long patientId = patientContextHelper.getCurrentPatientId();
        return R.ok(followupService.queryPatientTasks(patientId));
    }

    @Operation(summary = "查询随访任务详情")
    @GetMapping("/task/{taskId}")
    public R<ChFollowupTaskDetailVo> taskDetail(@Parameter(description = "任务ID") @PathVariable Long taskId) {
        Long patientId = patientContextHelper.getCurrentPatientId();
        // 归属校验：仅能查看本人任务
        return R.ok(followupService.queryTaskDetail(taskId, patientId, null));
    }

    @Operation(summary = "患者自填随访提交(仅采集体征/问卷/小结,待医生评估)")
    @Log(title = "患者自填随访", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PostMapping("/task/{taskId}/submit")
    public R<Long> submit(@Parameter(description = "任务ID") @PathVariable Long taskId,
                          @Validated @RequestBody ChFollowupSubmitBo bo) {
        Long patientId = patientContextHelper.getCurrentPatientId();
        // 患者身份取自登录上下文,禁止前端传入 patientId;只采集数据进入待医生评估,不完成任务
        return R.ok(followupManager.submitSelfFill(taskId, bo, patientId,
            patientContextHelper.getCurrentAccountId(), "ONLINE"));
    }
}
