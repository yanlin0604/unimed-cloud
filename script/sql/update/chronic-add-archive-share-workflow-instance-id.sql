-- ============================================================
-- 慢病模块增量 DDL
-- ch_archive_share_apply 新增 workflow_instance_id 字段
-- 用途：与 ArchiveShareManager 启动工作流后回填的 processInstanceId 对齐
-- 适配版本：2.X，2026-05
-- ============================================================

-- ---------- ch_archive_share_apply.workflow_instance_id ----------
ALTER TABLE `ch_archive_share_apply`
    ADD COLUMN `workflow_instance_id` bigint NULL DEFAULT NULL COMMENT '工作流实例ID(启动审批流程后回填)' AFTER `approval_status`,
    ADD INDEX `idx_asa_workflow_instance_id`(`workflow_instance_id` ASC) USING BTREE;
