package org.dromara.chronic.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChRiskAssessmentBo;
import org.dromara.chronic.domain.vo.ChRiskAssessmentVo;
import org.dromara.chronic.service.IChRiskAssessmentService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 风险评估编排层
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RiskAssessmentManager {

    private final IChRiskAssessmentService riskAssessmentService;

    /**
     * 同步执行风险评估，返回评估结果
     */
    public ChRiskAssessmentVo assess(ChRiskAssessmentBo bo) {
        return riskAssessmentService.assess(bo);
    }

    /**
     * 异步执行风险评估
     * <p>
     * 主要服务于 HIS 确诊同步等对外开放接口：在接口快速返回后，由后台线程完成
     * 风险评估、方案草案生成等耗时动作，确保外部系统不被阻塞。
     * <p>
     * 注意：异常在方法内部吃掉并记录 warn 日志，避免扩散影响异步线程池。
     */
    @Async("chronicAsyncExecutor")
    public void assessAsync(ChRiskAssessmentBo bo) {
        try {
            riskAssessmentService.assess(bo);
        } catch (Exception ex) {
            log.warn("[chronic] 异步风险评估失败 patientId={} diseaseCode={} msg={}",
                bo.getPatientId(), bo.getDiseaseCode(), ex.getMessage(), ex);
        }
    }
}
