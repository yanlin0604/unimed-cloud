package org.dromara.chronic.controller.patient;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChPatientAccountBo;
import org.dromara.chronic.domain.vo.ChPatientAccountVo;
import org.dromara.chronic.service.IChPatientAccountService;
import org.dromara.common.core.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 患者端认证接口
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者端认证")
@Validated
@RestController
@RequiredArgsConstructor
public class PatientAuthController {

    private final IChPatientAccountService patientAccountService;

    /**
     * 手机号注册/登录
     */
    @Operation(summary = "手机号注册登录")
    @PostMapping("/chronic/patient/auth/phone")
    public R<Long> phoneRegister(@Validated @RequestBody ChPatientAccountBo bo) {
        return R.ok(patientAccountService.register(bo));
    }

    /**
     * 微信登录：通过openid查询
     */
    @Operation(summary = "微信登录")
    @GetMapping("/chronic/patient/auth/wechat")
    public R<ChPatientAccountVo> wechatLogin(@Parameter(description = "微信OpenID") @RequestParam String openid) {
        return R.ok(patientAccountService.queryByOpenid(openid));
    }

    /**
     * 查询当前患者账号
     */
    @Operation(summary = "查询患者账号")
    @SaCheckLogin
    @GetMapping("/chronic/patient/auth/account/{patientId}")
    public R<ChPatientAccountVo> queryByPatientId(@Parameter(description = "患者ID") @PathVariable Long patientId) {
        return R.ok(patientAccountService.queryByPatientId(patientId));
    }

    /**
     * 绑定家属代管
     */
    @Operation(summary = "绑定家属代管")
    @SaCheckLogin
    @PostMapping("/chronic/patient/auth/family/bind")
    public R<Boolean> bindFamilyProxy(@Validated @RequestBody ChPatientAccountBo bo) {
        return R.ok(patientAccountService.bindFamilyProxy(bo));
    }

    /**
     * 解绑家属代管
     */
    @Operation(summary = "解绑家属代管")
    @SaCheckLogin
    @DeleteMapping("/chronic/patient/auth/family/{accountId}")
    public R<Boolean> unbindFamilyProxy(@Parameter(description = "账号ID") @PathVariable Long accountId) {
        return R.ok(patientAccountService.unbindFamilyProxy(accountId));
    }

    /**
     * 查询家属代管列表
     */
    @Operation(summary = "查询家属代管列表")
    @SaCheckLogin
    @GetMapping("/chronic/patient/auth/family/{masterAccountId}")
    public R<List<ChPatientAccountVo>> listFamilyProxies(@Parameter(description = "主账号ID") @PathVariable Long masterAccountId) {
        return R.ok(patientAccountService.queryFamilyProxies(masterAccountId));
    }

    /**
     * 更新授权范围
     */
    @Operation(summary = "更新授权范围")
    @SaCheckLogin
    @PutMapping("/chronic/patient/auth/family/{accountId}/scope")
    public R<Boolean> updateAuthScope(@Parameter(description = "账号ID") @PathVariable Long accountId, @Parameter(description = "授权范围") @RequestParam String authScope) {
        return R.ok(patientAccountService.updateAuthScope(accountId, authScope));
    }
}
