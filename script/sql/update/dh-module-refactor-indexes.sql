-- 口播模块重构优化 - 查询索引
-- 执行时间：2026-03-19
-- 说明：添加查询索引提升性能

-- 1. dh_order 表添加处理人查询索引
-- 用于运营人员查询"我的待处理订单"
ALTER TABLE `dh_order` 
ADD INDEX `idx_dh_order_assignee` (`tenant_id`, `assignee_name`, `status`);

-- 2. dh_material 表添加状态索引
-- 用于按状态筛选素材
ALTER TABLE `dh_material` 
ADD INDEX `idx_status` (`status`);

-- 3. dh_order 表添加创建时间索引（如果不存在）
-- 用于按时间范围查询订单
-- ALTER TABLE `dh_order` 
-- ADD INDEX `idx_create_time` (`create_time`);

-- 注意：
-- 1. 执行前请确认索引不存在，避免重复创建
-- 2. 大表添加索引可能需要较长时间，建议在低峰期执行
-- 3. 可使用 SHOW INDEX FROM table_name 查看现有索引
