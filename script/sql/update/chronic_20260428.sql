-- ==========================================================
-- 慢病管理系统增强特性 SQL 升级脚本 (2026-04-28)
-- Spec: chronic-disease-enhancement
-- 包含：档案字段补充、量化目标字段、自定义分组表、管理路径状态表
-- ==========================================================

-- 1. 修改患者主档案表 (ch_patient_profile)
-- 1a. 补充医保/联系人/户籍
ALTER TABLE ch_patient_profile
ADD COLUMN insurance_type VARCHAR(50) COMMENT '医保类型' AFTER source,
ADD COLUMN emergency_contact_name VARCHAR(50) COMMENT '紧急联系人姓名' AFTER insurance_type,
ADD COLUMN emergency_contact_phone VARCHAR(20) COMMENT '紧急联系人电话' AFTER emergency_contact_name,
ADD COLUMN permanent_address VARCHAR(255) COMMENT '户籍地址' AFTER address;

-- 1b. 补充身高/体重/血型/婚姻状况/既往史/过敏史/家族病史
ALTER TABLE ch_patient_profile
ADD COLUMN height DECIMAL(5,1) COMMENT '身高(cm)' AFTER drinking_amount,
ADD COLUMN weight DECIMAL(5,1) COMMENT '体重(kg)' AFTER height,
ADD COLUMN blood_type VARCHAR(20) COMMENT '血型' AFTER weight,
ADD COLUMN marital_status VARCHAR(20) COMMENT '婚姻状况' AFTER blood_type,
ADD COLUMN past_medical_history TEXT COMMENT '既往史(JSON数组)' AFTER marital_status,
ADD COLUMN allergy_history TEXT COMMENT '过敏史(JSON数组)' AFTER past_medical_history,
ADD COLUMN family_history TEXT COMMENT '家族病史(JSON数组)' AFTER allergy_history;

-- 1c. 补充年龄
ALTER TABLE ch_patient_profile
ADD COLUMN age INT COMMENT '年龄' AFTER birthday;

-- 1d. 删除冗余的细分病史字段（已归入既往史 past_medical_history）
-- 注意：如果列已不存在会报错，忽略即可
ALTER TABLE ch_patient_profile DROP COLUMN surgery_history;
ALTER TABLE ch_patient_profile DROP COLUMN trauma_history;
ALTER TABLE ch_patient_profile DROP COLUMN transfusion_history;
ALTER TABLE ch_patient_profile DROP COLUMN genetic_history;

-- 2. 修改管理方案子项表 (ch_manage_plan_item) —— 增加量化目标区间
ALTER TABLE ch_manage_plan_item
ADD COLUMN target_metric_type VARCHAR(50) COMMENT '目标指标类型(如 SYSTOLIC_BP, FASTING_GLUCOSE)' AFTER item_content,
ADD COLUMN target_min_value DECIMAL(10,2) COMMENT '目标下限值' AFTER target_metric_type,
ADD COLUMN target_max_value DECIMAL(10,2) COMMENT '目标上限值' AFTER target_min_value;

-- 3. 新增医生自定义分组表
CREATE TABLE IF NOT EXISTS ch_doctor_custom_group (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    group_name  VARCHAR(100) NOT NULL                COMMENT '分组名称',
    doctor_id   BIGINT       NOT NULL                COMMENT '创建/所属医生ID',
    description VARCHAR(255)                         COMMENT '分组描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by   VARCHAR(64)                          COMMENT '创建人',
    update_by   VARCHAR(64)                          COMMENT '更新人',
    PRIMARY KEY (id),
    INDEX idx_doctor_id (doctor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生自定义管理分组表';

-- 4. 新增分组成员关联表
CREATE TABLE IF NOT EXISTS ch_doctor_group_member (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    group_id    BIGINT       NOT NULL                COMMENT '分组ID',
    patient_id  BIGINT       NOT NULL                COMMENT '患者ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP   COMMENT '加入时间',
    create_by   VARCHAR(64)                          COMMENT '创建人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_group_patient (group_id, patient_id),
    INDEX idx_patient_id (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生分组成员关联表';

-- 5. 新增临床管理路径进度表
CREATE TABLE IF NOT EXISTS ch_clinical_pathway_status (
    id               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    patient_id       BIGINT       NOT NULL                COMMENT '患者ID',
    disease_code     VARCHAR(50)  NOT NULL                COMMENT '病种编码',
    current_stage    VARCHAR(50)  NOT NULL                COMMENT '当前所处阶段 (如: SCREENING, FIRST_EVAL, PLAN_EXECUTING, RE_EVAL)',
    stage_start_time DATETIME                             COMMENT '进入当前阶段时间',
    stage_deadline   DATETIME                             COMMENT '阶段截止/逾期时间',
    milestone_json   JSON                                 COMMENT '里程碑达成记录(JSON结构)',
    create_time      DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_patient_disease (patient_id, disease_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理路径进度表';

-- 6. COPD 底层兼容：初始化壳子数据
INSERT IGNORE INTO ch_disease_config (disease_code, disease_name, disease_category, is_active, create_time)
VALUES ('COPD', '慢性阻塞性肺疾病', 'RESPIRATORY', 1, CURRENT_TIMESTAMP);

-- 7. 统一去除冗余 org_id 字段（改用 dept_id，通过 sys_dept 树形结构追溯机构）
-- 注意：如果列已不存在会报错，忽略即可
ALTER TABLE ch_patient_profile DROP COLUMN org_id;
ALTER TABLE ch_patient_disease DROP COLUMN org_id;
ALTER TABLE ch_disease_config DROP COLUMN org_id;
ALTER TABLE ch_screening_batch DROP COLUMN org_id;
ALTER TABLE ch_doctor_team DROP COLUMN org_id;
ALTER TABLE ch_risk_assessment DROP COLUMN org_id;
ALTER TABLE ch_manage_plan DROP COLUMN org_id;
ALTER TABLE ch_manage_plan_item DROP COLUMN org_id;
ALTER TABLE ch_warning_rule DROP COLUMN org_id;
