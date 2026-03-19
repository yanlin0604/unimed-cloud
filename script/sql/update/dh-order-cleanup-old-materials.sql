-- 清理旧订单素材数据（fileType 为 IMAGE/AUDIO/VIDEO 的旧格式数据）
-- 执行时间: 2026-03-19
-- 说明: 新订单使用 AVATAR/VOICE/REFERENCE_VIDEO 类型

-- 1. 删除旧订单素材
DELETE FROM dh_order_material 
WHERE file_type IN ('IMAGE', 'AUDIO', 'VIDEO');

-- 2. 可选：删除测试订单（根据需要执行）
-- DELETE FROM dh_order WHERE order_no LIKE 'DH20260319%';
