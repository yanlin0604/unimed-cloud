-- ============================================================
-- 慢病模块增量 DDL
-- 1. ch_warning_rule 新增 rule_name 字段（替代复用 description 显示）
-- 2. ch_medication_record 新增 compliance / prescription_basis / remark
-- 适配版本：2.X，2026-05
-- ============================================================

-- ---------- 1. ch_warning_rule.rule_name ----------
ALTER TABLE `ch_warning_rule`
    ADD COLUMN `rule_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '规则名称' AFTER `rule_id`;

-- 兼容已有数据：把 description 当作 rule_name 兜底回填一次
UPDATE `ch_warning_rule`
SET `rule_name` = `description`
WHERE `rule_name` IS NULL
  AND `description` IS NOT NULL;

-- ---------- 2. ch_medication_record 用药扩展字段 ----------
ALTER TABLE `ch_medication_record`
    ADD COLUMN `compliance` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用药依从性(GOOD/FAIR/POOR，字典 chronic_compliance_level)' AFTER `status`,
    ADD COLUMN `prescription_basis` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处方依据' AFTER `compliance`,
    ADD COLUMN `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用药备注' AFTER `prescription_basis`;
