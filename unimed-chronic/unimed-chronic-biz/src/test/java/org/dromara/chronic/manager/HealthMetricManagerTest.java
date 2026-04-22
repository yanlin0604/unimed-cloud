package org.dromara.chronic.manager;

import org.dromara.chronic.domain.bo.ChHealthMetricRecordBo;
import org.dromara.chronic.domain.entity.ChHealthMetricRecord;
import org.dromara.chronic.mapper.ChAuditLogMapper;
import org.dromara.chronic.service.IChDeviceBindService;
import org.dromara.chronic.service.IChHealthMetricRecordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealthMetricManagerTest {

    @Mock
    private IChHealthMetricRecordService metricRecordService;

    @Mock
    private IChDeviceBindService deviceBindService;

    @Mock
    private WarningManager warningManager;

    @Mock
    private ChAuditLogMapper auditLogMapper;

    @InjectMocks
    private HealthMetricManager healthMetricManager;

    @Test
    void shouldTriggerWarningCheckAfterMetricReported() {
        ChHealthMetricRecordBo bo = buildMetricBo();
        ChHealthMetricRecord record = buildRecord();
        when(metricRecordService.reportMetric(bo)).thenReturn(1L);
        when(metricRecordService.getById(1L)).thenReturn(record);

        Long metricId = healthMetricManager.reportAndCheck(bo);

        assertEquals(1L, metricId);
        verify(warningManager).checkAndTrigger(record);
    }

    @Test
    void shouldNotRollbackWhenWarningCheckFails() {
        ChHealthMetricRecordBo bo = buildMetricBo();
        ChHealthMetricRecord record = buildRecord();
        when(metricRecordService.reportMetric(bo)).thenReturn(1L);
        when(metricRecordService.getById(1L)).thenReturn(record);
        doThrow(new RuntimeException("boom")).when(warningManager).checkAndTrigger(record);

        Long metricId = healthMetricManager.reportAndCheck(bo);

        assertEquals(1L, metricId);
        verify(warningManager).checkAndTrigger(record);
    }

    @Test
    void shouldSkipWarningCheckWhenMetricRecordMissing() {
        ChHealthMetricRecordBo bo = buildMetricBo();
        when(metricRecordService.reportMetric(any(ChHealthMetricRecordBo.class))).thenReturn(1L);
        when(metricRecordService.getById(1L)).thenReturn(null);

        Long metricId = healthMetricManager.reportAndCheck(bo);

        assertEquals(1L, metricId);
        verify(warningManager, never()).checkAndTrigger(any(ChHealthMetricRecord.class));
    }

    private ChHealthMetricRecordBo buildMetricBo() {
        ChHealthMetricRecordBo bo = new ChHealthMetricRecordBo();
        bo.setPatientId(1L);
        bo.setMetricType("BP_SYSTOLIC");
        bo.setMetricValue(new BigDecimal("160"));
        bo.setIsAbnormal(true);
        return bo;
    }

    private ChHealthMetricRecord buildRecord() {
        ChHealthMetricRecord record = new ChHealthMetricRecord();
        record.setMetricId(1L);
        record.setPatientId(1L);
        record.setMetricType("BP_SYSTOLIC");
        record.setMetricValue(new BigDecimal("160"));
        record.setIsAbnormal(true);
        return record;
    }

    @Test
    void shouldRejectUpdateOnDeviceMetric() {
        ChHealthMetricRecord deviceRecord = new ChHealthMetricRecord();
        deviceRecord.setMetricId(2L);
        deviceRecord.setPatientId(1L);
        deviceRecord.setMetricType("BP_SYSTOLIC");
        deviceRecord.setDataSource("DEVICE");

        when(metricRecordService.getById(2L)).thenReturn(deviceRecord);

        ChHealthMetricRecordBo bo = new ChHealthMetricRecordBo();
        bo.setMetricValue(new BigDecimal("140"));

        assertThrows(RuntimeException.class, () -> {
            healthMetricManager.updateManualMetric(2L, bo);
        });
    }

    @Test
    void shouldRejectDeleteOnDeviceMetric() {
        ChHealthMetricRecord deviceRecord = new ChHealthMetricRecord();
        deviceRecord.setMetricId(3L);
        deviceRecord.setPatientId(1L);
        deviceRecord.setDataSource("DEVICE");

        when(metricRecordService.getById(3L)).thenReturn(deviceRecord);

        assertThrows(RuntimeException.class, () -> {
            healthMetricManager.deleteManualMetric(3L);
        });
    }

    @Test
    void shouldWriteAuditLogOnManualMetricUpdate() {
        ChHealthMetricRecord manualRecord = new ChHealthMetricRecord();
        manualRecord.setMetricId(4L);
        manualRecord.setPatientId(1L);
        manualRecord.setMetricType("BP_SYSTOLIC");
        manualRecord.setDataSource("MANUAL");

        when(metricRecordService.getById(4L)).thenReturn(manualRecord);
        when(metricRecordService.updateMetric(any())).thenReturn(true);

        // 修改后重查返回更新后的记录
        ChHealthMetricRecord updatedRecord = new ChHealthMetricRecord();
        updatedRecord.setMetricId(4L);
        updatedRecord.setPatientId(1L);
        updatedRecord.setMetricType("BP_SYSTOLIC");
        updatedRecord.setDataSource("MANUAL");
        when(metricRecordService.getById(4L)).thenReturn(manualRecord, updatedRecord);

        ChHealthMetricRecordBo bo = new ChHealthMetricRecordBo();
        bo.setMetricValue(new BigDecimal("130"));

        healthMetricManager.updateManualMetric(4L, bo);

        // 验证审计日志被写入
        verify(auditLogMapper).insert(any());
        // 验证预警重检被触发
        verify(warningManager).checkAndTrigger(updatedRecord);
    }

    @Test
    void shouldWriteAuditLogOnManualMetricDelete() {
        ChHealthMetricRecord manualRecord = new ChHealthMetricRecord();
        manualRecord.setMetricId(5L);
        manualRecord.setPatientId(1L);
        manualRecord.setDataSource("MANUAL");

        when(metricRecordService.getById(5L)).thenReturn(manualRecord);
        doNothing().when(metricRecordService).deleteMetric(5L);

        healthMetricManager.deleteManualMetric(5L);

        // 验证审计日志被写入
        verify(auditLogMapper).insert(any());
    }
}
