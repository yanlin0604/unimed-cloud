-- 患者用药打卡持久化与首页统计
-- 适用于 unimed-chronic 数据库

CREATE TABLE IF NOT EXISTS `ch_medication_checkin` (
  `checkin_id` bigint NOT NULL COMMENT '打卡ID',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `med_id` bigint NOT NULL COMMENT '用药记录ID',
  `checkin_date` date NOT NULL COMMENT '打卡自然日',
  `first_checkin_time` datetime NOT NULL COMMENT '首次打卡时间',
  `last_checkin_time` datetime NOT NULL COMMENT '最近打卡时间',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `tenant_id` varchar(20) NOT NULL DEFAULT '000000' COMMENT '租户ID',
  PRIMARY KEY (`checkin_id`),
  UNIQUE KEY `uk_medication_checkin_day` (`tenant_id`, `patient_id`, `med_id`, `checkin_date`),
  KEY `idx_medication_checkin_patient_date` (`tenant_id`, `patient_id`, `checkin_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='患者用药打卡明细';
