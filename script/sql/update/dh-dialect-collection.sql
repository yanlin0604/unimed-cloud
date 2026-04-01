-- ============================================================
-- 方言采集管理 DDL 变更脚本
-- 对应 spec: dialect-collection-management
-- Task 1: 新建 dh_dialect_prompt 表（采集提示文字）
-- Task 2: 新建 dh_dialect_record 表（方言录音记录）
-- ============================================================

-- ---------------------------------------------------------------
-- Task 1: 新建 dh_dialect_prompt 表
-- ---------------------------------------------------------------
CREATE TABLE `dh_dialect_prompt` (
    `prompt_id`   bigint       NOT NULL AUTO_INCREMENT                    COMMENT '提示文字ID',
    `content`     varchar(500) NOT NULL                                   COMMENT '采集文字内容',
    `category`    varchar(100) NOT NULL                                   COMMENT '分类标签',
    `status`      char(1)      NOT NULL DEFAULT '0'                       COMMENT '状态 0正常 1禁用',
    `remark`      varchar(255)     NULL                                   COMMENT '备注',
    `tenant_id`   varchar(20)      NULL                                   COMMENT '租户ID',
    `create_by`   varchar(64)      NULL                                   COMMENT '创建者',
    `create_time` datetime         NULL                                   COMMENT '创建时间',
    `update_by`   varchar(64)      NULL                                   COMMENT '更新者',
    `update_time` datetime         NULL                                   COMMENT '更新时间',
    `del_flag`    char(1)          NULL DEFAULT '0'                       COMMENT '删除标志 0存在 1删除',
    `create_dept` bigint           NULL                                   COMMENT '创建部门',
    PRIMARY KEY (`prompt_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='方言采集提示文字表';

-- ---------------------------------------------------------------
-- Task 2: 新建 dh_dialect_record 表
-- ---------------------------------------------------------------
CREATE TABLE `dh_dialect_record` (
    `record_id`    bigint       NOT NULL AUTO_INCREMENT                    COMMENT '录音记录ID',
    `prompt_id`    bigint       NOT NULL                                   COMMENT '关联提示文字ID',
    `user_id`      bigint           NULL                                   COMMENT '提交用户ID（匿名提交为空）',
    `dialect_name` varchar(100) NOT NULL                                   COMMENT '方言名称',
    `dialect_area` varchar(100)     NULL                                   COMMENT '方言地区',
    `audio_url`    varchar(500)     NULL                                   COMMENT 'OSS持久访问URL',
    `oss_id`       varchar(100)     NULL                                   COMMENT 'OSS文件ID',
    `duration`     int              NULL DEFAULT 0                         COMMENT '录音时长（秒）',
    `audit_status` varchar(20)  NOT NULL DEFAULT 'PENDING'                 COMMENT '审核状态 PENDING/APPROVED/REJECTED',
    `audit_remark` varchar(255)     NULL                                   COMMENT '审核备注',
    `tenant_id`    varchar(20)      NULL                                   COMMENT '租户ID',
    `create_by`    varchar(64)      NULL                                   COMMENT '创建者',
    `create_time`  datetime         NULL                                   COMMENT '创建时间',
    `update_by`    varchar(64)      NULL                                   COMMENT '更新者',
    `update_time`  datetime         NULL                                   COMMENT '更新时间',
    `del_flag`     char(1)          NULL DEFAULT '0'                       COMMENT '删除标志 0存在 1删除',
    `create_dept`  bigint           NULL                                   COMMENT '创建部门',
    PRIMARY KEY (`record_id`),
    KEY `idx_prompt_id` (`prompt_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_audit_status` (`audit_status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='方言采集录音记录表';
