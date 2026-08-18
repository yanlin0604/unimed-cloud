package org.dromara.chronic.controller.doctor;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChDoctorWechatBindBo;
import org.dromara.chronic.domain.vo.ChDoctorWechatBindVo;
import org.dromara.chronic.domain.vo.DoctorLoginVo;
import org.dromara.chronic.service.IChDoctorWechatBindService;
import org.dromara.common.core.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 医生端认证接口
 *
 * @author unimed
 */
@Tag(name = "慢病管理-医生端认证")
@Validated
@RestController
@RequiredArgsConstructor
public class DoctorAuthController {

    private final IChDoctorWechatBindService wechatBindService;

    /**
     * 微信小程序登录：code -> openid -> 绑定的 sys_user -> 签发 token
     * 未登录可访问
     */
    @Operation(summary = "微信小程序登录")
    @PostMapping("/chronic/doctor/auth/wechat/code")
    public R<DoctorLoginVo> wechatCodeLogin(@Validated @RequestBody WxCodeLoginBody body) {
        return R.ok(wechatBindService.loginByWxCode(body.getCode()));
    }

    /**
     * 微信登录请求体
     */
    @lombok.Data
    public static class WxCodeLoginBody {
        @jakarta.validation.constraints.NotBlank(message = "微信 code 不能为空")
        private String code;
    }

    /**
     * 微信登录：通过openid查询绑定关系，映射到sys_user
     */
    @Operation(summary = "微信登录")
    @SaCheckLogin
    @GetMapping("/chronic/doctor/auth/wechat")
    public R<ChDoctorWechatBindVo> wechatLogin(@Parameter(description = "微信OpenID") @RequestParam String openid) {
        return R.ok(wechatBindService.queryByOpenid(openid));
    }

    /**
     * 绑定微信：openid→sys_user_id映射
     */
    @Operation(summary = "绑定微信")
    @SaCheckLogin
    @PostMapping("/chronic/doctor/auth/wechat/bind")
    public R<Boolean> bindWechat(@Validated @RequestBody ChDoctorWechatBindBo bo) {
        return R.ok(wechatBindService.bind(bo) != null);
    }

    /**
     * 解绑微信
     */
    @Operation(summary = "解绑微信")
    @SaCheckLogin
    @DeleteMapping("/chronic/doctor/auth/wechat/{id}")
    public R<Boolean> unbindWechat(@Parameter(description = "医生微信绑定ID") @PathVariable Long id) {
        return R.ok(wechatBindService.unbind(id));
    }

    /**
     * 查询当前用户绑定信息
     */
    @Operation(summary = "查询用户绑定信息")
    @SaCheckLogin
    @GetMapping("/chronic/doctor/auth/wechat/user/{userId}")
    public R<ChDoctorWechatBindVo> queryByUserId(@Parameter(description = "用户ID") @PathVariable Long userId) {
        return R.ok(wechatBindService.queryByUserId(userId));
    }
}
