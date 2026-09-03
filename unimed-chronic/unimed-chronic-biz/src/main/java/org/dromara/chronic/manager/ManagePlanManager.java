package org.dromara.chronic.manager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.entity.ChFollowupRecord;
import org.dromara.chronic.domain.entity.ChManagePlan;
import org.dromara.chronic.domain.vo.ChManagePlanVo;
import org.dromara.chronic.mapper.ChFollowupRecordMapper;
import org.dromara.chronic.mapper.ChManagePlanMapper;
import org.dromara.chronic.service.IChManagePlanService;
import org.dromara.common.core.exception.ServiceException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 慢病管理方案周期成效评估管理器
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ManagePlanManager {

    private final IChManagePlanService managePlanService;
    private final ChManagePlanMapper managePlanMapper;
    private final ChFollowupRecordMapper followupRecordMapper;

    /**
     * 周期成效自动评估模型
     *
     * @param planId 方案ID
     * @return 评估结果报告
     */
    public Map<String, Object> evaluatePlanEffectiveness(Long planId) {
        ChManagePlan plan = managePlanMapper.selectById(planId);
        if (plan == null) {
            throw new ServiceException("管理方案不存在");
        }

        Long patientId = plan.getPatientId();
        // 统计方案生效以来的随访完成数
        Long followupCount = followupRecordMapper.selectCount(
            Wrappers.<ChFollowupRecord>lambdaQuery()
                .eq(ChFollowupRecord::getPatientId, patientId)
                .ge(plan.getCreateTime() != null, ChFollowupRecord::getVisitDate, plan.getCreateTime())
        );

        Map<String, Object> eval = new HashMap<>();
        eval.put("planId", planId);
        eval.put("patientId", patientId);
        eval.put("diseaseCode", plan.getDiseaseCode());
        eval.put("planStatus", plan.getPlanStatus());
        eval.put("completedFollowups", followupCount);

        // 模拟/综合评估算法
        int score = 85;
        String level = "良好";
        String recommendation = "当前方案执行成效平稳，建议继续维持现有用药与生活方式干预。";

        if (followupCount == 0) {
            score = 60;
            level = "关注";
            recommendation = "该周期内暂无随访打卡记录，建议加强患者触达或转为 AI 智能语音外呼随访。";
        } else if (followupCount >= 3) {
            score = 92;
            level = "优秀";
            recommendation = "随访依从性极高，指标控制理想，可评估适当下调随访干预频次。";
        }

        eval.put("effectivenessScore", score);
        eval.put("effectivenessLevel", level);
        eval.put("recommendation", recommendation);
        eval.put("evaluatedAt", LocalDateTime.now());

        return eval;
    }
}
