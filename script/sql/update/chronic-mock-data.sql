-- =============================================
-- unimed-chronic 慢病管理系统 测试数据初始化脚本
-- =============================================
-- 说明：
--   1. 在 `unimed-chronic` 数据库中执行
--   2. 幂等设计：先 DELETE 再 INSERT，可重复执行
--   3. 覆盖 62 张业务表核心数据，形成完整业务闭环
--   4. 固定 ID 段，便于联调追踪
--      - 患者 ID:  1001-1010    (10 个)
--      - 医生 user_id: 2001-2010 (10 个)
--      - 机构 org_id: 3001-3005  (5 个)
--      - 团队 ID:  4001-4003    (3 个)
--      - 服务包 ID: 5001-5003
--   5. 租户 ID 默认 0 (tenant_id=0)
--   6. 涵盖 9 大病种：HYPERTENSION/DIABETES/HYPERLIPIDEMIA/CHD/STROKE/
--      NEPHROTIC/FUNDUS/NEUROPATHY/VASCULOPATHY
-- =============================================

USE `unimed-chronic`;

SET @tid = 0;
SET @cb  = 1;
SET @now = NOW();

-- =============================================
-- 清空业务数据（保留表结构）
-- =============================================
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `ch_patient_profile`;
TRUNCATE TABLE `ch_patient_disease`;
TRUNCATE TABLE `ch_patient_tag`;
TRUNCATE TABLE `ch_patient_timeline`;
TRUNCATE TABLE `ch_disease_config`;
TRUNCATE TABLE `ch_disease_relation`;
TRUNCATE TABLE `ch_icd_dict`;
TRUNCATE TABLE `ch_screening_batch`;
TRUNCATE TABLE `ch_screening_record`;
TRUNCATE TABLE `ch_patient_contract`;
TRUNCATE TABLE `ch_contract_service_package`;
TRUNCATE TABLE `ch_contract_fulfillment`;
TRUNCATE TABLE `ch_doctor_team`;
TRUNCATE TABLE `ch_doctor_team_member`;
TRUNCATE TABLE `ch_medication_record`;
TRUNCATE TABLE `ch_medication_adjust`;
TRUNCATE TABLE `ch_drug_interaction`;
TRUNCATE TABLE `ch_risk_assessment`;
TRUNCATE TABLE `ch_risk_factor_item`;
TRUNCATE TABLE `ch_assessment_rule`;
TRUNCATE TABLE `ch_manage_level_record`;
TRUNCATE TABLE `ch_manage_plan`;
TRUNCATE TABLE `ch_manage_plan_item`;
TRUNCATE TABLE `ch_followup_plan`;
TRUNCATE TABLE `ch_followup_plan_item`;
TRUNCATE TABLE `ch_followup_task`;
TRUNCATE TABLE `ch_followup_record`;
TRUNCATE TABLE `ch_followup_questionnaire`;
TRUNCATE TABLE `ch_followup_answer`;
TRUNCATE TABLE `ch_health_metric_record`;
TRUNCATE TABLE `ch_device_raw_record`;
TRUNCATE TABLE `ch_device_bind`;
TRUNCATE TABLE `ch_lifestyle_record`;
TRUNCATE TABLE `ch_health_exam`;
TRUNCATE TABLE `ch_health_exam_item`;
TRUNCATE TABLE `ch_warning_rule`;
TRUNCATE TABLE `ch_warning_event`;
TRUNCATE TABLE `ch_warning_action`;
TRUNCATE TABLE `ch_referral_record`;
TRUNCATE TABLE `ch_archive_share_apply`;
TRUNCATE TABLE `ch_external_sync_log`;
TRUNCATE TABLE `ch_message_session`;
TRUNCATE TABLE `ch_message_content`;
TRUNCATE TABLE `ch_health_education_content`;
TRUNCATE TABLE `ch_health_education_delivery`;
TRUNCATE TABLE `ch_education_rule`;
TRUNCATE TABLE `ch_notification_template`;
TRUNCATE TABLE `ch_report_template`;
TRUNCATE TABLE `ch_report_instance`;
TRUNCATE TABLE `ch_area_dict`;
TRUNCATE TABLE `ch_org_area_mapping`;
TRUNCATE TABLE `ch_stat_area_day`;
TRUNCATE TABLE `ch_kpi_definition`;
TRUNCATE TABLE `ch_consent_record`;
TRUNCATE TABLE `ch_audit_log`;
TRUNCATE TABLE `ch_file_attachment`;
TRUNCATE TABLE `ch_doctor_wechat_bind`;
TRUNCATE TABLE `ch_patient_account`;
TRUNCATE TABLE `ch_stat_disease_day`;
TRUNCATE TABLE `ch_stat_org_day`;
TRUNCATE TABLE `ch_stat_warning_day`;
TRUNCATE TABLE `ch_stat_followup_day`;
SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- 1. 行政区划字典（山东省 济南市 及部分县乡村）
-- =============================================
INSERT INTO `ch_area_dict` (`area_code`, `area_name`, `area_level`, `parent_area_code`, `create_by`, `create_time`) VALUES
('370000', '山东省',       1, NULL,     @cb, @now),
('370100', '济南市',       2, '370000', @cb, @now),
('370102', '历下区',       3, '370100', @cb, @now),
('370103', '市中区',       3, '370100', @cb, @now),
('370104', '槐荫区',       3, '370100', @cb, @now),
('370105', '天桥区',       3, '370100', @cb, @now),
('370112', '历城区',       3, '370100', @cb, @now),
('370113', '长清区',       3, '370100', @cb, @now),
('37010201', '大明湖街道', 4, '370102', @cb, @now),
('37010202', '泉城路街道', 4, '370102', @cb, @now),
('37010301', '二七街道',   4, '370103', @cb, @now),
('3701020101', '大明湖社区', 5, '37010201', @cb, @now),
('3701020102', '后宰门社区', 5, '37010201', @cb, @now);

-- =============================================
-- 2. 机构-区域映射
-- =============================================
INSERT INTO `ch_org_area_mapping` (`org_id`, `area_code`, `tenant_id`, `create_by`, `create_time`) VALUES
(3001, '370102', @tid, @cb, @now),  -- 省立医院 -> 历下区
(3002, '370103', @tid, @cb, @now),  -- 市中心医院 -> 市中区
(3003, '370104', @tid, @cb, @now),  -- 槐荫社区卫生中心 -> 槐荫区
(3004, '37010201', @tid, @cb, @now),-- 大明湖社区卫生站 -> 大明湖街道
(3005, '37010301', @tid, @cb, @now);-- 二七街道卫生服务中心 -> 二七街道

-- =============================================
-- 3. ICD 字典
-- =============================================
INSERT INTO `ch_icd_dict` (`icd_code`, `icd_version`, `icd_name_cn`, `icd_name_en`, `category`, `tenant_id`, `create_by`, `create_time`) VALUES
('I10',   'ICD10', '原发性高血压',          'Essential hypertension',                  'CIRCULATORY', @tid, @cb, @now),
('E11',   'ICD10', '2型糖尿病',             'Type 2 diabetes mellitus',                'ENDOCRINE',   @tid, @cb, @now),
('E78',   'ICD10', '脂蛋白代谢紊乱及其他脂血症', 'Disorders of lipoprotein metabolism',   'ENDOCRINE',   @tid, @cb, @now),
('I25',   'ICD10', '慢性缺血性心脏病',      'Chronic ischemic heart disease',          'CIRCULATORY', @tid, @cb, @now),
('I63',   'ICD10', '脑梗死',                'Cerebral infarction',                     'CIRCULATORY', @tid, @cb, @now),
('N04',   'ICD10', '肾病综合征',            'Nephrotic syndrome',                      'URINARY',     @tid, @cb, @now),
('H36.0', 'ICD10', '糖尿病性视网膜病变',    'Diabetic retinopathy',                    'EYE',         @tid, @cb, @now),
('G63.2', 'ICD10', '糖尿病性多发性神经病',  'Diabetic polyneuropathy',                 'NERVOUS',     @tid, @cb, @now),
('I79.2', 'ICD10', '糖尿病性周围血管病变',  'Peripheral angiopathy in diabetes',       'CIRCULATORY', @tid, @cb, @now);

-- =============================================
-- 4. 病种配置（9 病种）
-- =============================================
INSERT INTO `ch_disease_config` (`disease_code`, `disease_name`, `disease_category`, `is_primary`, `parent_disease_code`, `monitor_items`, `is_active`, `org_id`, `tenant_id`, `create_by`, `create_time`) VALUES
('HYPERTENSION',   '高血压',           'PRIMARY', 1, NULL,           '["SBP","DBP","HR"]',                  1, 3001, @tid, @cb, @now),
('DIABETES',       '糖尿病',           'PRIMARY', 1, NULL,           '["FBG","PBG","HBA1C"]',               1, 3001, @tid, @cb, @now),
('HYPERLIPIDEMIA', '高血脂',           'PRIMARY', 1, NULL,           '["TC","TG","LDL","HDL"]',             1, 3001, @tid, @cb, @now),
('CHD',            '冠心病',           'PRIMARY', 1, NULL,           '["ECG","BP","HR"]',                   1, 3001, @tid, @cb, @now),
('STROKE',         '脑卒中',           'PRIMARY', 1, NULL,           '["BP","NIHSS","MRS"]',                1, 3001, @tid, @cb, @now),
('NEPHROTIC',      '肾病综合征',       'PRIMARY', 1, NULL,           '["EGFR","URINE_PROTEIN","CREATININE"]', 1, 3001, @tid, @cb, @now),
('FUNDUS',         '眼底病变',         'COMPLICATION', 0, 'DIABETES','["DR_GRADE","VISION"]',               1, 3001, @tid, @cb, @now),
('NEUROPATHY',     '周围神经病变',     'COMPLICATION', 0, 'DIABETES','["TCSS","NERVE_CONDUCTION"]',         1, 3001, @tid, @cb, @now),
('VASCULOPATHY',   '周围血管病变',     'COMPLICATION', 0, 'DIABETES','["ABI","ANKLE_ARM"]',                 1, 3001, @tid, @cb, @now);

-- =============================================
-- 5. 病种关联关系（主病-并发症）
-- =============================================
INSERT INTO `ch_disease_relation` (`parent_disease_code`, `complication_disease_code`, `relation_type`, `is_active`, `tenant_id`, `create_by`, `create_time`) VALUES
('DIABETES',     'FUNDUS',       'COMPLICATION', 1, @tid, @cb, @now),
('DIABETES',     'NEUROPATHY',   'COMPLICATION', 1, @tid, @cb, @now),
('DIABETES',     'VASCULOPATHY', 'COMPLICATION', 1, @tid, @cb, @now),
('HYPERTENSION', 'FUNDUS',       'COMPLICATION', 1, @tid, @cb, @now),
('HYPERTENSION', 'CHD',          'COMORBIDITY',  1, @tid, @cb, @now),
('HYPERTENSION', 'STROKE',       'COMORBIDITY',  1, @tid, @cb, @now);

