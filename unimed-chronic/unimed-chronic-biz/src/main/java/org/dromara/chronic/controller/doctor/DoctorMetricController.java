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

    @Operation(summary = "新增人工指标")
    @SaCheckPermission("chronic:doctor:metric:add")
    @Log(title = "健康指标", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/patient/{patientId}/metric")
    public R<Long> add(@PathVariable Long patientId, @Validated @RequestBody ChHealthMetricRecordBo bo) {
        bo.setPatientId(patientId);
        bo.setDataSource("MANUAL");
        return R.ok(healthMetricManager.reportAndCheck(bo));
    }

    @Operation(summary = "修改人工指标")
    @SaCheckPermission("chronic:doctor:metric:edit")
    @Log(title = "健康指标", businessType = BusinessType.UPDATE)
    @PutMapping("/metric/{metricId}")
    public R<Void> edit(@PathVariable Long metricId, @Validated @RequestBody ChHealthMetricRecordBo bo) {
        return R.ok(healthMetricManager.updateManualMetric(metricId, bo));
    }

    @Operation(summary = "删除人工指标")
    @SaCheckPermission("chronic:doctor:metric:remove")
    @Log(title = "健康指标", businessType = BusinessType.DELETE)
    @DeleteMapping("/metric/{metricId}")
    public R<Void> remove(@PathVariable Long metricId) {
        return R.ok(healthMetricManager.deleteManualMetric(metricId));
    }

    @Operation(summary = "指标列表")
    @SaCheckPermission("chronic:doctor:metric:list")
    @GetMapping("/patient/{patientId}/metric/page")
    public TableDataInfo<ChHealthMetricRecordVo> page(@PathVariable Long patientId, ChHealthMetricRecordBo bo, PageQuery pageQuery) {
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
        return R.ok(metricRecordService.queryTrend(patientId, metricType, limit));
    }

    @Operation(summary = "最新指标")
    @SaCheckPermission("chronic:doctor:metric:list")
    @GetMapping("/patient/{patientId}/metric/latest")
    public R<List<ChHealthMetricRecordVo>> latest(@PathVariable Long patientId) {
        return R.ok(metricRecordService.queryLatest(patientId));
    }
}