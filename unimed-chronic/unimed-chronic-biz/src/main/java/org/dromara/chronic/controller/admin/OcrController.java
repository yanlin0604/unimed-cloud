package org.dromara.chronic.controller.admin;

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
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理端医疗文档OCR
 *
 * @author unimed
 */
@Tag(name = "慢病管理-医疗文档OCR")
@Validated
@RestController
@RequiredArgsConstructor
public class OcrController extends BaseController {

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

    @Operation(summary = "重试医疗文档OCR识别")
    @SaCheckPermission("chronic:medical-document-ocr:edit")
    @Log(title = "医疗文档OCR重试", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping("/chronic/admin/medical-document-ocr/{taskId}/retry")
    public R<OcrTaskVo> retry(@Parameter(description = "任务ID") @PathVariable Long taskId) {
        return R.ok(ocrManager.retry(taskId));
    }
    @Operation(summary = "快速识别医疗文档并提取结构化数据")
    @SaCheckPermission("chronic:medical-document-ocr:add")
    @PostMapping("/chronic/admin/ocr/recognize")
    public R<Map<String, Object>> recognizeDirect(@RequestBody Map<String, Object> body) {
        String image = (String) body.get("image");
        String type = (String) body.get("type");

        OcrTaskBo bo = new OcrTaskBo();
        bo.setSourceType("ADMIN");
        bo.setDocumentType("exam".equalsIgnoreCase(type) ? "MEDICAL_EXAM" : "LAB_TEST");

        if (image != null && image.startsWith("http")) {
            bo.setInputType("IMAGE_URL");
            bo.setFileUrl(image);
        } else {
            bo.setInputType("IMAGE_BASE64");
            String base64 = image != null && image.contains(",") ? image.substring(image.indexOf(",") + 1) : image;
            bo.setImageBase64(base64);
        }

        try {
            Long taskId = ocrManager.recognize(bo);
            OcrTaskVo task = ocrService.queryById(taskId);

            Map<String, Object> data = new HashMap<>();
            if (task != null && StringUtils.isNotBlank(task.getReportDraftJson())) {
                Map<String, Object> draftMap = JsonUtils.parseMap(task.getReportDraftJson());
                if (draftMap != null) {
                    data.putAll(draftMap);
                    if (draftMap.containsKey("reportTime")) {
                        data.put("testDate", draftMap.get("reportTime"));
                        data.put("examDate", draftMap.get("reportTime"));
                    }
                    if (draftMap.containsKey("reportTitle")) {
                        data.put("testType", draftMap.get("reportTitle"));
                        data.put("examType", draftMap.get("reportTitle"));
                    }
                    if (draftMap.containsKey("diagnosisSummary")) {
                        data.put("examConclusion", draftMap.get("diagnosisSummary"));
                    }
                }
            }
            if (task != null && task.getReportItems() != null) {
                List<Map<String, Object>> items = task.getReportItems().stream().map(it -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("itemName", it.getItemName());
                    m.put("itemCode", it.getItemCode());
                    m.put("itemValue", it.getResultValue());
                    m.put("unit", it.getUnit());
                    m.put("referenceRange", it.getReferenceRange());
                    m.put("abnormalFlag", it.getIsAbnormal() != null && it.getIsAbnormal() ? "1" : "0");
                    return m;
                }).collect(Collectors.toList());
                data.put("testItems", items);
            }

            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("data", data);
            res.put("confidence", 0.95);
            res.put("taskId", taskId);
            return R.ok(res);
        } catch (Exception e) {
            Map<String, Object> res = new HashMap<>();
            res.put("success", false);
            res.put("message", e.getMessage());
            return R.ok(res);
        }
    }

    @Operation(summary = "废弃医疗文档OCR草稿")
    @SaCheckPermission("chronic:medical-document-ocr:edit")
    @Log(title = "医疗文档OCR废弃", businessType = BusinessType.UPDATE)
    @PutMapping("/chronic/admin/medical-document-ocr/{taskId}/discard")
    public R<Void> discard(@Parameter(description = "任务ID") @PathVariable Long taskId) {
        return R.ok(ocrService.discard(taskId));
    }
}
