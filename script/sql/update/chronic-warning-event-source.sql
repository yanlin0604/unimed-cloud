-- 慢病预警事件来源与关联字段补充。
-- 执行前请确认目标数据库尚未添加这些字段；生产环境应由迁移工具按版本执行。
-- 本脚本用于已有数据库；全新数据库请直接使用 script/sql/unimed-chronic.sql。
ALTER TABLE `ch_warning_event`
    ADD COLUMN `event_source` varchar(20) NULL DEFAULT NULL COMMENT '事件来源(RULE/PLAN/SOS/SLA/MANUAL)' AFTER `rule_id`,
    ADD COLUMN `source_id` bigint NULL DEFAULT NULL COMMENT '来源业务ID' AFTER `event_source`,
    ADD COLUMN `metric_type` varchar(30) NULL DEFAULT NULL COMMENT '标准指标类型' AFTER `source_id`,
    ADD COLUMN `plan_id` bigint NULL DEFAULT NULL COMMENT '管理方案ID' AFTER `metric_type`,
    ADD COLUMN `org_id` bigint NULL DEFAULT NULL COMMENT '机构ID' AFTER `plan_id`;

UPDATE `ch_warning_event`
SET `event_source` = CASE
    WHEN `rule_id` = 0 OR `warning_value` LIKE '方案目标偏离%' THEN 'PLAN'
    WHEN `warning_value` LIKE '%SOS%' THEN 'SOS'
    WHEN `warning_value` = 'SLA_VIOLATION' THEN 'SLA'
    WHEN `rule_id` IS NOT NULL AND `rule_id` > 0 THEN 'RULE'
    ELSE 'MANUAL'
END
WHERE `event_source` IS NULL;

UPDATE `ch_warning_event`
SET `source_id` = `rule_id`
WHERE `event_source` = 'RULE' AND `source_id` IS NULL AND `rule_id` IS NOT NULL;

-- 历史 SLA 事件曾将 contract_id 临时写入 rule_id，迁移到独立来源字段后清理该兼容值。
UPDATE `ch_warning_event`
SET `source_id` = `rule_id`,
    `rule_id` = NULL
WHERE `event_source` = 'SLA'
  AND `source_id` IS NULL
  AND `rule_id` IS NOT NULL;

CREATE INDEX `idx_we_source_active`
    ON `ch_warning_event` (`tenant_id`, `patient_id`, `event_source`, `source_id`, `event_status`);

-- NEW 是新建预警的默认状态，补充字典后前端状态名称不会为空。
INSERT INTO `sys_dict_data`
    (`tenant_id`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `create_dept`, `create_by`, `create_time`, `remark`)
SELECT '000000', 1, '新预警', 'NEW', 'chronic_warning_event_status', '', '', 'Y', 103, 1, NOW(), '新建待处理预警'
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_dict_data`
    WHERE `dict_type` = 'chronic_warning_event_status' AND `dict_value` = 'NEW'
);
