package org.dromara.chronic.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.common.helper.DiseaseNameHelper;
import org.dromara.chronic.domain.bo.ChFollowupRuleBo;
import org.dromara.chronic.domain.entity.ChFollowupRule;
import org.dromara.chronic.domain.vo.ChFollowupRuleVo;
import org.dromara.chronic.mapper.ChFollowupRuleMapper;
import org.dromara.chronic.service.IChFollowupRuleService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 慢病随访排期规则配置服务实现
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChFollowupRuleServiceImpl implements IChFollowupRuleService {

    /**
     * 合法风险/管理等级
     */
    private static final Set<String> RISK_LEVELS = Set.of("LOW", "MEDIUM", "HIGH", "VERY_HIGH", "ANY");

    /**
     * 合法默认随访方式
     */
    private static final Set<String> VISIT_TYPES = Set.of("PHONE", "ONLINE", "OFFLINE", "VIDEO");

    private final ChFollowupRuleMapper ruleMapper;
    private final DiseaseNameHelper diseaseNameHelper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRule(ChFollowupRuleBo bo) {
        validateRule(bo);
        ChFollowupRule entity = MapstructUtils.convert(bo, ChFollowupRule.class);
        // 统一规格化
        entity.setDiseaseCode(bo.getDiseaseCode().trim().toUpperCase(Locale.ROOT));
        entity.setRiskLevel(bo.getRiskLevel().trim().toUpperCase(Locale.ROOT));
        if (entity.getFirstDueDays() == null) {
            entity.setFirstDueDays(7);
        }
        if (StringUtils.isBlank(entity.getDefaultVisitType())) {
            entity.setDefaultVisitType("PHONE");
        }
        // 幂等: 同一(病种,等级)已存在则拒绝,避免唯一键冲突
        checkUnique(bo.getDiseaseCode(), bo.getRiskLevel());
        ruleMapper.insert(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void updateRule(ChFollowupRuleBo bo) {
        ChFollowupRule existing = ruleMapper.selectById(bo.getId());
        if (ObjectUtil.isNull(existing)) {
            throw new ServiceException("随访排期规则不存在");
        }
        validateRule(bo);
        ChFollowupRule entity = MapstructUtils.convert(bo, ChFollowupRule.class);
        entity.setDiseaseCode(bo.getDiseaseCode().trim().toUpperCase(Locale.ROOT));
        entity.setRiskLevel(bo.getRiskLevel().trim().toUpperCase(Locale.ROOT));
        if (entity.getFirstDueDays() == null) {
            entity.setFirstDueDays(7);
        }
        if (StringUtils.isBlank(entity.getDefaultVisitType())) {
            entity.setDefaultVisitType("PHONE");
        }
        // 编辑改动了关键键位时需校验与修改自身的唯一性
        checkUniqueExcluding(bo.getDiseaseCode(), bo.getRiskLevel(), bo.getId());
        ruleMapper.updateById(entity);
        return null;
    }

    @Override
    public ChFollowupRuleVo queryById(Long id) {
        ChFollowupRuleVo vo = ruleMapper.selectVoById(id);
        if (vo != null) {
            fillDiseaseNames(Collections.singletonList(vo));
        }
        return vo;
    }

    @Override
    public TableDataInfo<ChFollowupRuleVo> queryPageList(ChFollowupRuleBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChFollowupRule> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getDiseaseCode()), ChFollowupRule::getDiseaseCode, bo.getDiseaseCode());
        lqw.eq(StringUtils.isNotBlank(bo.getRiskLevel()), ChFollowupRule::getRiskLevel, bo.getRiskLevel());
        lqw.eq(ObjectUtil.isNotNull(bo.getIsActive()), ChFollowupRule::getIsActive, bo.getIsActive());
        lqw.orderByAsc(ChFollowupRule::getDiseaseCode).orderByAsc(ChFollowupRule::getRiskLevel);
        Page<ChFollowupRuleVo> page = ruleMapper.selectVoPage(pageQuery.build(), lqw);
        fillDiseaseNames(page.getRecords());
        return TableDataInfo.build(page);
    }

    @Override
    public Void toggleActive(Long id, Boolean isActive) {
        ChFollowupRule entity = ruleMapper.selectById(id);
        if (ObjectUtil.isNull(entity)) {
            throw new ServiceException("随访排期规则不存在");
        }
        entity.setIsActive(isActive);
        ruleMapper.updateById(entity);
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void deleteRules(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        ruleMapper.deleteByIds(ids);
        return null;
    }

    /**
     * 校验字段合法性: 周期/轮次/面对面约束、风险等级枚举、随访方式枚举
     */
    private void validateRule(ChFollowupRuleBo bo) {
        if (bo.getCycleDays() == null || bo.getCycleDays() <= 0) {
            throw new ServiceException("随访周期必须大于0");
        }
        if (bo.getTotalRounds() == null || bo.getTotalRounds() <= 0) {
            throw new ServiceException("总轮次必须大于0");
        }
        String riskLevel = bo.getRiskLevel() == null ? "" : bo.getRiskLevel().trim().toUpperCase(Locale.ROOT);
        if (!RISK_LEVELS.contains(riskLevel)) {
            throw new ServiceException("风险等级非法,仅支持 LOW/MEDIUM/HIGH/VERY_HIGH/ANY");
        }
        String visitType = bo.getDefaultVisitType() == null ? "" : bo.getDefaultVisitType().trim().toUpperCase(Locale.ROOT);
        if (StringUtils.isNotBlank(visitType) && !VISIT_TYPES.contains(visitType)) {
            throw new ServiceException("随访方式非法,仅支持 PHONE/ONLINE/OFFLINE/VIDEO");
        }
    }

    /**
     * 新增时校验(病种,等级)唯一
     */
    private void checkUnique(String diseaseCode, String riskLevel) {
        Long existed = ruleMapper.selectCount(
            Wrappers.<ChFollowupRule>lambdaQuery()
                .eq(ChFollowupRule::getDiseaseCode, normalizeCode(diseaseCode))
                .eq(ChFollowupRule::getRiskLevel, normalizeLevel(riskLevel))
        );
        if (existed != null && existed > 0) {
            throw new ServiceException("同一病种与风险等级已存在规则,请编辑或停用原规则");
        }
    }

    /**
     * 编辑时校验(病种,等级)唯一,排除当前规则自身
     */
    private void checkUniqueExcluding(String diseaseCode, String riskLevel, Long id) {
        Long existed = ruleMapper.selectCount(
            Wrappers.<ChFollowupRule>lambdaQuery()
                .eq(ChFollowupRule::getDiseaseCode, normalizeCode(diseaseCode))
                .eq(ChFollowupRule::getRiskLevel, normalizeLevel(riskLevel))
                .ne(ChFollowupRule::getId, id)
        );
        if (existed != null && existed > 0) {
            throw new ServiceException("同一病种与风险等级已存在规则,请编辑或停用原规则");
        }
    }

    private String normalizeCode(String code) {
        return code == null ? "GENERAL" : code.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeLevel(String level) {
        return level == null ? "ANY" : level.trim().toUpperCase(Locale.ROOT);
    }

    private void fillDiseaseNames(java.util.List<ChFollowupRuleVo> list) {
        if (cn.hutool.core.collection.CollUtil.isEmpty(list)) {
            return;
        }
        Map<String, String> diseaseNameMap = Collections.emptyMap();
        try {
            diseaseNameMap = diseaseNameHelper.batchGetDiseaseName(list.stream()
                .map(ChFollowupRuleVo::getDiseaseCode)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList()));
        } catch (Exception e) {
            log.debug("填充病种名称失败: {}", e.getMessage());
        }
        Map<String, String> names = diseaseNameMap;
        list.forEach(v -> v.setDiseaseName(names.get(v.getDiseaseCode())));
    }
}