package org.dromara.chronic.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.Date;
import java.util.List;
import java.util.Set;

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

    @Override
    public Long createReferral(ChReferralRecordBo bo) {
        ChReferralRecord entity = MapstructUtils.convert(bo, ChReferralRecord.class);
        entity.setReferralStatus("PENDING");
        referralMapper.insert(entity);
        return entity.getReferralId();
    }

    @Override
    public ChReferralRecordVo queryById(Long referralId) {
        return referralMapper.selectVoById(referralId);
    }

    @Override
    public TableDataInfo<ChReferralRecordVo> queryPageList(ChReferralRecordBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChReferralRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getPatientId()), ChReferralRecord::getPatientId, bo.getPatientId());
        lqw.eq(StringUtils.isNotBlank(bo.getReferralType()), ChReferralRecord::getReferralType, bo.getReferralType());
        lqw.eq(StringUtils.isNotBlank(bo.getReferralStatus()), ChReferralRecord::getReferralStatus, bo.getReferralStatus());
        lqw.orderByDesc(ChReferralRecord::getCreateTime);
        Page<ChReferralRecordVo> page = referralMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public List<ChReferralRecordVo> queryByPatientId(Long patientId) {
        return referralMapper.selectVoList(
            Wrappers.<ChReferralRecord>lambdaQuery()
                .eq(ChReferralRecord::getPatientId, patientId)
                .orderByDesc(ChReferralRecord::getCreateTime)
        );
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
        return archiveShareMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<ChArchiveShareApplyVo> queryApplyPageList(ChArchiveShareApplyBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChArchiveShareApply> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getPatientId()), ChArchiveShareApply::getPatientId, bo.getPatientId());
        lqw.eq(StringUtils.isNotBlank(bo.getApprovalStatus()), ChArchiveShareApply::getApprovalStatus, bo.getApprovalStatus());
        lqw.orderByDesc(ChArchiveShareApply::getCreateTime);
        Page<ChArchiveShareApplyVo> page = archiveShareMapper.selectVoPage(pageQuery.build(), lqw);
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
}
