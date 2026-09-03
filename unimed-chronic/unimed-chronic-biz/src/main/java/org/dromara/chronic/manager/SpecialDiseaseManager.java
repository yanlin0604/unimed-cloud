package org.dromara.chronic.manager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChTumorRecordBo;
import org.dromara.chronic.domain.entity.ChTumorRecord;
import org.dromara.chronic.domain.vo.ChTumorRecordVo;
import org.dromara.chronic.mapper.ChTumorRecordMapper;
import org.dromara.common.core.utils.MapstructUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * 专病精细化计算引擎与临床评估管理器
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpecialDiseaseManager {

    private final ChTumorRecordMapper tumorRecordMapper;

    /**
     * 基于 2021 CKD-EPI 肌酐公式计算估算肾小球滤过率 (eGFR) 及 CKD 分期
     *
     * @param scr   血肌酐值 (mg/dL)
     * @param age   患者年龄 (岁)
     * @param isFemale 是否为女性
     * @return 包含 egfr 数值与 ckdStage 分期描述的 Map
     */
    public Map<String, Object> calculateCkdStage(BigDecimal scr, Integer age, Boolean isFemale) {
        Map<String, Object> result = new HashMap<>();
        if (scr == null || age == null || scr.compareTo(BigDecimal.ZERO) <= 0 || age <= 0) {
            result.put("egfr", BigDecimal.ZERO);
            result.put("ckdStage", "UNKNOWN");
            result.put("description", "指标不完整");
            return result;
        }

        double scrVal = scr.doubleValue();
        int ageVal = age;
        boolean female = Boolean.TRUE.equals(isFemale);

        double kappa = female ? 0.7 : 0.9;
        double alpha = female ? -0.241 : -0.302;
        double genderMultiplier = female ? 1.012 : 1.0;

        double minRatio = Math.min(scrVal / kappa, 1.0);
        double maxRatio = Math.max(scrVal / kappa, 1.0);

        double egfr = 142.0 * Math.pow(minRatio, alpha) * Math.pow(maxRatio, -1.200)
            * Math.pow(0.9938, ageVal) * genderMultiplier;

        BigDecimal egfrRounded = BigDecimal.valueOf(egfr).setScale(1, RoundingMode.HALF_UP);
        result.put("egfr", egfrRounded);

        String stage;
        String desc;
        if (egfr >= 90.0) {
            stage = "G1";
            desc = "肾功能正常或升高 (eGFR >= 90)";
        } else if (egfr >= 60.0) {
            stage = "G2";
            desc = "肾功能轻度下降 (eGFR 60~89)";
        } else if (egfr >= 45.0) {
            stage = "G3a";
            desc = "肾功能轻中度下降 (eGFR 45~59)";
        } else if (egfr >= 30.0) {
            stage = "G3b";
            desc = "肾功能中重度下降 (eGFR 30~44)";
        } else if (egfr >= 15.0) {
            stage = "G4";
            desc = "肾功能重度下降 (eGFR 15~29)";
        } else {
            stage = "G5";
            desc = "终末期肾病/肾衰竭 (eGFR < 15)";
        }

        result.put("ckdStage", stage);
        result.put("description", desc);
        return result;
    }

    /**
     * 评估脑卒中神经功能 NIHSS 与 Barthel 自理能力评定量表
     */
    public Map<String, Object> evaluateStroke(Integer nihssScore, Integer barthelScore) {
        Map<String, Object> result = new HashMap<>();
        if (nihssScore != null) {
            String nihssLevel;
            if (nihssScore <= 4) {
                nihssLevel = "轻度卒中 (1-4分)";
            } else if (nihssScore <= 15) {
                nihssLevel = "中度卒中 (5-15分)";
            } else if (nihssScore <= 20) {
                nihssLevel = "中重度卒中 (16-20分)";
            } else {
                nihssLevel = "重度卒中 (21-42分)";
            }
            result.put("nihssScore", nihssScore);
            result.put("nihssLevel", nihssLevel);
        }

        if (barthelScore != null) {
            String barthelLevel;
            if (barthelScore >= 100) {
                barthelLevel = "无需依赖 (100分)";
            } else if (barthelScore >= 61) {
                barthelLevel = "轻度依赖 (61-99分)";
            } else if (barthelScore >= 41) {
                barthelLevel = "中度依赖 (41-60分)";
            } else {
                barthelLevel = "重度依赖 (<=40分)";
            }
            result.put("barthelScore", barthelScore);
            result.put("barthelLevel", barthelLevel);
        }
        return result;
    }

    /**
     * 慢阻肺 (COPD) CAT 症状评估与肺功能分级
     */
    public Map<String, Object> evaluateCopd(Integer catScore, BigDecimal fev1PredictedPercent) {
        Map<String, Object> result = new HashMap<>();
        if (catScore != null) {
            String impact;
            if (catScore < 10) {
                impact = "轻微影响 (<10分)";
            } else if (catScore <= 20) {
                impact = "中度影响 (10-20分)";
            } else if (catScore <= 30) {
                impact = "严重影响 (21-30分)";
            } else {
                impact = "极重度影响 (>30分)";
            }
            result.put("catScore", catScore);
            result.put("catImpact", impact);
        }

        if (fev1PredictedPercent != null) {
            double percent = fev1PredictedPercent.doubleValue();
            String goldGrade;
            if (percent >= 80.0) {
                goldGrade = "GOLD 1 (轻度: FEV1% >= 80%)";
            } else if (percent >= 50.0) {
                goldGrade = "GOLD 2 (中度: 50% <= FEV1% < 80%)";
            } else if (percent >= 30.0) {
                goldGrade = "GOLD 3 (重度: 30% <= FEV1% < 50%)";
            } else {
                goldGrade = "GOLD 4 (极重度: FEV1% < 30%)";
            }
            result.put("fev1Percent", fev1PredictedPercent);
            result.put("goldGrade", goldGrade);
        }
        return result;
    }

    /**
     * 保存或更新肿瘤专项档案
     */
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrUpdateTumorRecord(ChTumorRecordBo bo) {
        ChTumorRecord existing = tumorRecordMapper.selectOne(
            Wrappers.<ChTumorRecord>lambdaQuery()
                .eq(ChTumorRecord::getPatientId, bo.getPatientId())
                .last("limit 1")
        );
        ChTumorRecord entity = MapstructUtils.convert(bo, ChTumorRecord.class);
        if (existing != null) {
            entity.setId(existing.getId());
            tumorRecordMapper.updateById(entity);
            return existing.getId();
        } else {
            tumorRecordMapper.insert(entity);
            return entity.getId();
        }
    }

    /**
     * 查询患者肿瘤档案
     */
    public ChTumorRecordVo queryTumorByPatientId(Long patientId) {
        return tumorRecordMapper.selectVoOne(
            Wrappers.<ChTumorRecord>lambdaQuery().eq(ChTumorRecord::getPatientId, patientId).last("limit 1")
        );
    }
}
