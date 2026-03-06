-- 数字人口播后台扩展表结构

CREATE TABLE `dh_topup_ticket` (
  `ticket_id` bigint(20) NOT NULL COMMENT '充值工单ID',
  `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `user_name` varchar(64) NOT NULL COMMENT '用户名快照',
  `amount` decimal(10,2) NOT NULL COMMENT '申请充值金额',
  `status` varchar(16) NOT NULL COMMENT '工单状态',
  `voucher_desc` varchar(255) DEFAULT NULL COMMENT '凭证说明',
  `voucher_image_ids` varchar(500) DEFAULT NULL COMMENT '凭证图片ID列表',
  `actual_amount` decimal(10,2) DEFAULT NULL COMMENT '实际到账金额',
  `approved_by` varchar(64) DEFAULT NULL COMMENT '审核人',
  `approved_at` datetime DEFAULT NULL COMMENT '审核通过时间',
  `reject_reason` varchar(500) DEFAULT NULL COMMENT '驳回原因',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_dept` bigint(20) DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`ticket_id`),
  KEY `idx_dh_topup_status_time` (`tenant_id`, `status`, `create_time`),
  KEY `idx_dh_topup_user_time` (`tenant_id`, `user_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数字人口播充值工单表';

CREATE TABLE `dh_user_profile` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
  `user_name` varchar(64) NOT NULL COMMENT '用户名',
  `phone` varchar(32) NOT NULL COMMENT '手机号',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像URL',
  `member_level` varchar(16) NOT NULL COMMENT '会员等级',
  `wallet_balance` decimal(12,2) DEFAULT 0.00 COMMENT '钱包余额',
  `total_topup` decimal(12,2) DEFAULT 0.00 COMMENT '累计充值',
  `total_consume` decimal(12,2) DEFAULT 0.00 COMMENT '累计消费',
  `order_count` int(11) DEFAULT 0 COMMENT '订单数',
  `status` char(1) DEFAULT '0' COMMENT '状态（0启用 1停用）',
  `register_time` datetime DEFAULT NULL COMMENT '注册时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_dept` bigint(20) DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_dh_user_phone` (`tenant_id`, `phone`),
  KEY `idx_dh_user_level_status` (`tenant_id`, `member_level`, `status`),
  KEY `idx_dh_user_register_time` (`tenant_id`, `register_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数字人口播用户画像表';

CREATE TABLE `dh_wallet_log` (
  `log_id` bigint(20) NOT NULL COMMENT '钱包流水ID',
  `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `user_name` varchar(64) DEFAULT NULL COMMENT '用户名快照',
  `type` varchar(16) NOT NULL COMMENT '流水类型',
  `amount` decimal(12,2) NOT NULL COMMENT '变动金额',
  `balance_after` decimal(12,2) NOT NULL COMMENT '变动后余额',
  `related_order_id` bigint(20) DEFAULT NULL COMMENT '关联订单ID',
  `operator_name` varchar(64) DEFAULT NULL COMMENT '操作人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_dept` bigint(20) DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`log_id`),
  KEY `idx_dh_wallet_user_time` (`tenant_id`, `user_id`, `create_time`),
  KEY `idx_dh_wallet_type_time` (`tenant_id`, `type`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数字人口播钱包流水表';

CREATE TABLE `dh_report_ticket` (
  `report_id` bigint(20) NOT NULL COMMENT '举报单ID',
  `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
  `reporter_name` varchar(64) NOT NULL COMMENT '举报人',
  `target_user_id` bigint(20) DEFAULT NULL COMMENT '被举报用户ID',
  `target_user_name` varchar(64) DEFAULT NULL COMMENT '被举报用户名',
  `target_content_id` varchar(64) NOT NULL COMMENT '被举报内容ID',
  `target_content_type` varchar(16) NOT NULL COMMENT '被举报内容类型',
  `type` varchar(32) NOT NULL COMMENT '举报类型',
  `description` varchar(1000) NOT NULL COMMENT '举报描述',
  `status` varchar(16) NOT NULL COMMENT '处理状态',
  `handler_name` varchar(64) DEFAULT NULL COMMENT '处理人',
  `handle_result` varchar(500) DEFAULT NULL COMMENT '处理结论',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_dept` bigint(20) DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`report_id`),
  KEY `idx_dh_report_type_status_time` (`tenant_id`, `type`, `status`, `create_time`),
  KEY `idx_dh_report_target_user` (`tenant_id`, `target_user_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数字人口播举报工单表';

CREATE TABLE `dh_member_config` (
  `config_id` bigint(20) NOT NULL COMMENT '会员配置ID',
  `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
  `level` varchar(16) NOT NULL COMMENT '会员等级',
  `level_name` varchar(64) NOT NULL COMMENT '等级名称',
  `order_price` decimal(10,2) DEFAULT 0.00 COMMENT '单价',
  `monthly_limit` int(11) DEFAULT 0 COMMENT '月度额度',
  `speed_priority` int(11) DEFAULT 0 COMMENT '速度优先级',
  `min_topup_amount` decimal(10,2) DEFAULT 0.00 COMMENT '最低充值要求',
  `validity_days` int(11) DEFAULT 30 COMMENT '有效期天数',
  `expect_delivery_hours` int(11) DEFAULT 24 COMMENT '预计交付时长',
  `redo_limit` int(11) DEFAULT 0 COMMENT '重做次数上限',
  `status` char(1) DEFAULT '0' COMMENT '状态（0启用 1停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_dept` bigint(20) DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`config_id`),
  UNIQUE KEY `uk_dh_member_level` (`tenant_id`, `level`),
  KEY `idx_dh_member_status_time` (`tenant_id`, `status`, `update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数字人口播会员配置表';

CREATE TABLE `dh_payment_price_config` (
  `config_id` bigint(20) NOT NULL COMMENT '充值档位配置ID',
  `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
  `config_name` varchar(64) NOT NULL COMMENT '配置名称',
  `member_level` varchar(16) NOT NULL COMMENT '适用会员等级',
  `pay_type` varchar(16) NOT NULL COMMENT '支付类型',
  `amount` decimal(10,2) NOT NULL COMMENT '充值金额',
  `bonus_amount` decimal(10,2) DEFAULT 0.00 COMMENT '赠送金额',
  `sort` int(11) DEFAULT 0 COMMENT '排序',
  `status` char(1) DEFAULT '0' COMMENT '状态（0启用 1停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_dept` bigint(20) DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`config_id`),
  KEY `idx_dh_price_level_pay` (`tenant_id`, `member_level`, `pay_type`, `status`),
  KEY `idx_dh_price_sort` (`tenant_id`, `sort`, `update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数字人口播充值档位配置表';

CREATE TABLE `dh_video_upload_config` (
  `config_id` bigint(20) NOT NULL COMMENT '视频上传配置ID',
  `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
  `config_name` varchar(64) NOT NULL COMMENT '配置名称',
  `type` varchar(16) NOT NULL COMMENT '上传类型',
  `video_file_ids` varchar(500) DEFAULT NULL COMMENT '文件ID列表',
  `max_size_mb` int(11) DEFAULT 200 COMMENT '大小限制MB',
  `format_desc` varchar(64) DEFAULT NULL COMMENT '格式描述',
  `status` char(1) DEFAULT '0' COMMENT '状态（0启用 1停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_dept` bigint(20) DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`config_id`),
  KEY `idx_dh_video_type_status` (`tenant_id`, `type`, `status`, `update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数字人口播视频上传配置表';

CREATE TABLE `dh_qr_upload_config` (
  `config_id` bigint(20) NOT NULL COMMENT '收款码配置ID',
  `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
  `config_name` varchar(64) NOT NULL COMMENT '配置名称',
  `type` varchar(16) NOT NULL COMMENT '收款码类型',
  `qr_image_ids` varchar(500) DEFAULT NULL COMMENT '收款码图片ID列表',
  `account_name` varchar(128) NOT NULL COMMENT '收款账户名',
  `account_no` varchar(128) NOT NULL COMMENT '收款账号',
  `bank_name` varchar(128) DEFAULT NULL COMMENT '银行名称',
  `status` char(1) DEFAULT '0' COMMENT '状态（0启用 1停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_dept` bigint(20) DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`config_id`),
  UNIQUE KEY `uk_dh_qr_type_account` (`tenant_id`, `type`, `account_no`),
  KEY `idx_dh_qr_status_time` (`tenant_id`, `status`, `update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数字人口播收款码配置表';

CREATE TABLE `dh_sensitive_word` (
  `word_id` bigint(20) NOT NULL COMMENT '敏感词ID',
  `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
  `word` varchar(128) NOT NULL COMMENT '敏感词',
  `level` varchar(16) NOT NULL COMMENT '风险等级',
  `category` varchar(16) NOT NULL COMMENT '分类',
  `status` char(1) DEFAULT '0' COMMENT '状态（0启用 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_dept` bigint(20) DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`word_id`),
  UNIQUE KEY `uk_dh_sensitive_word` (`tenant_id`, `word`),
  KEY `idx_dh_sensitive_level_status` (`tenant_id`, `level`, `status`, `update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数字人口播敏感词配置表';

CREATE TABLE `dh_notify_template` (
  `template_id` bigint(20) NOT NULL COMMENT '通知模板ID',
  `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
  `template_name` varchar(128) NOT NULL COMMENT '模板名称',
  `scene` varchar(32) NOT NULL COMMENT '通知场景',
  `channel` varchar(16) NOT NULL COMMENT '通知渠道',
  `content` varchar(1000) NOT NULL COMMENT '模板内容',
  `timeout_hours` int(11) DEFAULT NULL COMMENT '超时阈值（小时）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0启用 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_dept` bigint(20) DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`template_id`),
  KEY `idx_dh_notify_scene_status` (`tenant_id`, `scene`, `status`, `update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数字人口播通知模板表';

CREATE TABLE `dh_audit_log` (
  `log_id` bigint(20) NOT NULL COMMENT '审计日志ID',
  `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
  `action` varchar(32) NOT NULL COMMENT '审计动作',
  `operator_name` varchar(64) NOT NULL COMMENT '操作人',
  `target_type` varchar(32) NOT NULL COMMENT '目标类型',
  `target_id` varchar(64) NOT NULL COMMENT '目标ID',
  `detail` varchar(1000) NOT NULL COMMENT '详情',
  `ip_address` varchar(64) DEFAULT NULL COMMENT 'IP地址',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_dept` bigint(20) DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`log_id`),
  KEY `idx_dh_audit_action_time` (`tenant_id`, `action`, `create_time`),
  KEY `idx_dh_audit_operator_time` (`tenant_id`, `operator_name`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数字人口播审计日志表';

-- 最小演示数据（用于数字人口播后台扩展页面联调）
SET @dh_backoffice_now = NOW();

INSERT INTO `dh_user_profile` (
  `user_id`, `tenant_id`, `user_name`, `phone`, `avatar`, `member_level`,
  `wallet_balance`, `total_topup`, `total_consume`, `order_count`, `status`, `register_time`,
  `del_flag`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`
) VALUES
  (
    6205520100001, '000000', '小美', '13800001234', 'https://cdn.unimed.example/avatar/u-6205520100001.png', 'VIP',
    380.10, 899.00, 518.90, 26, '0', DATE_SUB(@dh_backoffice_now, INTERVAL 50 DAY),
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 50 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 2 DAY)
  ),
  (
    6205520100002, '000000', '王总', '13900005678', 'https://cdn.unimed.example/avatar/u-6205520100002.png', 'SVIP',
    1520.00, 5998.00, 4478.00, 156, '0', DATE_SUB(@dh_backoffice_now, INTERVAL 90 DAY),
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 90 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 1 DAY)
  ),
  (
    6205520100003, '000000', '张姐', '13600009012', NULL, 'NORMAL',
    79.00, 99.00, 20.00, 1, '0', DATE_SUB(@dh_backoffice_now, INTERVAL 10 DAY),
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 10 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 1 DAY)
  ),
  (
    6205520100004, '000000', '李四', '13700003456', NULL, 'NORMAL',
    0.00, 0.00, 0.00, 0, '1', DATE_SUB(@dh_backoffice_now, INTERVAL 15 DAY),
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 15 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 5 DAY)
  );

INSERT INTO `dh_topup_ticket` (
  `ticket_id`, `tenant_id`, `user_id`, `user_name`, `amount`, `status`, `voucher_desc`, `voucher_image_ids`,
  `actual_amount`, `approved_by`, `approved_at`, `reject_reason`, `remark`,
  `del_flag`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`
) VALUES
  (
    6205520200001, '000000', 6205520100001, '小美', 300.00, 'PENDING', '微信转账截图', 'oss-voucher-001',
    NULL, NULL, NULL, NULL, NULL,
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 3 HOUR), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 3 HOUR)
  ),
  (
    6205520200002, '000000', 6205520100002, '王总', 30000.00, 'APPROVED', '企业付款凭证', 'oss-voucher-002',
    30000.00, '管理员', DATE_SUB(@dh_backoffice_now, INTERVAL 2 DAY), NULL, '已到账',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 2 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 2 DAY)
  ),
  (
    6205520200003, '000000', 6205520100003, '张姐', 99.00, 'REJECTED', '支付宝转账截图', 'oss-voucher-003',
    NULL, NULL, NULL, '凭证模糊无法核验', '请重新提交清晰凭证',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 1 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 20 HOUR)
  );

INSERT INTO `dh_wallet_log` (
  `log_id`, `tenant_id`, `user_id`, `user_name`, `type`, `amount`, `balance_after`, `related_order_id`, `operator_name`, `remark`,
  `del_flag`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`
) VALUES
  (
    6205520300001, '000000', 6205520100001, '小美', 'TOPUP', 399.00, 399.00, NULL, '系统', '微信充值',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 40 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 40 DAY)
  ),
  (
    6205520300002, '000000', 6205520100001, '小美', 'CONSUME', -19.90, 379.10, 6205510200001, '系统', '订单消费',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 2 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 2 DAY)
  ),
  (
    6205520300003, '000000', 6205520100002, '王总', 'TOPUP', 30000.00, 30000.00, NULL, '管理员', '企业付款',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 80 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 80 DAY)
  ),
  (
    6205520300004, '000000', 6205520100002, '王总', 'CONSUME', -9.90, 29990.10, 6205510200002, '系统', '订单消费',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 1 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 1 DAY)
  ),
  (
    6205520300005, '000000', 6205520100003, '张姐', 'TOPUP', 99.00, 99.00, NULL, '系统', '支付宝充值',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 9 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 9 DAY)
  );

