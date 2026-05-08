package org.dromara.chronic.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信登录请求体
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@Schema(description = "微信登录请求体")
public class WxLoginCodeBo {

    @Schema(description = "微信小程序 wx.login 获取的 code", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "code不能为空")
    private String code;

    @Schema(description = "手机号（未绑定时二次提交携带）")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "短信验证码")
    private String smsCode;

    @Schema(description = "微信 getPhoneNumber 回调的 code（授权手机号）")
    private String phoneCode;
}
