-- ====================================================================
-- 慢病随访功能调整与重构 SQL 脚本
-- 1. 执行人术语统一（assignee_user_id 允许为空，支持随访任务池）
-- 2. 随访记录扩展随访结论、康复评级、随访回报建议
-- 3. 随访方式字典精简为三种：ONLINE(线上)、OFFLINE(线下)、PHONE(电话)
-- 4. 增加随访结论、康复评级、自动分发策略字典
-- 5. 增加任务池与随访统计菜单与权限
-- ====================================================================

-- 1. 表结构调整
-- 1.1 随访计划表：assignee_user_id 允许为空（为空即放入公共任务池），修改注释为执行人
ALTER TABLE `ch_followup_plan`
    MODIFY COLUMN `assignee_user_id` bigint NULL DEFAULT NULL COMMENT '执行人用户ID';

-- 1.2 随访任务表：assignee_user_id 允许为空（为空即在随访任务池中待认领/待分发），修改注释为执行人
ALTER TABLE `ch_followup_task`
    MODIFY COLUMN `assignee_user_id` bigint NULL DEFAULT NULL COMMENT '执行人用户ID';

-- 1.3 随访记录表：增加随访结论、康复评级、随访回报建议字段
ALTER TABLE `ch_followup_record`
    MODIFY COLUMN `visitor_user_id` bigint NULL DEFAULT NULL COMMENT '执行人用户ID',
    ADD COLUMN `followup_result` varchar(30) NULL DEFAULT NULL COMMENT '随访结论(CONTROLLED/IMPROVING/UNCONTROLLED/DETERIORATING/REFERRAL)' AFTER `visit_content`,
    ADD COLUMN `rehab_level` varchar(20) NULL DEFAULT NULL COMMENT '康复评级(EXCELLENT/GOOD/FAIR/POOR)' AFTER `followup_result`,
    ADD COLUMN `feedback_advice` text NULL COMMENT '随访回报与健康指导建议' AFTER `rehab_level`;

-- 2. 字典数据调整（不要兼容旧代码，全面按照新标准重构）
-- 2.1 随访方式字典 chronic_visit_type：精简为三种 (ONLINE, OFFLINE, PHONE)
DELETE FROM `sys_dict_data` WHERE `dict_type` = 'chronic_visit_type';
INSERT INTO `sys_dict_data` (`tenant_id`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_dept`, `create_by`, `create_time`, `remark`)
VALUES
('000000', 1, '线上随访', 'ONLINE', 'chronic_visit_type', '', 'primary', 'Y', '0', 103, 1, NOW(), '线上随访/问卷推送自填'),
('000000', 2, '线下随访', 'OFFLINE', 'chronic_visit_type', '', 'success', 'N', '0', 103, 1, NOW(), '门诊或上门随访/执行人填写'),
('000000', 3, '电话随访', 'PHONE', 'chronic_visit_type', '', 'warning', 'N', '0', 103, 1, NOW(), '电话随访/执行人记录');

-- 2.2 新增随访结论字典 chronic_followup_result
DELETE FROM `sys_dict_data` WHERE `dict_type` = 'chronic_followup_result';
DELETE FROM `sys_dict_type` WHERE `dict_type` = 'chronic_followup_result';
INSERT INTO `sys_dict_type` (`tenant_id`, `dict_name`, `dict_type`, `status`, `create_dept`, `create_by`, `create_time`, `remark`)
VALUES ('000000', '慢病随访结论', 'chronic_followup_result', '0', 103, 1, NOW(), '随访病情控制与评估结论');

INSERT INTO `sys_dict_data` (`tenant_id`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_dept`, `create_by`, `create_time`, `remark`)
VALUES
('000000', 1, '控制良好', 'CONTROLLED', 'chronic_followup_result', '', 'success', 'Y', '0', 103, 1, NOW(), '病情控制良好/指标达标'),
('000000', 2, '病情好转', 'IMPROVING', 'chronic_followup_result', '', 'primary', 'N', '0', 103, 1, NOW(), '病情稳步好转'),
('000000', 3, '控制不良', 'UNCONTROLLED', 'chronic_followup_result', '', 'warning', 'N', '0', 103, 1, NOW(), '控制不满意/需调整方案'),
('000000', 4, '病情恶化', 'DETERIORATING', 'chronic_followup_result', '', 'danger', 'N', '0', 103, 1, NOW(), '病情恶化需加急处理'),
('000000', 5, '建议转诊', 'REFERRAL', 'chronic_followup_result', '', 'danger', 'N', '0', 103, 1, NOW(), '超出基层诊疗范围需转诊');

