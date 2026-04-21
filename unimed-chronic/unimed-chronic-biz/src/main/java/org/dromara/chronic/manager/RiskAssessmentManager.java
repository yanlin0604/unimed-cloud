package org.dromara.chronic.manager;

import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChRiskAssessmentBo;
import org.dromara.chronic.domain.vo.ChRiskAssessmentVo;
import org.dromara.chronic.service.IChRiskAssessmentService;
import org.springframework.stereotype.Service;

/**
 * 风险评估编排层
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class RiskAssessmentManager {

    private final IChRiskAssessmentService riskAssessmentService;

    public ChRiskAssessmentVo assess(ChRiskAssessmentBo bo) {
        return riskAssessmentService.assess(bo);
    }
}
