package org.dromara.dhcore.domain.bo.portal;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * C端个人资料更新业务对象
 *
 * @author AI
 */
@Data
public class PortalProfileUpdateBo {

    /**
     * 昵称
     */
    @Size(max = 30, message = "昵称不能超过30个字符")
    private String nickName;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 联系方式
     */
    @Size(max = 20, message = "联系方式不能超过20个字符")
    private String phone;
}
