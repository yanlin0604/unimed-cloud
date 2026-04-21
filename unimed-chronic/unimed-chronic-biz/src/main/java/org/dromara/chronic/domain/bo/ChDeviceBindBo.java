package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChDeviceBind;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 设备绑定业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "设备绑定业务对象")
@AutoMapper(target = ChDeviceBind.class, reverseConvertGenerate = false)
public class ChDeviceBindBo extends BaseEntity {

    @Schema(description = "绑定ID")
    private Long bindId;

    @Schema(description = "患者ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    @Schema(description = "设备ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;

    @Schema(description = "设备类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "设备类型不能为空")
    private String deviceType;
}
