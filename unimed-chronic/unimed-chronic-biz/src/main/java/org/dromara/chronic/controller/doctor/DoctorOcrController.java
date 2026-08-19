package org.dromara.chronic.controller.doctor;

import org.dromara.common.web.core.BaseController;
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
import org.dromara.chronic.support.DoctorScopeGuard;
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
public class DoctorOcrController extends BaseController {

    private final OcrManager ocrManager;
    private final IOcrService ocrService;
    private final DoctorScopeGuard doctorScopeGuard;

    /**
     * 解析 OCR 任务归属患者并校验
     * <p>
     * detail/confirm/retry/discard 的路径参数是 taskId，原实现零校验。
     * confirm 尤其危险：确认他人 OCR 草稿会把识别结果写入该患者档案与健康指标。
     */
    private void assertTaskOwned(Long taskId) {
        OcrTaskVo task = ocrService.queryById(taskId);
        doctorScopeGuard.assertRecordOwned(task == null ? null : task.getPatientId());
    }

    @Operation(summary = "创建患者医疗文档OCR任务")
    @SaCheckPermission("chronic:doctor:medical-document-ocr:add")
    @Log(title = "医生端医疗文档OCR", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/patient/{patientId}/medical-document-ocr/tasks")
    public R<Long> add(@PathVariable Long patientId, @Validated @RequestBody OcrTaskBo bo) {
        doctorScopeGuard.assertPatientOwned(patientId);
        bo.setPatientId(patientId);
        bo.setSourceType("DOCTOR");
        return R.ok(ocrManager.recognize(bo));
    }

    @Operation(summary = "患者医疗文档OCR任务分页")
    @SaCheckPermission("chronic:doctor:medical-document-ocr:list")
    @GetMapping("/patient/{patientId}/medical-document-ocr/page")
    public TableDataInfo<OcrTaskVo> page(@PathVariable Long patientId, OcrTaskBo bo, PageQuery pageQuery) {
        doctorScopeGuard.assertPatientOwned(patientId);
        bo.setPatientId(patientId);
        bo.setSourceType("DOCTOR");
        return ocrService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "医疗文档OCR任务详情")
    @SaCheckPermission("chronic:doctor:medical-document-ocr:query")
    @GetMapping("/medical-document-ocr/{taskId}")
    public R<OcrTaskVo> detail(@PathVariable Long taskId) {
        assertTaskOwned(taskId);
        return R.ok(ocrService.queryById(taskId));
    }

    @Operation(summary = "确认医疗文档OCR草稿")
    @SaCheckPermission("chronic:doctor:medical-document-ocr:edit")
    @Log(title = "医生端医疗文档OCR确认", businessType = BusinessType.UPDATE)
    @PutMapping("/medical-document-ocr/{taskId}/confirm")
    public R<OcrConfirmResult> confirm(@PathVariable Long taskId, @Validated @RequestBody OcrConfirmBo bo) {
        assertTaskOwned(taskId);
        return R.ok(ocrManager.confirm(taskId, bo));
    }

    @Operation(summary = "重试医疗文档OCR识别")
    @SaCheckPermission("chronic:doctor:medical-document-ocr:edit")
    @Log(title = "医生端医疗文档OCR重试", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping("/medical-document-ocr/{taskId}/retry")
    public R<OcrTaskVo> retry(@Parameter(description = "任务ID") @PathVariable Long taskId) {
        assertTaskOwned(taskId);
        return R.ok(ocrManager.retry(taskId));
    }

    @Operation(summary = "废弃医疗文档OCR草稿")
    @SaCheckPermission("chronic:doctor:medical-document-ocr:edit")
    @PutMapping("/medical-document-ocr/{taskId}/discard")
    public R<Void> discard(@PathVariable Long taskId) {
        assertTaskOwned(taskId);
        return R.ok(ocrService.discard(taskId));
    }
}
