-- ============================================================
-- 慢病菜单增量：设备管理 / 通知模板 / 筛查批次列表与编辑
-- 执行库：unimed-cloud（系统库）
--
-- 背景：chronic-menu-import.sql 落库时 DeviceController / NotificationTemplateController
--       尚不存在，ScreeningBatchController 也还没有 page/update/status 端点，
--       因此这 3 组共 12 个权限码没有菜单承载 —— 非超管角色无法被授权，调用必 403。
--       后端补齐端点后由本脚本补上承载菜单。
--
-- 幂等：按精确 menu_id 先删后插（不使用 BETWEEN，避免误伤 workflow 菜单 11616~11806）
-- 回滚：单独执行下面两条 DELETE 即可移除本次新增菜单
-- ============================================================

DELETE FROM `sys_role_menu` WHERE menu_id IN (
  200306, 20030601, 20030602, 20030603,
  200805, 20080501, 20080502, 20080503, 20080504, 20080505,
  20060404, 20060405
);
DELETE FROM `sys_menu` WHERE menu_id IN (
  200306, 20030601, 20030602, 20030603,
  200805, 20080501, 20080502, 20080503, 20080504, 20080505,
  20060404, 20060405
);

-- ------------------------------------------------------------
-- 1. 设备管理（临床数据 2003 下，第 6 位）
--    后端：DeviceController  /chronic/admin/device/**
-- ------------------------------------------------------------
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200306, '设备管理', 2003, 6, 'device', 'clinical/device/index', '', 1, 0, 'C', '0', '0', 'chronic:device:list', 'mdi:devices', 103, 1, '2026-08-18 10:00:00', NULL, NULL, '物联设备绑定与原始上报数据');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (20030601, '详情', 200306, 1, '#', '', '', 1, 0, 'F', '0', '0', 'chronic:device:query', '#', 103, 1, '2026-08-18 10:00:00', NULL, NULL, '设备详情与原始数据查询');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (20030602, '绑定', 200306, 2, '#', '', '', 1, 0, 'F', '0', '0', 'chronic:device:add', '#', 103, 1, '2026-08-18 10:00:00', NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (20030603, '解绑', 200306, 3, '#', '', '', 1, 0, 'F', '0', '0', 'chronic:device:remove', '#', 103, 1, '2026-08-18 10:00:00', NULL, NULL, '');

-- ------------------------------------------------------------
-- 2. 通知模板（系统管理 1 下，第 16 位，紧随 200801~200804 慢病字典）
--    后端：NotificationTemplateController  /chronic/admin/notification-template/**
-- ------------------------------------------------------------
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200805, '通知模板', 1, 16, 'notification', 'system/notification/index', '', 1, 0, 'C', '0', '0', 'chronic:notification-template:list', 'mdi:message-cog', 103, 1, '2026-08-18 10:00:00', NULL, NULL, '随访提醒/SOS 通知文案模板');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (20080501, '详情', 200805, 1, '#', '', '', 1, 0, 'F', '0', '0', 'chronic:notification-template:query', '#', 103, 1, '2026-08-18 10:00:00', NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (20080502, '新增', 200805, 2, '#', '', '', 1, 0, 'F', '0', '0', 'chronic:notification-template:add', '#', 103, 1, '2026-08-18 10:00:00', NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (20080503, '修改', 200805, 3, '#', '', '', 1, 0, 'F', '0', '0', 'chronic:notification-template:edit', '#', 103, 1, '2026-08-18 10:00:00', NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (20080504, '删除', 200805, 4, '#', '', '', 1, 0, 'F', '0', '0', 'chronic:notification-template:remove', '#', 103, 1, '2026-08-18 10:00:00', NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (20080505, '状态变更', 200805, 5, '#', '', '', 1, 0, 'F', '0', '0', 'chronic:notification-template:status', '#', 103, 1, '2026-08-18 10:00:00', NULL, NULL, '启用/停用');

-- ------------------------------------------------------------
-- 3. 义诊筛查管理（200604）补批次列表 / 批次编辑按钮
--    后端：ScreeningBatchController  GET /screening-batch/page、PUT /screening-batch、PUT /{batchId}/status
-- ------------------------------------------------------------
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (20060404, '批次列表', 200604, 4, '#', '', '', 1, 0, 'F', '0', '0', 'chronic:screening-batch:list', '#', 103, 1, '2026-08-18 10:00:00', NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (20060405, '批次编辑', 200604, 5, '#', '', '', 1, 0, 'F', '0', '0', 'chronic:screening-batch:edit', '#', 103, 1, '2026-08-18 10:00:00', NULL, NULL, '含批次状态变更');

-- ------------------------------------------------------------
-- 4. 同步授权给「慢病医生」角色（role_id=100）？
--    不同步：以上均为管理端（admin 层）能力，医生端权限走 2009 段载体菜单。
--    如需给某个管理角色授权，请在「系统管理-角色」页面勾选，或按需执行：
--    INSERT INTO sys_role_menu(role_id, menu_id) VALUES (<roleId>, 200306), (<roleId>, 20030601), ...;
-- ------------------------------------------------------------
