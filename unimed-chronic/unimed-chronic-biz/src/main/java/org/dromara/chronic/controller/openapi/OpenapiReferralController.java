package org.dromara.chronic.controller.openapi;

import org.dromara.common.web.core.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChReferralRecordBo;
import org.dromara.chronic.domain.vo.ChReferralRecordVo;
import org.dromara.chronic.service.IChReferralService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 转诊开放接口
 *
 * @author unimed
 */
@Tag(name = "慢病管理-开放接口-转诊")
@Validated
@RestController
@RequiredArgsConstructor
public class OpenapiReferralController extends BaseController {

    private final IChReferralService referralService;

    @Operation(summary = "外部发起转诊")
    @RepeatSubmit
    @PostMapping("/chronic/openapi/referral")
    public R<Long> create(@Validated @RequestBody ChReferralRecordBo bo) {
        referralService.logSync("REFERRAL", "INBOUND", "HIS", "SUCCESS", "HIS发起转诊");
        return R.ok(referralService.createReferral(bo));
    }

    @Operation(summary = "查询转诊记录")
    @GetMapping("/chronic/openapi/referral/{referralId}")
    public R<ChReferralRecordVo> query(@Parameter(description = "转诊记录ID") @PathVariable Long referralId) {
        return R.ok(referralService.queryById(referralId));
    }
}
