-- ----------------------------
-- 为 ch_archive_share_apply 表添加 workflow_instance_id 字段
-- Date: 2026-05-18
-- ----------------------------

ALTER TABLE `ch_archive_share_apply`
ADD COLUMN `workflow_instance_id` bigint NULL DEFAULT NULL COMMENT '工作流实例ID（启动审批流程后回填）' AFTER `approval_status`;
