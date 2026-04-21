package org.dromara.chronic.controller.patient;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.vo.ChMedicationRecordVo;
import org.dromara.chronic.service.IChMedicationService;
import org.dromara.common.core.domain.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 患者端用药接口
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者端用药")
@RestController
@RequiredArgsConstructor
public class PatientMedicationController {

    private final IChMedicationService medicationService;

    /**
     * 查看用药列表
     */
    @Operation(summary = "查看用药列表")
    @SaCheckLogin
    @GetMapping("/chronic/patient/medication/list")
    public R<List<ChMedicationRecordVo>> list(@Parameter(description = "患者ID") @RequestParam Long patientId) {
        return R.ok(medicationService.queryMedicationList(patientId));
    }

    /**
     * 服药打卡
     */
    @Operation(summary = "服药打卡")
    @SaCheckLogin
    @PostMapping("/chronic/patient/medication/checkin")
    public R<Boolean> checkin(@Parameter(description = "患者ID") @RequestParam Long patientId,
                              @Parameter(description = "用药记录ID") @RequestParam Long medId) {
        return R.ok(medicationService.stopMedication(medId, "PATIENT_CHECKIN"));
    }
}
