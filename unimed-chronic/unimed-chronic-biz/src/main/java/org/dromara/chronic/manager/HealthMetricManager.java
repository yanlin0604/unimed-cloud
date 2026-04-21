package org.dromara.chronic.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChDeviceBindBo;
import org.dromara.chronic.domain.bo.ChDeviceRawRecordBo;
import org.dromara.chronic.domain.bo.ChHealthMetricRecordBo;
import org.dromara.chronic.domain.vo.ChDeviceRawRecordVo;
import org.dromara.chronic.service.IChDeviceBindService;
import org.dromara.chronic.service.IChHealthMetricRecordService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(rollbackFor = Exception.class)
    public Long reportAndCheck(ChHealthMetricRecordBo bo) {
        Long metricId = metricRecordService.reportMetric(bo);
        if (Boolean.TRUE.equals(bo.getIsAbnormal())) {
            triggerWarning(bo);
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

    /**
     * 触发预警事件（后续对接 WarningRuleEngine）
     */
    private void triggerWarning(ChHealthMetricRecordBo bo) {
        log.info("患者 {} 指标 {} 异常，值={}，触发预警检查", bo.getPatientId(), bo.getMetricType(), bo.getMetricValue());
        // TODO: 对接 WarningRuleEngine，执行连续N次超标窗口判定
    }
}
