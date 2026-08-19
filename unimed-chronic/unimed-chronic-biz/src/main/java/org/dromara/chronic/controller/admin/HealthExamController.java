package org.dromara.chronic.controller.admin;

import org.dromara.common.web.core.BaseController;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChHealthExamBo;
import org.dromara.chronic.domain.bo.ChHealthExamItemBo;
import org.dromara.chronic.domain.bo.EgfrCalcBo;
import org.dromara.chronic.domain.vo.ChHealthExamVo;
import org.dromara.chronic.domain.vo.EgfrCalcVo;
import org.dromara.chronic.manager.HealthExamManager;
import org.dromara.chronic.service.IChHealthExamService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 体检检验管理后台
 *
 * @author unimed
 */
@Tag(name = "慢病管理-体检检验")
@Validated
@RestController
@RequiredArgsConstructor
public class HealthExamController extends BaseController {

    private final HealthExamManager healthExamManager;
    private final IChHealthExamService healthExamService;

    @Operation(summary = "新增体检报告")
    @SaCheckPermission("chronic:health-exam:add")
    @Log(title = "体检报告", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/admin/patient/{patientId}/health-exams")
    public R<Long> add(@Parameter(description = "患者ID") @PathVariable Long patientId, @Validated @RequestBody ChHealthExamBo bo) {
        bo.setPatientId(patientId);
        return R.ok(healthExamService.createExam(bo));
    }

    @Operation(summary = "修改体检报告")
    @SaCheckPermission("chronic:health-exam:edit")
    @Log(title = "体检报告", businessType = BusinessType.UPDATE)
    @PutMapping("/chronic/admin/health-exam/{examId}")
    public R<Void> edit(@Parameter(description = "体检报告ID") @PathVariable Long examId, @Validated @RequestBody ChHealthExamBo bo) {
        bo.setExamId(examId);
        return R.ok(healthExamService.updateExam(bo));
    }

    @Operation(summary = "分页查询体检检验")
    @SaCheckPermission("chronic:health-exam:list")
    @GetMapping("/chronic/admin/health-exam/page")
    public TableDataInfo<ChHealthExamVo> page(ChHealthExamBo bo, PageQuery pageQuery) {
        return healthExamService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "体检报告详情")
    @SaCheckPermission("chronic:health-exam:query")
    @GetMapping("/chronic/admin/health-exam/{examId}")
    public R<ChHealthExamVo> detail(@Parameter(description = "体检报告ID") @PathVariable Long examId) {
        return R.ok(healthExamManager.queryDetail(examId));
    }

    @Operation(summary = "新增体检检验项")
    @SaCheckPermission("chronic:health-exam:add")
    @Log(title = "体检检验项", businessType = BusinessType.INSERT)
    @PostMapping("/chronic/admin/health-exam/{examId}/items")
    public R<Long> addItem(@Parameter(description = "体检报告ID") @PathVariable Long examId, @Validated @RequestBody ChHealthExamItemBo bo) {
        bo.setExamId(examId);
        return R.ok(healthExamService.addItem(bo));
    }

    @Operation(summary = "删除体检报告")
    @SaCheckPermission("chronic:health-exam:remove")
    @Log(title = "体检报告", businessType = BusinessType.DELETE)
    @DeleteMapping("/chronic/admin/health-exam/{examId}")
    public R<Void> remove(@Parameter(description = "体检报告ID") @PathVariable Long examId) {
        return R.ok(healthExamService.removeExam(examId));
    }

    @Operation(summary = "计算 eGFR（CKD-EPI 2021）")
    @SaCheckPermission("chronic:health-exam:query")
    @PostMapping("/chronic/admin/health-exam/calc-egfr")
    public R<EgfrCalcVo> calcEgfr(@Validated @RequestBody EgfrCalcBo bo) {
        return R.ok(healthExamService.calcEgfr(bo));
    }
}
