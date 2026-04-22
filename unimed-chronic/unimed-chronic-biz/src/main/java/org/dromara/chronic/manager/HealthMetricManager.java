package org.dromara.chronic.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChDeviceBindBo;
import org.dromara.chronic.domain.bo.ChDeviceRawRecordBo;
import org.dromara.chronic.domain.bo.ChHealthMetricRecordBo;
import org.dromara.chronic.domain.entity.ChAuditLog;
import org.dromara.chronic.domain.entity.ChHealthMetricRecord;
import org.dromara.chronic.domain.vo.ChDeviceRawRecordVo;
import org.dromara.chronic.mapper.ChAuditLogMapper;
import org.dromara.chronic.service.IChDeviceBindService;
import org.dromara.chronic.service.IChHealthMetricRecordService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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
    private final ChAuditLogMapper auditLogMapper;

    @Transactional(rollbackFor = Exception.class)
    public Long reportAndCheck(ChHealthMetricRecordBo bo) {
        Long metricId = metricRecordService.reportMetric(bo);
        ChHealthMetricRecord record = metricRecordService.getById(metricId);
        if (record != null) {
            try {
                warningManager.checkAndTrigger(record);
            } catch (Exception e) {
                log.warn("健康指标上报后预警检查失败, metricId={}", metricId, e);
            }
        }
        return metricId;
    }

    public Long reportDeviceMetric(ChDeviceRawRecordBo rawBo, ChHealthMetricRecordBo metricBo) {
        ChDeviceRawRecordVo rawVo = deviceBindService.saveRawRecord(rawBo);
        metricBo.setDataSource("DEVICE");
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
}
