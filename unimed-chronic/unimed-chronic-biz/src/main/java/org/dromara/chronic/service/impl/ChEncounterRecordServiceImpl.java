package org.dromara.chronic.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.common.helper.DiseaseNameHelper;
import org.dromara.chronic.domain.bo.ChEncounterDiagnosisBo;
import org.dromara.chronic.domain.bo.ChEncounterRecordBo;
import org.dromara.chronic.domain.entity.ChEncounterDiagnosis;
import org.dromara.chronic.domain.entity.ChEncounterRecord;
import org.dromara.chronic.domain.vo.ChEncounterRecordVo;
import org.dromara.chronic.mapper.ChEncounterDiagnosisMapper;
import org.dromara.chronic.mapper.ChEncounterRecordMapper;
import org.dromara.chronic.service.IChEncounterRecordService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 诊疗记录服务实现
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChEncounterRecordServiceImpl implements IChEncounterRecordService {

    private final ChEncounterRecordMapper baseMapper;
    private final ChEncounterDiagnosisMapper diagnosisMapper;
    private final DiseaseNameHelper diseaseNameHelper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveDraft(ChEncounterRecordBo bo, List<ChEncounterDiagnosisBo> diagnosisList) {
        ChEncounterRecord entity = MapstructUtils.convert(bo, ChEncounterRecord.class);
        if (entity.getSubmitStatus() == null) {
            entity.setSubmitStatus("DRAFT");
        }
        if (entity.getId() == null) {
            baseMapper.insert(entity);
        } else {
            baseMapper.updateById(entity);
        }
        if (diagnosisList != null && !diagnosisList.isEmpty()) {
            saveDiagnosisList(entity.getId(), bo.getPatientId(), diagnosisList);
        }
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submit(Long encounterId) {
        ChEncounterRecord record = baseMapper.selectById(encounterId);
        if (record == null) {
            throw new RuntimeException("诊疗记录不存在");
        }
        if ("SUBMITTED".equals(record.getSubmitStatus())) {
            return encounterId;
        }
        record.setSubmitStatus("SUBMITTED");
        record.setSubmittedTime(LocalDateTime.now());
        baseMapper.updateById(record);
        return encounterId;
    }

    @Override
    public ChEncounterRecord getById(Long encounterId) {
        return baseMapper.selectById(encounterId);
    }

    @Override
    public ChEncounterRecordVo queryById(Long encounterId) {
        ChEncounterRecordVo vo = baseMapper.selectVoById(encounterId);
        if (vo != null) {
            vo.setDiagnosisList(diagnosisMapper.selectVoList(
                Wrappers.<ChEncounterDiagnosis>lambdaQuery()
                    .eq(ChEncounterDiagnosis::getEncounterId, encounterId)
            ));
            fillEncounterDiseaseNames(Collections.singletonList(vo));
        }
        return vo;
    }

    @Override
    public TableDataInfo<ChEncounterRecordVo> queryPageList(ChEncounterRecordBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChEncounterRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getPatientId()), ChEncounterRecord::getPatientId, bo.getPatientId());
        lqw.eq(StringUtils.isNotBlank(bo.getDiseaseCode()), ChEncounterRecord::getDiseaseCode, bo.getDiseaseCode());
        lqw.eq(StringUtils.isNotBlank(bo.getEncounterType()), ChEncounterRecord::getEncounterType, bo.getEncounterType());
        lqw.eq(StringUtils.isNotBlank(bo.getSubmitStatus()), ChEncounterRecord::getSubmitStatus, bo.getSubmitStatus());
        lqw.orderByDesc(ChEncounterRecord::getEncounterTime);
        Page<ChEncounterRecordVo> page = baseMapper.selectVoPage(pageQuery.build(), lqw);
        fillEncounterDiseaseNames(page.getRecords());
        return TableDataInfo.build(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long updateById(ChEncounterRecordBo bo, List<ChEncounterDiagnosisBo> diagnosisList) {
        ChEncounterRecord record = baseMapper.selectById(bo.getId());
        if (record == null) {
            throw new RuntimeException("诊疗记录不存在");
        }
        if ("SUBMITTED".equals(record.getSubmitStatus())) {
            throw new RuntimeException("已提交的诊疗记录不允许修改");
        }
        MapstructUtils.convert(bo, record);
        baseMapper.updateById(record);
        if (diagnosisList != null) {
            LambdaQueryWrapper<ChEncounterDiagnosis> dlqw = Wrappers.lambdaQuery();
            dlqw.eq(ChEncounterDiagnosis::getEncounterId, record.getId());
            diagnosisMapper.delete(dlqw);
            saveDiagnosisList(record.getId(), bo.getPatientId(), diagnosisList);
        }
        return record.getId();
    }

    private void saveDiagnosisList(Long encounterId, Long patientId, List<ChEncounterDiagnosisBo> diagnosisList) {
        for (ChEncounterDiagnosisBo bo : diagnosisList) {
            ChEncounterDiagnosis entity = MapstructUtils.convert(bo, ChEncounterDiagnosis.class);
            entity.setEncounterId(encounterId);
            entity.setPatientId(patientId);
            diagnosisMapper.insert(entity);
        }
    }

    public List<ChEncounterDiagnosis> listDiagnosisByEncounterId(Long encounterId) {
        LambdaQueryWrapper<ChEncounterDiagnosis> lqw = Wrappers.lambdaQuery();
        lqw.eq(ChEncounterDiagnosis::getEncounterId, encounterId);
        return diagnosisMapper.selectList(lqw);
    }

    @Override
    public ChEncounterRecord queryBySourceBizNo(String sourceBizNo, Long patientId) {
        LambdaQueryWrapper<ChEncounterRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(ChEncounterRecord::getSourceBizNo, sourceBizNo);
        lqw.eq(ChEncounterRecord::getPatientId, patientId);
        return baseMapper.selectOne(lqw, false);
    }

    private void fillEncounterDiseaseNames(List<ChEncounterRecordVo> list) {
        if (CollUtil.isEmpty(list)) return;
        List<String> diseaseCodes = list.stream().map(ChEncounterRecordVo::getDiseaseCode)
            .filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        if (!diseaseCodes.isEmpty()) {
            try {
                Map<String, String> diseaseNameMap = diseaseNameHelper.batchGetDiseaseName(diseaseCodes);
                list.forEach(v -> v.setDiseaseName(diseaseNameMap.get(v.getDiseaseCode())));
            } catch (Exception e) { /* ignore */ }
        }
    }
}