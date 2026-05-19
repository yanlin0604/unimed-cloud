package org.dromara.chronic.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChPatientCloseApplyBo;
import org.dromara.chronic.domain.entity.ChPatientCloseApply;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.domain.vo.ChPatientCloseApplyVo;
import org.dromara.chronic.mapper.ChPatientCloseApplyMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.chronic.service.IChPatientCloseApplyService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 患者结案申请服务实现
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChPatientCloseApplyServiceImpl implements IChPatientCloseApplyService {

    private static final Set<String> ALLOWED_AUDIT_STATUS = Set.of("APPROVED", "REJECTED", "WITHDRAWN");
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String DEFAULT_APPLY_SOURCE = "ADMIN";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final ChPatientCloseApplyMapper closeApplyMapper;
    private final ChPatientProfileMapper patientProfileMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long applyClose(ChPatientCloseApplyBo bo) {
        if (ObjectUtil.isNull(bo.getPatientId())) {
            throw new ServiceException("患者ID不能为空");
        }
        ChPatientProfile profile = patientProfileMapper.selectById(bo.getPatientId());
        if (ObjectUtil.isNull(profile)) {
            throw new ServiceException("患者档案不存在");
        }
        // 同一患者存在 PENDING 申请时禁止重复发起
        Long pendingCount = closeApplyMapper.selectCount(
            Wrappers.<ChPatientCloseApply>lambdaQuery()
                .eq(ChPatientCloseApply::getPatientId, bo.getPatientId())
                .eq(ChPatientCloseApply::getAuditStatus, STATUS_PENDING)
        );
        if (pendingCount != null && pendingCount > 0) {
            throw new ServiceException("该患者已存在待审核的结案申请，请勿重复提交");
        }

        ChPatientCloseApply entity = new ChPatientCloseApply();
        entity.setPatientId(bo.getPatientId());
        entity.setCloseType(bo.getCloseType());
        entity.setApplyReason(bo.getApplyReason());
        entity.setEvidenceFileId(bo.getEvidenceFileId());
        entity.setApplicantUserId(LoginHelper.getUserId());
        entity.setApplySource(StringUtils.isNotBlank(bo.getApplySource()) ? bo.getApplySource() : DEFAULT_APPLY_SOURCE);
        entity.setAuditStatus(STATUS_PENDING);
        entity.setSnapshotJson(buildSnapshotJson(bo));

        closeApplyMapper.insert(entity);
        return entity.getApplyId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditClose(ChPatientCloseApplyBo bo) {
        if (ObjectUtil.isNull(bo.getApplyId())) {
            throw new ServiceException("申请ID不能为空");
        }
        String targetStatus = bo.getAuditStatus();
        if (!ALLOWED_AUDIT_STATUS.contains(targetStatus)) {
            throw new ServiceException("无效的审核状态：" + targetStatus);
        }
        ChPatientCloseApply apply = closeApplyMapper.selectById(bo.getApplyId());
        if (ObjectUtil.isNull(apply)) {
            throw new ServiceException("结案申请不存在");
        }
        if (!STATUS_PENDING.equals(apply.getAuditStatus())) {
            throw new ServiceException("当前申请已完成审核，状态：" + apply.getAuditStatus());
        }
        if ("REJECTED".equals(targetStatus) && StringUtils.isBlank(bo.getRejectReason()) && StringUtils.isBlank(bo.getAuditRemark())) {
            throw new ServiceException("驳回时必须填写驳回理由或审核备注");
        }

        apply.setAuditStatus(targetStatus);
        apply.setAuditRemark(bo.getAuditRemark());
        apply.setRejectReason(bo.getRejectReason());
        apply.setAuditorUserId(LoginHelper.getUserId());
        apply.setAuditTime(new Date());
        closeApplyMapper.updateById(apply);

        // 通过则联动更新患者管理状态为 CLOSED
        if (STATUS_APPROVED.equals(targetStatus)) {
            ChPatientProfile profile = patientProfileMapper.selectById(apply.getPatientId());
            if (ObjectUtil.isNotNull(profile)) {
                profile.setManageStatus("CLOSED");
                patientProfileMapper.updateById(profile);
            }
        }
    }

    @Override
    public TableDataInfo<ChPatientCloseApplyVo> queryPageList(ChPatientCloseApplyBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChPatientCloseApply> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getPatientId()), ChPatientCloseApply::getPatientId, bo.getPatientId());
        lqw.eq(StringUtils.isNotBlank(bo.getCloseType()), ChPatientCloseApply::getCloseType, bo.getCloseType());
        lqw.eq(StringUtils.isNotBlank(bo.getAuditStatus()), ChPatientCloseApply::getAuditStatus, bo.getAuditStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getApplySource()), ChPatientCloseApply::getApplySource, bo.getApplySource());
        lqw.orderByDesc(ChPatientCloseApply::getCreateTime);
        Page<ChPatientCloseApplyVo> page = closeApplyMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public ChPatientCloseApplyVo queryById(Long applyId) {
        return closeApplyMapper.selectVoById(applyId);
    }

    @Override
    public ChPatientCloseApplyVo queryLatestByPatient(Long patientId) {
        if (ObjectUtil.isNull(patientId)) {
            return null;
        }
        ChPatientCloseApply entity = closeApplyMapper.selectOne(
            Wrappers.<ChPatientCloseApply>lambdaQuery()
                .eq(ChPatientCloseApply::getPatientId, patientId)
                .orderByDesc(ChPatientCloseApply::getCreateTime)
                .last("LIMIT 1"),
            false
        );
        return entity == null ? null : BeanUtil.copyProperties(entity, ChPatientCloseApplyVo.class);
    }

    /**
     * 把前端附加的结案日期、转出机构、联动开关等序列化到 snapshot_json，便于审核回放与扩展
     */
    private String buildSnapshotJson(ChPatientCloseApplyBo bo) {
        Map<String, Object> snapshot = new HashMap<>(8);
        snapshot.put("closeDate", bo.getCloseDate());
        snapshot.put("transferOrg", bo.getTransferOrg());
        snapshot.put("terminateContract", bo.getTerminateContract());
        snapshot.put("terminateFollowup", bo.getTerminateFollowup());
        snapshot.put("archiveAlert", bo.getArchiveAlert());
        snapshot.put("notifyContact", bo.getNotifyContact());
        try {
            return OBJECT_MAPPER.writeValueAsString(snapshot);
        } catch (Exception e) {
            log.warn("结案申请快照序列化失败, patientId={}", bo.getPatientId(), e);
            return null;
        }
    }
}
