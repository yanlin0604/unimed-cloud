-- -------------------------------------------------------------
-- 慢病随访「患者自填待医生评估」闭环 + 基于任务的医患对话 增量脚本
-- 适用于 unimed-chronic 数据库
--
-- 背景: 原患者端自填复用了三端共用的 completeTask, 直接把任务置为 DONE,
--       跳过了医生评估环节, 导致医生端/管理端显示"已完成"而医生并未评估。
-- 本脚本:
--   1. ch_followup_task 增加患者自填内容/时间字段(PATIENT_FILLED 中间态承载)
--   2. ch_message_session 增加 task_id 维度支持 TASK_CHAT(基于任务的医患对话)
--   3. chronic_followup_task_status 字典增加 PATIENT_FILLED 枚举
-- -------------------------------------------------------------

-- 1. ch_followup_task: 患者自填摘录字段
ALTER TABLE `ch_followup_task`
  ADD COLUMN `patient_fill_content` json NULL COMMENT '患者自填内容(体征/问卷/小结, JSON)',
  ADD COLUMN `patient_fill_time` datetime NULL COMMENT '患者自填提交时间';

-- 2. ch_message_session: 支持基于随访任务的医患会话(TASK_CHAT)
ALTER TABLE `ch_message_session`
  ADD COLUMN `task_id` bigint NULL COMMENT '关联随访任务ID(TASK_CHAT 会话)';

-- 3. ch_message_session: TASK_CHAT 幂等查询索引 (patient + doctor + task + type)
ALTER TABLE `ch_message_session`
  ADD INDEX `idx_msg_session_task` (`patient_id`, `doctor_user_id`, `task_id`);

-- 4. 随访任务状态字典: 新增 PATIENT_FILLED(已自填待医生评估)
--    注意: sys_dict_data 位于 chronic-system 库; dict_code 为非自增主键, 需显式指定且不得与现有值冲突
INSERT INTO `chronic-system`.`sys_dict_data`
  (`dict_code`, `tenant_id`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `create_dept`, `create_by`, `create_time`, `remark`)
VALUES
  (1001, '000000', 6, '已自填待医生评估', 'PATIENT_FILLED', 'chronic_followup_task_status', 'processing', '', 'N', 103, 1, NOW(), '患者已完成自填, 等待医生评估完成');

-- -----------------------------------------------------------------------------
-- 已知问题(环境): ch_followup_task 的 ALTER 曾被一个应用侧长事务阻塞
--   (root@192.168.1.88, conn_id=2039, 持有该表 MDL SHARED_READ, 空闲事务 0 行修改)。
--   处理方式: 先 KILL 该连接或重启应用释放 MDL 后, 再单独执行下面这条:
--
-- ALTER TABLE `ch_followup_task`
--   ADD COLUMN `patient_fill_content` json NULL COMMENT '患者自填内容(体征/问卷/小结, JSON)',
--   ADD COLUMN `patient_fill_time` datetime NULL COMMENT '患者自填提交时间';
-- -----------------------------------------------------------------------------