package org.dromara.chronic.controller.doctor;

import org.dromara.common.web.core.BaseController;
import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChReferralRecordBo;
import org.dromara.chronic.domain.vo.ChReferralRecordVo;
import org.dromara.chronic.service.IChReferralService;
import org.dromara.chronic.support.DoctorScopeGuard;
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
public class DoctorReferralController extends BaseController {

    private final IChReferralService referralService;
    private final DoctorScopeGuard doctorScopeGuard;

    /**
     * 解析转诊记录归属患者并校验
     */
    private void assertReferralOwned(Long referralId) {
        ChReferralRecordVo record = referralService.queryById(referralId);
        doctorScopeGuard.assertRecordOwned(record == null ? null : record.getPatientId());
    }

    @Operation(summary = "新增转诊")
    @RepeatSubmit
    @PostMapping("/chronic/doctor/referral")
    public R<Long> create(@Validated @RequestBody ChReferralRecordBo bo) {
        // patientId 来自请求体，必须校验归属，否则可给任意患者发起转诊
        doctorScopeGuard.assertPatientOwned(bo.getPatientId());
        return R.ok(referralService.createReferral(bo));
    }

    @Operation(summary = "查询患者转诊记录")
    @GetMapping("/chronic/doctor/referral/patient/{patientId}")
    public R<List<ChReferralRecordVo>> patientReferrals(@Parameter(description = "患者ID") @PathVariable Long patientId) {
        doctorScopeGuard.assertPatientOwned(patientId);
        return R.ok(referralService.queryByPatientId(patientId));
    }

    @Operation(summary = "分页查询转诊")
    @GetMapping("/chronic/doctor/referral/page")
    public TableDataInfo<ChReferralRecordVo> page(ChReferralRecordBo bo, PageQuery pageQuery) {
        // 本控制器类上只有 @SaCheckLogin、没有任何权限码，且 queryPageList 不按医生过滤
        // （ChReferralRecordBo.referralUserId 存在但 Service 未使用），
        // 原实现等于任意登录身份可翻阅全部转诊记录。
        // 服务层归 admin 与 doctor 共用、不能改，故在此强制要求患者维度并校验归属。
        doctorScopeGuard.assertPatientOwned(bo.getPatientId());
        return referralService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "更新转诊状态")
    @PutMapping("/chronic/doctor/referral/{referralId}/status")
    public R<Void> updateStatus(@Parameter(description = "转诊ID") @PathVariable Long referralId, @Parameter(description = "状态") @RequestParam String status) {
        assertReferralOwned(referralId);
        return R.ok(referralService.updateStatus(referralId, status));
    }
}