INSERT INTO `dh_report_ticket` (
  `report_id`, `tenant_id`, `reporter_name`, `target_user_id`, `target_user_name`,
  `target_content_id`, `target_content_type`, `type`, `description`, `status`,
  `handler_name`, `handle_result`, `handle_time`,
  `del_flag`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`
) VALUES
  (
    6205520400001, '000000', '用户A', 6205520100004, '李四',
    'ORD-2001', 'ORDER', 'PORTRAIT_RIGHTS', '该订单使用了他人照片，未经授权', 'PENDING',
    NULL, NULL, NULL,
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 1 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 1 DAY)
  ),
  (
    6205520400002, '000000', '用户B', NULL, '赵六',
    'ORD-2002', 'ORDER', 'FRAUD', '文案内容涉嫌虚假宣传', 'CONFIRMED',
    '管理员', '确认违规，已下架处理', DATE_SUB(@dh_backoffice_now, INTERVAL 2 DAY),
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 3 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 2 DAY)
  );

INSERT INTO `dh_member_config` (
  `config_id`, `tenant_id`, `level`, `level_name`, `order_price`, `monthly_limit`, `speed_priority`,
  `min_topup_amount`, `validity_days`, `expect_delivery_hours`, `redo_limit`, `status`, `remark`,
  `del_flag`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`
) VALUES
  (
    6205520500001, '000000', 'NORMAL', '普通会员', 29.90, 20, 10,
    0.00, 30, 24, 1, '0', '默认基础等级',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 30 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 30 DAY)
  ),
  (
    6205520500002, '000000', 'VIP', '高级会员', 19.90, 80, 50,
    500.00, 90, 12, 2, '0', '享受加速处理',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 30 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 30 DAY)
  ),
  (
    6205520500003, '000000', 'SVIP', '旗舰会员', 9.90, 300, 90,
    2000.00, 365, 4, 5, '0', '最高优先级',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 30 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 30 DAY)
  );

