-- ============================================================
-- 慢病医生端：权限载体菜单 + 角色 + 医生账号
-- 执行库：unimed-cloud（系统库）
--
-- 背景：医生端 36 个 @SaCheckPermission 权限码原先没有任何菜单承载，
--       导致除超管外任何医生账号访问医生端接口都会 403。
--       这里建一个隐藏菜单目录承载权限码（visible=1 不在管理端菜单显示），
--       再建“慢病医生”角色绑定这些权限，并补齐慢病数据引用的 8 个医生账号。
--
-- 账号：doctor2001 ~ doctor2008，初始密码 666666
-- 幂等：按固定 id 先删后插
-- 回滚：DELETE FROM sys_user_role WHERE role_id = 100;
--       DELETE FROM sys_user WHERE user_id BETWEEN 2001 AND 2008;
--       DELETE FROM sys_role_menu WHERE role_id = 100;
--       DELETE FROM sys_role WHERE role_id = 100;
--       DELETE FROM sys_menu WHERE menu_id IN (2009, 200901, 200902, 200903, 200904, 200905, 200906, 200907, 200908, 200909, 200910, 200911, 200912, 200913, 200914, 200915, 200916, 200917, 200918, 200919, 200920, 200921, 200922, 200923, 200924, 200925, 200926, 200927, 200928, 200929, 200930, 200931, 200932, 200933, 200934, 200935, 200936);
-- ============================================================

-- 1) 清理（幂等）
DELETE FROM `sys_user_role` WHERE user_id BETWEEN 2001 AND 2008 OR role_id = 100;
DELETE FROM `sys_user` WHERE user_id BETWEEN 2001 AND 2008;
DELETE FROM `sys_role_menu` WHERE role_id = 100;
DELETE FROM `sys_role` WHERE role_id = 100;
DELETE FROM `sys_menu` WHERE menu_id IN (2009, 200901, 200902, 200903, 200904, 200905, 200906, 200907, 200908, 200909, 200910, 200911, 200912, 200913, 200914, 200915, 200916, 200917, 200918, 200919, 200920, 200921, 200922, 200923, 200924, 200925, 200926, 200927, 200928, 200929, 200930, 200931, 200932, 200933, 200934, 200935, 200936);

-- 2) 权限载体菜单（隐藏目录 + 权限按钮）
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2009, '慢病医生端权限', 0, 99, 'chronic-doctor-perm', '', '', 1, 0, 'M', '1', '0', '', 'mdi:doctor', 103, 1, NOW(), NULL, NULL, '仅用于承载医生端(uni-app)权限码，不在菜单树展示');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200901, '患者列表', 2009, 1, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:patient:list', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200902, '患者详情', 2009, 2, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:patient:query', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200903, '患者签约', 2009, 3, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:patient:sign', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200904, '绑定团队', 2009, 4, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:patient:bind-team', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200905, '管理路径', 2009, 5, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:patient:pathway', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200906, '随访待办', 2009, 6, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:followup-task:list', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200907, '随访执行', 2009, 7, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:followup-task:visit', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200908, '指标查询', 2009, 8, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:metric:list', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200909, '指标录入', 2009, 9, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:metric:add', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200910, '指标修改', 2009, 10, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:metric:edit', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200911, '指标删除', 2009, 11, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:metric:remove', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200912, '用药查询', 2009, 12, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:medication:list', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200913, '用药调整', 2009, 13, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:medication-adjust:add', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200914, '不良反应上报', 2009, 14, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:medication-adverse:add', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200915, '诊疗查询', 2009, 15, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:encounter:list', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200916, '诊疗详情', 2009, 16, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:encounter:query', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200917, '诊疗新增', 2009, 17, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:encounter:add', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200918, '诊疗修改', 2009, 18, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:encounter:edit', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200919, '诊疗提交', 2009, 19, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:encounter:submit', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200920, '分组查询', 2009, 20, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:group:list', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200921, '分组新增', 2009, 21, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:group:add', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200922, '分组修改', 2009, 22, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:group:edit', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200923, '分组删除', 2009, 23, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:group:remove', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200924, '团队解散', 2009, 24, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:team:dissolve', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200925, '团队改派', 2009, 25, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:team:reassign', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200926, '发起筛查', 2009, 26, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:screening:start', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200927, '筛查录入', 2009, 27, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:screening:record', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200928, '筛查补传', 2009, 28, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:screening:batch-upload', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200929, '筛查入组', 2009, 29, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:screening:enroll', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200930, '档案共享查询', 2009, 30, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:archive-share:list', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200931, '档案共享申请', 2009, 31, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:archive-share:add', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200932, '档案共享撤回', 2009, 32, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:archive-share:withdraw', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200933, 'OCR查询', 2009, 33, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:medical-document-ocr:list', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200934, 'OCR详情', 2009, 34, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:medical-document-ocr:query', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200935, 'OCR上传', 2009, 35, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:medical-document-ocr:add', '#', 103, 1, NOW(), NULL, NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (200936, 'OCR确认', 2009, 36, '#', '', '', 1, 0, 'F', '1', '0', 'chronic:doctor:medical-document-ocr:edit', '#', 103, 1, NOW(), NULL, NULL, '');

