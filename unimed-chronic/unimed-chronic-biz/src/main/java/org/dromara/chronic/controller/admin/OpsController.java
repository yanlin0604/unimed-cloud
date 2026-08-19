package org.dromara.chronic.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.chronic.domain.bo.ChWarningEventBo;
import org.dromara.chronic.domain.entity.ChOpsHealthCheck;
import org.dromara.chronic.domain.entity.ChOpsRerunTicket;
import org.dromara.chronic.domain.vo.ChWarningEventVo;
import org.dromara.chronic.mapper.ChOpsHealthCheckMapper;
import org.dromara.chronic.mapper.ChOpsRerunTicketMapper;
import org.dromara.chronic.service.IChWarningEventService;
import org.dromara.chronic.support.helper.HealthCheckHelper;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.workflow.api.RemoteWorkflowService;
import org.dromara.workflow.api.domain.RemoteStartProcess;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

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
public class OpsController extends BaseController {

    private final HealthCheckHelper healthCheckHelper;
    private final IChWarningEventService warningEventService;
    private final ChOpsHealthCheckMapper healthCheckMapper;
    private final ChOpsRerunTicketMapper rerunTicketMapper;
    @DubboReference(mock = "true")
    private RemoteWorkflowService workflowService;

    /**
     * R10: 健康巡检 —— 检测 DB/Redis/Nacos/RocketMQ/HIS 连通性并持久化
     */
    @SaCheckPermission("chronic:ops:health")
    @Operation(summary = "健康巡检")
    @GetMapping("/chronic/admin/ops/health-check")
    public R<Map<String, HealthCheckHelper.ComponentStatus>> healthCheck() {
        Map<String, HealthCheckHelper.ComponentStatus> result = healthCheckHelper.checkAll();
        
        // 巡检结果落库 ch_ops_health_check
        String batchNo = UUID.randomUUID().toString().replace("-", "");
        Date now = new Date();
        for (Map.Entry<String, HealthCheckHelper.ComponentStatus> entry : result.entrySet()) {
            HealthCheckHelper.ComponentStatus status = entry.getValue();
            ChOpsHealthCheck entity = new ChOpsHealthCheck();
            entity.setCheckBatch(batchNo);
            entity.setTargetComponent(entry.getKey());
            entity.setCheckStatus(status.isHealthy() ? "SUCCESS" : "FAILED");
            entity.setResponseMs(0L);
            entity.setErrorMsg(status.getDetail());
            entity.setAlertTriggered(!status.isHealthy());
            entity.setCheckTime(now);
            try {
                healthCheckMapper.insert(entity);
            } catch (Exception e) {
                log.warn("巡检记录落库异常: component={}, err={}", entry.getKey(), e.getMessage());
            }
        }
        return R.ok(result);
    }

    /**
     * R10: 任务重跑申请（持久化工单并对接审批流）
     */
    @SaCheckPermission("chronic:ops:rerun")
    @Operation(summary = "任务重跑申请")
    @PostMapping("/chronic/admin/ops/task-rerun")
    public R<Long> rerunApply(
        @Parameter(description = "任务类型") @RequestParam String jobType,
        @Parameter(description = "任务参数") @RequestParam String jobParam,
        @Parameter(description = "重跑原因") @RequestParam String reason) {
        
        Long userId = LoginHelper.getUserId();
        String businessId = "RERUN_" + jobType + "_" + System.currentTimeMillis();

        // 1. 保存工单至 ch_ops_rerun_ticket
        ChOpsRerunTicket ticket = new ChOpsRerunTicket();
        ticket.setTaskCode(jobType);
        ticket.setApplyUserId(userId);
        ticket.setApplyReason(reason);
        ticket.setAuditStatus("PENDING");
        ticket.setExecStatus("NOT_STARTED");
        ticket.setAffectedRange(jobParam);
        rerunTicketMapper.insert(ticket);

        // 2. 触发审批流
        RemoteStartProcess startProcess = new RemoteStartProcess();
        startProcess.setFlowCode("chronic_task_rerun");
        startProcess.setBusinessId(businessId);
        startProcess.setVariables(Map.of(
            "ticketId", ticket.getTicketId(),
            "jobType", jobType,
            "jobParam", jobParam,
            "reason", reason
        ));
        try {
            workflowService.startWorkFlow(startProcess);
        } catch (Exception e) {
            log.warn("任务重跑审批流启动失败: ticketId={}, err={}", ticket.getTicketId(), e.getMessage());
        }
        return R.ok(ticket.getTicketId());
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
