package org.dromara.chronic.support.rule;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.entity.ChFollowupQuestionnaire;
import org.dromara.chronic.domain.entity.ChFollowupRule;
import org.dromara.chronic.mapper.ChFollowupQuestionnaireMapper;
import org.dromara.chronic.mapper.ChFollowupRuleMapper;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * 慢病随访规则与路径推导引擎
 * <p>
 * 基于国家基本公共卫生服务规范（第三版）及各专病临床指南，
 * 依据病种编码与患者风险管理等级自动推导标准化随访方案（周期、轮次、首轮到期日、面对面要求及推荐问卷）。
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FollowupRuleEngine {

    private final ChFollowupQuestionnaireMapper questionnaireMapper;

    private final ChFollowupRuleMapper followupRuleMapper;

    /**
     * 随访路径建议方案
     */
    public record FollowupPlanProposal(
        String diseaseCode,
        String managementLevel,
        int cycleDays,
        int totalRounds,
        int firstDueDays,
        String defaultVisitType,
        int requireFaceToFaceRounds,
        Long questionnaireId,
        String summaryAdvice
    ) {}

    /**
     * 根据病种和风险管理等级推导随访方案
     *
     * @param diseaseCode 病种编码（如 HTN, T2DM, COPD, CHD, STROKE, CKD, TUMOR, DYSLIPID 等）
     * @param riskLevel   风险/管理等级（LOW, MEDIUM, HIGH, VERY_HIGH）
     * @return 随访建议方案
     */
    public FollowupPlanProposal generateProposal(String diseaseCode, String riskLevel) {
        String normalizedDisease = normalize(diseaseCode);
        String normalizedLevel = normalizeLevel(riskLevel);

        // 优先查库:四级回退链 (病种,等级) -> (病种,ANY) -> (GENERAL,等级) -> (GENERAL,ANY)
        ChFollowupRule rule = matchRule(normalizedDisease, normalizedLevel);
        if (rule != null) {
            Long questionnaireId = resolveQuestionnaireId(normalizedDisease);
            return new FollowupPlanProposal(
                normalizedDisease,
                normalizedLevel,
                intValue(rule.getCycleDays(), 90),
                intValue(rule.getTotalRounds(), 4),
                intValue(rule.getFirstDueDays(), 7),
                defaultIfBlank(rule.getDefaultVisitType(), "PHONE").toUpperCase(Locale.ROOT),
                intValue(rule.getRequireFaceToFaceRounds(), 2),
                questionnaireId,
                rule.getSummaryAdvice()
            );
        }

        int cycleDays;
        int totalRounds;
        int firstDueDays = 7; // 新建档/确诊默认建议 7 天内完成首诊随访与基线建立
        String defaultVisitType = "PHONE";
        int requireFaceToFaceRounds = 2; // 默认要求面对面 ≥2 次
        String advice;

        switch (normalizedDisease) {
            case "HTN" -> {
                // 高血压分级管理规范
                if ("HIGH".equals(normalizedLevel) || "VERY_HIGH".equals(normalizedLevel)) {
                    cycleDays = 30;
                    totalRounds = 12;
                    requireFaceToFaceRounds = 4;
                    advice = "高血压三级管理（高危/极高危）：至少每1个月随访1次，重点监测靶器官损害、血压达标情况及药物不良反应。";
                } else if ("MEDIUM".equals(normalizedLevel)) {
                    cycleDays = 60;
                    totalRounds = 6;
                    requireFaceToFaceRounds = 3;
                    advice = "高血压二级管理（中危）：至少每2个月随访1次，指导规律用药与生活方式干预。";
                } else {
                    cycleDays = 90;
                    totalRounds = 4;
                    requireFaceToFaceRounds = 2;
                    advice = "高血压一级管理（低危）：至少每3个月随访1次（每年≥4次），其中面对面随访不少于2次。";
                }
            }
            case "T2DM" -> {
                // 2型糖尿病分级管理规范
                if ("HIGH".equals(normalizedLevel) || "VERY_HIGH".equals(normalizedLevel)) {
                    cycleDays = 30;
                    totalRounds = 12;
                    requireFaceToFaceRounds = 4;
                    advice = "2型糖尿病强化管理（血糖不达标或伴并发症）：每1个月随访1次，监测空腹/餐后血糖及胰岛素用药反应。";
                } else {
                    cycleDays = 90;
                    totalRounds = 4;
                    requireFaceToFaceRounds = 2;
                    advice = "2型糖尿病常规管理（血糖达标且稳定）：每3个月随访1次（每年≥4次），其中面对面随访不少于2次。";
                }
            }
            case "COPD" -> {
                // 慢性阻塞性肺疾病管理规范
                cycleDays = 90;
                totalRounds = 4;
                requireFaceToFaceRounds = 2;
                advice = "慢阻肺患者管理：每年至少随访4次（其中面对面随访≥2次），评估CAT/mMRC呼吸困难分级与吸入剂依从性。";
            }
            case "CHD", "STROKE" -> {
                // 冠心病 / 脑卒中 二级预防管理
                if ("HIGH".equals(normalizedLevel) || "VERY_HIGH".equals(normalizedLevel)) {
                    cycleDays = 30;
                    totalRounds = 12;
                    requireFaceToFaceRounds = 4;
                    advice = "心脑血管重症/急性发作恢复期强化随访：每月随访1次，评估神经缺损/心绞痛发作与抗栓药物依从性。";
                } else {
                    cycleDays = 60;
                    totalRounds = 6;
                    requireFaceToFaceRounds = 3;
                    advice = "心脑血管常规二级预防管理：每2个月随访1次，维持血压血脂达标。";
                }
            }
            case "CKD" -> {
                // 慢性肾脏病
                if ("HIGH".equals(normalizedLevel) || "VERY_HIGH".equals(normalizedLevel)) {
                    cycleDays = 30;
                    totalRounds = 12;
                    requireFaceToFaceRounds = 4;
                    advice = "CKD 3~5期强化管理：每月随访1次，监测尿蛋白、肾功能与水肿情况。";
                } else {
                    cycleDays = 90;
                    totalRounds = 4;
                    requireFaceToFaceRounds = 2;
                    advice = "CKD 1~2期常规管理：每3个月随访1次，控制血压与低蛋白饮食指导。";
                }
            }
            case "TUMOR" -> {
                // 恶性肿瘤康复期
                cycleDays = 60;
                totalRounds = 6;
                requireFaceToFaceRounds = 2;
                advice = "肿瘤康复随访：每2个月随访1次，评估体能状态(ECOG)、癌痛评分与定期复查进度。";
            }
            default -> {
                // 通用慢病兜底规范
                cycleDays = 90;
                totalRounds = 4;
                requireFaceToFaceRounds = 2;
                advice = "通用慢病规范化随访管理：每3个月随访1次（每年4次）。";
            }
        }

        // 匹配该病种标准问卷
        Long questionnaireId = resolveQuestionnaireId(normalizedDisease);

        return new FollowupPlanProposal(
            normalizedDisease,
            normalizedLevel,
            cycleDays,
            totalRounds,
            firstDueDays,
            defaultVisitType,
            requireFaceToFaceRounds,
            questionnaireId,
            advice
        );
    }

    /**
     * 解析该病种激活的国家标准/默认问卷ID
     */
    public Long resolveQuestionnaireId(String diseaseCode) {
        if (StringUtils.isBlank(diseaseCode) || questionnaireMapper == null) {
            return null;
        }
        try {
            ChFollowupQuestionnaire q = questionnaireMapper.selectOne(
                Wrappers.<ChFollowupQuestionnaire>lambdaQuery()
                    .eq(ChFollowupQuestionnaire::getDiseaseCode, diseaseCode.toUpperCase(Locale.ROOT))
                    .eq(ChFollowupQuestionnaire::getIsActive, true)
                    .orderByDesc(ChFollowupQuestionnaire::getIsNationalStandard)
                    .orderByDesc(ChFollowupQuestionnaire::getVersion)
                    .last("limit 1")
            );
            return q != null ? q.getQuestionnaireId() : null;
        } catch (Exception e) {
            log.warn("推导随访问卷失败 diseaseCode={}, err={}", diseaseCode, e.getMessage());
            return null;
        }
    }

    private String normalize(String diseaseCode) {
        return diseaseCode == null ? "GENERAL" : diseaseCode.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeLevel(String riskLevel) {
        if (StringUtils.isBlank(riskLevel)) {
            return "LOW";
        }
        String upper = riskLevel.trim().toUpperCase(Locale.ROOT);
        if (Set.of("LOW", "MEDIUM", "HIGH", "VERY_HIGH").contains(upper)) {
            return upper;
        }
        return "LOW";
    }

    /**
     * 按四级回退链匹配启用的可配置规则:
     * 1. (病种, 等级) 精确行
     * 2. (病种, ANY) 通配行
     * 3. (GENERAL, 等级) 通用病种同名等级
     * 4. (GENERAL, ANY) 通用兜底
     * <p>
     * 未命中返回 null(由调用方走代码内置 switch 兜底,保证零行为回归)
     */
    private ChFollowupRule matchRule(String diseaseCode, String riskLevel) {
        if (followupRuleMapper == null) {
            return null;
        }
        try {
            List<ChFollowupRule> rows = followupRuleMapper.selectList(
                Wrappers.<ChFollowupRule>lambdaQuery()
                    .eq(ChFollowupRule::getIsActive, true)
            );
            if (rows == null || rows.isEmpty()) {
                return null;
            }
            // 同键可能命中多条(如 A 租户与 B 租户隔离后通常一条;若重复取最新一条)
            ChFollowupRule exactDiseaseLevel = null;
            ChFollowupRule diseaseAny = null;
            ChFollowupRule generalLevel = null;
            ChFollowupRule generalAny = null;
            for (ChFollowupRule r : rows) {
                // 防御性校验:仅采纳启用行(与查询条件 isActive=1 双保险)
                if (!Boolean.TRUE.equals(r.getIsActive())) {
                    continue;
                }
                boolean isGeneral = "GENERAL".equalsIgnoreCase(r.getDiseaseCode());
                boolean levelAny = "ANY".equalsIgnoreCase(r.getRiskLevel());
                boolean codeMatch = isGeneral || r.getDiseaseCode().equalsIgnoreCase(diseaseCode);
                if (!codeMatch) {
                    continue;
                }
                if (!isGeneral && !levelAny && r.getRiskLevel().equalsIgnoreCase(riskLevel)) {
                    exactDiseaseLevel = r;
                } else if (!isGeneral && levelAny) {
                    diseaseAny = r;
                } else if (isGeneral && !levelAny && r.getRiskLevel().equalsIgnoreCase(riskLevel)) {
                    generalLevel = r;
                } else if (isGeneral && levelAny) {
                    generalAny = r;
                }
            }
            if (exactDiseaseLevel != null) {
                return exactDiseaseLevel;
            }
            if (diseaseAny != null) {
                return diseaseAny;
            }
            if (generalLevel != null) {
                return generalLevel;
            }
            return generalAny;
        } catch (Exception e) {
            log.warn("查询随访排期规则失败,回退内置默认 diseaseCode={}, riskLevel={}, err={}",
                diseaseCode, riskLevel, e.getMessage());
            return null;
        }
    }

    private int intValue(Integer value, int defaultVal) {
        return (value == null || value <= 0) ? defaultVal : value;
    }

    private String defaultIfBlank(String value, String defaultVal) {
        return StringUtils.isBlank(value) ? defaultVal : value;
    }
}
