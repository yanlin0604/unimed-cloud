package org.dromara.dhcore.domain.bo.portal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * C端修改密码请求参数
 *
 * @author unimed
 */
@Data
public class PasswordUpdateBo {

    /**
     * 旧密码
     */
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度需在6-20位之间")
    private String newPassword;
}
