package org.dromara.dhcore.controller.portal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.dromara.dhcore.domain.bo.portal.PasswordLoginBo;
import org.dromara.dhcore.domain.bo.portal.PortalRegisterBo;
import org.dromara.dhcore.domain.bo.portal.SmsCodeBo;
import org.dromara.dhcore.domain.bo.portal.SmsLoginBo;
import org.dromara.dhcore.domain.vo.portal.PortalLoginVo;
import org.dromara.dhcore.service.IPortalAuthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * C端门户认证控制器
 *
 * @author unimed
 */
@Tag(name = "C端-门户认证")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/portal/auth")
public class PortalAuthController extends BaseController {

    private final IPortalAuthService portalAuthService;

    /**
     * 发送短信验证码
     */
    @Operation(summary = "发送短信验证码")
    @PostMapping("/sms/code")
    public R<String> sendSmsCode(@Validated @RequestBody SmsCodeBo bo) {
        String code = portalAuthService.sendSmsCode(bo);
        return R.ok("验证码发送成功", code);
    }

    /**
     * 短信验证码登录
     */
    @Operation(summary = "短信验证码登录")
    @PostMapping("/login/sms")
    public R<PortalLoginVo> smsLogin(@Validated @RequestBody SmsLoginBo bo) {
        PortalLoginVo vo = portalAuthService.smsLogin(bo);
        return R.ok(vo);
    }

    /**
     * 密码登录
     */
    @Operation(summary = "密码登录")
    @PostMapping("/login/password")
    public R<PortalLoginVo> passwordLogin(@Validated @RequestBody PasswordLoginBo bo) {
        PortalLoginVo vo = portalAuthService.passwordLogin(bo);
        return R.ok(vo);
    }

    /**
     * 登出
     */
    @Operation(summary = "登出")
    @PostMapping("/logout")
    public R<Void> logout() {
        portalAuthService.logout();
        return R.ok();
    }

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public R<PortalLoginVo> register(@Validated @RequestBody PortalRegisterBo bo) {
        PortalLoginVo vo = portalAuthService.register(bo);
        return R.ok(vo);
    }
}