INSERT INTO `dh_payment_price_config` (
  `config_id`, `tenant_id`, `config_name`, `member_level`, `pay_type`, `amount`, `bonus_amount`, `sort`, `status`, `remark`,
  `del_flag`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`
) VALUES
  (
    6205520600001, '000000', '微信通用 100', 'ALL', 'WECHAT_QR', 100.00, 0.00, 10, '0', '默认对外档位',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 20 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 20 DAY)
  ),
  (
    6205520600002, '000000', '微信 VIP 500', 'VIP', 'WECHAT_QR', 500.00, 50.00, 20, '0', 'VIP专项活动',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 20 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 20 DAY)
  ),
  (
    6205520600003, '000000', '支付宝 SVIP 1000', 'SVIP', 'ALIPAY_QR', 1000.00, 150.00, 30, '0', 'SVIP专享',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 20 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 20 DAY)
  );

INSERT INTO `dh_video_upload_config` (
  `config_id`, `tenant_id`, `config_name`, `type`, `video_file_ids`, `max_size_mb`, `format_desc`, `status`, `remark`,
  `del_flag`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`
) VALUES
  (
    6205520700001, '000000', '默认视频上传配置', 'OSS', '', 200, 'mp4/mov', '0', '运营默认配置',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 18 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 18 DAY)
  ),
  (
    6205520700002, '000000', '第三方回传配置', 'THIRD_PARTY', '', 500, 'mp4', '1', '对接第三方上传',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 18 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 18 DAY)
  );