-- =============================================
-- 6. 药物相互作用（常见规则）
-- =============================================
INSERT INTO `ch_drug_interaction` (`drug_code_a`, `drug_code_b`, `interaction_level`, `description`, `clinical_advice`, `tenant_id`, `create_by`, `create_time`) VALUES
('ACEI',        'ARB',          'CONTRAINDICATED', 'ACEI与ARB联用增加高钾血症、肾功能损伤风险', '避免联用，选择其中一种', @tid, @cb, @now),
('WARFARIN',    'ASPIRIN',      'MAJOR_RISK',      '华法林与阿司匹林联用显著增加出血风险',     '密切监测INR，必要时调整剂量', @tid, @cb, @now),
('METFORMIN',   'CONTRAST',     'CONTRAINDICATED', '二甲双胍与含碘造影剂联用增加乳酸酸中毒风险', '造影前48小时停用二甲双胍', @tid, @cb, @now),
('STATIN',      'FIBRATE',      'MONITOR',         '他汀与贝特类联用增加横纹肌溶解风险',       '监测CK及肝功能',           @tid, @cb, @now),
('AMLODIPINE',  'SIMVASTATIN',  'MONITOR',         '氨氯地平增加辛伐他汀血药浓度',             '辛伐他汀日剂量不超过20mg', @tid, @cb, @now);

-- =============================================
-- 7. 风险评估规则（高血压/糖尿病维度）
-- =============================================
INSERT INTO `ch_assessment_rule` (`rule_id`, `disease_code`, `dimension_name`, `dimension_weight`, `threshold_config`, `tenant_id`, `create_by`, `create_time`) VALUES
(7001, 'HYPERTENSION', 'SBP',        0.40, '{"min":140,"max":179,"score":20}', @tid, @cb, @now),
(7002, 'HYPERTENSION', 'DBP',        0.30, '{"min":90,"max":109,"score":15}',  @tid, @cb, @now),
(7003, 'HYPERTENSION', 'AGE',        0.15, '{"min":55,"score":5}',             @tid, @cb, @now),
(7004, 'HYPERTENSION', 'SMOKING',    0.15, '{"equals":"YES","score":10}',      @tid, @cb, @now),
(7011, 'DIABETES',     'FBG',        0.40, '{"min":7.0,"max":11.0,"score":25}', @tid, @cb, @now),
(7012, 'DIABETES',     'HBA1C',      0.35, '{"min":7.0,"max":9.0,"score":20}',  @tid, @cb, @now),
(7013, 'DIABETES',     'BMI',        0.25, '{"min":28.0,"score":10}',           @tid, @cb, @now);

-- =============================================
-- 8. 预警规则（各病种关键指标阈值）
-- =============================================
INSERT INTO `ch_warning_rule` (`rule_id`, `disease_code`, `metric_type`, `warning_level`, `threshold_min`, `threshold_max`, `consecutive_window`, `org_id`, `tenant_id`, `create_by`, `create_time`) VALUES
(8001, 'HYPERTENSION', 'SBP',   'HIGH',     140, 179, 3, 3001, @tid, @cb, @now),
(8002, 'HYPERTENSION', 'SBP',   'CRITICAL', 180, 300, 1, 3001, @tid, @cb, @now),
(8003, 'HYPERTENSION', 'DBP',   'HIGH',     90,  109, 3, 3001, @tid, @cb, @now),
(8004, 'HYPERTENSION', 'DBP',   'CRITICAL', 110, 200, 1, 3001, @tid, @cb, @now),
(8011, 'DIABETES',     'FBG',   'HIGH',     7.0, 11.0, 3, 3001, @tid, @cb, @now),
(8012, 'DIABETES',     'FBG',   'CRITICAL', 16.7, 40,  1, 3001, @tid, @cb, @now),
(8013, 'DIABETES',     'FBG',   'CRITICAL', 0,   3.9,  1, 3001, @tid, @cb, @now),
(8021, 'CHD',          'HR',    'HIGH',     100, 150, 3, 3001, @tid, @cb, @now);

-- =============================================
-- 9. 随访问卷（国家公卫第3版标准）
-- =============================================
INSERT INTO `ch_followup_questionnaire` (`questionnaire_id`, `disease_code`, `questionnaire_name`, `version`, `questions`, `is_national_standard`, `tenant_id`, `create_by`, `create_time`) VALUES
(9001, 'HYPERTENSION', '高血压患者随访服务记录表(国家公卫v3)', 3,
 '[{"id":"q1","type":"number","title":"收缩压(mmHg)","required":true},{"id":"q2","type":"number","title":"舒张压(mmHg)","required":true},{"id":"q3","type":"radio","title":"症状","options":["无症状","头痛","头晕","胸闷","心悸"]},{"id":"q4","type":"radio","title":"服药依从性","options":["规律","间断","不服药"]}]',
 1, @tid, @cb, @now),
(9002, 'DIABETES', '2型糖尿病患者随访服务记录表(国家公卫v3)', 3,
 '[{"id":"q1","type":"number","title":"空腹血糖(mmol/L)","required":true},{"id":"q2","type":"number","title":"糖化血红蛋白(%)","required":false},{"id":"q3","type":"radio","title":"低血糖反应","options":["无","偶尔","频繁"]},{"id":"q4","type":"checkbox","title":"并发症筛查","options":["视网膜","肾脏","神经","血管"]}]',
 1, @tid, @cb, @now);

-- =============================================
-- 10. 健康教育内容
-- =============================================
INSERT INTO `ch_health_education_content` (`content_id`, `title`, `content_body`, `tags`, `tenant_id`, `create_by`, `create_time`) VALUES
(10001, '高血压患者饮食指南：低盐低脂的日常选择',       '每日食盐摄入不超过6克，多吃蔬菜水果，少吃腌制食品...', '["高血压","饮食","慢病"]', @tid, @cb, @now),
(10002, '糖尿病运动处方：每周150分钟中等强度运动',     '推荐快走、游泳、骑自行车等有氧运动，每周5天，每次30分钟...', '["糖尿病","运动"]', @tid, @cb, @now),
(10003, '冬季心脑血管保健提示',                         '注意保暖、避免清晨剧烈运动、按时服药、监测血压...',   '["冠心病","脑卒中","季节"]', @tid, @cb, @now),
(10004, '糖尿病足自我检查五步法',                       '每日检查足部皮肤、温度、感觉、血管搏动、趾甲...',       '["糖尿病","并发症","足部"]', @tid, @cb, @now),
(10005, '高温预警：慢病患者如何安全度夏',               '多饮水、避免正午外出、调整用药时间、警惕低血压...',     '["季节","高温","慢病"]', @tid, @cb, @now);

-- =============================================
-- 11. 教育推送规则
-- =============================================
INSERT INTO `ch_education_rule` (`rule_id`, `condition_expression`, `template_id`, `push_channel`, `is_active`, `tenant_id`, `create_by`, `create_time`) VALUES
(11001, '{"disease":"HYPERTENSION","riskLevel":["HIGH","VERY_HIGH"]}', 10001, 'WECHAT', 1, @tid, @cb, @now),
(11002, '{"disease":"DIABETES"}',                                      10002, 'WECHAT', 1, @tid, @cb, @now),
(11003, '{"trigger":"SEASONAL","season":"WINTER"}',                    10003, 'SMS',    1, @tid, @cb, @now),
(11004, '{"disease":"DIABETES","complication":"NEUROPATHY"}',          10004, 'WECHAT', 1, @tid, @cb, @now),
(11005, '{"trigger":"WEATHER","condition":"HIGH_TEMP"}',               10005, 'SMS',    1, @tid, @cb, @now);

-- =============================================
-- 12. 通知模板
-- =============================================
INSERT INTO `ch_notification_template` (`template_id`, `channel`, `template_code`, `template_content`, `tenant_id`, `create_by`, `create_time`) VALUES
(12001, 'WECHAT', 'FOLLOWUP_REMIND',   '尊敬的{name}，您的随访任务即将到期({dueDate})，请及时联系您的责任医生。', @tid, @cb, @now),
(12002, 'SMS',    'WARNING_CRITICAL',  '[慢病管理]{name}患者血压/血糖危急值({value}{unit})，请立即处理。',     @tid, @cb, @now),
(12003, 'WECHAT', 'REPORT_READY',      '您的{reportType}报告已生成，请点击查看。',                            @tid, @cb, @now),
(12004, 'IVR',    'MEDICATION_REMIND', '您好，请记得按时服用{drug}。',                                         @tid, @cb, @now);

-- =============================================
-- 13. 报告模板
-- =============================================
INSERT INTO `ch_report_template` (`template_id`, `template_name`, `template_type`, `template_content`, `tenant_id`, `create_by`, `create_time`) VALUES
(13001, '年度健康体检报告',         'ANNUAL_CHECKUP',    '{"sections":["基本信息","体检结果","风险评估","健康建议"]}', @tid, @cb, @now),
(13002, '季度慢病管理报告',         'QUARTERLY_MANAGE',  '{"sections":["随访情况","用药变化","指标趋势","管理建议"]}', @tid, @cb, @now),
(13003, '半年度区域监管报告',       'AREA_STAT',         '{"sections":["区域概况","病种分布","管理成效","KPI"]}',     @tid, @cb, @now);

-- =============================================
-- 14. KPI 指标定义（公卫考核指标）
-- =============================================
INSERT INTO `ch_kpi_definition` (`kpi_id`, `kpi_code`, `kpi_name`, `kpi_formula`, `kpi_category`, `tenant_id`, `create_by`, `create_time`) VALUES
(14001, 'HTN_MGR_RATE',   '高血压规范管理率', '(规范管理人数 / 高血压登记人数) * 100%',   'MANAGEMENT_RATE', @tid, @cb, @now),
(14002, 'HTN_CTRL_RATE',  '高血压血压控制率', '(血压<140/90人数 / 管理人数) * 100%',     'CONTROL_RATE',    @tid, @cb, @now),
(14003, 'DM_MGR_RATE',    '糖尿病规范管理率', '(规范管理人数 / 糖尿病登记人数) * 100%',   'MANAGEMENT_RATE', @tid, @cb, @now),
(14004, 'DM_CTRL_RATE',   '糖尿病血糖控制率', '(FBG<7.0人数 / 管理人数) * 100%',          'CONTROL_RATE',    @tid, @cb, @now),
(14005, 'FOLLOWUP_COMPL', '随访完成率',       '(完成随访任务数 / 计划任务数) * 100%',     'COMPLIANCE_RATE', @tid, @cb, @now);

-- =============================================
-- 15. 服务包
-- =============================================
INSERT INTO `ch_contract_service_package` (`package_id`, `package_name`, `package_type`, `service_items`, `price`, `tenant_id`, `create_by`, `create_time`) VALUES
(5001, '基础型慢病签约包', 'BASIC',    '["年度体检1次","季度随访4次","健康咨询"]',                       120.00, @tid, @cb, @now),
(5002, '进阶型慢病签约包', 'ADVANCED', '["年度体检1次","月度随访12次","健康咨询","设备监测","用药指导"]', 380.00, @tid, @cb, @now),
(5003, '定制型慢病签约包', 'CUSTOM',   '["个性化定制"]',                                                 500.00, @tid, @cb, @now);

