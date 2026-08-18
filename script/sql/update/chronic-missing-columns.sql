-- ============================================================
-- 慢病模块补列脚本
-- 背景：线上 unimed-chronic 库按旧版 DDL 建库，实体类新增字段未同步到表，
--       MyBatis-Plus 按实体字段生成 SELECT，缺列会直接抛 Unknown column。
-- 生成依据：domain/entity/*.java 字段（含 @TableField 映射、跳过 exist=false）
--           与 information_schema.columns 逐列比对。
-- 幂等性：MySQL 不支持 ADD COLUMN IF NOT EXISTS，重复执行会报 1060 Duplicate column，
--         属预期，可安全忽略（脚本按语句独立执行）。
-- ============================================================

-- ---------- 第一组：主脚本 unimed-chronic.sql 已定义、线上库缺失（20 列） ----------

ALTER TABLE `ch_archive_share_apply` ADD COLUMN `workflow_instance_id` bigint NULL DEFAULT NULL COMMENT '工作流实例ID(启动审批流程后回填)' AFTER `approval_status`;

ALTER TABLE `ch_assessment_rule` ADD COLUMN `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用' AFTER `threshold_config`;

ALTER TABLE `ch_medication_record` ADD COLUMN `compliance` varchar(20) NULL DEFAULT NULL COMMENT '用药依从性(GOOD/FAIR/POOR，字典 chronic_compliance_level)' AFTER `status`;
ALTER TABLE `ch_medication_record` ADD COLUMN `prescription_basis` varchar(500) NULL DEFAULT NULL COMMENT '处方依据' AFTER `compliance`;
ALTER TABLE `ch_medication_record` ADD COLUMN `remark` varchar(500) NULL DEFAULT NULL COMMENT '用药备注' AFTER `prescription_basis`;

ALTER TABLE `ch_patient_account` ADD COLUMN `nickname` varchar(64) NULL DEFAULT NULL COMMENT '微信昵称' AFTER `del_flag`;
ALTER TABLE `ch_patient_account` ADD COLUMN `avatar_oss_id` varchar(64) NULL DEFAULT NULL COMMENT '头像OSS ID' AFTER `nickname`;

ALTER TABLE `ch_patient_contract` ADD COLUMN `last_remind_time` datetime NULL DEFAULT NULL COMMENT '上次提醒时间' AFTER `del_flag`;

ALTER TABLE `ch_patient_disease` ADD COLUMN `manage_level` varchar(16) NULL DEFAULT NULL COMMENT '管理级别(字典 chronic_manage_level)' AFTER `parent_disease_code`;
ALTER TABLE `ch_patient_disease` ADD COLUMN `enable_status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '启用状态(1启用 0停用)' AFTER `manage_level`;
ALTER TABLE `ch_patient_disease` ADD COLUMN `diagnosis_doctor_user_id` bigint NULL DEFAULT NULL COMMENT '确诊医生用户ID' AFTER `enable_status`;
ALTER TABLE `ch_patient_disease` ADD COLUMN `diagnosis_org_id` bigint NULL DEFAULT NULL COMMENT '确诊机构ID' AFTER `diagnosis_doctor_user_id`;

ALTER TABLE `ch_patient_tag` ADD COLUMN `tag_code` varchar(64) NULL DEFAULT NULL COMMENT '标签字典编码 ch_patient_tag_dict.tag_code' AFTER `tag_type`;

ALTER TABLE `ch_referral_record` ADD COLUMN `referral_time` datetime NULL DEFAULT NULL COMMENT '转诊时间' AFTER `referral_status`;

ALTER TABLE `ch_report_instance` ADD COLUMN `report_type` varchar(20) NULL DEFAULT NULL COMMENT '报告类型(ANNUAL/FOLLOWUP/SPECIAL)' AFTER `del_flag`;
ALTER TABLE `ch_report_instance` ADD COLUMN `pdf_oss_id` varchar(255) NULL DEFAULT NULL COMMENT 'PDF文件OSS ID' AFTER `report_type`;
ALTER TABLE `ch_report_instance` ADD COLUMN `qr_code_content` varchar(255) NULL DEFAULT NULL COMMENT '二维码内容' AFTER `pdf_oss_id`;
ALTER TABLE `ch_report_instance` ADD COLUMN `push_status` varchar(20) NULL DEFAULT NULL COMMENT '推送状态(PENDING/PUSHED/FAILED)' AFTER `qr_code_content`;

ALTER TABLE `ch_warning_rule` ADD COLUMN `rule_name` varchar(100) NULL DEFAULT NULL COMMENT '规则名称' AFTER `rule_id`;
ALTER TABLE `ch_warning_rule` ADD COLUMN `description` varchar(200) NULL DEFAULT NULL COMMENT '规则描述' AFTER `recovery_rule`;

-- ---------- 第二组：主脚本也缺失、实体已声明（4 列） ----------
-- ch_area_dict.del_flag：实体带 @TableLogic，缺列会导致区划字典所有查询报错
ALTER TABLE `ch_area_dict` ADD COLUMN `del_flag` char(1) NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)';

-- ch_report_template：实体用 disease_code + is_active（表原有 template_type 保留不动，多余列对 MP 无影响）
ALTER TABLE `ch_report_template` ADD COLUMN `disease_code` varchar(50) NULL DEFAULT NULL COMMENT '适用病种编码(NULL表示通用)' AFTER `template_content`;
ALTER TABLE `ch_report_template` ADD COLUMN `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用' AFTER `disease_code`;

-- ch_screening_batch.status：批次状态(字典 chronic_screening_status)
ALTER TABLE `ch_screening_batch` ADD COLUMN `status` varchar(20) NULL DEFAULT 'PLANNED' COMMENT '批次状态(PLANNED/ONGOING/FINISHED/CANCELED)' AFTER `notes`;

-- ---------- 第三组：存量数据回填 ----------

-- 逻辑删除标志必须非空，否则 @TableLogic 的 del_flag='0' 过滤会漏掉存量行
UPDATE `ch_area_dict` SET `del_flag` = '0' WHERE `del_flag` IS NULL;

-- 患者标签编码：由 tag_type + tag_value 反推字典编码，命中 ch_patient_tag_dict.tag_code
UPDATE `ch_patient_tag` SET `tag_code` = CONCAT('RISK_', `tag_value`)
 WHERE `tag_code` IS NULL AND `tag_type` = 'RISK'
   AND `tag_value` IN ('LOW', 'MEDIUM', 'HIGH', 'VERY_HIGH');
-- 其余（COMORBIDITY/CUSTOM 存的是自由文本）按自定义标签归类，保留原文本在 tag_value
UPDATE `ch_patient_tag` SET `tag_code` = 'TAG_CUSTOM'
 WHERE `tag_code` IS NULL AND `tag_type` IN ('CUSTOM', 'COMORBIDITY');

-- 报告实例：存量行补默认报告类型与推送状态，避免前端筛选项全空
UPDATE `ch_report_instance` SET `report_type` = 'FOLLOWUP' WHERE `report_type` IS NULL;
UPDATE `ch_report_instance` SET `push_status` = 'PENDING'  WHERE `push_status` IS NULL;

-- 预警规则：规则名为空时用「指标+等级」兜底，供列表展示
UPDATE `ch_warning_rule` SET `rule_name` = CONCAT(`metric_type`, ' ', `warning_level`, ' 预警规则')
 WHERE `rule_name` IS NULL OR `rule_name` = '';

-- 筛查批次：存量 2 条按活动日期判断状态
UPDATE `ch_screening_batch` SET `status` = CASE
    WHEN `activity_date` IS NULL        THEN 'PLANNED'
    WHEN `activity_date` < CURDATE()    THEN 'FINISHED'
    ELSE 'PLANNED' END
 WHERE `status` IS NULL OR `status` = 'PLANNED';

-- 用药依从性：存量记录默认良好，避免依从性统计除零
UPDATE `ch_medication_record` SET `compliance` = 'GOOD' WHERE `compliance` IS NULL;

-- 病种启用状态：存量行全部启用
UPDATE `ch_patient_disease` SET `enable_status` = 1 WHERE `enable_status` IS NULL;

-- 评估规则/报告模板：存量行默认启用
UPDATE `ch_assessment_rule` SET `is_active` = 1 WHERE `is_active` IS NULL;
UPDATE `ch_report_template`  SET `is_active` = 1 WHERE `is_active` IS NULL;

-- ---------- 第四组：主脚本已定义、线上库缺失（补齐设计字段，实体暂未使用）----------
ALTER TABLE `ch_clinical_pathway_status` ADD COLUMN `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门';
ALTER TABLE `ch_clinical_pathway_status` ADD COLUMN `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户ID';
ALTER TABLE `ch_clinical_pathway_status` ADD COLUMN `create_by` bigint NULL DEFAULT NULL COMMENT '创建者';
ALTER TABLE `ch_clinical_pathway_status` ADD COLUMN `update_by` bigint NULL DEFAULT NULL COMMENT '更新者';
ALTER TABLE `ch_clinical_pathway_status` ADD COLUMN `del_flag` char(1) NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)';
ALTER TABLE `ch_consent_record` ADD COLUMN `sign_method` varchar(20) NULL DEFAULT NULL COMMENT '签名方式(HANDWRITE/FACE/SMS_OTP)';
ALTER TABLE `ch_consent_record` ADD COLUMN `operator_ip` varchar(50) NULL DEFAULT NULL COMMENT '操作IP';
ALTER TABLE `ch_consent_record` ADD COLUMN `device_info` varchar(255) NULL DEFAULT NULL COMMENT '设备信息(UA/设备指纹)';
ALTER TABLE `ch_consent_record` ADD COLUMN `related_biz_type` varchar(30) NULL DEFAULT NULL COMMENT '关联业务类型';
ALTER TABLE `ch_consent_record` ADD COLUMN `related_biz_id` bigint NULL DEFAULT NULL COMMENT '关联业务ID';
ALTER TABLE `ch_doctor_custom_group` ADD COLUMN `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门';
ALTER TABLE `ch_doctor_custom_group` ADD COLUMN `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户ID';
ALTER TABLE `ch_doctor_custom_group` ADD COLUMN `del_flag` char(1) NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)';
ALTER TABLE `ch_doctor_group_member` ADD COLUMN `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门';
ALTER TABLE `ch_doctor_group_member` ADD COLUMN `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户ID';
ALTER TABLE `ch_doctor_group_member` ADD COLUMN `update_by` bigint NULL DEFAULT NULL COMMENT '更新者';
ALTER TABLE `ch_doctor_group_member` ADD COLUMN `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间';
ALTER TABLE `ch_doctor_group_member` ADD COLUMN `del_flag` char(1) NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)';
ALTER TABLE `ch_health_exam` ADD COLUMN `conclusion` text NULL COMMENT '体检结论';
ALTER TABLE `ch_health_exam_item` ADD COLUMN `item_unit` varchar(20) NULL DEFAULT NULL COMMENT '项目单位';
ALTER TABLE `ch_lifestyle_record` ADD COLUMN `record_date` date NULL DEFAULT NULL COMMENT '记录日期';
ALTER TABLE `ch_lifestyle_record` ADD COLUMN `exercise_minutes` int NULL DEFAULT NULL COMMENT '运动时长(分钟)';
ALTER TABLE `ch_lifestyle_record` ADD COLUMN `diet_score` int NULL DEFAULT NULL COMMENT '饮食评分';
ALTER TABLE `ch_lifestyle_record` ADD COLUMN `sleep_hours` decimal(3, 1) NULL DEFAULT NULL COMMENT '睡眠时长(小时)';
ALTER TABLE `ch_lifestyle_record` ADD COLUMN `mood_score` int NULL DEFAULT NULL COMMENT '心情评分';
ALTER TABLE `ch_lifestyle_record` ADD COLUMN `lifestyle_detail` json NULL COMMENT '生活方式详情';
ALTER TABLE `ch_patient_account` ADD COLUMN `avatar_url` varchar(500) NULL DEFAULT NULL COMMENT '微信头像URL';
ALTER TABLE `ch_patient_account` ADD COLUMN `bind_qr_token` varchar(64) NULL DEFAULT NULL COMMENT '绑定二维码 token';
ALTER TABLE `ch_patient_account` ADD COLUMN `qr_token_expire_time` datetime NULL DEFAULT NULL COMMENT '二维码过期时间';

UPDATE `ch_clinical_pathway_status` SET `del_flag`='0' WHERE `del_flag` IS NULL;
UPDATE `ch_doctor_custom_group` SET `del_flag`='0' WHERE `del_flag` IS NULL;
UPDATE `ch_doctor_group_member` SET `del_flag`='0' WHERE `del_flag` IS NULL;
UPDATE `ch_lifestyle_record` SET `record_date` = DATE(`create_time`) WHERE `record_date` IS NULL AND `create_time` IS NOT NULL;
