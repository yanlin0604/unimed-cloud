-- =====================================================================
-- 慢病模块 - 诊疗记录菜单权限码对齐
-- 作用：
--   1. 把 sys_menu 中 `clinical:encounter:*` 系列权限码统一对齐到后端
--      Controller 实际使用的 `chronic:encounter:*` 前缀（list/query/add/edit/remove）。
--   2. 新增"诊疗记录提交"子菜单（menu_id=20030105，perms=chronic:encounter:submit）。
-- 对应：
--   - EncounterController @SaCheckPermission("chronic:encounter:*")
--   - 新增端点：POST /chronic/admin/encounter/{id}/submit、DELETE /chronic/admin/encounter/{id}
-- 影响表：sys_menu
-- 幂等性：使用 REPLACE 仅修改前缀 + INSERT IGNORE，可重复执行
-- 创建时间：2026-05-18
-- =====================================================================

-- 1. 把所有 clinical:encounter:* 权限码替换为 chronic:encounter:*（list/query/add/edit/remove 一次性覆盖）
UPDATE `sys_menu`
   SET `perms` = REPLACE(`perms`, 'clinical:encounter:', 'chronic:encounter:')
 WHERE `perms` LIKE 'clinical:encounter:%';

-- 2. 新增"诊疗记录提交"功能权限（menu_id=20030105，parent=200301，order=5）
INSERT IGNORE INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (20030105, '诊疗记录提交', 200301, 5, '#', '', '', 1, 0, 'F', '0', '0', 'chronic:encounter:submit', '#', 103, 1, '2026-05-18 09:30:00', NULL, NULL, '');
