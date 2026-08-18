package org.dromara.chronic.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.dromara.chronic.domain.bo.ChDeviceRawRecordBo;
import org.dromara.chronic.domain.bo.ChHealthMetricRecordBo;

import java.io.Serial;
import java.io.Serializable;

/**
 * 设备数据上传包装对象。
 * <p>
 * 一次上传同时落库「设备原始记录」和「健康指标记录」，
 * 替代原先在同一方法上声明两个 {@code @RequestBody} 的非法写法。
 *
 * @author unimed
 */
@Data
@Schema(description = "设备数据上传对象")
public class DeviceDataUploadDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "设备原始记录", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "设备原始记录不能为空")
    @Valid
    private ChDeviceRawRecordBo rawRecord;

    @Schema(description = "健康指标记录", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "健康指标记录不能为空")
    @Valid
    private ChHealthMetricRecordBo metricRecord;
}