-- =============================================
-- 16. 医生团队
-- =============================================
INSERT INTO `ch_doctor_team` (`team_id`, `team_name`, `org_id`, `dept_id`, `leader_user_id`, `team_status`, `tenant_id`, `create_by`, `create_time`) VALUES
(4001, '省立医院心内科家医团队', 3001, 100001, 2001, 'ACTIVE',    @tid, @cb, @now),
(4002, '省立医院内分泌科团队',   3001, 100002, 2002, 'ACTIVE',    @tid, @cb, @now),
(4003, '大明湖社区卫生家医团队', 3004, 100003, 2003, 'ACTIVE',    @tid, @cb, @now);

INSERT INTO `ch_doctor_team_member` (`team_id`, `user_id`, `member_role`, `tenant_id`, `create_by`, `create_time`) VALUES
(4001, 2001, 'LEADER', @tid, @cb, @now),
(4001, 2004, 'MEMBER', @tid, @cb, @now),
(4001, 2005, 'MEMBER', @tid, @cb, @now),
(4002, 2002, 'LEADER', @tid, @cb, @now),
(4002, 2006, 'MEMBER', @tid, @cb, @now),
(4003, 2003, 'LEADER', @tid, @cb, @now),
(4003, 2007, 'MEMBER', @tid, @cb, @now),
(4003, 2008, 'MEMBER', @tid, @cb, @now);

-- 医生微信绑定（示例）
INSERT INTO `ch_doctor_wechat_bind` (`user_id`, `openid`, `unionid`, `bind_time`, `tenant_id`, `create_by`, `create_time`) VALUES
(2001, 'wx_openid_doctor_2001', 'wx_unionid_2001', @now, @tid, @cb, @now),
(2002, 'wx_openid_doctor_2002', 'wx_unionid_2002', @now, @tid, @cb, @now),
(2003, 'wx_openid_doctor_2003', 'wx_unionid_2003', @now, @tid, @cb, @now);

-- =============================================
-- 17. 患者档案（10 个患者，覆盖 9 病种）
-- =============================================
INSERT INTO `ch_patient_profile`
(`patient_id`, `name`, `id_card`, `gender`, `birthday`, `phone`, `address`, `gis_lng`, `gis_lat`, `nation`, `occupation`, `education_level`, `surgery_history`, `trauma_history`, `transfusion_history`, `genetic_history`, `disability_type`, `disability_level`, `assistive_device`, `smoking_index`, `drinking_amount`, `org_id`, `dept_id`, `doctor_user_id`, `manage_status`, `source`, `tenant_id`, `create_by`, `create_time`) VALUES
(1001, '张建国', '370102196501011234', 'M', '1965-01-01', '13800000001', '济南市历下区大明湖路88号',  117.017, 36.675, '汉族', '退休工人', '初中', '阑尾炎手术2010年', NULL, NULL, '父亲高血压',        NULL, NULL, NULL, 400, '每日白酒2两', 3001, 100001, 2001, 'MANAGED',          'OUTPATIENT', @tid, @cb, @now),
(1002, '李淑芬', '370102195803152345', 'F', '1958-03-15', '13800000002', '济南市历下区泉城路20号',    117.020, 36.668, '汉族', '退休教师', '本科', NULL,             NULL, NULL, '母亲糖尿病',          NULL, NULL, NULL,   0, '不饮酒',      3001, 100002, 2002, 'MANAGED',          'OUTPATIENT', @tid, @cb, @now),
(1003, '王立军', '370103197206104567', 'M', '1972-06-10', '13800000003', '济南市市中区经四路100号',    117.000, 36.650, '汉族', '公司职员', '大专', NULL,             NULL, NULL, NULL,                NULL, NULL, NULL, 600, '每周啤酒5次', 3002, 100001, 2004, 'WARNING_ACTIVE',   'HIS_SYNC',   @tid, @cb, @now),
(1004, '刘秀英', '370102194511236789', 'F', '1945-11-23', '13800000004', '济南市历下区解放路50号',    117.025, 36.672, '汉族', '退休',     '高中', '胆囊切除2018年',   NULL, '2015年输血200ml','父亲冠心病',    NULL, NULL, NULL,   0, '不饮酒',      3001, 100001, 2001, 'MANAGED',          'OUTPATIENT', @tid, @cb, @now),
(1005, '陈国强', '370112196808287890', 'M', '1968-08-28', '13800000005', '济南市历城区工业南路38号',  117.080, 36.678, '汉族', '工程师',   '本科', NULL,             '2020年车祸轻伤', NULL,       NULL,                NULL, NULL, NULL, 350, '每周白酒3次', 3001, 100003, 2005, 'FOLLOWUP_OVERDUE', 'OUTPATIENT', @tid, @cb, @now),
(1006, '赵玉兰', '370104195207198901', 'F', '1952-07-19', '13800000006', '济南市槐荫区经十路200号',    117.040, 36.668, '汉族', '退休',     '初中', NULL,             NULL, NULL, '兄弟脑卒中',          '视力',   '三级', '助视器',   0, '不饮酒',      3003, 100004, 2003, 'MANAGED',          'SCREENING',  @tid, @cb, @now),
(1007, '孙建华', '370105196003029012', 'M', '1960-03-02', '13800000007', '济南市天桥区清河路88号',    117.030, 36.690, '汉族', '退休',     '初中', '心脏支架2022年',   NULL, NULL, NULL,                NULL, NULL, NULL, 500, '每日白酒3两', 3002, 100001, 2004, 'MANAGED',          'OUTPATIENT', @tid, @cb, @now),
(1008, '周美华', '370102195109130123', 'F', '1951-09-13', '13800000008', '济南市历下区趵突泉北路66号',117.018, 36.670, '汉族', '退休医生', '本科', NULL,             NULL, NULL, '家族性高脂血症',      NULL, NULL, NULL,   0, '不饮酒',      3001, 100002, 2002, 'MANAGED',          'OUTPATIENT', @tid, @cb, @now),
(1009, '黄志刚', '370113197501234567', 'M', '1975-01-23', '13800000009', '济南市长清区大学路10号',    117.120, 36.550, '汉族', '教师',     '硕士', NULL,             NULL, NULL, NULL,                NULL, NULL, NULL, 200, '每周啤酒2次', 3001, 100001, 2001, 'PENDING_ENTRY',    'SCREENING',  @tid, @cb, @now),
(1010, '吴桂珍', '370102194810055678', 'F', '1948-10-05', '13800000010', '济南市历下区文化东路156号',117.028, 36.665, '汉族', '退休',     '小学', NULL,             NULL, NULL, NULL,                NULL, NULL, NULL,   0, '不饮酒',      3001, 100002, 2002, 'REFERRING',        'TRANSFER',   @tid, @cb, @now);

-- =============================================
-- 18. 患者疾病关联
-- =============================================
INSERT INTO `ch_patient_disease` (`patient_id`, `disease_code`, `icd_code`, `diagnosis_basis`, `confirm_date`, `is_complication`, `parent_disease_code`, `org_id`, `tenant_id`, `create_by`, `create_time`) VALUES
(1001, 'HYPERTENSION',   'I10',   '诊室血压连续3次>140/90', '2020-03-15', 0, NULL,       3001, @tid, @cb, @now),
(1002, 'DIABETES',       'E11',   '空腹血糖>7.0mmol/L',    '2018-06-20', 0, NULL,       3001, @tid, @cb, @now),
(1002, 'FUNDUS',         'H36.0', '糖尿病病史+眼底检查',    '2022-09-10', 1, 'DIABETES', 3001, @tid, @cb, @now),
(1003, 'HYPERTENSION',   'I10',   'HIS确诊同步',           '2023-05-01', 0, NULL,       3002, @tid, @cb, @now),
(1003, 'DIABETES',       'E11',   'HIS确诊同步',           '2023-05-01', 0, NULL,       3002, @tid, @cb, @now),
(1004, 'CHD',            'I25',   '冠脉造影+病史',         '2019-11-08', 0, NULL,       3001, @tid, @cb, @now),
(1005, 'HYPERLIPIDEMIA', 'E78',   'TC>6.2mmol/L',          '2021-04-12', 0, NULL,       3001, @tid, @cb, @now),
(1006, 'STROKE',         'I63',   '头颅CT+病史',           '2022-01-15', 0, NULL,       3003, @tid, @cb, @now),
(1007, 'CHD',            'I25',   '心脏支架术后',           '2022-03-20', 0, NULL,       3002, @tid, @cb, @now),
(1007, 'HYPERTENSION',   'I10',   '合并高血压',             '2020-07-08', 0, NULL,       3002, @tid, @cb, @now),
(1008, 'HYPERLIPIDEMIA', 'E78',   '家族性高脂血症',         '2015-08-22', 0, NULL,       3001, @tid, @cb, @now),
(1009, 'NEPHROTIC',      'N04',   '24h尿蛋白>3.5g',         '2023-12-05', 0, NULL,       3001, @tid, @cb, @now),
(1010, 'DIABETES',       'E11',   '糖尿病10年病史',         '2014-02-10', 0, NULL,       3001, @tid, @cb, @now),
(1010, 'NEUROPATHY',     'G63.2', '糖尿病+神经传导检查',    '2023-06-18', 1, 'DIABETES', 3001, @tid, @cb, @now),
(1010, 'VASCULOPATHY',   'I79.2', '糖尿病+ABI<0.9',         '2023-06-18', 1, 'DIABETES', 3001, @tid, @cb, @now);

-- =============================================
-- 19. 患者标签
-- =============================================
INSERT INTO `ch_patient_tag` (`patient_id`, `tag_type`, `tag_value`, `tenant_id`, `create_by`, `create_time`) VALUES
(1001, 'RISK',        'HIGH',          @tid, @cb, @now),
(1002, 'RISK',        'VERY_HIGH',     @tid, @cb, @now),
(1002, 'COMORBIDITY', '糖尿病+眼底病变', @tid, @cb, @now),
(1003, 'RISK',        'VERY_HIGH',     @tid, @cb, @now),
(1003, 'COMORBIDITY', '高血压+糖尿病',   @tid, @cb, @now),
(1004, 'RISK',        'HIGH',          @tid, @cb, @now),
(1004, 'CUSTOM',      '独居老人',        @tid, @cb, @now),
(1005, 'RISK',        'MEDIUM',        @tid, @cb, @now),
(1006, 'RISK',        'HIGH',          @tid, @cb, @now),
(1006, 'CUSTOM',      '行动不便',        @tid, @cb, @now),
(1010, 'RISK',        'VERY_HIGH',     @tid, @cb, @now),
(1010, 'COMORBIDITY', '糖尿病+神经+血管', @tid, @cb, @now);

