package org.dromara.chronic.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChSosRecordBo;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.domain.entity.ChSosRecord;
import org.dromara.chronic.domain.vo.ChSosRecordVo;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.chronic.mapper.ChSosRecordMapper;
import org.dromara.chronic.service.IChSosRecordService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 紧急求助记录服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChSosRecordServiceImpl implements IChSosRecordService {

    private final ChSosRecordMapper sosRecordMapper;
    private final ChPatientProfileMapper patientProfileMapper;

    @Override
    public ChSosRecordVo queryById(Long sosId) {
        ChSosRecordVo vo = sosRecordMapper.selectVoById(sosId);
        if (vo != null && vo.getPatientId() != null) {
            ChPatientProfile profile = patientProfileMapper.selectById(vo.getPatientId());
            if (profile != null) {
                vo.setPatientName(profile.getName());
                vo.setPatientPhone(profile.getPhone());
            }
        }
        return vo;
    }

    @Override
    public TableDataInfo<ChSosRecordVo> queryPageList(ChSosRecordBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChSosRecord> lqw = buildQueryWrapper(bo);
        lqw.orderByDesc(ChSosRecord::getCreateTime);
        Page<ChSosRecordVo> page = sosRecordMapper.selectVoPage(pageQuery.build(), lqw);
        enrichPatientInfo(page.getRecords());
        return TableDataInfo.build(page);
    }

    @Override
    public List<ChSosRecordVo> queryList(ChSosRecordBo bo) {
        LambdaQueryWrapper<ChSosRecord> lqw = buildQueryWrapper(bo);
        lqw.orderByDesc(ChSosRecord::getCreateTime);
        List<ChSosRecordVo> list = sosRecordMapper.selectVoList(lqw);
        enrichPatientInfo(list);
        return list;
    }

    private LambdaQueryWrapper<ChSosRecord> buildQueryWrapper(ChSosRecordBo bo) {
        LambdaQueryWrapper<ChSosRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getPatientId()), ChSosRecord::getPatientId, bo.getPatientId());
        lqw.eq(StringUtils.isNotBlank(bo.getEventStatus()), ChSosRecord::getEventStatus, bo.getEventStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getNotifyDoctorStatus()), ChSosRecord::getNotifyDoctorStatus, bo.getNotifyDoctorStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getNotifyEmergencyStatus()), ChSosRecord::getNotifyEmergencyStatus, bo.getNotifyEmergencyStatus());
        return lqw;
    }

    private void enrichPatientInfo(List<ChSosRecordVo> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        List<Long> patientIds = records.stream()
            .map(ChSosRecordVo::getPatientId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
        if (patientIds.isEmpty()) {
            return;
        }
        List<ChPatientProfile> profiles = patientProfileMapper.selectList(
            Wrappers.<ChPatientProfile>lambdaQuery().in(ChPatientProfile::getPatientId, patientIds)
        );
        Map<Long, ChPatientProfile> profileMap = profiles.stream()
            .collect(Collectors.toMap(ChPatientProfile::getPatientId, p -> p, (a, b) -> a));

        for (ChSosRecordVo vo : records) {
            if (vo.getPatientId() != null && profileMap.containsKey(vo.getPatientId())) {
                ChPatientProfile profile = profileMap.get(vo.getPatientId());
                vo.setPatientName(profile.getName());
                vo.setPatientPhone(profile.getPhone());
            }
        }
    }

    @Override
    public Long insertByBo(ChSosRecordBo bo) {
        ChSosRecord entity = MapstructUtils.convert(bo, ChSosRecord.class);
        if (StringUtils.isBlank(entity.getEventStatus())) {
            entity.setEventStatus("NEW");
        }
        sosRecordMapper.insert(entity);
        return entity.getSosId();
    }

    @Override
    public Boolean updateByBo(ChSosRecordBo bo) {
        ChSosRecord entity = sosRecordMapper.selectById(bo.getSosId());
        if (ObjectUtil.isNull(entity)) {
            throw new ServiceException("紧急求助记录不存在");
        }
        ChSosRecord update = MapstructUtils.convert(bo, ChSosRecord.class);
        sosRecordMapper.updateById(update);
        return true;
    }

    @Override
    public Boolean handleSos(Long sosId, Long handlerUserId, String eventStatus, String handleRemark) {
        ChSosRecord entity = sosRecordMapper.selectById(sosId);
        if (ObjectUtil.isNull(entity)) {
            throw new ServiceException("紧急求助记录不存在");
        }
        entity.setHandlerUserId(handlerUserId);
        entity.setHandleTime(new Date());
        entity.setEventStatus(StringUtils.isNotBlank(eventStatus) ? eventStatus : "RESOLVED");
        entity.setHandleRemark(handleRemark);
        sosRecordMapper.updateById(entity);
        return true;
    }

    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        return sosRecordMapper.deleteByIds(ids) > 0;
    }
}
