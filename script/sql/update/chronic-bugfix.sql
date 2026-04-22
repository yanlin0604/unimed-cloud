-- =============================================
-- chronic-bugfix: 慢病管理 bugfix 升级脚本
-- =============================================

-- P0-2: 随访任务去重 - 添加 (plan_id, task_round, patient_id) 唯一索引
ALTER TABLE `ch_followup_task`
    ADD UNIQUE KEY `uk_plan_round_patient` (`plan_id`, `task_round`, `patient_id`);
