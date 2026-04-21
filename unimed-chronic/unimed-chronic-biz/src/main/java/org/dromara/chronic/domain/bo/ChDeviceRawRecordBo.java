package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChDeviceRawRecord;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 设备原始数据业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "设备原始记录业务对象")
@AutoMapper(target = ChDeviceRawRecord.class, reverseConvertGenerate = false)
public class ChDeviceRawRecordBo extends BaseEntity {

    @Schema(description = "设备ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;

    @Schema(description = "患者ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    @Schema(description = "原始数据", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "原始数据不能为空")
    private String rawData;
}
