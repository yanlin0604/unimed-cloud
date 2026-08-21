-- 随访计划完整修改权限菜单
-- 执行库：unimed-cloud（系统库）
-- 幂等：存在则更新，不删除已有角色菜单关系。
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`,
                        `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2005020103, '修改', 20050201, 3, '#', '', '', 1, 0, 'F', '0', '0',
        'chronic:followup-plan:edit', '#', 103, 1, '2026-08-21 12:00:00', NULL, NULL, '')
ON DUPLICATE KEY UPDATE
    `menu_name` = VALUES(`menu_name`),
    `parent_id` = VALUES(`parent_id`),
    `order_num` = VALUES(`order_num`),
    `perms` = VALUES(`perms`),
    `status` = VALUES(`status`);
