-- ============================================================
-- 方言采集邀请码配置 DDL + 菜单 变更脚本
-- 对应 spec: dialect-invite-link
-- Task 1: 创建 dh_dialect_invite 邀请码配置表（只有语种名字段）
-- Task 2: dh_dialect_record 表添加 invite_code 列
-- Task 3: 添加邀请码管理菜单及按钮权限
-- ============================================================

-- ---------------------------------------------------------------
-- Task 1: 创建 dh_dialect_invite 邀请码配置表
-- ---------------------------------------------------------------
DROP TABLE IF EXISTS `dh_dialect_invite`;
CREATE TABLE `dh_dialect_invite` (
    `invite_id`      BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '邀请配置ID',
    `dialect_name`   VARCHAR(100) NOT NULL COMMENT '语种名（与C端方言名称一致）',
    `invite_code`    VARCHAR(20)  NOT NULL COMMENT '邀请码（8位唯一）',
    `collection_url` VARCHAR(500) DEFAULT NULL COMMENT '生成的分享链接',
    `status`         CHAR(1)      NOT NULL DEFAULT '0' COMMENT '状态（0正常 1禁用）',
    `tenant_id`      VARCHAR(20)  DEFAULT '000000' COMMENT '租户ID',
    `create_dept`    BIGINT(20)   DEFAULT NULL COMMENT '创建部门',
    `create_by`      BIGINT(20)   DEFAULT NULL COMMENT '创建者',
    `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`      BIGINT(20)   DEFAULT NULL COMMENT '更新者',
    `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`       CHAR(1)      DEFAULT '0' COMMENT '删除标志（0正常 1删除）',
    PRIMARY KEY (`invite_id`),
    UNIQUE INDEX `uk_invite_code` (`invite_code`) COMMENT '邀请码唯一索引',
    INDEX `idx_dialect_name` (`dialect_name`) COMMENT '语种名索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='方言邀请码配置表';

-- ---------------------------------------------------------------
-- Task 2: dh_dialect_record 表添加 invite_code 列
-- ---------------------------------------------------------------
ALTER TABLE `dh_dialect_record`
    ADD COLUMN `invite_code` VARCHAR(20) DEFAULT NULL COMMENT '邀请码来源' AFTER `user_id`;

-- ---------------------------------------------------------------
-- Task 3: 添加邀请码管理菜单及按钮权限
-- ---------------------------------------------------------------
-- 获取"采集文字管理"菜单ID
SET @parent_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '采集文字管理' AND del_flag = '0' LIMIT 1);

-- 插入"邀请码管理"菜单（目录级）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, menu_type, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('邀请码管理', @parent_id, 5, 'dialectInvite', 'C', 'dh:dialectInvite:list', 'link', 'admin', NOW(), NULL, NULL, '方言邀请码管理菜单');

-- 设置新插入的菜单ID为 @invite_menu_id，用于后续按钮权限关联
SET @invite_menu_id = LAST_INSERT_ID();

-- 新增按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, menu_type, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
('邀请码查询', @invite_menu_id, 1, NULL, 'F', 'dh:dialectInvite:list', NULL, 'admin', NOW(), NULL, NULL, '邀请码列表查询按钮权限'),
('邀请码新增',  @invite_menu_id, 2, NULL, 'F', 'dh:dialectInvite:add', NULL, 'admin', NOW(), NULL, NULL, '邀请码新增按钮权限'),
('邀请码修改',  @invite_menu_id, 3, NULL, 'F', 'dh:dialectInvite:edit', NULL, 'admin', NOW(), NULL, NULL, '邀请码修改按钮权限'),
('邀请码删除',  @invite_menu_id, 4, NULL, 'F', 'dh:dialectInvite:remove', NULL, 'admin', NOW(), NULL, NULL, '邀请码删除按钮权限');
