package org.dromara.chronic.controller.patient;

import org.dromara.common.web.core.BaseController;
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
import org.dromara.chronic.support.PatientContextHelper;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Random;

import lombok.extern.slf4j.Slf4j;
import org.dromara.common.redis.utils.RedisUtils;

/**
 * 患者端认证接口
 * <p>
 * 安全约定：本控制器整体处于网关白名单内（unimed-gateway.yml 的
 * {@code /chronic/chronic/patient/auth/**}），因此**网关不做登录校验**，
 * 每个需要登录的端点必须自己带 {@code @SaCheckLogin}。
 * <p>
 * 另外，患者端的 {@code LoginHelper.getUserId()} 返回的是 <b>accountId</b>（账号ID），
 * 不是 patientId（患者档案ID）；凡是以 accountId 为入参的端点都必须校验其等于登录账号，
 * 否则可通过枚举 id 越权操作他人账号（线上 accountId 为 21001~21008 连续整数）。
 *
 * @author unimed
 */
@Slf4j
@Tag(name = "慢病管理-患者端认证")
@Validated
@RestController
@RequiredArgsConstructor
public class PatientAuthController extends BaseController {

    private final IChPatientAccountService patientAccountService;
    private final PatientContextHelper patientContextHelper;


    private static final String SMS_CODE_KEY = "chronic:sms:code:";
    private static final Random RANDOM = new Random();

    /**
     * 校验目标账号即当前登录账号，用于所有以 accountId 为入参的端点。
     * <p>
     * 错误信息不区分「不存在」与「无权」，避免被用来探测账号是否存在。
     *
     * @param accountId 目标账号ID
     */
    private void assertOwnAccount(Long accountId) {
        Long loginAccountId = LoginHelper.getUserId();
        if (loginAccountId == null || accountId == null || !accountId.equals(loginAccountId)) {
            log.warn("patient-account-scope-denied: loginAccountId={}, targetAccountId={}", loginAccountId, accountId);
            throw new ServiceException("账号不存在或无权操作");
        }
    }