-- =============================================
-- 20. 患者时间线
-- =============================================
INSERT INTO `ch_patient_timeline` (`patient_id`, `event_type`, `event_title`, `event_detail`, `event_time`, `tenant_id`, `create_by`, `create_time`) VALUES
(1001, 'ARCHIVE',           '建档',       '首次建档，确诊高血压',         '2020-03-15 09:00:00', @tid, @cb, @now),
(1001, 'SIGN',              '签约',       '与省立医院家医团队签约',        '2020-03-20 10:30:00', @tid, @cb, @now),
(1001, 'PLAN_CHANGE',       '方案调整',   '根据随访结果调整用药',          '2024-08-10 14:00:00', @tid, @cb, @now),
(1002, 'ARCHIVE',           '建档',       '糖尿病患者入档',               '2018-06-20 09:00:00', @tid, @cb, @now),
(1002, 'WARNING',           '血糖预警',   '空腹血糖16.8mmol/L，启动处置', '2024-11-15 07:30:00', @tid, @cb, @now),
(1003, 'ARCHIVE',           'HIS同步入档', 'HIS门诊确诊后自动入档',        '2023-05-01 11:00:00', @tid, @cb, @now),
(1003, 'WARNING',           '血压预警',   '收缩压185mmHg危急值',          '2025-01-05 08:00:00', @tid, @cb, @now),
(1004, 'ARCHIVE',           '建档',       '冠心病患者入档',               '2019-11-08 10:00:00', @tid, @cb, @now),
(1006, 'REFERRAL',          '转诊',       '脑卒中急性期转上级医院',        '2022-01-15 14:30:00', @tid, @cb, @now),
(1010, 'MEDICATION_ADJUST', '用药调整',   '新增普瑞巴林治疗神经病变',      '2024-12-01 11:00:00', @tid, @cb, @now);

-- =============================================
-- 21. 患者账户（微信 + 手机号）
-- =============================================
INSERT INTO `ch_patient_account` (`account_id`, `patient_id`, `phone`, `openid`, `is_family_proxy`, `master_account_id`, `auth_scope`, `auth_expire_time`, `tenant_id`, `create_by`, `create_time`) VALUES
(21001, 1001, '13800000001', 'wx_openid_p1001', 0, NULL, NULL,                                   NULL, @tid, @cb, @now),
(21002, 1002, '13800000002', 'wx_openid_p1002', 0, NULL, NULL,                                   NULL, @tid, @cb, @now),
(21003, 1003, '13800000003', 'wx_openid_p1003', 0, NULL, NULL,                                   NULL, @tid, @cb, @now),
(21004, 1004, '13800000004', NULL,              0, NULL, NULL,                                   NULL, @tid, @cb, @now),
(21005, 1004, '13911111114', 'wx_openid_proxy4',1, 21004, '["HEALTH_DATA","FOLLOWUP","MESSAGE"]', '2026-12-31 23:59:59', @tid, @cb, @now),
(21006, 1006, '13800000006', 'wx_openid_p1006', 0, NULL, NULL,                                   NULL, @tid, @cb, @now),
(21007, 1006, '13911111116', 'wx_openid_proxy6',1, 21006, '["HEALTH_DATA","MEDICATION"]',         '2026-06-30 23:59:59', @tid, @cb, @now),
(21008, 1010, '13800000010', 'wx_openid_p1010', 0, NULL, NULL,                                   NULL, @tid, @cb, @now);

-- =============================================
-- 22. 签约关系
-- =============================================
INSERT INTO `ch_patient_contract` (`contract_id`, `patient_id`, `team_id`, `package_id`, `contract_type`, `contract_period_start`, `contract_period_end`, `renewal_status`, `expiry_remind_status`, `tenant_id`, `create_by`, `create_time`) VALUES
(22001, 1001, 4001, 5002, 'PERSONAL', '2024-03-20', '2025-03-19', 'ACTIVE',   0, @tid, @cb, @now),
(22002, 1002, 4002, 5002, 'PERSONAL', '2024-06-20', '2025-06-19', 'ACTIVE',   0, @tid, @cb, @now),
(22003, 1003, 4001, 5001, 'PERSONAL', '2024-05-01', '2025-04-30', 'EXPIRING', 1, @tid, @cb, @now),
(22004, 1004, 4001, 5003, 'PERSONAL', '2024-11-08', '2025-11-07', 'ACTIVE',   0, @tid, @cb, @now),
(22005, 1005, 4001, 5001, 'PERSONAL', '2024-04-12', '2025-04-11', 'EXPIRED',  1, @tid, @cb, @now),
(22006, 1006, 4003, 5002, 'PERSONAL', '2024-01-15', '2025-01-14', 'RENEWED',  0, @tid, @cb, @now),
(22007, 1007, 4001, 5002, 'PERSONAL', '2024-03-20', '2025-03-19', 'ACTIVE',   0, @tid, @cb, @now),
(22008, 1008, 4002, 5001, 'PERSONAL', '2024-08-22', '2025-08-21', 'ACTIVE',   0, @tid, @cb, @now),
(22010, 1010, 4002, 5003, 'PERSONAL', '2024-02-10', '2025-02-09', 'EXPIRING', 1, @tid, @cb, @now);

-- =============================================
-- 23. 履约记录（部分已履约、部分违约）
-- =============================================
INSERT INTO `ch_contract_fulfillment` (`contract_id`, `service_item`, `plan_date`, `actual_date`, `fulfillment_status`, `sla_violation`, `tenant_id`, `create_by`, `create_time`) VALUES
(22001, '月度随访', '2024-10-20', '2024-10-22', 'DONE',    0, @tid, @cb, @now),
(22001, '月度随访', '2024-11-20', '2024-11-28', 'DONE',    1, @tid, @cb, @now),
(22001, '月度随访', '2024-12-20', NULL,         'MISSED',  1, @tid, @cb, @now),
(22002, '月度随访', '2024-11-20', '2024-11-20', 'DONE',    0, @tid, @cb, @now),
(22002, '季度体检', '2024-12-20', NULL,         'PLANNED', 0, @tid, @cb, @now),
(22005, '季度随访', '2024-10-12', NULL,         'MISSED',  1, @tid, @cb, @now);

-- =============================================
-- 24. 知情同意电子签名
-- =============================================
INSERT INTO `ch_consent_record` (`consent_id`, `patient_id`, `consent_type`, `sign_image_file_id`, `sign_time`, `tenant_id`, `create_by`, `create_time`) VALUES
(24001, 1001, 'SIGN_CONTRACT', 100001, '2020-03-20 10:30:00', @tid, @cb, @now),
(24002, 1002, 'SIGN_CONTRACT', 100002, '2018-06-25 11:00:00', @tid, @cb, @now),
(24003, 1003, 'DATA_SHARE',    100003, '2023-05-01 11:30:00', @tid, @cb, @now),
(24004, 1006, 'REFERRAL',      100004, '2022-01-15 14:30:00', @tid, @cb, @now),
(24005, 1010, 'SIGN_CONTRACT', 100005, '2024-02-10 10:00:00', @tid, @cb, @now);

-- =============================================
-- 25. 用药记录
-- =============================================
INSERT INTO `ch_medication_record` (`med_id`, `patient_id`, `drug_name`, `drug_code`, `dosage`, `frequency`, `route`, `start_date`, `stop_date`, `dispense_quantity`, `prescription_period`, `prescriber_user_id`, `prescriber_verified`, `status`, `tenant_id`, `create_by`, `create_time`) VALUES
(25001, 1001, '氨氯地平',   'AMLODIPINE', '5mg',   'QD',  'ORAL', '2020-03-15', NULL, '28片', '28天', 2001, 1, 'ACTIVE',  @tid, @cb, @now),
(25002, 1001, '缬沙坦',     'ARB',        '80mg',  'QD',  'ORAL', '2024-08-10', NULL, '28片', '28天', 2001, 1, 'ACTIVE',  @tid, @cb, @now),
(25003, 1002, '二甲双胍',   'METFORMIN',  '500mg', 'TID', 'ORAL', '2018-06-20', NULL, '90片', '30天', 2002, 1, 'ACTIVE',  @tid, @cb, @now),
(25004, 1002, '格列美脲',   'GLIMEPIRIDE','2mg',   'QD',  'ORAL', '2022-01-10', NULL, '30片', '30天', 2002, 1, 'ACTIVE',  @tid, @cb, @now),
(25005, 1003, '氨氯地平',   'AMLODIPINE', '5mg',   'QD',  'ORAL', '2023-05-10', NULL, '28片', '28天', 2004, 1, 'ACTIVE',  @tid, @cb, @now),
(25006, 1003, '二甲双胍',   'METFORMIN',  '500mg', 'BID', 'ORAL', '2023-05-10', NULL, '60片', '30天', 2004, 1, 'ACTIVE',  @tid, @cb, @now),
(25007, 1004, '阿司匹林',   'ASPIRIN',    '100mg', 'QD',  'ORAL', '2019-11-10', NULL, '30片', '30天', 2001, 1, 'ACTIVE',  @tid, @cb, @now),
(25008, 1004, '阿托伐他汀', 'STATIN',     '20mg',  'QN',  'ORAL', '2019-11-10', NULL, '28片', '28天', 2001, 1, 'ACTIVE',  @tid, @cb, @now),
(25009, 1005, '阿托伐他汀', 'STATIN',     '10mg',  'QN',  'ORAL', '2021-04-15', NULL, '28片', '28天', 2005, 1, 'ACTIVE',  @tid, @cb, @now),
(25010, 1007, '阿司匹林',   'ASPIRIN',    '100mg', 'QD',  'ORAL', '2022-03-22', NULL, '30片', '30天', 2004, 1, 'ACTIVE',  @tid, @cb, @now),
(25011, 1007, '硫酸氢氯吡格雷','CLOPIDOGREL','75mg','QD','ORAL','2022-03-22',  '2023-03-22','30片','30天', 2004, 1, 'STOPPED', @tid, @cb, @now),
(25012, 1010, '胰岛素',     'INSULIN',    '12U',   'TID', 'SC',   '2020-05-10', NULL, '1支',  '15天', 2002, 1, 'ACTIVE',  @tid, @cb, @now),
(25013, 1010, '普瑞巴林',   'PREGABALIN', '75mg',  'BID', 'ORAL', '2024-12-01', NULL, '56片', '28天', 2002, 1, 'ACTIVE',  @tid, @cb, @now);

-- =============================================
-- 26. 用药调整
-- =============================================
INSERT INTO `ch_medication_adjust` (`med_id`, `patient_id`, `adjust_type`, `adjust_reason`, `adverse_reaction`, `preview_confirmed`, `pin_verified_at`, `adjuster_user_id`, `tenant_id`, `create_by`, `create_time`) VALUES
(25001, 1001, 'DOSE_CHANGE', '单药控制不佳，联用ARB', NULL, 1, '2024-08-10 14:00:00', 2001, @tid, @cb, '2024-08-10 14:00:00'),
(25011, 1007, 'SWITCH',      '支架1年后停用',         NULL, 1, '2023-03-22 10:00:00', 2004, @tid, @cb, '2023-03-22 10:00:00'),
(25013, 1010, 'ADD',         '新发神经病变',           NULL, 1, '2024-12-01 11:00:00', 2002, @tid, @cb, '2024-12-01 11:00:00'),
(25003, 1002, 'DOSE_CHANGE', '血糖控制差，加量',       '偶有胃部不适', 1, '2024-11-20 09:30:00', 2002, @tid, @cb, '2024-11-20 09:30:00');

