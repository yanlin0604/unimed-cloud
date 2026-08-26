-- -------------------------------------------------------------
-- 慢病随访规则引擎与动态调整状态机 DDL 增量脚本
-- 适用于 unimed-chronic 数据库
-- -------------------------------------------------------------

-- 1. ch_followup_plan 增加管理等级与多病共管标记
ALTER TABLE `ch_followup_plan`
  ADD COLUMN `management_level` varchar(20) DEFAULT 'LOW' COMMENT '管理等级: LOW/MEDIUM/HIGH/VERY_HIGH',
  ADD COLUMN `is_multi_disease` tinyint(1) DEFAULT 0 COMMENT '是否多病共管: 0-否 1-是',
  ADD COLUMN `merged_disease_codes` varchar(255) DEFAULT NULL COMMENT '多病共管合并病种(JSON数组)';

-- 2. ch_followup_task 增加任务类型、面对面标记与失访归因原因
ALTER TABLE `ch_followup_task`
  ADD COLUMN `task_type` varchar(30) DEFAULT 'NORMAL' COMMENT '任务类型: NORMAL-常规/DYNAMIC-动态调整/REFERRAL_TRACK-转诊追踪/EMERGENCY-预警临时',
  ADD COLUMN `is_face_to_face` tinyint(1) DEFAULT 0 COMMENT '是否面对面随访: 0-否 1-是',
  ADD COLUMN `cancel_reason_code` varchar(30) DEFAULT NULL COMMENT '取消/失访原因: LOST/REFUSED/RELOCATED/DECEASED/OTHER',
  ADD COLUMN `cancel_reason_desc` varchar(500) DEFAULT NULL COMMENT '取消原因补充说明';

-- 3. ch_followup_record 增加控制不达标原因、不良反应描述与转诊建议标记
ALTER TABLE `ch_followup_record`
  ADD COLUMN `unsatisfied_reason` varchar(500) DEFAULT NULL COMMENT '控制不满意原因',
  ADD COLUMN `adr_description` varchar(500) DEFAULT NULL COMMENT '药物不良反应描述',
  ADD COLUMN `is_referral_suggested` tinyint(1) DEFAULT 0 COMMENT '是否建议转诊: 0-否 1-是';
