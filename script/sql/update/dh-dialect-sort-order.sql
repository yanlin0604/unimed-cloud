-- ============================================================
-- 方言采集提示文字增强 DDL 变更脚本
-- 对应 spec: dialect-prompt-enhancement
-- Task 1: dh_dialect_prompt 表添加 sort_order 排序字段
-- Task 10: 添加批量导入按钮权限
-- Task 11: 修复已有数据排序号全为0的问题
-- ============================================================

-- ---------------------------------------------------------------
-- Task 1: dh_dialect_prompt 表添加 sort_order 字段
-- ---------------------------------------------------------------
ALTER TABLE `dh_dialect_prompt`
    ADD COLUMN `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序号' AFTER `status`;

-- ---------------------------------------------------------------
-- Task 10: 添加批量导入按钮权限
-- ---------------------------------------------------------------
-- 获取方言管理菜单ID
SET @parent_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '采集文字管理' AND del_flag = '0' LIMIT 1);

-- 插入批量导入按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, menu_type, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('批量导入', @parent_id, 1, NULL, 'F', 'dh:dialect:import', NULL, 'admin', NOW(), NULL, NULL, '方言采集提示文字批量导入按钮权限');

-- ---------------------------------------------------------------
-- Task 11: 为已有数据按分类+创建时间分配递增排序号
-- 修复所有 sort_order=0 的记录，避免上移/下移功能失效
-- ---------------------------------------------------------------
UPDATE dh_dialect_prompt dp
JOIN (
    SELECT prompt_id,
           ROW_NUMBER() OVER (PARTITION BY category ORDER BY create_time ASC) AS rn
    FROM dh_dialect_prompt
    WHERE del_flag = '0'
) ranked ON dp.prompt_id = ranked.prompt_id
SET dp.sort_order = ranked.rn
WHERE dp.del_flag = '0';

