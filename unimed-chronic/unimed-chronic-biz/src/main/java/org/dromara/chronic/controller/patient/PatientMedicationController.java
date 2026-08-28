package org.dromara.chronic.controller.patient;

import org.dromara.common.web.core.BaseController;
import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.vo.ChMedicationCheckinStatVo;
import org.dromara.chronic.domain.vo.ChMedicationRecordVo;
import org.dromara.chronic.service.IChMedicationService;
import org.dromara.chronic.support.PatientContextHelper;
import org.dromara.common.core.domain.R;
import org.springframework.validation.annotation.Validated;
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
@SaCheckLogin
@Validated
public class PatientMedicationController extends BaseController {

    private final IChMedicationService medicationService;
    private final PatientContextHelper patientContextHelper;

    /**
     * 查看用药列表
     */
    @Operation(summary = "查看用药列表")
    @GetMapping("/chronic/patient/medication/list")
    public R<List<ChMedicationRecordVo>> list() {
        Long patientId = patientContextHelper.getCurrentPatientId();
        return R.ok(medicationService.queryMedicationList(patientId));
    }

    @Operation(summary = "查询用药打卡统计")
    @GetMapping("/chronic/patient/medication/checkin/stat")
    public R<ChMedicationCheckinStatVo> checkinStat() {
        Long patientId = patientContextHelper.getCurrentPatientId();
        return R.ok(medicationService.queryCheckinStat(patientId));
    }

    /**
     * 服药打卡
     */
    @Operation(summary = "服药打卡")
    @PostMapping("/chronic/patient/medication/checkin")
    public R<Boolean> checkin(@Parameter(description = "用药记录ID") @RequestParam Long medId) {
        Long patientId = patientContextHelper.getCurrentPatientId();
        return R.ok(medicationService.checkinMedication(medId, patientId));
    }
}
