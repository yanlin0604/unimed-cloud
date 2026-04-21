package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChRiskAssessmentBo;
import org.dromara.chronic.domain.vo.ChManageLevelRecordVo;
import org.dromara.chronic.domain.vo.ChRiskAssessmentVo;
import org.dromara.chronic.manager.RiskAssessmentManager;
import org.dromara.chronic.service.IChRiskAssessmentService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 风险评估控制器
 *
 * @author unimed
 */
@Tag(name = "慢病管理-风险评估")
@Validated
@RestController
@RequiredArgsConstructor
public class RiskAssessmentController {

    private final RiskAssessmentManager riskAssessmentManager;
    private final IChRiskAssessmentService riskAssessmentService;

    @Operation(summary = "发起风险评估")
    @SaCheckPermission("chronic:risk-assessment:add")
    @Log(title = "风险评估", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/admin/patient/{patientId}/risk-assessment")
    public R<ChRiskAssessmentVo> assess(@Parameter(description = "患者ID") @PathVariable Long patientId, @Validated @RequestBody ChRiskAssessmentBo bo) {
        bo.setPatientId(patientId);
        return R.ok(riskAssessmentManager.assess(bo));
    }

    @Operation(summary = "查询最新风险评估")
    @SaCheckPermission("chronic:risk-assessment:query")
    @GetMapping("/chronic/admin/patient/{patientId}/risk-assessment/latest")
    public R<ChRiskAssessmentVo> latest(@Parameter(description = "患者ID") @PathVariable Long patientId) {
        return R.ok(riskAssessmentService.queryLatest(patientId));
    }

    @Operation(summary = "查询管理等级变更历史")
    @SaCheckPermission("chronic:manage-level:history")
    @GetMapping("/chronic/admin/patient/{patientId}/manage-level/history")
    public R<List<ChManageLevelRecordVo>> history(@Parameter(description = "患者ID") @PathVariable Long patientId) {
        return R.ok(riskAssessmentService.queryHistory(patientId));
    }
}
