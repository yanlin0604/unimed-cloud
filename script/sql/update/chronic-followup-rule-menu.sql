-- ============================================================
-- 慢病菜单增量:随访排期规则配置
-- 执行库:unimed-cloud(系统库)
-- 页面组件:service/followup/rule/index
-- 后端接口:/chronic/admin/followup-rule/**
--
-- 说明:非破坏性幂等脚本。使用 INSERT IGNORE + 固定 menu_id 主键,
--       已存在的菜单/按钮保持原样(不重建、不覆盖既有配置与数据),
--       仅插入缺失的项。不会删除或重置任何已有菜单配置。
-- ============================================================

INSERT IGNORE INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `remark`) VALUES
(20050206, '随访排期规则', 200502, 6, 'rule', 'service/followup/rule/index', '', 1, 0, 'C', '0', '0', 'chronic:followupRule:list', 'mdi:calendar-edit', 103, 1, NOW(), '随访排期规则配置');

INSERT IGNORE INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `remark`) VALUES
(2005020601, '详情', 20050206, 1, '#', '', '', 1, 0, 'F', '0', '0', 'chronic:followupRule:list', '#', 103, 1, NOW(), '随访排期规则详情'),
(2005020602, '新增', 20050206, 2, '#', '', '', 1, 0, 'F', '0', '0', 'chronic:followupRule:add', '#', 103, 1, NOW(), '新增随访排期规则'),
(2005020603, '修改', 20050206, 3, '#', '', '', 1, 0, 'F', '0', '0', 'chronic:followupRule:edit', '#', 103, 1, NOW(), '修改随访排期规则'),
(2005020604, '状态变更', 20050206, 4, '#', '', '', 1, 0, 'F', '0', '0', 'chronic:followupRule:status', '#', 103, 1, NOW(), '启用或停用随访排期规则'),
(2005020605, '删除', 20050206, 5, '#', '', '', 1, 0, 'F', '0', '0', 'chronic:followupRule:remove', '#', 103, 1, NOW(), '删除随访排期规则');
