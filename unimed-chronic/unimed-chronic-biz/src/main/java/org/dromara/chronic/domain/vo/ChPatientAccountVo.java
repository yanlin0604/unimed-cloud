package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChPatientAccount;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 患者账号视图对象
 *
 * @author unimed
 */
@Schema(description = "患者账号视图对象")
@Data
@AutoMapper(target = ChPatientAccount.class)
public class ChPatientAccountVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "账号ID")
    private Long accountId;

    @Schema(description = "患者ID")
    private Long patientId;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "微信openid")
    private String openid;

    @Schema(description = "微信unionid")
    private String unionid;

    @Schema(description = "是否家庭代理")
    private Boolean isFamilyProxy;

    @Schema(description = "主账号ID")
    private Long masterAccountId;

    @Schema(description = "授权范围")
    private String authScope;

    @Schema(description = "授权过期时间")
    private Date authExpireTime;

    @Schema(description = "微信昵称")
    private String nickname;

    @Schema(description = "头像OSS ID")
    private String avatarOssId;

    @Schema(description = "头像URL（由ossId解析）")
    @Translation(type = TransConstant.OSS_ID_TO_URL, mapper = "avatarOssId")
    private String avatarUrl;

    @Schema(description = "是否已绑定微信")
    private Boolean isBoundWechat;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "创建时间")
    private Date createTime;
}
