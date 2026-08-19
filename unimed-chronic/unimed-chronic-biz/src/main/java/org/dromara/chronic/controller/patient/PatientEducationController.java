package org.dromara.chronic.controller.patient;

import org.dromara.common.web.core.BaseController;
import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.vo.ChHealthEducationContentVo;
import org.dromara.chronic.domain.vo.ChHealthEducationDeliveryVo;
import org.dromara.chronic.service.IChHealthEducationService;
import org.dromara.chronic.support.PatientContextHelper;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
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
public class PatientEducationController extends BaseController {

    private final IChHealthEducationService healthEducationService;
    private final PatientContextHelper patientContextHelper;

    @Operation(summary = "查询我的宣教列表")
    @GetMapping("/chronic/patient/education/list")
    public R<List<ChHealthEducationDeliveryVo>> myList() {
        // 原实现用 LoginHelper.getUserId()，那是 accountId 不是 patientId（线上 21001~21008 vs 1001~1010），
        // 查不到任何数据 → 「我的宣教」永远为空；且 patientId 增长到区间重叠后即变成跨患者泄露。
        Long patientId = patientContextHelper.getCurrentPatientId();
        return R.ok(healthEducationService.queryDeliveriesByPatientId(patientId));
    }

    @Operation(summary = "宣教内容详情")
    @GetMapping("/chronic/patient/education/content/{contentId}")
    public R<ChHealthEducationContentVo> contentDetail(@Parameter(description = "宣教内容ID") @PathVariable Long contentId) {
        // 宣教内容是面向全体患者的公共知识库（非患者私有数据），无需归属校验，
        // 登录态即可访问。这与下方按「下发记录」维度的 markRead 不同。
        return R.ok(healthEducationService.queryContentById(contentId));
    }

    @Operation(summary = "标记已读")
    @PutMapping("/chronic/patient/education/delivery/{deliveryId}/read")
    public R<Void> markRead(@Parameter(description = "宣教下发记录ID") @PathVariable Long deliveryId,
                            @Parameter(description = "停留时长(秒)") @RequestParam(required = false) Integer stayDuration) {
        // 原实现只校验记录存在，任意患者可把他人的宣教下发记录标记为已读（污染依从性统计）。
        // 服务层没有「按 deliveryId 查单条」的只读方法，因此改为在本人下发列表中比对，
        // 避免为此改动被 admin 层复用的共享 Service。
        Long patientId = patientContextHelper.getCurrentPatientId();
        boolean owned = healthEducationService.queryDeliveriesByPatientId(patientId).stream()
            .anyMatch(d -> deliveryId.equals(d.getDeliveryId()));
        if (!owned) {
            throw new ServiceException("宣教记录不存在或无权操作");
        }
        return R.ok(healthEducationService.markRead(deliveryId, stayDuration));
    }
}
