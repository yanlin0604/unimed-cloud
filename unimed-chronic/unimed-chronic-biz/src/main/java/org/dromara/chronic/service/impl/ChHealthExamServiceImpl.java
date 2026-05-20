package org.dromara.chronic.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.common.helper.OrgNameHelper;
import org.dromara.chronic.domain.bo.ChHealthExamBo;
import org.dromara.chronic.domain.bo.ChHealthExamItemBo;
import org.dromara.chronic.domain.bo.EgfrCalcBo;
import org.dromara.chronic.domain.entity.ChHealthExam;
import org.dromara.chronic.domain.entity.ChHealthExamItem;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.domain.vo.ChHealthExamItemVo;
import org.dromara.chronic.domain.vo.ChHealthExamVo;
import org.dromara.chronic.domain.vo.EgfrCalcVo;
import org.dromara.chronic.mapper.ChHealthExamItemMapper;
import org.dromara.chronic.mapper.ChHealthExamMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.chronic.service.IChHealthExamService;
import org.dromara.chronic.utils.EgfrCalculator;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 体检检验服务实现
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChHealthExamServiceImpl implements IChHealthExamService {

    private final ChHealthExamMapper examMapper;
    private final ChHealthExamItemMapper itemMapper;
    private final ChPatientProfileMapper patientProfileMapper;
    private final OrgNameHelper orgNameHelper;

    @Override
    public Long createExam(ChHealthExamBo bo) {
        ChHealthExam entity = MapstructUtils.convert(bo, ChHealthExam.class);
        examMapper.insert(entity);
        return entity.getExamId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void updateExam(ChHealthExamBo bo) {
        ChHealthExam existing = examMapper.selectById(bo.getExamId());
        if (ObjectUtil.isNull(existing)) {
            throw new ServiceException("体检报告不存在");
        }
        ChHealthExam entity = MapstructUtils.convert(bo, ChHealthExam.class);
        examMapper.updateById(entity);
        return null;
    }

    @Override
    public ChHealthExamVo queryById(Long examId) {
        ChHealthExamVo vo = examMapper.selectVoById(examId);
        if (vo != null) {
            vo.setItems(queryItemsByExamId(examId));
            fillExamOrgNames(Collections.singletonList(vo));
            fillPatientNames(Collections.singletonList(vo));
        }
        return vo;
    }

    @Override
    public TableDataInfo<ChHealthExamVo> queryPageList(ChHealthExamBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChHealthExam> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getPatientId()), ChHealthExam::getPatientId, bo.getPatientId());
        lqw.eq(StringUtils.isNotBlank(bo.getExamType()), ChHealthExam::getExamType, bo.getExamType());
        lqw.eq(StringUtils.isNotBlank(bo.getSpecialCategory()), ChHealthExam::getSpecialCategory, bo.getSpecialCategory());
        lqw.orderByDesc(ChHealthExam::getExamDate);
        Page<ChHealthExamVo> page = examMapper.selectVoPage(pageQuery.build(), lqw);
        fillExamOrgNames(page.getRecords());
        fillPatientNames(page.getRecords());
        return TableDataInfo.build(page);
    }

    @Override
    public List<ChHealthExamVo> queryByPatientId(Long patientId) {
        List<ChHealthExamVo> list = examMapper.selectVoList(
            Wrappers.<ChHealthExam>lambdaQuery()
                .eq(ChHealthExam::getPatientId, patientId)
                .orderByDesc(ChHealthExam::getExamDate)
        );
        fillExamOrgNames(list);
        fillPatientNames(list);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long syncLisExam(ChHealthExamBo bo) {
        if (StringUtils.isNotBlank(bo.getExternalSn())) {
            ChHealthExam existed = examMapper.selectOne(
                Wrappers.<ChHealthExam>lambdaQuery().eq(ChHealthExam::getExternalSn, bo.getExternalSn())
            );
            if (ObjectUtil.isNotNull(existed)) {
                log.info("LIS幂等命中 externalSn={}, 返回已有examId={}", bo.getExternalSn(), existed.getExamId());
                return existed.getExamId();
            }
        }
        return createExam(bo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long syncPacsExam(ChHealthExamBo bo) {
        return syncLisExam(bo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addItem(ChHealthExamItemBo bo) {
        ChHealthExamItem entity = MapstructUtils.convert(bo, ChHealthExamItem.class);
        // 肌酐项自动计算 eGFR 并回填到当前记录
        if (entity != null && entity.getEgfrValue() == null
            && EgfrCalculator.isCreatinineItem(bo.getItemCode())
            && StringUtils.isNotBlank(bo.getResultValue())) {
            try {
                BigDecimal scr = new BigDecimal(bo.getResultValue().trim());
                String unit = inferCreatinineUnit(bo.getReferenceRange());
                PatientContext ctx = loadPatientContext(bo.getExamId());
                if (ctx != null) {
                    BigDecimal egfr = EgfrCalculator.calculate(scr, unit, ctx.age, ctx.female);
                    entity.setEgfrValue(egfr);
                }
            } catch (Exception e) {
                log.warn("自动计算 eGFR 失败 examId={}, value={}", bo.getExamId(), bo.getResultValue(), e);
            }
        }
        itemMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public List<ChHealthExamItemVo> queryItemsByExamId(Long examId) {
        return itemMapper.selectVoList(
            Wrappers.<ChHealthExamItem>lambdaQuery()
                .eq(ChHealthExamItem::getExamId, examId)
                .orderByAsc(ChHealthExamItem::getId)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void removeExam(Long examId) {
        if (ObjectUtil.isNull(examMapper.selectById(examId))) {
            throw new ServiceException("体检报告不存在");
        }
        itemMapper.delete(Wrappers.<ChHealthExamItem>lambdaQuery().eq(ChHealthExamItem::getExamId, examId));
        examMapper.deleteById(examId);
        return null;
    }

    @Override
    public EgfrCalcVo calcEgfr(EgfrCalcBo bo) {
        Integer age = bo.getAge();
        Boolean female = parseFemale(bo.getGender());
        // 若提供 patientId 则从档案补齐
        if (bo.getPatientId() != null) {
            ChPatientProfile profile = patientProfileMapper.selectById(bo.getPatientId());
            if (profile != null) {
                if (age == null) age = profile.getAge();
                if (female == null) female = parseFemale(profile.getGender());
            }
        }
        if (age == null || female == null) {
            throw new ServiceException("缺少年龄或性别，无法计算 eGFR");
        }
        BigDecimal egfr = EgfrCalculator.calculate(bo.getCreatinine(), bo.getUnit(), age, female);
        if (egfr == null) {
            throw new ServiceException("eGFR 计算参数非法");
        }
        EgfrCalcVo vo = new EgfrCalcVo();
        vo.setEgfrValue(egfr);
        fillCkdStage(vo, egfr);
        return vo;
    }

    private Boolean parseFemale(String gender) {
        if (StringUtils.isBlank(gender)) return null;
        // 字典 chronic_gender：0=女 1=男 2=未知
        if ("0".equals(gender) || "F".equalsIgnoreCase(gender) || "FEMALE".equalsIgnoreCase(gender)) {
            return Boolean.TRUE;
        }
        if ("1".equals(gender) || "M".equalsIgnoreCase(gender) || "MALE".equalsIgnoreCase(gender)) {
            return Boolean.FALSE;
        }
        return null;
    }

    private void fillCkdStage(EgfrCalcVo vo, BigDecimal egfr) {
        double v = egfr.doubleValue();
        String stage;
        String desc;
        if (v >= 90) { stage = "G1"; desc = "肾功能正常或高滤过"; }
        else if (v >= 60) { stage = "G2"; desc = "肾功能轻度下降"; }
        else if (v >= 45) { stage = "G3a"; desc = "肾功能轻-中度下降"; }
        else if (v >= 30) { stage = "G3b"; desc = "肾功能中-重度下降"; }
        else if (v >= 15) { stage = "G4"; desc = "肾功能重度下降"; }
        else { stage = "G5"; desc = "肾衰竭"; }
        vo.setCkdStage(stage);
        vo.setStageDescription(desc);
    }

    private String inferCreatinineUnit(String referenceRange) {
        if (referenceRange == null) return "MG_DL";
        String r = referenceRange.toLowerCase();
        if (r.contains("umol") || r.contains("μmol") || r.contains("µmol")) {
            return "UMOL_L";
        }
        return "MG_DL";
    }

    private PatientContext loadPatientContext(Long examId) {
        if (examId == null) return null;
        ChHealthExam exam = examMapper.selectById(examId);
        if (exam == null || exam.getPatientId() == null) return null;
        ChPatientProfile profile = patientProfileMapper.selectById(exam.getPatientId());
        if (profile == null) return null;
        Boolean female = parseFemale(profile.getGender());
        if (profile.getAge() == null || female == null) return null;
        return new PatientContext(profile.getAge(), female);
    }

    private record PatientContext(Integer age, Boolean female) {
    }

    private void fillExamOrgNames(List<ChHealthExamVo> list) {
        if (CollUtil.isEmpty(list)) return;
        List<Long> orgIds = list.stream().map(ChHealthExamVo::getExamOrgId)
            .filter(ObjectUtil::isNotNull).distinct().collect(Collectors.toList());
        if (!orgIds.isEmpty()) {
            try {
                Map<Long, String> orgNameMap = orgNameHelper.batchGetOrgName(orgIds);
                list.forEach(v -> v.setExamOrgName(orgNameMap.get(v.getExamOrgId())));
            } catch (Exception e) { /* ignore */ }
        }
    }

    private void fillPatientNames(List<ChHealthExamVo> list) {
        if (CollUtil.isEmpty(list)) return;
        List<Long> patientIds = list.stream().map(ChHealthExamVo::getPatientId)
            .filter(ObjectUtil::isNotNull).distinct().collect(Collectors.toList());
        if (!patientIds.isEmpty()) {
            try {
                Map<Long, String> nameMap = patientProfileMapper.selectBatchIds(patientIds).stream()
                    .collect(Collectors.toMap(ChPatientProfile::getPatientId, ChPatientProfile::getName, (a, b) -> a));
                list.forEach(v -> v.setPatientName(nameMap.get(v.getPatientId())));
            } catch (Exception e) { /* ignore */ }
        }
    }
}