    /**
     * 发送短信验证码（开发环境：生成随机码打印到日志）
     */
    @Operation(summary = "发送短信验证码")
    @GetMapping("/chronic/patient/auth/sms/send")
    public R<String> sendSmsCode(@Parameter(description = "手机号") @RequestParam String phone) {
        String code = String.format("%06d", RANDOM.nextInt(1000000));
        RedisUtils.setCacheObject(SMS_CODE_KEY + phone, code, Duration.ofMinutes(5));
        log.info("【慢病管理-短信验证码】手机号: {}，验证码: {}（5分钟内有效）", phone, code);
        return R.ok("验证码已发送", code);
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

    /*
     * 已移除：GET /chronic/patient/auth/wechat?openid=xxx
     *
     * 该端点无 @SaCheckLogin，且整个 /chronic/chronic/patient/auth/** 在网关白名单内
     * （unimed-gateway.yml），属**完全公开**接口，却直接返回 ChPatientAccountVo
     * 全量字段（手机号、patientId、openid），是一个未认证的 PII 泄露入口。
     *
     * 移除依据：全仓零调用方 —— 患者端前端只调 sms/send、phone、wechat/code 三个
     * auth 端点（chronic-patient/src/api/auth.ts:41,48,55），后端亦无引用。
     * 微信登录的实际入口是 POST /wechat/code（loginByWxCode），它内部会调
     * patientAccountService.queryByOpenid()，服务层方法保留不受影响。
     *
     * 若后续确需「按 openid 查是否已绑定」的能力，请勿吐 PII，
     * 只返回 {needBind: true/false} 这类布尔结果，并加 @SaCheckLogin。
     */

    /**
     * 查询当前患者账号
     */
    @Operation(summary = "查询患者账号")
    @SaCheckLogin
    @GetMapping("/chronic/patient/auth/account/{patientId}")
    public R<ChPatientAccountVo> queryByPatientId(@Parameter(description = "患者ID") @PathVariable Long patientId) {
        // 只允许查询自己档案对应的账号：原实现直接按传入 patientId 查询，
        // 而 patientId 是 1001~1010 连续整数，可枚举读取他人手机号与 openid。
        Long currentPatientId = patientContextHelper.getCurrentPatientId();
        if (!currentPatientId.equals(patientId)) {
            log.warn("patient-scope-denied: currentPatientId={}, targetPatientId={}", currentPatientId, patientId);
            throw new ServiceException("账号不存在或无权访问");
        }
        return R.ok(patientAccountService.queryByPatientId(patientId));
    }

    /**
     * 更新授权范围
     */
    @Operation(summary = "更新授权范围")
    @SaCheckLogin
    @PutMapping("/chronic/patient/auth/family/{accountId}/scope")
    public R<Boolean> updateAuthScope(@Parameter(description = "账号ID") @PathVariable Long accountId, @Parameter(description = "授权范围") @RequestParam String authScope) {
        // 只能修改「自己」或「自己名下家属代管账号」的授权范围。
        // 原实现仅校验账号存在，任意登录患者可把他人代管账号的只读授权改成完全授权（提权）。
        Long loginAccountId = LoginHelper.getUserId();
        ChPatientAccountVo target = patientAccountService.getAccountById(accountId);
        boolean isSelf = accountId.equals(loginAccountId);
        boolean isOwnProxy = target != null && loginAccountId != null
            && loginAccountId.equals(target.getMasterAccountId());
        if (target == null || !(isSelf || isOwnProxy)) {
            log.warn("patient-authscope-denied: loginAccountId={}, targetAccountId={}", loginAccountId, accountId);
            throw new ServiceException("账号不存在或无权操作");
        }
        return R.ok(patientAccountService.updateAuthScope(accountId, authScope));
    }

    /**
     * 绑定微信（已登录用户通过 wx.login code 绑定 openid）
     * <p>
     * accountId 一律取登录态，不再接收前端传参：原实现由前端传 accountId 且不校验归属，
     * 攻击者可把自己微信绑到任一「尚未绑微信」的账号上，之后微信一键登录直接进入该账号。
     */
    @Operation(summary = "绑定微信")
    @SaCheckLogin
    @RepeatSubmit
    @PostMapping("/chronic/patient/auth/bindwechat")
    public R<Boolean> bindWechat(@Parameter(description = "wx.login code") @RequestParam String code) {
        Long loginAccountId = LoginHelper.getUserId();
        if (loginAccountId == null) {
            throw new ServiceException("未登录");
        }
        return R.ok(patientAccountService.bindWechat(loginAccountId, code));
    }

    /**
     * 查询当前登录账号信息
     */
    @Operation(summary = "查询账号信息")
    @SaCheckLogin
    @GetMapping("/chronic/patient/auth/info/{accountId}")
    public R<ChPatientAccountVo> getAccountInfo(@Parameter(description = "账号ID") @PathVariable Long accountId) {
        // 路径参数保留以兼容既有前端调用，但只允许查询本人账号
        assertOwnAccount(accountId);
        return R.ok(patientAccountService.getAccountById(accountId));
    }

    /**
     * 更新用户昵称和头像
     * <p>
     * accountId 一律取登录态：原实现由前端传参且不校验归属，可篡改他人昵称与头像。
     * 前端若仍传 accountId 查询参数会被 Spring 忽略，无需同步改动。
     */
    @Operation(summary = "更新用户信息")
    @SaCheckLogin
    @PutMapping("/chronic/patient/auth/info")
    public R<Boolean> updateAccountInfo(@Parameter(description = "昵称") @RequestParam(required = false) String nickname,
                                         @Parameter(description = "头像OSS ID") @RequestParam(required = false) String avatarOssId) {
        Long loginAccountId = LoginHelper.getUserId();
        if (loginAccountId == null) {
            throw new ServiceException("未登录");
        }
        return R.ok(patientAccountService.updateAccountInfo(loginAccountId, nickname, avatarOssId));
    }
}
