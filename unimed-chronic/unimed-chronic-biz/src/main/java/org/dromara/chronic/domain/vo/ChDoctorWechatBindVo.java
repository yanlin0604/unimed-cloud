package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChDoctorWechatBind;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 医生微信绑定视图对象
 *
 * @author unimed
 */
@Schema(description = "医生微信绑定视图对象")
@Data
@AutoMapper(target = ChDoctorWechatBind.class)
public class ChDoctorWechatBindVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "微信openid")
    private String openid;

    @Schema(description = "微信unionid")
    private String unionid;

    @Schema(description = "绑定时间")
    private Date bindTime;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "创建时间")
    private Date createTime;
}
