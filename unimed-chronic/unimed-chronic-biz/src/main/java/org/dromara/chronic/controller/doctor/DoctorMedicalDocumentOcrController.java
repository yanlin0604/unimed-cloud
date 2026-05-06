package org.dromara.chronic.controller.doctor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.MedicalDocumentOcrConfirmBo;
import org.dromara.chronic.domain.bo.MedicalDocumentOcrTaskBo;
import org.dromara.chronic.domain.vo.MedicalDocumentOcrConfirmResult;
import org.dromara.chronic.domain.vo.MedicalDocumentOcrTaskVo;
import org.dromara.chronic.manager.MedicalDocumentOcrManager;
import org.dromara.chronic.service.IMedicalDocumentOcrService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 医生端医疗文档OCR
 *
 * @author unimed
 */
@Tag(name = "慢病管理-医生端医疗文档OCR")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/doctor")
public class DoctorMedicalDocumentOcrController {

    private final MedicalDocumentOcrManager ocrManager;
    private final IMedicalDocumentOcrService ocrService;

    @Operation(summary = "创建患者医疗文档OCR任务")
    @SaCheckPermission("chronic:doctor:medical-document-ocr:add")
    @Log(title = "医生端医疗文档OCR", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/patient/{patientId}/medical-document-ocr/tasks")
    public R<Long> add(@PathVariable Long patientId, @Validated @RequestBody MedicalDocumentOcrTaskBo bo) {
        bo.setPatientId(patientId);
        bo.setSourceType("DOCTOR");
        return R.ok(ocrManager.recognize(bo));
    }

    @Operation(summary = "患者医疗文档OCR任务分页")
    @SaCheckPermission("chronic:doctor:medical-document-ocr:list")
    @GetMapping("/patient/{patientId}/medical-document-ocr/page")
    public TableDataInfo<MedicalDocumentOcrTaskVo> page(@PathVariable Long patientId, MedicalDocumentOcrTaskBo bo, PageQuery pageQuery) {
        bo.setPatientId(patientId);
        bo.setSourceType("DOCTOR");
        return ocrService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "医疗文档OCR任务详情")
    @SaCheckPermission("chronic:doctor:medical-document-ocr:query")
    @GetMapping("/medical-document-ocr/{taskId}")
    public R<MedicalDocumentOcrTaskVo> detail(@PathVariable Long taskId) {
        return R.ok(ocrService.queryById(taskId));
    }

    @Operation(summary = "确认医疗文档OCR草稿")
    @SaCheckPermission("chronic:doctor:medical-document-ocr:edit")
    @Log(title = "医生端医疗文档OCR确认", businessType = BusinessType.UPDATE)
    @PutMapping("/medical-document-ocr/{taskId}/confirm")
    public R<MedicalDocumentOcrConfirmResult> confirm(@PathVariable Long taskId, @Validated @RequestBody MedicalDocumentOcrConfirmBo bo) {
        return R.ok(ocrManager.confirm(taskId, bo));
    }

    @Operation(summary = "废弃医疗文档OCR草稿")
    @SaCheckPermission("chronic:doctor:medical-document-ocr:edit")
    @PutMapping("/medical-document-ocr/{taskId}/discard")
    public R<Void> discard(@PathVariable Long taskId) {
        return R.ok(ocrService.discard(taskId));
    }
}
