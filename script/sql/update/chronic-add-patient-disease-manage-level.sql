-- =====================================================================
-- 慢病模块 - 患者病种表补字段
-- 作用：
--   1. ch_patient_disease 新增 manage_level（管理级别）字段
--      字典 chronic_manage_level，落位于病种粒度（不依赖 ch_manage_plan）
-- 创建时间：2026-05-18
-- =====================================================================

ALTER TABLE `ch_patient_disease`
    ADD COLUMN `manage_level` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '管理级别(字典 chronic_manage_level)' AFTER `parent_disease_code`;
