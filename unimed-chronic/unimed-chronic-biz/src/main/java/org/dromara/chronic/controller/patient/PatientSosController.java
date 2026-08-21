package org.dromara.chronic.controller.patient;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChSosRecordBo;
import org.dromara.chronic.domain.bo.ChWarningEventBo;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.domain.vo.ChPatientContractVo;
import org.dromara.chronic.manager.SosNotificationManager;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.chronic.service.IChPatientContractService;
import org.dromara.chronic.service.IChSosRecordService;
import org.dromara.chronic.service.IChWarningEventService;
import org.dromara.chronic.support.PatientContextHelper;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.web.core.BaseController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * 患者端SOS一键求助
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者端SOS")
@SaCheckLogin
@RestController
@RequiredArgsConstructor
public class PatientSosController extends BaseController {

    private final IChWarningEventService warningEventService;
    private final IChSosRecordService sosRecordService;
    private final PatientContextHelper patientContextHelper;
    private final SosNotificationManager sosNotificationManager;
    private final ChPatientProfileMapper patientProfileMapper;
    private final IChPatientContractService patientContractService;

    /**
     * SOS一键求助
     * <p>
     * 持久化SOS求助记录并异步通知签约医生和紧急联系人
     */
    @Operation(summary = "SOS一键求助")
    @Log(title = "SOS紧急求助", businessType = BusinessType.INSERT)
    @PostMapping("/chronic/patient/sos")
    public R<Long> sos(
            @Parameter(description = "求助描述") @RequestParam(required = false) String description,
            @Parameter(description = "GPS经度") @RequestParam(required = false) BigDecimal gpsLng,
            @Parameter(description = "GPS纬度") @RequestParam(required = false) BigDecimal gpsLat,
            @Parameter(description = "GPS反向地理编码地址") @RequestParam(required = false) String gpsAddress) {
        Long patientId = patientContextHelper.getCurrentPatientId();

        // 1. 持久化 SOS 求助记录
        ChSosRecordBo sosBo = new ChSosRecordBo();
        sosBo.setPatientId(patientId);
        sosBo.setGpsLng(gpsLng);
        sosBo.setGpsLat(gpsLat);
        sosBo.setGpsAddress(gpsAddress);
        sosBo.setEventStatus("NEW");
        sosBo.setNotifyDoctorStatus("PENDING");
        sosBo.setNotifyEmergencyStatus("PENDING");
        sosBo.setHandleRemark(description);
        Long sosId = sosRecordService.insertByBo(sosBo);

        // 2. 创建 CRITICAL 紧急预警事件并指派责任医生
        ChWarningEventBo eventBo = new ChWarningEventBo();
        eventBo.setPatientId(patientId);
        eventBo.setWarningLevel("CRITICAL");
        eventBo.setEventStatus("NEW");
        eventBo.setWarningValue("SOS一键紧急求助" + (description != null && !description.isBlank() ? ": " + description : ""));

        // 优先指派责任医生/签约医生
        Long assigneeDoctorUserId = null;
        ChPatientProfile profile = patientProfileMapper.selectById(patientId);
        if (profile != null && profile.getDoctorUserId() != null) {
            assigneeDoctorUserId = profile.getDoctorUserId();
        }
        if (assigneeDoctorUserId == null) {
            try {
                ChPatientContractVo contract = patientContractService.queryCurrentContract(patientId);
                if (contract != null && contract.getTeamDoctorUserId() != null) {
                    assigneeDoctorUserId = contract.getTeamDoctorUserId();
                }
            } catch (Exception ignored) {
            }
        }
        eventBo.setAssigneeUserId(assigneeDoctorUserId);
        warningEventService.createEvent(eventBo);

        // 3. 异步通知签约医生和紧急联系人，并回写 SOS 记录状态
        sosNotificationManager.notifyDoctorAndEmergencyContact(sosId, patientId, description);

        return R.ok(sosId);
    }
}

