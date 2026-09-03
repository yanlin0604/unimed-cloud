package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChTumorRecordBo;
import org.dromara.chronic.domain.vo.ChTumorRecordVo;
import org.dromara.chronic.manager.SpecialDiseaseManager;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 专病精细化管理与专病计算引擎
 *
 * @author unimed
 */
@Tag(name = "慢病管理-专病精细化")
@Validated
@RestController
@RequiredArgsConstructor
public class SpecialDiseaseController extends BaseController {

    private final SpecialDiseaseManager specialDiseaseManager;

    @Operation(summary = "CKD-EPI 肾小球滤过率与肾病分期计算")
    @SaCheckPermission("chronic:patient:query")
    @GetMapping("/chronic/admin/special-disease/ckd/calc")
    public R<Map<String, Object>> calcCkd(
        @Parameter(description = "血肌酐值(mg/dL)") @RequestParam BigDecimal scr,
        @Parameter(description = "年龄(岁)") @RequestParam Integer age,
        @Parameter(description = "是否女性") @RequestParam(defaultValue = "false") Boolean isFemale) {
        return R.ok(specialDiseaseManager.calculateCkdStage(scr, age, isFemale));
    }

    @Operation(summary = "脑卒中 NIHSS 与 Barthel 评分临床评估")
    @SaCheckPermission("chronic:patient:query")
    @GetMapping("/chronic/admin/special-disease/stroke/eval")
    public R<Map<String, Object>> evalStroke(
        @Parameter(description = "NIHSS神经功能评分") @RequestParam(required = false) Integer nihssScore,
        @Parameter(description = "Barthel生活自理评分") @RequestParam(required = false) Integer barthelScore) {
        return R.ok(specialDiseaseManager.evaluateStroke(nihssScore, barthelScore));
    }

    @Operation(summary = "慢阻肺 CAT 症状与肺功能 GOLD 分级评估")
    @SaCheckPermission("chronic:patient:query")
    @GetMapping("/chronic/admin/special-disease/copd/eval")
    public R<Map<String, Object>> evalCopd(
        @Parameter(description = "CAT问卷总分") @RequestParam(required = false) Integer catScore,
        @Parameter(description = "FEV1占预计值百分比") @RequestParam(required = false) BigDecimal fev1Percent) {
        return R.ok(specialDiseaseManager.evaluateCopd(catScore, fev1Percent));
    }

    @Operation(summary = "保存或更新肿瘤专项档案")
    @SaCheckPermission("chronic:patient:edit")
    @Log(title = "肿瘤专项档案", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/admin/special-disease/tumor")
    public R<Long> saveTumor(@Validated @RequestBody ChTumorRecordBo bo) {
        return R.ok(specialDiseaseManager.saveOrUpdateTumorRecord(bo));
    }

    @Operation(summary = "查询患者肿瘤专项档案")
    @SaCheckPermission("chronic:patient:query")
    @GetMapping("/chronic/admin/special-disease/tumor/{patientId}")
    public R<ChTumorRecordVo> queryTumor(@Parameter(description = "患者ID") @PathVariable Long patientId) {
        return R.ok(specialDiseaseManager.queryTumorByPatientId(patientId));
    }
}
