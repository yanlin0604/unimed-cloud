package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.vo.ChWarningEventVo;
import org.dromara.chronic.service.IChWarningEventService;
import org.dromara.chronic.support.helper.HealthCheckHelper;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 运维管理接口
 * <p>
 * 健康巡检、任务重跑(带审批流)、SLA报表
 *
 * @author unimed
 */
@Tag(name = "慢病管理-运维管理")
@Validated
@RestController
@RequiredArgsConstructor
public class OpsController {

    private final HealthCheckHelper healthCheckHelper;
    private final IChWarningEventService warningEventService;

    /**
     * 健康巡检：检测DB/Redis连通性
     */
    @SaCheckPermission("chronic:ops:health")
    @Operation(summary = "健康巡检")
    @GetMapping("/chronic/admin/ops/health")
    public R<Map<String, HealthCheckHelper.ComponentStatus>> healthCheck() {
        return R.ok(healthCheckHelper.checkAll());
    }

    /**
     * 任务重跑申请（需审批）
     */
    @SaCheckPermission("chronic:ops:rerun")
    @Operation(summary = "任务重跑申请")
    @PostMapping("/chronic/admin/ops/rerun/apply")
    public R<Void> rerunApply(
        @Parameter(description = "任务类型") @RequestParam String jobType,
        @Parameter(description = "任务参数") @RequestParam String jobParam,
        @Parameter(description = "重跑原因") @RequestParam String reason) {
        // TODO: 接入审批流(WorkflowService)，审批通过后触发任务重跑
        return R.ok();
    }

    /**
     * SLA报表：预警响应/解决时效统计
     */
    @SaCheckPermission("chronic:ops:sla")
    @Operation(summary = "SLA报表")
    @GetMapping("/chronic/admin/ops/sla/report")
    public R<TableDataInfo<ChWarningEventVo>> slaReport(
        @Parameter(description = "预警级别") @RequestParam(required = false) String warningLevel,
        PageQuery pageQuery) {
        org.dromara.chronic.domain.bo.ChWarningEventBo bo = new org.dromara.chronic.domain.bo.ChWarningEventBo();
        bo.setWarningLevel(warningLevel);
        return R.ok(warningEventService.queryPageList(bo, pageQuery));
    }
}
