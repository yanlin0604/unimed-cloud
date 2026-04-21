package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChPatientAccount;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

/**
 * 患者账号业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "患者账号业务对象")
@AutoMapper(target = ChPatientAccount.class, reverseConvertGenerate = false)
public class ChPatientAccountBo extends BaseEntity {

    @Schema(description = "账号ID")
    private Long accountId;

    @Schema(description = "患者ID")
    private Long patientId;

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @Schema(description = "openid")
    private String openid;

    @Schema(description = "unionid")
    private String unionid;

    @Schema(description = "是否家属代理")
    private Boolean isFamilyProxy;

    @Schema(description = "主账号ID")
    private Long masterAccountId;

    @Schema(description = "授权范围")
    private String authScope;

    @Schema(description = "授权过期时间")
    private Date authExpireTime;
}
