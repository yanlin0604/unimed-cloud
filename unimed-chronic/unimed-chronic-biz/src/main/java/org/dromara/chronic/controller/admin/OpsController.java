package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.chronic.domain.bo.ChWarningEventBo;
import org.dromara.chronic.domain.vo.ChWarningEventVo;
import org.dromara.chronic.service.IChWarningEventService;
import org.dromara.chronic.support.helper.HealthCheckHelper;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.workflow.api.RemoteWorkflowService;
import org.dromara.workflow.api.domain.RemoteStartProcess;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 运维管理接口
 * <p>
 * R10: 健康巡检（5项）、任务重跑(带审批流)、SLA报表
 *
 * @author unimed
 */
@Tag(name = "慢病管理-运维管理")
@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
public class OpsController {

    private final HealthCheckHelper healthCheckHelper;
    private final IChWarningEventService warningEventService;
    @DubboReference(mock = "true")
    private RemoteWorkflowService workflowService;

    /**
     * R10: 健康巡检 —— 检测 DB/Redis/Nacos/RocketMQ/HIS 连通性
     */
    @SaCheckPermission("chronic:ops:health")
    @Operation(summary = "健康巡检")
    @GetMapping("/chronic/admin/ops/health-check")
    public R<Map<String, HealthCheckHelper.ComponentStatus>> healthCheck() {
        return R.ok(healthCheckHelper.checkAll());
    }

    /**
     * R10: 任务重跑申请（需审批流）
     */
    @SaCheckPermission("chronic:ops:rerun")
    @Operation(summary = "任务重跑申请")
    @PostMapping("/chronic/admin/ops/task-rerun")
    public R<Void> rerunApply(
        @Parameter(description = "任务类型") @RequestParam String jobType,
        @Parameter(description = "任务参数") @RequestParam String jobParam,
        @Parameter(description = "重跑原因") @RequestParam String reason) {
        // R10: 对接 RemoteWorkflowService 审批流
        RemoteStartProcess startProcess = new RemoteStartProcess();
        startProcess.setFlowCode("chronic_task_rerun");
        startProcess.setBusinessId("RERUN_" + jobType + "_" + System.currentTimeMillis());
        startProcess.setVariables(Map.of(
            "jobType", jobType,
            "jobParam", jobParam,
            "reason", reason
        ));
        try {
            workflowService.startWorkFlow(startProcess);
        } catch (Exception e) {
            log.warn("任务重跑审批流启动失败: {}", e.getMessage());
            throw new ServiceException("审批流启动失败: " + e.getMessage());
        }
        return R.ok();
    }

    /**
     * R10: SLA报表 —— 预警响应/解决时效统计
     */
    @SaCheckPermission("chronic:ops:sla")
    @Operation(summary = "SLA报表")
    @GetMapping("/chronic/admin/ops/sla-report")
    public R<TableDataInfo<ChWarningEventVo>> slaReport(
        @Parameter(description = "预警级别") @RequestParam(required = false) String warningLevel,
        PageQuery pageQuery) {
        ChWarningEventBo bo = new ChWarningEventBo();
        bo.setWarningLevel(warningLevel);
        return R.ok(warningEventService.queryPageList(bo, pageQuery));
    }
}
