package org.dromara.chronic.support.rule;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.support.rule.FollowupRuleEngine.FollowupPlanProposal;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 多病共患（多病共管）随访合并引擎
 * <p>
 * 遵循公卫多病共管规范：
 * 1. 周期取严：多病共患时，取随访频次最高（周期最短）的病种作为主随访基准周期。
 * 2. 节点融合：在季度节点融合各专病随访内容，避免患者被频繁打扰与医生重复随访。
 * 3. 任务防重：同患者在 7 天时间窗口内的任务进行智能合并。
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MultiDiseaseFollowupMerger {

    private final FollowupRuleEngine ruleEngine;
    private final ObjectMapper objectMapper;

    public record MergedProposal(
        boolean isMultiDisease,
        List<String> diseaseCodes,
        String primaryDiseaseCode,
        String mergedDiseaseCodesJson,
        String managementLevel,
        int cycleDays,
        int totalRounds,
        int firstDueDays,
        String defaultVisitType,
        Long questionnaireId,
        String summaryAdvice
    ) {}

    /**
     * 合并多病种方案建议
     *
     * @param diseaseCodes 患者确诊的所有病种编码列表
     * @param riskLevels   各病种对应风险等级 (diseaseCode -> riskLevel)
     * @return 综合合并方案
     */
    public MergedProposal mergeProposals(List<String> diseaseCodes, Map<String, String> riskLevels) {
        if (CollUtil.isEmpty(diseaseCodes)) {
            // 无确诊病种：病种保持为空，由引擎内置默认档排期（不存在「通用病种」这种编码）
            FollowupPlanProposal defaultProp = ruleEngine.generateProposal(null, "LOW");
            return toMergedProposal(false, List.of(), null, "[]", defaultProp);
        }

        if (diseaseCodes.size() == 1) {
            String singleDisease = diseaseCodes.get(0);
            String level = riskLevels != null ? riskLevels.getOrDefault(singleDisease, "LOW") : "LOW";
            FollowupPlanProposal proposal = ruleEngine.generateProposal(singleDisease, level);
            String json = toJson(List.of(singleDisease));
            return toMergedProposal(false, List.of(singleDisease), singleDisease, json, proposal);
        }

        // 多病共患：分别推导各专病建议，执行“取严合并”
        List<FollowupPlanProposal> proposals = new ArrayList<>();
        for (String disease : diseaseCodes) {
            String level = riskLevels != null ? riskLevels.getOrDefault(disease, "LOW") : "LOW";
            proposals.add(ruleEngine.generateProposal(disease, level));
        }

        // 1. 取最小周期天数 (最高频次)
        FollowupPlanProposal strictest = proposals.stream()
            .min(Comparator.comparingInt(FollowupPlanProposal::cycleDays))
            .orElse(proposals.get(0));

        // 2. 取最高管理等级
        String highestLevel = proposals.stream()
            .map(FollowupPlanProposal::managementLevel)
            .max(Comparator.comparingInt(this::levelRank))
            .orElse("LOW");

        // 3. 随访方式: 多病场景统一采用主病种(最高频)的默认随访方式
        String visitType = strictest.defaultVisitType();

        String jsonList = toJson(diseaseCodes);
        String mergedAdvice = String.format("多病共管协同随访(合并病种: %s):以%s病种高频方案(周期%d天,每年%d次)为主干排期,并在季度节点融合共管。",
            String.join("+", diseaseCodes), strictest.diseaseCode(), strictest.cycleDays(), strictest.totalRounds());

        return new MergedProposal(
            true,
            diseaseCodes,
            strictest.diseaseCode(),
            jsonList,
            highestLevel,
            strictest.cycleDays(),
            strictest.totalRounds(),
            strictest.firstDueDays(),
            visitType,
            strictest.questionnaireId(),
            mergedAdvice
        );
    }

    private MergedProposal toMergedProposal(boolean isMulti, List<String> codes, String primary, String json, FollowupPlanProposal p) {
        return new MergedProposal(
            isMulti,
            codes,
            primary,
            json,
            p.managementLevel(),
            p.cycleDays(),
            p.totalRounds(),
            p.firstDueDays(),
            p.defaultVisitType(),
            p.questionnaireId(),
            p.summaryAdvice()
        );
    }

    private int levelRank(String level) {
        if (level == null) return 0;
        return switch (level.toUpperCase(Locale.ROOT)) {
            case "VERY_HIGH" -> 4;
            case "HIGH" -> 3;
            case "MEDIUM" -> 2;
            case "LOW" -> 1;
            default -> 0;
        };
    }

    private String toJson(List<String> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }
}
