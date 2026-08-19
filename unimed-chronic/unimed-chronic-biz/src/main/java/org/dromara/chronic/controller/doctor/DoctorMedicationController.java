package org.dromara.chronic.controller.doctor;

import org.dromara.common.web.core.BaseController;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChMedicationAdjustBo;
import org.dromara.chronic.domain.vo.ChMedicationRecordVo;
import org.dromara.chronic.domain.vo.DrugInteractionCheckVo;
import org.dromara.chronic.manager.MedicationManager;
import org.dromara.chronic.service.IChMedicationService;
import org.dromara.chronic.support.DoctorScopeGuard;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 医生端用药管理控制器
 *
 * @author unimed
 */
@Tag(name = "慢病管理-医生端用药")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/doctor/patient")
public class DoctorMedicationController extends BaseController {

    private final IChMedicationService medicationService;
    private final MedicationManager medicationManager;
    private final DoctorScopeGuard doctorScopeGuard;

    @Operation(summary = "查询患者用药列表")
    @SaCheckPermission("chronic:doctor:medication:list")
    @GetMapping("/{patientId}/medication")
    public R<List<ChMedicationRecordVo>> medication(@Parameter(description = "患者ID", required = true) @PathVariable Long patientId) {
        doctorScopeGuard.assertPatientOwned(patientId);
        return R.ok(medicationService.queryMedicationList(patientId));
    }

    /**
     * R14: 用药调整预览
     */
    @Operation(summary = "用药调整预览")
    @SaCheckPermission("chronic:doctor:medication-adjust:add")
    @PostMapping("/{patientId}/medication-adjust/preview")
    public R<DrugInteractionCheckVo> previewAdjust(@Parameter(description = "患者ID", required = true) @PathVariable Long patientId, @Validated @RequestBody ChMedicationAdjustBo bo) {
        doctorScopeGuard.assertPatientOwned(patientId);
        bo.setPatientId(patientId);
        return R.ok(medicationManager.previewAdjust(bo));
    }

    /**
     * R14: 新增用药调整 —— 注入登录用户上下文
     */
    @Operation(summary = "新增用药调整")
    @SaCheckPermission("chronic:doctor:medication-adjust:add")
    @Log(title = "医生端用药调整", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/{patientId}/medication-adjust")
    public R<Long> adjust(@Parameter(description = "患者ID", required = true) @PathVariable Long patientId, @Validated @RequestBody ChMedicationAdjustBo bo) {
        // setAdjusterUserId 仅为署名（记录谁调的药），不构成鉴权，仍需校验患者归属
        doctorScopeGuard.assertPatientOwned(patientId);
        bo.setPatientId(patientId);
        bo.setAdjusterUserId(LoginHelper.getUserId());
        return R.ok(medicationManager.adjustMedication(bo));
    }

    @Operation(summary = "登记药物不良反应")
    @SaCheckPermission("chronic:doctor:medication-adverse:add")
    @Log(title = "药物不良反应", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/{patientId}/medication-adverse")
    public R<Long> adverse(@Parameter(description = "患者ID", required = true) @PathVariable Long patientId, @RequestBody Map<String, Object> body) {
        doctorScopeGuard.assertPatientOwned(patientId);
        ChMedicationAdjustBo bo = new ChMedicationAdjustBo();
        bo.setPatientId(patientId);
        bo.setMedId(body.get("medId") == null ? null : Long.valueOf(String.valueOf(body.get("medId"))));
        bo.setAdjustType("ADVERSE");
        bo.setAdjustReason("不良反应登记");
        bo.setAdverseReaction(String.valueOf(body.get("adverseReaction")));
        bo.setPreviewConfirmed(Boolean.TRUE);
        return R.ok(medicationManager.adjustMedication(bo));
    }
}
