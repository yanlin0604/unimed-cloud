-- ============================================================================
-- 慢病随访：移除 GENERAL「通用兜底」规则档（数据 + 引擎回退链）
-- 日期：2026-09-02
-- 执行库：unimed-chronic
--
-- 背景：
--   ch_followup_rule 曾有一条 disease_code='GENERAL' 的行（dev 库 id=24），配合
--   FollowupRuleEngine.matchRule() 的四级回退链最后两级使用：
--     (病种,等级) -> (病种,ANY) -> (GENERAL,等级) -> (GENERAL,ANY)
--   GENERAL 不是病种，不在 ch_disease_config 里，导致：
--     1) 管理端规则列表该列退化成裸显英文码（DiseaseNameHelper 查不到名）；
--     2) 「病种」这一唯一匹配维度上混入了一个伪病种，语义与其余 23 行不一致。
--   现取消该档：规则只按病种匹配，未配规则的病种由引擎内置 switch default 兜底。
--
-- 零行为回归论证（删除前后逐字段等价）：
--   被删行 id=24：cycle_days=90, total_rounds=1, first_due_days=7,
--                 default_visit_type='PHONE',
--                 summary_advice='通用慢病规范化随访管理:每3个月随访1次(每年4次)。'
--   内置 default 分支（FollowupRuleEngine#generateProposal）：
--                 cycleDays=90, totalRounds=1, firstDueDays=7,
--                 defaultVisitType='PHONE',
--                 advice='通用慢病规范化随访管理:每3个月随访1次(每年4次)。'
--   两者完全一致，且 resolveQuestionnaireId 在两条路径上都按「真实病种码」查询，
--   结果亦一致 -> 未配规则病种（如 ASTHMA、HTN_HEART、DM_NEPHRO、病种为空）推导结果不变。
--   (GENERAL,等级) 这一级在 dev 库从未有数据行，删除不影响任何现存匹配。
--
-- 代价（已知并接受）：
--   未配规则病种的通用默认从此只能改代码，运营侧不再有可配置的「通用档」入口。
--
-- 幂等：DELETE 天然可重复执行；附校验查询。
-- ============================================================================

-- ---------------------------------------------------------------------------
-- 1. 删除 GENERAL 伪病种规则行（含历史可能存在的 (GENERAL,等级) 行）
-- ---------------------------------------------------------------------------
DELETE FROM `ch_followup_rule`
WHERE UPPER(`disease_code`) = 'GENERAL';

-- ---------------------------------------------------------------------------
-- 2. 校验
-- ---------------------------------------------------------------------------
-- 2.1 应返回 0 行
SELECT `id`, `disease_code`, `risk_level`
FROM `ch_followup_rule`
WHERE UPPER(`disease_code`) = 'GENERAL';

-- 2.2 剩余规则应全部为 ch_disease_config 内的真实病种，且名称可解析
SELECT r.`disease_code`,
       IFNULL(dc.`disease_name`, '未配置') AS `disease_name`,
       COUNT(*) AS `rule_cnt`
FROM `ch_followup_rule` r
LEFT JOIN `ch_disease_config` dc
       ON dc.`disease_code` = r.`disease_code` AND dc.`del_flag` = '0'
WHERE r.`del_flag` = '0'
GROUP BY r.`disease_code`, dc.`disease_name`
ORDER BY r.`disease_code`;

-- 2.3 规则总数应为 23（原 24 减去 GENERAL）
SELECT COUNT(*) AS `rule_total` FROM `ch_followup_rule` WHERE `del_flag` = '0';
