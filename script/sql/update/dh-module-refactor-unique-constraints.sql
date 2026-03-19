-- 口播模块重构优化 - 唯一约束
-- 执行时间：2026-03-19
-- 说明：添加唯一约束防止同一用户创建同名形象/音色

-- 1. dh_avatar 表添加唯一约束
-- 同一租户下，同一用户的形象名称不能重复
ALTER TABLE `dh_avatar` 
ADD UNIQUE KEY `uk_tenant_user_name` (`tenant_id`, `user_id`, `name`);

-- 2. dh_voice 表添加唯一约束
-- 同一租户下，同一用户的音色名称不能重复
ALTER TABLE `dh_voice` 
ADD UNIQUE KEY `uk_tenant_user_name` (`tenant_id`, `user_id`, `name`);

-- 注意：
-- 1. 执行前请确认数据中不存在违反约束的重复数据
-- 2. 如果存在重复数据，需要先清理或合并
-- 3. user_id 为 NULL 的系统预设资产不受此约束影响（MySQL 对 NULL 值不强制唯一性）