-- =============================================
-- 27. 风险评估
-- =============================================
INSERT INTO `ch_risk_assessment` (`assessment_id`, `patient_id`, `disease_code`, `risk_level`, `assessment_report`, `assessor_user_id`, `org_id`, `tenant_id`, `create_by`, `create_time`) VALUES
(27001, 1001, 'HYPERTENSION', 'HIGH',      '血压长期偏高，吸烟+年龄风险',         2001, 3001, @tid, @cb, '2024-09-15 10:00:00'),
(27002, 1002, 'DIABETES',     'VERY_HIGH', '血糖+HbA1c双高，已出现眼底病变',      2002, 3001, @tid, @cb, '2024-10-20 10:30:00'),
(27003, 1003, 'HYPERTENSION', 'VERY_HIGH', '血压危急值+合并糖尿病',               2004, 3002, @tid, @cb, '2025-01-06 09:00:00'),
(27004, 1003, 'DIABETES',     'HIGH',      '血糖控制不理想',                      2004, 3002, @tid, @cb, '2025-01-06 09:15:00'),
(27005, 1004, 'CHD',          'HIGH',      '冠心病+独居老人，需要强化管理',       2001, 3001, @tid, @cb, '2024-11-10 10:00:00'),
(27006, 1005, 'HYPERLIPIDEMIA','MEDIUM',   'TC偏高，依从性尚可',                   2005, 3001, @tid, @cb, '2024-08-20 14:00:00'),
(27007, 1010, 'DIABETES',     'VERY_HIGH', '糖尿病10年+神经+血管并发症',          2002, 3001, @tid, @cb, '2024-12-01 09:00:00');

-- =============================================
-- 28. 风险因子项
-- =============================================
INSERT INTO `ch_risk_factor_item` (`assessment_id`, `factor_name`, `factor_value`, `factor_weight`, `tenant_id`, `create_by`, `create_time`) VALUES
(27001, 'SBP',     '148',  0.40, @tid, @cb, @now),
(27001, 'DBP',     '95',   0.30, @tid, @cb, @now),
(27001, 'SMOKING', 'YES',  0.15, @tid, @cb, @now),
(27001, 'AGE',     '59',   0.15, @tid, @cb, @now),
(27002, 'FBG',     '10.2', 0.40, @tid, @cb, @now),
(27002, 'HBA1C',   '8.6',  0.35, @tid, @cb, @now),
(27002, 'BMI',     '28.5', 0.25, @tid, @cb, @now),
(27003, 'SBP',     '185',  0.40, @tid, @cb, @now),
(27003, 'DBP',     '110',  0.30, @tid, @cb, @now),
(27007, 'FBG',     '12.5', 0.40, @tid, @cb, @now),
(27007, 'HBA1C',   '9.8',  0.35, @tid, @cb, @now),
(27007, 'BMI',     '29.1', 0.25, @tid, @cb, @now);

-- =============================================
-- 29. 管理等级变更记录
-- =============================================
INSERT INTO `ch_manage_level_record` (`patient_id`, `disease_code`, `old_level`, `new_level`, `change_reason`, `change_time`, `tenant_id`, `create_by`, `create_time`) VALUES
(1001, 'HYPERTENSION', 'MEDIUM',    'HIGH',      '收缩压连续3次>140，吸烟',    '2024-09-15 10:00:00', @tid, @cb, @now),
(1002, 'DIABETES',     'HIGH',      'VERY_HIGH', '出现眼底病变并发症',         '2022-09-10 15:00:00', @tid, @cb, @now),
(1003, 'HYPERTENSION', 'HIGH',      'VERY_HIGH', '血压危急值+糖尿病',          '2025-01-06 09:00:00', @tid, @cb, @now),
(1010, 'DIABETES',     'HIGH',      'VERY_HIGH', '出现神经+血管并发症',        '2023-06-18 10:00:00', @tid, @cb, @now);

-- =============================================
-- 30. 管理方案 + 方案项
-- =============================================
INSERT INTO `ch_manage_plan` (`plan_id`, `patient_id`, `disease_code`, `plan_status`, `org_id`, `tenant_id`, `create_by`, `create_time`) VALUES
(30001, 1001, 'HYPERTENSION', 'ACTIVE',  3001, @tid, @cb, @now),
(30002, 1002, 'DIABETES',     'ACTIVE',  3001, @tid, @cb, @now),
(30003, 1003, 'HYPERTENSION', 'ACTIVE',  3002, @tid, @cb, @now),
(30004, 1003, 'DIABETES',     'ACTIVE',  3002, @tid, @cb, @now),
(30005, 1004, 'CHD',          'ACTIVE',  3001, @tid, @cb, @now),
(30006, 1010, 'DIABETES',     'ACTIVE',  3001, @tid, @cb, @now),
(30007, 1001, 'HYPERTENSION', 'HISTORY', 3001, @tid, @cb, @now);

INSERT INTO `ch_manage_plan_item` (`plan_id`, `item_type`, `item_content`, `org_id`, `tenant_id`, `create_by`, `create_time`) VALUES
(30001, 'MEDICATION', '{"drugs":["氨氯地平5mg QD","缬沙坦80mg QD"]}',     3001, @tid, @cb, @now),
(30001, 'DIET',       '{"principles":["低盐","每日盐<6g","低脂"]}',       3001, @tid, @cb, @now),
(30001, 'EXERCISE',   '{"type":"快走","freq":"5次/周","duration":"30分钟"}',3001, @tid, @cb, @now),
(30001, 'FOLLOWUP',   '{"cycle":"月度","method":"PHONE"}',                  3001, @tid, @cb, @now),
(30001, 'MONITOR',    '{"metrics":["SBP","DBP"],"freq":"每日1次"}',         3001, @tid, @cb, @now),
(30002, 'MEDICATION', '{"drugs":["二甲双胍500mg TID","格列美脲2mg QD"]}',  3001, @tid, @cb, @now),
(30002, 'DIET',       '{"principles":["糖尿病饮食","控碳水"]}',             3001, @tid, @cb, @now),
(30002, 'FOLLOWUP',   '{"cycle":"月度","method":"PHONE"}',                  3001, @tid, @cb, @now),
(30006, 'MEDICATION', '{"drugs":["胰岛素12U TID","普瑞巴林75mg BID"]}',    3001, @tid, @cb, @now),
(30006, 'PSYCHOLOGY', '{"advice":"疼痛心理支持"}',                          3001, @tid, @cb, @now);

-- =============================================
-- 31. 随访计划 + 任务 + 记录
-- =============================================
INSERT INTO `ch_followup_plan` (`plan_id`, `patient_id`, `disease_code`, `cycle_days`, `total_rounds`, `current_round`, `status`, `tenant_id`, `create_by`, `create_time`) VALUES
(31001, 1001, 'HYPERTENSION', 30, 12, 9, 'ACTIVE', @tid, @cb, @now),
(31002, 1002, 'DIABETES',     30, 12, 8, 'ACTIVE', @tid, @cb, @now),
(31003, 1003, 'HYPERTENSION', 30, 12, 2, 'ACTIVE', @tid, @cb, @now),
(31004, 1004, 'CHD',          30, 12, 7, 'ACTIVE', @tid, @cb, @now),
(31005, 1005, 'HYPERLIPIDEMIA',90, 4, 2, 'ACTIVE', @tid, @cb, @now),
(31006, 1010, 'DIABETES',     15, 24, 5, 'ACTIVE', @tid, @cb, @now);

INSERT INTO `ch_followup_plan_item` (`plan_id`, `item_type`, `visit_type`, `due_date`, `item_config`, `tenant_id`, `create_by`, `create_time`) VALUES
(31001, 'ROUTINE', 'PHONE', '2025-02-20', '{"questionnaireId":9001}', @tid, @cb, @now),
(31002, 'ROUTINE', 'PHONE', '2025-02-15', '{"questionnaireId":9002}', @tid, @cb, @now),
(31003, 'URGENT',  'OFFLINE','2025-01-20','{"questionnaireId":9001,"reason":"危急值后复访"}', @tid, @cb, @now);

INSERT INTO `ch_followup_task` (`task_id`, `patient_id`, `plan_id`, `task_round`, `plan_due_date`, `task_status`, `assignee_user_id`, `visit_type`, `tenant_id`, `create_by`, `create_time`) VALUES
(31101, 1001, 31001, 9,  '2025-01-20', 'DONE',      2001, 'PHONE',  @tid, @cb, @now),
(31102, 1001, 31001, 10, '2025-02-20', 'PENDING',   2001, 'PHONE',  @tid, @cb, @now),
(31103, 1002, 31002, 8,  '2025-01-15', 'DONE',      2002, 'PHONE',  @tid, @cb, @now),
(31104, 1002, 31002, 9,  '2025-02-15', 'REMINDING', 2002, 'PHONE',  @tid, @cb, @now),
(31105, 1003, 31003, 2,  '2025-01-20', 'PENDING',   2004, 'OFFLINE',@tid, @cb, @now),
(31106, 1004, 31004, 7,  '2024-12-20', 'OVERDUE',   2001, 'PHONE',  @tid, @cb, @now),
(31107, 1004, 31004, 8,  '2025-01-20', 'PENDING',   2001, 'PHONE',  @tid, @cb, @now),
(31108, 1005, 31005, 2,  '2024-10-12', 'OVERDUE',   2005, 'SELF_FILL', @tid, @cb, @now),
(31109, 1010, 31006, 5,  '2025-01-25', 'PENDING',   2002, 'VIDEO',  @tid, @cb, @now);

INSERT INTO `ch_followup_record` (`record_id`, `task_id`, `patient_id`, `visit_type`, `visit_content`, `visitor_user_id`, `visit_date`, `tenant_id`, `create_by`, `create_time`) VALUES
(31201, 31101, 1001, 'PHONE', '血压控制稳定SBP=138，用药依从良好', 2001, '2025-01-21 10:00:00', @tid, @cb, '2025-01-21 10:00:00'),
(31202, 31103, 1002, 'PHONE', '空腹血糖7.8，医嘱加量二甲双胍',      2002, '2025-01-16 09:30:00', @tid, @cb, '2025-01-16 09:30:00');

-- =============================================
-- 32. 随访答卷
-- =============================================
INSERT INTO `ch_followup_answer` (`record_id`, `questionnaire_id`, `question_id`, `answer_value`, `tenant_id`, `create_by`, `create_time`) VALUES
(31201, 9001, 'q1', '138', @tid, @cb, @now),
(31201, 9001, 'q2', '86',  @tid, @cb, @now),
(31201, 9001, 'q3', '无症状', @tid, @cb, @now),
(31201, 9001, 'q4', '规律', @tid, @cb, @now),
(31202, 9002, 'q1', '7.8',  @tid, @cb, @now),
(31202, 9002, 'q2', '7.9',  @tid, @cb, @now),
(31202, 9002, 'q3', '无',   @tid, @cb, @now);

