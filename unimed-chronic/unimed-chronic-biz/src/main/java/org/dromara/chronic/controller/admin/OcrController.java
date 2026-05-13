package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.OcrConfirmBo;
import org.dromara.chronic.domain.bo.OcrTaskBo;
import org.dromara.chronic.domain.vo.OcrConfirmResult;
import org.dromara.chronic.domain.vo.OcrTaskVo;
import org.dromara.chronic.manager.OcrManager;
import org.dromara.chronic.service.IOcrService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端医疗文档OCR
 *
 * @author unimed
 */
@Tag(name = "慢病管理-医疗文档OCR")
@Validated
@RestController
@RequiredArgsConstructor
public class OcrController {

    private final OcrManager ocrManager;
    private final IOcrService ocrService;

    @Operation(summary = "创建医疗文档OCR任务")
    @SaCheckPermission("chronic:medical-document-ocr:add")
    @Log(title = "医疗文档OCR", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/admin/medical-document-ocr/tasks")
    public R<Long> add(@Validated @RequestBody OcrTaskBo bo) {
        bo.setSourceType("ADMIN");
        return R.ok(ocrManager.recognize(bo));
    }

    @Operation(summary = "为患者创建医疗文档OCR任务")
    @SaCheckPermission("chronic:medical-document-ocr:add")
    @Log(title = "医疗文档OCR", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/admin/patient/{patientId}/medical-document-ocr/tasks")
    public R<Long> addForPatient(@Parameter(description = "患者ID") @PathVariable Long patientId,
                                 @Validated @RequestBody OcrTaskBo bo) {
        bo.setPatientId(patientId);
        bo.setSourceType("ADMIN");
        return R.ok(ocrManager.recognize(bo));
    }

    @Operation(summary = "分页查询医疗文档OCR任务")
    @SaCheckPermission("chronic:medical-document-ocr:list")
    @GetMapping("/chronic/admin/medical-document-ocr/page")
    public TableDataInfo<OcrTaskVo> page(OcrTaskBo bo, PageQuery pageQuery) {
        return ocrService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "医疗文档OCR任务详情")
    @SaCheckPermission("chronic:medical-document-ocr:query")
    @GetMapping("/chronic/admin/medical-document-ocr/{taskId}")
    public R<OcrTaskVo> detail(@Parameter(description = "任务ID") @PathVariable Long taskId) {
        return R.ok(ocrService.queryById(taskId));
    }

    @Operation(summary = "确认医疗文档OCR草稿")
    @SaCheckPermission("chronic:medical-document-ocr:edit")
    @Log(title = "医疗文档OCR确认", businessType = BusinessType.UPDATE)
    @PutMapping("/chronic/admin/medical-document-ocr/{taskId}/confirm")
    public R<OcrConfirmResult> confirm(@Parameter(description = "任务ID") @PathVariable Long taskId,
                                                      @Validated @RequestBody OcrConfirmBo bo) {
        return R.ok(ocrManager.confirm(taskId, bo));
    }

    @Operation(summary = "废弃医疗文档OCR草稿")
    @SaCheckPermission("chronic:medical-document-ocr:edit")
    @Log(title = "医疗文档OCR废弃", businessType = BusinessType.UPDATE)
    @PutMapping("/chronic/admin/medical-document-ocr/{taskId}/discard")
    public R<Void> discard(@Parameter(description = "任务ID") @PathVariable Long taskId) {
        return R.ok(ocrService.discard(taskId));
    }
}
