package org.dromara.chronic.controller.patient;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChMessageContentBo;
import org.dromara.chronic.domain.vo.ChMessageContentVo;
import org.dromara.chronic.domain.vo.ChMessageSessionVo;
import org.dromara.chronic.service.IChMessageSessionService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
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
public class PatientMessageController {

    private final IChMessageSessionService messageSessionService;

    @Operation(summary = "查询我的会话列表")
    @GetMapping("/chronic/patient/message/sessions")
    public R<List<ChMessageSessionVo>> mySessions(@Parameter(description = "患者ID") @RequestParam Long patientId) {
        return R.ok(messageSessionService.queryByPatientId(patientId));
    }

    @Operation(summary = "会话详情")
    @GetMapping("/chronic/patient/message/session/{sessionId}")
    public R<ChMessageSessionVo> sessionDetail(@Parameter(description = "会话ID") @PathVariable Long sessionId) {
        return R.ok(messageSessionService.queryById(sessionId));
    }

    @Operation(summary = "发送消息")
    @RepeatSubmit
    @PostMapping("/chronic/patient/message/send")
    public R<Long> send(@Validated @RequestBody ChMessageContentBo bo) {
        bo.setSenderType("PATIENT");
        return R.ok(messageSessionService.sendMessage(bo));
    }

    @Operation(summary = "查询消息历史")
    @GetMapping("/chronic/patient/message/session/{sessionId}/history")
    public R<List<ChMessageContentVo>> history(@Parameter(description = "会话ID") @PathVariable Long sessionId) {
        return R.ok(messageSessionService.queryMessagesBySessionId(sessionId));
    }
}
