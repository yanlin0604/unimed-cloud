package org.dromara.chronic.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.common.helper.OrgNameHelper;
import org.dromara.chronic.domain.bo.ChArchiveShareApplyBo;
import org.dromara.chronic.domain.bo.ChReferralRecordBo;
import org.dromara.chronic.domain.entity.ChArchiveShareApply;
import org.dromara.chronic.domain.entity.ChExternalSyncLog;
import org.dromara.chronic.domain.entity.ChReferralRecord;
import org.dromara.chronic.domain.vo.ChArchiveShareApplyVo;
import org.dromara.chronic.domain.vo.ChReferralRecordVo;
import org.dromara.chronic.mapper.ChArchiveShareApplyMapper;
import org.dromara.chronic.mapper.ChExternalSyncLogMapper;
import org.dromara.chronic.mapper.ChReferralRecordMapper;
import org.dromara.chronic.service.IChReferralService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 转诊服务实现
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChReferralServiceImpl implements IChReferralService {

    private static final Set<String> VALID_REFERRAL_STATUSES = Set.of("PENDING", "APPROVED", "ACCEPTED", "REJECTED", "COMPLETED");

    private final ChReferralRecordMapper referralMapper;
    private final ChArchiveShareApplyMapper archiveShareMapper;
    private final ChExternalSyncLogMapper syncLogMapper;
    private final OrgNameHelper orgNameHelper;

    @Override
    public Long createReferral(ChReferralRecordBo bo) {
        ChReferralRecord entity = MapstructUtils.convert(bo, ChReferralRecord.class);
        entity.setReferralStatus("PENDING");
        referralMapper.insert(entity);
        return entity.getReferralId();
    }

    @Override
    public ChReferralRecordVo queryById(Long referralId) {
        ChReferralRecordVo vo = referralMapper.selectVoById(referralId);
        if (vo != null) {
            fillReferralOrgNames(Collections.singletonList(vo));
        }
        return vo;
    }

    @Override
    public TableDataInfo<ChReferralRecordVo> queryPageList(ChReferralRecordBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChReferralRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getPatientId()), ChReferralRecord::getPatientId, bo.getPatientId());
        lqw.eq(StringUtils.isNotBlank(bo.getReferralType()), ChReferralRecord::getReferralType, bo.getReferralType());
        lqw.eq(StringUtils.isNotBlank(bo.getReferralStatus()), ChReferralRecord::getReferralStatus, bo.getReferralStatus());
        lqw.orderByDesc(ChReferralRecord::getCreateTime);
        Page<ChReferralRecordVo> page = referralMapper.selectVoPage(pageQuery.build(), lqw);
        fillReferralOrgNames(page.getRecords());
        return TableDataInfo.build(page);
    }

    @Override
    public List<ChReferralRecordVo> queryByPatientId(Long patientId) {
        List<ChReferralRecordVo> list = referralMapper.selectVoList(
            Wrappers.<ChReferralRecord>lambdaQuery()
                .eq(ChReferralRecord::getPatientId, patientId)
                .orderByDesc(ChReferralRecord::getCreateTime)
        );
        fillReferralOrgNames(list);
        return list;
    }

    @Override
    public Void updateStatus(Long referralId, String newStatus) {
        if (!VALID_REFERRAL_STATUSES.contains(newStatus)) {
            throw new ServiceException("无效的转诊状态: " + newStatus);
        }
        ChReferralRecord entity = referralMapper.selectById(referralId);
        if (ObjectUtil.isNull(entity)) {
            throw new ServiceException("转诊记录不存在");
        }
        entity.setReferralStatus(newStatus);
        referralMapper.updateById(entity);
        return null;
    }

    @Override
    public Long applyArchiveShare(ChArchiveShareApplyBo bo) {
        ChArchiveShareApply entity = MapstructUtils.convert(bo, ChArchiveShareApply.class);
        entity.setApprovalStatus("PENDING");
        archiveShareMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public ChArchiveShareApplyVo queryApplyById(Long id) {
        ChArchiveShareApplyVo vo = archiveShareMapper.selectVoById(id);
        if (vo != null) {
            fillArchiveShareOrgNames(Collections.singletonList(vo));
        }
        return vo;
    }

    @Override
    public TableDataInfo<ChArchiveShareApplyVo> queryApplyPageList(ChArchiveShareApplyBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChArchiveShareApply> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getPatientId()), ChArchiveShareApply::getPatientId, bo.getPatientId());
        lqw.eq(StringUtils.isNotBlank(bo.getApprovalStatus()), ChArchiveShareApply::getApprovalStatus, bo.getApprovalStatus());
        lqw.orderByDesc(ChArchiveShareApply::getCreateTime);
        Page<ChArchiveShareApplyVo> page = archiveShareMapper.selectVoPage(pageQuery.build(), lqw);
        fillArchiveShareOrgNames(page.getRecords());
        return TableDataInfo.build(page);
    }

    @Override
    public Void approveArchiveShare(Long id, String approvalStatus) {
        ChArchiveShareApply entity = archiveShareMapper.selectById(id);
        if (ObjectUtil.isNull(entity)) {
            throw new ServiceException("调档申请不存在");
        }
        entity.setApprovalStatus(approvalStatus);
        archiveShareMapper.updateById(entity);
        return null;
    }

    @Override
    public void logSync(String syncType, String syncDirection, String externalSystem, String syncStatus, String syncDetail) {
        ChExternalSyncLog logEntity = new ChExternalSyncLog();
        logEntity.setSyncType(syncType);
        logEntity.setSyncDirection(syncDirection);
        logEntity.setExternalSystem(externalSystem);
        logEntity.setSyncStatus(syncStatus);
        logEntity.setSyncDetail(syncDetail);
        logEntity.setSyncTime(new Date());
        syncLogMapper.insert(logEntity);
    }

    private void fillReferralOrgNames(List<ChReferralRecordVo> list) {
        if (CollUtil.isEmpty(list)) return;
        List<Long> orgIds = list.stream()
            .flatMap(v -> java.util.stream.Stream.of(v.getFromOrgId(), v.getToOrgId()))
            .filter(ObjectUtil::isNotNull).distinct().collect(Collectors.toList());
        if (!orgIds.isEmpty()) {
            try {
                Map<Long, String> orgNameMap = orgNameHelper.batchGetOrgName(orgIds);
                list.forEach(v -> {
                    v.setFromOrgName(orgNameMap.get(v.getFromOrgId()));
                    v.setToOrgName(orgNameMap.get(v.getToOrgId()));
                });
            } catch (Exception e) { /* ignore */ }
        }
    }

    private void fillArchiveShareOrgNames(List<ChArchiveShareApplyVo> list) {
        if (CollUtil.isEmpty(list)) return;
        List<Long> orgIds = list.stream()
            .flatMap(v -> java.util.stream.Stream.of(v.getApplyOrgId(), v.getTargetOrgId()))
            .filter(ObjectUtil::isNotNull).distinct().collect(Collectors.toList());
        if (!orgIds.isEmpty()) {
            try {
                Map<Long, String> orgNameMap = orgNameHelper.batchGetOrgName(orgIds);
                list.forEach(v -> {
                    v.setApplyOrgName(orgNameMap.get(v.getApplyOrgId()));
                    v.setTargetOrgName(orgNameMap.get(v.getTargetOrgId()));
                });
            } catch (Exception e) { /* ignore */ }
        }
    }
}
