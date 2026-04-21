package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChDeviceBindBo;
import org.dromara.chronic.domain.bo.ChDeviceRawRecordBo;
import org.dromara.chronic.domain.vo.ChDeviceBindVo;
import org.dromara.chronic.domain.vo.ChDeviceRawRecordVo;

import java.util.List;

/**
 * 设备绑定服务
 *
 * @author unimed
 */
public interface IChDeviceBindService {

    Long bindDevice(ChDeviceBindBo bo);

    Void unbindDevice(Long bindId);

    List<ChDeviceBindVo> queryByPatientId(Long patientId);

    Void updateHeartbeat(String deviceId, Integer batteryLevel, String onlineStatus);

    ChDeviceRawRecordVo saveRawRecord(ChDeviceRawRecordBo bo);
}