-- =============================================
-- 33. 健康指标记录（血压/血糖/心率 等 - 含异常值）
-- =============================================
-- 患者 1001 高血压 近 10 条血压
INSERT INTO `ch_health_metric_record` (`patient_id`, `metric_type`, `metric_value`, `unit`, `measure_scene`, `measure_period`, `is_abnormal`, `data_source`, `reference_value_min`, `reference_value_max`, `tenant_id`, `create_by`, `create_time`) VALUES
(1001, 'SBP', 138, 'mmHg', 'HOME', 'MORNING', 0, 'MANUAL',  90, 140, @tid, @cb, DATE_SUB(@now, INTERVAL 10 DAY)),
(1001, 'SBP', 142, 'mmHg', 'HOME', 'MORNING', 1, 'MANUAL',  90, 140, @tid, @cb, DATE_SUB(@now, INTERVAL 9 DAY)),
(1001, 'SBP', 145, 'mmHg', 'HOME', 'MORNING', 1, 'MANUAL',  90, 140, @tid, @cb, DATE_SUB(@now, INTERVAL 8 DAY)),
(1001, 'SBP', 148, 'mmHg', 'HOME', 'MORNING', 1, 'DEVICE',  90, 140, @tid, @cb, DATE_SUB(@now, INTERVAL 7 DAY)),
(1001, 'SBP', 135, 'mmHg', 'HOME', 'MORNING', 0, 'DEVICE',  90, 140, @tid, @cb, DATE_SUB(@now, INTERVAL 5 DAY)),
(1001, 'DBP',  86, 'mmHg', 'HOME', 'MORNING', 0, 'DEVICE',  60, 90,  @tid, @cb, DATE_SUB(@now, INTERVAL 5 DAY)),
(1001, 'HR',   76, 'bpm',  'HOME', 'MORNING', 0, 'DEVICE',  60, 100, @tid, @cb, DATE_SUB(@now, INTERVAL 5 DAY)),
-- 患者 1002 糖尿病空腹血糖
(1002, 'FBG', 7.8, 'mmol/L','HOME','FASTING', 1, 'MANUAL',  3.9, 6.1, @tid, @cb, DATE_SUB(@now, INTERVAL 10 DAY)),
(1002, 'FBG', 8.2, 'mmol/L','HOME','FASTING', 1, 'MANUAL',  3.9, 6.1, @tid, @cb, DATE_SUB(@now, INTERVAL 8 DAY)),
(1002, 'FBG', 9.5, 'mmol/L','HOME','FASTING', 1, 'DEVICE',  3.9, 6.1, @tid, @cb, DATE_SUB(@now, INTERVAL 6 DAY)),
(1002, 'FBG',16.8, 'mmol/L','HOME','FASTING', 1, 'DEVICE',  3.9, 6.1, @tid, @cb, DATE_SUB(@now, INTERVAL 4 DAY)),
(1002, 'HBA1C', 8.6,'%',    'HOSPITAL','RANDOM',1,'HIS_LIS',4.0,6.0,  @tid, @cb, DATE_SUB(@now, INTERVAL 30 DAY)),
-- 患者 1003 双高危急值
(1003, 'SBP',  185, 'mmHg', 'HOSPITAL','RANDOM',1,'MANUAL',  90, 140, @tid, @cb, DATE_SUB(@now, INTERVAL 2 DAY)),
(1003, 'DBP',  110, 'mmHg', 'HOSPITAL','RANDOM',1,'MANUAL',  60, 90,  @tid, @cb, DATE_SUB(@now, INTERVAL 2 DAY)),
(1003, 'FBG', 11.3, 'mmol/L','HOSPITAL','FASTING',1,'HIS_LIS',3.9,6.1,@tid, @cb, DATE_SUB(@now, INTERVAL 2 DAY)),
-- 患者 1004 心率
(1004, 'HR',   118, 'bpm',  'HOME', 'EVENING', 1, 'DEVICE',  60, 100, @tid, @cb, DATE_SUB(@now, INTERVAL 3 DAY)),
(1004, 'HR',    82, 'bpm',  'HOME', 'MORNING', 0, 'DEVICE',  60, 100, @tid, @cb, DATE_SUB(@now, INTERVAL 1 DAY)),
-- 患者 1010 糖尿病复杂
(1010, 'FBG', 12.5, 'mmol/L','HOME','FASTING', 1, 'DEVICE',  3.9, 6.1, @tid, @cb, DATE_SUB(@now, INTERVAL 5 DAY)),
(1010, 'HBA1C', 9.8,'%',    'HOSPITAL','RANDOM',1,'HIS_LIS',4.0,6.0,  @tid, @cb, DATE_SUB(@now, INTERVAL 15 DAY));

-- =============================================
-- 34. 设备绑定 + 原始数据
-- =============================================
INSERT INTO `ch_device_bind` (`bind_id`, `patient_id`, `device_id`, `device_type`, `battery_level`, `online_status`, `last_comm_time`, `tenant_id`, `create_by`, `create_time`) VALUES
(34001, 1001, 'BP_DEV_001', 'BP_MONITOR',    85, 1, @now, @tid, @cb, @now),
(34002, 1002, 'GLU_DEV_001','GLUCOMETER',    60, 1, @now, @tid, @cb, @now),
(34003, 1004, 'ECG_DEV_001','ECG_WEARABLE',  45, 1, @now, @tid, @cb, @now),
(34004, 1010, 'GLU_DEV_002','CGM',           72, 1, @now, @tid, @cb, @now);

INSERT INTO `ch_device_raw_record` (`device_id`, `patient_id`, `raw_data`, `parsed_at`, `tenant_id`, `create_by`, `create_time`) VALUES
('BP_DEV_001', 1001, '{"sbp":135,"dbp":86,"hr":76,"ts":"2025-04-20T06:30:00"}', @now, @tid, @cb, @now),
('GLU_DEV_001',1002, '{"value":16.8,"period":"FASTING","ts":"2025-04-17T07:00:00"}',@now, @tid, @cb, @now),
('ECG_DEV_001',1004, '{"hr":118,"arrhythmia":false,"ts":"2025-04-18T20:00:00"}',@now, @tid, @cb, @now);

-- =============================================
-- 35. 生活方式
-- =============================================
INSERT INTO `ch_lifestyle_record` (`patient_id`, `smoking_status`, `drinking_status`, `exercise_freq`, `diet_habit`, `psychological_status`, `compliance_level`, `tenant_id`, `create_by`, `create_time`) VALUES
(1001, 'SMOKING',        'DRINKING_LIGHT', '3_TIMES_WEEK', '口味偏咸',     'NORMAL',  'MODERATE', @tid, @cb, DATE_SUB(@now, INTERVAL 30 DAY)),
(1001, 'SMOKING_REDUCED','DRINKING_LIGHT', '5_TIMES_WEEK', '逐渐减盐',     'NORMAL',  'GOOD',     @tid, @cb, @now),
(1002, 'NEVER',          'NEVER',          '5_TIMES_WEEK', '控糖严格',     'ANXIOUS', 'GOOD',     @tid, @cb, @now),
(1003, 'SMOKING',        'DRINKING_HEAVY', 'RARELY',       '饮食不规律',   'NORMAL',  'POOR',     @tid, @cb, @now),
(1004, 'QUIT_5_YEARS',   'NEVER',          '3_TIMES_WEEK', '少盐少油',     'DEPRESSED','GOOD',    @tid, @cb, @now),
(1010, 'NEVER',          'NEVER',          'RARELY',       '糖尿病饮食',   'NORMAL',  'MODERATE', @tid, @cb, @now);

-- =============================================
-- 36. 体检记录 + 体检项目（含并发症专项）
-- =============================================
INSERT INTO `ch_health_exam` (`exam_id`, `patient_id`, `external_sn`, `exam_type`, `exam_date`, `exam_org_id`, `special_category`, `tenant_id`, `create_by`, `create_time`) VALUES
(36001, 1001, 'LIS-2024-0001', 'ANNUAL_CHECKUP', '2024-09-15', 3001, NULL,            @tid, @cb, @now),
(36002, 1002, 'LIS-2024-0002', 'REGULAR_TEST',   '2024-10-20', 3001, NULL,            @tid, @cb, @now),
(36003, 1002, 'PACS-2022-0001','SPECIAL_TEST',   '2022-09-10', 3001, 'FUNDUS_PHOTO',  @tid, @cb, @now),
(36004, 1003, 'LIS-2025-0001', 'ANNUAL_CHECKUP', '2025-01-06', 3002, NULL,            @tid, @cb, @now),
(36005, 1004, 'PACS-2024-0001','SPECIAL_TEST',   '2024-11-10', 3001, 'ECG',           @tid, @cb, @now),
(36006, 1006, 'PACS-2022-0002','SPECIAL_TEST',   '2022-01-15', 3003, 'CT',            @tid, @cb, @now),
(36007, 1010, 'PACS-2023-0001','SPECIAL_TEST',   '2023-06-18', 3001, 'NERVE_CONDUCTION',@tid, @cb, @now),
(36008, 1010, 'PACS-2023-0002','SPECIAL_TEST',   '2023-06-18', 3001, 'ABI',           @tid, @cb, @now);

INSERT INTO `ch_health_exam_item` (`exam_id`, `item_name`, `item_code`, `result_value`, `reference_range`, `is_abnormal`, `dr_grade`, `tcss_score`, `mrs_score`, `nihss_score`, `egfr_value`, `tenant_id`, `create_by`, `create_time`) VALUES
(36001, '收缩压',         'SBP',    '148',    '90-140',     1, NULL, NULL, NULL, NULL, NULL, @tid, @cb, @now),
(36001, '舒张压',         'DBP',    '95',     '60-90',      1, NULL, NULL, NULL, NULL, NULL, @tid, @cb, @now),
(36001, '总胆固醇',       'TC',     '5.8',    '3.1-5.2',    1, NULL, NULL, NULL, NULL, NULL, @tid, @cb, @now),
(36002, '空腹血糖',       'FBG',    '10.2',   '3.9-6.1',    1, NULL, NULL, NULL, NULL, NULL, @tid, @cb, @now),
(36002, '糖化血红蛋白',   'HBA1C',  '8.6',    '4.0-6.0',    1, NULL, NULL, NULL, NULL, NULL, @tid, @cb, @now),
(36003, 'DR分级',         'DR',     'III级',  NULL,         1,   3,  NULL, NULL, NULL, NULL, @tid, @cb, @now),
(36004, '收缩压',         'SBP',    '185',    '90-140',     1, NULL, NULL, NULL, NULL, NULL, @tid, @cb, @now),
(36004, '空腹血糖',       'FBG',    '11.3',   '3.9-6.1',    1, NULL, NULL, NULL, NULL, NULL, @tid, @cb, @now),
(36005, 'ECG结论',        'ECG',    '窦性心动过速', NULL,   1, NULL, NULL, NULL, NULL, NULL, @tid, @cb, @now),
(36006, 'NIHSS评分',      'NIHSS',  '6分',    NULL,         1, NULL, NULL, NULL,    6, NULL, @tid, @cb, @now),
(36006, 'MRS评分',        'MRS',    '2分',    NULL,         0, NULL, NULL,    2, NULL, NULL, @tid, @cb, @now),
(36007, 'TCSS评分',       'TCSS',   '9分',    NULL,         1, NULL,    9, NULL, NULL, NULL, @tid, @cb, @now),
(36008, 'ABI值',          'ABI',    '0.78',   '>0.9',       1, NULL, NULL, NULL, NULL, NULL, @tid, @cb, @now),
(36008, 'eGFR',           'EGFR',   '62',     '>90',        1, NULL, NULL, NULL, NULL, 62.0, @tid, @cb, @now);

