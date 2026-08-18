package org.dromara.chronic.controller.openapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChDeviceBindBo;
import org.dromara.chronic.domain.dto.DeviceDataUploadDto;
import org.dromara.chronic.domain.vo.ChDeviceBindVo;
import org.dromara.chronic.manager.HealthMetricManager;
import org.dromara.chronic.service.IChDeviceBindService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设备接入开放接口
 *
 * @author unimed
 */
@Tag(name = "慢病管理-开放接口-设备接入")
@Validated
@RestController
@RequiredArgsConstructor
public class OpenapiDeviceController {

    private final HealthMetricManager healthMetricManager;
    private final IChDeviceBindService deviceBindService;

    @Operation(summary = "绑定设备")
    @RepeatSubmit
    @PostMapping("/chronic/openapi/device/bind")
    public R<Long> bind(@Validated @RequestBody ChDeviceBindBo bo) {
        return R.ok(healthMetricManager.bindDevice(bo));
    }

    @Operation(summary = "解绑设备")
    @DeleteMapping("/chronic/openapi/device/unbind/{bindId}")
    public R<Void> unbind(@Parameter(description = "绑定记录ID") @PathVariable Long bindId) {
        return R.ok(healthMetricManager.unbindDevice(bindId));
    }

    @Operation(summary = "设备心跳")
    @PostMapping("/chronic/openapi/device/heartbeat")
    public R<Void> heartbeat(@Parameter(description = "设备ID") @RequestParam String deviceId,
                             @Parameter(description = "电池电量") @RequestParam(required = false) Integer batteryLevel,
                             @Parameter(description = "在线状态") @RequestParam(required = false) String onlineStatus) {
        return R.ok(healthMetricManager.deviceHeartbeat(deviceId, batteryLevel, onlineStatus));
    }

    @Operation(summary = "上传设备数据")
    @RepeatSubmit
    @PostMapping("/chronic/openapi/device/data")
    public R<Long> uploadDeviceData(@Validated @RequestBody DeviceDataUploadDto dto) {
        // 既落库设备原始记录，又落库健康指标记录（语义与原实现一致）
        return R.ok(healthMetricManager.reportDeviceMetric(dto.getRawRecord(), dto.getMetricRecord()));
    }

    @Operation(summary = "查询患者绑定设备")
    @GetMapping("/chronic/openapi/device/patient/{patientId}")
    public R<List<ChDeviceBindVo>> queryByPatient(@Parameter(description = "患者ID") @PathVariable Long patientId) {
        return R.ok(deviceBindService.queryByPatientId(patientId));
    }
}
