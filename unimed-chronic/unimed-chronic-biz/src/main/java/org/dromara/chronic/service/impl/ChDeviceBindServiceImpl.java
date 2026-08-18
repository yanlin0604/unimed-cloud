package org.dromara.chronic.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChDeviceBindBo;
import org.dromara.chronic.domain.bo.ChDeviceRawRecordBo;
import org.dromara.chronic.domain.entity.ChDeviceBind;
import org.dromara.chronic.domain.entity.ChDeviceRawRecord;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.domain.vo.ChDeviceBindVo;
import org.dromara.chronic.domain.vo.ChDeviceRawRecordVo;
import org.dromara.chronic.mapper.ChDeviceBindMapper;
import org.dromara.chronic.mapper.ChDeviceRawRecordMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.chronic.service.IChDeviceBindService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final ChPatientProfileMapper patientProfileMapper;

    /**
     * 批量回填患者姓名
     */
    private void fillPatientName(Collection<ChDeviceBindVo> vos) {
        if (vos == null || vos.isEmpty()) {
            return;
        }
        Set<Long> patientIds = vos.stream()
            .map(ChDeviceBindVo::getPatientId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        if (patientIds.isEmpty()) {
            return;
        }
        Map<Long, String> patientNames = patientProfileMapper.selectByIds(patientIds).stream()
            .collect(Collectors.toMap(ChPatientProfile::getPatientId, ChPatientProfile::getName, (a, b) -> a));
        vos.forEach(vo -> vo.setPatientName(patientNames.get(vo.getPatientId())));
    }

    @Override
    public Long bindDevice(ChDeviceBindBo bo) {
        if (bo.getPatientId() == null) {
            throw new ServiceException("患者ID不能为空");
        }
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
    public Void unbindByPatient(Long bindId, Long patientId) {
        if (bindId == null) {
            throw new ServiceException("绑定ID不能为空");
        }
        if (patientId == null) {
            throw new ServiceException("未获取当前患者身份");
        }
        ChDeviceBind entity = deviceBindMapper.selectById(bindId);
        if (ObjectUtil.isNull(entity)) {
            throw new ServiceException("设备绑定记录不存在");
        }
        if (!patientId.equals(entity.getPatientId())) {
            throw new ServiceException("无权操作该设备");
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
    public TableDataInfo<ChDeviceBindVo> queryPageList(ChDeviceBindBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChDeviceBind> lqw = Wrappers.lambdaQuery();
        if (bo != null) {
            lqw.eq(ObjectUtil.isNotNull(bo.getPatientId()), ChDeviceBind::getPatientId, bo.getPatientId());
            lqw.like(StringUtils.isNotBlank(bo.getDeviceId()), ChDeviceBind::getDeviceId, bo.getDeviceId());
            lqw.eq(StringUtils.isNotBlank(bo.getDeviceType()), ChDeviceBind::getDeviceType, bo.getDeviceType());
            lqw.eq(StringUtils.isNotBlank(bo.getOnlineStatus()), ChDeviceBind::getOnlineStatus, bo.getOnlineStatus());
        }
        lqw.orderByDesc(ChDeviceBind::getLastCommTime).orderByDesc(ChDeviceBind::getBindId);
        Page<ChDeviceBindVo> page = deviceBindMapper.selectVoPage(pageQuery.build(), lqw);
        fillPatientName(page.getRecords());
        return TableDataInfo.build(page);
    }

    @Override
    public ChDeviceBindVo queryById(Long bindId) {
        if (bindId == null) {
            return null;
        }
        ChDeviceBindVo vo = deviceBindMapper.selectVoById(bindId);
        if (vo != null) {
            fillPatientName(List.of(vo));
        }
        return vo;
    }

    @Override
    public TableDataInfo<ChDeviceRawRecordVo> queryRawRecordPage(String deviceId, Long patientId, PageQuery pageQuery) {
        LambdaQueryWrapper<ChDeviceRawRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(deviceId), ChDeviceRawRecord::getDeviceId, deviceId);
        lqw.eq(ObjectUtil.isNotNull(patientId), ChDeviceRawRecord::getPatientId, patientId);
        lqw.orderByDesc(ChDeviceRawRecord::getId);
        Page<ChDeviceRawRecordVo> page = rawRecordMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
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
