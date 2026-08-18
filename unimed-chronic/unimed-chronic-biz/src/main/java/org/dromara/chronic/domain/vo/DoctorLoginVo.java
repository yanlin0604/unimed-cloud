package org.dromara.chronic.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 医生端登录结果
 *
 * @author unimed
 */
@Data
@Schema(description = "医生端登录结果")
public class DoctorLoginVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 与 unimed-auth /auth/login 保持同名，便于前端统一处理
     */
    @Schema(description = "访问令牌")
    private String access_token;

    @Schema(description = "令牌有效期(秒)")
    private Long expire_in;

    @Schema(description = "是否需要先绑定微信")
    private Boolean needBind;

    @Schema(description = "系统用户ID")
    private Long userId;

    @Schema(description = "用户昵称")
    private String nickName;
}
