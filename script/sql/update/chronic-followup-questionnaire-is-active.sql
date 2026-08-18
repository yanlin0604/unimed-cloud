-- ============================================================
-- 慢病模块：随访问卷启停字段补齐
-- 背景：实体 ChFollowupQuestionnaire 已映射 is_active，
--       但线上 ch_followup_questionnaire 表缺少该列，
--       导致问卷查询/启停接口 SQL 报错。
-- 执行库：unimed-chronic
-- ============================================================

ALTER TABLE `ch_followup_questionnaire`
    ADD COLUMN `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用(0停用 1启用)' AFTER `is_national_standard`;
