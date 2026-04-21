package org.dromara.chronic.controller.patient;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChHealthMetricRecordBo;
import org.dromara.chronic.domain.vo.ChDeviceBindVo;
import org.dromara.chronic.domain.vo.ChHealthMetricRecordVo;
import org.dromara.chronic.manager.HealthMetricManager;
import org.dromara.chronic.service.IChDeviceBindService;
import org.dromara.chronic.service.IChHealthMetricRecordService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 患者端健康指标
 *
 * @author unimed
 */
@Tag(name = "慢病管理-患者端健康指标")
@SaCheckLogin
@Validated
@RestController
@RequiredArgsConstructor
public class PatientMetricController {

    private final HealthMetricManager healthMetricManager;
    private final IChHealthMetricRecordService metricRecordService;
    private final IChDeviceBindService deviceBindService;

    @Operation(summary = "上报健康指标")
    @RepeatSubmit
    @PostMapping("/chronic/patient/health-metrics")
    public R<Long> report(@Validated @RequestBody ChHealthMetricRecordBo bo) {
        bo.setDataSource("MANUAL");
        return R.ok(healthMetricManager.reportAndCheck(bo));
    }

    @Operation(summary = "查询健康指标趋势")
    @GetMapping("/chronic/patient/health-metrics/trend")
    public R<List<ChHealthMetricRecordVo>> trend(@Parameter(description = "患者ID") @RequestParam Long patientId,
                                                 @Parameter(description = "指标类型") @RequestParam String metricType,
                                                 @Parameter(description = "查询天数") @RequestParam(required = false, defaultValue = "30") Integer limit) {
        return R.ok(metricRecordService.queryTrend(patientId, metricType, limit));
    }

    @Operation(summary = "查询我的设备")
    @GetMapping("/chronic/patient/devices")
    public R<List<ChDeviceBindVo>> myDevices(@Parameter(description = "患者ID") @RequestParam Long patientId) {
        return R.ok(deviceBindService.queryByPatientId(patientId));
    }
}
