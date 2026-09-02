-- ============================================================================
-- 慢病随访：ch_followup_rule.total_rounds 收敛为 1（逐轮生成模型）
-- 日期：2026-08-31
--
-- 背景：
--   随访任务已改为「只生成首轮、每轮完成后由医生填写『下次随访日期』决定是否续轮」。
--   原种子数据 total_rounds = 4 / 6 / 12 是旧「一次性预生成全轮次」模型的产物：
--   它既不再驱动任何生成（引擎与建计划路径均已收敛到 ensureRound(plan, 1, ...)），
--   又会在管理端/患者端被当成"年度随访总轮数"展示，造成配置与结果不一致。
--
-- 决策：
--   规则表 total_rounds 统一固定为 1，语义为「规则生成轮次 = 仅首轮」。
--   配套代码变更：
--     - FollowupRuleEngine 内置兜底 switch 的 totalRounds 由 4/6/12 改为常量 1；
--     - ChFollowupRuleServiceImpl.validateRule 不再要求前端传该值，统一置 1；
--     - 管理端规则表单/列表移除 totalRounds 配置项（配了也不生效即为隐藏机制）；
--     - ChFollowupServiceImpl 摘除 total_rounds 的三处硬作用：
--         updatePlan 的「总轮次不能小于当前已完成轮次」校验、
--         syncUnfinishedTasks 的 taskRound > totalRounds 取消任务、
--         syncUnfinishedTasks 按 cycle_days 公式覆写医生所选到期日；
--     - 医生端移除「已达管理轮次上限」二次确认（上限=1 时每轮必弹，属噪音）。
--
-- 不迁移：
--   存量 ch_followup_plan.total_rounds 保持原值（老计划老办法）。前端展示已加护栏：
--   total_rounds > 1 才显示「已完成 N / M 轮」，否则显示「已完成 N 轮」，
--   避免历史计划与新计划在同一列表里口径互相失真。
--
-- 幂等：带 total_rounds <> 1 守卫，可重复执行。
-- ============================================================================

-- ---------------------------------------------------------------------------
-- 1. 规则轮次收敛为 1
-- ---------------------------------------------------------------------------
UPDATE `ch_followup_rule`
SET `total_rounds` = 1
WHERE `del_flag` = '0'
  AND `total_rounds` <> 1;

-- ---------------------------------------------------------------------------
-- 2. 校验：以下查询都应返回 0 行
-- ---------------------------------------------------------------------------
-- 2.1 不应再有轮次大于 1 的启用规则
SELECT `id`, `disease_code`, `risk_level`, `total_rounds`
FROM `ch_followup_rule`
WHERE `del_flag` = '0'
  AND `total_rounds` <> 1;

-- 2.2 不应再残留已废弃的「面对面」措辞（由 chronic-followup-per-round-rule-wording.sql 清理）
SELECT `id`, `disease_code`, `risk_level`, `summary_advice`
FROM `ch_followup_rule`
WHERE `del_flag` = '0'
  AND `summary_advice` LIKE '%面对面%';

-- ---------------------------------------------------------------------------
-- 3. 结果概览（人工核对用）：应按建议间隔分档、轮次恒为 1
-- ---------------------------------------------------------------------------
-- SELECT cycle_days, total_rounds, COUNT(*) AS rules
-- FROM ch_followup_rule WHERE del_flag='0'
-- GROUP BY cycle_days, total_rounds ORDER BY cycle_days;
