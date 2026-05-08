package org.dromara.chronic.controller.patient;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChPatientAccountBo;
import org.dromara.chronic.domain.bo.WxLoginCodeBo;
import org.dromara.chronic.domain.vo.ChPatientAccountVo;
import org.dromara.chronic.domain.vo.WxLoginVo;
import org.dromara.chronic.service.IChPatientAccountService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Random;

import lombok.extern.slf4j.Slf4j;
import org.dromara.common.redis.utils.RedisUtils;

/**
 * 患者端认证接口
 *
 * @author unimed
 */
@Slf4j
@Tag(name = "慢病管理-患者端认证")
@Validated
@RestController
@RequiredArgsConstructor
public class PatientAuthController {

    private final IChPatientAccountService patientAccountService;


    private static final String SMS_CODE_KEY = "chronic:sms:code:";
    private static final Random RANDOM = new Random();

    /**
     * 发送短信验证码（开发环境：生成随机码打印到日志）
     */
    @Operation(summary = "发送短信验证码")
    @GetMapping("/chronic/patient/auth/sms/send")
    public R<Boolean> sendSmsCode(@Parameter(description = "手机号") @RequestParam String phone) {
        String code = String.format("%06d", RANDOM.nextInt(1000000));
        RedisUtils.setCacheObject(SMS_CODE_KEY + phone, code, Duration.ofMinutes(5));
        log.info("【慢病管理-短信验证码】手机号: {}，验证码: {}（5分钟内有效）", phone, code);
        return R.ok(true);
    }

    /**
     * 手机号注册/登录
     */
    @Operation(summary = "手机号注册登录")
    @PostMapping("/chronic/patient/auth/phone")
    public R<WxLoginVo> phoneRegister(@Validated @RequestBody ChPatientAccountBo bo) {
        return R.ok(patientAccountService.register(bo));
    }

    /**
     * 微信小程序code登录
     */
    @Operation(summary = "微信小程序code登录")
    @PostMapping("/chronic/patient/auth/wechat/code")
    @RepeatSubmit
    public R<WxLoginVo> loginByWxCode(@Validated @RequestBody WxLoginCodeBo bo) {
        return R.ok(patientAccountService.loginByWxCode(bo));
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

    /**
     * 绑定微信（已登录用户通过 wx.login code 绑定 openid）
     */
    @Operation(summary = "绑定微信")
    @SaCheckLogin
    @RepeatSubmit
    @PostMapping("/chronic/patient/auth/bindwechat")
    public R<Boolean> bindWechat(@Parameter(description = "账号ID") @RequestParam Long accountId,
                                  @Parameter(description = "wx.login code") @RequestParam String code) {
        return R.ok(patientAccountService.bindWechat(accountId, code));
    }

    /**
     * 查询当前登录账号信息
     */
    @Operation(summary = "查询账号信息")
    @SaCheckLogin
    @GetMapping("/chronic/patient/auth/info/{accountId}")
    public R<ChPatientAccountVo> getAccountInfo(@Parameter(description = "账号ID") @PathVariable Long accountId) {
        return R.ok(patientAccountService.getAccountById(accountId));
    }

    /**
     * 更新用户昵称和头像
     */
    @Operation(summary = "更新用户信息")
    @SaCheckLogin
    @PutMapping("/chronic/patient/auth/info")
    public R<Boolean> updateAccountInfo(@Parameter(description = "账号ID") @RequestParam Long accountId,
                                         @Parameter(description = "昵称") @RequestParam(required = false) String nickname,
                                         @Parameter(description = "头像OSS ID") @RequestParam(required = false) String avatarOssId) {
        return R.ok(patientAccountService.updateAccountInfo(accountId, nickname, avatarOssId));
    }
}
