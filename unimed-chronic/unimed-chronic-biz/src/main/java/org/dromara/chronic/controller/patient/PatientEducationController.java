package org.dromara.chronic.controller.patient;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.vo.ChHealthEducationContentVo;
import org.dromara.chronic.domain.vo.ChHealthEducationDeliveryVo;
import org.dromara.chronic.service.IChHealthEducationService;
import org.dromara.common.core.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 患者端宣教
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者端宣教")
@SaCheckLogin
@Validated
@RestController
@RequiredArgsConstructor
public class PatientEducationController {

    private final IChHealthEducationService healthEducationService;

    @Operation(summary = "查询我的宣教列表")
    @GetMapping("/chronic/patient/education/list")
    public R<List<ChHealthEducationDeliveryVo>> myList(@Parameter(description = "患者ID") @RequestParam Long patientId) {
        return R.ok(healthEducationService.queryDeliveriesByPatientId(patientId));
    }

    @Operation(summary = "宣教内容详情")
    @GetMapping("/chronic/patient/education/content/{contentId}")
    public R<ChHealthEducationContentVo> contentDetail(@Parameter(description = "宣教内容ID") @PathVariable Long contentId) {
        return R.ok(healthEducationService.queryContentById(contentId));
    }

    @Operation(summary = "标记已读")
    @PutMapping("/chronic/patient/education/delivery/{deliveryId}/read")
    public R<Void> markRead(@Parameter(description = "宣教下发记录ID") @PathVariable Long deliveryId,
                            @Parameter(description = "停留时长(秒)") @RequestParam(required = false) Integer stayDuration) {
        return R.ok(healthEducationService.markRead(deliveryId, stayDuration));
    }
}
