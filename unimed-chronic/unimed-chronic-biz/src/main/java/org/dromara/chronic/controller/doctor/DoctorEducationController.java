package org.dromara.chronic.controller.doctor;

import org.dromara.common.web.core.BaseController;
import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChHealthEducationContentBo;
import org.dromara.chronic.domain.vo.ChHealthEducationContentVo;
import org.dromara.chronic.domain.vo.ChHealthEducationDeliveryVo;
import org.dromara.chronic.manager.EducationPushManager;
import org.dromara.chronic.service.IChHealthEducationService;
import org.dromara.chronic.support.DoctorScopeGuard;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
public class DoctorEducationController extends BaseController {

    private final IChHealthEducationService healthEducationService;
    private final EducationPushManager educationPushManager;
    private final DoctorScopeGuard doctorScopeGuard;

    @Operation(summary = "分页查询宣教内容库")
    @GetMapping("/chronic/doctor/education-content/page")
    public TableDataInfo<ChHealthEducationContentVo> contentPage(ChHealthEducationContentBo bo, PageQuery pageQuery) {
        return healthEducationService.queryContentPageList(bo, pageQuery);
    }

    @Operation(summary = "宣教内容详情")
    @GetMapping("/chronic/doctor/education-content/{contentId}")
    public R<ChHealthEducationContentVo> contentDetail(
            @Parameter(description = "宣教内容ID") @PathVariable Long contentId) {
        return R.ok(healthEducationService.queryContentById(contentId));
    }

    @Operation(summary = "查询患者宣教推送记录")
    @GetMapping("/chronic/doctor/patient/{patientId}/education-deliveries")
    public R<List<ChHealthEducationDeliveryVo>> patientDeliveries(
            @Parameter(description = "患者ID") @PathVariable Long patientId) {
        // 推送记录属患者数据（能看出患者被推送过哪些病种宣教，可反推病情）
        doctorScopeGuard.assertPatientOwned(patientId);
        return R.ok(healthEducationService.queryDeliveriesByPatientId(patientId));
    }

    @Operation(summary = "推送宣教给患者")
    @RepeatSubmit
    @PostMapping("/chronic/doctor/education/push")
    public R<Long> pushToPatient(
            @Parameter(description = "宣教内容ID") @RequestParam Long contentId,
            @Parameter(description = "患者ID") @RequestParam Long patientId,
            @Parameter(description = "触发类型") @RequestParam(defaultValue = "MANUAL") String triggerType,
            @Parameter(description = "推送渠道") @RequestParam(defaultValue = "WECHAT") String pushChannel) {
        // 向患者推送内容属写操作，且会触发微信/短信外发，必须限定自己名下患者
        doctorScopeGuard.assertPatientOwned(patientId);
        return R.ok(educationPushManager.pushToPatient(contentId, patientId, triggerType, pushChannel));
    }
}
