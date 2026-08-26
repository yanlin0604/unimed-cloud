-- ============================================================
-- 慢病模块 SnailJob 定时任务注册
-- 执行库：SnailJob 服务端库（unimed-job.sql 初始化出的库）
-- 前置：SnailJob Server 已部署且 unimed-job.sql 已导入；慢病服务已注册到组 unimed_group
--
-- 任务清单（执行器均为 unimed-chronic 服务中的 @JobExecutor）
--   followupTaskGenJob   随访任务生成      每日 01:00
--   contractSlaCheckJob  签约履约SLA检查   每日 02:00
--   followupRemindJob    随访到期提醒/逾期  每日 08:00
--   statDailyJob         区域统计日报      每日 23:50
--
-- trigger_type=3 表示 CRON 表达式；trigger_type=2 表示固定时间间隔；job_status=1 开启；task_type=1 集群；route_key=4 一致性哈希
-- 幂等：按 job_name 先删后插
-- ============================================================

DELETE FROM `sj_job`
WHERE `group_name` = 'unimed_group'
  AND `job_name` IN ('慢病-随访任务生成', '慢病-签约SLA检查', '慢病-随访到期提醒', '慢病-统计日报');

INSERT INTO `sj_job` (`namespace_id`, `group_name`, `job_name`, `args_str`, `args_type`, `next_trigger_at`,
                      `job_status`, `task_type`, `route_key`, `executor_type`, `executor_info`, `trigger_type`,
                      `trigger_interval`, `block_strategy`, `executor_timeout`, `max_retry_times`, `parallel_num`,
                      `retry_interval`, `bucket_index`, `resident`, `notify_ids`, `owner_id`, `labels`,
                      `description`, `ext_attrs`, `deleted`, `create_dt`, `update_dt`)
VALUES
('dev', 'unimed_group', '慢病-随访任务生成', NULL, 1, UNIX_TIMESTAMP() * 1000, 1, 1, 4, 1,
 'followupTaskGenJob', 3, '0 0 1 * * ?', 1, 300, 3, 1, 60, 0, 0, '', 1, '', '按随访计划周期生成下一轮随访任务', '', 0, NOW(), NOW()),

('dev', 'unimed_group', '慢病-签约SLA检查', NULL, 1, UNIX_TIMESTAMP() * 1000, 1, 1, 4, 1,
 'contractSlaCheckJob', 3, '0 0 2 * * ?', 1, 300, 3, 1, 60, 0, 0, '', 1, '', '签约履约项超期检查与提醒', '', 0, NOW(), NOW()),

('dev', 'unimed_group', '慢病-随访到期提醒', NULL, 1, UNIX_TIMESTAMP() * 1000, 1, 1, 4, 1,
 'followupRemindJob', 3, '0 0 8 * * ?', 1, 300, 3, 1, 60, 0, 0, '', 1, '', '逾期任务置OVERDUE、临期任务置REMINDING并通知执行医生', '', 0, NOW(), NOW()),

('dev', 'unimed_group', '慢病-统计日报', NULL, 1, UNIX_TIMESTAMP() * 1000, 1, 1, 4, 1,
 'statDailyJob', 3, '0 50 23 * * ?', 1, 600, 3, 1, 60, 0, 0, '', 1, '', '区域/随访/预警统计日表汇总', '', 0, NOW(), NOW()),

('chronic-dev', 'unimed_group', '慢病-随访任务生成', NULL, 1, UNIX_TIMESTAMP() * 1000, 1, 1, 4, 1,
 'followupTaskGenJob', 3, '0 0 1 * * ?', 1, 300, 3, 1, 60, 0, 0, '', 1, '', '按随访计划周期生成下一轮随访任务', '', 0, NOW(), NOW()),

('chronic-dev', 'unimed_group', '慢病-签约SLA检查', NULL, 1, UNIX_TIMESTAMP() * 1000, 1, 1, 4, 1,
 'contractSlaCheckJob', 3, '0 0 2 * * ?', 1, 300, 3, 1, 60, 0, 0, '', 1, '', '签约履约项超期检查与提醒', '', 0, NOW(), NOW()),

('chronic-dev', 'unimed_group', '慢病-随访到期提醒', NULL, 1, UNIX_TIMESTAMP() * 1000, 1, 1, 4, 1,
 'followupRemindJob', 3, '0 0 8 * * ?', 1, 300, 3, 1, 60, 0, 0, '', 1, '', '逾期任务置OVERDUE、临期任务置REMINDING并通知执行医生', '', 0, NOW(), NOW()),

('chronic-dev', 'unimed_group', '慢病-统计日报', NULL, 1, UNIX_TIMESTAMP() * 1000, 1, 1, 4, 1,
 'statDailyJob', 3, '0 50 23 * * ?', 1, 600, 3, 1, 60, 0, 0, '', 1, '', '区域/随访/预警统计日表汇总', '', 0, NOW(), NOW());
