package org.dromara.chronic.controller.patient;

import org.dromara.common.web.core.BaseController;
import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChMessageContentBo;
import org.dromara.chronic.domain.vo.ChMessageContentVo;
import org.dromara.chronic.domain.vo.ChMessageSessionVo;
import org.dromara.chronic.domain.vo.ChPatientDetailVo;
import org.dromara.chronic.service.IChMessageSessionService;
import org.dromara.chronic.service.IChPatientProfileService;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.chronic.support.PatientContextHelper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 患者端消息
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者端消息")
@SaCheckLogin
@Validated
@RestController
@RequiredArgsConstructor
public class PatientMessageController extends BaseController {

    private final IChMessageSessionService messageSessionService;
    private final IChPatientProfileService patientProfileService;
    private final PatientContextHelper patientContextHelper;

    /**
     * 校验会话属于当前登录患者，并返回该会话。
     */
    private ChMessageSessionVo requireOwnSession(Long sessionId) {
        Long patientId = patientContextHelper.getCurrentPatientId();
        ChMessageSessionVo session = messageSessionService.queryById(sessionId);
        if (session == null || !patientId.equals(session.getPatientId())) {
            throw new ServiceException("会话不存在或无权访问");
        }
        return session;
    }

    @Operation(summary = "查询我的会话列表")
    @GetMapping("/chronic/patient/message/sessions")
    public R<List<ChMessageSessionVo>> mySessions() {
        Long patientId = patientContextHelper.getCurrentPatientId();
        return R.ok(messageSessionService.queryByPatientId(patientId));
    }

    @Operation(summary = "获取或创建在线问诊咨询会话")
    @PostMapping("/chronic/patient/message/consultation/session")
    public R<Long> getOrCreateConsultationSession(@Parameter(description = "指定医生用户ID(可选)") @RequestParam(required = false) Long doctorUserId) {
        Long patientId = patientContextHelper.getCurrentPatientId();
        if (doctorUserId == null) {
            ChPatientDetailVo detail = patientProfileService.queryDetailById(patientId);
            if (detail != null && detail.getDoctorUserId() != null) {
                doctorUserId = detail.getDoctorUserId();
            }
        }
        Long sessionId = messageSessionService.getOrCreateConsultationSession(patientId, doctorUserId);
        return R.ok(sessionId);
    }

    @Operation(summary = "会话详情")
    @GetMapping("/chronic/patient/message/session/{sessionId}")
    public R<ChMessageSessionVo> sessionDetail(@Parameter(description = "会话ID") @PathVariable Long sessionId) {
        return R.ok(requireOwnSession(sessionId));
    }

    @Operation(summary = "发送消息")
    @RepeatSubmit
    @PostMapping("/chronic/patient/message/send")
    public R<Long> send(@Validated @RequestBody ChMessageContentBo bo) {
        requireOwnSession(bo.getSessionId());
        bo.setSenderType("PATIENT");
        return R.ok(messageSessionService.sendMessage(bo));
    }

    @Operation(summary = "查询消息历史(支持增量)")
    @GetMapping("/chronic/patient/message/session/{sessionId}/history")
    public R<List<ChMessageContentVo>> history(@Parameter(description = "会话ID") @PathVariable Long sessionId,
                                               @Parameter(description = "起始消息ID(增量)") @RequestParam(required = false) Long sinceId) {
        requireOwnSession(sessionId);
        return R.ok(messageSessionService.queryMessagesBySessionId(sessionId, sinceId));
    }
}
