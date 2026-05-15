/*
 Navicat Premium Dump SQL

 Source Server         : 192.168.2.43
 Source Server Type    : MySQL
 Source Server Version : 80046 (8.0.46)
 Source Host           : 192.168.2.43:3306
 Source Schema         : unimed-chronic

 Target Server Type    : MySQL
 Target Server Version : 80046 (8.0.46)
 File Encoding         : 65001

 Date: 15/05/2026 18:09:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ch_archive_share_apply
-- ----------------------------
DROP TABLE IF EXISTS `ch_archive_share_apply`;
CREATE TABLE `ch_archive_share_apply`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `apply_org_id` bigint NULL DEFAULT NULL COMMENT '申请机构ID',
  `target_org_id` bigint NULL DEFAULT NULL COMMENT '目标机构ID',
  `apply_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '申请原因',
  `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审批状态',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_asa_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_asa_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '档案共享申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_area_dict
-- ----------------------------
DROP TABLE IF EXISTS `ch_area_dict`;
CREATE TABLE `ch_area_dict`  (
  `area_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '区域编码',
  `area_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '区域名称',
  `area_level` int NULL DEFAULT NULL COMMENT '区域层级(1省/2市/3县/4乡/5村)',
  `parent_area_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '父级区域编码',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`area_code`) USING BTREE,
  INDEX `idx_ad_parent_area_code`(`parent_area_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '区域字典表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_assessment_rule
-- ----------------------------
DROP TABLE IF EXISTS `ch_assessment_rule`;
CREATE TABLE `ch_assessment_rule`  (
  `rule_id` bigint NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `disease_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '疾病编码',
  `dimension_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维度名称',
  `dimension_weight` decimal(5, 2) NULL DEFAULT NULL COMMENT '维度权重',
  `threshold_config` json NULL COMMENT '阈值配置',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`rule_id`) USING BTREE,
  INDEX `idx_ar_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评估规则表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_audit_log
-- ----------------------------
DROP TABLE IF EXISTS `ch_audit_log`;
CREATE TABLE `ch_audit_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `operation_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作类型',
  `operation_target` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作目标',
  `operation_detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '操作详情',
  `operator_id` bigint NULL DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作人姓名',
  `operator_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作人IP',
  `operation_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_al_operator_id`(`operator_id` ASC) USING BTREE,
  INDEX `idx_al_operation_time`(`operation_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '审计日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_clinical_pathway_status
-- ----------------------------
DROP TABLE IF EXISTS `ch_clinical_pathway_status`;
CREATE TABLE `ch_clinical_pathway_status`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `disease_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '病种编码',
  `current_stage` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '当前所处阶段 (如: SCREENING, FIRST_EVAL, PLAN_EXECUTING, RE_EVAL)',
  `stage_start_time` datetime NULL DEFAULT NULL COMMENT '进入当前阶段时间',
  `stage_deadline` datetime NULL DEFAULT NULL COMMENT '阶段截止/逾期时间',
  `milestone_json` json NULL COMMENT '里程碑达成记录(JSON结构)',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_patient_disease`(`patient_id` ASC, `disease_code` ASC) USING BTREE,
  INDEX `idx_cps_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理路径进度表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_consent_record
-- ----------------------------
DROP TABLE IF EXISTS `ch_consent_record`;
CREATE TABLE `ch_consent_record`  (
  `consent_id` bigint NOT NULL AUTO_INCREMENT COMMENT '同意ID',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `consent_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '同意类型(SIGN_CONTRACT/DATA_SHARE/REFERRAL)',
  `sign_image_file_id` bigint NULL DEFAULT NULL COMMENT '签名图片文件ID',
  `sign_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '签名方式(HANDWRITE/FACE/SMS_OTP)',
  `sign_time` datetime NULL DEFAULT NULL COMMENT '签名时间',
  `operator_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作IP',
  `device_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备信息(UA/设备指纹)',
  `related_biz_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联业务类型',
  `related_biz_id` bigint NULL DEFAULT NULL COMMENT '关联业务ID',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`consent_id`) USING BTREE,
  INDEX `idx_cr_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_cr_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '知情同意记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_contract_fulfillment
-- ----------------------------
DROP TABLE IF EXISTS `ch_contract_fulfillment`;
CREATE TABLE `ch_contract_fulfillment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `contract_id` bigint NULL DEFAULT NULL COMMENT '签约ID',
  `service_item` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务项目',
  `plan_date` date NULL DEFAULT NULL COMMENT '计划日期',
  `actual_date` date NULL DEFAULT NULL COMMENT '实际日期',
  `fulfillment_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '履约状态(PLANNED/DONE/MISSED)',
  `sla_violation` tinyint(1) NULL DEFAULT 0 COMMENT '是否SLA违约',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_cf_contract_id`(`contract_id` ASC) USING BTREE,
  INDEX `idx_cf_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '签约履约表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_contract_service_package
-- ----------------------------
DROP TABLE IF EXISTS `ch_contract_service_package`;
CREATE TABLE `ch_contract_service_package`  (
  `package_id` bigint NOT NULL AUTO_INCREMENT COMMENT '服务包ID',
  `package_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务包名称',
  `package_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务包类型(BASIC/ADVANCED/CUSTOM)',
  `service_items` json NULL COMMENT '服务项目',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`package_id`) USING BTREE,
  INDEX `idx_csp_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '签约服务包表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_device_bind
-- ----------------------------
DROP TABLE IF EXISTS `ch_device_bind`;
CREATE TABLE `ch_device_bind`  (
  `bind_id` bigint NOT NULL AUTO_INCREMENT COMMENT '绑定ID',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `device_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备ID',
  `device_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备类型',
  `battery_level` int NULL DEFAULT NULL COMMENT '电池电量',
  `online_status` tinyint(1) NULL DEFAULT 0 COMMENT '在线状态',
  `last_comm_time` datetime NULL DEFAULT NULL COMMENT '最后通信时间',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`bind_id`) USING BTREE,
  INDEX `idx_db_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_db_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '设备绑定表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_device_raw_record
-- ----------------------------
DROP TABLE IF EXISTS `ch_device_raw_record`;
CREATE TABLE `ch_device_raw_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `device_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备ID',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `raw_data` json NULL COMMENT '原始数据',
  `parsed_at` datetime NULL DEFAULT NULL COMMENT '解析时间',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_drr_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_drr_device_id`(`device_id` ASC) USING BTREE,
  INDEX `idx_drr_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '设备原始数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_disease_config
-- ----------------------------
DROP TABLE IF EXISTS `ch_disease_config`;
CREATE TABLE `ch_disease_config`  (
  `config_id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `disease_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '疾病编码',
  `disease_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '疾病名称',
  `disease_category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '疾病分类',
  `is_primary` tinyint(1) NULL DEFAULT NULL COMMENT '是否主病种',
  `parent_disease_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '父级疾病编码',
  `followup_template_id` bigint NULL DEFAULT NULL COMMENT '随访模板ID',
  `assessment_strategy_id` bigint NULL DEFAULT NULL COMMENT '评估策略ID',
  `monitor_items` json NULL COMMENT '监测项目',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `org_id` bigint NULL DEFAULT NULL COMMENT '机构ID',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`config_id`) USING BTREE,
  UNIQUE INDEX `uk_disease_code`(`disease_code` ASC) USING BTREE,
  INDEX `idx_dc_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '疾病配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_disease_relation
-- ----------------------------
DROP TABLE IF EXISTS `ch_disease_relation`;
CREATE TABLE `ch_disease_relation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_disease_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '父级疾病编码',
  `complication_disease_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '并发症疾病编码',
  `relation_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联类型',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_dr_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '疾病关联关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_doctor_custom_group
-- ----------------------------
DROP TABLE IF EXISTS `ch_doctor_custom_group`;
CREATE TABLE `ch_doctor_custom_group`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `group_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分组名称',
  `doctor_id` bigint NOT NULL COMMENT '创建/所属医生ID',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分组描述',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_doctor_id`(`doctor_id` ASC) USING BTREE,
  INDEX `idx_dcg_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '医生自定义管理分组表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_doctor_group_member
-- ----------------------------
DROP TABLE IF EXISTS `ch_doctor_group_member`;
CREATE TABLE `ch_doctor_group_member`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `group_id` bigint NOT NULL COMMENT '分组ID',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '加入时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_group_patient`(`group_id` ASC, `patient_id` ASC) USING BTREE,
  INDEX `idx_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_dgm_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '医生分组成员关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_doctor_team
-- ----------------------------
DROP TABLE IF EXISTS `ch_doctor_team`;
CREATE TABLE `ch_doctor_team`  (
  `team_id` bigint NOT NULL AUTO_INCREMENT COMMENT '团队ID',
  `team_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '团队名称',
  `org_id` bigint NULL DEFAULT NULL COMMENT '机构ID',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '科室ID',
  `leader_user_id` bigint NULL DEFAULT NULL COMMENT '队长用户ID',
  `team_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '团队状态(ACTIVE/DISSOLVED)',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`team_id`) USING BTREE,
  INDEX `idx_dt_org_id`(`org_id` ASC) USING BTREE,
  INDEX `idx_dt_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_dt_tenant_org`(`tenant_id` ASC, `org_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '医生团队表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_doctor_team_member
-- ----------------------------
DROP TABLE IF EXISTS `ch_doctor_team_member`;
CREATE TABLE `ch_doctor_team_member`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `team_id` bigint NULL DEFAULT NULL COMMENT '团队ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `member_role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '成员角色(LEADER/MEMBER)',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_dtm_team_id`(`team_id` ASC) USING BTREE,
  INDEX `idx_dtm_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '医生团队成员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_doctor_wechat_bind
-- ----------------------------
DROP TABLE IF EXISTS `ch_doctor_wechat_bind`;
CREATE TABLE `ch_doctor_wechat_bind`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `openid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信openid',
  `unionid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信unionid',
  `bind_time` datetime NULL DEFAULT NULL COMMENT '绑定时间',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_dwb_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_dwb_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '医生微信绑定表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_drug_interaction
-- ----------------------------
DROP TABLE IF EXISTS `ch_drug_interaction`;
CREATE TABLE `ch_drug_interaction`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `drug_code_a` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '药品编码A',
  `drug_code_b` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '药品编码B',
  `interaction_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '相互作用等级(CONTRAINDICATED/MAJOR_RISK/MONITOR)',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述',
  `clinical_advice` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '临床建议',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_di_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '药物相互作用表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_education_rule
-- ----------------------------
DROP TABLE IF EXISTS `ch_education_rule`;
CREATE TABLE `ch_education_rule`  (
  `rule_id` bigint NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `condition_expression` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '条件表达式',
  `template_id` bigint NULL DEFAULT NULL COMMENT '模板ID',
  `push_channel` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '推送渠道',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`rule_id`) USING BTREE,
  INDEX `idx_er_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教育推送规则表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_encounter_diagnosis
-- ----------------------------
DROP TABLE IF EXISTS `ch_encounter_diagnosis`;
CREATE TABLE `ch_encounter_diagnosis`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `encounter_id` bigint NULL DEFAULT NULL COMMENT '诊疗记录ID',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `diagnosis_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '诊断类型(PRIMARY/SECONDARY)',
  `diagnosis_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ICD编码',
  `diagnosis_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '诊断名称',
  `diagnosis_basis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '诊断依据',
  `risk_factor_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '风险因素编码',
  `risk_factor_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '风险因素名称',
  `complication_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'N' COMMENT '是否并发症(Y/N)',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_ed_encounter_id`(`encounter_id` ASC) USING BTREE,
  INDEX `idx_ed_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_ed_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '诊疗诊断表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_encounter_record
-- ----------------------------
DROP TABLE IF EXISTS `ch_encounter_record`;
CREATE TABLE `ch_encounter_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `disease_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '病种编码',
  `encounter_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '就诊类型(INITIAL/FOLLOWUP)',
  `encounter_time` datetime NULL DEFAULT NULL COMMENT '就诊时间',
  `complaint` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '主诉',
  `present_history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '现病史',
  `physical_exam_summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '体格检查摘要',
  `auxiliary_exam_summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '辅助检查摘要',
  `treatment_plan` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '处理方案',
  `revisit_advice` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '复诊建议',
  `medication_snapshot` json NULL COMMENT '当前用药快照',
  `risk_factor_snapshot` json NULL COMMENT '风险因素快照',
  `source_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '来源类型(DOCTOR/ADMIN/HIS)',
  `source_biz_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '外部单号或门诊号',
  `submit_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'DRAFT' COMMENT '提交状态(DRAFT/SUBMITTED)',
  `submitted_time` datetime NULL DEFAULT NULL COMMENT '提交时间',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_er_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_er_encounter_time`(`encounter_time` ASC) USING BTREE,
  INDEX `idx_er_source_biz_no`(`source_biz_no` ASC) USING BTREE,
  INDEX `idx_er_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '诊疗记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_external_sync_log
-- ----------------------------
DROP TABLE IF EXISTS `ch_external_sync_log`;
CREATE TABLE `ch_external_sync_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sync_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '同步类型',
  `sync_direction` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '同步方向',
  `external_system` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '外部系统',
  `sync_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '同步状态',
  `sync_detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '同步详情',
  `sync_time` datetime NULL DEFAULT NULL COMMENT '同步时间',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_esl_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '外部系统同步日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_file_attachment
-- ----------------------------
DROP TABLE IF EXISTS `ch_file_attachment`;
CREATE TABLE `ch_file_attachment`  (
  `file_id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `biz_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务类型(REPORT_PDF/SIGN_IMAGE/FUNDUS_PHOTO/ECG/OTHER)',
  `biz_id` bigint NULL DEFAULT NULL COMMENT '业务ID',
  `file_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件名',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小(字节)',
  `oss_id` bigint NULL DEFAULT NULL COMMENT 'OSS存储ID',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`file_id`) USING BTREE,
  INDEX `idx_fa_biz`(`biz_type` ASC, `biz_id` ASC) USING BTREE,
  INDEX `idx_fa_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文件附件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_followup_answer
-- ----------------------------
DROP TABLE IF EXISTS `ch_followup_answer`;
CREATE TABLE `ch_followup_answer`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `record_id` bigint NULL DEFAULT NULL COMMENT '随访记录ID',
  `questionnaire_id` bigint NULL DEFAULT NULL COMMENT '问卷ID',
  `question_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '题目ID',
  `answer_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '答案值',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_fa_record_id`(`record_id` ASC) USING BTREE,
  INDEX `idx_fa_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '随访答卷表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_followup_plan
-- ----------------------------
DROP TABLE IF EXISTS `ch_followup_plan`;
CREATE TABLE `ch_followup_plan`  (
  `plan_id` bigint NOT NULL AUTO_INCREMENT COMMENT '计划ID',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `disease_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '疾病编码',
  `cycle_days` int NULL DEFAULT NULL COMMENT '周期天数',
  `total_rounds` int NULL DEFAULT NULL COMMENT '总轮次',
  `current_round` int NULL DEFAULT 0 COMMENT '当前轮次',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '计划状态(ACTIVE/COMPLETED/DISABLED)',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`plan_id`) USING BTREE,
  INDEX `idx_fp_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_fp_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '随访计划表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_followup_plan_item
-- ----------------------------
DROP TABLE IF EXISTS `ch_followup_plan_item`;
CREATE TABLE `ch_followup_plan_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NULL DEFAULT NULL COMMENT '计划ID',
  `item_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '项类型',
  `visit_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '随访方式',
  `due_date` date NULL DEFAULT NULL COMMENT '到期日期',
  `item_config` json NULL COMMENT '项配置',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_fpi_plan_id`(`plan_id` ASC) USING BTREE,
  INDEX `idx_fpi_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '随访计划项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_followup_questionnaire
-- ----------------------------
DROP TABLE IF EXISTS `ch_followup_questionnaire`;
CREATE TABLE `ch_followup_questionnaire`  (
  `questionnaire_id` bigint NOT NULL AUTO_INCREMENT COMMENT '问卷ID',
  `disease_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '疾病编码',
  `questionnaire_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '问卷名称',
  `version` int NULL DEFAULT 1 COMMENT '版本',
  `questions` json NULL COMMENT '题目',
  `is_national_standard` tinyint(1) NULL DEFAULT 0 COMMENT '是否国家标准',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`questionnaire_id`) USING BTREE,
  INDEX `idx_fq_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '随访问卷表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_followup_record
-- ----------------------------
DROP TABLE IF EXISTS `ch_followup_record`;
CREATE TABLE `ch_followup_record`  (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `task_id` bigint NULL DEFAULT NULL COMMENT '任务ID',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `visit_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '随访方式(PHONE/VIDEO/OFFLINE/SELF_FILL/ADMIN_PROXY)',
  `visit_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '随访内容',
  `visitor_user_id` bigint NULL DEFAULT NULL COMMENT '随访人用户ID',
  `visit_date` datetime NULL DEFAULT NULL COMMENT '随访日期',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `idx_fr_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_fr_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '随访记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_followup_task
-- ----------------------------
DROP TABLE IF EXISTS `ch_followup_task`;
CREATE TABLE `ch_followup_task`  (
  `task_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `plan_id` bigint NULL DEFAULT NULL COMMENT '计划ID',
  `task_round` int NULL DEFAULT NULL COMMENT '任务轮次',
  `plan_due_date` date NULL DEFAULT NULL COMMENT '计划到期日期',
  `task_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '任务状态(PENDING/REMINDING/DONE/OVERDUE/CANCELLED)',
  `assignee_user_id` bigint NULL DEFAULT NULL COMMENT '执行人用户ID',
  `visit_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '随访方式',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`task_id`) USING BTREE,
  INDEX `idx_ft_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_ft_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '随访任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_health_education_content
-- ----------------------------
DROP TABLE IF EXISTS `ch_health_education_content`;
CREATE TABLE `ch_health_education_content`  (
  `content_id` bigint NOT NULL AUTO_INCREMENT COMMENT '内容ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
  `content_body` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '内容正文',
  `tags` json NULL COMMENT '标签',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`content_id`) USING BTREE,
  INDEX `idx_hec_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健康教育内容表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_health_education_delivery
-- ----------------------------
DROP TABLE IF EXISTS `ch_health_education_delivery`;
CREATE TABLE `ch_health_education_delivery`  (
  `delivery_id` bigint NOT NULL AUTO_INCREMENT COMMENT '推送ID',
  `content_id` bigint NULL DEFAULT NULL COMMENT '内容ID',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `trigger_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '触发类型(RULE_ENGINE/MANUAL/WEATHER/SEASONAL)',
  `push_channel` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '推送渠道(WECHAT/SMS/IVR/PAPER)',
  `delivery_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '推送状态',
  `read_status` tinyint(1) NULL DEFAULT 0 COMMENT '阅读状态',
  `read_time` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  `stay_duration` int NULL DEFAULT NULL COMMENT '停留时长(秒)',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`delivery_id`) USING BTREE,
  INDEX `idx_hed_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_hed_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健康教育推送表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_health_exam
-- ----------------------------
DROP TABLE IF EXISTS `ch_health_exam`;
CREATE TABLE `ch_health_exam`  (
  `exam_id` bigint NOT NULL AUTO_INCREMENT COMMENT '体检ID',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `external_sn` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '外部流水号',
  `exam_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '体检类型(ANNUAL_CHECKUP/REGULAR_TEST/SPECIAL_TEST)',
  `exam_date` date NULL DEFAULT NULL COMMENT '体检日期',
  `exam_org_id` bigint NULL DEFAULT NULL COMMENT '体检机构ID',
  `special_category` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专项类别(FUNDUS_PHOTO/ABI/NERVE_CONDUCTION/ECG/ECHO/CT)',
  `conclusion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '体检结论',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`exam_id`) USING BTREE,
  UNIQUE INDEX `uk_external_sn`(`external_sn` ASC) USING BTREE,
  INDEX `idx_he_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_he_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_he_tenant_org`(`tenant_id` ASC, `exam_org_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '体检记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_health_exam_item
-- ----------------------------
DROP TABLE IF EXISTS `ch_health_exam_item`;
CREATE TABLE `ch_health_exam_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `exam_id` bigint NULL DEFAULT NULL COMMENT '体检ID',
  `item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '项目名称',
  `item_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '项目编码',
  `result_value` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '结果值',
  `item_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '项目单位',
  `reference_range` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '参考范围',
  `is_abnormal` tinyint(1) NULL DEFAULT 0 COMMENT '是否异常',
  `dr_grade` int NULL DEFAULT NULL COMMENT 'DR分级',
  `tcss_score` int NULL DEFAULT NULL COMMENT 'TCSS评分',
  `mrs_score` int NULL DEFAULT NULL COMMENT 'MRS评分',
  `nihss_score` int NULL DEFAULT NULL COMMENT 'NIHSS评分',
  `egfr_value` decimal(10, 2) NULL DEFAULT NULL COMMENT 'eGFR值',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_hei_exam_id`(`exam_id` ASC) USING BTREE,
  INDEX `idx_hei_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '体检项目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_health_metric_record
-- ----------------------------
DROP TABLE IF EXISTS `ch_health_metric_record`;
CREATE TABLE `ch_health_metric_record`  (
  `metric_id` bigint NOT NULL AUTO_INCREMENT COMMENT '指标ID',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `metric_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '指标类型',
  `metric_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '指标值',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '单位',
  `measure_scene` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '测量场景',
  `measure_period` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '测量时段',
  `measure_posture` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '测量体位',
  `reference_value_min` decimal(10, 2) NULL DEFAULT NULL COMMENT '参考值下限',
  `reference_value_max` decimal(10, 2) NULL DEFAULT NULL COMMENT '参考值上限',
  `is_abnormal` tinyint(1) NULL DEFAULT 0 COMMENT '是否异常',
  `data_source` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '数据来源(MANUAL/DEVICE/HIS_LIS)',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`metric_id`) USING BTREE,
  INDEX `idx_hmr_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_hmr_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_hmr_patient_type_time`(`patient_id` ASC, `metric_type` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健康指标记录表(建议按create_time做RANGE时间分区)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_icd_dict
-- ----------------------------
DROP TABLE IF EXISTS `ch_icd_dict`;
CREATE TABLE `ch_icd_dict`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `icd_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ICD编码',
  `icd_version` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ICD版本(ICD10/ICD11)',
  `icd_name_cn` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ICD中文名称',
  `icd_name_en` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ICD英文名称',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_icd_code`(`icd_code` ASC) USING BTREE,
  INDEX `idx_icd_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'ICD字典表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_kpi_definition
-- ----------------------------
DROP TABLE IF EXISTS `ch_kpi_definition`;
CREATE TABLE `ch_kpi_definition`  (
  `kpi_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'KPI ID',
  `kpi_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'KPI编码',
  `kpi_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'KPI名称',
  `kpi_formula` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'KPI公式',
  `kpi_category` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'KPI分类(MANAGEMENT_RATE/COMPLIANCE_RATE/CONTROL_RATE)',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`kpi_id`) USING BTREE,
  INDEX `idx_kd_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'KPI指标定义表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_lab_test
-- ----------------------------
DROP TABLE IF EXISTS `ch_lab_test`;
CREATE TABLE `ch_lab_test`  (
  `test_id` bigint NOT NULL AUTO_INCREMENT COMMENT '检验ID',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `test_date` datetime NULL DEFAULT NULL COMMENT '检验日期',
  `test_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '检验类型(血常规/肝功能/肾功能等)',
  `test_items` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '检验项目明细JSON',
  `report_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '报告图片URL',
  `hospital` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '检验医院',
  `doctor` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '检验医生',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`test_id`) USING BTREE,
  INDEX `idx_lt_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_lt_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '检验记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for ch_lifestyle_record
-- ----------------------------
DROP TABLE IF EXISTS `ch_lifestyle_record`;
CREATE TABLE `ch_lifestyle_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `record_date` date NULL DEFAULT NULL COMMENT '记录日期',
  `exercise_minutes` int NULL DEFAULT NULL COMMENT '运动时长(分钟)',
  `diet_score` int NULL DEFAULT NULL COMMENT '饮食评分',
  `sleep_hours` decimal(3, 1) NULL DEFAULT NULL COMMENT '睡眠时长(小时)',
  `mood_score` int NULL DEFAULT NULL COMMENT '心情评分',
  `lifestyle_detail` json NULL COMMENT '生活方式详情',
  `smoking_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '吸烟状态',
  `drinking_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '饮酒状态',
  `exercise_freq` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '运动频率',
  `diet_habit` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '饮食习惯',
  `psychological_status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '心理状态',
  `compliance_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '依从性等级',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_lr_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_lr_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '生活方式记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_manage_level_record
-- ----------------------------
DROP TABLE IF EXISTS `ch_manage_level_record`;
CREATE TABLE `ch_manage_level_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `disease_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '疾病编码',
  `old_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '原等级',
  `new_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '新等级',
  `change_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '变更原因',
  `change_time` datetime NULL DEFAULT NULL COMMENT '变更时间',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_mlr_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_mlr_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理等级变更记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_manage_plan
-- ----------------------------
DROP TABLE IF EXISTS `ch_manage_plan`;
CREATE TABLE `ch_manage_plan`  (
  `plan_id` bigint NOT NULL AUTO_INCREMENT COMMENT '方案ID',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `disease_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '疾病编码',
  `plan_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '方案状态(DRAFT/ACTIVE/DISABLED/HISTORY)',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `org_id` bigint NULL DEFAULT NULL COMMENT '机构ID',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`plan_id`) USING BTREE,
  INDEX `idx_mp_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_mp_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_mp_tenant_org_disease`(`tenant_id` ASC, `org_id` ASC, `disease_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理方案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_manage_plan_item
-- ----------------------------
DROP TABLE IF EXISTS `ch_manage_plan_item`;
CREATE TABLE `ch_manage_plan_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plan_id` bigint NULL DEFAULT NULL COMMENT '方案ID',
  `item_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '项类型(MEDICATION/DIET/EXERCISE/PSYCHOLOGY/FOLLOWUP/MONITOR)',
  `item_content` json NULL COMMENT '项内容',
  `target_metric_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '目标指标类型(如 SYSTOLIC_BP, FASTING_GLUCOSE)',
  `target_min_value` decimal(10, 2) NULL DEFAULT NULL COMMENT '目标下限值',
  `target_max_value` decimal(10, 2) NULL DEFAULT NULL COMMENT '目标上限值',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `org_id` bigint NULL DEFAULT NULL COMMENT '机构ID',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_mpi_plan_id`(`plan_id` ASC) USING BTREE,
  INDEX `idx_mpi_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理方案项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_medical_exam
-- ----------------------------
DROP TABLE IF EXISTS `ch_medical_exam`;
CREATE TABLE `ch_medical_exam`  (
  `exam_id` bigint NOT NULL AUTO_INCREMENT COMMENT '检查ID',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `exam_date` datetime NULL DEFAULT NULL COMMENT '检查日期',
  `exam_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '检查类型(X光/CT/B超/心电图等)',
  `exam_part` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '检查部位',
  `exam_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '检查结果描述',
  `exam_conclusion` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '检查结论',
  `report_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '报告图片URL',
  `hospital` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '检查医院',
  `doctor` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '检查医生',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`exam_id`) USING BTREE,
  INDEX `idx_me_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_me_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '检查记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for ch_medication_adjust
-- ----------------------------
DROP TABLE IF EXISTS `ch_medication_adjust`;
CREATE TABLE `ch_medication_adjust`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `med_id` bigint NULL DEFAULT NULL COMMENT '用药记录ID',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `adjust_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '调整类型(ADD/REDUCE/SWITCH/DOSE_CHANGE)',
  `adjust_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '调整原因',
  `adverse_reaction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '不良反应',
  `preview_confirmed` tinyint(1) NULL DEFAULT 0 COMMENT '预览是否确认',
  `pin_verified_at` datetime NULL DEFAULT NULL COMMENT 'PIN验证时间',
  `adjuster_user_id` bigint NULL DEFAULT NULL COMMENT '调整人用户ID',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_ma_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_ma_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用药调整表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_medication_record
-- ----------------------------
DROP TABLE IF EXISTS `ch_medication_record`;
CREATE TABLE `ch_medication_record`  (
  `med_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用药ID',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `drug_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '药品名称',
  `drug_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '药品编码',
  `dosage` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '剂量',
  `frequency` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '频次',
  `route` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '给药途径',
  `start_date` date NULL DEFAULT NULL COMMENT '开始日期',
  `stop_date` date NULL DEFAULT NULL COMMENT '停药日期',
  `dispense_quantity` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '配药数量',
  `prescription_period` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处方周期',
  `prescriber_user_id` bigint NULL DEFAULT NULL COMMENT '处方医生用户ID',
  `prescriber_verified` tinyint(1) NULL DEFAULT 0 COMMENT '处方是否已审核',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用药状态(ACTIVE/STOPPED)',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`med_id`) USING BTREE,
  INDEX `idx_mr_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_mr_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用药记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_message_content
-- ----------------------------
DROP TABLE IF EXISTS `ch_message_content`;
CREATE TABLE `ch_message_content`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `session_id` bigint NULL DEFAULT NULL COMMENT '会话ID',
  `sender_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发送者类型(DOCTOR/PATIENT)',
  `content_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '内容类型(TEXT/IMAGE/VOICE)',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '消息内容',
  `file_id` bigint NULL DEFAULT NULL COMMENT '文件ID',
  `voice_duration` int NULL DEFAULT NULL COMMENT '语音时长(秒)',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_mc_session_id`(`session_id` ASC) USING BTREE,
  INDEX `idx_mc_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '消息内容表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_message_session
-- ----------------------------
DROP TABLE IF EXISTS `ch_message_session`;
CREATE TABLE `ch_message_session`  (
  `session_id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `doctor_user_id` bigint NULL DEFAULT NULL COMMENT '医生用户ID',
  `session_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会话类型(DOCTOR_PATIENT/TEAM_PATIENT)',
  `last_message_time` datetime NULL DEFAULT NULL COMMENT '最后消息时间',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`session_id`) USING BTREE,
  INDEX `idx_ms_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_ms_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '消息会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_notification_template
-- ----------------------------
DROP TABLE IF EXISTS `ch_notification_template`;
CREATE TABLE `ch_notification_template`  (
  `template_id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `channel` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '渠道(WECHAT/SMS/IN_APP/IVR)',
  `template_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模板编码',
  `template_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '模板内容',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`template_id`) USING BTREE,
  INDEX `idx_nt_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_ocr_draft
-- ----------------------------
DROP TABLE IF EXISTS `ch_ocr_draft`;
CREATE TABLE `ch_ocr_draft`  (
  `draft_id` bigint NOT NULL AUTO_INCREMENT COMMENT '草稿ID',
  `task_id` bigint NULL DEFAULT NULL COMMENT 'OCR任务ID',
  `draft_category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '草稿类型(PROFILE/DISEASE/METRIC/REPORT)',
  `draft_data` json NULL COMMENT '识别结构化数据',
  `confidence` decimal(5, 2) NULL DEFAULT NULL COMMENT '识别置信度(0-100)',
  `need_confirm` tinyint(1) NULL DEFAULT 1 COMMENT '是否待人工确认',
  `confirm_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '确认状态(PENDING/CONFIRMED/DISCARDED)',
  `written_biz_id` bigint NULL DEFAULT NULL COMMENT '入库后的业务ID',
  `matched_patient_id` bigint NULL DEFAULT NULL COMMENT '身份证命中的已有患者ID',
  `action_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '建议动作(CREATE_ARCHIVE/UPDATE_ARCHIVE)',
  `unmapped_field_json` json NULL COMMENT '未映射字段JSON',
  `metric_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '映射后的慢病指标类型',
  `original_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'OCR原始项目名称',
  `metric_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '指标值(METRIC类)',
  `unit` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '单位',
  `reference_value_min` decimal(10, 2) NULL DEFAULT NULL COMMENT '参考下限',
  `reference_value_max` decimal(10, 2) NULL DEFAULT NULL COMMENT '参考上限',
  `reference_range` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '原始参考范围',
  `is_abnormal` tinyint(1) NULL DEFAULT 0 COMMENT '是否异常',
  `item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '检查检验项目名称(REPORT类)',
  `item_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '项目编码(REPORT类)',
  `result_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '结果/结论(REPORT类)',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`draft_id`) USING BTREE,
  INDEX `idx_od_task_id`(`task_id` ASC) USING BTREE,
  INDEX `idx_od_confirm_status`(`confirm_status` ASC) USING BTREE,
  INDEX `idx_od_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_od_category_task`(`draft_category` ASC, `task_id` ASC) USING BTREE,
  INDEX `idx_od_metric_type`(`metric_type` ASC) USING BTREE,
  INDEX `idx_od_matched_patient`(`matched_patient_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'OCR草稿表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_ocr_task
-- ----------------------------
DROP TABLE IF EXISTS `ch_ocr_task`;
CREATE TABLE `ch_ocr_task`  (
  `task_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '目标患者ID',
  `doc_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文档类型(MEDICAL_HOME/DISCHARGE/LAB/EXAM/DIAGNOSIS/OTHER)',
  `input_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '输入形式(IMAGE_BASE64/IMAGE_URL/PDF/OSS)',
  `file_id` bigint NULL DEFAULT NULL COMMENT '源文件ID',
  `oss_id` bigint NULL DEFAULT NULL COMMENT 'OSS 资源ID(历史兼容，与 file_id 二选一)',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件访问地址(历史兼容)',
  `file_md5` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件MD5(用于幂等判重)',
  `source_terminal` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发起端(ADMIN/DOCTOR/PATIENT)',
  `task_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '任务状态(PENDING/PROCESSING/SUCCESS/FAILED/CONFIRMED/DISCARDED)',
  `recognized_at` datetime NULL DEFAULT NULL COMMENT '识别完成时间',
  `confirmed_at` datetime NULL DEFAULT NULL COMMENT '确认入库时间',
  `confirmer_user_id` bigint NULL DEFAULT NULL COMMENT '确认人用户ID',
  `report_draft_json` json NULL COMMENT '报告主信息草稿JSON(历史兼容)',
  `raw_ocr_json` json NULL COMMENT '原始OCR JSON(调试/复盘用)',
  `error_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '错误码(失败时填写)',
  `error_msg` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '错误信息(失败时填写)',
  `confirmed_patient_id` bigint NULL DEFAULT NULL COMMENT '确认后的患者ID',
  `confirmed_metric_count` int NULL DEFAULT 0 COMMENT '确认入库指标数量',
  `confirmed_exam_id` bigint NULL DEFAULT NULL COMMENT '确认后的检查检验报告ID',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`task_id`) USING BTREE,
  INDEX `idx_ot_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_ot_task_status`(`task_status` ASC) USING BTREE,
  INDEX `idx_ot_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_ot_file_md5`(`patient_id` ASC, `file_md5` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'OCR任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_ops_health_check
-- ----------------------------
DROP TABLE IF EXISTS `ch_ops_health_check`;
CREATE TABLE `ch_ops_health_check`  (
  `check_id` bigint NOT NULL AUTO_INCREMENT COMMENT '巡检ID',
  `check_batch` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '巡检批次号',
  `target_component` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '目标组件(DB/REDIS/NACOS/MQ/HIS/LIS/PACS 等)',
  `check_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '检查结果(SUCCESS/FAILED/TIMEOUT)',
  `response_ms` bigint NULL DEFAULT NULL COMMENT '响应时长(毫秒)',
  `error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误信息(失败时填写)',
  `alert_triggered` tinyint(1) NULL DEFAULT 0 COMMENT '是否触发告警',
  `check_time` datetime NULL DEFAULT NULL COMMENT '巡检时间',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`check_id`) USING BTREE,
  INDEX `idx_ohc_check_batch`(`check_batch` ASC) USING BTREE,
  INDEX `idx_ohc_check_time`(`check_time` ASC) USING BTREE,
  INDEX `idx_ohc_component`(`target_component` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '运维健康巡检表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_ops_rerun_ticket
-- ----------------------------
DROP TABLE IF EXISTS `ch_ops_rerun_ticket`;
CREATE TABLE `ch_ops_rerun_ticket`  (
  `ticket_id` bigint NOT NULL AUTO_INCREMENT COMMENT '工单ID',
  `task_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '重跑任务编码',
  `apply_user_id` bigint NULL DEFAULT NULL COMMENT '申请人用户ID',
  `apply_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '申请理由',
  `audit_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审批状态(PENDING/APPROVED/REJECTED)',
  `auditor_user_id` bigint NULL DEFAULT NULL COMMENT '审批人用户ID',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审批时间',
  `audit_remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '审批备注',
  `exec_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '执行状态(NOT_STARTED/RUNNING/SUCCESS/FAILED)',
  `exec_start_time` datetime NULL DEFAULT NULL COMMENT '执行开始时间',
  `exec_end_time` datetime NULL DEFAULT NULL COMMENT '执行结束时间',
  `exec_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '执行结果摘要',
  `affected_range` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '影响数据范围',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`ticket_id`) USING BTREE,
  INDEX `idx_ort_task_code`(`task_code` ASC) USING BTREE,
  INDEX `idx_ort_audit_status`(`audit_status` ASC) USING BTREE,
  INDEX `idx_ort_exec_status`(`exec_status` ASC) USING BTREE,
  INDEX `idx_ort_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '运维任务重跑工单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_org_area_mapping
-- ----------------------------
DROP TABLE IF EXISTS `ch_org_area_mapping`;
CREATE TABLE `ch_org_area_mapping`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `org_id` bigint NULL DEFAULT NULL COMMENT '机构ID',
  `area_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '区域编码',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_oam_org_id`(`org_id` ASC) USING BTREE,
  INDEX `idx_oam_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_oam_tenant_org`(`tenant_id` ASC, `org_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '机构区域映射表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_patient_account
-- ----------------------------
DROP TABLE IF EXISTS `ch_patient_account`;
CREATE TABLE `ch_patient_account`  (
  `account_id` bigint NOT NULL AUTO_INCREMENT COMMENT '账户ID',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `openid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信openid',
  `unionid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信unionid',
  `is_family_proxy` tinyint(1) NULL DEFAULT 0 COMMENT '是否家属代办',
  `master_account_id` bigint NULL DEFAULT NULL COMMENT '主账户ID',
  `auth_scope` json NULL COMMENT '授权范围',
  `auth_expire_time` datetime NULL DEFAULT NULL COMMENT '授权过期时间',
  `bind_qr_token` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '绑定二维码 token',
  `qr_token_expire_time` datetime NULL DEFAULT NULL COMMENT '二维码过期时间',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信昵称',
  `avatar_oss_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像OSS ID',
  `avatar_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信头像URL',
  PRIMARY KEY (`account_id`) USING BTREE,
  INDEX `idx_pac_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_pac_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '患者账户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_patient_close_apply
-- ----------------------------
DROP TABLE IF EXISTS `ch_patient_close_apply`;
CREATE TABLE `ch_patient_close_apply`  (
  `apply_id` bigint NOT NULL AUTO_INCREMENT COMMENT '结案申请ID',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `close_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '结案类型(VOLUNTARY/TRANSFER/LOST/DEATH)',
  `apply_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '申请理由',
  `evidence_file_id` bigint NULL DEFAULT NULL COMMENT '证据附件文件ID',
  `snapshot_json` json NULL COMMENT '审核通过后用于追溯的快照',
  `applicant_user_id` bigint NULL DEFAULT NULL COMMENT '申请人用户ID',
  `apply_source` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '申请发起端(ADMIN/DOCTOR/PATIENT)',
  `audit_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核状态(PENDING/APPROVED/REJECTED/WITHDRAWN)',
  `auditor_user_id` bigint NULL DEFAULT NULL COMMENT '审核人用户ID',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `audit_remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '审核备注',
  `reject_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '驳回理由(仅驳回时填写)',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`apply_id`) USING BTREE,
  INDEX `idx_pca_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_pca_audit_status`(`audit_status` ASC) USING BTREE,
  INDEX `idx_pca_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '患者结案申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_patient_contract
-- ----------------------------
DROP TABLE IF EXISTS `ch_patient_contract`;
CREATE TABLE `ch_patient_contract`  (
  `contract_id` bigint NOT NULL AUTO_INCREMENT COMMENT '签约ID',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `team_id` bigint NULL DEFAULT NULL COMMENT '团队ID',
  `package_id` bigint NULL DEFAULT NULL COMMENT '服务包ID',
  `contract_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '签约类型',
  `contract_period_start` date NULL DEFAULT NULL COMMENT '签约开始日期',
  `contract_period_end` date NULL DEFAULT NULL COMMENT '签约结束日期',
  `renewal_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '续约状态(ACTIVE/EXPIRING/EXPIRED/RENEWED)',
  `expiry_remind_status` tinyint(1) NULL DEFAULT 0 COMMENT '到期提醒状态',
  `contract_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '合同状态(ACTIVE/TERMINATED)',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  `last_remind_time` datetime NULL DEFAULT NULL COMMENT '上次提醒时间',
  PRIMARY KEY (`contract_id`) USING BTREE,
  INDEX `idx_pc_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_pc_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '患者签约表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_patient_disease
-- ----------------------------
DROP TABLE IF EXISTS `ch_patient_disease`;
CREATE TABLE `ch_patient_disease`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `disease_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '疾病编码',
  `icd_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ICD编码',
  `diagnosis_basis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '诊断依据',
  `confirm_date` date NULL DEFAULT NULL COMMENT '确诊日期',
  `is_complication` tinyint(1) NULL DEFAULT NULL COMMENT '是否并发症',
  `parent_disease_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '父级疾病编码',
  `enable_status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '启用状态(1启用 0停用)',
  `diagnosis_doctor_user_id` bigint NULL DEFAULT NULL COMMENT '确诊医生用户ID',
  `diagnosis_org_id` bigint NULL DEFAULT NULL COMMENT '确诊机构ID',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `org_id` bigint NULL DEFAULT NULL COMMENT '机构ID',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pd_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_pd_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_pd_tenant_org_disease`(`tenant_id` ASC, `org_id` ASC, `disease_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '患者疾病关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_patient_profile
-- ----------------------------
DROP TABLE IF EXISTS `ch_patient_profile`;
CREATE TABLE `ch_patient_profile`  (
  `patient_id` bigint NOT NULL AUTO_INCREMENT COMMENT '患者ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '姓名',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证号',
  `gender` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '性别',
  `birthday` date NULL DEFAULT NULL COMMENT '出生日期',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '居住地址',
  `permanent_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '户籍地址',
  `gis_lng` decimal(10, 6) NULL DEFAULT NULL COMMENT '经度',
  `gis_lat` decimal(10, 6) NULL DEFAULT NULL COMMENT '纬度',
  `nation` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '民族',
  `occupation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '职业',
  `education_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文化程度',
  `disability_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '残疾类型',
  `disability_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '残疾等级',
  `assistive_device` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '辅助器具',
  `smoking_index` int NULL DEFAULT NULL COMMENT '吸烟指数',
  `drinking_amount` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '饮酒量',
  `height` decimal(5, 1) NULL DEFAULT NULL COMMENT '身高(cm)',
  `weight` decimal(5, 1) NULL DEFAULT NULL COMMENT '体重(kg)',
  `blood_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '血型',
  `marital_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '婚姻状况',
  `past_medical_history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '既往史(JSON数组)',
  `allergy_history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '过敏史(JSON数组)',
  `family_history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '家族病史(JSON数组)',
  `org_id` bigint NULL DEFAULT NULL COMMENT '管理机构ID',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '管理科室ID',
  `doctor_user_id` bigint NULL DEFAULT NULL COMMENT '责任医生用户ID',
  `manage_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '管理状态(PENDING_ENTRY/MANAGED/FOLLOWUP_OVERDUE/WARNING_ACTIVE/REFERRING/PAUSED/CLOSED)',
  `source` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '来源(OUTPATIENT/SCREENING/HIS_SYNC/TRANSFER)',
  `insurance_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '医保类型',
  `emergency_contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '紧急联系人姓名',
  `emergency_contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '紧急联系人电话',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`patient_id`) USING BTREE,
  INDEX `idx_patient_org_id`(`org_id` ASC) USING BTREE,
  INDEX `idx_patient_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_patient_tenant_org`(`tenant_id` ASC, `org_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1023 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '患者档案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_patient_tag
-- ----------------------------
DROP TABLE IF EXISTS `ch_patient_tag`;
CREATE TABLE `ch_patient_tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `tag_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签类型(RISK/CUSTOM/COMORBIDITY)',
  `tag_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签字典编码 ch_patient_tag_dict.tag_code',
  `tag_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签值',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pt_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_pt_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_patient_tag_code`(`patient_id` ASC, `tag_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '患者标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_patient_tag_dict
-- ----------------------------
DROP TABLE IF EXISTS `ch_patient_tag_dict`;
CREATE TABLE `ch_patient_tag_dict`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tag_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签编码（业务唯一）如 RISK_HIGH / TAG0001',
  `tag_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签名称 如 高危',
  `tag_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签大类（字典 chronic_tag_type）RISK/CUSTOM/COMORBIDITY',
  `category` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '细分类（行为/社会/经济/风险等，可自由扩展）',
  `color` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '前端展示色 如 #FF4D4F',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '状态 0启用 1停用',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID（NULL 表示全局系统预置）',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tag_code_tenant`(`tag_code` ASC, `tenant_id` ASC) USING BTREE,
  INDEX `idx_tag_type`(`tag_type` ASC) USING BTREE,
  INDEX `idx_status_sort`(`status` ASC, `sort_order` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '慢病-患者标签字典' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_patient_timeline
-- ----------------------------
DROP TABLE IF EXISTS `ch_patient_timeline`;
CREATE TABLE `ch_patient_timeline`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `event_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '事件类型(ARCHIVE/SIGN/FOLLOWUP/MEDICATION_ADJUST/WARNING/REFERRAL/PLAN_CHANGE)',
  `event_title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '事件标题',
  `event_detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '事件详情',
  `event_time` datetime NULL DEFAULT NULL COMMENT '事件时间',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_ptl_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_ptl_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 42 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '患者时间线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_referral_record
-- ----------------------------
DROP TABLE IF EXISTS `ch_referral_record`;
CREATE TABLE `ch_referral_record`  (
  `referral_id` bigint NOT NULL AUTO_INCREMENT COMMENT '转诊ID',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `from_org_id` bigint NULL DEFAULT NULL COMMENT '转出机构ID',
  `to_org_id` bigint NULL DEFAULT NULL COMMENT '转入机构ID',
  `to_area_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '转入区域编码',
  `referral_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '转诊原因',
  `referral_category` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '转诊类别',
  `referral_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '转诊状态(PENDING/APPROVED/ACCEPTED/REJECTED/COMPLETED)',
  `referral_time` datetime NULL DEFAULT NULL COMMENT '转诊时间',
  `referral_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '转诊类型(UPWARD/DOWNWARD/TOWNSHIP)',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`referral_id`) USING BTREE,
  INDEX `idx_rr_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_rr_from_org_id`(`from_org_id` ASC) USING BTREE,
  INDEX `idx_rr_to_org_id`(`to_org_id` ASC) USING BTREE,
  INDEX `idx_rr_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_rr_tenant_org`(`tenant_id` ASC, `from_org_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '转诊记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_report_instance
-- ----------------------------
DROP TABLE IF EXISTS `ch_report_instance`;
CREATE TABLE `ch_report_instance`  (
  `report_id` bigint NOT NULL AUTO_INCREMENT COMMENT '报告ID',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `template_id` bigint NULL DEFAULT NULL COMMENT '模板ID',
  `report_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '报告状态(GENERATING/COMPLETED/FAILED)',
  `pdf_file_id` bigint NULL DEFAULT NULL COMMENT 'PDF文件ID',
  `qr_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '二维码',
  `sign_status` tinyint(1) NULL DEFAULT 0 COMMENT '签名状态',
  `sign_time` datetime NULL DEFAULT NULL COMMENT '签名时间',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  `report_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '报告类型(ANNUAL/FOLLOWUP/SPECIAL)',
  `pdf_oss_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'PDF文件OSS ID',
  `qr_code_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '二维码内容',
  `push_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '推送状态(PENDING/PUSHED/FAILED)',
  PRIMARY KEY (`report_id`) USING BTREE,
  INDEX `idx_ri_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_ri_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '报告实例表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_report_template
-- ----------------------------
DROP TABLE IF EXISTS `ch_report_template`;
CREATE TABLE `ch_report_template`  (
  `template_id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模板名称',
  `template_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模板类型(ANNUAL_CHECKUP/QUARTERLY_MANAGE/AREA_STAT)',
  `template_content` json NULL COMMENT '模板内容',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`template_id`) USING BTREE,
  INDEX `idx_rt_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '报告模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_risk_assessment
-- ----------------------------
DROP TABLE IF EXISTS `ch_risk_assessment`;
CREATE TABLE `ch_risk_assessment`  (
  `assessment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '评估ID',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `disease_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '疾病编码',
  `risk_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '风险等级(LOW/MEDIUM/HIGH/VERY_HIGH)',
  `assessment_report` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评估报告',
  `assessor_user_id` bigint NULL DEFAULT NULL COMMENT '评估人用户ID',
  `org_id` bigint NULL DEFAULT NULL COMMENT '机构ID',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`assessment_id`) USING BTREE,
  INDEX `idx_ra_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_ra_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_ra_tenant_org_disease`(`tenant_id` ASC, `org_id` ASC, `disease_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '风险评估表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_risk_factor_item
-- ----------------------------
DROP TABLE IF EXISTS `ch_risk_factor_item`;
CREATE TABLE `ch_risk_factor_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `assessment_id` bigint NULL DEFAULT NULL COMMENT '评估ID',
  `factor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '因子名称',
  `factor_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '因子值',
  `factor_weight` decimal(5, 2) NULL DEFAULT NULL COMMENT '因子权重',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_rfi_assessment_id`(`assessment_id` ASC) USING BTREE,
  INDEX `idx_rfi_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '风险因子项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_screening_batch
-- ----------------------------
DROP TABLE IF EXISTS `ch_screening_batch`;
CREATE TABLE `ch_screening_batch`  (
  `batch_id` bigint NOT NULL AUTO_INCREMENT COMMENT '批次ID',
  `batch_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '批次名称',
  `activity_date` date NULL DEFAULT NULL COMMENT '活动日期',
  `org_id` bigint NULL DEFAULT NULL COMMENT '机构ID',
  `doctor_user_id` bigint NULL DEFAULT NULL COMMENT '医生用户ID',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '筛查地点',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`batch_id`) USING BTREE,
  INDEX `idx_sb_org_id`(`org_id` ASC) USING BTREE,
  INDEX `idx_sb_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_sb_tenant_org`(`tenant_id` ASC, `org_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '筛查批次表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_screening_record
-- ----------------------------
DROP TABLE IF EXISTS `ch_screening_record`;
CREATE TABLE `ch_screening_record`  (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `batch_id` bigint NULL DEFAULT NULL COMMENT '批次ID',
  `offline_uuid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '离线唯一标识',
  `patient_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '患者姓名',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证号',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `gender` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '性别',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `symptoms` json NULL COMMENT '症状',
  `vitals` json NULL COMMENT '体征',
  `risk_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '风险等级(LOW/MEDIUM/HIGH/VERY_HIGH)',
  `enroll_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '入组状态(PENDING/ENROLLED/REJECTED)',
  `enrolled_patient_id` bigint NULL DEFAULT NULL COMMENT '入组患者ID',
  `upload_time` datetime NULL DEFAULT NULL COMMENT '上传时间',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`record_id`) USING BTREE,
  UNIQUE INDEX `uk_offline_uuid`(`offline_uuid` ASC) USING BTREE,
  INDEX `idx_sr_batch_id`(`batch_id` ASC) USING BTREE,
  INDEX `idx_sr_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '筛查记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_sos_record
-- ----------------------------
DROP TABLE IF EXISTS `ch_sos_record`;
CREATE TABLE `ch_sos_record`  (
  `sos_id` bigint NOT NULL AUTO_INCREMENT COMMENT '求助ID',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `gps_lng` decimal(10, 6) NULL DEFAULT NULL COMMENT 'GPS经度',
  `gps_lat` decimal(10, 6) NULL DEFAULT NULL COMMENT 'GPS纬度',
  `gps_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '反向地理编码地址',
  `notify_doctor_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '通知医生状态(PENDING/SENT/FAILED)',
  `notify_emergency_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '通知紧急联系人状态(PENDING/SENT/FAILED)',
  `notify_channel_summary` json NULL COMMENT '通知渠道明细',
  `event_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '事件状态(NEW/HANDLING/RESOLVED/FALSE_ALARM)',
  `handler_user_id` bigint NULL DEFAULT NULL COMMENT '处置人用户ID',
  `handle_time` datetime NULL DEFAULT NULL COMMENT '处置时间',
  `handle_remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '处置备注',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`sos_id`) USING BTREE,
  INDEX `idx_sos_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_sos_event_status`(`event_status` ASC) USING BTREE,
  INDEX `idx_sos_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '紧急求助记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_stat_area_day
-- ----------------------------
DROP TABLE IF EXISTS `ch_stat_area_day`;
CREATE TABLE `ch_stat_area_day`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `area_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '区域编码',
  `stat_date` date NULL DEFAULT NULL COMMENT '统计日期',
  `patient_count` bigint NULL DEFAULT 0 COMMENT '患者数',
  `managed_count` bigint NULL DEFAULT 0 COMMENT '管理数',
  `warning_count` bigint NULL DEFAULT 0 COMMENT '预警数',
  `followup_count` bigint NULL DEFAULT 0 COMMENT '随访数',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sad_area_code`(`area_code` ASC) USING BTREE,
  INDEX `idx_sad_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '区域统计日表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_stat_disease_day
-- ----------------------------
DROP TABLE IF EXISTS `ch_stat_disease_day`;
CREATE TABLE `ch_stat_disease_day`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `disease_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '疾病编码',
  `stat_date` date NULL DEFAULT NULL COMMENT '统计日期',
  `patient_count` bigint NULL DEFAULT 0 COMMENT '患者数',
  `new_count` bigint NULL DEFAULT 0 COMMENT '新增数',
  `risk_high_count` bigint NULL DEFAULT 0 COMMENT '高风险数',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sdd_disease_code`(`disease_code` ASC) USING BTREE,
  INDEX `idx_sdd_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '疾病统计日表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_stat_followup_day
-- ----------------------------
DROP TABLE IF EXISTS `ch_stat_followup_day`;
CREATE TABLE `ch_stat_followup_day`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `stat_date` date NULL DEFAULT NULL COMMENT '统计日期',
  `total_count` bigint NULL DEFAULT 0 COMMENT '总随访数',
  `done_count` bigint NULL DEFAULT 0 COMMENT '完成数',
  `overdue_count` bigint NULL DEFAULT 0 COMMENT '逾期数',
  `completion_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '完成率',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sfd_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '随访统计日表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_stat_org_day
-- ----------------------------
DROP TABLE IF EXISTS `ch_stat_org_day`;
CREATE TABLE `ch_stat_org_day`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `org_id` bigint NULL DEFAULT NULL COMMENT '机构ID',
  `stat_date` date NULL DEFAULT NULL COMMENT '统计日期',
  `patient_count` bigint NULL DEFAULT 0 COMMENT '患者数',
  `followup_done_count` bigint NULL DEFAULT 0 COMMENT '随访完成数',
  `warning_count` bigint NULL DEFAULT 0 COMMENT '预警数',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sod_org_id`(`org_id` ASC) USING BTREE,
  INDEX `idx_sod_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_sod_tenant_org`(`tenant_id` ASC, `org_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '机构统计日表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_stat_warning_day
-- ----------------------------
DROP TABLE IF EXISTS `ch_stat_warning_day`;
CREATE TABLE `ch_stat_warning_day`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `stat_date` date NULL DEFAULT NULL COMMENT '统计日期',
  `total_count` bigint NULL DEFAULT 0 COMMENT '总预警数',
  `resolved_count` bigint NULL DEFAULT 0 COMMENT '已解决数',
  `escalated_count` bigint NULL DEFAULT 0 COMMENT '已升级数',
  `avg_resolve_minutes` bigint NULL DEFAULT 0 COMMENT '平均解决时长(分钟)',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_swd_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '预警统计日表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_warning_action
-- ----------------------------
DROP TABLE IF EXISTS `ch_warning_action`;
CREATE TABLE `ch_warning_action`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `warning_id` bigint NULL DEFAULT NULL COMMENT '预警ID',
  `action_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处置类型(CONFIRM/HANDLE/ESCALATE/RESOLVE)',
  `action_detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '处置详情',
  `action_user_id` bigint NULL DEFAULT NULL COMMENT '处置人用户ID',
  `action_time` datetime NULL DEFAULT NULL COMMENT '处置时间',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_wa_warning_id`(`warning_id` ASC) USING BTREE,
  INDEX `idx_wa_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '预警处置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_warning_event
-- ----------------------------
DROP TABLE IF EXISTS `ch_warning_event`;
CREATE TABLE `ch_warning_event`  (
  `warning_id` bigint NOT NULL AUTO_INCREMENT COMMENT '预警ID',
  `patient_id` bigint NULL DEFAULT NULL COMMENT '患者ID',
  `rule_id` bigint NULL DEFAULT NULL COMMENT '规则ID',
  `warning_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '预警等级',
  `warning_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '预警值',
  `warning_time` datetime NULL DEFAULT NULL COMMENT '预警时间',
  `event_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '事件状态(NEW/CONFIRMED/PROCESSING/ESCALATED/RESOLVED/ARCHIVED)',
  `assignee_user_id` bigint NULL DEFAULT NULL COMMENT '处理人用户ID',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`warning_id`) USING BTREE,
  INDEX `idx_we_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_we_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '预警事件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_warning_rule
-- ----------------------------
DROP TABLE IF EXISTS `ch_warning_rule`;
CREATE TABLE `ch_warning_rule`  (
  `rule_id` bigint NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `disease_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '疾病编码',
  `metric_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '指标类型',
  `warning_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '预警等级(LOW/MEDIUM/HIGH/CRITICAL)',
  `threshold_min` decimal(10, 2) NULL DEFAULT NULL COMMENT '阈值下限',
  `threshold_max` decimal(10, 2) NULL DEFAULT NULL COMMENT '阈值上限',
  `consecutive_window` int NULL DEFAULT NULL COMMENT '连续窗口次数',
  `time_window_start` time NULL DEFAULT NULL COMMENT '时间窗口开始',
  `time_window_end` time NULL DEFAULT NULL COMMENT '时间窗口结束',
  `recovery_rule` json NULL COMMENT '恢复规则',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '规则描述',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `org_id` bigint NULL DEFAULT NULL COMMENT '机构ID',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`rule_id`) USING BTREE,
  INDEX `idx_wr_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_wr_tenant_org_disease`(`tenant_id` ASC, `org_id` ASC, `disease_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '预警规则表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ch_webhook_subscription
-- ----------------------------
DROP TABLE IF EXISTS `ch_webhook_subscription`;
CREATE TABLE `ch_webhook_subscription`  (
  `sub_id` bigint NOT NULL AUTO_INCREMENT COMMENT '订阅ID',
  `third_party_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '第三方系统名称',
  `event_types` json NULL COMMENT '订阅事件列表(如[WARNING_CREATED, FOLLOWUP_DONE])',
  `callback_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '回调地址',
  `signature_secret` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '签名密钥(HMAC 签名校验)',
  `retry_max` int NULL DEFAULT 5 COMMENT '最大重试次数',
  `retry_strategy` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '重试策略(EXPONENTIAL_BACKOFF/LINEAR)',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `last_invoke_time` datetime NULL DEFAULT NULL COMMENT '最近推送时间',
  `last_invoke_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最近推送状态',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`sub_id`) USING BTREE,
  INDEX `idx_ws_is_active`(`is_active` ASC) USING BTREE,
  INDEX `idx_ws_tenant_id`(`tenant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Webhook订阅表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