-- =============================================
-- 37. 预警事件 + 处置
-- =============================================
INSERT INTO `ch_warning_event` (`warning_id`, `patient_id`, `rule_id`, `warning_level`, `warning_value`, `warning_time`, `event_status`, `assignee_user_id`, `tenant_id`, `create_by`, `create_time`) VALUES
(37001, 1003, 8002, 'CRITICAL', 185.0, DATE_SUB(@now, INTERVAL 2 DAY), 'RESOLVED',   2004, @tid, @cb, DATE_SUB(@now, INTERVAL 2 DAY)),
(37002, 1002, 8012, 'CRITICAL', 16.8,  DATE_SUB(@now, INTERVAL 4 DAY), 'PROCESSING', 2002, @tid, @cb, DATE_SUB(@now, INTERVAL 4 DAY)),
(37003, 1001, 8001, 'HIGH',     148.0, DATE_SUB(@now, INTERVAL 7 DAY), 'RESOLVED',   2001, @tid, @cb, DATE_SUB(@now, INTERVAL 7 DAY)),
(37004, 1004, 8021, 'HIGH',     118.0, DATE_SUB(@now, INTERVAL 3 DAY), 'CONFIRMED',  2001, @tid, @cb, DATE_SUB(@now, INTERVAL 3 DAY)),
(37005, 1010, 8012, 'CRITICAL', 12.5,  DATE_SUB(@now, INTERVAL 5 DAY), 'ESCALATED',  2002, @tid, @cb, DATE_SUB(@now, INTERVAL 5 DAY)),
(37006, 1003, 8011, 'HIGH',     11.3,  DATE_SUB(@now, INTERVAL 2 DAY), 'NEW',        2004, @tid, @cb, DATE_SUB(@now, INTERVAL 2 DAY));

INSERT INTO `ch_warning_action` (`warning_id`, `action_type`, `action_detail`, `action_user_id`, `action_time`, `tenant_id`, `create_by`, `create_time`) VALUES
(37001, 'CONFIRM', '医生确认危急值',                 2004, DATE_SUB(@now, INTERVAL 2 DAY), @tid, @cb, @now),
(37001, 'HANDLE',  '电话联系患者，紧急到院',         2004, DATE_SUB(@now, INTERVAL 2 DAY), @tid, @cb, @now),
(37001, 'RESOLVE', '患者到院调整用药，血压降至150',  2004, DATE_SUB(@now, INTERVAL 1 DAY), @tid, @cb, @now),
(37002, 'CONFIRM', '确认糖尿病急性高血糖',           2002, DATE_SUB(@now, INTERVAL 4 DAY), @tid, @cb, @now),
(37002, 'HANDLE',  '安排住院治疗',                    2002, DATE_SUB(@now, INTERVAL 3 DAY), @tid, @cb, @now),
(37005, 'ESCALATE','升级至上级医院',                  2002, DATE_SUB(@now, INTERVAL 4 DAY), @tid, @cb, @now);

-- =============================================
-- 38. 筛查批次 + 记录
-- =============================================
INSERT INTO `ch_screening_batch` (`batch_id`, `batch_name`, `activity_date`, `org_id`, `doctor_user_id`, `location`, `notes`, `tenant_id`, `create_by`, `create_time`) VALUES
(38001, '2024春季大明湖社区义诊', '2024-04-15', 3004, 2003, '大明湖公园入口广场', '春季爱心义诊筛查',  @tid, @cb, @now),
(38002, '2025冬季槐荫区慢病筛查', '2025-01-10', 3003, 2007, '槐荫区党群服务中心', '冬季心脑血管筛查',  @tid, @cb, @now);

INSERT INTO `ch_screening_record` (`batch_id`, `offline_uuid`, `patient_name`, `id_card`, `phone`, `gender`, `age`, `symptoms`, `vitals`, `risk_level`, `enroll_status`, `enrolled_patient_id`, `tenant_id`, `create_by`, `create_time`) VALUES
(38001, 'uuid-screening-001', '刘老太',  '370102195011110001', '13888880001', 'F', 74, '["头晕","心慌"]',         '{"sbp":165,"dbp":95,"fbg":7.2}', 'HIGH',      'ENROLLED', 1006, @tid, @cb, @now),
(38001, 'uuid-screening-002', '高志明',  '370102196305150002', '13888880002', 'M', 61, '["无症状"]',              '{"sbp":138,"dbp":86,"fbg":5.8}', 'LOW',       'PENDING',  NULL, @tid, @cb, @now),
(38001, 'uuid-screening-003', '吴桂珍',  '370102194810055678', '13800000010', 'F', 76, '["多饮多尿","麻木"]',      '{"sbp":155,"dbp":92,"fbg":12.5}','VERY_HIGH', 'ENROLLED', 1010, @tid, @cb, @now),
(38002, 'uuid-screening-010', '张建华',  '370104195507200003', '13888880003', 'M', 69, '["胸闷"]',                '{"sbp":150,"dbp":88,"fbg":6.5}', 'MEDIUM',    'PENDING',  NULL, @tid, @cb, @now),
(38002, 'uuid-screening-011', '赵婉华',  '370104194903130004', '13888880004', 'F', 75, '["肢体乏力"]',            '{"sbp":170,"dbp":100}',          'HIGH',      'PENDING',  NULL, @tid, @cb, @now);

-- =============================================
-- 39. 转诊 + 档案共享 + 同步日志
-- =============================================
INSERT INTO `ch_referral_record` (`referral_id`, `patient_id`, `from_org_id`, `to_org_id`, `to_area_code`, `referral_reason`, `referral_category`, `referral_status`, `referral_type`, `tenant_id`, `create_by`, `create_time`) VALUES
(39001, 1006, 3003, 3001, '370102', '脑卒中急性期，需要上级医院救治',     'EMERGENCY', 'COMPLETED', 'UPWARD',   @tid, @cb, '2022-01-15 14:30:00'),
(39002, 1010, 3001, 3004, '370201', '病情稳定，转下级乡镇卫生院长期管理', 'STABLE',    'APPROVED',  'TOWNSHIP', @tid, @cb, '2025-01-10 10:00:00'),
(39003, 1005, 3001, 3002, '370103', '患者就诊便利考虑',                   'ROUTINE',   'PENDING',   'DOWNWARD', @tid, @cb, '2025-04-15 09:00:00');

INSERT INTO `ch_archive_share_apply` (`patient_id`, `apply_org_id`, `target_org_id`, `apply_reason`, `approval_status`, `tenant_id`, `create_by`, `create_time`) VALUES
(1006, 3001, 3003, '接手后续随访，需完整既往档案',     'APPROVED', @tid, @cb, '2022-02-01 10:00:00'),
(1003, 3002, 3001, '跨院转诊需要调档',                 'PENDING',  @tid, @cb, '2025-04-10 11:00:00');

INSERT INTO `ch_external_sync_log` (`sync_type`, `sync_direction`, `external_system`, `sync_status`, `sync_detail`, `sync_time`, `tenant_id`, `create_by`, `create_time`) VALUES
('PATIENT',   'INBOUND', 'HIS',      'SUCCESS', '患者1003 HIS确诊同步',                '2023-05-01 11:00:00', @tid, @cb, @now),
('LAB_EXAM',  'INBOUND', 'LIS',      'SUCCESS', '患者1001体检LIS同步',                 '2024-09-15 11:00:00', @tid, @cb, @now),
('IMAGE_EXAM','INBOUND', 'PACS',     'SUCCESS', '患者1002眼底照同步',                  '2022-09-10 15:00:00', @tid, @cb, @now),
('REFERRAL',  'OUTBOUND','PHS',      'SUCCESS', '患者1010下转乡镇卫生院',              '2025-01-10 10:30:00', @tid, @cb, @now),
('LAB_EXAM',  'INBOUND', 'LIS',      'FAILED',  '连接超时，LIS网络中断',               '2025-04-19 08:30:00', @tid, @cb, @now);

-- =============================================
-- 40. 医患消息会话 + 消息内容
-- =============================================
INSERT INTO `ch_message_session` (`session_id`, `patient_id`, `doctor_user_id`, `session_type`, `last_message_time`, `tenant_id`, `create_by`, `create_time`) VALUES
(40001, 1001, 2001, 'DOCTOR_PATIENT', @now,                              @tid, @cb, @now),
(40002, 1002, 2002, 'DOCTOR_PATIENT', DATE_SUB(@now, INTERVAL 1 DAY),   @tid, @cb, @now),
(40003, 1003, 2004, 'DOCTOR_PATIENT', DATE_SUB(@now, INTERVAL 2 DAY),   @tid, @cb, @now),
(40004, 1010, 2002, 'DOCTOR_PATIENT', DATE_SUB(@now, INTERVAL 3 DAY),   @tid, @cb, @now);

INSERT INTO `ch_message_content` (`session_id`, `sender_type`, `content_type`, `content`, `file_id`, `voice_duration`, `tenant_id`, `create_by`, `create_time`) VALUES
(40001, 'PATIENT','TEXT','医生，今天早上血压145/92，比较高',                NULL, NULL, @tid, @cb, DATE_SUB(@now, INTERVAL 1 HOUR)),
(40001, 'DOCTOR', 'TEXT','请先按方案服药，休息30分钟后重测，若仍高请告知',NULL, NULL, @tid, @cb, DATE_SUB(@now, INTERVAL 30 MINUTE)),
(40001, 'PATIENT','TEXT','好的，谢谢医生',                                   NULL, NULL, @tid, @cb, @now),
(40002, 'PATIENT','VOICE','语音消息',                                         100001, 18,  @tid, @cb, DATE_SUB(@now, INTERVAL 1 DAY)),
(40002, 'DOCTOR', 'TEXT','收到，请按时测血糖',                               NULL, NULL, @tid, @cb, DATE_SUB(@now, INTERVAL 23 HOUR)),
(40003, 'DOCTOR', 'TEXT','王先生，您上次血压过高，务必来院复查',            NULL, NULL, @tid, @cb, DATE_SUB(@now, INTERVAL 2 DAY)),
(40004, 'PATIENT','IMAGE','血糖仪照片',                                       100002, NULL,@tid, @cb, DATE_SUB(@now, INTERVAL 3 DAY));

