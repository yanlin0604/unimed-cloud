package org.dromara.chronic.controller.patient;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.service.IChPatientProfileService;
import org.dromara.chronic.service.IChWarningEventService;
import org.dromara.common.core.domain.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 患者端SOS一键求助
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者端SOS")
@RestController
@RequiredArgsConstructor
public class PatientSosController {

    private final IChWarningEventService warningEventService;
    private final IChPatientProfileService patientProfileService;

    /**
     * SOS一键求助
     * <p>
     * 异步通知签约医生和紧急联系人，不阻塞请求
     */
    @Operation(summary = "SOS一键求助")
    @SaCheckLogin
    @PostMapping("/chronic/patient/sos")
    public R<Void> sos(
            @Parameter(description = "患者ID") @RequestParam Long patientId,
            @Parameter(description = "求助描述") @RequestParam(required = false) String description) {
        // TODO: 异步通知签约医生+紧急联系人（需集成消息推送服务）
        // 当前先记录一条紧急预警事件
        org.dromara.chronic.domain.bo.ChWarningEventBo eventBo = new org.dromara.chronic.domain.bo.ChWarningEventBo();
        eventBo.setPatientId(patientId);
        eventBo.setWarningLevel("URGENT");
        eventBo.setEventStatus("NEW");
        warningEventService.createEvent(eventBo);
        return R.ok();
    }
}
