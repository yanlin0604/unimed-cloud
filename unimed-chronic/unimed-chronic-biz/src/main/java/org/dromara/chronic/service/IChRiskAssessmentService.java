package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChAssessmentRuleBo;
import org.dromara.chronic.domain.bo.ChRiskAssessmentBo;
import org.dromara.chronic.domain.vo.ChAssessmentRuleVo;
import org.dromara.chronic.domain.vo.ChManageLevelRecordVo;
import org.dromara.chronic.domain.vo.ChRiskAssessmentVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 风险评估服务
 *
 * @author unimed
 */
public interface IChRiskAssessmentService {

    ChRiskAssessmentVo assess(ChRiskAssessmentBo bo);

    ChRiskAssessmentVo queryLatest(Long patientId);

    List<ChManageLevelRecordVo> queryHistory(Long patientId);

    TableDataInfo<ChAssessmentRuleVo> queryRulePage(ChAssessmentRuleBo bo, PageQuery pageQuery);

    ChAssessmentRuleVo queryRuleById(Long ruleId);

    Boolean createRule(ChAssessmentRuleBo bo);

    Boolean updateRule(ChAssessmentRuleBo bo);

    Boolean deleteRuleById(Long ruleId);
}
