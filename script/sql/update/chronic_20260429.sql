-- 修复 ch_assessment_rule 表缺少 is_active 字段的问题
-- Date: 2026-04-29

ALTER TABLE `ch_assessment_rule` 
ADD COLUMN `is_active` TINYINT(1) DEFAULT 1 COMMENT '是否启用' AFTER `threshold_config`;
