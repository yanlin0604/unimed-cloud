-- ============================================================================
-- 慢病随访：补齐缺失病种主数据 STROKE(脑卒中) / TUMOR(恶性肿瘤)
-- 日期：2026-09-02
-- 执行库：unimed-chronic（第 1~2 节）+ chronic-system（第 3 节，字典）
--
-- 背景：
--   ch_followup_rule 与 ch_followup_questionnaire 已在使用 STROKE / TUMOR 两个病种码，
--   但 ch_disease_config 里没有对应行：
--     STROKE -> 规则 id 14/15/16/17（HIGH/VERY_HIGH/MEDIUM/LOW）+ 问卷 id 7
--     TUMOR  -> 规则 id 22（ANY）+ 问卷 id 9
--   病种中文名唯一来源是 DiseaseNameHelper.batchGetDiseaseName()，它只查 ch_disease_config。
--   查不到时 diseaseName=null，各端 VO 的 diseaseName 全部退化为裸显英文码，影响面是
--   14 处调用点：随访规则、随访计划/任务、问卷、派单池、管理计划、风险评估、预警规则、
--   诊疗记录、患者档案、随访统计。
--   实际故障不止显示：管理端问卷页病种下拉 ensureDiseaseOptions() 同样来自 ch_disease_config，
--   因此 STROKE/TUMOR 的问卷此前既筛不出也选不到。
--
-- 说明：
--   1) GENERAL 不是病种，是 FollowupRuleEngine.matchRule() 四级回退链的最后一级
--      （(病种,等级) -> (病种,ANY) -> (GENERAL,等级) -> (GENERAL,ANY)），
--      故【不写入】ch_disease_config，否则它会出现在「患者确诊病种」下拉里被当成诊断选择，
--      并在按病种统计中多出一个假病种。其展示名由后端伪病种映射处理，不在本脚本范围。
--   2) STROKE 行在 script/sql/mock/chronic-mock-data.sql:150 的种子中本已存在，
--      dev 库缺失，属漏同步；TUMOR 行种子脚本从未提供，属漏配。
--   3) monitor_items 仅用于病种配置页展示与存储，无后端逻辑解析，
--      指标命名沿用库内存量行的风格（SBP/DBP/HR/WEIGHT/TEMP），不引入新词表。
--   4) followup_template_id 存量 9 行均为 NULL，此处保持一致不臆造问卷绑定。
--   5) 幂等：ch_disease_config 走 uk_disease_code 唯一键 ON DUPLICATE KEY UPDATE
--      （含 del_flag/is_active 复位，可修复被软删的情况）；
--      ch_disease_relation 与 sys_dict_data 无相关唯一键，走 WHERE NOT EXISTS 守卫。
--   6) 不改任何业务数据行：已生成的计划/任务/规则均不受影响，仅名称解析结果变化。
-- ============================================================================

-- ---------------------------------------------------------------------------
-- 1. 病种主数据：STROKE / TUMOR
-- ---------------------------------------------------------------------------
INSERT INTO `ch_disease_config`
  (`disease_code`, `disease_name`, `disease_category`, `is_primary`, `parent_disease_code`,
   `monitor_items`, `is_active`, `create_dept`, `org_id`, `tenant_id`, `create_by`, `create_time`, `del_flag`)
VALUES
  ('STROKE', '脑卒中', 'PRIMARY', 1, NULL,
   '{"metrics":["SBP","DBP","HR"],"frequency":"DAILY"}', 1, 103, 1001, '000000', 1, NOW(), '0'),
  ('TUMOR', '恶性肿瘤', 'PRIMARY', 1, NULL,
   '{"metrics":["WEIGHT","TEMP"],"frequency":"MONTHLY"}', 1, 103, 1001, '000000', 1, NOW(), '0')
ON DUPLICATE KEY UPDATE
  `disease_name`     = VALUES(`disease_name`),
  `disease_category` = VALUES(`disease_category`),
  `is_primary`       = VALUES(`is_primary`),
  `is_active`        = VALUES(`is_active`),
  `del_flag`         = VALUES(`del_flag`),
  `update_time`      = NOW();

