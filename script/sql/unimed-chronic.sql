-- =============================================
-- unimed-chronic 慢病管理数据库 DDL
-- 共 62 张业务表，表名统一 ch_ 前缀
-- =============================================

CREATE DATABASE IF NOT EXISTS `unimed-chronic` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `unimed-chronic`;

-- ----------------------------
-- 1. 患者档案表
-- ----------------------------
CREATE TABLE `ch_patient_profile` (
  `patient_id`       BIGINT        NOT NULL AUTO_INCREMENT COMMENT '患者ID',
  `name`             VARCHAR(50)   DEFAULT NULL COMMENT '姓名',
  `id_card`          VARCHAR(18)   DEFAULT NULL COMMENT '身份证号',
  `gender`           CHAR(1)       DEFAULT NULL COMMENT '性别',
  `birthday`         DATE          DEFAULT NULL COMMENT '出生日期',
  `phone`            VARCHAR(20)   DEFAULT NULL COMMENT '联系电话',
  `address`          VARCHAR(200)  DEFAULT NULL COMMENT '居住地址',
  `gis_lng`          DECIMAL(10,6) DEFAULT NULL COMMENT '经度',
  `gis_lat`          DECIMAL(10,6) DEFAULT NULL COMMENT '纬度',
  `nation`           VARCHAR(20)   DEFAULT NULL COMMENT '民族',
  `occupation`       VARCHAR(50)   DEFAULT NULL COMMENT '职业',
  `education_level`  VARCHAR(20)   DEFAULT NULL COMMENT '文化程度',
  `surgery_history`  TEXT          DEFAULT NULL COMMENT '手术史',
  `trauma_history`   TEXT          DEFAULT NULL COMMENT '外伤史',
  `transfusion_history` TEXT       DEFAULT NULL COMMENT '输血史',
  `genetic_history`  TEXT          DEFAULT NULL COMMENT '遗传史',
  `disability_type`  VARCHAR(50)   DEFAULT NULL COMMENT '残疾类型',
  `disability_level` VARCHAR(20)   DEFAULT NULL COMMENT '残疾等级',
  `assistive_device` VARCHAR(100)  DEFAULT NULL COMMENT '辅助器具',
  `smoking_index`    INT           DEFAULT NULL COMMENT '吸烟指数',
  `drinking_amount`  VARCHAR(50)   DEFAULT NULL COMMENT '饮酒量',
  `org_id`           BIGINT        DEFAULT NULL COMMENT '管理机构ID',
  `dept_id`          BIGINT        DEFAULT NULL COMMENT '管理科室ID',
  `doctor_user_id`   BIGINT        DEFAULT NULL COMMENT '责任医生用户ID',
  `manage_status`    VARCHAR(20)   DEFAULT NULL COMMENT '管理状态(PENDING_ENTRY/MANAGED/FOLLOWUP_OVERDUE/WARNING_ACTIVE/REFERRING/PAUSED/CLOSED)',
  `source`           VARCHAR(20)   DEFAULT NULL COMMENT '来源(OUTPATIENT/SCREENING/HIS_SYNC/TRANSFER)',
  `tenant_id`        BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`        BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`      DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`        BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`      DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`         CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`patient_id`),
  INDEX `idx_patient_org_id` (`org_id`),
  INDEX `idx_patient_tenant_id` (`tenant_id`),
  INDEX `idx_patient_tenant_org` (`tenant_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者档案表';

-- ----------------------------
-- 2. 患者疾病关联表
-- ----------------------------
CREATE TABLE `ch_patient_disease` (
  `id`                  BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `patient_id`          BIGINT        DEFAULT NULL COMMENT '患者ID',
  `disease_code`        VARCHAR(32)   DEFAULT NULL COMMENT '疾病编码',
  `icd_code`            VARCHAR(20)   DEFAULT NULL COMMENT 'ICD编码',
  `diagnosis_basis`     TEXT          DEFAULT NULL COMMENT '诊断依据',
  `confirm_date`        DATE          DEFAULT NULL COMMENT '确诊日期',
  `is_complication`     TINYINT(1)    DEFAULT NULL COMMENT '是否并发症',
  `parent_disease_code` VARCHAR(32)   DEFAULT NULL COMMENT '父级疾病编码',
  `org_id`              BIGINT        DEFAULT NULL COMMENT '机构ID',
  `tenant_id`           BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`           BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`         DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`           BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`         DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`            CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_pd_patient_id` (`patient_id`),
  INDEX `idx_pd_tenant_id` (`tenant_id`),
  INDEX `idx_pd_tenant_org_disease` (`tenant_id`, `org_id`, `disease_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者疾病关联表';

-- ----------------------------
-- 3. 患者标签表
-- ----------------------------
CREATE TABLE `ch_patient_tag` (
  `id`         BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `patient_id` BIGINT        DEFAULT NULL COMMENT '患者ID',
  `tag_type`   VARCHAR(20)   DEFAULT NULL COMMENT '标签类型(RISK/CUSTOM/COMORBIDITY)',
  `tag_value`  VARCHAR(100)  DEFAULT NULL COMMENT '标签值',
  `tenant_id`  BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`  BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME     DEFAULT NULL COMMENT '创建时间',
  `update_by`  BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME     DEFAULT NULL COMMENT '更新时间',
  `del_flag`   CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_pt_patient_id` (`patient_id`),
  INDEX `idx_pt_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者标签表';

-- ----------------------------
-- 4. 患者时间线表
-- ----------------------------
CREATE TABLE `ch_patient_timeline` (
  `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `patient_id`   BIGINT        DEFAULT NULL COMMENT '患者ID',
  `event_type`   VARCHAR(30)   DEFAULT NULL COMMENT '事件类型(ARCHIVE/SIGN/FOLLOWUP/MEDICATION_ADJUST/WARNING/REFERRAL/PLAN_CHANGE)',
  `event_title`  VARCHAR(100)  DEFAULT NULL COMMENT '事件标题',
  `event_detail` TEXT          DEFAULT NULL COMMENT '事件详情',
  `event_time`   DATETIME      DEFAULT NULL COMMENT '事件时间',
  `tenant_id`    BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`    BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`  DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`    BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`  DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`     CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_ptl_patient_id` (`patient_id`),
  INDEX `idx_ptl_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者时间线表';

-- ----------------------------
-- 5. 疾病配置表
-- ----------------------------
CREATE TABLE `ch_disease_config` (
  `config_id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `disease_code`           VARCHAR(32)   DEFAULT NULL COMMENT '疾病编码',
  `disease_name`           VARCHAR(100)  DEFAULT NULL COMMENT '疾病名称',
  `disease_category`       VARCHAR(50)   DEFAULT NULL COMMENT '疾病分类',
  `is_primary`             TINYINT(1)    DEFAULT NULL COMMENT '是否主病种',
  `parent_disease_code`    VARCHAR(32)   DEFAULT NULL COMMENT '父级疾病编码',
  `followup_template_id`   BIGINT        DEFAULT NULL COMMENT '随访模板ID',
  `assessment_strategy_id` BIGINT        DEFAULT NULL COMMENT '评估策略ID',
  `monitor_items`          JSON          DEFAULT NULL COMMENT '监测项目',
  `is_active`              TINYINT(1)    DEFAULT 1 COMMENT '是否启用',
`org_id`            BIGINT        DEFAULT NULL COMMENT '机构ID',
  `tenant_id`         BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`              BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`            DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`              BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`            DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`               CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`config_id`),
  UNIQUE INDEX `uk_disease_code` (`disease_code`),
  INDEX `idx_dc_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='疾病配置表';

-- ----------------------------
-- 6. 疾病关联关系表
-- ----------------------------
CREATE TABLE `ch_disease_relation` (
  `id`                       BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_disease_code`      VARCHAR(32)   DEFAULT NULL COMMENT '父级疾病编码',
  `complication_disease_code` VARCHAR(32)  DEFAULT NULL COMMENT '并发症疾病编码',
  `relation_type`            VARCHAR(30)   DEFAULT NULL COMMENT '关联类型',
  `is_active`                TINYINT(1)    DEFAULT 1 COMMENT '是否启用',
  `tenant_id`                BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`                BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`              DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`                BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`              DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`                 CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_dr_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='疾病关联关系表';

-- ----------------------------
-- 7. ICD字典表
-- ----------------------------
CREATE TABLE `ch_icd_dict` (
  `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `icd_code`     VARCHAR(20)   DEFAULT NULL COMMENT 'ICD编码',
  `icd_version`  VARCHAR(10)   DEFAULT NULL COMMENT 'ICD版本(ICD10/ICD11)',
  `icd_name_cn`  VARCHAR(200)  DEFAULT NULL COMMENT 'ICD中文名称',
  `icd_name_en`  VARCHAR(200)  DEFAULT NULL COMMENT 'ICD英文名称',
  `category`     VARCHAR(50)   DEFAULT NULL COMMENT '分类',
  `tenant_id`    BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`    BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`  DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`    BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`  DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`     CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_icd_code` (`icd_code`),
  INDEX `idx_icd_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ICD字典表';

-- ----------------------------
-- 8. 筛查批次表
-- ----------------------------
CREATE TABLE `ch_screening_batch` (
  `batch_id`        BIGINT        NOT NULL AUTO_INCREMENT COMMENT '批次ID',
  `batch_name`      VARCHAR(100)  DEFAULT NULL COMMENT '批次名称',
  `activity_date`   DATE          DEFAULT NULL COMMENT '活动日期',
  `org_id`          BIGINT        DEFAULT NULL COMMENT '机构ID',
  `doctor_user_id`  BIGINT        DEFAULT NULL COMMENT '医生用户ID',
  `location`        VARCHAR(200)  DEFAULT NULL COMMENT '筛查地点',
  `notes`           TEXT          DEFAULT NULL COMMENT '备注',
  `tenant_id`       BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`       BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`     DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`       BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`     DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`        CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`batch_id`),
  INDEX `idx_sb_org_id` (`org_id`),
  INDEX `idx_sb_tenant_id` (`tenant_id`),
  INDEX `idx_sb_tenant_org` (`tenant_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='筛查批次表';

-- ----------------------------
-- 9. 筛查记录表
-- ----------------------------
CREATE TABLE `ch_screening_record` (
  `record_id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `batch_id`            BIGINT        DEFAULT NULL COMMENT '批次ID',
  `offline_uuid`        VARCHAR(64)   DEFAULT NULL COMMENT '离线唯一标识',
  `patient_name`        VARCHAR(50)   DEFAULT NULL COMMENT '患者姓名',
  `id_card`             VARCHAR(18)   DEFAULT NULL COMMENT '身份证号',
  `phone`               VARCHAR(20)   DEFAULT NULL COMMENT '联系电话',
  `gender`              CHAR(1)       DEFAULT NULL COMMENT '性别',
  `age`                 INT           DEFAULT NULL COMMENT '年龄',
  `symptoms`            JSON          DEFAULT NULL COMMENT '症状',
  `vitals`              JSON          DEFAULT NULL COMMENT '体征',
  `risk_level`          VARCHAR(20)   DEFAULT NULL COMMENT '风险等级(LOW/MEDIUM/HIGH/VERY_HIGH)',
  `enroll_status`       VARCHAR(20)   DEFAULT NULL COMMENT '入组状态(PENDING/ENROLLED/REJECTED)',
  `enrolled_patient_id` BIGINT        DEFAULT NULL COMMENT '入组患者ID',
  `tenant_id`           BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`           BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`         DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`           BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`         DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`            CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`record_id`),
  UNIQUE INDEX `uk_offline_uuid` (`offline_uuid`),
  INDEX `idx_sr_batch_id` (`batch_id`),
  INDEX `idx_sr_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='筛查记录表';

-- ----------------------------
-- 10. 患者签约表
-- ----------------------------
CREATE TABLE `ch_patient_contract` (
  `contract_id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '签约ID',
  `patient_id`           BIGINT        DEFAULT NULL COMMENT '患者ID',
  `team_id`              BIGINT        DEFAULT NULL COMMENT '团队ID',
  `package_id`           BIGINT        DEFAULT NULL COMMENT '服务包ID',
  `contract_type`        VARCHAR(30)   DEFAULT NULL COMMENT '签约类型',
  `contract_period_start` DATE         DEFAULT NULL COMMENT '签约开始日期',
  `contract_period_end`  DATE          DEFAULT NULL COMMENT '签约结束日期',
  `renewal_status`       VARCHAR(20)   DEFAULT NULL COMMENT '续约状态(ACTIVE/EXPIRING/EXPIRED/RENEWED)',
  `expiry_remind_status` TINYINT(1)    DEFAULT 0 COMMENT '到期提醒状态',
  `tenant_id`            BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`            BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`          DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`            BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`          DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`             CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`contract_id`),
  INDEX `idx_pc_patient_id` (`patient_id`),
  INDEX `idx_pc_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者签约表';

-- ----------------------------
-- 11. 签约服务包表
-- ----------------------------
CREATE TABLE `ch_contract_service_package` (
  `package_id`    BIGINT        NOT NULL AUTO_INCREMENT COMMENT '服务包ID',
  `package_name`  VARCHAR(100)  DEFAULT NULL COMMENT '服务包名称',
  `package_type`  VARCHAR(20)   DEFAULT NULL COMMENT '服务包类型(BASIC/ADVANCED/CUSTOM)',
  `service_items` JSON          DEFAULT NULL COMMENT '服务项目',
  `price`         DECIMAL(10,2) DEFAULT NULL COMMENT '价格',
  `tenant_id`     BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`     BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`   DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`     BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`   DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`      CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`package_id`),
  INDEX `idx_csp_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签约服务包表';

-- ----------------------------
-- 12. 签约履约表
-- ----------------------------
CREATE TABLE `ch_contract_fulfillment` (
  `id`                BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `contract_id`       BIGINT        DEFAULT NULL COMMENT '签约ID',
  `service_item`      VARCHAR(100)  DEFAULT NULL COMMENT '服务项目',
  `plan_date`         DATE          DEFAULT NULL COMMENT '计划日期',
  `actual_date`       DATE          DEFAULT NULL COMMENT '实际日期',
  `fulfillment_status` VARCHAR(20)  DEFAULT NULL COMMENT '履约状态(PLANNED/DONE/MISSED)',
  `sla_violation`     TINYINT(1)    DEFAULT 0 COMMENT '是否SLA违约',
  `tenant_id`         BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`         BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`       DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`         BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`       DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`          CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_cf_contract_id` (`contract_id`),
  INDEX `idx_cf_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签约履约表';

-- ----------------------------
-- 13. 医生团队表
-- ----------------------------
CREATE TABLE `ch_doctor_team` (
  `team_id`        BIGINT        NOT NULL AUTO_INCREMENT COMMENT '团队ID',
  `team_name`      VARCHAR(100)  DEFAULT NULL COMMENT '团队名称',
  `org_id`         BIGINT        DEFAULT NULL COMMENT '机构ID',
  `dept_id`        BIGINT        DEFAULT NULL COMMENT '科室ID',
  `leader_user_id` BIGINT        DEFAULT NULL COMMENT '队长用户ID',
  `team_status`    VARCHAR(20)   DEFAULT NULL COMMENT '团队状态(ACTIVE/DISSOLVED)',
  `tenant_id`      BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`      BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`    DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`      BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`    DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`       CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`team_id`),
  INDEX `idx_dt_org_id` (`org_id`),
  INDEX `idx_dt_tenant_id` (`tenant_id`),
  INDEX `idx_dt_tenant_org` (`tenant_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生团队表';

-- ----------------------------
-- 14. 医生团队成员表
-- ----------------------------
CREATE TABLE `ch_doctor_team_member` (
  `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `team_id`     BIGINT        DEFAULT NULL COMMENT '团队ID',
  `user_id`     BIGINT        DEFAULT NULL COMMENT '用户ID',
  `member_role` VARCHAR(20)   DEFAULT NULL COMMENT '成员角色(LEADER/MEMBER)',
  `tenant_id`   BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`   BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`   BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`    CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_dtm_team_id` (`team_id`),
  INDEX `idx_dtm_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生团队成员表';

-- ----------------------------
-- 15. 用药记录表
-- ----------------------------
CREATE TABLE `ch_medication_record` (
  `med_id`               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '用药ID',
  `patient_id`           BIGINT        DEFAULT NULL COMMENT '患者ID',
  `drug_name`            VARCHAR(100)  DEFAULT NULL COMMENT '药品名称',
  `drug_code`            VARCHAR(50)   DEFAULT NULL COMMENT '药品编码',
  `dosage`               VARCHAR(50)   DEFAULT NULL COMMENT '剂量',
  `frequency`            VARCHAR(30)   DEFAULT NULL COMMENT '频次',
  `route`                VARCHAR(30)   DEFAULT NULL COMMENT '给药途径',
  `start_date`           DATE          DEFAULT NULL COMMENT '开始日期',
  `stop_date`            DATE          DEFAULT NULL COMMENT '停药日期',
  `dispense_quantity`    VARCHAR(50)   DEFAULT NULL COMMENT '配药数量',
  `prescription_period`  VARCHAR(30)   DEFAULT NULL COMMENT '处方周期',
  `prescriber_user_id`   BIGINT        DEFAULT NULL COMMENT '处方医生用户ID',
  `prescriber_verified`  TINYINT(1)    DEFAULT 0 COMMENT '处方是否已审核',
  `status`               VARCHAR(20)   DEFAULT NULL COMMENT '用药状态(ACTIVE/STOPPED)',
  `tenant_id`            BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`            BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`          DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`            BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`          DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`             CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`med_id`),
  INDEX `idx_mr_patient_id` (`patient_id`),
  INDEX `idx_mr_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用药记录表';

-- ----------------------------
-- 16. 用药调整表
-- ----------------------------
CREATE TABLE `ch_medication_adjust` (
  `id`                 BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `med_id`             BIGINT        DEFAULT NULL COMMENT '用药记录ID',
  `patient_id`         BIGINT        DEFAULT NULL COMMENT '患者ID',
  `adjust_type`        VARCHAR(20)   DEFAULT NULL COMMENT '调整类型(ADD/REDUCE/SWITCH/DOSE_CHANGE)',
  `adjust_reason`      TEXT          DEFAULT NULL COMMENT '调整原因',
  `adverse_reaction`   TEXT          DEFAULT NULL COMMENT '不良反应',
  `preview_confirmed`  TINYINT(1)    DEFAULT 0 COMMENT '预览是否确认',
  `pin_verified_at`    DATETIME      DEFAULT NULL COMMENT 'PIN验证时间',
  `adjuster_user_id`   BIGINT        DEFAULT NULL COMMENT '调整人用户ID',
  `tenant_id`          BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`          BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`        DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`          BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`        DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`           CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_ma_patient_id` (`patient_id`),
  INDEX `idx_ma_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用药调整表';

-- ----------------------------
-- 17. 药物相互作用表
-- ----------------------------
CREATE TABLE `ch_drug_interaction` (
  `id`               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `drug_code_a`      VARCHAR(50)   DEFAULT NULL COMMENT '药品编码A',
  `drug_code_b`      VARCHAR(50)   DEFAULT NULL COMMENT '药品编码B',
  `interaction_level` VARCHAR(20)  DEFAULT NULL COMMENT '相互作用等级(CONTRAINDICATED/MAJOR_RISK/MONITOR)',
  `description`      TEXT          DEFAULT NULL COMMENT '描述',
  `clinical_advice`  TEXT          DEFAULT NULL COMMENT '临床建议',
  `tenant_id`        BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`        BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`      DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`        BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`      DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`         CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_di_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药物相互作用表';

-- ----------------------------
-- 18. 风险评估表
-- ----------------------------
CREATE TABLE `ch_risk_assessment` (
  `assessment_id`     BIGINT        NOT NULL AUTO_INCREMENT COMMENT '评估ID',
  `patient_id`        BIGINT        DEFAULT NULL COMMENT '患者ID',
  `disease_code`      VARCHAR(32)   DEFAULT NULL COMMENT '疾病编码',
  `risk_level`        VARCHAR(20)   DEFAULT NULL COMMENT '风险等级(LOW/MEDIUM/HIGH/VERY_HIGH)',
  `assessment_report` TEXT          DEFAULT NULL COMMENT '评估报告',
  `assessor_user_id`  BIGINT        DEFAULT NULL COMMENT '评估人用户ID',
  `org_id`            BIGINT        DEFAULT NULL COMMENT '机构ID',
  `tenant_id`         BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`         BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`       DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`         BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`       DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`          CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`assessment_id`),
  INDEX `idx_ra_patient_id` (`patient_id`),
  INDEX `idx_ra_tenant_id` (`tenant_id`),
  INDEX `idx_ra_tenant_org_disease` (`tenant_id`, `org_id`, `disease_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='风险评估表';

-- ----------------------------
-- 19. 风险因子项表
-- ----------------------------
CREATE TABLE `ch_risk_factor_item` (
  `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `assessment_id`  BIGINT        DEFAULT NULL COMMENT '评估ID',
  `factor_name`    VARCHAR(50)   DEFAULT NULL COMMENT '因子名称',
  `factor_value`   VARCHAR(100)  DEFAULT NULL COMMENT '因子值',
  `factor_weight`  DECIMAL(5,2)  DEFAULT NULL COMMENT '因子权重',
  `tenant_id`      BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`      BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`    DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`      BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`    DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`       CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_rfi_assessment_id` (`assessment_id`),
  INDEX `idx_rfi_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='风险因子项表';

-- ----------------------------
-- 20. 评估规则表
-- ----------------------------
CREATE TABLE `ch_assessment_rule` (
  `rule_id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `disease_code`      VARCHAR(32)   DEFAULT NULL COMMENT '疾病编码',
  `dimension_name`    VARCHAR(50)   DEFAULT NULL COMMENT '维度名称',
  `dimension_weight`  DECIMAL(5,2)  DEFAULT NULL COMMENT '维度权重',
  `threshold_config`  JSON          DEFAULT NULL COMMENT '阈值配置',
  `tenant_id`         BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`         BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`       DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`         BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`       DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`          CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`rule_id`),
  INDEX `idx_ar_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评估规则表';

-- ----------------------------
-- 21. 管理等级变更记录表
-- ----------------------------
CREATE TABLE `ch_manage_level_record` (
  `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `patient_id`    BIGINT        DEFAULT NULL COMMENT '患者ID',
  `disease_code`  VARCHAR(32)   DEFAULT NULL COMMENT '疾病编码',
  `old_level`     VARCHAR(20)   DEFAULT NULL COMMENT '原等级',
  `new_level`     VARCHAR(20)   DEFAULT NULL COMMENT '新等级',
  `change_reason` TEXT          DEFAULT NULL COMMENT '变更原因',
  `change_time`   DATETIME      DEFAULT NULL COMMENT '变更时间',
  `tenant_id`     BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`     BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`   DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`     BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`   DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`      CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_mlr_patient_id` (`patient_id`),
  INDEX `idx_mlr_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理等级变更记录表';

-- ----------------------------
-- 22. 管理方案表
-- ----------------------------
CREATE TABLE `ch_manage_plan` (
  `plan_id`       BIGINT        NOT NULL AUTO_INCREMENT COMMENT '方案ID',
  `patient_id`    BIGINT        DEFAULT NULL COMMENT '患者ID',
  `disease_code`  VARCHAR(32)   DEFAULT NULL COMMENT '疾病编码',
  `plan_status`   VARCHAR(20)   DEFAULT NULL COMMENT '方案状态(DRAFT/ACTIVE/DISABLED/HISTORY)',
  `org_id`       BIGINT        DEFAULT NULL COMMENT '机构ID',
  `tenant_id`     BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`     BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`   DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`     BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`   DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`      CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`plan_id`),
  INDEX `idx_mp_patient_id` (`patient_id`),
  INDEX `idx_mp_tenant_id` (`tenant_id`),
  INDEX `idx_mp_tenant_org_disease` (`tenant_id`, `org_id`, `disease_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理方案表';

-- ----------------------------
-- 23. 管理方案项表
-- ----------------------------
CREATE TABLE `ch_manage_plan_item` (
  `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id`      BIGINT        DEFAULT NULL COMMENT '方案ID',
  `item_type`    VARCHAR(20)   DEFAULT NULL COMMENT '项类型(MEDICATION/DIET/EXERCISE/PSYCHOLOGY/FOLLOWUP/MONITOR)',
  `item_content` JSON          DEFAULT NULL COMMENT '项内容',
  `org_id`      BIGINT        DEFAULT NULL COMMENT '机构ID',
  `tenant_id`    BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`    BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`  DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`    BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`  DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`     CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_mpi_plan_id` (`plan_id`),
  INDEX `idx_mpi_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理方案项表';

-- ----------------------------
-- 24. 随访计划表
-- ----------------------------
CREATE TABLE `ch_followup_plan` (
  `plan_id`       BIGINT        NOT NULL AUTO_INCREMENT COMMENT '计划ID',
  `patient_id`    BIGINT        DEFAULT NULL COMMENT '患者ID',
  `disease_code`  VARCHAR(32)   DEFAULT NULL COMMENT '疾病编码',
  `cycle_days`    INT           DEFAULT NULL COMMENT '周期天数',
  `total_rounds`  INT           DEFAULT NULL COMMENT '总轮次',
  `current_round` INT           DEFAULT 0 COMMENT '当前轮次',
  `status`        VARCHAR(20)   DEFAULT NULL COMMENT '计划状态(ACTIVE/COMPLETED/DISABLED)',
  `tenant_id`     BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`     BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`   DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`     BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`   DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`      CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`plan_id`),
  INDEX `idx_fp_patient_id` (`patient_id`),
  INDEX `idx_fp_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='随访计划表';

-- ----------------------------
-- 25. 随访计划项表
-- ----------------------------
CREATE TABLE `ch_followup_plan_item` (
  `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id`      BIGINT        DEFAULT NULL COMMENT '计划ID',
  `item_type`    VARCHAR(30)   DEFAULT NULL COMMENT '项类型',
  `visit_type`   VARCHAR(20)   DEFAULT NULL COMMENT '随访方式',
  `due_date`     DATE          DEFAULT NULL COMMENT '到期日期',
  `item_config`  JSON          DEFAULT NULL COMMENT '项配置',
  `tenant_id`    BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`    BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`  DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`    BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`  DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`     CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_fpi_plan_id` (`plan_id`),
  INDEX `idx_fpi_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='随访计划项表';

-- ----------------------------
-- 26. 随访任务表
-- ----------------------------
CREATE TABLE `ch_followup_task` (
  `task_id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `patient_id`        BIGINT        DEFAULT NULL COMMENT '患者ID',
  `plan_id`           BIGINT        DEFAULT NULL COMMENT '计划ID',
  `task_round`        INT           DEFAULT NULL COMMENT '任务轮次',
  `plan_due_date`     DATE          DEFAULT NULL COMMENT '计划到期日期',
  `task_status`       VARCHAR(20)   DEFAULT NULL COMMENT '任务状态(PENDING/REMINDING/DONE/OVERDUE/CANCELLED)',
  `assignee_user_id`  BIGINT        DEFAULT NULL COMMENT '执行人用户ID',
  `visit_type`        VARCHAR(20)   DEFAULT NULL COMMENT '随访方式',
  `tenant_id`         BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`         BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`       DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`         BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`       DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`          CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`task_id`),
  INDEX `idx_ft_patient_id` (`patient_id`),
  INDEX `idx_ft_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='随访任务表';

-- ----------------------------
-- 27. 随访记录表
-- ----------------------------
CREATE TABLE `ch_followup_record` (
  `record_id`     BIGINT        NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `task_id`       BIGINT        DEFAULT NULL COMMENT '任务ID',
  `patient_id`    BIGINT        DEFAULT NULL COMMENT '患者ID',
  `visit_type`    VARCHAR(20)   DEFAULT NULL COMMENT '随访方式(PHONE/VIDEO/OFFLINE/SELF_FILL/ADMIN_PROXY)',
  `visit_content` TEXT          DEFAULT NULL COMMENT '随访内容',
  `visitor_user_id` BIGINT      DEFAULT NULL COMMENT '随访人用户ID',
  `visit_date`    DATETIME      DEFAULT NULL COMMENT '随访日期',
  `tenant_id`     BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`     BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`   DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`     BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`   DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`      CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`record_id`),
  INDEX `idx_fr_patient_id` (`patient_id`),
  INDEX `idx_fr_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='随访记录表';

-- ----------------------------
-- 28. 随访问卷表
-- ----------------------------
CREATE TABLE `ch_followup_questionnaire` (
  `questionnaire_id`    BIGINT        NOT NULL AUTO_INCREMENT COMMENT '问卷ID',
  `disease_code`        VARCHAR(32)   DEFAULT NULL COMMENT '疾病编码',
  `questionnaire_name`  VARCHAR(100)  DEFAULT NULL COMMENT '问卷名称',
  `version`             INT           DEFAULT 1 COMMENT '版本',
  `questions`           JSON          DEFAULT NULL COMMENT '题目',
  `is_national_standard` TINYINT(1)   DEFAULT 0 COMMENT '是否国家标准',
  `tenant_id`           BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`           BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`         DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`           BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`         DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`            CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`questionnaire_id`),
  INDEX `idx_fq_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='随访问卷表';

-- ----------------------------
-- 29. 随访答卷表
-- ----------------------------
CREATE TABLE `ch_followup_answer` (
  `id`               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `record_id`        BIGINT        DEFAULT NULL COMMENT '随访记录ID',
  `questionnaire_id` BIGINT        DEFAULT NULL COMMENT '问卷ID',
  `question_id`      VARCHAR(50)   DEFAULT NULL COMMENT '题目ID',
  `answer_value`     TEXT          DEFAULT NULL COMMENT '答案值',
  `tenant_id`        BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`        BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`      DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`        BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`      DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`         CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_fa_record_id` (`record_id`),
  INDEX `idx_fa_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='随访答卷表';

-- ----------------------------
-- 30. 健康指标记录表
-- ----------------------------
-- NOTE: 建议按 measure_date 或 create_time 做时间分区(RANGE PARTITION BY RANGE COLUMNS(create_time))
-- 以提升大批量时序数据的查询与清理效率
CREATE TABLE `ch_health_metric_record` (
  `metric_id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '指标ID',
  `patient_id`         BIGINT        DEFAULT NULL COMMENT '患者ID',
  `metric_type`        VARCHAR(30)   DEFAULT NULL COMMENT '指标类型',
  `metric_value`       DECIMAL(10,2) DEFAULT NULL COMMENT '指标值',
  `unit`               VARCHAR(20)   DEFAULT NULL COMMENT '单位',
  `measure_scene`      VARCHAR(30)   DEFAULT NULL COMMENT '测量场景',
  `measure_period`     VARCHAR(20)   DEFAULT NULL COMMENT '测量时段',
  `measure_posture`    VARCHAR(20)   DEFAULT NULL COMMENT '测量体位',
  `reference_value_min` DECIMAL(10,2) DEFAULT NULL COMMENT '参考值下限',
  `reference_value_max` DECIMAL(10,2) DEFAULT NULL COMMENT '参考值上限',
  `is_abnormal`        TINYINT(1)    DEFAULT 0 COMMENT '是否异常',
  `data_source`        VARCHAR(20)   DEFAULT NULL COMMENT '数据来源(MANUAL/DEVICE/HIS_LIS)',
  `tenant_id`          BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`          BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`        DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`          BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`        DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`           CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`metric_id`),
  INDEX `idx_hmr_patient_id` (`patient_id`),
  INDEX `idx_hmr_tenant_id` (`tenant_id`),
  INDEX `idx_hmr_patient_type_time` (`patient_id`, `metric_type`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康指标记录表(建议按create_time做RANGE时间分区)';

-- ----------------------------
-- 31. 设备原始数据表
-- ----------------------------
CREATE TABLE `ch_device_raw_record` (
  `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `device_id`   VARCHAR(50)   DEFAULT NULL COMMENT '设备ID',
  `patient_id`  BIGINT        DEFAULT NULL COMMENT '患者ID',
  `raw_data`    JSON          DEFAULT NULL COMMENT '原始数据',
  `parsed_at`   DATETIME      DEFAULT NULL COMMENT '解析时间',
  `tenant_id`   BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`   BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`   BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`    CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_drr_patient_id` (`patient_id`),
  INDEX `idx_drr_device_id` (`device_id`),
  INDEX `idx_drr_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备原始数据表';

-- ----------------------------
-- 32. 设备绑定表
-- ----------------------------
CREATE TABLE `ch_device_bind` (
  `bind_id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '绑定ID',
  `patient_id`       BIGINT        DEFAULT NULL COMMENT '患者ID',
  `device_id`        VARCHAR(50)   DEFAULT NULL COMMENT '设备ID',
  `device_type`      VARCHAR(30)   DEFAULT NULL COMMENT '设备类型',
  `battery_level`    INT           DEFAULT NULL COMMENT '电池电量',
  `online_status`    TINYINT(1)    DEFAULT 0 COMMENT '在线状态',
  `last_comm_time`   DATETIME      DEFAULT NULL COMMENT '最后通信时间',
  `tenant_id`        BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`        BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`      DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`        BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`      DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`         CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`bind_id`),
  INDEX `idx_db_patient_id` (`patient_id`),
  INDEX `idx_db_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备绑定表';

-- ----------------------------
-- 33. 生活方式记录表
-- ----------------------------
CREATE TABLE `ch_lifestyle_record` (
  `id`                   BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `patient_id`           BIGINT        DEFAULT NULL COMMENT '患者ID',
  `smoking_status`       VARCHAR(20)   DEFAULT NULL COMMENT '吸烟状态',
  `drinking_status`      VARCHAR(20)   DEFAULT NULL COMMENT '饮酒状态',
  `exercise_freq`        VARCHAR(30)   DEFAULT NULL COMMENT '运动频率',
  `diet_habit`           TEXT          DEFAULT NULL COMMENT '饮食习惯',
  `psychological_status` VARCHAR(30)   DEFAULT NULL COMMENT '心理状态',
  `compliance_level`     VARCHAR(20)   DEFAULT NULL COMMENT '依从性等级',
  `tenant_id`            BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`            BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`          DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`            BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`          DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`             CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_lr_patient_id` (`patient_id`),
  INDEX `idx_lr_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生活方式记录表';

-- ----------------------------
-- 34. 体检记录表
-- ----------------------------
CREATE TABLE `ch_health_exam` (
  `exam_id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '体检ID',
  `patient_id`       BIGINT        DEFAULT NULL COMMENT '患者ID',
  `external_sn`      VARCHAR(64)   DEFAULT NULL COMMENT '外部流水号',
  `exam_type`        VARCHAR(30)   DEFAULT NULL COMMENT '体检类型(ANNUAL_CHECKUP/REGULAR_TEST/SPECIAL_TEST)',
  `exam_date`        DATE          DEFAULT NULL COMMENT '体检日期',
  `exam_org_id`      BIGINT        DEFAULT NULL COMMENT '体检机构ID',
  `special_category` VARCHAR(30)   DEFAULT NULL COMMENT '专项类别(FUNDUS_PHOTO/ABI/NERVE_CONDUCTION/ECG/ECHO/CT)',
  `tenant_id`        BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`        BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`      DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`        BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`      DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`         CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`exam_id`),
  UNIQUE INDEX `uk_external_sn` (`external_sn`),
  INDEX `idx_he_patient_id` (`patient_id`),
  INDEX `idx_he_tenant_id` (`tenant_id`),
  INDEX `idx_he_tenant_org` (`tenant_id`, `exam_org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体检记录表';

-- ----------------------------
-- 35. 体检项目表
-- ----------------------------
CREATE TABLE `ch_health_exam_item` (
  `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `exam_id`         BIGINT        DEFAULT NULL COMMENT '体检ID',
  `item_name`       VARCHAR(100)  DEFAULT NULL COMMENT '项目名称',
  `item_code`       VARCHAR(50)   DEFAULT NULL COMMENT '项目编码',
  `result_value`    VARCHAR(200)  DEFAULT NULL COMMENT '结果值',
  `reference_range` VARCHAR(100)  DEFAULT NULL COMMENT '参考范围',
  `is_abnormal`     TINYINT(1)    DEFAULT 0 COMMENT '是否异常',
  `dr_grade`        INT           DEFAULT NULL COMMENT 'DR分级',
  `tcss_score`      INT           DEFAULT NULL COMMENT 'TCSS评分',
  `mrs_score`       INT           DEFAULT NULL COMMENT 'MRS评分',
  `nihss_score`     INT           DEFAULT NULL COMMENT 'NIHSS评分',
  `egfr_value`      DECIMAL(10,2) DEFAULT NULL COMMENT 'eGFR值',
  `tenant_id`       BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`       BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`     DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`       BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`     DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`        CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_hei_exam_id` (`exam_id`),
  INDEX `idx_hei_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体检项目表';

-- ----------------------------
-- 36. 预警规则表
-- ----------------------------
CREATE TABLE `ch_warning_rule` (
  `rule_id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `disease_code`       VARCHAR(32)   DEFAULT NULL COMMENT '疾病编码',
  `metric_type`        VARCHAR(30)   DEFAULT NULL COMMENT '指标类型',
  `warning_level`      VARCHAR(20)   DEFAULT NULL COMMENT '预警等级(LOW/MEDIUM/HIGH/CRITICAL)',
  `threshold_min`      DECIMAL(10,2) DEFAULT NULL COMMENT '阈值下限',
  `threshold_max`      DECIMAL(10,2) DEFAULT NULL COMMENT '阈值上限',
  `consecutive_window` INT           DEFAULT NULL COMMENT '连续窗口次数',
  `time_window_start`  TIME          DEFAULT NULL COMMENT '时间窗口开始',
  `time_window_end`    TIME          DEFAULT NULL COMMENT '时间窗口结束',
  `recovery_rule`      JSON          DEFAULT NULL COMMENT '恢复规则',
  `org_id`            BIGINT        DEFAULT NULL COMMENT '机构ID',
  `tenant_id`          BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`          BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`        DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`          BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`        DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`           CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`rule_id`),
  INDEX `idx_wr_tenant_id` (`tenant_id`),
  INDEX `idx_wr_tenant_org_disease` (`tenant_id`, `org_id`, `disease_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警规则表';

-- ----------------------------
-- 37. 预警事件表
-- ----------------------------
CREATE TABLE `ch_warning_event` (
  `warning_id`       BIGINT        NOT NULL AUTO_INCREMENT COMMENT '预警ID',
  `patient_id`       BIGINT        DEFAULT NULL COMMENT '患者ID',
  `rule_id`          BIGINT        DEFAULT NULL COMMENT '规则ID',
  `warning_level`    VARCHAR(20)   DEFAULT NULL COMMENT '预警等级',
  `warning_value`    DECIMAL(10,2) DEFAULT NULL COMMENT '预警值',
  `warning_time`     DATETIME      DEFAULT NULL COMMENT '预警时间',
  `event_status`     VARCHAR(20)   DEFAULT NULL COMMENT '事件状态(NEW/CONFIRMED/PROCESSING/ESCALATED/RESOLVED/ARCHIVED)',
  `assignee_user_id` BIGINT        DEFAULT NULL COMMENT '处理人用户ID',
  `tenant_id`        BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`        BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`      DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`        BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`      DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`         CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`warning_id`),
  INDEX `idx_we_patient_id` (`patient_id`),
  INDEX `idx_we_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警事件表';

-- ----------------------------
-- 38. 预警处置表
-- ----------------------------
CREATE TABLE `ch_warning_action` (
  `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `warning_id`     BIGINT        DEFAULT NULL COMMENT '预警ID',
  `action_type`    VARCHAR(20)   DEFAULT NULL COMMENT '处置类型(CONFIRM/HANDLE/ESCALATE/RESOLVE)',
  `action_detail`  TEXT          DEFAULT NULL COMMENT '处置详情',
  `action_user_id` BIGINT        DEFAULT NULL COMMENT '处置人用户ID',
  `action_time`    DATETIME      DEFAULT NULL COMMENT '处置时间',
  `tenant_id`      BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`      BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`    DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`      BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`    DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`       CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_wa_warning_id` (`warning_id`),
  INDEX `idx_wa_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警处置表';

-- ----------------------------
-- 39. 转诊记录表
-- ----------------------------
CREATE TABLE `ch_referral_record` (
  `referral_id`       BIGINT        NOT NULL AUTO_INCREMENT COMMENT '转诊ID',
  `patient_id`        BIGINT        DEFAULT NULL COMMENT '患者ID',
  `from_org_id`       BIGINT        DEFAULT NULL COMMENT '转出机构ID',
  `to_org_id`         BIGINT        DEFAULT NULL COMMENT '转入机构ID',
  `to_area_code`      VARCHAR(20)   DEFAULT NULL COMMENT '转入区域编码',
  `referral_reason`   TEXT          DEFAULT NULL COMMENT '转诊原因',
  `referral_category` VARCHAR(30)   DEFAULT NULL COMMENT '转诊类别',
  `referral_status`   VARCHAR(20)   DEFAULT NULL COMMENT '转诊状态(PENDING/APPROVED/ACCEPTED/REJECTED/COMPLETED)',
  `referral_type`     VARCHAR(20)   DEFAULT NULL COMMENT '转诊类型(UPWARD/DOWNWARD/TOWNSHIP)',
  `tenant_id`         BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`         BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`       DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`         BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`       DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`          CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`referral_id`),
  INDEX `idx_rr_patient_id` (`patient_id`),
  INDEX `idx_rr_from_org_id` (`from_org_id`),
  INDEX `idx_rr_to_org_id` (`to_org_id`),
  INDEX `idx_rr_tenant_id` (`tenant_id`),
  INDEX `idx_rr_tenant_org` (`tenant_id`, `from_org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='转诊记录表';

-- ----------------------------
-- 40. 档案共享申请表
-- ----------------------------
CREATE TABLE `ch_archive_share_apply` (
  `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `patient_id`      BIGINT        DEFAULT NULL COMMENT '患者ID',
  `apply_org_id`    BIGINT        DEFAULT NULL COMMENT '申请机构ID',
  `target_org_id`   BIGINT        DEFAULT NULL COMMENT '目标机构ID',
  `apply_reason`    TEXT          DEFAULT NULL COMMENT '申请原因',
  `approval_status` VARCHAR(20)   DEFAULT NULL COMMENT '审批状态',
  `tenant_id`       BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`       BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`     DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`       BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`     DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`        CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_asa_patient_id` (`patient_id`),
  INDEX `idx_asa_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='档案共享申请表';

-- ----------------------------
-- 41. 外部系统同步日志表
-- ----------------------------
CREATE TABLE `ch_external_sync_log` (
  `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sync_type`       VARCHAR(30)   DEFAULT NULL COMMENT '同步类型',
  `sync_direction`  VARCHAR(20)   DEFAULT NULL COMMENT '同步方向',
  `external_system` VARCHAR(30)   DEFAULT NULL COMMENT '外部系统',
  `sync_status`     VARCHAR(20)   DEFAULT NULL COMMENT '同步状态',
  `sync_detail`     TEXT          DEFAULT NULL COMMENT '同步详情',
  `sync_time`       DATETIME      DEFAULT NULL COMMENT '同步时间',
  `tenant_id`       BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`       BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`     DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`       BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`     DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`        CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_esl_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='外部系统同步日志表';

-- ----------------------------
-- 42. 消息会话表
-- ----------------------------
CREATE TABLE `ch_message_session` (
  `session_id`         BIGINT        NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `patient_id`         BIGINT        DEFAULT NULL COMMENT '患者ID',
  `doctor_user_id`     BIGINT        DEFAULT NULL COMMENT '医生用户ID',
  `session_type`       VARCHAR(20)   DEFAULT NULL COMMENT '会话类型(DOCTOR_PATIENT/TEAM_PATIENT)',
  `last_message_time`  DATETIME      DEFAULT NULL COMMENT '最后消息时间',
  `tenant_id`          BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`          BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`        DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`          BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`        DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`           CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`session_id`),
  INDEX `idx_ms_patient_id` (`patient_id`),
  INDEX `idx_ms_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息会话表';

-- ----------------------------
-- 43. 消息内容表
-- ----------------------------
CREATE TABLE `ch_message_content` (
  `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `session_id`      BIGINT        DEFAULT NULL COMMENT '会话ID',
  `sender_type`     VARCHAR(10)   DEFAULT NULL COMMENT '发送者类型(DOCTOR/PATIENT)',
  `content_type`    VARCHAR(10)   DEFAULT NULL COMMENT '内容类型(TEXT/IMAGE/VOICE)',
  `content`         TEXT          DEFAULT NULL COMMENT '消息内容',
  `file_id`         BIGINT        DEFAULT NULL COMMENT '文件ID',
  `voice_duration`  INT           DEFAULT NULL COMMENT '语音时长(秒)',
  `tenant_id`       BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`       BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`     DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`       BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`     DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`        CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_mc_session_id` (`session_id`),
  INDEX `idx_mc_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息内容表';

-- ----------------------------
-- 44. 健康教育内容表
-- ----------------------------
CREATE TABLE `ch_health_education_content` (
  `content_id`   BIGINT        NOT NULL AUTO_INCREMENT COMMENT '内容ID',
  `title`        VARCHAR(200)  DEFAULT NULL COMMENT '标题',
  `content_body` TEXT          DEFAULT NULL COMMENT '内容正文',
  `tags`         JSON          DEFAULT NULL COMMENT '标签',
  `tenant_id`    BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`    BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`  DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`    BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`  DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`     CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`content_id`),
  INDEX `idx_hec_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康教育内容表';

-- ----------------------------
-- 45. 健康教育推送表
-- ----------------------------
CREATE TABLE `ch_health_education_delivery` (
  `delivery_id`     BIGINT        NOT NULL AUTO_INCREMENT COMMENT '推送ID',
  `content_id`      BIGINT        DEFAULT NULL COMMENT '内容ID',
  `patient_id`      BIGINT        DEFAULT NULL COMMENT '患者ID',
  `trigger_type`    VARCHAR(20)   DEFAULT NULL COMMENT '触发类型(RULE_ENGINE/MANUAL/WEATHER/SEASONAL)',
  `push_channel`    VARCHAR(20)   DEFAULT NULL COMMENT '推送渠道(WECHAT/SMS/IVR/PAPER)',
  `delivery_status` VARCHAR(20)   DEFAULT NULL COMMENT '推送状态',
  `read_status`     TINYINT(1)    DEFAULT 0 COMMENT '阅读状态',
  `read_time`       DATETIME      DEFAULT NULL COMMENT '阅读时间',
  `stay_duration`   INT           DEFAULT NULL COMMENT '停留时长(秒)',
  `tenant_id`       BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`       BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`     DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`       BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`     DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`        CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`delivery_id`),
  INDEX `idx_hed_patient_id` (`patient_id`),
  INDEX `idx_hed_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康教育推送表';

-- ----------------------------
-- 46. 教育推送规则表
-- ----------------------------
CREATE TABLE `ch_education_rule` (
  `rule_id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `condition_expression` TEXT          DEFAULT NULL COMMENT '条件表达式',
  `template_id`          BIGINT        DEFAULT NULL COMMENT '模板ID',
  `push_channel`         VARCHAR(20)   DEFAULT NULL COMMENT '推送渠道',
  `is_active`            TINYINT(1)    DEFAULT 1 COMMENT '是否启用',
  `tenant_id`            BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`            BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`          DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`            BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`          DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`             CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`rule_id`),
  INDEX `idx_er_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教育推送规则表';

-- ----------------------------
-- 47. 通知模板表
-- ----------------------------
CREATE TABLE `ch_notification_template` (
  `template_id`      BIGINT        NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `channel`          VARCHAR(20)   DEFAULT NULL COMMENT '渠道(WECHAT/SMS/IN_APP/IVR)',
  `template_code`    VARCHAR(50)   DEFAULT NULL COMMENT '模板编码',
  `template_content` TEXT          DEFAULT NULL COMMENT '模板内容',
  `tenant_id`        BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`        BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`      DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`        BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`      DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`         CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`template_id`),
  INDEX `idx_nt_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知模板表';

-- ----------------------------
-- 48. 报告模板表
-- ----------------------------
CREATE TABLE `ch_report_template` (
  `template_id`      BIGINT        NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_name`    VARCHAR(100)  DEFAULT NULL COMMENT '模板名称',
  `template_type`    VARCHAR(30)   DEFAULT NULL COMMENT '模板类型(ANNUAL_CHECKUP/QUARTERLY_MANAGE/AREA_STAT)',
  `template_content` JSON          DEFAULT NULL COMMENT '模板内容',
  `tenant_id`        BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`        BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`      DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`        BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`      DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`         CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`template_id`),
  INDEX `idx_rt_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报告模板表';

-- ----------------------------
-- 49. 报告实例表
-- ----------------------------
CREATE TABLE `ch_report_instance` (
  `report_id`    BIGINT        NOT NULL AUTO_INCREMENT COMMENT '报告ID',
  `patient_id`   BIGINT        DEFAULT NULL COMMENT '患者ID',
  `template_id`  BIGINT        DEFAULT NULL COMMENT '模板ID',
  `report_status` VARCHAR(20)  DEFAULT NULL COMMENT '报告状态(GENERATING/COMPLETED/FAILED)',
  `pdf_file_id`  BIGINT        DEFAULT NULL COMMENT 'PDF文件ID',
  `qr_code`      VARCHAR(100)  DEFAULT NULL COMMENT '二维码',
  `sign_status`  TINYINT(1)    DEFAULT 0 COMMENT '签名状态',
  `sign_time`    DATETIME      DEFAULT NULL COMMENT '签名时间',
  `tenant_id`    BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`    BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`  DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`    BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`  DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`     CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`report_id`),
  INDEX `idx_ri_patient_id` (`patient_id`),
  INDEX `idx_ri_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报告实例表';

-- ----------------------------
-- 50. 区域字典表
-- ----------------------------
CREATE TABLE `ch_area_dict` (
  `area_code`        VARCHAR(20)   NOT NULL COMMENT '区域编码',
  `area_name`        VARCHAR(100)  DEFAULT NULL COMMENT '区域名称',
  `area_level`       INT           DEFAULT NULL COMMENT '区域层级(1省/2市/3县/4乡/5村)',
  `parent_area_code` VARCHAR(20)   DEFAULT NULL COMMENT '父级区域编码',
  `create_by`        BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`      DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`        BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`      DATETIME      DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`area_code`),
  INDEX `idx_ad_parent_area_code` (`parent_area_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='区域字典表';

-- ----------------------------
-- 51. 机构区域映射表
-- ----------------------------
CREATE TABLE `ch_org_area_mapping` (
  `id`        BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `org_id`    BIGINT        DEFAULT NULL COMMENT '机构ID',
  `area_code` VARCHAR(20)   DEFAULT NULL COMMENT '区域编码',
  `tenant_id` BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by` BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME    DEFAULT NULL COMMENT '创建时间',
  `update_by` BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME    DEFAULT NULL COMMENT '更新时间',
  `del_flag`  CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_oam_org_id` (`org_id`),
  INDEX `idx_oam_tenant_id` (`tenant_id`),
  INDEX `idx_oam_tenant_org` (`tenant_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='机构区域映射表';

-- ----------------------------
-- 52. 区域统计日表
-- ----------------------------
CREATE TABLE `ch_stat_area_day` (
  `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `area_code`       VARCHAR(20)   DEFAULT NULL COMMENT '区域编码',
  `stat_date`       DATE          DEFAULT NULL COMMENT '统计日期',
  `patient_count`   BIGINT        DEFAULT 0 COMMENT '患者数',
  `managed_count`   BIGINT        DEFAULT 0 COMMENT '管理数',
  `warning_count`   BIGINT        DEFAULT 0 COMMENT '预警数',
  `followup_count`  BIGINT        DEFAULT 0 COMMENT '随访数',
  `tenant_id`       BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`       BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`     DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`       BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`     DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`        CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_sad_area_code` (`area_code`),
  INDEX `idx_sad_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='区域统计日表';

-- ----------------------------
-- 53. KPI指标定义表
-- ----------------------------
CREATE TABLE `ch_kpi_definition` (
  `kpi_id`       BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'KPI ID',
  `kpi_code`     VARCHAR(50)   DEFAULT NULL COMMENT 'KPI编码',
  `kpi_name`     VARCHAR(100)  DEFAULT NULL COMMENT 'KPI名称',
  `kpi_formula`  TEXT          DEFAULT NULL COMMENT 'KPI公式',
  `kpi_category` VARCHAR(30)   DEFAULT NULL COMMENT 'KPI分类(MANAGEMENT_RATE/COMPLIANCE_RATE/CONTROL_RATE)',
  `tenant_id`    BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`    BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`  DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`    BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`  DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`     CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`kpi_id`),
  INDEX `idx_kd_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='KPI指标定义表';

-- ----------------------------
-- 54. 知情同意记录表
-- ----------------------------
CREATE TABLE `ch_consent_record` (
  `consent_id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '同意ID',
  `patient_id`          BIGINT        DEFAULT NULL COMMENT '患者ID',
  `consent_type`        VARCHAR(20)   DEFAULT NULL COMMENT '同意类型(SIGN_CONTRACT/DATA_SHARE/REFERRAL)',
  `sign_image_file_id`  BIGINT        DEFAULT NULL COMMENT '签名图片文件ID',
  `sign_time`           DATETIME      DEFAULT NULL COMMENT '签名时间',
  `tenant_id`           BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`           BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`         DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`           BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`         DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`            CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`consent_id`),
  INDEX `idx_cr_patient_id` (`patient_id`),
  INDEX `idx_cr_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知情同意记录表';

-- ----------------------------
-- 55. 审计日志表(不含tenant_id和del_flag，审计日志不可软删除)
-- ----------------------------
CREATE TABLE `ch_audit_log` (
  `id`               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `operation_type`   VARCHAR(30)   DEFAULT NULL COMMENT '操作类型',
  `operation_target` VARCHAR(100)  DEFAULT NULL COMMENT '操作目标',
  `operation_detail` TEXT          DEFAULT NULL COMMENT '操作详情',
  `operator_id`      BIGINT        DEFAULT NULL COMMENT '操作人ID',
  `operator_name`    VARCHAR(50)   DEFAULT NULL COMMENT '操作人姓名',
  `operator_ip`      VARCHAR(50)   DEFAULT NULL COMMENT '操作人IP',
  `operation_time`   DATETIME      DEFAULT NULL COMMENT '操作时间',
  `create_by`        BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`      DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`        BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`      DATETIME      DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_al_operator_id` (`operator_id`),
  INDEX `idx_al_operation_time` (`operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审计日志表';

-- ----------------------------
-- 56. 文件附件表
-- ----------------------------
CREATE TABLE `ch_file_attachment` (
  `file_id`    BIGINT        NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `biz_type`   VARCHAR(20)   DEFAULT NULL COMMENT '业务类型(REPORT_PDF/SIGN_IMAGE/FUNDUS_PHOTO/ECG/OTHER)',
  `biz_id`     BIGINT        DEFAULT NULL COMMENT '业务ID',
  `file_name`  VARCHAR(200)  DEFAULT NULL COMMENT '文件名',
  `file_size`  BIGINT        DEFAULT NULL COMMENT '文件大小(字节)',
  `oss_id`     BIGINT        DEFAULT NULL COMMENT 'OSS存储ID',
  `tenant_id`  BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`  BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME     DEFAULT NULL COMMENT '创建时间',
  `update_by`  BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME     DEFAULT NULL COMMENT '更新时间',
  `del_flag`   CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`file_id`),
  INDEX `idx_fa_biz` (`biz_type`, `biz_id`),
  INDEX `idx_fa_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件附件表';

-- ----------------------------
-- 57. 医生微信绑定表
-- ----------------------------
CREATE TABLE `ch_doctor_wechat_bind` (
  `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`     BIGINT        DEFAULT NULL COMMENT '用户ID',
  `openid`      VARCHAR(100)  DEFAULT NULL COMMENT '微信openid',
  `unionid`     VARCHAR(100)  DEFAULT NULL COMMENT '微信unionid',
  `bind_time`   DATETIME      DEFAULT NULL COMMENT '绑定时间',
  `tenant_id`   BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`   BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`   BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`    CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_dwb_user_id` (`user_id`),
  INDEX `idx_dwb_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生微信绑定表';

-- ----------------------------
-- 58. 患者账户表
-- ----------------------------
CREATE TABLE `ch_patient_account` (
  `account_id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '账户ID',
  `patient_id`          BIGINT        DEFAULT NULL COMMENT '患者ID',
  `phone`               VARCHAR(20)   DEFAULT NULL COMMENT '手机号',
  `openid`              VARCHAR(100)  DEFAULT NULL COMMENT '微信openid',
  `unionid`             VARCHAR(100)  DEFAULT NULL COMMENT '微信unionid',
  `is_family_proxy`     TINYINT(1)    DEFAULT 0 COMMENT '是否家属代办',
  `master_account_id`   BIGINT        DEFAULT NULL COMMENT '主账户ID',
  `auth_scope`          JSON          DEFAULT NULL COMMENT '授权范围',
  `auth_expire_time`    DATETIME      DEFAULT NULL COMMENT '授权过期时间',
  `tenant_id`           BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`           BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`         DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`           BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`         DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`            CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`account_id`),
  INDEX `idx_pac_patient_id` (`patient_id`),
  INDEX `idx_pac_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者账户表';

-- ----------------------------
-- 59. 疾病统计日表
-- ----------------------------
CREATE TABLE `ch_stat_disease_day` (
  `id`               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `disease_code`     VARCHAR(32)   DEFAULT NULL COMMENT '疾病编码',
  `stat_date`        DATE          DEFAULT NULL COMMENT '统计日期',
  `patient_count`    BIGINT        DEFAULT 0 COMMENT '患者数',
  `new_count`        BIGINT        DEFAULT 0 COMMENT '新增数',
  `risk_high_count`  BIGINT        DEFAULT 0 COMMENT '高风险数',
  `tenant_id`        BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`        BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`      DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`        BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`      DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`         CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_sdd_disease_code` (`disease_code`),
  INDEX `idx_sdd_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='疾病统计日表';

-- ----------------------------
-- 60. 机构统计日表
-- ----------------------------
CREATE TABLE `ch_stat_org_day` (
  `id`                  BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `org_id`              BIGINT        DEFAULT NULL COMMENT '机构ID',
  `stat_date`           DATE          DEFAULT NULL COMMENT '统计日期',
  `patient_count`       BIGINT        DEFAULT 0 COMMENT '患者数',
  `followup_done_count` BIGINT        DEFAULT 0 COMMENT '随访完成数',
  `warning_count`       BIGINT        DEFAULT 0 COMMENT '预警数',
  `tenant_id`           BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`           BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`         DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`           BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`         DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`            CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_sod_org_id` (`org_id`),
  INDEX `idx_sod_tenant_id` (`tenant_id`),
  INDEX `idx_sod_tenant_org` (`tenant_id`, `org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='机构统计日表';

-- ----------------------------
-- 61. 预警统计日表
-- ----------------------------
CREATE TABLE `ch_stat_warning_day` (
  `id`                  BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `stat_date`           DATE          DEFAULT NULL COMMENT '统计日期',
  `total_count`         BIGINT        DEFAULT 0 COMMENT '总预警数',
  `resolved_count`      BIGINT        DEFAULT 0 COMMENT '已解决数',
  `escalated_count`     BIGINT        DEFAULT 0 COMMENT '已升级数',
  `avg_resolve_minutes` BIGINT        DEFAULT 0 COMMENT '平均解决时长(分钟)',
  `tenant_id`           BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`           BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`         DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`           BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`         DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`            CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_swd_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警统计日表';

-- ----------------------------
-- 62. 随访统计日表
-- ----------------------------
CREATE TABLE `ch_stat_followup_day` (
  `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `stat_date`       DATE          DEFAULT NULL COMMENT '统计日期',
  `total_count`     BIGINT        DEFAULT 0 COMMENT '总随访数',
  `done_count`      BIGINT        DEFAULT 0 COMMENT '完成数',
  `overdue_count`   BIGINT        DEFAULT 0 COMMENT '逾期数',
  `completion_rate` DECIMAL(5,2)  DEFAULT 0 COMMENT '完成率',
  `tenant_id`       BIGINT        DEFAULT NULL COMMENT '租户ID',
  `create_by`       BIGINT        DEFAULT NULL COMMENT '创建者',
  `create_time`     DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_by`       BIGINT        DEFAULT NULL COMMENT '更新者',
  `update_time`     DATETIME      DEFAULT NULL COMMENT '更新时间',
  `del_flag`        CHAR(1)       DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`),
  INDEX `idx_sfd_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='随访统计日表';
