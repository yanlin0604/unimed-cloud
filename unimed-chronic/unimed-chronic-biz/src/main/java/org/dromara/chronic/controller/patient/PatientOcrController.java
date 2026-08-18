package org.dromara.chronic.controller.patient;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.OcrTaskBo;
import org.dromara.chronic.domain.vo.OcrTaskVo;
import org.dromara.chronic.manager.OcrManager;
import org.dromara.chronic.service.IOcrService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.chronic.support.PatientContextHelper;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 患者端医疗文档OCR
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者端医疗文档OCR")
@SaCheckLogin
@Validated
@RestController
@RequiredArgsConstructor
public class PatientOcrController {

    private final OcrManager ocrManager;
    private final PatientContextHelper patientContextHelper;
    private final IOcrService ocrService;

    @Operation(summary = "创建本人医疗文档OCR任务")
    @RepeatSubmit
    @PostMapping("/chronic/patient/medical-document-ocr/tasks")
    public R<Long> add(@Validated @RequestBody OcrTaskBo bo) {
        bo.setPatientId(patientContextHelper.getCurrentPatientId());
        bo.setSourceType("PATIENT");
        return R.ok(ocrManager.recognize(bo));
    }

    @Operation(summary = "本人医疗文档OCR任务分页")
    @GetMapping("/chronic/patient/medical-document-ocr/page")
    public TableDataInfo<OcrTaskVo> page(OcrTaskBo bo,
                                                        PageQuery pageQuery) {
        bo.setPatientId(patientContextHelper.getCurrentPatientId());
        bo.setSourceType("PATIENT");
        return ocrService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "医疗文档OCR任务详情")
    @GetMapping("/chronic/patient/medical-document-ocr/{taskId}")
    public R<OcrTaskVo> detail(@PathVariable Long taskId) {
        return R.ok(ocrService.queryById(taskId));
    }

    @Operation(summary = "废弃医疗文档OCR草稿")
    @PutMapping("/chronic/patient/medical-document-ocr/{taskId}/discard")
    public R<Void> discard(@PathVariable Long taskId) {
        return R.ok(ocrService.discard(taskId));
    }
}
