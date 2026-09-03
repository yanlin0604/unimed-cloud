-- -------------------------------------------------------------
-- 慢病系统三端招标书功能补齐底层支撑表与字段更新
-- 执行方式: 通过 dynamic-db MCP 在 unimed-chronic 库执行
-- -------------------------------------------------------------

-- 1. 肿瘤专病专项档案表
CREATE TABLE IF NOT EXISTS `ch_tumor_record` (
  `id` bigint NOT NULL COMMENT '主键',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `cancer_type` varchar(50) NOT NULL COMMENT '肿瘤类型(LUNG/COLORECTAL/GASTRIC/BREAST/OTHER)',
  `tnm_stage` varchar(20) DEFAULT NULL COMMENT 'TNM分期',
  `pathology_result` varchar(500) DEFAULT NULL COMMENT '病理诊断',
  `surgery_date` date DEFAULT NULL COMMENT '手术日期',
  `chemo_status` varchar(20) DEFAULT NULL COMMENT '放化疗状态',
  `cea_value` decimal(8,2) DEFAULT NULL COMMENT '癌胚抗原CEA(ng/mL)',
  `afp_value` decimal(8,2) DEFAULT NULL COMMENT '甲胎蛋白AFP(ng/mL)',
  `high_risk_factors` varchar(500) DEFAULT NULL COMMENT '高危因素',
  `next_review_date` date DEFAULT NULL COMMENT '下次复查日期',
  `tenant_id` varchar(20) DEFAULT '000000',
  `del_flag` char(1) DEFAULT '0',
  `create_by` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_patient_tumor` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='肿瘤专病专项档案';

-- 2. AI智能随访外呼调度表
CREATE TABLE IF NOT EXISTS `ch_ai_call_task` (
  `task_id` bigint NOT NULL COMMENT '外呼任务ID',
  `plan_id` bigint DEFAULT NULL COMMENT '随访计划ID',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `patient_phone` varchar(20) NOT NULL COMMENT '外呼电话',
  `disease_code` varchar(30) NOT NULL COMMENT '专病类型',
  `call_priority` int DEFAULT '1' COMMENT '优先级(1-5)',
  `call_status` varchar(20) DEFAULT 'PENDING' COMMENT '状态(PENDING/CALLING/SUCCESS/FAILED/REFUSED)',
  `audio_record_url` varchar(500) DEFAULT NULL COMMENT '录音文件URL',
  `transcript_text` text COMMENT 'AI外呼全通语音转写文本',
  `extracted_metrics` json DEFAULT NULL COMMENT '从语音中结构化抽取的指标(血压/血糖/症状)',
  `patient_feedback` varchar(500) DEFAULT NULL COMMENT '患者主诉与反馈小结',
  `tenant_id` varchar(20) DEFAULT '000000',
  `del_flag` char(1) DEFAULT '0',
  `create_by` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`task_id`),
  KEY `idx_call_patient` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI智能随访外呼任务';

-- 3. 患者意见反馈表
CREATE TABLE IF NOT EXISTS `ch_patient_feedback` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `feedback_type` varchar(30) NOT NULL COMMENT '类型(SUGGESTION/COMPLAINT/BUG/CONSULT)',
  `content` text NOT NULL COMMENT '反馈内容',
  `contact_phone` varchar(30) DEFAULT NULL COMMENT '联系电话',
  `images` varchar(1000) DEFAULT NULL COMMENT '截图OSS地址,逗号分隔',
  `reply_status` varchar(20) DEFAULT 'PENDING' COMMENT '处理状态(PENDING/PROCESSED)',
  `reply_content` text COMMENT '管理员回复内容',
  `tenant_id` varchar(20) DEFAULT '000000',
  `del_flag` char(1) DEFAULT '0',
  `create_by` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者意见反馈';

-- 4. 门诊与慢病结构化处方表
CREATE TABLE IF NOT EXISTS `ch_prescription` (
  `id` bigint NOT NULL COMMENT '处方ID',
  `prescription_no` varchar(64) NOT NULL COMMENT '处方单编号',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `doctor_user_id` bigint NOT NULL COMMENT '开方医生ID',
  `encounter_id` bigint DEFAULT NULL COMMENT '关联诊疗记录ID',
  `diagnosis_name` varchar(200) NOT NULL COMMENT '临床诊断',
  `prescription_items` json NOT NULL COMMENT '处方药品明细JSON(名称/规格/用法/用量/天数/金额)',
  `total_amount` decimal(10,2) DEFAULT '0.00' COMMENT '处方总金额',
  `status` varchar(20) DEFAULT 'ISSUED' COMMENT '状态(ISSUED已开立/DISPENSED已调配/CANCELLED已作废)',
  `tenant_id` varchar(20) DEFAULT '000000',
  `del_flag` char(1) DEFAULT '0',
  `create_by` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_prescription_no` (`prescription_no`),
  KEY `idx_patient_prescription` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门诊与慢病结构化处方';

-- 5. 国家标准药品库表
CREATE TABLE IF NOT EXISTS `ch_standard_drug` (
  `id` bigint NOT NULL COMMENT '药品ID',
  `drug_code` varchar(64) NOT NULL COMMENT '国家医保药品代码',
  `drug_name` varchar(100) NOT NULL COMMENT '通用名称',
  `trade_name` varchar(100) DEFAULT NULL COMMENT '商品名称',
  `specification` varchar(100) NOT NULL COMMENT '规格',
  `dosage_form` varchar(50) NOT NULL COMMENT '剂型',
  `manufacturer` varchar(200) DEFAULT NULL COMMENT '生产厂家',
  `insurance_type` varchar(20) DEFAULT 'A' COMMENT '医保甲乙类(A/B/C)',
  `standard_price` decimal(10,2) DEFAULT '0.00' COMMENT '参考单价',
  `usage_method` varchar(50) DEFAULT NULL COMMENT '给药途径',
  `disease_code` varchar(50) DEFAULT NULL COMMENT '适配专病类型',
  `is_common_template` char(1) DEFAULT '0' COMMENT '是否慢病常用模板(0否 1是)',
  `tenant_id` varchar(20) DEFAULT '000000',
  `del_flag` char(1) DEFAULT '0',
  `create_by` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_drug_code` (`drug_code`),
  KEY `idx_drug_name` (`drug_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='国家标准药品库';

-- 6. 医生端入驻执业资质审核表
CREATE TABLE IF NOT EXISTS `ch_doctor_qualification` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `doctor_user_id` bigint NOT NULL COMMENT '医生用户ID',
  `doctor_name` varchar(50) NOT NULL COMMENT '医生姓名',
  `id_card` varchar(20) DEFAULT NULL COMMENT '身份证号',
  `org_name` varchar(100) NOT NULL COMMENT '执业机构名称',
  `dept_name` varchar(50) NOT NULL COMMENT '所属科室',
  `title` varchar(50) DEFAULT NULL COMMENT '职称',
  `certificate_no` varchar(50) NOT NULL COMMENT '医师执业证书编码',
  `certificate_images` varchar(1000) DEFAULT NULL COMMENT '证件图片URL,逗号分隔',
  `audit_status` varchar(20) DEFAULT 'PENDING' COMMENT '审核状态(PENDING待审/APPROVED通过/REJECTED驳回)',
  `audit_opinion` varchar(500) DEFAULT NULL COMMENT '审核意见',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `tenant_id` varchar(20) DEFAULT '000000',
  `del_flag` char(1) DEFAULT '0',
  `create_by` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_doctor_user` (`doctor_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生端入驻执业资质审核';

-- 7. 医生与团队绩效考核表
CREATE TABLE IF NOT EXISTS `ch_performance_eval` (
  `id` bigint NOT NULL COMMENT '考核ID',
  `doctor_user_id` bigint NOT NULL COMMENT '医生用户ID',
  `doctor_name` varchar(50) NOT NULL COMMENT '医生姓名',
  `team_id` bigint DEFAULT NULL COMMENT '所属团队ID',
  `stat_month` varchar(10) NOT NULL COMMENT '考核月份(YYYY-MM)',
  `contract_count` int DEFAULT '0' COMMENT '期末有效签约数',
  `followup_count` int DEFAULT '0' COMMENT '规范随访完成数',
  `alert_handle_count` int DEFAULT '0' COMMENT '预警处置完成数',
  `consultation_count` int DEFAULT '0' COMMENT '线上接诊咨询数',
  `bp_control_rate` decimal(5,2) DEFAULT '0.00' COMMENT '高血压血压控制达标率(%)',
  `fbg_control_rate` decimal(5,2) DEFAULT '0.00' COMMENT '糖尿病血糖控制达标率(%)',
  `composite_score` decimal(6,2) DEFAULT '0.00' COMMENT '加权综合绩效得分',
  `grade_level` varchar(10) DEFAULT 'B' COMMENT '考核等级(A/B/C/D)',
  `tenant_id` varchar(20) DEFAULT '000000',
  `del_flag` char(1) DEFAULT '0',
  `create_by` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_doc_month` (`doctor_user_id`,`stat_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生与团队绩效考核';

-- 8. 患者主表补充删除归档原因列
-- ALTER TABLE `ch_patient_profile` ADD COLUMN `deletion_reason` varchar(200) DEFAULT NULL COMMENT '删除/归档原因' AFTER `del_flag`;
