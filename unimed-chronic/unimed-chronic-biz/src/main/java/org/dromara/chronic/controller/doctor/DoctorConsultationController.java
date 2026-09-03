package org.dromara.chronic.controller.doctor;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChMessageContentBo;
import org.dromara.chronic.domain.bo.ChMessageSessionBo;
import org.dromara.chronic.domain.vo.ChMessageContentVo;
import org.dromara.chronic.domain.vo.ChMessageSessionVo;
import org.dromara.chronic.service.IChMessageSessionService;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 医生端在线问诊与接诊服务
 *
 * @author unimed
 */
@Tag(name = "慢病管理-医生端在线问诊")
@RestController
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class DoctorConsultationController extends BaseController {

    private final IChMessageSessionService messageSessionService;

    /**
     * 校验会话归属于当前医生
     */
    private ChMessageSessionVo requireDoctorSession(Long sessionId) {
        Long doctorUserId = LoginHelper.getUserId();
        ChMessageSessionVo session = messageSessionService.queryById(sessionId);
        if (session == null || (session.getDoctorUserId() != null && !doctorUserId.equals(session.getDoctorUserId()))) {
            throw new ServiceException("会话不存在或无权访问");
        }
        return session;
    }

    /**
     * 分页查询医生的咨询会话列表
     */
    @Operation(summary = "查询医生咨询会话列表")
    @GetMapping("/chronic/doctor/consultation/sessions")
    public TableDataInfo<ChMessageSessionVo> mySessions(ChMessageSessionBo bo, PageQuery pageQuery) {
        bo.setDoctorUserId(LoginHelper.getUserId());
        return messageSessionService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询会话消息历史(支持增量拉取)
     */
    @Operation(summary = "查询会话消息历史")
    @GetMapping("/chronic/doctor/consultation/session/{sessionId}/history")
    public R<List<ChMessageContentVo>> history(@Parameter(description = "会话ID") @PathVariable Long sessionId,
                                               @Parameter(description = "起始消息ID(增量)") @RequestParam(required = false) Long sinceId) {
        requireDoctorSession(sessionId);
        return R.ok(messageSessionService.queryMessagesBySessionId(sessionId, sinceId));
    }

    /**
     * 医生回复消息
     */
    @Operation(summary = "医生回复消息")
    @RepeatSubmit
    @PostMapping("/chronic/doctor/consultation/send")
    public R<Long> send(@Validated @RequestBody ChMessageContentBo bo) {
        requireDoctorSession(bo.getSessionId());
        bo.setSenderType("DOCTOR");
        return R.ok(messageSessionService.sendMessage(bo));
    }
}
