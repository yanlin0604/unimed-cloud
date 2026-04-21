package org.dromara.chronic.controller.doctor;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChReferralRecordBo;
import org.dromara.chronic.domain.vo.ChReferralRecordVo;
import org.dromara.chronic.service.IChReferralService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 医生端转诊
 *
 * @author unimed
 */
@Tag(name = "慢病管理-医生端转诊")
@SaCheckLogin
@Validated
@RestController
@RequiredArgsConstructor
public class DoctorReferralController {

    private final IChReferralService referralService;

    @Operation(summary = "新增转诊")
    @RepeatSubmit
    @PostMapping("/chronic/doctor/referral")
    public R<Long> create(@Validated @RequestBody ChReferralRecordBo bo) {
        return R.ok(referralService.createReferral(bo));
    }

    @Operation(summary = "查询患者转诊记录")
    @GetMapping("/chronic/doctor/referral/patient/{patientId}")
    public R<List<ChReferralRecordVo>> patientReferrals(@Parameter(description = "患者ID") @PathVariable Long patientId) {
        return R.ok(referralService.queryByPatientId(patientId));
    }

    @Operation(summary = "分页查询转诊")
    @GetMapping("/chronic/doctor/referral/page")
    public TableDataInfo<ChReferralRecordVo> page(ChReferralRecordBo bo, PageQuery pageQuery) {
        return referralService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "更新转诊状态")
    @PutMapping("/chronic/doctor/referral/{referralId}/status")
    public R<Void> updateStatus(@Parameter(description = "转诊ID") @PathVariable Long referralId, @Parameter(description = "状态") @RequestParam String status) {
        return R.ok(referralService.updateStatus(referralId, status));
    }
}
