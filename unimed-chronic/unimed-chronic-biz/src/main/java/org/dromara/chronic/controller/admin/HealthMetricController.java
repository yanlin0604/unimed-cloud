package org.dromara.chronic.controller.admin;

import org.dromara.common.web.core.BaseController;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 健康指标管理后台
 *
 * @author unimed
 */
@Tag(name = "慢病管理-健康指标")
@Validated
@RestController
@RequiredArgsConstructor
public class HealthMetricController extends BaseController {

    private final HealthMetricManager healthMetricManager;
    private final IChHealthMetricRecordService metricRecordService;

    @Operation(summary = "上报健康指标")
    @SaCheckPermission("chronic:metric:add")
    @Log(title = "健康指标上报", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/admin/patient/{patientId}/health-metrics")
    public R<Long> report(@PathVariable @Parameter(description = "患者ID") Long patientId, @Validated @RequestBody ChHealthMetricRecordBo bo) {
        bo.setPatientId(patientId);
        return R.ok(healthMetricManager.reportAndCheck(bo));
    }

    @Operation(summary = "批量上报健康指标")
    @SaCheckPermission("chronic:metric:add")
    @Log(title = "健康指标批量上报", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/chronic/admin/patient/{patientId}/health-metrics/batch")
    public R<List<Long>> reportBatch(@PathVariable @Parameter(description = "患者ID") Long patientId, @Validated @RequestBody List<ChHealthMetricRecordBo> boList) {
        for (ChHealthMetricRecordBo bo : boList) {
            bo.setPatientId(patientId);
        }
        return R.ok(healthMetricManager.reportAndCheckBatch(boList));
    }

    @Operation(summary = "分页查询健康指标")
    @SaCheckPermission("chronic:metric:list")
    @GetMapping("/chronic/admin/health-metric/page")
    public TableDataInfo<ChHealthMetricRecordVo> page(ChHealthMetricRecordBo bo, PageQuery pageQuery) {
        return metricRecordService.queryPageList(bo, pageQuery);
    }

    @Operation(summary = "健康指标详情")
    @SaCheckPermission("chronic:metric:query")
    @GetMapping("/chronic/admin/health-metric/{metricId}")
    public R<ChHealthMetricRecordVo> detail(@PathVariable @Parameter(description = "健康指标ID") Long metricId) {
        return R.ok(metricRecordService.queryById(metricId));
    }

    @Operation(summary = "查询健康指标趋势")
    @SaCheckPermission("chronic:metric:query")
    @GetMapping("/chronic/admin/patient/{patientId}/health-metrics/trend")
    public R<List<ChHealthMetricRecordVo>> trend(@PathVariable @Parameter(description = "患者ID") Long patientId,
                                                  @RequestParam @Parameter(description = "指标类型") String metricType,
                                                  @RequestParam(required = false, defaultValue = "30") @Parameter(description = "查询条数") Integer limit) {
        return R.ok(metricRecordService.queryTrend(patientId, metricType, limit));
    }

    @Operation(summary = "修改人工录入指标")
    @SaCheckPermission("chronic:metric:edit")
    @Log(title = "健康指标修改", businessType = BusinessType.UPDATE)
    @PutMapping("/chronic/admin/health-metric/{metricId}")
    public R<Void> edit(@PathVariable @Parameter(description = "健康指标ID") Long metricId,
                       @Validated @RequestBody ChHealthMetricRecordBo bo) {
        return R.ok(healthMetricManager.updateManualMetric(metricId, bo));
    }

    @Operation(summary = "删除人工录入指标")
    @SaCheckPermission("chronic:metric:remove")
    @Log(title = "健康指标删除", businessType = BusinessType.DELETE)
    @DeleteMapping("/chronic/admin/health-metric/{metricId}")
    public R<Void> remove(@PathVariable @Parameter(description = "健康指标ID") Long metricId) {
        return R.ok(healthMetricManager.deleteManualMetric(metricId));
    }
}
