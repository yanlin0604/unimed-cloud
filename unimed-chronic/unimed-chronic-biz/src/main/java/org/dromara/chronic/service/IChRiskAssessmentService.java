package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChAssessmentRuleBo;
import org.dromara.chronic.domain.bo.ChRiskAssessmentBo;
import org.dromara.chronic.domain.vo.ChAssessmentRuleVo;
import org.dromara.chronic.domain.vo.ChManageLevelRecordVo;
import org.dromara.chronic.domain.vo.ChRiskAssessmentVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 风险评估服务
 *
 * @author unimed
 */
public interface IChRiskAssessmentService {

    ChRiskAssessmentVo assess(ChRiskAssessmentBo bo);

    /**
     * 查询患者最新风险评估
     *
     * @param patientId   患者ID
     * @param diseaseCode 病种编码（可选，传入则只查该病种最新一条）
     */
    ChRiskAssessmentVo queryLatest(Long patientId, String diseaseCode);

    /**
     * 查询风险评估详情（含 factorItems 与解析后的快照字段）
     */
    ChRiskAssessmentVo queryDetail(Long assessmentId);

    /**
     * 批量删除风险评估（级联清理 ch_risk_factor_item）
     */
    Boolean deleteByIds(Collection<Long> assessmentIds);

    List<ChManageLevelRecordVo> queryHistory(Long patientId);

    TableDataInfo<ChRiskAssessmentVo> queryPageList(ChRiskAssessmentBo bo, PageQuery pageQuery);

    TableDataInfo<ChAssessmentRuleVo> queryRulePage(ChAssessmentRuleBo bo, PageQuery pageQuery);

    ChAssessmentRuleVo queryRuleById(Long ruleId);

    Boolean createRule(ChAssessmentRuleBo bo);

    Boolean updateRule(ChAssessmentRuleBo bo);

    Boolean deleteRuleById(Long ruleId);
}
