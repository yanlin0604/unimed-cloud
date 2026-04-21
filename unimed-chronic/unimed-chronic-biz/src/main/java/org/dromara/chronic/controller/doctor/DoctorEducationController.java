package org.dromara.chronic.controller.doctor;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.vo.ChHealthEducationContentVo;
import org.dromara.chronic.manager.EducationPushManager;
import org.dromara.chronic.service.IChHealthEducationService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 医生端宣教推送
 *
 * @author unimed
 */
@Tag(name = "慢病管理-医生端宣教")
@SaCheckLogin
@Validated
@RestController
@RequiredArgsConstructor
public class DoctorEducationController {

    private final IChHealthEducationService healthEducationService;
    private final EducationPushManager educationPushManager;

    @Operation(summary = "宣教内容详情")
    @GetMapping("/chronic/doctor/education-content/{contentId}")
    public R<ChHealthEducationContentVo> contentDetail(
            @Parameter(description = "宣教内容ID") @PathVariable Long contentId) {
        return R.ok(healthEducationService.queryContentById(contentId));
    }

    @Operation(summary = "推送宣教给患者")
    @RepeatSubmit
    @PostMapping("/chronic/doctor/education/push")
    public R<Long> pushToPatient(
            @Parameter(description = "宣教内容ID") @RequestParam Long contentId,
            @Parameter(description = "患者ID") @RequestParam Long patientId,
            @Parameter(description = "触发类型") @RequestParam(defaultValue = "MANUAL") String triggerType,
            @Parameter(description = "推送渠道") @RequestParam(defaultValue = "WECHAT") String pushChannel) {
        return R.ok(educationPushManager.pushToPatient(contentId, patientId, triggerType, pushChannel));
    }
}