-- ---------------------------------------------------------------------------
-- 2. 疾病关联关系：HTN -> STROKE（并发症）
--    与 script/sql/mock/chronic-mock-data.sql:155 的种子对齐，dev 库此前缺失
-- ---------------------------------------------------------------------------
INSERT INTO `ch_disease_relation`
  (`parent_disease_code`, `complication_disease_code`, `relation_type`, `is_active`,
   `create_dept`, `tenant_id`, `create_by`, `create_time`, `del_flag`)
SELECT 'HTN', 'STROKE', 'COMPLICATION', 1, 103, '000000', 1, NOW(), '0'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `ch_disease_relation`
  WHERE `parent_disease_code` = 'HTN'
    AND `complication_disease_code` = 'STROKE'
    AND `relation_type` = 'COMPLICATION'
);

-- ---------------------------------------------------------------------------
-- 3. 字典兜底：chronic-system.sys_dict_data / chronic_disease_type
--    该字典是病种接口不可用时前端 DEFAULT_DISEASE_PRESETS 之外的第二数据源，
--    补齐后与 ch_disease_config 口径一致（dict_code 非自增，需显式给值）
-- ---------------------------------------------------------------------------
INSERT INTO `chronic-system`.`sys_dict_data`
  (`dict_code`, `tenant_id`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`,
   `is_default`, `create_dept`, `create_by`, `create_time`, `remark`)
SELECT t.`dict_code`, '000000', t.`dict_sort`, t.`dict_label`, t.`dict_value`, 'chronic_disease_type',
       'N', NULL, NULL, NOW(), '慢病随访病种补齐'
FROM (
  SELECT 2047215758051680390 AS `dict_code`, 7 AS `dict_sort`, '脑卒中' AS `dict_label`, 'STROKE' AS `dict_value`
  UNION ALL
  SELECT 2047215758051680391, 8, '恶性肿瘤', 'TUMOR'
) t
WHERE NOT EXISTS (
  SELECT 1 FROM `chronic-system`.`sys_dict_data` d
  WHERE d.`dict_type` = 'chronic_disease_type'
    AND d.`dict_value` = t.`dict_value`
);

-- ---------------------------------------------------------------------------
-- 4. 校验
-- ---------------------------------------------------------------------------
-- 4.1 应返回 2 行：脑卒中 / 恶性肿瘤
SELECT `config_id`, `disease_code`, `disease_name`, `disease_category`, `is_primary`, `is_active`, `del_flag`
FROM `ch_disease_config`
WHERE `disease_code` IN ('STROKE', 'TUMOR');

-- 4.2 业务表中不应再有「未配置病种名」的码（GENERAL 为预期保留项，见说明 1）
SELECT r.`disease_code`, COUNT(*) AS `cnt`, IFNULL(dc.`disease_name`, '未配置') AS `disease_name`
FROM `ch_followup_rule` r
LEFT JOIN `ch_disease_config` dc ON dc.`disease_code` = r.`disease_code` AND dc.`del_flag` = '0'
WHERE r.`del_flag` = '0'
GROUP BY r.`disease_code`, dc.`disease_name`
HAVING `disease_name` = '未配置';

-- 4.3 问卷病种名应全部可解析
SELECT q.`questionnaire_id`, q.`disease_code`, IFNULL(dc.`disease_name`, '未配置') AS `disease_name`
FROM `ch_followup_questionnaire` q
LEFT JOIN `ch_disease_config` dc ON dc.`disease_code` = q.`disease_code` AND dc.`del_flag` = '0'
WHERE q.`del_flag` = '0'
ORDER BY q.`disease_code`;

-- 4.4 字典应返回 8 行（原 6 + 新 2）
SELECT `dict_value`, `dict_label`, `dict_sort`
FROM `chronic-system`.`sys_dict_data`
WHERE `dict_type` = 'chronic_disease_type'
ORDER BY `dict_sort`;
