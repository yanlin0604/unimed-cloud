package org.dromara.chronic.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChDeviceBindBo;
import org.dromara.chronic.domain.bo.ChDeviceRawRecordBo;
import org.dromara.chronic.domain.entity.ChDeviceBind;
import org.dromara.chronic.domain.entity.ChDeviceRawRecord;
import org.dromara.chronic.domain.vo.ChDeviceBindVo;
import org.dromara.chronic.domain.vo.ChDeviceRawRecordVo;
import org.dromara.chronic.mapper.ChDeviceBindMapper;
import org.dromara.chronic.mapper.ChDeviceRawRecordMapper;
import org.dromara.chronic.service.IChDeviceBindService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 设备绑定服务实现
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChDeviceBindServiceImpl implements IChDeviceBindService {

    private final ChDeviceBindMapper deviceBindMapper;
    private final ChDeviceRawRecordMapper rawRecordMapper;

    @Override
    public Long bindDevice(ChDeviceBindBo bo) {
        ChDeviceBind existed = deviceBindMapper.selectOne(
            Wrappers.<ChDeviceBind>lambdaQuery()
                .eq(ChDeviceBind::getPatientId, bo.getPatientId())
                .eq(ChDeviceBind::getDeviceId, bo.getDeviceId())
        );
        if (ObjectUtil.isNotNull(existed)) {
            throw new ServiceException("设备已绑定该患者");
        }
        ChDeviceBind entity = MapstructUtils.convert(bo, ChDeviceBind.class);
        entity.setOnlineStatus("ONLINE");
        entity.setLastCommTime(new Date());
        deviceBindMapper.insert(entity);
        return entity.getBindId();
    }

    @Override
    public Void unbindDevice(Long bindId) {
        ChDeviceBind entity = deviceBindMapper.selectById(bindId);
        if (ObjectUtil.isNull(entity)) {
            throw new ServiceException("设备绑定记录不存在");
        }
        deviceBindMapper.deleteById(bindId);
        return null;
    }

    @Override
    public List<ChDeviceBindVo> queryByPatientId(Long patientId) {
        return deviceBindMapper.selectVoList(
            Wrappers.<ChDeviceBind>lambdaQuery()
                .eq(ChDeviceBind::getPatientId, patientId)
                .orderByDesc(ChDeviceBind::getLastCommTime)
        );
    }

    @Override
    public Void updateHeartbeat(String deviceId, Integer batteryLevel, String onlineStatus) {
        ChDeviceBind bind = deviceBindMapper.selectOne(
            Wrappers.<ChDeviceBind>lambdaQuery().eq(ChDeviceBind::getDeviceId, deviceId)
        );
        if (ObjectUtil.isNull(bind)) {
            log.warn("心跳更新未找到设备: {}", deviceId);
            return null;
        }
        if (batteryLevel != null) {
            bind.setBatteryLevel(batteryLevel);
        }
        if (onlineStatus != null) {
            bind.setOnlineStatus(onlineStatus);
        }
        bind.setLastCommTime(new Date());
        deviceBindMapper.updateById(bind);
        return null;
    }

    @Override
    public ChDeviceRawRecordVo saveRawRecord(ChDeviceRawRecordBo bo) {
        ChDeviceRawRecord entity = MapstructUtils.convert(bo, ChDeviceRawRecord.class);
        entity.setParsedAt(new Date());
        rawRecordMapper.insert(entity);
        return MapstructUtils.convert(entity, ChDeviceRawRecordVo.class);
    }
}
