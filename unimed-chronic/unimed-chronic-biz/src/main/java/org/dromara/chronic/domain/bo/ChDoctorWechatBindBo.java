package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChDoctorWechatBind;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 医生微信绑定业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "医生微信绑定业务对象")
@AutoMapper(target = ChDoctorWechatBind.class, reverseConvertGenerate = false)
public class ChDoctorWechatBindBo extends BaseEntity {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "openid", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "openid不能为空")
    private String openid;

    @Schema(description = "unionid")
    private String unionid;
}
