-- ============================================================
-- 调档申请表新增工作流实例ID字段
-- 用于关联工作流审批流程，支持回调反查
-- ============================================================

ALTER TABLE `ch_archive_share_apply`
    ADD COLUMN `workflow_instance_id` BIGINT DEFAULT NULL COMMENT '工作流实例ID' AFTER `approval_status`;
