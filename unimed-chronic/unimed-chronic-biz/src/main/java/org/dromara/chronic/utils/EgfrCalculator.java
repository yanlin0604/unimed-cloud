package org.dromara.chronic.utils;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * eGFR（估算肾小球滤过率）计算工具类
 * <p>
 * 采用 CKD-EPI 2021 race-free 公式（KDIGO 2024 推荐）：
 * <pre>
 * eGFR = 142 × min(Scr/κ, 1)^α × max(Scr/κ, 1)^-1.200 × 0.9938^age × (1.012 if female)
 * 其中：
 *   Scr —— 血清肌酐 (mg/dL)
 *   κ   —— 0.7（女）/ 0.9（男）
 *   α   —— -0.241（女）/ -0.302（男）
 * </pre>
 *
 * @author unimed
 */
@Slf4j
public final class EgfrCalculator {

    /** μmol/L → mg/dL 换算系数（μmol/L ÷ 88.4 = mg/dL） */
    private static final BigDecimal UMOL_TO_MGDL = new BigDecimal("88.4");

    private EgfrCalculator() {
    }

    /**
     * 计算 eGFR（mL/min/1.73m²）
     *
     * @param creatinineMgDl 血清肌酐（mg/dL）
     * @param ageYears       年龄（岁）
     * @param isFemale       是否女性
     * @return eGFR 值（保留两位小数），输入非法返回 null
     */
    public static BigDecimal calculate(BigDecimal creatinineMgDl, Integer ageYears, Boolean isFemale) {
        if (creatinineMgDl == null || ageYears == null || isFemale == null) {
            return null;
        }
        if (creatinineMgDl.signum() <= 0 || ageYears <= 0) {
            log.warn("eGFR 入参非法: creatinine={}, age={}", creatinineMgDl, ageYears);
            return null;
        }
        double scr = creatinineMgDl.doubleValue();
        double kappa = isFemale ? 0.7 : 0.9;
        double alpha = isFemale ? -0.241 : -0.302;
        double ratio = scr / kappa;
        double minTerm = Math.pow(Math.min(ratio, 1.0), alpha);
        double maxTerm = Math.pow(Math.max(ratio, 1.0), -1.200);
        double ageTerm = Math.pow(0.9938, ageYears);
        double sexTerm = isFemale ? 1.012 : 1.0;
        double egfr = 142.0 * minTerm * maxTerm * ageTerm * sexTerm;
        return BigDecimal.valueOf(egfr).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 自动识别单位的 eGFR 计算入口
     *
     * @param creatinine 血清肌酐数值
     * @param unit       单位：MG_DL（默认）/ UMOL_L
     * @param ageYears   年龄
     * @param isFemale   是否女性
     * @return eGFR 值
     */
    public static BigDecimal calculate(BigDecimal creatinine, String unit, Integer ageYears, Boolean isFemale) {
        if (creatinine == null) return null;
        BigDecimal mgdl = "UMOL_L".equalsIgnoreCase(unit)
            ? creatinine.divide(UMOL_TO_MGDL, 6, RoundingMode.HALF_UP)
            : creatinine;
        return calculate(mgdl, ageYears, isFemale);
    }

    /**
     * 根据检验项编码判断是否为血清肌酐项
     *
     * @param itemCode 检验项编码
     * @return true 表示该项是肌酐项
     */
    public static boolean isCreatinineItem(String itemCode) {
        if (itemCode == null) return false;
        String upper = itemCode.trim().toUpperCase();
        return upper.equals("SCR") || upper.equals("CRE") || upper.equals("CR")
            || upper.equals("CREATININE") || upper.contains("肌酐");
    }
}