-- 2.3 新增康复评级字典 chronic_rehab_level
DELETE FROM `sys_dict_data` WHERE `dict_type` = 'chronic_rehab_level';
DELETE FROM `sys_dict_type` WHERE `dict_type` = 'chronic_rehab_level';
INSERT INTO `sys_dict_type` (`tenant_id`, `dict_name`, `dict_type`, `status`, `create_dept`, `create_by`, `create_time`, `remark`)
VALUES ('000000', '慢病康复评级', 'chronic_rehab_level', '0', 103, 1, NOW(), '患者康复情况评估评级');

INSERT INTO `sys_dict_data` (`tenant_id`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_dept`, `create_by`, `create_time`, `remark`)
VALUES
('000000', 1, '优秀', 'EXCELLENT', 'chronic_rehab_level', '', 'success', 'Y', '0', 103, 1, NOW(), '功能恢复优秀/依从性高'),
('000000', 2, '良好', 'GOOD', 'chronic_rehab_level', '', 'primary', 'N', '0', 103, 1, NOW(), '功能恢复良好'),
('000000', 3, '一般', 'FAIR', 'chronic_rehab_level', '', 'warning', 'N', '0', 103, 1, NOW(), '康复一般/需督导'),
('000000', 4, '较差', 'POOR', 'chronic_rehab_level', '', 'danger', 'N', '0', 103, 1, NOW(), '康复较差/存在功能障碍');

-- 2.4 新增自动分发策略字典 chronic_dispatch_strategy
DELETE FROM `sys_dict_data` WHERE `dict_type` = 'chronic_dispatch_strategy';
DELETE FROM `sys_dict_type` WHERE `dict_type` = 'chronic_dispatch_strategy';
INSERT INTO `sys_dict_type` (`tenant_id`, `dict_name`, `dict_type`, `status`, `create_dept`, `create_by`, `create_time`, `remark`)
VALUES ('000000', '随访任务自动分发策略', 'chronic_dispatch_strategy', '0', 103, 1, NOW(), '任务池自动分发执行人算法策略');

INSERT INTO `sys_dict_data` (`tenant_id`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_dept`, `create_by`, `create_time`, `remark`)
VALUES
('000000', 1, '负载均衡(最少待办优先)', 'LEAST_LOADED', 'chronic_dispatch_strategy', '', 'primary', 'Y', '0', 103, 1, NOW(), '优先分发给当前待办任务最少的执行人'),
('000000', 2, '轮询分发', 'ROUND_ROBIN', 'chronic_dispatch_strategy', '', 'info', 'N', '0', 103, 1, NOW(), '在执行人池中顺序循环轮询分发'),
('000000', 3, '随机分发', 'RANDOM', 'chronic_dispatch_strategy', '', 'default', 'N', '0', 103, 1, NOW(), '在执行人池中完全随机分配'),
('000000', 4, '专病匹配优先', 'DISEASE_MATCH', 'chronic_dispatch_strategy', '', 'success', 'N', '0', 103, 1, NOW(), '优先匹配负责该病种的执行人');

-- 3. 菜单与按钮权限配置
-- 3.1 随访统计菜单 (挂在 2001 工作台与看板 下，order 5)
DELETE FROM `sys_menu` WHERE `menu_id` = 20050205;
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `remark`)
VALUES (20050205, '随访统计看板', 2001, 5, 'followup-stat', 'service/followup/stat/index', '', 1, 0, 'C', '0', '0', 'chronic:followup-stat:query', 'carbon:chart-line-smooth', 103, 1, NOW(), '随访统计与效果评价看板');

-- 3.2 随访任务池与批量认领/指派按钮权限
DELETE FROM `sys_menu` WHERE `menu_id` IN (2005020204, 2005020205, 2005020206, 2005020207);
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query_param`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_dept`, `create_by`, `create_time`, `remark`)
VALUES
(2005020204, '任务池查询', 20050202, 4, '#', '', '', 1, 0, 'F', '0', '0', 'chronic:followup-task:pool', '#', 103, 1, NOW(), '随访任务池查询'),
(2005020205, '认领任务', 20050202, 5, '#', '', '', 1, 0, 'F', '0', '0', 'chronic:followup-task:claim', '#', 103, 1, NOW(), '认领与批量认领随访任务'),
(2005020206, '批量指派', 20050202, 6, '#', '', '', 1, 0, 'F', '0', '0', 'chronic:followup-task:batch-assign', '#', 103, 1, NOW(), '批量指派随访任务'),
(2005020207, '退回任务池', 20050202, 7, '#', '', '', 1, 0, 'F', '0', '0', 'chronic:followup-task:release', '#', 103, 1, NOW(), '释放任务回随访任务池');
