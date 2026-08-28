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
import org.dromara.chronic.domain.entity.ChMedicationCheckin;
import org.dromara.chronic.domain.entity.ChMedicationRecord;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.domain.vo.ChDrugInteractionVo;
import org.dromara.chronic.domain.vo.ChMedicationAdjustVo;
import org.dromara.chronic.domain.vo.ChMedicationCheckinStatVo;
import org.dromara.chronic.domain.vo.ChMedicationRecordVo;
import org.dromara.chronic.domain.vo.DrugInteractionCheckVo;
import org.dromara.chronic.mapper.ChDrugInteractionMapper;
import org.dromara.chronic.mapper.ChMedicationAdjustMapper;
import org.dromara.chronic.mapper.ChMedicationCheckinMapper;
import org.dromara.chronic.mapper.ChMedicationRecordMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.chronic.service.IChMedicationService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final ChMedicationCheckinMapper medicationCheckinMapper;
    private final ChDrugInteractionMapper drugInteractionMapper;
    private final ChPatientProfileMapper patientProfileMapper;

    @Override
    public List<ChMedicationRecordVo> queryMedicationList(Long patientId) {
        return medicationRecordMapper.selectVoList(
            Wrappers.<ChMedicationRecord>lambdaQuery()
                .eq(ChMedicationRecord::getPatientId, patientId)
                .orderByDesc(ChMedicationRecord::getStartDate)
        );
    }

    @Override
    public TableDataInfo<ChMedicationRecordVo> queryMedicationPage(Long patientId, String status,
                                                                    String drugName, PageQuery pageQuery) {
        Page<ChMedicationRecordVo> page = medicationRecordMapper.selectVoPage(
            pageQuery.build(),
            Wrappers.<ChMedicationRecord>lambdaQuery()
                .eq(ObjectUtil.isNotNull(patientId), ChMedicationRecord::getPatientId, patientId)
                .eq(StringUtils.isNotBlank(status), ChMedicationRecord::getStatus, status)
                .like(StringUtils.isNotBlank(drugName), ChMedicationRecord::getDrugName, drugName)
                .orderByDesc(ChMedicationRecord::getStartDate));
        fillMedicationPatientNames(page.getRecords(), ChMedicationRecordVo::getPatientId,
            ChMedicationRecordVo::setPatientName);
        return TableDataInfo.build(page);
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
    public TableDataInfo<ChMedicationAdjustVo> queryAdjustPage(Long patientId, String adjustType,
                                                                PageQuery pageQuery) {
        Page<ChMedicationAdjustVo> page = medicationAdjustMapper.selectVoPage(
            pageQuery.build(),
            Wrappers.<ChMedicationAdjust>lambdaQuery()
                .eq(ObjectUtil.isNotNull(patientId), ChMedicationAdjust::getPatientId, patientId)
                .eq(StringUtils.isNotBlank(adjustType), ChMedicationAdjust::getAdjustType, adjustType)
                .orderByDesc(ChMedicationAdjust::getCreateTime));
        fillMedicationPatientNames(page.getRecords(), ChMedicationAdjustVo::getPatientId,
            ChMedicationAdjustVo::setPatientName);
        return TableDataInfo.build(page);
    }

    /**
     * 批量回填患者姓名（用药记录/调整记录分页共用）
     */
    private <T> void fillMedicationPatientNames(List<T> vos, Function<T, Long> idGetter,
                                                BiConsumer<T, String> nameSetter) {
        if (vos == null || vos.isEmpty()) {
            return;
        }
        List<Long> patientIds = vos.stream().map(idGetter).filter(ObjectUtil::isNotNull).distinct().toList();
        if (patientIds.isEmpty()) {
            return;
        }
        Map<Long, String> patientNames = patientProfileMapper.selectByIds(patientIds).stream()
            .collect(Collectors.toMap(ChPatientProfile::getPatientId, ChPatientProfile::getName, (a, b) -> a));
        vos.forEach(vo -> nameSetter.accept(vo, patientNames.get(idGetter.apply(vo))));
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean checkinMedication(Long medId, Long patientId) {
        ChMedicationRecord entity = medicationRecordMapper.selectById(medId);
        if (entity == null) {
            throw new ServiceException("用药记录不存在");
        }
        if (!patientId.equals(entity.getPatientId())) {
            throw new ServiceException("无权操作他人的用药记录");
        }
        if (!"ACTIVE".equalsIgnoreCase(entity.getStatus())) {
            throw new ServiceException("该药物已停用，无法打卡");
        }

        LocalDate today = LocalDate.now();
        Date now = new Date();
        ChMedicationCheckin existing = medicationCheckinMapper.selectOne(
            Wrappers.<ChMedicationCheckin>lambdaQuery()
                .eq(ChMedicationCheckin::getPatientId, patientId)
                .eq(ChMedicationCheckin::getMedId, medId)
                .eq(ChMedicationCheckin::getCheckinDate, today)
        );
        if (existing == null) {
            ChMedicationCheckin checkin = new ChMedicationCheckin();
            checkin.setPatientId(patientId);
            checkin.setMedId(medId);
            checkin.setCheckinDate(today);
            checkin.setFirstCheckinTime(now);
            checkin.setLastCheckinTime(now);
            try {
                medicationCheckinMapper.insert(checkin);
            } catch (DuplicateKeyException ignored) {
                updateLastCheckinTime(patientId, medId, today, now);
            }
        } else {
            existing.setLastCheckinTime(now);
            medicationCheckinMapper.updateById(existing);
        }

        return true;
    }

    @Override
    public ChMedicationCheckinStatVo queryCheckinStat(Long patientId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        List<ChMedicationRecordVo> activeMedications = medicationRecordMapper.selectVoList(
            Wrappers.<ChMedicationRecord>lambdaQuery()
                .eq(ChMedicationRecord::getPatientId, patientId)
                .eq(ChMedicationRecord::getStatus, "ACTIVE")
                .orderByDesc(ChMedicationRecord::getStartDate)
        );

        ChMedicationCheckinStatVo result = new ChMedicationCheckinStatVo();
        result.setHasActiveMedication(!activeMedications.isEmpty());
        if (activeMedications.isEmpty()) {
            result.setConsecutiveDays(0);
            result.setWeekCompletedDays(0);
            result.setWeekExpectedDays(0);
            result.setWeekAchievementRate(0);
            result.setCheckedInToday(false);
            result.setMedications(List.of());
            return result;
        }

        HashSet<Long> activeMedicationIds = activeMedications.stream()
            .map(ChMedicationRecordVo::getMedId)
            .collect(Collectors.toCollection(HashSet::new));
        List<ChMedicationCheckin> checkins = medicationCheckinMapper.selectList(
            Wrappers.<ChMedicationCheckin>lambdaQuery()
                .eq(ChMedicationCheckin::getPatientId, patientId)
                .in(ChMedicationCheckin::getMedId, activeMedicationIds)
                .le(ChMedicationCheckin::getCheckinDate, today)
                .orderByDesc(ChMedicationCheckin::getCheckinDate)
        );
        HashSet<LocalDate> completedDates = checkins.stream()
            .map(ChMedicationCheckin::getCheckinDate)
            .collect(Collectors.toCollection(HashSet::new));
        HashSet<Long> todayMedicationIds = checkins.stream()
            .filter(item -> today.equals(item.getCheckinDate()))
            .map(ChMedicationCheckin::getMedId)
            .collect(Collectors.toCollection(HashSet::new));

        boolean checkedInToday = completedDates.contains(today);
        LocalDate cursor = checkedInToday ? today : today.minusDays(1);
        int consecutiveDays = 0;
        while (completedDates.contains(cursor)) {
            consecutiveDays++;
            cursor = cursor.minusDays(1);
        }
        int weekCompletedDays = (int) completedDates.stream()
            .filter(date -> !date.isBefore(weekStart) && !date.isAfter(today))
            .count();
        int weekExpectedDays = today.getDayOfWeek().getValue();
        int achievementRate = (int) Math.round(weekCompletedDays * 100.0 / weekExpectedDays);

        List<ChMedicationCheckinStatVo.MedicationTodayVo> medications = activeMedications.stream().map(medication -> {
            ChMedicationCheckinStatVo.MedicationTodayVo item = new ChMedicationCheckinStatVo.MedicationTodayVo();
            item.setMedId(medication.getMedId());
            item.setDrugName(medication.getDrugName());
            item.setDosage(medication.getDosage());
            item.setFrequency(medication.getFrequency());
            item.setFrequencyName(medication.getFrequencyName());
            item.setCheckedInToday(todayMedicationIds.contains(medication.getMedId()));
            return item;
        }).toList();

        result.setConsecutiveDays(consecutiveDays);
        result.setWeekCompletedDays(weekCompletedDays);
        result.setWeekExpectedDays(weekExpectedDays);
        result.setWeekAchievementRate(achievementRate);
        result.setCheckedInToday(checkedInToday);
        result.setMedications(medications);
        return result;
    }

    private void updateLastCheckinTime(Long patientId, Long medId, LocalDate checkinDate, Date now) {
        medicationCheckinMapper.update(null,
            Wrappers.<ChMedicationCheckin>lambdaUpdate()
                .eq(ChMedicationCheckin::getPatientId, patientId)
                .eq(ChMedicationCheckin::getMedId, medId)
                .eq(ChMedicationCheckin::getCheckinDate, checkinDate)
                .set(ChMedicationCheckin::getLastCheckinTime, now)
        );
    }
}