INSERT INTO `dh_qr_upload_config` (
  `config_id`, `tenant_id`, `config_name`, `type`, `qr_image_ids`, `account_name`, `account_no`, `bank_name`, `status`, `remark`,
  `del_flag`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`
) VALUES
  (
    6205520800001, '000000', '微信收款码-默认', 'WECHAT', 'https://cdn.unimed.example/dh/qr/wechat-default.png',
    '杭州联医科技有限公司', 'wx-unimed-default', NULL, '0', '主微信收款',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 16 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 16 DAY)
  ),
  (
    6205520800002, '000000', '对公银行卡-备用', 'BANK_CARD', 'https://cdn.unimed.example/dh/qr/bank-default.png',
    '杭州联医科技有限公司', '6222020202020202', '招商银行杭州分行', '1', '备用对公账号',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 16 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 16 DAY)
  );

INSERT INTO `dh_sensitive_word` (
  `word_id`, `tenant_id`, `word`, `level`, `category`, `status`,
  `del_flag`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`
) VALUES
  (
    6205520900001, '000000', '赌博', 'FORBIDDEN', 'FRAUD', '0',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 15 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 15 DAY)
  ),
  (
    6205520900002, '000000', '代孕', 'HIGH_RISK', 'OTHER', '0',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 15 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 15 DAY)
  ),
  (
    6205520900003, '000000', '最低价', 'WARNING', 'FRAUD', '0',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 15 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 15 DAY)
  ),
  (
    6205520900004, '000000', '国家领导', 'FORBIDDEN', 'POLITICAL', '0',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 15 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 15 DAY)
  );

