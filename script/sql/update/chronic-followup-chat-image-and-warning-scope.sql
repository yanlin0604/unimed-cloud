-- ============================================================================
-- 慢病随访：医患对话图片消息 + 预警联动范围收敛
-- 日期：2026-08-26
--
-- 说明：
-- 1) ch_message_content 无需 DDL 变更 —— content_type varchar(10) 已能承载 'IMAGE'，
--    file_id bigint 用于存放 OSS 文件ID；字典 chronic_content_type 的 IMAGE 项已随
--    chronic-dict-data.sql 落库，本脚本不重复插入。
-- 2) 本脚本仅补授权：慢病医生角色缺少文件上传权限，导致医生端发图/OCR 上传 403。
-- ============================================================================

-- ---------------------------------------------------------------------------
-- 1. 慢病医生角色(role_id=100)补文件上传/下载权限
--    menu_id 1601 = system:oss:upload，1602 = system:oss:download（见 sys_menu.sql:105-106）
--    原 39 条授权全为 chronic:doctor:*，无任何 system:* —— 医生端调
--    /resource/oss/upload 必然 403（顺带修复医生端既有 OCR 上传功能）。
--    患者端不受影响：ChPatientAccountServiceImpl 登录时已硬编码注入这两个权限。
-- ---------------------------------------------------------------------------
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 100, 1601 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 100 AND `menu_id` = 1601);

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 100, 1602 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 100 AND `menu_id` = 1602);

-- 校验：应返回 2 行
-- SELECT rm.role_id, rm.menu_id, m.perms FROM sys_role_menu rm
--   JOIN sys_menu m ON m.menu_id = rm.menu_id
--  WHERE rm.role_id = 100 AND rm.menu_id IN (1601, 1602);


-- ---------------------------------------------------------------------------
-- 2.【可选，需人工确认后执行】清理历史误生成的紧急干预随访任务
--
--    背景：WarningManager 此前对"随访现场由医生当面测量"的体征（measure_scene='FOLLOWUP'）
--    也会联动派发 EMERGENCY 电话干预任务，且无幂等 —— 同一个未处理预警每提交一次随访就多
--    一条待办。代码侧已修复（来源闸门 + 未完结去重 + task_round 置空）。
--
--    以下语句将历史遗留的、仍未完结的紧急任务置为已取消。不删除记录以保留审计痕迹。
--    执行前请先跑 SELECT 确认影响范围。
-- ---------------------------------------------------------------------------
-- 影响范围预览：
-- SELECT t.task_id, t.patient_id, t.plan_id, t.task_round, t.visit_type,
--        t.task_status, t.plan_due_date, t.assignee_user_id, t.create_time
--   FROM ch_followup_task t
--  WHERE t.task_type = 'EMERGENCY'
--    AND t.task_status NOT IN ('DONE', 'CANCELLED')
--  ORDER BY t.patient_id, t.create_time;

-- UPDATE `ch_followup_task`
--    SET `task_status` = 'CANCELLED',
--        `update_time` = NOW(),
--        `remark`      = CONCAT(IFNULL(`remark`, ''), '[2026-08-26 预警联动范围修正，历史误派任务批量取消]')
--  WHERE `task_type` = 'EMERGENCY'
--    AND `task_status` NOT IN ('DONE', 'CANCELLED');

-- 同批遗留数据：task_round 恒为 1 会与计划内 round1 撞键，污染 FollowupTaskGenJob 的
-- planId+round 去重，并在医生端被显示成"第 1 轮"。置空以与新逻辑一致：
-- UPDATE `ch_followup_task` SET `task_round` = NULL
--  WHERE `task_type` IN ('EMERGENCY', 'DYNAMIC', 'REFERRAL_TRACK') AND `task_round` IS NOT NULL;
