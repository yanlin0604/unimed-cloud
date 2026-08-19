package org.dromara.chronic.controller.patient;

import org.dromara.common.web.core.BaseController;
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
import org.dromara.chronic.support.PatientContextHelper;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
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
public class PatientMetricController extends BaseController {

    private final HealthMetricManager healthMetricManager;
    private final IChHealthMetricRecordService metricRecordService;
    private final IChDeviceBindService deviceBindService;
    private final PatientContextHelper patientContextHelper;

    @Operation(summary = "上报健康指标")
    @RepeatSubmit
    @PostMapping("/chronic/patient/health-metrics")
    public R<Long> report(@Validated @RequestBody ChHealthMetricRecordBo bo) {
        bo.setDataSource("MANUAL");
        bo.setPatientId(patientContextHelper.getCurrentPatientId());
        return R.ok(healthMetricManager.reportAndCheck(bo));
    }

    @Operation(summary = "批量上报健康指标")
    @RepeatSubmit
    @PostMapping("/chronic/patient/health-metrics/batch")
    public R<List<Long>> reportBatch(@Validated @RequestBody List<ChHealthMetricRecordBo> boList) {
        Long patientId = patientContextHelper.getCurrentPatientId();
        for (ChHealthMetricRecordBo bo : boList) {
            bo.setDataSource("MANUAL");
            bo.setPatientId(patientId);
        }
        return R.ok(healthMetricManager.reportAndCheckBatch(boList));
    }

    @Operation(summary = "查询健康指标趋势")
    @GetMapping("/chronic/patient/health-metrics/trend")
    public R<List<ChHealthMetricRecordVo>> trend(@Parameter(description = "指标类型") @RequestParam String metricType,
                                                 @Parameter(description = "查询天数") @RequestParam(required = false, defaultValue = "30") Integer limit) {
        Long patientId = patientContextHelper.getCurrentPatientId();
        return R.ok(metricRecordService.queryTrend(patientId, metricType, limit));
    }

    @Operation(summary = "查询我的设备")
    @GetMapping("/chronic/patient/devices")
    public R<List<ChDeviceBindVo>> myDevices() {
        Long patientId = patientContextHelper.getCurrentPatientId();
        return R.ok(deviceBindService.queryByPatientId(patientId));
    }

    @Operation(summary = "历史指标分页查询")
    @GetMapping("/chronic/patient/metric/page")
    public TableDataInfo<ChHealthMetricRecordVo> page(ChHealthMetricRecordBo bo, PageQuery pageQuery) {
        bo.setPatientId(patientContextHelper.getCurrentPatientId());
        return metricRecordService.queryPageList(bo, pageQuery);
    }
}
