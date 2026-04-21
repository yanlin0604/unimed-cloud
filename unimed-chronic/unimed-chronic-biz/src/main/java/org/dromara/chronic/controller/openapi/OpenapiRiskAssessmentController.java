package org.dromara.chronic.controller.openapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChRiskAssessmentBo;
import org.dromara.chronic.domain.vo.ChRiskAssessmentVo;
import org.dromara.chronic.manager.RiskAssessmentManager;
import org.dromara.chronic.service.impl.ChRiskAssessmentServiceImpl;
import org.dromara.common.core.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 风险评估开放接口
 *
 * @author unimed
 */
@Tag(name = "慢病管理-开放接口-风险评估")
@Validated
@RestController
@RequiredArgsConstructor
public class OpenapiRiskAssessmentController {

    private final RiskAssessmentManager riskAssessmentManager;

    @Operation(summary = "调用风险评估")
    @PostMapping("/chronic/openapi/risk-assessment/invoke")
    public R<ChRiskAssessmentVo> invoke(@Validated @RequestBody ChRiskAssessmentBo bo) {
        return R.ok(riskAssessmentManager.assess(bo));
    }

    @Operation(summary = "获取筛查嵌入URL")
    @GetMapping("/chronic/openapi/screening/embed-url")
    public R<Map<String, String>> embedUrl(
            @Parameter(description = "患者ID") @RequestParam(required = false) Long patientId) {
        String url = "/chronic/doctor/screening/start";
        if (patientId != null) {
            url = url + "?patientId=" + patientId;
        }
        return R.ok(Map.of("embedUrl", url));
    }

    @Operation(summary = "注册异步回调")
    @PostMapping("/chronic/openapi/risk-assessment/async-callback")
    public R<Void> registerCallback(@RequestBody Map<String, Object> body) {
        String bizKey = String.valueOf(body.get("bizKey"));
        String callbackUrl = String.valueOf(body.get("callbackUrl"));
        ChRiskAssessmentServiceImpl.CALLBACK_REGISTRY.put(bizKey, callbackUrl);
        return R.ok();
    }
}