INSERT INTO `dh_notify_template` (
  `template_id`, `tenant_id`, `template_name`, `scene`, `channel`, `content`, `timeout_hours`, `status`,
  `del_flag`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`
) VALUES
  (
    6205521000001, '000000', '订单完成通知', 'ORDER_COMPLETED', 'SITE', '您的订单 {{orderNo}} 已完成，请前往创作中心查看。', NULL, '0',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 14 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 14 DAY)
  ),
  (
    6205521000002, '000000', '充值确认通知', 'TOPUP_CONFIRMED', 'SITE', '您的充值 ¥{{amount}} 已确认到账，当前余额 ¥{{balance}}。', NULL, '0',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 14 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 14 DAY)
  ),
  (
    6205521000003, '000000', '充值补充通知', 'TOPUP_NEED_MORE', 'SMS', '您的充值工单需要补充信息，请登录平台查看。', NULL, '0',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 14 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 14 DAY)
  ),
  (
    6205521000004, '000000', '订单超时预警', 'ORDER_TIMEOUT', 'SITE', '订单 {{orderNo}} 已超时 {{hours}} 小时，请尽快处理。', 24, '0',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 14 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 14 DAY)
  ),
  (
    6205521000005, '000000', '重做请求通知', 'REDO_REQUEST', 'SITE', '用户 {{userName}} 对订单 {{orderNo}} 提交了重做申请，原因：{{reason}}。', NULL, '0',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 14 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 14 DAY)
  );

INSERT INTO `dh_audit_log` (
  `log_id`, `tenant_id`, `action`, `operator_name`, `target_type`, `target_id`, `detail`, `ip_address`,
  `del_flag`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`
) VALUES
  (
    6205521100001, '000000', 'TOPUP_APPROVE', '管理员', 'TOPUP', '6205520200002', '确认充值 ¥30000', '192.168.1.100',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 2 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 2 DAY)
  ),
  (
    6205521100002, '000000', 'USER_DISABLE', '管理员', 'USER', '6205520100004', '禁用用户 李四，原因：违规操作', '192.168.1.100',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 5 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 5 DAY)
  ),
  (
    6205521100003, '000000', 'BALANCE_ADJUST', '管理员', 'USER', '6205520100001', '调整余额 +100，原因：补偿', '192.168.1.101',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 1 DAY), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 1 DAY)
  ),
  (
    6205521100004, '000000', 'ORDER_REJECT', '运营主管', 'ORDER', '6205510200001', '拒绝订单：素材涉嫌侵权', '192.168.1.102',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 20 HOUR), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 20 HOUR)
  ),
  (
    6205521100005, '000000', 'USER_PUNISH', '风控专员', 'USER', '6205520100004', '处罚用户 李四：BAN，原因：多次违规', '192.168.1.103',
    '0', 103, 1, DATE_SUB(@dh_backoffice_now, INTERVAL 12 HOUR), 1, DATE_SUB(@dh_backoffice_now, INTERVAL 12 HOUR)
  );
