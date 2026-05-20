package org.dromara.chronic.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChMedicalExamBo;
import org.dromara.chronic.domain.entity.ChMedicalExam;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.domain.vo.ChMedicalExamVo;
import org.dromara.chronic.mapper.ChMedicalExamMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.chronic.service.IChMedicalExamService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 检查记录服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChMedicalExamServiceImpl implements IChMedicalExamService {

    private final ChMedicalExamMapper medicalExamMapper;
    private final ChPatientProfileMapper patientProfileMapper;

    @Override
    public TableDataInfo<ChMedicalExamVo> queryPageList(ChMedicalExamBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChMedicalExam> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getPatientId()), ChMedicalExam::getPatientId, bo.getPatientId());
        lqw.like(StringUtils.isNotBlank(bo.getExamType()), ChMedicalExam::getExamType, bo.getExamType());
        lqw.orderByDesc(ChMedicalExam::getExamDate);
        Page<ChMedicalExamVo> page = medicalExamMapper.selectVoPage(pageQuery.build(), lqw);
        fillPatientNames(page.getRecords());
        return TableDataInfo.build(page);
    }

    @Override
    public List<ChMedicalExamVo> queryByPatientId(Long patientId) {
        return medicalExamMapper.selectVoList(
            Wrappers.<ChMedicalExam>lambdaQuery()
                .eq(ChMedicalExam::getPatientId, patientId)
                .orderByDesc(ChMedicalExam::getExamDate)
        );
    }

    @Override
    public ChMedicalExamVo queryById(Long examId) {
        ChMedicalExamVo vo = medicalExamMapper.selectVoById(examId);
        if (vo != null) {
            fillPatientNames(java.util.Collections.singletonList(vo));
        }
        return vo;
    }

    @Override
    public Long create(ChMedicalExamBo bo) {
        ChMedicalExam entity = MapstructUtils.convert(bo, ChMedicalExam.class);
        medicalExamMapper.insert(entity);
        return entity.getExamId();
    }

    @Override
    public Boolean update(ChMedicalExamBo bo) {
        ChMedicalExam entity = MapstructUtils.convert(bo, ChMedicalExam.class);
        return medicalExamMapper.updateById(entity) > 0;
    }

    @Override
    public Boolean deleteByIds(java.util.Collection<Long> ids) {
        return medicalExamMapper.deleteByIds(ids) > 0;
    }

    private void fillPatientNames(List<ChMedicalExamVo> list) {
        if (CollUtil.isEmpty(list)) return;
        List<Long> patientIds = list.stream().map(ChMedicalExamVo::getPatientId)
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
