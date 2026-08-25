package org.dromara.chronic.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChDeviceBindBo;
import org.dromara.chronic.domain.bo.ChDeviceRawRecordBo;
import org.dromara.chronic.domain.bo.ChHealthMetricRecordBo;
import org.dromara.chronic.domain.bo.ChWarningEventBo;
import org.dromara.chronic.domain.entity.ChAuditLog;
import org.dromara.chronic.domain.entity.ChHealthMetricRecord;
import org.dromara.chronic.domain.entity.ChManagePlanItem;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.domain.entity.ChManagePlan;
import org.dromara.chronic.domain.vo.ChDeviceRawRecordVo;
import org.dromara.chronic.mapper.ChAuditLogMapper;
import org.dromara.chronic.mapper.ChManagePlanMapper;
import org.dromara.chronic.mapper.ChManagePlanItemMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.chronic.service.IChDeviceBindService;
import org.dromara.chronic.service.IChHealthMetricRecordService;
import org.dromara.chronic.service.IChWarningEventService;
import org.dromara.chronic.utils.MetricValueUtils;
import org.dromara.chronic.support.rule.WarningRuleEngine;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 健康指标管理器：上报→预警规则匹配→触发预警事件
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HealthMetricManager {

    private final IChHealthMetricRecordService metricRecordService;
    private final IChDeviceBindService deviceBindService;
    private final WarningManager warningManager;
    private final IChWarningEventService warningEventService;
    private final ChAuditLogMapper auditLogMapper;
    private final ChManagePlanMapper managePlanMapper;
    private final ChManagePlanItemMapper planItemMapper;
    private final ChPatientProfileMapper patientProfileMapper;

    @Transactional(rollbackFor = Exception.class)
    public Long reportAndCheck(ChHealthMetricRecordBo bo) {
        Long metricId = metricRecordService.reportMetric(bo);
        ChHealthMetricRecord record = metricRecordService.getById(metricId);
        if (record != null) {
            // 原有预警规则检查
            try {
                warningManager.checkAndTrigger(record);
            } catch (Exception e) {
                log.warn("健康指标上报后预警检查失败, metricId={}", metricId, e);
            }
            // 新增：方案量化达标判定
            try {
                checkPlanCompliance(record);
            } catch (Exception e) {
                log.warn("方案量化达标判定失败, metricId={}", metricId, e);
            }
        }
        return metricId;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Long> reportAndCheckBatch(List<ChHealthMetricRecordBo> boList) {
        List<Long> ids = new ArrayList<>(boList.size());
        for (ChHealthMetricRecordBo bo : boList) {
            ids.add(reportAndCheck(bo));
        }
        return ids;
    }

    public Long reportDeviceMetric(ChDeviceRawRecordBo rawBo, ChHealthMetricRecordBo metricBo) {
        ChDeviceRawRecordVo rawVo = deviceBindService.saveRawRecord(rawBo);
        metricBo.setDataSource("DEVICE");
        // 指标记录未显式携带患者时，沿用原始记录的患者（同一次上报必属同一患者）
        if (metricBo.getPatientId() == null && rawVo != null) {
            metricBo.setPatientId(rawVo.getPatientId());
        }
        return reportAndCheck(metricBo);
    }

    public Long bindDevice(ChDeviceBindBo bo) {
        return deviceBindService.bindDevice(bo);
    }

    public Void unbindDevice(Long bindId) {
        return deviceBindService.unbindDevice(bindId);
    }

    public Void deviceHeartbeat(String deviceId, Integer batteryLevel, String onlineStatus) {
        return deviceBindService.updateHeartbeat(deviceId, batteryLevel, onlineStatus);
    }

    @Transactional(rollbackFor = Exception.class)
    public Void updateManualMetric(Long metricId, ChHealthMetricRecordBo bo) {
        org.dromara.chronic.domain.entity.ChHealthMetricRecord record = metricRecordService.getById(metricId);
        if (record == null) {
            throw new RuntimeException("健康指标记录不存在");
        }
        if (!"MANUAL".equals(record.getDataSource())) {
            throw new RuntimeException("只有人工录入数据可修改");
        }
        org.dromara.chronic.domain.bo.ChHealthMetricRecordBo updateBo = new org.dromara.chronic.domain.bo.ChHealthMetricRecordBo();
        updateBo.setMetricId(metricId);
        updateBo.setMetricValue(bo.getMetricValue());
        updateBo.setUnit(bo.getUnit());
        updateBo.setMeasureScene(bo.getMeasureScene());
        updateBo.setMeasurePeriod(bo.getMeasurePeriod());
        updateBo.setMeasurePosture(bo.getMeasurePosture());
        metricRecordService.updateMetric(updateBo);
        logAudit("METRIC_UPDATE", "MANUAL", "修改人工指标: metricId=" + metricId + ", patientId=" + record.getPatientId());
        // 修改后触发预警重检
        try {
            ChHealthMetricRecord updated = metricRecordService.getById(metricId);
            if (updated != null) {
                warningManager.checkAndTrigger(updated);
                checkPlanCompliance(updated);
            }
        } catch (Exception e) {
            log.warn("人工指标修改后预警重检失败, metricId={}", metricId, e);
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public Void deleteManualMetric(Long metricId) {
        org.dromara.chronic.domain.entity.ChHealthMetricRecord record = metricRecordService.getById(metricId);
        if (record == null) {
            throw new RuntimeException("健康指标记录不存在");
        }
        if (!"MANUAL".equals(record.getDataSource())) {
            throw new RuntimeException("设备上报数据不可删除，请通过审计流程作废");
        }
        metricRecordService.deleteMetric(metricId);
        logAudit("METRIC_DELETE", "MANUAL", "删除人工指标: metricId=" + metricId + ", patientId=" + record.getPatientId());
        return null;
    }

    private void logAudit(String operationType, String operationTarget, String detail) {
        try {
            ChAuditLog auditLog = new ChAuditLog();
            auditLog.setOperationType(operationType);
            auditLog.setOperationTarget(operationTarget);
            auditLog.setOperationDetail(detail);
            auditLog.setOperationTime(new Date());
            auditLogMapper.insert(auditLog);
        } catch (Exception e) {
            log.warn("审计日志写入失败", e);
        }
    }

    /**
     * 方案量化达标判定：比对上报指标与该患者现行有效方案的目标区间
     */
    private void checkPlanCompliance(ChHealthMetricRecord record) {
        if (record.getPatientId() == null || record.getMetricType() == null || record.getMetricValue() == null) {
            return;
        }

        // 1. 查询该患者当前所有激活/进行中的管理方案
        List<ChManagePlan> activePlans = managePlanMapper.selectList(
            Wrappers.<ChManagePlan>lambdaQuery()
                .eq(ChManagePlan::getPatientId, record.getPatientId())
                .eq(ChManagePlan::getPlanStatus, "ACTIVE")
                .eq(ChManagePlan::getDelFlag, "0")
        );

        if (activePlans.isEmpty()) {
            return;
        }

        List<Long> activePlanIds = activePlans.stream().map(ChManagePlan::getPlanId).toList();

        // 2. 查询该患者现行方案中类型匹配的量化目标
        List<ChManagePlanItem> matchingItems = planItemMapper.selectList(
            new LambdaQueryWrapper<ChManagePlanItem>()
                .in(ChManagePlanItem::getPlanId, activePlanIds)
                .in(ChManagePlanItem::getTargetMetricType,
                    WarningRuleEngine.getMetricTypeAliases(record.getMetricType()))
                .eq(ChManagePlanItem::getItemType, "MONITOR")
                .and(wrapper -> wrapper.isNotNull(ChManagePlanItem::getTargetMinValue)
                    .or().isNotNull(ChManagePlanItem::getTargetMaxValue))
                .eq(ChManagePlanItem::getDelFlag, "0")
        );

        BigDecimal value = MetricValueUtils.extractPrimaryValue(record.getMetricValue(), record.getMetricType());
        if (value == null) {
            log.warn("方案量化达标判定跳过: 指标值解析失败, patientId={}, metricType={}, metricValue={}",
                record.getPatientId(), record.getMetricType(), record.getMetricValue());
            return;
        }

        for (ChManagePlanItem item : matchingItems) {
            BigDecimal min = item.getTargetMinValue();
            BigDecimal max = item.getTargetMaxValue();

            boolean isCompliant = (min == null || value.compareTo(min) >= 0)
                && (max == null || value.compareTo(max) <= 0);

            if (!isCompliant) {
                log.info("方案量化未达标: patientId={}, planId={}, metricType={}, value={}, target=[{}, {}], planItemId={}",
                    record.getPatientId(), item.getPlanId(), record.getMetricType(), value, min, max, item.getId());
                logAudit("PLAN_NON_COMPLIANT", "METRIC_CHECK",
                    String.format("方案目标偏离: patientId=%d, planId=%d, metricType=%s, 当前值=%s, 目标区间=[%s,%s]",
                        record.getPatientId(), item.getPlanId(), record.getMetricType(), value, min, max));
                // 方案未达标软提醒使用 PLAN 来源，ruleId=0 仅为历史兼容字段。
                try {
                    ChWarningEventBo eventBo = new ChWarningEventBo();
                    eventBo.setPatientId(record.getPatientId());
                    eventBo.setRuleId(0L);
                    eventBo.setEventSource("PLAN");
                    eventBo.setSourceId(item.getId());
                    eventBo.setPlanId(item.getPlanId());
                    eventBo.setMetricType(WarningRuleEngine.normalizeMetricType(record.getMetricType()));
                    ChManagePlan activePlan = activePlans.stream()
                        .filter(plan -> item.getPlanId().equals(plan.getPlanId()))
                        .findFirst().orElse(null);
                    if (activePlan != null) {
                        eventBo.setOrgId(activePlan.getOrgId());
                    }
                    eventBo.setWarningLevel("LOW");
                    eventBo.setWarningValue(String.format("方案目标偏离: metricType=%s, 当前值=%s, 目标区间=[%s,%s], planId=%d",
                        record.getMetricType(), value, min, max, item.getPlanId()));
                    eventBo.setEventStatus("NEW");
                    if (record.getPatientId() != null) {
                        ChPatientProfile profile = patientProfileMapper.selectById(record.getPatientId());
                        if (profile != null && profile.getDoctorUserId() != null) {
                            eventBo.setAssigneeUserId(profile.getDoctorUserId());
                        }
                    }
                    warningEventService.createEvent(eventBo);
                    log.info("方案未达标软提醒已创建: patientId={}, planId={}, metricType={}",
                        record.getPatientId(), item.getPlanId(), record.getMetricType());
                } catch (Exception e) {
                    log.warn("方案未达标软提醒创建失败", e);
                }
            } else {
                warningEventService.resolveActiveEvents(record.getPatientId(), "PLAN", item.getId(),
                    "指标已恢复到管理方案目标范围");
                log.debug("方案量化达标: patientId={}, planId={}, metricType={}, value={}",
                    record.getPatientId(), item.getPlanId(), record.getMetricType(), value);
            }
        }
    }
}
