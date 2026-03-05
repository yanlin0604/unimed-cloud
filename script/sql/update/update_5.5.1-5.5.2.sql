-- 数字人口播核心链路表结构

CREATE TABLE `dh_order` (
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
  `order_no` varchar(64) NOT NULL COMMENT '订单号',
  `title` varchar(200) NOT NULL COMMENT '订单标题',
  `applicant_name` varchar(64) NOT NULL COMMENT '申请人',
  `member_level` varchar(16) NOT NULL COMMENT '会员等级',
  `status` varchar(16) NOT NULL COMMENT '订单状态',
  `is_redo` tinyint(1) DEFAULT 0 COMMENT '是否重做',
  `priority` int(11) DEFAULT 0 COMMENT '优先级',
  `assignee_name` varchar(64) DEFAULT NULL COMMENT '处理人',
  `expect_delivery_hours` int(11) DEFAULT 24 COMMENT '预计交付时长(小时)',
  `script_text` text COMMENT '口播文案',
  `material_summary` varchar(500) DEFAULT NULL COMMENT '素材摘要',
  `contact_info` varchar(64) DEFAULT NULL COMMENT '联系方式',
  `tone_style` varchar(64) DEFAULT NULL COMMENT '语气风格',
  `scene_type` varchar(64) DEFAULT NULL COMMENT '场景类型',
  `speech_speed` varchar(32) DEFAULT NULL COMMENT '语速',
  `order_amount` decimal(10,2) DEFAULT 0.00 COMMENT '订单金额',
  `discount_rate` decimal(5,2) DEFAULT 1.00 COMMENT '折扣率',
  `actual_amount` decimal(10,2) DEFAULT 0.00 COMMENT '实际金额',
  `copyright_declared` tinyint(1) DEFAULT 0 COMMENT '肖像权声明',
  `redo_reason` varchar(500) DEFAULT NULL COMMENT '重做原因',
  `original_order_id` bigint(20) DEFAULT NULL COMMENT '原始订单ID',
  `redo_count` int(11) DEFAULT 0 COMMENT '重做次数',
  `result_video_url` varchar(500) DEFAULT NULL COMMENT '成品视频URL',
  `cancel_reason` varchar(500) DEFAULT NULL COMMENT '取消原因',
  `reject_reason` varchar(500) DEFAULT NULL COMMENT '拒绝原因',
  `reject_type` varchar(32) DEFAULT NULL COMMENT '拒绝类型',
  `claim_time` datetime DEFAULT NULL COMMENT '领取时间',
  `completed_time` datetime DEFAULT NULL COMMENT '完成时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_dept` bigint(20) DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `uk_dh_order_no` (`tenant_id`, `order_no`),
  KEY `idx_dh_order_status_priority_time` (`tenant_id`, `status`, `priority`, `create_time`),
  KEY `idx_dh_order_applicant` (`tenant_id`, `applicant_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数字人口播订单主表';

CREATE TABLE `dh_order_material` (
  `material_id` bigint(20) NOT NULL COMMENT '素材ID',
  `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `file_id` varchar(64) NOT NULL COMMENT '文件ID',
  `file_name` varchar(255) NOT NULL COMMENT '文件名',
  `file_url` varchar(500) NOT NULL COMMENT '文件地址',
  `file_type` varchar(16) NOT NULL COMMENT '文件类型',
  `thumbnail_url` varchar(500) DEFAULT NULL COMMENT '缩略图地址',
  `sort` int(11) DEFAULT 0 COMMENT '排序',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_dept` bigint(20) DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`material_id`),
  KEY `idx_dh_order_material` (`tenant_id`, `order_id`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数字人口播订单素材表';

CREATE TABLE `dh_order_process_log` (
  `log_id` bigint(20) NOT NULL COMMENT '日志ID',
  `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `action_text` varchar(500) NOT NULL COMMENT '操作内容',
  `operator_name` varchar(64) DEFAULT NULL COMMENT '操作人',
  `operate_time` datetime NOT NULL COMMENT '操作时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_dept` bigint(20) DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`log_id`),
  KEY `idx_dh_order_log` (`tenant_id`, `order_id`, `operate_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数字人口播订单处理日志表';

CREATE TABLE `dh_order_production_asset` (
  `asset_id` bigint(20) NOT NULL COMMENT '资产ID',
  `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `generation_channel` varchar(32) NOT NULL COMMENT '生成渠道',
  `generation_ref` varchar(255) DEFAULT NULL COMMENT '生成任务标识',
  `output_video_name` varchar(255) DEFAULT NULL COMMENT '成品视频文件名',
  `output_video_url` varchar(500) DEFAULT NULL COMMENT '成品视频地址',
  `output_video_duration_sec` int(11) DEFAULT NULL COMMENT '成品时长(秒)',
  `output_video_size_mb` decimal(10,2) DEFAULT NULL COMMENT '成品大小(MB)',
  `operator_name` varchar(64) DEFAULT NULL COMMENT '操作人',
  `submitted_at` datetime DEFAULT NULL COMMENT '提交时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_dept` bigint(20) DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`asset_id`),
  UNIQUE KEY `uk_dh_order_asset` (`tenant_id`, `order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数字人口播订单生产资产表';

CREATE TABLE `dh_order_qc_snapshot` (
  `qc_id` bigint(20) NOT NULL COMMENT '质检ID',
  `tenant_id` varchar(20) DEFAULT '000000' COMMENT '租户编号',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `lip_sync` tinyint(1) DEFAULT 0 COMMENT '口型同步',
  `no_visual_defect` tinyint(1) DEFAULT 0 COMMENT '无明显瑕疵',
  `script_matched` tinyint(1) DEFAULT 0 COMMENT '文案匹配',
  `duration_ok` tinyint(1) DEFAULT 0 COMMENT '时长合理',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_dept` bigint(20) DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`qc_id`),
  UNIQUE KEY `uk_dh_order_qc` (`tenant_id`, `order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='数字人口播订单质检快照表';

-- 最小演示数据（用于数字人口播订单与生产接口联调）
SET @dh_now = NOW();

INSERT INTO `dh_order` (
  `order_id`, `tenant_id`, `order_no`, `title`, `applicant_name`, `member_level`, `status`, `is_redo`, `priority`,
  `assignee_name`, `expect_delivery_hours`, `script_text`, `material_summary`, `contact_info`, `tone_style`, `scene_type`,
  `speech_speed`, `order_amount`, `discount_rate`, `actual_amount`, `copyright_declared`,
  `redo_reason`, `original_order_id`, `redo_count`, `result_video_url`, `cancel_reason`, `reject_reason`, `reject_type`,
  `claim_time`, `completed_time`, `del_flag`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`
) VALUES
  (
    6205510200001, '000000', 'DH202603040001', '新品发布会开场播报', '王小北', 'GOLD', 'PENDING', 0, 90,
    NULL, 24, '大家好，欢迎来到本次新品发布会，接下来将为大家介绍三项核心升级。', '2 张产品图 + 1 份讲稿', '13800001111', '专业稳重', '发布会',
    '正常', 299.00, 1.00, 299.00, 1,
    NULL, NULL, 0, NULL, NULL, NULL, NULL,
    NULL, NULL, '0', 103, 1, DATE_SUB(@dh_now, INTERVAL 2 DAY), 1, DATE_SUB(@dh_now, INTERVAL 2 DAY)
  ),
  (
    6205510200002, '000000', 'DH202603040002', '直播间活动口播', '赵一宁', 'SILVER', 'PROCESSING', 0, 70,
    '运营小林', 12, '今晚八点整点抽奖，参与互动即可领取专属优惠券。', '活动海报 + 优惠券文案', '13900002222', '活泼热情', '直播间',
    '偏快', 199.00, 0.95, 189.05, 1,
    NULL, NULL, 0, NULL, NULL, NULL, NULL,
    DATE_SUB(@dh_now, INTERVAL 20 HOUR), NULL, '0', 103, 1, DATE_SUB(@dh_now, INTERVAL 1 DAY), 1, DATE_SUB(@dh_now, INTERVAL 20 HOUR)
  ),
  (
    6205510200003, '000000', 'DH202603040003', '门店促销短视频口播', '李沐晨', 'PLATINUM', 'COMPLETED', 0, 60,
    '制作阿青', 8, '本周末到店享满减优惠，会员再享双倍积分，欢迎到店咨询。', '门店实景图 + 活动海报', '13700003333', '亲和推荐', '门店促销',
    '正常', 999.00, 0.90, 899.10, 1,
    NULL, NULL, 0, 'https://cdn.unimed.example/dh/result/6205510200003.mp4', NULL, NULL, NULL,
    DATE_SUB(@dh_now, INTERVAL 10 HOUR), DATE_SUB(@dh_now, INTERVAL 4 HOUR), '0', 103, 1, DATE_SUB(@dh_now, INTERVAL 12 HOUR), 1, DATE_SUB(@dh_now, INTERVAL 4 HOUR)
  );

INSERT INTO `dh_order_material` (
  `material_id`, `tenant_id`, `order_id`, `file_id`, `file_name`, `file_url`, `file_type`, `thumbnail_url`, `sort`,
  `del_flag`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`
) VALUES
  (
    6205510300001, '000000', 6205510200001, 'FILE-DH-001', '新品主视觉.png', 'https://cdn.unimed.example/dh/material/FILE-DH-001.png', 'image',
    'https://cdn.unimed.example/dh/material/thumb/FILE-DH-001.png', 1, '0', 103, 1, DATE_SUB(@dh_now, INTERVAL 2 DAY), 1, DATE_SUB(@dh_now, INTERVAL 2 DAY)
  ),
  (
    6205510300002, '000000', 6205510200001, 'FILE-DH-002', '发布会串词.docx', 'https://cdn.unimed.example/dh/material/FILE-DH-002.docx', 'doc',
    NULL, 2, '0', 103, 1, DATE_SUB(@dh_now, INTERVAL 2 DAY), 1, DATE_SUB(@dh_now, INTERVAL 2 DAY)
  ),
  (
    6205510300003, '000000', 6205510200002, 'FILE-DH-003', '直播活动海报.jpg', 'https://cdn.unimed.example/dh/material/FILE-DH-003.jpg', 'image',
    'https://cdn.unimed.example/dh/material/thumb/FILE-DH-003.jpg', 1, '0', 103, 1, DATE_SUB(@dh_now, INTERVAL 1 DAY), 1, DATE_SUB(@dh_now, INTERVAL 1 DAY)
  ),
  (
    6205510300004, '000000', 6205510200003, 'FILE-DH-004', '门店促销素材包.zip', 'https://cdn.unimed.example/dh/material/FILE-DH-004.zip', 'zip',
    NULL, 1, '0', 103, 1, DATE_SUB(@dh_now, INTERVAL 12 HOUR), 1, DATE_SUB(@dh_now, INTERVAL 12 HOUR)
  );

INSERT INTO `dh_order_process_log` (
  `log_id`, `tenant_id`, `order_id`, `action_text`, `operator_name`, `operate_time`,
  `del_flag`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`
) VALUES
  (
    6205510400001, '000000', 6205510200002, '运营领取订单', '运营小林', DATE_SUB(@dh_now, INTERVAL 20 HOUR),
    '0', 103, 1, DATE_SUB(@dh_now, INTERVAL 20 HOUR), 1, DATE_SUB(@dh_now, INTERVAL 20 HOUR)
  ),
  (
    6205510400002, '000000', 6205510200002, '开始视频制作', '运营小林', DATE_SUB(@dh_now, INTERVAL 19 HOUR),
    '0', 103, 1, DATE_SUB(@dh_now, INTERVAL 19 HOUR), 1, DATE_SUB(@dh_now, INTERVAL 19 HOUR)
  ),
  (
    6205510400003, '000000', 6205510200003, '运营领取订单', '制作阿青', DATE_SUB(@dh_now, INTERVAL 10 HOUR),
    '0', 103, 1, DATE_SUB(@dh_now, INTERVAL 10 HOUR), 1, DATE_SUB(@dh_now, INTERVAL 10 HOUR)
  ),
  (
    6205510400004, '000000', 6205510200003, '开始视频制作', '制作阿青', DATE_SUB(@dh_now, INTERVAL 9 HOUR),
    '0', 103, 1, DATE_SUB(@dh_now, INTERVAL 9 HOUR), 1, DATE_SUB(@dh_now, INTERVAL 9 HOUR)
  ),
  (
    6205510400005, '000000', 6205510200003, '上传成品视频', '制作阿青', DATE_SUB(@dh_now, INTERVAL 5 HOUR),
    '0', 103, 1, DATE_SUB(@dh_now, INTERVAL 5 HOUR), 1, DATE_SUB(@dh_now, INTERVAL 5 HOUR)
  ),
  (
    6205510400006, '000000', 6205510200003, '提交交付成功', '制作阿青', DATE_SUB(@dh_now, INTERVAL 4 HOUR),
    '0', 103, 1, DATE_SUB(@dh_now, INTERVAL 4 HOUR), 1, DATE_SUB(@dh_now, INTERVAL 4 HOUR)
  );

INSERT INTO `dh_order_production_asset` (
  `asset_id`, `tenant_id`, `order_id`, `generation_channel`, `generation_ref`,
  `output_video_name`, `output_video_url`, `output_video_duration_sec`, `output_video_size_mb`,
  `operator_name`, `submitted_at`, `remark`,
  `del_flag`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`
) VALUES
  (
    6205510500001, '000000', 6205510200002, 'THIRD_PARTY_MANUAL', 'TASK-6205510200002',
    NULL, NULL, NULL, NULL,
    '运营小林', NULL, '制作进行中，等待成片上传',
    '0', 103, 1, DATE_SUB(@dh_now, INTERVAL 19 HOUR), 1, DATE_SUB(@dh_now, INTERVAL 19 HOUR)
  ),
  (
    6205510500002, '000000', 6205510200003, 'THIRD_PARTY_MANUAL', 'TASK-6205510200003',
    '门店促销成片.mp4', 'https://cdn.unimed.example/dh/result/6205510200003.mp4', 45, 18.60,
    '制作阿青', DATE_SUB(@dh_now, INTERVAL 4 HOUR), '已完成自动质检，内容通过',
    '0', 103, 1, DATE_SUB(@dh_now, INTERVAL 10 HOUR), 1, DATE_SUB(@dh_now, INTERVAL 4 HOUR)
  );

INSERT INTO `dh_order_qc_snapshot` (
  `qc_id`, `tenant_id`, `order_id`, `lip_sync`, `no_visual_defect`, `script_matched`, `duration_ok`,
  `del_flag`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`
) VALUES
  (
    6205510600001, '000000', 6205510200003, 1, 1, 1, 1,
    '0', 103, 1, DATE_SUB(@dh_now, INTERVAL 4 HOUR), 1, DATE_SUB(@dh_now, INTERVAL 4 HOUR)
  );
