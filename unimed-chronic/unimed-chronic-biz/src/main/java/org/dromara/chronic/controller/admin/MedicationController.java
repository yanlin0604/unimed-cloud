package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChDrugInteractionBo;
import org.dromara.chronic.domain.bo.ChMedicationAdjustBo;
import org.dromara.chronic.domain.bo.ChMedicationRecordBo;
import org.dromara.chronic.domain.vo.ChDrugInteractionVo;
import org.dromara.chronic.domain.vo.ChMedicationAdjustVo;
import org.dromara.chronic.domain.vo.ChMedicationRecordVo;
import org.dromara.chronic.domain.vo.DrugInteractionCheckVo;
import org.dromara.chronic.manager.MedicationManager;
import org.dromara.chronic.service.IChMedicationService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 后台用药管理控制器
 *
 * @author unimed
 */
@Tag(name = "慢病管理-用药管理")
@Validated
@RestController
@RequiredArgsConstructor
public class MedicationController extends BaseController {

    private final IChMedicationService medicationService;
    private final MedicationManager medicationManager;

    @Operation(summary = "查询患者用药列表")
    @SaCheckPermission("chronic:medication:list")
    @GetMapping("/chronic/admin/patient/{patientId}/medication")
    public R<List<ChMedicationRecordVo>> medicationList(@Parameter(description = "患者ID") @PathVariable Long patientId) {
        return R.ok(medicationService.queryMedicationList(patientId));
    }

    @Operation(summary = "新增患者用药")
    @SaCheckPermission("chronic:medication:add")
    @Log(title = "患者用药", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/admin/patient/{patientId}/medication")
    public R<Void> addMedication(@Parameter(description = "患者ID") @PathVariable Long patientId, @Validated @RequestBody ChMedicationRecordBo bo) {
        bo.setPatientId(patientId);
        return toAjax(medicationManager.addMedication(bo));
    }

    @Operation(summary = "停用药物")
    @SaCheckPermission("chronic:medication:stop")
    @Log(title = "停用药物", businessType = BusinessType.UPDATE)
    @PostMapping("/chronic/admin/patient/{patientId}/medication/{medId}/stop")
    public R<Void> stopMedication(@Parameter(description = "患者ID") @PathVariable Long patientId, @Parameter(description = "药物ID") @PathVariable Long medId, @RequestBody(required = false) Map<String, Object> body) {
        String reason = body == null ? null : String.valueOf(body.get("reason"));
        return toAjax(medicationService.stopMedication(medId, reason));
    }

    @Operation(summary = "查询用药调整列表")
    @SaCheckPermission("chronic:medication-adjust:list")
    @GetMapping("/chronic/admin/patient/{patientId}/medication-adjust")
    public R<List<ChMedicationAdjustVo>> adjustList(@Parameter(description = "患者ID") @PathVariable Long patientId) {
        return R.ok(medicationService.queryAdjustList(patientId));
    }

    @Operation(summary = "新增用药调整")
    @SaCheckPermission("chronic:medication-adjust:add")
    @Log(title = "用药调整", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/admin/patient/{patientId}/medication-adjust")
    public R<Long> adjust(@Parameter(description = "患者ID") @PathVariable Long patientId, @Validated @RequestBody ChMedicationAdjustBo bo) {
        bo.setPatientId(patientId);
        return R.ok(medicationManager.adjustMedication(bo));
    }

    @Operation(summary = "查询用药依从性")
    @SaCheckPermission("chronic:medication:compliance")
    @GetMapping("/chronic/admin/patient/{patientId}/medication-compliance")
    public R<String> compliance(@Parameter(description = "患者ID") @PathVariable Long patientId) {
        return R.ok(medicationService.queryCompliance(patientId));
    }

    @Operation(summary = "分页查询药物相互作用")
    @SaCheckPermission("chronic:drug-interaction:list")
    @GetMapping("/chronic/admin/drug-interaction/page")
    public TableDataInfo<ChDrugInteractionVo> interactionPage(ChDrugInteractionBo bo, PageQuery pageQuery) {
        return medicationService.queryInteractionPage(bo, pageQuery);
    }

    @Operation(summary = "新增药物相互作用规则")
    @SaCheckPermission("chronic:drug-interaction:add")
    @Log(title = "药物相互作用规则", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/admin/drug-interaction")
    public R<Void> addInteraction(@Validated @RequestBody ChDrugInteractionBo bo) {
        return toAjax(medicationService.createInteractionRule(bo));
    }

    @Operation(summary = "检查药物相互作用")
    @SaCheckPermission("chronic:drug-interaction:check")
    @PostMapping("/chronic/admin/drug-interaction/check")
    public R<DrugInteractionCheckVo> checkInteraction(@RequestBody Map<String, Object> body) {
        Long patientId = Long.valueOf(String.valueOf(body.get("patientId")));
        String targetDrugCode = String.valueOf(body.get("targetDrugCode"));
        return R.ok(medicationService.checkInteraction(patientId, targetDrugCode));
    }
}
