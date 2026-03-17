package org.dromara.dhcore.domain.bo.portal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 短信验证码请求业务对�? *
 * @author unimed
 */
@Data
public class SmsCodeBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 手机�?     */
    @NotBlank(message = "手机号不能为�?)
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
}