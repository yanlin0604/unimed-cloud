package org.dromara.chronic.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信登录响应体
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@Schema(description = "微信登录响应体")
public class WxLoginVo {

    @Schema(description = "登录 token（仅登录成功时非空）")
    private String token;

    @Schema(description = "token 过期时间（秒）")
    private Long expireIn;

    @Schema(description = "是否需要绑定手机号（true 表示首次登录需补充手机号）")
    private Boolean needBind;

    @Schema(description = "患者账号信息（脱敏）")
    private ChPatientAccountVo account;
}
