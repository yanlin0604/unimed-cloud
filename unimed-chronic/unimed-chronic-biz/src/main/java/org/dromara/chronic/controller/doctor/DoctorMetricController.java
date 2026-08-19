package org.dromara.chronic.controller.doctor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChHealthMetricRecordBo;
import org.dromara.chronic.domain.vo.ChHealthMetricRecordVo;
import org.dromara.chronic.manager.HealthMetricManager;
import org.dromara.chronic.service.IChHealthMetricRecordService;
import org.dromara.chronic.support.DoctorScopeGuard;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 医生端健康指标维护
 *
 * @author unimed
 */
@Tag(name = "慢病管理-医生端健康指标")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/chronic/doctor")
public class DoctorMetricController extends BaseController {

    private final HealthMetricManager healthMetricManager;
    private final IChHealthMetricRecordService metricRecordService;
    private final DoctorScopeGuard doctorScopeGuard;

    /**
     * 解析指标记录归属患者并校验
     * <p>
     * edit/remove 的路径参数是 metricId 而非 patientId，原实现只校验
     * dataSource=='MANUAL'（那是「是否人工录入」的业务约束，不是鉴权），
     * 因此可枚举 metricId 改删他人患者指标，且改动会触发 warningManager 预警链路。
     */
    private void assertMetricOwned(Long metricId) {
        ChHealthMetricRecordVo record = metricRecordService.queryById(metricId);
        doctorScopeGuard.assertRecordOwned(record == null ? null : record.getPatientId());
    }

    @Operation(summary = "新增人工指标")
    @SaCheckPermission("chronic:doctor:metric:add")
    @Log(title = "健康指标", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/patient/{patientId}/metric")
    public R<Long> add(@PathVariable Long patientId, @Validated @RequestBody ChHealthMetricRecordBo bo) {
        doctorScopeGuard.assertPatientOwned(patientId);
        bo.setPatientId(patientId);
        bo.setDataSource("MANUAL");
        return R.ok(healthMetricManager.reportAndCheck(bo));
    }

    @Operation(summary = "批量新增人工指标")
    @SaCheckPermission("chronic:doctor:metric:add")
    @Log(title = "健康指标批量上报", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/patient/{patientId}/metric/batch")
    public R<List<Long>> addBatch(@PathVariable Long patientId, @Validated @RequestBody List<ChHealthMetricRecordBo> boList) {
        doctorScopeGuard.assertPatientOwned(patientId);
        for (ChHealthMetricRecordBo bo : boList) {
            bo.setPatientId(patientId);
            bo.setDataSource("MANUAL");
        }
        return R.ok(healthMetricManager.reportAndCheckBatch(boList));
    }

    @Operation(summary = "修改人工指标")
    @SaCheckPermission("chronic:doctor:metric:edit")
    @Log(title = "健康指标", businessType = BusinessType.UPDATE)
    @PutMapping("/metric/{metricId}")
    public R<Void> edit(@PathVariable Long metricId, @Validated @RequestBody ChHealthMetricRecordBo bo) {
        assertMetricOwned(metricId);
        return R.ok(healthMetricManager.updateManualMetric(metricId, bo));
    }

    @Operation(summary = "删除人工指标")
    @SaCheckPermission("chronic:doctor:metric:remove")
    @Log(title = "健康指标", businessType = BusinessType.DELETE)
    @DeleteMapping("/metric/{metricId}")
    public R<Void> remove(@PathVariable Long metricId) {
        assertMetricOwned(metricId);
        return R.ok(healthMetricManager.deleteManualMetric(metricId));
    }

    @Operation(summary = "指标列表")
    @SaCheckPermission("chronic:doctor:metric:list")
    @GetMapping("/patient/{patientId}/metric/page")
    public TableDataInfo<ChHealthMetricRecordVo> page(@PathVariable Long patientId, ChHealthMetricRecordBo bo, PageQuery pageQuery) {
        doctorScopeGuard.assertPatientOwned(patientId);
        bo.setPatientId(patientId);
        return metricRecordService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "指标趋势")
    @SaCheckPermission("chronic:doctor:metric:list")
    @GetMapping("/patient/{patientId}/metric/trend")
    public R<List<ChHealthMetricRecordVo>> trend(
            @PathVariable Long patientId,
            @RequestParam String metricType,
            @RequestParam(required = false, defaultValue = "30") Integer limit) {
        doctorScopeGuard.assertPatientOwned(patientId);
        return R.ok(metricRecordService.queryTrend(patientId, metricType, limit));
    }

    @Operation(summary = "最新指标")
    @SaCheckPermission("chronic:doctor:metric:list")
    @GetMapping("/patient/{patientId}/metric/latest")
    public R<List<ChHealthMetricRecordVo>> latest(@PathVariable Long patientId) {
        doctorScopeGuard.assertPatientOwned(patientId);
        return R.ok(metricRecordService.queryLatest(patientId));
    }
}