-- ====================================================================
-- 慢病模块 - 患者结案申请补丁
-- 表：ch_patient_close_apply（已在 unimed-chronic.sql 中定义）
-- 权限码：patient:closeCase:*（与 sys_menu.sql 已存在的命名空间保持一致）
--   已存在：list(200203) / query(20020301) / add(20020302) / edit(20020303) / remove(20020304)
--   本补丁新增：audit（后端审核接口需要的按钮权限）
-- 字典：chronic_close_apply_status / chronic_close_type
-- ====================================================================

-- ============== 缺失按钮：结案审核 ==============
INSERT IGNORE INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `remark`)
VALUES (20020305, '结案审核', 200203, 5, '#', '', '', 1, 0, 'F', '0', '0', 'patient:closeCase:audit', '#', 103, 1, NOW(), '结案申请审核按钮');

-- ============== 字典类型 ==============
INSERT IGNORE INTO `sys_dict_type` (`tenant_id`, `dict_name`, `dict_type`, `create_dept`, `create_by`, `create_time`, `remark`)
VALUES
('000000', '结案审核状态', 'chronic_close_apply_status', 103, 1, NOW(), '患者结案申请审核状态'),
('000000', '结案类型',     'chronic_close_type',         103, 1, NOW(), '患者结案类型');

-- ============== 字典数据：审核状态 ==============
INSERT IGNORE INTO `sys_dict_data` (`tenant_id`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `create_dept`, `create_by`, `create_time`, `remark`)
VALUES
('000000', 1, '待审核', 'PENDING',   'chronic_close_apply_status', '', 'processing', 'Y', 103, 1, NOW(), '提交待审核'),
('000000', 2, '已通过', 'APPROVED',  'chronic_close_apply_status', '', 'success',    'N', 103, 1, NOW(), '审核通过'),
('000000', 3, '已驳回', 'REJECTED',  'chronic_close_apply_status', '', 'error',      'N', 103, 1, NOW(), '审核驳回'),
('000000', 4, '已撤回', 'WITHDRAWN', 'chronic_close_apply_status', '', 'default',    'N', 103, 1, NOW(), '申请人撤回');

-- ============== 字典数据：结案类型 ==============
INSERT IGNORE INTO `sys_dict_data` (`tenant_id`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `create_dept`, `create_by`, `create_time`, `remark`)
VALUES
('000000', 1, '死亡',     'DEATH',     'chronic_close_type', '', 'error',   'N', 103, 1, NOW(), '需上传死亡证明'),
('000000', 2, '迁出辖区', 'MOVED_OUT', 'chronic_close_type', '', 'default', 'N', 103, 1, NOW(), '居住地变更'),
('000000', 3, '迁出辖区', 'TRANSFER',  'chronic_close_type', '', 'default', 'N', 103, 1, NOW(), '兼容值，与 MOVED_OUT 同义'),
('000000', 4, '自愿退出', 'VOLUNTARY', 'chronic_close_type', '', 'warning', 'N', 103, 1, NOW(), '患者主动退出管理'),
('000000', 5, '康复转出', 'RECOVERED', 'chronic_close_type', '', 'success', 'Y', 103, 1, NOW(), '康复转上级或社区'),
('000000', 6, '长期失联', 'LOST',      'chronic_close_type', '', 'error',   'N', 103, 1, NOW(), '需上传3次失联记录'),
('000000', 7, '其他',     'OTHER',     'chronic_close_type', '', 'default', 'N', 103, 1, NOW(), '其他原因');
