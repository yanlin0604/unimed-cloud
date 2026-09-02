-- ============================================================================
-- 慢病随访：逐轮生成模型落地 —— 随访规则建议文案清理
-- 日期：2026-08-31
--
-- 背景：
--   随访任务生成从「建档时一次性预生成全部轮次」改为「仅生成首轮，每轮随访完成后
--   由医生填写『下次随访日期』决定是否生成下一轮」。ch_followup_rule.total_rounds
--   语义随之由「预生成轮次数」变为「管理目标轮次上限」。
--
-- 说明：
--   1) 无 DDL 变更：total_rounds / cycle_days / first_due_days 字段含义与类型不变，
--      仅其消费方式改变（后端 FollowupRoundTaskGenerator）。
--   2) 本脚本仅修正 ch_followup_rule.summary_advice 中残留的「面对面随访」旧措辞。
--      面对面随访机制此前已下线，全域 OFFLINE 统一称「线下随访」
--      （见 rule/data.ts、task/data.ts 的 VISIT_TYPE 文案），管理端统计页同步改名。
--   3) 不迁移任何存量计划与任务：历史 ACTIVE 计划已预生成的后续轮次任务保持原样，
--      「老计划老办法」，新模型只影响此后新建/新完成的轮次。
--   4) UPDATE 带 LIKE '%面对面%' 守卫，可重复执行（幂等）。
-- ============================================================================

-- ---------------------------------------------------------------------------
-- 1. 清理 4 条规则建议文案中的「面对面随访」旧措辞 → 「线下随访」
--    id=1 HTN/LOW、id=7 T2DM/MEDIUM、id=8 T2DM/LOW、id=9 COPD/ANY
-- ---------------------------------------------------------------------------
UPDATE `ch_followup_rule`
SET `summary_advice` = REPLACE(`summary_advice`, '面对面随访', '线下随访')
WHERE `id` IN (1, 7, 8, 9)
  AND `summary_advice` LIKE '%面对面%';

-- ---------------------------------------------------------------------------
-- 2. 校验：以下两条查询都应返回 0 行
-- ---------------------------------------------------------------------------
-- 2.1 全表不应再残留「面对面」措辞
SELECT `id`, `disease_code`, `risk_level`, `summary_advice`
FROM `ch_followup_rule`
WHERE `summary_advice` LIKE '%面对面%';

-- 2.2 目标 4 条规则文案应已更新为「线下随访」
SELECT `id`, `disease_code`, `risk_level`, `summary_advice`
FROM `ch_followup_rule`
WHERE `id` IN (1, 7, 8, 9)
  AND `summary_advice` NOT LIKE '%线下随访%';
