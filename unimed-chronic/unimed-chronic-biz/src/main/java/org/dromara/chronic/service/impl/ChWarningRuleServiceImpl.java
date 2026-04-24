package org.dromara.chronic.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.common.helper.DiseaseNameHelper;
import org.dromara.chronic.common.helper.OrgNameHelper;
import org.dromara.chronic.domain.bo.ChWarningRuleBo;
import org.dromara.chronic.domain.entity.ChWarningRule;
import org.dromara.chronic.domain.vo.ChWarningRuleVo;
import org.dromara.chronic.mapper.ChWarningRuleMapper;
import org.dromara.chronic.service.IChWarningRuleService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 预警规则服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChWarningRuleServiceImpl implements IChWarningRuleService {

    private final ChWarningRuleMapper baseMapper;
    private final OrgNameHelper orgNameHelper;
    private final DiseaseNameHelper diseaseNameHelper;

    @Override
    public Long add(ChWarningRuleBo bo) {
        ChWarningRule entity = MapstructUtils.convert(bo, ChWarningRule.class);
        baseMapper.insert(entity);
        return entity.getRuleId();
    }

    @Override
    public Void update(ChWarningRuleBo bo) {
        ChWarningRule existing = baseMapper.selectById(bo.getRuleId());
        if (ObjectUtil.isNull(existing)) {
            throw new ServiceException("预警规则不存在");
        }
        ChWarningRule entity = MapstructUtils.convert(bo, ChWarningRule.class);
        baseMapper.updateById(entity);
        return null;
    }

    @Override
    public ChWarningRuleVo queryById(Long ruleId) {
        ChWarningRuleVo vo = baseMapper.selectVoById(ruleId);
        if (vo != null) {
            fillWarningRuleNames(Collections.singletonList(vo));
        }
        return vo;
    }

    @Override
    public TableDataInfo<ChWarningRuleVo> queryPageList(ChWarningRuleBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChWarningRule> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getDiseaseCode()), ChWarningRule::getDiseaseCode, bo.getDiseaseCode());
        lqw.eq(StringUtils.isNotBlank(bo.getMetricType()), ChWarningRule::getMetricType, bo.getMetricType());
        lqw.eq(StringUtils.isNotBlank(bo.getWarningLevel()), ChWarningRule::getWarningLevel, bo.getWarningLevel());
        lqw.orderByDesc(ChWarningRule::getCreateTime);
        Page<ChWarningRuleVo> page = baseMapper.selectVoPage(pageQuery.build(), lqw);
        fillWarningRuleNames(page.getRecords());
        return TableDataInfo.build(page);
    }

    @Override
    public List<ChWarningRuleVo> queryByDiseaseCode(String diseaseCode) {
        List<ChWarningRuleVo> list = baseMapper.selectVoList(
            Wrappers.<ChWarningRule>lambdaQuery()
                .eq(ChWarningRule::getDiseaseCode, diseaseCode)
                .orderByAsc(ChWarningRule::getWarningLevel)
        );
        fillWarningRuleNames(list);
        return list;
    }

    private void fillWarningRuleNames(List<ChWarningRuleVo> list) {
        if (CollUtil.isEmpty(list)) return;
        List<Long> orgIds = list.stream().map(ChWarningRuleVo::getOrgId)
            .filter(ObjectUtil::isNotNull).distinct().collect(Collectors.toList());
        if (!orgIds.isEmpty()) {
            try {
                Map<Long, String> orgNameMap = orgNameHelper.batchGetOrgName(orgIds);
                list.forEach(v -> v.setOrgName(orgNameMap.get(v.getOrgId())));
            } catch (Exception e) { /* ignore */ }
        }
        List<String> diseaseCodes = list.stream().map(ChWarningRuleVo::getDiseaseCode)
            .filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        if (!diseaseCodes.isEmpty()) {
            try {
                Map<String, String> diseaseNameMap = diseaseNameHelper.batchGetDiseaseName(diseaseCodes);
                list.forEach(v -> v.setDiseaseName(diseaseNameMap.get(v.getDiseaseCode())));
            } catch (Exception e) { /* ignore */ }
        }
    }
}
