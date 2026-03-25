-- ============================================================
-- 数字人系统资产管理 DDL 变更脚本
-- 对应 spec: dh-system-asset-management
-- Task 1: dh_material 表新增 is_system / sort_order 字段
-- Task 2: 新建 dh_background 表
-- ============================================================

-- ---------------------------------------------------------------
-- Task 1: dh_material 表新增字段（向后兼容，存量数据默认为 0）
-- ---------------------------------------------------------------
ALTER TABLE dh_material
    ADD COLUMN `is_system`  tinyint(1)  NOT NULL DEFAULT 0 COMMENT '是否系统预设 0否 1是' AFTER `user_id`,
    ADD COLUMN `sort_order` int         NOT NULL DEFAULT 0 COMMENT '排序号'               AFTER `remark`;

-- ---------------------------------------------------------------
-- Task 2: 新建 dh_background 表
-- ---------------------------------------------------------------
CREATE TABLE `dh_background` (
    `background_id` bigint       NOT NULL AUTO_INCREMENT                    COMMENT '背景ID',
    `name`          varchar(100) NOT NULL                                   COMMENT '背景名称',
    `bg_type`       varchar(20)  NOT NULL                                   COMMENT '背景类型 IMAGE/VIDEO',
    `oss_id`        varchar(100)     NULL                                   COMMENT 'OSS文件ID',
    `preview_url`   varchar(500)     NULL                                   COMMENT '预览URL',
    `is_system`     tinyint(1)   NOT NULL DEFAULT 1                        COMMENT '是否系统预设 0否 1是',
    `status`        char(1)      NOT NULL DEFAULT '0'                       COMMENT '状态 0正常 1禁用',
    `sort_order`    int          NOT NULL DEFAULT 0                         COMMENT '排序号',
    `remark`        varchar(500)     NULL                                   COMMENT '备注',
    `tenant_id`     varchar(20)      NULL                                   COMMENT '租户ID',
    `create_by`     varchar(64)      NULL                                   COMMENT '创建者',
    `create_time`   datetime         NULL                                   COMMENT '创建时间',
    `update_by`     varchar(64)      NULL                                   COMMENT '更新者',
    `update_time`   datetime         NULL                                   COMMENT '更新时间',
    `del_flag`      char(1)          NULL DEFAULT '0'                       COMMENT '删除标志 0存在 1删除',
    `create_dept`   bigint           NULL                                   COMMENT '创建部门',
    PRIMARY KEY (`background_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='数字人背景资源表';

-- ---------------------------------------------------------------
-- Task 2 补丁：dh_background 表补充 create_dept 字段（TenantEntity 审计字段）
-- 若已执行建表 SQL，需单独执行此语句
-- ---------------------------------------------------------------
-- ALTER TABLE dh_background ADD COLUMN `create_dept` bigint NULL COMMENT '创建部门' AFTER `del_flag`;

-- ---------------------------------------------------------------
-- Task 3: dh_material.user_id 改为允许 NULL（系统预设素材无用户）
-- ---------------------------------------------------------------
ALTER TABLE dh_material MODIFY COLUMN `user_id` BIGINT(20) NULL COMMENT '用户ID（系统预设为空）';
