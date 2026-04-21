package org.dromara.chronic.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChAssessmentRuleBo;
import org.dromara.chronic.domain.bo.ChRiskAssessmentBo;
import org.dromara.chronic.domain.entity.ChAssessmentRule;
import org.dromara.chronic.domain.entity.ChManageLevelRecord;
import org.dromara.chronic.domain.entity.ChRiskAssessment;
import org.dromara.chronic.domain.entity.ChRiskFactorItem;
import org.dromara.chronic.domain.vo.ChAssessmentRuleVo;
import org.dromara.chronic.domain.vo.ChManageLevelRecordVo;
import org.dromara.chronic.domain.vo.ChRiskAssessmentVo;
import org.dromara.chronic.mapper.ChAssessmentRuleMapper;
import org.dromara.chronic.mapper.ChManageLevelRecordMapper;
import org.dromara.chronic.mapper.ChRiskAssessmentMapper;
import org.dromara.chronic.mapper.ChRiskFactorItemMapper;
import org.dromara.chronic.service.IChRiskAssessmentService;
import org.dromara.chronic.support.rule.RiskRuleEngine;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 风险评估服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChRiskAssessmentServiceImpl implements IChRiskAssessmentService {

    public static final Map<String, String> CALLBACK_REGISTRY = new ConcurrentHashMap<>();

    private final ChRiskAssessmentMapper riskAssessmentMapper;
    private final ChRiskFactorItemMapper riskFactorItemMapper;
    private final ChAssessmentRuleMapper assessmentRuleMapper;
    private final ChManageLevelRecordMapper manageLevelRecordMapper;
    private final RiskRuleEngine riskRuleEngine;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChRiskAssessmentVo assess(ChRiskAssessmentBo bo) {
        Dict metricData = ObjectUtil.defaultIfNull(JsonUtils.parseMap(bo.getMetricData()), Dict.create());
        Dict factorData = ObjectUtil.defaultIfNull(JsonUtils.parseMap(bo.getFactorData()), Dict.create());
        List<ChAssessmentRule> rules = assessmentRuleMapper.selectList(
            Wrappers.<ChAssessmentRule>lambdaQuery()
                .eq(ChAssessmentRule::getDiseaseCode, bo.getDiseaseCode())
                .eq(ChAssessmentRule::getIsActive, Boolean.TRUE)
        );
        RiskRuleEngine.Result result = riskRuleEngine.evaluate(rules, metricData, factorData);
        ChRiskAssessment entity = MapstructUtils.convert(bo, ChRiskAssessment.class);
        entity.setRiskLevel(result.riskLevel());
        entity.setAssessmentReport(buildReport(result, metricData, factorData));
        riskAssessmentMapper.insert(entity);

        List<ChRiskFactorItem> factorItems = result.factorItems();
        factorItems.forEach(item -> item.setAssessmentId(entity.getAssessmentId()));
        if (!factorItems.isEmpty()) {
            riskFactorItemMapper.insertBatch(factorItems);
        }

        ChRiskAssessment latestBefore = riskAssessmentMapper.selectOne(
            Wrappers.<ChRiskAssessment>lambdaQuery()
                .eq(ChRiskAssessment::getPatientId, bo.getPatientId())
                .eq(ChRiskAssessment::getDiseaseCode, bo.getDiseaseCode())
                .orderByDesc(ChRiskAssessment::getCreateTime)
                .last("limit 2")
        );
        String oldLevel = latestBefore == null || latestBefore.getAssessmentId().equals(entity.getAssessmentId()) ? null : latestBefore.getRiskLevel();
        ChManageLevelRecord levelRecord = new ChManageLevelRecord();
        levelRecord.setPatientId(bo.getPatientId());
        levelRecord.setDiseaseCode(bo.getDiseaseCode());
        levelRecord.setOldLevel(oldLevel);
        levelRecord.setNewLevel(result.riskLevel());
        levelRecord.setChangeReason("风险评估触发管理级别更新");
        levelRecord.setChangeTime(new Date());
        manageLevelRecordMapper.insert(levelRecord);

        ChRiskAssessmentVo vo = MapstructUtils.convert(entity, ChRiskAssessmentVo.class);
        vo.setFactorItems(MapstructUtils.convert(factorItems, org.dromara.chronic.domain.vo.ChRiskFactorItemVo.class));
        return vo;
    }

    @Override
    public ChRiskAssessmentVo queryLatest(Long patientId) {
        ChRiskAssessment entity = riskAssessmentMapper.selectOne(
            Wrappers.<ChRiskAssessment>lambdaQuery()
                .eq(ChRiskAssessment::getPatientId, patientId)
                .orderByDesc(ChRiskAssessment::getCreateTime)
                .last("limit 1")
        );
        if (entity == null) {
            return null;
        }
        ChRiskAssessmentVo vo = MapstructUtils.convert(entity, ChRiskAssessmentVo.class);
        vo.setFactorItems(riskFactorItemMapper.selectVoList(
            Wrappers.<ChRiskFactorItem>lambdaQuery().eq(ChRiskFactorItem::getAssessmentId, entity.getAssessmentId())
        ));
        return vo;
    }

    @Override
    public List<ChManageLevelRecordVo> queryHistory(Long patientId) {
        return manageLevelRecordMapper.selectVoList(
            Wrappers.<ChManageLevelRecord>lambdaQuery()
                .eq(ChManageLevelRecord::getPatientId, patientId)
                .orderByDesc(ChManageLevelRecord::getChangeTime)
        );
    }

    @Override
    public TableDataInfo<ChAssessmentRuleVo> queryRulePage(ChAssessmentRuleBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChAssessmentRule> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getDiseaseCode()), ChAssessmentRule::getDiseaseCode, bo.getDiseaseCode());
        lqw.like(StringUtils.isNotBlank(bo.getDimensionName()), ChAssessmentRule::getDimensionName, bo.getDimensionName());
        lqw.eq(ObjectUtil.isNotNull(bo.getIsActive()), ChAssessmentRule::getIsActive, bo.getIsActive());
        lqw.orderByAsc(ChAssessmentRule::getRuleId);
        Page<ChAssessmentRuleVo> page = assessmentRuleMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public Boolean createRule(ChAssessmentRuleBo bo) {
        ChAssessmentRule entity = MapstructUtils.convert(bo, ChAssessmentRule.class);
        if (entity.getIsActive() == null) {
            entity.setIsActive(Boolean.TRUE);
        }
        return assessmentRuleMapper.insert(entity) > 0;
    }

    private String buildReport(RiskRuleEngine.Result result, Dict metricData, Dict factorData) {
        return JsonUtils.toJsonString(Map.of(
            "riskLevel", result.riskLevel(),
            "totalScore", result.totalScore(),
            "metricData", metricData,
            "factorData", factorData
        ));
    }
}
