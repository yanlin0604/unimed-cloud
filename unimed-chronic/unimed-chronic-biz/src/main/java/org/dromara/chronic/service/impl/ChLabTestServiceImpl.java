package org.dromara.chronic.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChLabTestBo;
import org.dromara.chronic.domain.entity.ChLabTest;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.domain.vo.ChLabTestVo;
import org.dromara.chronic.mapper.ChLabTestMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.chronic.service.IChLabTestService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 检验记录服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChLabTestServiceImpl implements IChLabTestService {

    private final ChLabTestMapper labTestMapper;
    private final ChPatientProfileMapper patientProfileMapper;

    @Override
    public TableDataInfo<ChLabTestVo> queryPageList(ChLabTestBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChLabTest> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getPatientId()), ChLabTest::getPatientId, bo.getPatientId());
        lqw.like(StringUtils.isNotBlank(bo.getTestType()), ChLabTest::getTestType, bo.getTestType());
        lqw.orderByDesc(ChLabTest::getTestDate);
        Page<ChLabTestVo> page = labTestMapper.selectVoPage(pageQuery.build(), lqw);
        fillPatientNames(page.getRecords());
        return TableDataInfo.build(page);
    }

    @Override
    public List<ChLabTestVo> queryByPatientId(Long patientId) {
        return labTestMapper.selectVoList(
            Wrappers.<ChLabTest>lambdaQuery()
                .eq(ChLabTest::getPatientId, patientId)
                .orderByDesc(ChLabTest::getTestDate)
        );
    }

    @Override
    public ChLabTestVo queryById(Long testId) {
        ChLabTestVo vo = labTestMapper.selectVoById(testId);
        if (vo != null) {
            fillPatientNames(java.util.Collections.singletonList(vo));
        }
        return vo;
    }

    @Override
    public Long create(ChLabTestBo bo) {
        ChLabTest entity = MapstructUtils.convert(bo, ChLabTest.class);
        labTestMapper.insert(entity);
        return entity.getTestId();
    }

    @Override
    public Boolean update(ChLabTestBo bo) {
        ChLabTest entity = MapstructUtils.convert(bo, ChLabTest.class);
        return labTestMapper.updateById(entity) > 0;
    }

    @Override
    public Boolean deleteByIds(java.util.Collection<Long> ids) {
        return labTestMapper.deleteByIds(ids) > 0;
    }

    private void fillPatientNames(List<ChLabTestVo> list) {
        if (CollUtil.isEmpty(list)) return;
        List<Long> patientIds = list.stream().map(ChLabTestVo::getPatientId)
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
