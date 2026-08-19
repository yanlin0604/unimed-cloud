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
import org.dromara.chronic.service.IChMessageSessionService;
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
    private final PatientContextHelper patientContextHelper;

    /**
     * 校验会话属于当前登录患者，并返回该会话。
     * <p>
     * sessionId 由前端传入且为自增整数，不校验归属则可枚举读取／写入他人的医患对话。
     * 错误信息不区分「不存在」与「无权」，避免探测会话是否存在。
     *
     * @param sessionId 会话ID
     * @return 归属校验通过的会话
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

    @Operation(summary = "会话详情")
    @GetMapping("/chronic/patient/message/session/{sessionId}")
    public R<ChMessageSessionVo> sessionDetail(@Parameter(description = "会话ID") @PathVariable Long sessionId) {
        return R.ok(requireOwnSession(sessionId));
    }

    @Operation(summary = "发送消息")
    @RepeatSubmit
    @PostMapping("/chronic/patient/message/send")
    public R<Long> send(@Validated @RequestBody ChMessageContentBo bo) {
        // 原实现只设置 senderType，未校验 bo.sessionId 归属：
        // 患者可往任意会话（他人与其医生的对话）插入消息，属越权写。
        requireOwnSession(bo.getSessionId());
        bo.setSenderType("PATIENT");
        return R.ok(messageSessionService.sendMessage(bo));
    }

    @Operation(summary = "查询消息历史")
    @GetMapping("/chronic/patient/message/session/{sessionId}/history")
    public R<List<ChMessageContentVo>> history(@Parameter(description = "会话ID") @PathVariable Long sessionId) {
        requireOwnSession(sessionId);
        return R.ok(messageSessionService.queryMessagesBySessionId(sessionId));
    }
}
