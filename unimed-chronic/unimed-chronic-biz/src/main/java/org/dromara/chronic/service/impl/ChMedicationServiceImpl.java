package org.dromara.chronic.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChDrugInteractionBo;
import org.dromara.chronic.domain.bo.ChMedicationAdjustBo;
import org.dromara.chronic.domain.bo.ChMedicationRecordBo;
import org.dromara.chronic.domain.entity.ChDrugInteraction;
import org.dromara.chronic.domain.entity.ChMedicationAdjust;
import org.dromara.chronic.domain.entity.ChMedicationRecord;
import org.dromara.chronic.domain.vo.ChDrugInteractionVo;
import org.dromara.chronic.domain.vo.ChMedicationAdjustVo;
import org.dromara.chronic.domain.vo.ChMedicationRecordVo;
import org.dromara.chronic.domain.vo.DrugInteractionCheckVo;
import org.dromara.chronic.mapper.ChDrugInteractionMapper;
import org.dromara.chronic.mapper.ChMedicationAdjustMapper;
import org.dromara.chronic.mapper.ChMedicationRecordMapper;
import org.dromara.chronic.service.IChMedicationService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.dromara.common.redis.utils.RedisUtils;

import java.time.Duration;
import java.util.Date;
import java.util.List;

/**
 * 用药管理服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChMedicationServiceImpl implements IChMedicationService {

    private final ChMedicationRecordMapper medicationRecordMapper;
    private final ChMedicationAdjustMapper medicationAdjustMapper;
    private final ChDrugInteractionMapper drugInteractionMapper;

    @Override
    public List<ChMedicationRecordVo> queryMedicationList(Long patientId) {
        return medicationRecordMapper.selectVoList(
            Wrappers.<ChMedicationRecord>lambdaQuery()
                .eq(ChMedicationRecord::getPatientId, patientId)
                .orderByDesc(ChMedicationRecord::getStartDate)
        );
    }

    @Override
    public Boolean addMedication(ChMedicationRecordBo bo) {
        ChMedicationRecord entity = MapstructUtils.convert(bo, ChMedicationRecord.class);
        if (StringUtils.isBlank(entity.getStatus())) {
            entity.setStatus("ACTIVE");
        }
        if (entity.getPrescriberVerified() == null) {
            entity.setPrescriberVerified(Boolean.TRUE);
        }
        return medicationRecordMapper.insert(entity) > 0;
    }

    @Override
    public Boolean stopMedication(Long medId, String reason) {
        ChMedicationRecord entity = medicationRecordMapper.selectById(medId);
        if (entity == null) {
            throw new ServiceException("用药记录不存在");
        }
        entity.setStatus("STOPPED");
        entity.setStopDate(new Date());
        boolean success = medicationRecordMapper.updateById(entity) > 0;
        if (success) {
            ChMedicationAdjust adjust = new ChMedicationAdjust();
            adjust.setMedId(medId);
            adjust.setPatientId(entity.getPatientId());
            adjust.setAdjustType("STOP");
            adjust.setAdjustReason(StringUtils.defaultIfBlank(reason, "停用药物"));
            adjust.setPreviewConfirmed(Boolean.TRUE);
            adjust.setPinVerifiedAt(new Date());
            medicationAdjustMapper.insert(adjust);
        }
        return success;
    }

    @Override
    public List<ChMedicationAdjustVo> queryAdjustList(Long patientId) {
        return medicationAdjustMapper.selectVoList(
            Wrappers.<ChMedicationAdjust>lambdaQuery()
                .eq(ChMedicationAdjust::getPatientId, patientId)
                .orderByDesc(ChMedicationAdjust::getCreateTime)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long recordAdjust(ChMedicationAdjustBo bo) {
        if (!Boolean.TRUE.equals(bo.getPreviewConfirmed())) {
            throw new ServiceException("调药前必须完成预览确认");
        }
        if (StringUtils.isNotBlank(bo.getTargetDrugCode())) {
            DrugInteractionCheckVo checkVo = checkInteraction(bo.getPatientId(), bo.getTargetDrugCode());
            if (checkVo.isConflict()) {
                throw new ServiceException("存在药物相互作用风险：" + checkVo.getDescription());
            }
        }
        ChMedicationAdjust entity = MapstructUtils.convert(bo, ChMedicationAdjust.class);
        if (entity.getPinVerifiedAt() == null) {
            entity.setPinVerifiedAt(new Date());
        }
        medicationAdjustMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public String queryCompliance(Long patientId) {
        List<ChMedicationRecordVo> records = queryMedicationList(patientId);
        if (records.isEmpty()) {
            return "暂无用药记录";
        }
        long activeCount = records.stream().filter(item -> "ACTIVE".equalsIgnoreCase(item.getStatus())).count();
        long adjustedCount = queryAdjustList(patientId).size();
        return "当前活跃用药" + activeCount + "项，累计调药" + adjustedCount + "次";
    }

    @Override
    public TableDataInfo<ChDrugInteractionVo> queryInteractionPage(ChDrugInteractionBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChDrugInteraction> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getDrugCodeA()), ChDrugInteraction::getDrugCodeA, bo.getDrugCodeA());
        lqw.like(StringUtils.isNotBlank(bo.getDrugCodeB()), ChDrugInteraction::getDrugCodeB, bo.getDrugCodeB());
        lqw.eq(StringUtils.isNotBlank(bo.getInteractionLevel()), ChDrugInteraction::getInteractionLevel, bo.getInteractionLevel());
        lqw.orderByAsc(ChDrugInteraction::getDrugCodeA);
        Page<ChDrugInteractionVo> page = drugInteractionMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public Boolean createInteractionRule(ChDrugInteractionBo bo) {
        ChDrugInteraction entity = MapstructUtils.convert(bo, ChDrugInteraction.class);
        return drugInteractionMapper.insert(entity) > 0;
    }

    @Override
    public DrugInteractionCheckVo checkInteraction(Long patientId, String targetDrugCode) {
        DrugInteractionCheckVo result = new DrugInteractionCheckVo();
        result.setConflict(false);
        if (StringUtils.isBlank(targetDrugCode)) {
            return result;
        }
        List<ChMedicationRecord> activeMeds = medicationRecordMapper.selectList(
            Wrappers.<ChMedicationRecord>lambdaQuery()
                .eq(ChMedicationRecord::getPatientId, patientId)
                .eq(ChMedicationRecord::getStatus, "ACTIVE")
        );
        for (ChMedicationRecord item : activeMeds) {
            ChDrugInteraction interaction = drugInteractionMapper.selectOne(
                Wrappers.<ChDrugInteraction>lambdaQuery()
                    .and(wrapper -> wrapper
                        .eq(ChDrugInteraction::getDrugCodeA, item.getDrugCode())
                        .eq(ChDrugInteraction::getDrugCodeB, targetDrugCode)
                        .or()
                        .eq(ChDrugInteraction::getDrugCodeA, targetDrugCode)
                        .eq(ChDrugInteraction::getDrugCodeB, item.getDrugCode()))
            );
            if (ObjectUtil.isNotNull(interaction)) {
                result.setConflict(true);
                result.setExistingDrugCode(item.getDrugCode());
                result.setTargetDrugCode(targetDrugCode);
                result.setInteractionLevel(interaction.getInteractionLevel());
                result.setDescription(interaction.getDescription());
                result.setClinicalAdvice(interaction.getClinicalAdvice());
                return result;
            }
        }
        return result;
    }

    private static final String MEDICATION_CHECKIN_KEY = "chronic:medication:checkin:";

    @Override
    public Boolean checkinMedication(Long medId, Long patientId) {
        ChMedicationRecord entity = medicationRecordMapper.selectById(medId);
        if (entity == null) {
            throw new ServiceException("用药记录不存在");
        }
        if (!patientId.equals(entity.getPatientId())) {
            throw new ServiceException("无权操作他人的用药记录");
        }
        if ("STOPPED".equalsIgnoreCase(entity.getStatus())) {
            throw new ServiceException("该药物已停用，无法打卡");
        }
        // 记录打卡时间到 Redis，key: chronic:medication:checkin:{medId}:{yyyy-MM-dd}
        String dateKey = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String redisKey = MEDICATION_CHECKIN_KEY + medId + ":" + dateKey;
        RedisUtils.setCacheObject(redisKey, String.valueOf(System.currentTimeMillis()), Duration.ofHours(48));
        return true;
    }
}