-- 3) 慢病医生角色（data_scope=5 仅本人及以下，按需调整）
INSERT INTO `sys_role` (`role_id`, `tenant_id`, `role_name`, `role_key`, `role_sort`, `data_scope`, `menu_check_strictly`, `dept_check_strictly`, `status`, `del_flag`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (100, '000000', '慢病医生', 'chronic_doctor', 10, '5', 1, 1, '0', '0', 103, 1, NOW(), NULL, NULL, '慢病医生端(uni-app)角色，持有 chronic:doctor:* 全部权限');

-- 4) 角色-菜单绑定
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 2009);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200901);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200902);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200903);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200904);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200905);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200906);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200907);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200908);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200909);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200910);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200911);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200912);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200913);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200914);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200915);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200916);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200917);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200918);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200919);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200920);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200921);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200922);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200923);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200924);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200925);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200926);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200927);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200928);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200929);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200930);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200931);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200932);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200933);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200934);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200935);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (100, 200936);

-- 5) 医生账号（慢病库 ch_doctor_team / ch_followup_task 等已引用这些 user_id）
INSERT INTO `sys_user` (`user_id`, `tenant_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2001, '000000', 103, 'doctor2001', '张建国', 'sys_user', '', '13910002001', '1', NULL, '$2a$10$b8yUzN0C71sbz.PhNOCgJe.Tu1yWC3RNrTyjSQ8p1W0.aaUXUJ.Ne', '0', '0', '', NULL, 103, 1, NOW(), NULL, NULL, '心内科主任医师·省立医院心内科家医团队负责人');
INSERT INTO `sys_user` (`user_id`, `tenant_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2002, '000000', 106, 'doctor2002', '李明', 'sys_user', '', '13910002002', '1', NULL, '$2a$10$b8yUzN0C71sbz.PhNOCgJe.Tu1yWC3RNrTyjSQ8p1W0.aaUXUJ.Ne', '0', '0', '', NULL, 103, 1, NOW(), NULL, NULL, '内分泌科主任医师·省立医院内分泌科团队负责人');
INSERT INTO `sys_user` (`user_id`, `tenant_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2003, '000000', 104, 'doctor2003', '王芳', 'sys_user', '', '13910002003', '0', NULL, '$2a$10$b8yUzN0C71sbz.PhNOCgJe.Tu1yWC3RNrTyjSQ8p1W0.aaUXUJ.Ne', '0', '0', '', NULL, 103, 1, NOW(), NULL, NULL, '社区全科医师·大明湖社区卫生家医团队负责人');
INSERT INTO `sys_user` (`user_id`, `tenant_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2004, '000000', 103, 'doctor2004', '赵敏', 'sys_user', '', '13910002004', '0', NULL, '$2a$10$b8yUzN0C71sbz.PhNOCgJe.Tu1yWC3RNrTyjSQ8p1W0.aaUXUJ.Ne', '0', '0', '', NULL, 103, 1, NOW(), NULL, NULL, '心内科主治医师');
INSERT INTO `sys_user` (`user_id`, `tenant_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2005, '000000', 103, 'doctor2005', '孙磊', 'sys_user', '', '13910002005', '1', NULL, '$2a$10$b8yUzN0C71sbz.PhNOCgJe.Tu1yWC3RNrTyjSQ8p1W0.aaUXUJ.Ne', '0', '0', '', NULL, 103, 1, NOW(), NULL, NULL, '心内科医师');
INSERT INTO `sys_user` (`user_id`, `tenant_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2006, '000000', 106, 'doctor2006', '周婷', 'sys_user', '', '13910002006', '0', NULL, '$2a$10$b8yUzN0C71sbz.PhNOCgJe.Tu1yWC3RNrTyjSQ8p1W0.aaUXUJ.Ne', '0', '0', '', NULL, 103, 1, NOW(), NULL, NULL, '内分泌科主治医师');
INSERT INTO `sys_user` (`user_id`, `tenant_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2007, '000000', 106, 'doctor2007', '吴强', 'sys_user', '', '13910002007', '1', NULL, '$2a$10$b8yUzN0C71sbz.PhNOCgJe.Tu1yWC3RNrTyjSQ8p1W0.aaUXUJ.Ne', '0', '0', '', NULL, 103, 1, NOW(), NULL, NULL, '内分泌科医师');
INSERT INTO `sys_user` (`user_id`, `tenant_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2008, '000000', 104, 'doctor2008', '郑洁', 'sys_user', '', '13910002008', '0', NULL, '$2a$10$b8yUzN0C71sbz.PhNOCgJe.Tu1yWC3RNrTyjSQ8p1W0.aaUXUJ.Ne', '0', '0', '', NULL, 103, 1, NOW(), NULL, NULL, '社区全科医师');

-- 6) 用户-角色绑定
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (2001, 100);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (2002, 100);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (2003, 100);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (2004, 100);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (2005, 100);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (2006, 100);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (2007, 100);
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (2008, 100);