-- =============================================
-- 41. 健康宣教推送记录
-- =============================================
INSERT INTO `ch_health_education_delivery` (`content_id`, `patient_id`, `trigger_type`, `push_channel`, `delivery_status`, `read_status`, `read_time`, `stay_duration`, `tenant_id`, `create_by`, `create_time`) VALUES
(10001, 1001, 'RULE_ENGINE', 'WECHAT', 'SENT',    1, DATE_SUB(@now, INTERVAL 5 DAY), 120, @tid, @cb, DATE_SUB(@now, INTERVAL 5 DAY)),
(10001, 1003, 'RULE_ENGINE', 'WECHAT', 'SENT',    0, NULL,                            NULL, @tid, @cb, DATE_SUB(@now, INTERVAL 3 DAY)),
(10002, 1002, 'RULE_ENGINE', 'WECHAT', 'SENT',    1, DATE_SUB(@now, INTERVAL 4 DAY), 90,  @tid, @cb, DATE_SUB(@now, INTERVAL 4 DAY)),
(10002, 1010, 'RULE_ENGINE', 'WECHAT', 'SENT',    1, DATE_SUB(@now, INTERVAL 2 DAY), 60,  @tid, @cb, DATE_SUB(@now, INTERVAL 2 DAY)),
(10003, 1004, 'SEASONAL',    'SMS',    'SENT',    0, NULL,                            NULL, @tid, @cb, '2025-01-15 09:00:00'),
(10004, 1010, 'RULE_ENGINE', 'WECHAT', 'SENT',    1, DATE_SUB(@now, INTERVAL 1 DAY), 180, @tid, @cb, DATE_SUB(@now, INTERVAL 1 DAY)),
(10005, 1001, 'WEATHER',     'SMS',    'PENDING', 0, NULL,                            NULL, @tid, @cb, @now);

-- =============================================
-- 42. 报告实例（PDF + 二维码 + 签章）
-- =============================================
INSERT INTO `ch_report_instance` (`report_id`, `patient_id`, `template_id`, `report_status`, `pdf_file_id`, `qr_code`, `sign_status`, `sign_time`, `tenant_id`, `create_by`, `create_time`) VALUES
(42001, 1001, 13001, 'COMPLETED', 200001, 'QR_R42001_CHECKSUM', 1, '2024-09-20 15:00:00', @tid, @cb, '2024-09-20 15:00:00'),
(42002, 1002, 13002, 'COMPLETED', 200002, 'QR_R42002_CHECKSUM', 1, '2025-01-05 10:00:00', @tid, @cb, '2025-01-05 10:00:00'),
(42003, 1004, 13001, 'COMPLETED', 200003, 'QR_R42003_CHECKSUM', 1, '2024-11-15 14:00:00', @tid, @cb, '2024-11-15 14:00:00'),
(42004, 1010, 13002, 'GENERATING',NULL,   NULL,                 0, NULL,                  @tid, @cb, @now);

-- =============================================
-- 43. 文件附件
-- =============================================
INSERT INTO `ch_file_attachment` (`file_id`, `biz_type`, `biz_id`, `file_name`, `file_size`, `oss_id`, `tenant_id`, `create_by`, `create_time`) VALUES
(100001, 'SIGN_IMAGE',  24001, 'consent_sign_24001.png', 25600,  200001001, @tid, @cb, @now),
(100002, 'SIGN_IMAGE',  24002, 'consent_sign_24002.png', 26400,  200001002, @tid, @cb, @now),
(100003, 'FUNDUS_PHOTO',36003, 'fundus_p1002.jpg',       1245678,200002001, @tid, @cb, @now),
(100004, 'ECG',         36005, 'ecg_p1004.pdf',          524288, 200002002, @tid, @cb, @now),
(200001, 'REPORT_PDF',  42001, 'annual_report_p1001.pdf',2097152,200003001, @tid, @cb, @now),
(200002, 'REPORT_PDF',  42002, 'quarterly_p1002.pdf',    1572864,200003002, @tid, @cb, @now),
(200003, 'REPORT_PDF',  42003, 'annual_report_p1004.pdf',2345678,200003003, @tid, @cb, @now);

-- =============================================
-- 44. 审计日志
-- =============================================
INSERT INTO `ch_audit_log` (`operation_type`, `operation_target`, `operation_detail`, `operator_id`, `operator_name`, `operator_ip`, `operation_time`, `create_by`, `create_time`) VALUES
('MEDICATION_ADJUST', 'ch_medication_record:25001', '氨氯地平剂量调整并联用缬沙坦',  2001, '张医生', '192.168.1.100', '2024-08-10 14:00:00', @cb, @now),
('RISK_ASSESSMENT',   'ch_risk_assessment:27003',   '王立军高血压危急值评估',        2004, '李医生', '192.168.1.101', '2025-01-06 09:00:00', @cb, @now),
('REFERRAL_CREATE',   'ch_referral_record:39002',   '吴桂珍下转乡镇卫生院',          2002, '陈医生', '192.168.1.102', '2025-01-10 10:00:00', @cb, @now),
('REPORT_GENERATE',   'ch_report_instance:42001',   '生成张建国年度体检报告',        2001, '张医生', '192.168.1.100', '2024-09-20 15:00:00', @cb, @now),
('CONSENT_SIGN',      'ch_consent_record:24001',    '张建国签约知情同意电子签名',    2001, '张医生', '192.168.1.100', '2020-03-20 10:30:00', @cb, @now),
('WARNING_RESOLVE',   'ch_warning_event:37001',     '王立军血压危急值处置完成',      2004, '李医生', '192.168.1.101', '2025-04-20 10:00:00', @cb, @now),
('PATIENT_ARCHIVE',   'ch_patient_profile:1009',    '黄志刚新建档案',                 2001, '张医生', '192.168.1.100', '2023-12-05 10:00:00', @cb, @now);

-- =============================================
-- 45. 区域统计日 / 病种统计日 / 机构统计日 / 预警统计日 / 随访统计日
-- =============================================
INSERT INTO `ch_stat_area_day` (`area_code`, `stat_date`, `patient_count`, `managed_count`, `warning_count`, `followup_count`, `tenant_id`, `create_by`, `create_time`) VALUES
('370102', CURDATE(),                   5,  4, 2, 3, @tid, @cb, @now),
('370102', DATE_SUB(CURDATE(), INTERVAL 1 DAY), 5,  4, 1, 2, @tid, @cb, @now),
('370102', DATE_SUB(CURDATE(), INTERVAL 2 DAY), 5,  4, 1, 4, @tid, @cb, @now),
('370103', CURDATE(),                   2,  2, 2, 1, @tid, @cb, @now),
('370104', CURDATE(),                   1,  1, 0, 1, @tid, @cb, @now),
('370105', CURDATE(),                   1,  1, 0, 0, @tid, @cb, @now),
('370112', CURDATE(),                   1,  1, 0, 0, @tid, @cb, @now),
('370113', CURDATE(),                   1,  0, 0, 0, @tid, @cb, @now),
('370100', CURDATE(),                  10,  8, 4, 5, @tid, @cb, @now),
('370000', CURDATE(),                  10,  8, 4, 5, @tid, @cb, @now);

INSERT INTO `ch_stat_disease_day` (`disease_code`, `stat_date`, `patient_count`, `new_count`, `risk_high_count`, `tenant_id`, `create_by`, `create_time`) VALUES
('HYPERTENSION',   CURDATE(),4, 0, 3, @tid, @cb, @now),
('DIABETES',       CURDATE(),4, 0, 4, @tid, @cb, @now),
('HYPERLIPIDEMIA', CURDATE(),2, 0, 0, @tid, @cb, @now),
('CHD',            CURDATE(),2, 0, 1, @tid, @cb, @now),
('STROKE',         CURDATE(),1, 0, 1, @tid, @cb, @now),
('NEPHROTIC',      CURDATE(),1, 1, 0, @tid, @cb, @now),
('FUNDUS',         CURDATE(),1, 0, 1, @tid, @cb, @now),
('NEUROPATHY',     CURDATE(),1, 0, 1, @tid, @cb, @now),
('VASCULOPATHY',   CURDATE(),1, 0, 1, @tid, @cb, @now);

INSERT INTO `ch_stat_org_day` (`org_id`, `stat_date`, `patient_count`, `followup_done_count`, `warning_count`, `tenant_id`, `create_by`, `create_time`) VALUES
(3001, CURDATE(), 6, 2, 2, @tid, @cb, @now),
(3002, CURDATE(), 2, 0, 1, @tid, @cb, @now),
(3003, CURDATE(), 1, 0, 0, @tid, @cb, @now),
(3004, CURDATE(), 0, 0, 0, @tid, @cb, @now),
(3005, CURDATE(), 0, 0, 0, @tid, @cb, @now);

INSERT INTO `ch_stat_warning_day` (`stat_date`, `total_count`, `resolved_count`, `escalated_count`, `avg_resolve_minutes`, `tenant_id`, `create_by`, `create_time`) VALUES
(CURDATE(),                         2, 0, 0, 0,    @tid, @cb, @now),
(DATE_SUB(CURDATE(), INTERVAL 1 DAY), 1, 1, 0, 360,@tid, @cb, @now),
(DATE_SUB(CURDATE(), INTERVAL 2 DAY), 2, 1, 0, 480,@tid, @cb, @now),
(DATE_SUB(CURDATE(), INTERVAL 3 DAY), 1, 0, 0, 0,  @tid, @cb, @now),
(DATE_SUB(CURDATE(), INTERVAL 4 DAY), 1, 0, 0, 0,  @tid, @cb, @now),
(DATE_SUB(CURDATE(), INTERVAL 5 DAY), 1, 0, 1, 0,  @tid, @cb, @now);

INSERT INTO `ch_stat_followup_day` (`stat_date`, `total_count`, `done_count`, `overdue_count`, `completion_rate`, `tenant_id`, `create_by`, `create_time`) VALUES
(CURDATE(),                         5, 2, 2, 40.00, @tid, @cb, @now),
(DATE_SUB(CURDATE(), INTERVAL 1 DAY), 4, 3, 1, 75.00, @tid, @cb, @now),
(DATE_SUB(CURDATE(), INTERVAL 2 DAY), 6, 4, 0, 66.67, @tid, @cb, @now),
(DATE_SUB(CURDATE(), INTERVAL 3 DAY), 3, 3, 0, 100.00,@tid, @cb, @now);

-- =============================================
-- 完成
-- =============================================
SELECT '=== 慢病管理系统测试数据初始化完成 ===' AS msg;
SELECT 'patient_count' AS metric, COUNT(*) AS value FROM `ch_patient_profile`
UNION ALL SELECT 'disease_count',  COUNT(*) FROM `ch_patient_disease`
UNION ALL SELECT 'contract_count', COUNT(*) FROM `ch_patient_contract`
UNION ALL SELECT 'medication_count',COUNT(*) FROM `ch_medication_record`
UNION ALL SELECT 'risk_count',     COUNT(*) FROM `ch_risk_assessment`
UNION ALL SELECT 'plan_count',     COUNT(*) FROM `ch_manage_plan`
UNION ALL SELECT 'followup_task_count', COUNT(*) FROM `ch_followup_task`
UNION ALL SELECT 'metric_count',   COUNT(*) FROM `ch_health_metric_record`
UNION ALL SELECT 'warning_count',  COUNT(*) FROM `ch_warning_event`
UNION ALL SELECT 'exam_count',     COUNT(*) FROM `ch_health_exam`
UNION ALL SELECT 'referral_count', COUNT(*) FROM `ch_referral_record`
UNION ALL SELECT 'message_count',  COUNT(*) FROM `ch_message_content`
UNION ALL SELECT 'report_count',   COUNT(*) FROM `ch_report_instance`;
