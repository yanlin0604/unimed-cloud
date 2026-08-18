package org.dromara.chronic.controller.patient;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChWarningEventBo;
import org.dromara.chronic.manager.SosNotificationManager;
import org.dromara.chronic.service.IChWarningEventService;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.chronic.support.PatientContextHelper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 患者端SOS一键求助
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者端SOS")
@SaCheckLogin
@RestController
@RequiredArgsConstructor
public class PatientSosController {

    private final IChWarningEventService warningEventService;
    private final PatientContextHelper patientContextHelper;
    private final SosNotificationManager sosNotificationManager;

    /**
     * SOS一键求助
     * <p>
     * 异步通知签约医生和紧急联系人，不阻塞请求
     */
    @Operation(summary = "SOS一键求助")
    @Log(title = "SOS紧急求助", businessType = BusinessType.INSERT)
    @PostMapping("/chronic/patient/sos")
    public R<Void> sos(
            @Parameter(description = "求助描述") @RequestParam(required = false) String description) {
        Long patientId = patientContextHelper.getCurrentPatientId();
        // 创建紧急预警事件
        ChWarningEventBo eventBo = new ChWarningEventBo();
        eventBo.setPatientId(patientId);
        eventBo.setWarningLevel("CRITICAL");
        eventBo.setEventStatus("NEW");
        warningEventService.createEvent(eventBo);
        // 异步通知签约医生和紧急联系人
        sosNotificationManager.notifyDoctorAndEmergencyContact(patientId, description);
        return R.ok();
    }
}
