-- =============================================
-- unimed-chronic 慢病管理字典初始化脚本
-- 基于 unimed-chronic/CLAUDE.md 字段值定义生成
-- =============================================

USE `unimed-cloud`;

-- ----------------------------
-- 慢病字典类型
-- ----------------------------

-- 签约相关
INSERT INTO sys_dict_type VALUES(100, '000000', '签约类型', 'chronic_contract_type',        103, 1, sysdate(), null, null, '慢病签约类型列表');
INSERT INTO sys_dict_type VALUES(101, '000000', '续约状态', 'chronic_renewal_status',       103, 1, sysdate(), null, null, '慢病续约状态列表');
INSERT INTO sys_dict_type VALUES(102, '000000', '合同状态', 'chronic_contract_status',      103, 1, sysdate(), null, null, '慢病合同状态列表');
INSERT INTO sys_dict_type VALUES(103, '000000', '服务包类型', 'chronic_package_type',       103, 1, sysdate(), null, null, '慢病服务包类型列表');
INSERT INTO sys_dict_type VALUES(104, '000000', '履约状态', 'chronic_fulfillment_status',   103, 1, sysdate(), null, null, '慢病履约状态列表');

-- 患者档案
INSERT INTO sys_dict_type VALUES(105, '000000', '性别', 'chronic_gender',                   103, 1, sysdate(), null, null, '慢病患者性别列表');
INSERT INTO sys_dict_type VALUES(106, '000000', '管理状态', 'chronic_manage_status',        103, 1, sysdate(), null, null, '慢病管理状态列表');
INSERT INTO sys_dict_type VALUES(107, '000000', '患者来源', 'chronic_patient_source',       103, 1, sysdate(), null, null, '慢病患者来源列表');

-- 医生团队
INSERT INTO sys_dict_type VALUES(108, '000000', '成员角色', 'doctor_group_type',          103, 1, sysdate(), null, null, '慢病医生团队成员角色列表');
INSERT INTO sys_dict_type VALUES(109, '000000', '团队状态', 'chronic_team_status',          103, 1, sysdate(), null, null, '慢病医生团队状态列表');

-- 用药管理
INSERT INTO sys_dict_type VALUES(110, '000000', '用药状态', 'chronic_medication_status',       103, 1, sysdate(), null, null, '慢病用药状态列表');
INSERT INTO sys_dict_type VALUES(111, '000000', '用药调整类型', 'chronic_medication_adjust_type', 103, 1, sysdate(), null, null, '慢病用药调整类型列表');
INSERT INTO sys_dict_type VALUES(112, '000000', '药物相互作用等级', 'chronic_interaction_level', 103, 1, sysdate(), null, null, '慢病药物相互作用等级列表');

-- 风险与评估
INSERT INTO sys_dict_type VALUES(113, '000000', '风险等级', 'chronic_risk_level',           103, 1, sysdate(), null, null, '慢病风险等级列表');
INSERT INTO sys_dict_type VALUES(114, '000000', '管理计划状态', 'chronic_plan_status',      103, 1, sysdate(), null, null, '慢病管理计划状态列表');
INSERT INTO sys_dict_type VALUES(115, '000000', '计划项类型', 'chronic_plan_item_type',     103, 1, sysdate(), null, null, '慢病计划项类型列表');

-- 随访
INSERT INTO sys_dict_type VALUES(116, '000000', '随访任务状态', 'chronic_followup_task_status', 103, 1, sysdate(), null, null, '慢病随访任务状态列表');
INSERT INTO sys_dict_type VALUES(117, '000000', '随访方式', 'chronic_visit_type',           103, 1, sysdate(), null, null, '慢病随访方式列表');

-- 健康指标
INSERT INTO sys_dict_type VALUES(118, '000000', '数据来源', 'chronic_data_source',          103, 1, sysdate(), null, null, '慢病健康指标数据来源列表');

-- 生活方式
INSERT INTO sys_dict_type VALUES(119, '000000', '吸烟状态', 'chronic_smoking_status',       103, 1, sysdate(), null, null, '慢病吸烟状态列表');
INSERT INTO sys_dict_type VALUES(120, '000000', '饮酒状态', 'chronic_drinking_status',      103, 1, sysdate(), null, null, '慢病饮酒状态列表');
INSERT INTO sys_dict_type VALUES(121, '000000', '依从性等级', 'chronic_compliance_level',   103, 1, sysdate(), null, null, '慢病依从性等级列表');

-- 体检
INSERT INTO sys_dict_type VALUES(122, '000000', '体检类型', 'chronic_exam_type',            103, 1, sysdate(), null, null, '慢病体检类型列表');
INSERT INTO sys_dict_type VALUES(123, '000000', '专项类别', 'chronic_special_category',     103, 1, sysdate(), null, null, '慢病专项筛查类别列表');

-- 预警
INSERT INTO sys_dict_type VALUES(124, '000000', '预警等级', 'chronic_warning_level',        103, 1, sysdate(), null, null, '慢病预警等级列表');
INSERT INTO sys_dict_type VALUES(125, '000000', '预警事件状态', 'chronic_warning_event_status', 103, 1, sysdate(), null, null, '慢病预警事件状态列表');
INSERT INTO sys_dict_type VALUES(126, '000000', '处置类型', 'chronic_action_type',          103, 1, sysdate(), null, null, '慢病预警处置类型列表');

-- 转诊
INSERT INTO sys_dict_type VALUES(127, '000000', '转诊状态', 'chronic_referral_status',      103, 1, sysdate(), null, null, '慢病转诊状态列表');
INSERT INTO sys_dict_type VALUES(128, '000000', '转诊类型', 'chronic_referral_type',        103, 1, sysdate(), null, null, '慢病转诊类型列表');

-- 就诊
INSERT INTO sys_dict_type VALUES(129, '000000', '就诊类型', 'chronic_encounter_type',       103, 1, sysdate(), null, null, '慢病就诊类型列表');
INSERT INTO sys_dict_type VALUES(130, '000000', '提交状态', 'chronic_submit_status',        103, 1, sysdate(), null, null, '慢病就诊提交状态列表');
INSERT INTO sys_dict_type VALUES(131, '000000', '诊断类型', 'chronic_diagnosis_type',       103, 1, sysdate(), null, null, '慢病诊断类型列表');

-- 其他
INSERT INTO sys_dict_type VALUES(132, '000000', '标签类型', 'chronic_tag_type',             103, 1, sysdate(), null, null, '慢病标签类型列表');
INSERT INTO sys_dict_type VALUES(133, '000000', '时间线事件类型', 'chronic_event_type',     103, 1, sysdate(), null, null, '慢病时间线事件类型列表');
INSERT INTO sys_dict_type VALUES(134, '000000', '入组状态', 'chronic_enroll_status',        103, 1, sysdate(), null, null, '慢病入组状态列表');
INSERT INTO sys_dict_type VALUES(135, '000000', '同意类型', 'chronic_consent_type',         103, 1, sysdate(), null, null, '慢病知情同意类型列表');
INSERT INTO sys_dict_type VALUES(136, '000000', '会话类型', 'chronic_session_type',         103, 1, sysdate(), null, null, '慢病消息会话类型列表');
INSERT INTO sys_dict_type VALUES(137, '000000', '触发类型', 'chronic_trigger_type',         103, 1, sysdate(), null, null, '慢病推送触发类型列表');
INSERT INTO sys_dict_type VALUES(138, '000000', '推送渠道', 'chronic_push_channel',         103, 1, sysdate(), null, null, '慢病推送渠道列表');
INSERT INTO sys_dict_type VALUES(139, '000000', 'ICD版本', 'chronic_icd_version',             103, 1, sysdate(), null, null, '慢病ICD版本列表');
INSERT INTO sys_dict_type VALUES(140, '000000', '附件业务类型', 'chronic_biz_type',         103, 1, sysdate(), null, null, '慢病附件业务类型列表');
INSERT INTO sys_dict_type VALUES(141, '000000', 'KPI分类', 'chronic_kpi_category',           103, 1, sysdate(), null, null, '慢病KPI分类列表');
INSERT INTO sys_dict_type VALUES(142, '000000', '指标类型', 'chronic_metric_type',          103, 1, sysdate(), null, null, '慢病健康指标类型列表');
INSERT INTO sys_dict_type VALUES(143, '000000', '民族', 'chronic_nation',               103, 1, sysdate(), null, null, '慢病民族字典');
INSERT INTO sys_dict_type VALUES(144, '000000', '职业', 'chronic_occupation',           103, 1, sysdate(), null, null, '慢病职业字典');
INSERT INTO sys_dict_type VALUES(145, '000000', '文化程度', 'chronic_education_level',      103, 1, sysdate(), null, null, '慢病文化程度字典');
INSERT INTO sys_dict_type VALUES(146, '000000', '残疾类型', 'chronic_disability_type',      103, 1, sysdate(), null, null, '慢病残疾类型字典');
INSERT INTO sys_dict_type VALUES(147, '000000', '残疾等级', 'chronic_disability_level',     103, 1, sysdate(), null, null, '慢病残疾等级字典');


-- ----------------------------
-- 慢病字典数据
-- ----------------------------

-- 100 chronic_contract_type 签约类型
INSERT INTO sys_dict_data VALUES(100, '000000', 1, '个人签约',     'PERSONAL',    'chronic_contract_type', '', '', 'N', 103, 1, sysdate(), null, null, '患者个人与医疗机构/医生团队的服务协议');
INSERT INTO sys_dict_data VALUES(101, '000000', 2, '家庭签约',     'FAMILY',      'chronic_contract_type', '', '', 'N', 103, 1, sysdate(), null, null, '家庭成员统一签约慢病管理服务');
INSERT INTO sys_dict_data VALUES(102, '000000', 3, '团体签约',     'GROUP',       'chronic_contract_type', '', '', 'N', 103, 1, sysdate(), null, null, '企业、学校、机构等为成员统一签约服务');
INSERT INTO sys_dict_data VALUES(103, '000000', 4, '社区签约',     'COMMUNITY',   'chronic_contract_type', '', '', 'N', 103, 1, sysdate(), null, null, '社区卫生中心为辖区居民提供统一的慢病管理服务');
INSERT INTO sys_dict_data VALUES(104, '000000', 5, '企业签约',     'CORPORATE',   'chronic_contract_type', '', '', 'N', 103, 1, sysdate(), null, null, '专门针对企业客户的健康管理服务包');

-- 101 chronic_renewal_status 续约状态
INSERT INTO sys_dict_data VALUES(105, '000000', 1, '有效中',       'ACTIVE',      'chronic_renewal_status', '', '', 'Y', 103, 1, sysdate(), null, null, '合同当前处于有效状态');
INSERT INTO sys_dict_data VALUES(106, '000000', 2, '即将到期',     'EXPIRING',    'chronic_renewal_status', '', '', 'N', 103, 1, sysdate(), null, null, '合同即将到达结束日期');
INSERT INTO sys_dict_data VALUES(107, '000000', 3, '已到期',       'EXPIRED',     'chronic_renewal_status', '', '', 'N', 103, 1, sysdate(), null, null, '合同已超过结束日期');
INSERT INTO sys_dict_data VALUES(108, '000000', 4, '已续约',       'RENEWED',     'chronic_renewal_status', '', '', 'N', 103, 1, sysdate(), null, null, '合同已经成功续约');

-- 102 chronic_contract_status 合同状态
INSERT INTO sys_dict_data VALUES(109, '000000', 1, '有效中',       'ACTIVE',      'chronic_contract_status', '', '', 'Y', 103, 1, sysdate(), null, null, '合同当前有效且正在执行中');
INSERT INTO sys_dict_data VALUES(110, '000000', 2, '已终止',       'TERMINATED',  'chronic_contract_status', '', '', 'N', 103, 1, sysdate(), null, null, '合同被提前终止');
INSERT INTO sys_dict_data VALUES(1110, '000000', 3, '未签约', 'UNSIGNED', 'chronic_contract_status', '', '', 'N', 103, 1, sysdate(), null, null, '患者尚未签约');

-- 103 chronic_package_type 服务包类型
INSERT INTO sys_dict_data VALUES(111, '000000', 1, '基础包',       'BASIC',       'chronic_package_type', '', '', 'Y', 103, 1, sysdate(), null, null, '基础服务包，包含常规慢病管理服务');
INSERT INTO sys_dict_data VALUES(112, '000000', 2, '高级包',       'ADVANCED',    'chronic_package_type', '', '', 'N', 103, 1, sysdate(), null, null, '高级服务包，增加专项检查和优先服务');
INSERT INTO sys_dict_data VALUES(113, '000000', 3, '自定义包',     'CUSTOM',      'chronic_package_type', '', '', 'N', 103, 1, sysdate(), null, null, '自定义服务包，按需配置服务内容');

-- 104 chronic_fulfillment_status 履约状态
INSERT INTO sys_dict_data VALUES(114, '000000', 1, '已计划',       'PLANNED',     'chronic_fulfillment_status', '', '', 'N', 103, 1, sysdate(), null, null, '计划中的履约项');
INSERT INTO sys_dict_data VALUES(115, '000000', 2, '已完成',       'DONE',        'chronic_fulfillment_status', '', '', 'Y', 103, 1, sysdate(), null, null, '已完成履约');
INSERT INTO sys_dict_data VALUES(116, '000000', 3, '已逾期',       'MISSED',      'chronic_fulfillment_status', '', '', 'N', 103, 1, sysdate(), null, null, '逾期未完成');

-- 105 chronic_gender 性别（慢病独立字典，0=女 1=男 2=未知）
INSERT INTO sys_dict_data VALUES(117, '000000', 1, '女',           '0',           'chronic_gender', '', '', 'Y', 103, 1, sysdate(), null, null, '女性');
INSERT INTO sys_dict_data VALUES(118, '000000', 2, '男',           '1',           'chronic_gender', '', '', 'N', 103, 1, sysdate(), null, null, '男性');
INSERT INTO sys_dict_data VALUES(119, '000000', 3, '未知',         '2',           'chronic_gender', '', '', 'N', 103, 1, sysdate(), null, null, '性别未知');

-- 106 chronic_manage_status 管理状态
INSERT INTO sys_dict_data VALUES(120, '000000', 1, '待入组',       'PENDING_ENTRY',     'chronic_manage_status', '', '', 'N', 103, 1, sysdate(), null, null, '筛查通过待建档');
INSERT INTO sys_dict_data VALUES(121, '000000', 2, '管理中',       'MANAGED',           'chronic_manage_status', '', '', 'Y', 103, 1, sysdate(), null, null, '正常管理中');
INSERT INTO sys_dict_data VALUES(122, '000000', 3, '随访逾期',     'FOLLOWUP_OVERDUE',  'chronic_manage_status', '', '', 'N', 103, 1, sysdate(), null, null, '有随访任务超期未完成');
INSERT INTO sys_dict_data VALUES(123, '000000', 4, '预警活跃',     'WARNING_ACTIVE',    'chronic_manage_status', '', '', 'N', 103, 1, sysdate(), null, null, '存在未处理预警');
INSERT INTO sys_dict_data VALUES(124, '000000', 5, '转诊中',       'REFERRING',         'chronic_manage_status', '', '', 'N', 103, 1, sysdate(), null, null, '正在转诊流程中');
INSERT INTO sys_dict_data VALUES(125, '000000', 6, '暂停管理',     'PAUSED',            'chronic_manage_status', '', '', 'N', 103, 1, sysdate(), null, null, '管理暂停（如住院、外出）');
INSERT INTO sys_dict_data VALUES(126, '000000', 7, '已关闭',       'CLOSED',            'chronic_manage_status', '', '', 'N', 103, 1, sysdate(), null, null, '管理终止');

-- 107 chronic_patient_source 患者来源
INSERT INTO sys_dict_data VALUES(127, '000000', 1, '门诊',         'OUTPATIENT',   'chronic_patient_source', '', '', 'Y', 103, 1, sysdate(), null, null, '门诊就诊建档');
INSERT INTO sys_dict_data VALUES(128, '000000', 2, '筛查',         'SCREENING',    'chronic_patient_source', '', '', 'N', 103, 1, sysdate(), null, null, '社区筛查建档');
INSERT INTO sys_dict_data VALUES(129, '000000', 3, 'HIS同步',      'HIS_SYNC',     'chronic_patient_source', '', '', 'N', 103, 1, sysdate(), null, null, 'HIS系统自动同步');
INSERT INTO sys_dict_data VALUES(130, '000000', 4, '转入',         'TRANSFER',     'chronic_patient_source', '', '', 'N', 103, 1, sysdate(), null, null, '其他机构转入');

-- 108 doctor_group_type 成员角色
INSERT INTO sys_dict_data VALUES(131, '000000', 1, '团队负责人',   'LEADER',       'doctor_group_type', '', '', 'N', 103, 1, sysdate(), null, null, '团队负责人');
INSERT INTO sys_dict_data VALUES(132, '000000', 2, '团队成员',     'MEMBER',       'doctor_group_type', '', '', 'N', 103, 1, sysdate(), null, null, '团队成员');

-- 109 chronic_team_status 团队状态
INSERT INTO sys_dict_data VALUES(133, '000000', 1, '活跃',         'ACTIVE',       'chronic_team_status', '', '', 'Y', 103, 1, sysdate(), null, null, '团队正常运作');
INSERT INTO sys_dict_data VALUES(134, '000000', 2, '已解散',       'DISSOLVED',    'chronic_team_status', '', '', 'N', 103, 1, sysdate(), null, null, '团队已解散');

-- 110 chronic_medication_status 用药状态
INSERT INTO sys_dict_data VALUES(135, '000000', 1, '使用中',       'ACTIVE',       'chronic_medication_status', '', '', 'Y', 103, 1, sysdate(), null, null, '当前正在使用');
INSERT INTO sys_dict_data VALUES(136, '000000', 2, '已停药',       'STOPPED',      'chronic_medication_status', '', '', 'N', 103, 1, sysdate(), null, null, '已停用');

-- 111 chronic_medication_adjust_type 用药调整类型
INSERT INTO sys_dict_data VALUES(137, '000000', 1, '加药',         'ADD',          'chronic_medication_adjust_type', '', '', 'N', 103, 1, sysdate(), null, null, '新增药品');
INSERT INTO sys_dict_data VALUES(138, '000000', 2, '减药',         'REDUCE',       'chronic_medication_adjust_type', '', '', 'N', 103, 1, sysdate(), null, null, '减少药品');
INSERT INTO sys_dict_data VALUES(139, '000000', 3, '换药',         'SWITCH',       'chronic_medication_adjust_type', '', '', 'N', 103, 1, sysdate(), null, null, '替换药品');
INSERT INTO sys_dict_data VALUES(140, '000000', 4, '调量',         'DOSE_CHANGE',  'chronic_medication_adjust_type', '', '', 'N', 103, 1, sysdate(), null, null, '调整剂量');

-- 112 chronic_interaction_level 药物相互作用等级
INSERT INTO sys_dict_data VALUES(141, '000000', 1, '禁忌',         'CONTRAINDICATED', 'chronic_interaction_level', '', '', 'N', 103, 1, sysdate(), null, null, '禁止联用');
INSERT INTO sys_dict_data VALUES(142, '000000', 2, '重大风险',     'MAJOR_RISK',      'chronic_interaction_level', '', '', 'N', 103, 1, sysdate(), null, null, '联用风险高，需医生确认');
INSERT INTO sys_dict_data VALUES(143, '000000', 3, '需监测',       'MONITOR',         'chronic_interaction_level', '', '', 'Y', 103, 1, sysdate(), null, null, '联用需常规监测');

-- 113 chronic_risk_level 风险等级
INSERT INTO sys_dict_data VALUES(144, '000000', 1, '低风险',       'LOW',       'chronic_risk_level', '', '', 'N', 103, 1, sysdate(), null, null, '低风险');
INSERT INTO sys_dict_data VALUES(145, '000000', 2, '中风险',       'MEDIUM',    'chronic_risk_level', '', '', 'N', 103, 1, sysdate(), null, null, '中风险');
INSERT INTO sys_dict_data VALUES(146, '000000', 3, '高风险',       'HIGH',      'chronic_risk_level', '', '', 'N', 103, 1, sysdate(), null, null, '高风险');
INSERT INTO sys_dict_data VALUES(147, '000000', 4, '极高风险',     'VERY_HIGH', 'chronic_risk_level', '', '', 'N', 103, 1, sysdate(), null, null, '极高风险');

-- 114 chronic_plan_status 管理计划状态
INSERT INTO sys_dict_data VALUES(148, '000000', 1, '草稿',         'DRAFT',     'chronic_plan_status', '', '', 'N', 103, 1, sysdate(), null, null, '草稿状态');
INSERT INTO sys_dict_data VALUES(149, '000000', 2, '生效中',       'ACTIVE',    'chronic_plan_status', '', '', 'Y', 103, 1, sysdate(), null, null, '当前生效方案');
INSERT INTO sys_dict_data VALUES(150, '000000', 3, '已停用',       'DISABLED',  'chronic_plan_status', '', '', 'N', 103, 1, sysdate(), null, null, '已停用');
INSERT INTO sys_dict_data VALUES(151, '000000', 4, '历史',         'HISTORY',   'chronic_plan_status', '', '', 'N', 103, 1, sysdate(), null, null, '历史方案（被新方案替代）');

-- 115 chronic_plan_item_type 计划项类型
INSERT INTO sys_dict_data VALUES(152, '000000', 1, '用药',         'MEDICATION',  'chronic_plan_item_type', '', '', 'N', 103, 1, sysdate(), null, null, '用药计划项');
INSERT INTO sys_dict_data VALUES(153, '000000', 2, '饮食',         'DIET',        'chronic_plan_item_type', '', '', 'N', 103, 1, sysdate(), null, null, '饮食计划项');
INSERT INTO sys_dict_data VALUES(154, '000000', 3, '运动',         'EXERCISE',    'chronic_plan_item_type', '', '', 'N', 103, 1, sysdate(), null, null, '运动计划项');
INSERT INTO sys_dict_data VALUES(155, '000000', 4, '心理',         'PSYCHOLOGY',  'chronic_plan_item_type', '', '', 'N', 103, 1, sysdate(), null, null, '心理计划项');
INSERT INTO sys_dict_data VALUES(156, '000000', 5, '随访',         'FOLLOWUP',    'chronic_plan_item_type', '', '', 'N', 103, 1, sysdate(), null, null, '随访计划项');
INSERT INTO sys_dict_data VALUES(157, '000000', 6, '监测',         'MONITOR',     'chronic_plan_item_type', '', '', 'N', 103, 1, sysdate(), null, null, '监测计划项');

-- 116 chronic_followup_task_status 随访任务状态
INSERT INTO sys_dict_data VALUES(158, '000000', 1, '待执行',       'PENDING',      'chronic_followup_task_status', '', '', 'Y', 103, 1, sysdate(), null, null, '等待执行');
INSERT INTO sys_dict_data VALUES(159, '000000', 2, '提醒中',       'REMINDING',    'chronic_followup_task_status', '', '', 'N', 103, 1, sysdate(), null, null, '已发送提醒');
INSERT INTO sys_dict_data VALUES(160, '000000', 3, '已完成',       'DONE',         'chronic_followup_task_status', '', '', 'N', 103, 1, sysdate(), null, null, '已完成随访');
INSERT INTO sys_dict_data VALUES(161, '000000', 4, '已逾期',       'OVERDUE',      'chronic_followup_task_status', '', '', 'N', 103, 1, sysdate(), null, null, '超过计划日期未完成');
INSERT INTO sys_dict_data VALUES(162, '000000', 5, '已取消',       'CANCELLED',    'chronic_followup_task_status', '', '', 'N', 103, 1, sysdate(), null, null, '已取消');

-- 117 chronic_visit_type 随访方式
INSERT INTO sys_dict_data VALUES(163, '000000', 1, '电话',         'PHONE',        'chronic_visit_type', '', '', 'Y', 103, 1, sysdate(), null, null, '电话随访');
INSERT INTO sys_dict_data VALUES(164, '000000', 2, '视频',         'VIDEO',        'chronic_visit_type', '', '', 'N', 103, 1, sysdate(), null, null, '视频随访');
INSERT INTO sys_dict_data VALUES(165, '000000', 3, '线下',         'OFFLINE',      'chronic_visit_type', '', '', 'N', 103, 1, sysdate(), null, null, '上门/门诊随访');
INSERT INTO sys_dict_data VALUES(166, '000000', 4, '患者自填',     'SELF_FILL',    'chronic_visit_type', '', '', 'N', 103, 1, sysdate(), null, null, '患者自行填写问卷');
INSERT INTO sys_dict_data VALUES(167, '000000', 5, '管理员代填',   'ADMIN_PROXY',  'chronic_visit_type', '', '', 'N', 103, 1, sysdate(), null, null, '管理员代填');

-- 118 chronic_data_source 数据来源
INSERT INTO sys_dict_data VALUES(168, '000000', 1, '手动录入',     'MANUAL',       'chronic_data_source', '', '', 'Y', 103, 1, sysdate(), null, null, '医生或患者手动录入');
INSERT INTO sys_dict_data VALUES(169, '000000', 2, '设备采集',     'DEVICE',       'chronic_data_source', '', '', 'N', 103, 1, sysdate(), null, null, 'IoT设备自动上报');
INSERT INTO sys_dict_data VALUES(170, '000000', 3, 'HIS/LIS同步',  'HIS_LIS',      'chronic_data_source', '', '', 'N', 103, 1, sysdate(), null, null, '从HIS/LIS系统同步');

-- 119 chronic_smoking_status 吸烟状态
INSERT INTO sys_dict_data VALUES(171, '000000', 1, '从不吸烟',     'NEVER',    'chronic_smoking_status', '', '', 'Y', 103, 1, sysdate(), null, null, '从不吸烟');
INSERT INTO sys_dict_data VALUES(172, '000000', 2, '已戒烟',       'FORMER',   'chronic_smoking_status', '', '', 'N', 103, 1, sysdate(), null, null, '已戒烟');
INSERT INTO sys_dict_data VALUES(173, '000000', 3, '当前吸烟',     'CURRENT',  'chronic_smoking_status', '', '', 'N', 103, 1, sysdate(), null, null, '当前吸烟');

-- 120 chronic_drinking_status 饮酒状态
INSERT INTO sys_dict_data VALUES(174, '000000', 1, '从不饮酒',     'NEVER',    'chronic_drinking_status', '', '', 'Y', 103, 1, sysdate(), null, null, '从不饮酒');
INSERT INTO sys_dict_data VALUES(175, '000000', 2, '已戒酒',       'FORMER',   'chronic_drinking_status', '', '', 'N', 103, 1, sysdate(), null, null, '已戒酒');
INSERT INTO sys_dict_data VALUES(176, '000000', 3, '当前饮酒',     'CURRENT',  'chronic_drinking_status', '', '', 'N', 103, 1, sysdate(), null, null, '当前饮酒');

-- 121 chronic_compliance_level 依从性等级
INSERT INTO sys_dict_data VALUES(177, '000000', 1, '良好',         'GOOD',     'chronic_compliance_level', '', '', 'Y', 103, 1, sysdate(), null, null, '依从性良好');
INSERT INTO sys_dict_data VALUES(178, '000000', 2, '一般',         'FAIR',     'chronic_compliance_level', '', '', 'N', 103, 1, sysdate(), null, null, '依从性一般');
INSERT INTO sys_dict_data VALUES(179, '000000', 3, '差',           'POOR',     'chronic_compliance_level', '', '', 'N', 103, 1, sysdate(), null, null, '依从性差');

-- 122 chronic_exam_type 体检类型
INSERT INTO sys_dict_data VALUES(180, '000000', 1, '年度体检',     'ANNUAL_CHECKUP',  'chronic_exam_type', '', '', 'Y', 103, 1, sysdate(), null, null, '年度健康体检');
INSERT INTO sys_dict_data VALUES(181, '000000', 2, '常规检查',     'REGULAR_TEST',    'chronic_exam_type', '', '', 'N', 103, 1, sysdate(), null, null, '定期复查');
INSERT INTO sys_dict_data VALUES(182, '000000', 3, '专项检查',     'SPECIAL_TEST',    'chronic_exam_type', '', '', 'N', 103, 1, sysdate(), null, null, '专项检查');

-- 123 chronic_special_category 专项类别
INSERT INTO sys_dict_data VALUES(183, '000000', 1, '眼底照相',     'FUNDUS_PHOTO',       'chronic_special_category', '', '', 'N', 103, 1, sysdate(), null, null, '眼底照相');
INSERT INTO sys_dict_data VALUES(184, '000000', 2, '踝肱指数',     'ABI',                'chronic_special_category', '', '', 'N', 103, 1, sysdate(), null, null, '踝肱指数');
INSERT INTO sys_dict_data VALUES(185, '000000', 3, '神经传导',     'NERVE_CONDUCTION',   'chronic_special_category', '', '', 'N', 103, 1, sysdate(), null, null, '神经传导');
INSERT INTO sys_dict_data VALUES(186, '000000', 4, '心电图',       'ECG',                'chronic_special_category', '', '', 'N', 103, 1, sysdate(), null, null, '心电图');
INSERT INTO sys_dict_data VALUES(187, '000000', 5, '心脏超声',     'ECHO',               'chronic_special_category', '', '', 'N', 103, 1, sysdate(), null, null, '心脏超声');
INSERT INTO sys_dict_data VALUES(188, '000000', 6, 'CT',           'CT',                 'chronic_special_category', '', '', 'N', 103, 1, sysdate(), null, null, 'CT');

-- 124 chronic_warning_level 预警等级
INSERT INTO sys_dict_data VALUES(189, '000000', 1, '低',           'LOW',       'chronic_warning_level', '', '', 'N', 103, 1, sysdate(), null, null, '轻微偏离，关注即可');
INSERT INTO sys_dict_data VALUES(190, '000000', 2, '中',           'MEDIUM',    'chronic_warning_level', '', '', 'N', 103, 1, sysdate(), null, null, '中度偏离，需干预');
INSERT INTO sys_dict_data VALUES(191, '000000', 3, '高',           'HIGH',      'chronic_warning_level', '', '', 'N', 103, 1, sysdate(), null, null, '严重偏离，需立即干预');
INSERT INTO sys_dict_data VALUES(192, '000000', 4, '危急',         'CRITICAL',  'chronic_warning_level', '', '', 'N', 103, 1, sysdate(), null, null, '危急值，需紧急处理');

-- 125 chronic_warning_event_status 预警事件状态
INSERT INTO sys_dict_data VALUES(193, '000000', 1, '新建',         'NEW',        'chronic_warning_event_status', '', '', 'N', 103, 1, sysdate(), null, null, '系统自动生成');
INSERT INTO sys_dict_data VALUES(194, '000000', 2, '已确认',       'CONFIRMED',  'chronic_warning_event_status', '', '', 'N', 103, 1, sysdate(), null, null, '医生已确认');
INSERT INTO sys_dict_data VALUES(195, '000000', 3, '处理中',       'PROCESSING', 'chronic_warning_event_status', '', '', 'N', 103, 1, sysdate(), null, null, '正在处理');
INSERT INTO sys_dict_data VALUES(196, '000000', 4, '已升级',       'ESCALATED',  'chronic_warning_event_status', '', '', 'N', 103, 1, sysdate(), null, null, '已升级处理');
INSERT INTO sys_dict_data VALUES(197, '000000', 5, '已解决',       'RESOLVED',   'chronic_warning_event_status', '', '', 'N', 103, 1, sysdate(), null, null, '已解决');
INSERT INTO sys_dict_data VALUES(198, '000000', 6, '已归档',       'ARCHIVED',   'chronic_warning_event_status', '', '', 'N', 103, 1, sysdate(), null, null, '已归档');

-- 126 chronic_action_type 处置类型
INSERT INTO sys_dict_data VALUES(199, '000000', 1, '确认',         'CONFIRM',   'chronic_action_type', '', '', 'N', 103, 1, sysdate(), null, null, '确认预警有效');
INSERT INTO sys_dict_data VALUES(200, '000000', 2, '处理',         'HANDLE',    'chronic_action_type', '', '', 'N', 103, 1, sysdate(), null, null, '执行处理措施');
INSERT INTO sys_dict_data VALUES(201, '000000', 3, '升级',         'ESCALATE',  'chronic_action_type', '', '', 'N', 103, 1, sysdate(), null, null, '升级到上级处理');
INSERT INTO sys_dict_data VALUES(202, '000000', 4, '解决',         'RESOLVE',   'chronic_action_type', '', '', 'N', 103, 1, sysdate(), null, null, '标记为已解决');

-- 127 chronic_referral_status 转诊状态
INSERT INTO sys_dict_data VALUES(203, '000000', 1, '待审核',       'PENDING',    'chronic_referral_status', '', '', 'N', 103, 1, sysdate(), null, null, '提交申请待审核');
INSERT INTO sys_dict_data VALUES(204, '000000', 2, '已批准',       'APPROVED',   'chronic_referral_status', '', '', 'N', 103, 1, sysdate(), null, null, '转出方已批准');
INSERT INTO sys_dict_data VALUES(205, '000000', 3, '已接收',       'ACCEPTED',   'chronic_referral_status', '', '', 'N', 103, 1, sysdate(), null, null, '接收方已接收');
INSERT INTO sys_dict_data VALUES(206, '000000', 4, '已拒绝',       'REJECTED',   'chronic_referral_status', '', '', 'N', 103, 1, sysdate(), null, null, '申请被拒绝');
INSERT INTO sys_dict_data VALUES(207, '000000', 5, '已完成',       'COMPLETED',  'chronic_referral_status', '', '', 'N', 103, 1, sysdate(), null, null, '转诊流程完成');

-- 128 chronic_referral_type 转诊类型
INSERT INTO sys_dict_data VALUES(208, '000000', 1, '上转',         'UPWARD',     'chronic_referral_type', '', '', 'N', 103, 1, sysdate(), null, null, '下级转上级');
INSERT INTO sys_dict_data VALUES(209, '000000', 2, '下转',         'DOWNWARD',   'chronic_referral_type', '', '', 'N', 103, 1, sysdate(), null, null, '上级转下级');
INSERT INTO sys_dict_data VALUES(210, '000000', 3, '乡镇转诊',     'TOWNSHIP',   'chronic_referral_type', '', '', 'N', 103, 1, sysdate(), null, null, '乡镇卫生院转诊');

-- 129 chronic_encounter_type 就诊类型
INSERT INTO sys_dict_data VALUES(211, '000000', 1, '首诊',         'INITIAL',    'chronic_encounter_type', '', '', 'Y', 103, 1, sysdate(), null, null, '首次就诊');
INSERT INTO sys_dict_data VALUES(212, '000000', 2, '复诊',         'FOLLOWUP',   'chronic_encounter_type', '', '', 'N', 103, 1, sysdate(), null, null, '复诊随访');

-- 130 chronic_submit_status 提交状态
INSERT INTO sys_dict_data VALUES(213, '000000', 1, '草稿',         'DRAFT',      'chronic_submit_status', '', '', 'N', 103, 1, sysdate(), null, null, '暂存草稿');
INSERT INTO sys_dict_data VALUES(214, '000000', 2, '已提交',       'SUBMITTED',  'chronic_submit_status', '', '', 'Y', 103, 1, sysdate(), null, null, '已正式提交');

-- 131 chronic_diagnosis_type 诊断类型
INSERT INTO sys_dict_data VALUES(215, '000000', 1, '主诊断',       'PRIMARY',    'chronic_diagnosis_type', '', '', 'Y', 103, 1, sysdate(), null, null, '主要诊断');
INSERT INTO sys_dict_data VALUES(216, '000000', 2, '次诊断',       'SECONDARY',  'chronic_diagnosis_type', '', '', 'N', 103, 1, sysdate(), null, null, '次要/伴随诊断');

-- 132 chronic_tag_type 标签类型
INSERT INTO sys_dict_data VALUES(217, '000000', 1, '风险标签',     'RISK',         'chronic_tag_type', '', '', 'N', 103, 1, sysdate(), null, null, '风险标签');
INSERT INTO sys_dict_data VALUES(218, '000000', 2, '自定义标签',   'CUSTOM',       'chronic_tag_type', '', '', 'N', 103, 1, sysdate(), null, null, '自定义标签');
INSERT INTO sys_dict_data VALUES(219, '000000', 3, '合并症标签',   'COMORBIDITY',  'chronic_tag_type', '', '', 'N', 103, 1, sysdate(), null, null, '合并症标签');

-- 133 chronic_event_type 时间线事件类型
INSERT INTO sys_dict_data VALUES(220, '000000', 1, '建档',         'ARCHIVE',            'chronic_event_type', '', '', 'N', 103, 1, sysdate(), null, null, '建档事件');
INSERT INTO sys_dict_data VALUES(221, '000000', 2, '签约',         'SIGN',               'chronic_event_type', '', '', 'N', 103, 1, sysdate(), null, null, '签约事件');
INSERT INTO sys_dict_data VALUES(222, '000000', 3, '随访',         'FOLLOWUP',           'chronic_event_type', '', '', 'N', 103, 1, sysdate(), null, null, '随访事件');
INSERT INTO sys_dict_data VALUES(223, '000000', 4, '用药调整',     'MEDICATION_ADJUST',  'chronic_event_type', '', '', 'N', 103, 1, sysdate(), null, null, '用药调整事件');
INSERT INTO sys_dict_data VALUES(224, '000000', 5, '预警',         'WARNING',            'chronic_event_type', '', '', 'N', 103, 1, sysdate(), null, null, '预警事件');
INSERT INTO sys_dict_data VALUES(225, '000000', 6, '转诊',         'REFERRAL',           'chronic_event_type', '', '', 'N', 103, 1, sysdate(), null, null, '转诊事件');
INSERT INTO sys_dict_data VALUES(226, '000000', 7, '方案变更',     'PLAN_CHANGE',        'chronic_event_type', '', '', 'N', 103, 1, sysdate(), null, null, '方案变更事件');

-- 134 chronic_enroll_status 入组状态
INSERT INTO sys_dict_data VALUES(227, '000000', 1, '待入组',       'PENDING',    'chronic_enroll_status', '', '', 'N', 103, 1, sysdate(), null, null, '筛查通过待入组');
INSERT INTO sys_dict_data VALUES(228, '000000', 2, '已入组',       'ENROLLED',   'chronic_enroll_status', '', '', 'Y', 103, 1, sysdate(), null, null, '已入组管理');
INSERT INTO sys_dict_data VALUES(229, '000000', 3, '已拒绝',       'REJECTED',   'chronic_enroll_status', '', '', 'N', 103, 1, sysdate(), null, null, '拒绝入组');

-- 135 chronic_consent_type 同意类型
INSERT INTO sys_dict_data VALUES(230, '000000', 1, '签约同意',     'SIGN_CONTRACT',  'chronic_consent_type', '', '', 'N', 103, 1, sysdate(), null, null, '签约同意');
INSERT INTO sys_dict_data VALUES(231, '000000', 2, '数据共享同意', 'DATA_SHARE',     'chronic_consent_type', '', '', 'N', 103, 1, sysdate(), null, null, '数据共享同意');
INSERT INTO sys_dict_data VALUES(232, '000000', 3, '转诊同意',     'REFERRAL',       'chronic_consent_type', '', '', 'N', 103, 1, sysdate(), null, null, '转诊同意');

-- 136 chronic_session_type 会话类型
INSERT INTO sys_dict_data VALUES(233, '000000', 1, '医患对话',     'DOCTOR_PATIENT',  'chronic_session_type', '', '', 'Y', 103, 1, sysdate(), null, null, '医患对话');
INSERT INTO sys_dict_data VALUES(234, '000000', 2, '团队对话',     'TEAM_PATIENT',    'chronic_session_type', '', '', 'N', 103, 1, sysdate(), null, null, '团队对话');

-- 137 chronic_trigger_type 触发类型
INSERT INTO sys_dict_data VALUES(235, '000000', 1, '规则引擎',     'RULE_ENGINE',  'chronic_trigger_type', '', '', 'Y', 103, 1, sysdate(), null, null, '规则引擎触发');
INSERT INTO sys_dict_data VALUES(236, '000000', 2, '手动触发',     'MANUAL',       'chronic_trigger_type', '', '', 'N', 103, 1, sysdate(), null, null, '手动触发');
INSERT INTO sys_dict_data VALUES(237, '000000', 3, '天气触发',     'WEATHER',      'chronic_trigger_type', '', '', 'N', 103, 1, sysdate(), null, null, '天气触发');
INSERT INTO sys_dict_data VALUES(238, '000000', 4, '季节触发',     'SEASONAL',     'chronic_trigger_type', '', '', 'N', 103, 1, sysdate(), null, null, '季节触发');

-- 138 chronic_push_channel 推送渠道
INSERT INTO sys_dict_data VALUES(239, '000000', 1, '微信',         'WECHAT',  'chronic_push_channel', '', '', 'Y', 103, 1, sysdate(), null, null, '微信推送');
INSERT INTO sys_dict_data VALUES(240, '000000', 2, '短信',         'SMS',     'chronic_push_channel', '', '', 'N', 103, 1, sysdate(), null, null, '短信推送');
INSERT INTO sys_dict_data VALUES(241, '000000', 3, '语音',         'IVR',     'chronic_push_channel', '', '', 'N', 103, 1, sysdate(), null, null, '语音推送');
INSERT INTO sys_dict_data VALUES(242, '000000', 4, '纸质',         'PAPER',   'chronic_push_channel', '', '', 'N', 103, 1, sysdate(), null, null, '纸质推送');

-- 139 chronic_icd_version ICD版本
INSERT INTO sys_dict_data VALUES(243, '000000', 1, 'ICD-10',       'ICD10',  'chronic_icd_version', '', '', 'Y', 103, 1, sysdate(), null, null, 'ICD-10编码');
INSERT INTO sys_dict_data VALUES(244, '000000', 2, 'ICD-11',       'ICD11',  'chronic_icd_version', '', '', 'N', 103, 1, sysdate(), null, null, 'ICD-11编码');

-- 140 chronic_biz_type 附件业务类型
INSERT INTO sys_dict_data VALUES(245, '000000', 1, '报告PDF',      'REPORT_PDF',     'chronic_biz_type', '', '', 'N', 103, 1, sysdate(), null, null, '报告PDF');
INSERT INTO sys_dict_data VALUES(246, '000000', 2, '签名图片',     'SIGN_IMAGE',     'chronic_biz_type', '', '', 'N', 103, 1, sysdate(), null, null, '签名图片');
INSERT INTO sys_dict_data VALUES(247, '000000', 3, '眼底照片',     'FUNDUS_PHOTO',   'chronic_biz_type', '', '', 'N', 103, 1, sysdate(), null, null, '眼底照片');
INSERT INTO sys_dict_data VALUES(248, '000000', 4, '心电图',       'ECG',            'chronic_biz_type', '', '', 'N', 103, 1, sysdate(), null, null, '心电图');
INSERT INTO sys_dict_data VALUES(249, '000000', 5, '其他',         'OTHER',          'chronic_biz_type', '', '', 'N', 103, 1, sysdate(), null, null, '其他附件');

-- 141 chronic_kpi_category KPI分类
INSERT INTO sys_dict_data VALUES(250, '000000', 1, '管理率',       'MANAGEMENT_RATE',  'chronic_kpi_category', '', '', 'N', 103, 1, sysdate(), null, null, '管理率');
INSERT INTO sys_dict_data VALUES(251, '000000', 2, '依从率',       'COMPLIANCE_RATE',  'chronic_kpi_category', '', '', 'N', 103, 1, sysdate(), null, null, '依从率');
INSERT INTO sys_dict_data VALUES(252, '000000', 3, '控制率',       'CONTROL_RATE',     'chronic_kpi_category', '', '', 'N', 103, 1, sysdate(), null, null, '控制率');

-- 142 chronic_metric_type 指标类型
INSERT INTO sys_dict_data VALUES(253, '000000', 1,  '收缩压',      'BP_SYSTOLIC',      'chronic_metric_type', '', '', 'N', 103, 1, sysdate(), null, null, '收缩压');
INSERT INTO sys_dict_data VALUES(254, '000000', 2,  '舒张压',      'BP_DIASTOLIC',     'chronic_metric_type', '', '', 'N', 103, 1, sysdate(), null, null, '舒张压');
INSERT INTO sys_dict_data VALUES(255, '000000', 3,  '血糖',        'BLOOD_GLUCOSE',    'chronic_metric_type', '', '', 'N', 103, 1, sysdate(), null, null, '血糖');
INSERT INTO sys_dict_data VALUES(256, '000000', 4,  '心率',        'HEART_RATE',       'chronic_metric_type', '', '', 'N', 103, 1, sysdate(), null, null, '心率');
INSERT INTO sys_dict_data VALUES(257, '000000', 5,  '血氧',        'SPO2',             'chronic_metric_type', '', '', 'N', 103, 1, sysdate(), null, null, '血氧饱和度');
INSERT INTO sys_dict_data VALUES(258, '000000', 6,  '体温',        'TEMPERATURE',      'chronic_metric_type', '', '', 'N', 103, 1, sysdate(), null, null, '体温');
INSERT INTO sys_dict_data VALUES(259, '000000', 7,  '心电图',      'ECG',              'chronic_metric_type', '', '', 'N', 103, 1, sysdate(), null, null, '心电图');
INSERT INTO sys_dict_data VALUES(260, '000000', 8,  '体重',        'WEIGHT',           'chronic_metric_type', '', '', 'N', 103, 1, sysdate(), null, null, '体重');
INSERT INTO sys_dict_data VALUES(261, '000000', 9,  'BMI',         'BMI',              'chronic_metric_type', '', '', 'N', 103, 1, sysdate(), null, null, '身体质量指数');
INSERT INTO sys_dict_data VALUES(262, '000000', 10, '腰围',        'WAIST',            'chronic_metric_type', '', '', 'N', 103, 1, sysdate(), null, null, '腰围');
INSERT INTO sys_dict_data VALUES(263, '000000', 11, '血脂',        'LIPID',            'chronic_metric_type', '', '', 'N', 103, 1, sysdate(), null, null, '血脂');
INSERT INTO sys_dict_data VALUES(264, '000000', 12, '尿酸',        'URIC_ACID',        'chronic_metric_type', '', '', 'N', 103, 1, sysdate(), null, null, '尿酸');

-- 143 chronic_nation 民族
INSERT INTO sys_dict_data VALUES(265, '000000', 1,  '汉族',        'HAN',              'chronic_nation', '', '', 'Y', 103, 1, sysdate(), null, null, '汉族');
INSERT INTO sys_dict_data VALUES(266, '000000', 2,  '蒙古族',      'MONGOL',           'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '蒙古族');
INSERT INTO sys_dict_data VALUES(267, '000000', 3,  '回族',        'HUI',              'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '回族');
INSERT INTO sys_dict_data VALUES(268, '000000', 4,  '藏族',        'TIBETAN',          'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '藏族');
INSERT INTO sys_dict_data VALUES(269, '000000', 5,  '维吾尔族',    'UYGUR',            'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '维吾尔族');
INSERT INTO sys_dict_data VALUES(270, '000000', 6,  '苗族',        'MIAO',             'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '苗族');
INSERT INTO sys_dict_data VALUES(271, '000000', 7,  '彝族',        'YI',               'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '彝族');
INSERT INTO sys_dict_data VALUES(272, '000000', 8,  '壮族',        'ZHUANG',           'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '壮族');
INSERT INTO sys_dict_data VALUES(273, '000000', 9,  '布依族',      'BUYEI',            'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '布依族');
INSERT INTO sys_dict_data VALUES(274, '000000', 10, '朝鲜族',      'KOREAN',           'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '朝鲜族');
INSERT INTO sys_dict_data VALUES(275, '000000', 11, '满族',        'MANCHU',           'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '满族');
INSERT INTO sys_dict_data VALUES(276, '000000', 12, '侗族',        'DONG',             'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '侗族');
INSERT INTO sys_dict_data VALUES(277, '000000', 13, '瑶族',        'YAO',              'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '瑶族');
INSERT INTO sys_dict_data VALUES(278, '000000', 14, '白族',        'BAI',              'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '白族');
INSERT INTO sys_dict_data VALUES(279, '000000', 15, '土家族',      'TUJIA',            'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '土家族');
INSERT INTO sys_dict_data VALUES(280, '000000', 16, '哈尼族',      'HANI',             'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '哈尼族');
INSERT INTO sys_dict_data VALUES(281, '000000', 17, '哈萨克族',    'KAZAK',            'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '哈萨克族');
INSERT INTO sys_dict_data VALUES(282, '000000', 18, '傣族',        'DAI',              'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '傣族');
INSERT INTO sys_dict_data VALUES(283, '000000', 19, '黎族',        'LI',               'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '黎族');
INSERT INTO sys_dict_data VALUES(284, '000000', 20, '傈僳族',      'LISU',             'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '傈僳族');
INSERT INTO sys_dict_data VALUES(285, '000000', 21, '佤族',        'WA',               'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '佤族');
INSERT INTO sys_dict_data VALUES(286, '000000', 22, '畲族',        'SHE',              'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '畲族');
INSERT INTO sys_dict_data VALUES(287, '000000', 23, '高山族',      'GAOSHAN',          'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '高山族');
INSERT INTO sys_dict_data VALUES(288, '000000', 24, '拉祜族',      'LAHU',             'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '拉祜族');
INSERT INTO sys_dict_data VALUES(289, '000000', 25, '水族',        'SHUI',             'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '水族');
INSERT INTO sys_dict_data VALUES(290, '000000', 26, '东乡族',      'DONGXIANG',        'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '东乡族');
INSERT INTO sys_dict_data VALUES(291, '000000', 27, '纳西族',      'NAXI',             'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '纳西族');
INSERT INTO sys_dict_data VALUES(292, '000000', 28, '景颇族',      'JINGPO',           'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '景颇族');
INSERT INTO sys_dict_data VALUES(293, '000000', 29, '柯尔克孜族',  'KIRGIZ',           'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '柯尔克孜族');
INSERT INTO sys_dict_data VALUES(294, '000000', 30, '土族',        'TU',               'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '土族');
INSERT INTO sys_dict_data VALUES(295, '000000', 31, '达斡尔族',    'DAUR',             'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '达斡尔族');
INSERT INTO sys_dict_data VALUES(296, '000000', 32, '仫佬族',      'MULAO',            'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '仫佬族');
INSERT INTO sys_dict_data VALUES(297, '000000', 33, '羌族',        'QIANG',            'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '羌族');
INSERT INTO sys_dict_data VALUES(298, '000000', 34, '布朗族',      'BULANG',           'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '布朗族');
INSERT INTO sys_dict_data VALUES(299, '000000', 35, '撒拉族',      'SALAR',            'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '撒拉族');
INSERT INTO sys_dict_data VALUES(300, '000000', 36, '毛南族',      'MAONAN',           'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '毛南族');
INSERT INTO sys_dict_data VALUES(301, '000000', 37, '仡佬族',      'GELO',             'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '仡佬族');
INSERT INTO sys_dict_data VALUES(302, '000000', 38, '锡伯族',      'XIBE',             'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '锡伯族');
INSERT INTO sys_dict_data VALUES(303, '000000', 39, '阿昌族',      'ACHANG',           'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '阿昌族');
INSERT INTO sys_dict_data VALUES(304, '000000', 40, '普米族',      'PUMI',             'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '普米族');
INSERT INTO sys_dict_data VALUES(305, '000000', 41, '塔吉克族',    'TAJIK',            'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '塔吉克族');
INSERT INTO sys_dict_data VALUES(306, '000000', 42, '怒族',        'NU',               'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '怒族');
INSERT INTO sys_dict_data VALUES(307, '000000', 43, '乌孜别克族',  'UZBEK',            'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '乌孜别克族');
INSERT INTO sys_dict_data VALUES(308, '000000', 44, '俄罗斯族',    'RUSSIAN',          'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '俄罗斯族');
INSERT INTO sys_dict_data VALUES(309, '000000', 45, '鄂温克族',    'EWENKI',           'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '鄂温克族');
INSERT INTO sys_dict_data VALUES(310, '000000', 46, '德昂族',      'DEANG',            'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '德昂族');
INSERT INTO sys_dict_data VALUES(311, '000000', 47, '保安族',      'BONAN',            'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '保安族');
INSERT INTO sys_dict_data VALUES(312, '000000', 48, '裕固族',      'YUGUR',            'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '裕固族');
INSERT INTO sys_dict_data VALUES(313, '000000', 49, '京族',        'GIN',              'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '京族');
INSERT INTO sys_dict_data VALUES(314, '000000', 50, '塔塔尔族',    'TATAR',            'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '塔塔尔族');
INSERT INTO sys_dict_data VALUES(315, '000000', 51, '独龙族',      'DERUNG',           'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '独龙族');
INSERT INTO sys_dict_data VALUES(316, '000000', 52, '鄂伦春族',    'OROQEN',           'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '鄂伦春族');
INSERT INTO sys_dict_data VALUES(317, '000000', 53, '赫哲族',      'HEZHE',            'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '赫哲族');
INSERT INTO sys_dict_data VALUES(318, '000000', 54, '门巴族',      'MONBA',            'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '门巴族');
INSERT INTO sys_dict_data VALUES(319, '000000', 55, '珞巴族',      'LHOBA',            'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '珞巴族');
INSERT INTO sys_dict_data VALUES(320, '000000', 56, '基诺族',      'JINO',             'chronic_nation', '', '', 'N', 103, 1, sysdate(), null, null, '基诺族');

-- 144 chronic_occupation 职业
INSERT INTO sys_dict_data VALUES(321, '000000', 1,  '国家机关/党群组织/企业/事业单位负责人', 'GOVERNMENT', 'chronic_occupation', '', '', 'N', 103, 1, sysdate(), null, null, '负责人');
INSERT INTO sys_dict_data VALUES(322, '000000', 2,  '专业技术人员', 'TECHNICIAN',       'chronic_occupation', '', '', 'N', 103, 1, sysdate(), null, null, '专业技术人员');
INSERT INTO sys_dict_data VALUES(323, '000000', 3,  '办事人员',     'CLERK',            'chronic_occupation', '', '', 'N', 103, 1, sysdate(), null, null, '办事人员');
INSERT INTO sys_dict_data VALUES(324, '000000', 4,  '商业/服务业人员', 'BUSINESS',         'chronic_occupation', '', '', 'N', 103, 1, sysdate(), null, null, '商业服务业');
INSERT INTO sys_dict_data VALUES(325, '000000', 5,  '农/林/牧/渔/水利业生产人员', 'FARMER', 'chronic_occupation', '', '', 'N', 103, 1, sysdate(), null, null, '农林牧渔');
INSERT INTO sys_dict_data VALUES(326, '000000', 6,  '生产/运输设备操作人员', 'WORKER',     'chronic_occupation', '', '', 'N', 103, 1, sysdate(), null, null, '生产运输工人');
INSERT INTO sys_dict_data VALUES(327, '000000', 7,  '军人',         'SOLDIER',          'chronic_occupation', '', '', 'N', 103, 1, sysdate(), null, null, '军人');
INSERT INTO sys_dict_data VALUES(328, '000000', 8,  '学生',         'STUDENT',          'chronic_occupation', '', '', 'N', 103, 1, sysdate(), null, null, '学生');
INSERT INTO sys_dict_data VALUES(329, '000000', 9,  '离退休人员',   'RETIRED',          'chronic_occupation', '', '', 'N', 103, 1, sysdate(), null, null, '离退休人员');
INSERT INTO sys_dict_data VALUES(330, '000000', 10, '失业/无业/其他', 'UNEMPLOYED',       'chronic_occupation', '', '', 'N', 103, 1, sysdate(), null, null, '失业无业其他');

-- 145 chronic_education_level 文化程度
INSERT INTO sys_dict_data VALUES(331, '000000', 1,  '研究生',       'POSTGRADUATE',     'chronic_education_level', '', '', 'N', 103, 1, sysdate(), null, null, '研究生及以上');
INSERT INTO sys_dict_data VALUES(332, '000000', 2,  '大学本科',     'UNDERGRADUATE',    'chronic_education_level', '', '', 'N', 103, 1, sysdate(), null, null, '大学本科');
INSERT INTO sys_dict_data VALUES(333, '000000', 3,  '大学专科/高职', 'COLLEGE',          'chronic_education_level', '', '', 'N', 103, 1, sysdate(), null, null, '大学专科或高职');
INSERT INTO sys_dict_data VALUES(334, '000000', 4,  '中等职业教育', 'VOCATIONAL',       'chronic_education_level', '', '', 'N', 103, 1, sysdate(), null, null, '中专/技校/职高');
INSERT INTO sys_dict_data VALUES(335, '000000', 5,  '普通高中',     'HIGH_SCHOOL',      'chronic_education_level', '', '', 'N', 103, 1, sysdate(), null, null, '普通高中');
INSERT INTO sys_dict_data VALUES(336, '000000', 6,  '初中',         'JUNIOR_HIGH',      'chronic_education_level', '', '', 'N', 103, 1, sysdate(), null, null, '初中');
INSERT INTO sys_dict_data VALUES(337, '000000', 7,  '小学',         'PRIMARY',          'chronic_education_level', '', '', 'N', 103, 1, sysdate(), null, null, '小学');
INSERT INTO sys_dict_data VALUES(338, '000000', 8,  '文盲或半文盲', 'ILLITERATE',       'chronic_education_level', '', '', 'N', 103, 1, sysdate(), null, null, '文盲半文盲');

-- 146 chronic_disability_type 残疾类型
INSERT INTO sys_dict_data VALUES(339, '000000', 1,  '视力残疾',     'VISION',           'chronic_disability_type', '', '', 'N', 103, 1, sysdate(), null, null, '视力残疾');
INSERT INTO sys_dict_data VALUES(340, '000000', 2,  '听力残疾',     'HEARING',          'chronic_disability_type', '', '', 'N', 103, 1, sysdate(), null, null, '听力残疾');
INSERT INTO sys_dict_data VALUES(341, '000000', 3,  '言语残疾',     'SPEECH',           'chronic_disability_type', '', '', 'N', 103, 1, sysdate(), null, null, '言语残疾');
INSERT INTO sys_dict_data VALUES(342, '000000', 4,  '肢体残疾',     'PHYSICAL',         'chronic_disability_type', '', '', 'N', 103, 1, sysdate(), null, null, '肢体残疾');
INSERT INTO sys_dict_data VALUES(343, '000000', 5,  '智力残疾',     'INTELLECTUAL',     'chronic_disability_type', '', '', 'N', 103, 1, sysdate(), null, null, '智力残疾');
INSERT INTO sys_dict_data VALUES(344, '000000', 6,  '精神残疾',     'MENTAL',           'chronic_disability_type', '', '', 'N', 103, 1, sysdate(), null, null, '精神残疾');
INSERT INTO sys_dict_data VALUES(345, '000000', 7,  '多重残疾',     'MULTIPLE',         'chronic_disability_type', '', '', 'N', 103, 1, sysdate(), null, null, '多重残疾');

-- 147 chronic_disability_level 残疾等级
INSERT INTO sys_dict_data VALUES(346, '000000', 1,  '一级（极重度）', 'LEVEL_1',          'chronic_disability_level', '', '', 'N', 103, 1, sysdate(), null, null, '极重度');
INSERT INTO sys_dict_data VALUES(347, '000000', 2,  '二级（重度）',   'LEVEL_2',          'chronic_disability_level', '', '', 'N', 103, 1, sysdate(), null, null, '重度');
INSERT INTO sys_dict_data VALUES(348, '000000', 3,  '三级（中度）',   'LEVEL_3',          'chronic_disability_level', '', '', 'N', 103, 1, sysdate(), null, null, '中度');
INSERT INTO sys_dict_data VALUES(349, '000000', 4,  '四级（轻度）',   'LEVEL_4',          'chronic_disability_level', '', '', 'N', 103, 1, sysdate(), null, null, '轻度');

-- 148 chronic_relation_type 病种关联类型
INSERT INTO sys_dict_type VALUES(148, '000000', '病种关联类型', 'chronic_relation_type', 103, 1, sysdate(), null, null, '慢病病种关联类型列表');

INSERT INTO sys_dict_data VALUES(350, '000000', 1, '并发症', 'COMPLICATION', 'chronic_relation_type', '', '', 'N', 103, 1, sysdate(), null, null, '并发症');
INSERT INTO sys_dict_data VALUES(351, '000000', 2, '合并症', 'COMORBIDITY', 'chronic_relation_type', '', '', 'N', 103, 1, sysdate(), null, null, '合并症');

-- 149 chronic_blood_type 血型
INSERT INTO sys_dict_type VALUES(149, '000000', '血型', 'chronic_blood_type', 103, 1, sysdate(), null, null, '慢病患者血型字典');

INSERT INTO sys_dict_data VALUES(352, '000000', 1, 'A型',          'A',            'chronic_blood_type', '', '', 'N', 103, 1, sysdate(), null, null, 'A型血');
INSERT INTO sys_dict_data VALUES(353, '000000', 2, 'B型',          'B',            'chronic_blood_type', '', '', 'N', 103, 1, sysdate(), null, null, 'B型血');
INSERT INTO sys_dict_data VALUES(354, '000000', 3, 'AB型',         'AB',           'chronic_blood_type', '', '', 'N', 103, 1, sysdate(), null, null, 'AB型血');
INSERT INTO sys_dict_data VALUES(355, '000000', 4, 'O型',          'O',            'chronic_blood_type', '', '', 'N', 103, 1, sysdate(), null, null, 'O型血');
INSERT INTO sys_dict_data VALUES(356, '000000', 5, 'Rh阴性',      'RH_NEGATIVE',  'chronic_blood_type', '', '', 'N', 103, 1, sysdate(), null, null, 'Rh阴性（熊猫血）');
INSERT INTO sys_dict_data VALUES(357, '000000', 6, '不详',         'UNKNOWN',      'chronic_blood_type', '', '', 'N', 103, 1, sysdate(), null, null, '血型不详');

-- 150 chronic_marital_status 婚姻状况
INSERT INTO sys_dict_type VALUES(150, '000000', '婚姻状况', 'chronic_marital_status', 103, 1, sysdate(), null, null, '慢病患者婚姻状况字典');

INSERT INTO sys_dict_data VALUES(358, '000000', 1, '未婚',         'UNMARRIED',    'chronic_marital_status', '', '', 'N', 103, 1, sysdate(), null, null, '未婚');
INSERT INTO sys_dict_data VALUES(359, '000000', 2, '已婚',         'MARRIED',      'chronic_marital_status', '', '', 'Y', 103, 1, sysdate(), null, null, '已婚');
INSERT INTO sys_dict_data VALUES(360, '000000', 3, '离婚',         'DIVORCED',     'chronic_marital_status', '', '', 'N', 103, 1, sysdate(), null, null, '离婚');
INSERT INTO sys_dict_data VALUES(361, '000000', 4, '丧偶',         'WIDOWED',      'chronic_marital_status', '', '', 'N', 103, 1, sysdate(), null, null, '丧偶');
INSERT INTO sys_dict_data VALUES(362, '000000', 5, '其他',         'OTHER',        'chronic_marital_status', '', '', 'N', 103, 1, sysdate(), null, null, '其他婚姻状况');

-- =============================================
-- 以下为代码常量引用但前面未定义的补充字典
-- =============================================

-- 151 chronic_adjust_type 用药调整类型（与 chronic_medication_adjust_type 值域相同，VO 翻译用）
INSERT INTO sys_dict_type VALUES(151, '000000', '用药调整类型', 'chronic_adjust_type', 103, 1, sysdate(), null, null, '用药调整类型(VO翻译)');
INSERT INTO sys_dict_data VALUES(363, '000000', 1, '加药',   'ADD',         'chronic_adjust_type', '', '', 'N', 103, 1, sysdate(), null, null, '新增药品');
INSERT INTO sys_dict_data VALUES(364, '000000', 2, '减药',   'REDUCE',      'chronic_adjust_type', '', '', 'N', 103, 1, sysdate(), null, null, '减少药品');
INSERT INTO sys_dict_data VALUES(365, '000000', 3, '换药',   'SWITCH',      'chronic_adjust_type', '', '', 'N', 103, 1, sysdate(), null, null, '替换药品');
INSERT INTO sys_dict_data VALUES(366, '000000', 4, '调量',   'DOSE_CHANGE', 'chronic_adjust_type', '', '', 'N', 103, 1, sysdate(), null, null, '调整剂量');

-- 152 chronic_approval_status 审批状态
INSERT INTO sys_dict_type VALUES(152, '000000', '审批状态', 'chronic_approval_status', 103, 1, sysdate(), null, null, '审批状态');
INSERT INTO sys_dict_data VALUES(367, '000000', 1, '待审批', 'PENDING',  'chronic_approval_status', '', '', 'N', 103, 1, sysdate(), null, null, '待审批');
INSERT INTO sys_dict_data VALUES(368, '000000', 2, '已批准', 'APPROVED', 'chronic_approval_status', '', '', 'N', 103, 1, sysdate(), null, null, '已批准');
INSERT INTO sys_dict_data VALUES(369, '000000', 3, '已拒绝', 'REJECTED', 'chronic_approval_status', '', '', 'N', 103, 1, sysdate(), null, null, '已拒绝');

-- 153 chronic_content_type 消息内容类型
INSERT INTO sys_dict_type VALUES(153, '000000', '消息内容类型', 'chronic_content_type', 103, 1, sysdate(), null, null, '消息内容类型');
INSERT INTO sys_dict_data VALUES(370, '000000', 1, '文本',   'TEXT',  'chronic_content_type', '', '', 'Y', 103, 1, sysdate(), null, null, '文本');
INSERT INTO sys_dict_data VALUES(371, '000000', 2, '图片',   'IMAGE', 'chronic_content_type', '', '', 'N', 103, 1, sysdate(), null, null, '图片');
INSERT INTO sys_dict_data VALUES(372, '000000', 3, '语音',   'VOICE', 'chronic_content_type', '', '', 'N', 103, 1, sysdate(), null, null, '语音');

-- 154 chronic_delivery_status 推送投递状态
INSERT INTO sys_dict_type VALUES(154, '000000', '投递状态', 'chronic_delivery_status', 103, 1, sysdate(), null, null, '推送投递状态');
INSERT INTO sys_dict_data VALUES(373, '000000', 1, '待发送', 'PENDING',   'chronic_delivery_status', '', '', 'N', 103, 1, sysdate(), null, null, '待发送');
INSERT INTO sys_dict_data VALUES(374, '000000', 2, '已发送', 'SENT',      'chronic_delivery_status', '', '', 'Y', 103, 1, sysdate(), null, null, '已发送');
INSERT INTO sys_dict_data VALUES(375, '000000', 3, '发送失败','FAILED',   'chronic_delivery_status', '', '', 'N', 103, 1, sysdate(), null, null, '发送失败');

-- 155 chronic_device_type 设备类型
INSERT INTO sys_dict_type VALUES(155, '000000', '设备类型', 'chronic_device_type', 103, 1, sysdate(), null, null, 'IoT设备类型');
INSERT INTO sys_dict_data VALUES(376, '000000', 1, '血压计',       'BP_MONITOR',   'chronic_device_type', '', '', 'N', 103, 1, sysdate(), null, null, '血压计');
INSERT INTO sys_dict_data VALUES(377, '000000', 2, '血糖仪',       'GLUCOMETER',   'chronic_device_type', '', '', 'N', 103, 1, sysdate(), null, null, '血糖仪');
INSERT INTO sys_dict_data VALUES(378, '000000', 3, '心电穿戴',     'ECG_WEARABLE', 'chronic_device_type', '', '', 'N', 103, 1, sysdate(), null, null, '心电穿戴设备');
INSERT INTO sys_dict_data VALUES(379, '000000', 4, '动态血糖监测', 'CGM',          'chronic_device_type', '', '', 'N', 103, 1, sysdate(), null, null, '动态血糖监测');

-- 156 chronic_disease_category 疾病分类
INSERT INTO sys_dict_type VALUES(156, '000000', '疾病分类', 'chronic_disease_category', 103, 1, sysdate(), null, null, '疾病分类');
INSERT INTO sys_dict_data VALUES(380, '000000', 1, '主病种',   'PRIMARY',      'chronic_disease_category', '', '', 'Y', 103, 1, sysdate(), null, null, '主病种');
INSERT INTO sys_dict_data VALUES(381, '000000', 2, '并发症',   'COMPLICATION', 'chronic_disease_category', '', '', 'N', 103, 1, sysdate(), null, null, '并发症');

-- 157 chronic_exercise_freq 运动频率
INSERT INTO sys_dict_type VALUES(157, '000000', '运动频率', 'chronic_exercise_freq', 103, 1, sysdate(), null, null, '运动频率');
INSERT INTO sys_dict_data VALUES(382, '000000', 1, '几乎不运动', 'RARELY',        'chronic_exercise_freq', '', '', 'N', 103, 1, sysdate(), null, null, '几乎不运动');
INSERT INTO sys_dict_data VALUES(383, '000000', 2, '每周1-2次', '1_2_TIMES_WEEK', 'chronic_exercise_freq', '', '', 'N', 103, 1, sysdate(), null, null, '每周1-2次');
INSERT INTO sys_dict_data VALUES(384, '000000', 3, '每周3-4次', '3_TIMES_WEEK',   'chronic_exercise_freq', '', '', 'Y', 103, 1, sysdate(), null, null, '每周3-4次');
INSERT INTO sys_dict_data VALUES(385, '000000', 4, '每周5次以上','5_TIMES_WEEK',  'chronic_exercise_freq', '', '', 'N', 103, 1, sysdate(), null, null, '每周5次以上');

-- 158 chronic_followup_item_type 随访计划项类型
INSERT INTO sys_dict_type VALUES(158, '000000', '随访计划项类型', 'chronic_followup_item_type', 103, 1, sysdate(), null, null, '随访计划项类型');
INSERT INTO sys_dict_data VALUES(386, '000000', 1, '常规随访', 'ROUTINE', 'chronic_followup_item_type', '', '', 'Y', 103, 1, sysdate(), null, null, '常规随访');
INSERT INTO sys_dict_data VALUES(387, '000000', 2, '紧急随访', 'URGENT',  'chronic_followup_item_type', '', '', 'N', 103, 1, sysdate(), null, null, '紧急随访');

-- 159 chronic_followup_plan_status 随访计划状态
INSERT INTO sys_dict_type VALUES(159, '000000', '随访计划状态', 'chronic_followup_plan_status', 103, 1, sysdate(), null, null, '随访计划状态');
INSERT INTO sys_dict_data VALUES(388, '000000', 1, '生效中', 'ACTIVE',   'chronic_followup_plan_status', '', '', 'Y', 103, 1, sysdate(), null, null, '生效中');
INSERT INTO sys_dict_data VALUES(389, '000000', 2, '已停用', 'DISABLED', 'chronic_followup_plan_status', '', '', 'N', 103, 1, sysdate(), null, null, '已停用');

-- 160 chronic_frequency 用药频次
INSERT INTO sys_dict_type VALUES(160, '000000', '用药频次', 'chronic_frequency', 103, 1, sysdate(), null, null, '用药频次');
INSERT INTO sys_dict_data VALUES(390, '000000', 1, '每日1次', 'QD',  'chronic_frequency', '', '', 'Y', 103, 1, sysdate(), null, null, '每日1次');
INSERT INTO sys_dict_data VALUES(391, '000000', 2, '每日2次', 'BID', 'chronic_frequency', '', '', 'N', 103, 1, sysdate(), null, null, '每日2次');
INSERT INTO sys_dict_data VALUES(392, '000000', 3, '每日3次', 'TID', 'chronic_frequency', '', '', 'N', 103, 1, sysdate(), null, null, '每日3次');
INSERT INTO sys_dict_data VALUES(393, '000000', 4, '每晚1次', 'QN',  'chronic_frequency', '', '', 'N', 103, 1, sysdate(), null, null, '每晚1次');

-- 161 chronic_manage_level 管理等级
INSERT INTO sys_dict_type VALUES(161, '000000', '管理等级', 'chronic_manage_level', 103, 1, sysdate(), null, null, '慢病管理等级');
INSERT INTO sys_dict_data VALUES(394, '000000', 1, '低风险',   'LOW',       'chronic_manage_level', '', '', 'N', 103, 1, sysdate(), null, null, '低风险管理');
INSERT INTO sys_dict_data VALUES(395, '000000', 2, '中风险',   'MEDIUM',    'chronic_manage_level', '', '', 'N', 103, 1, sysdate(), null, null, '中风险管理');
INSERT INTO sys_dict_data VALUES(396, '000000', 3, '高风险',   'HIGH',      'chronic_manage_level', '', '', 'N', 103, 1, sysdate(), null, null, '高风险管理');
INSERT INTO sys_dict_data VALUES(397, '000000', 4, '极高风险', 'VERY_HIGH', 'chronic_manage_level', '', '', 'N', 103, 1, sysdate(), null, null, '极高风险管理');

-- 162 chronic_measure_period 测量时段
INSERT INTO sys_dict_type VALUES(162, '000000', '测量时段', 'chronic_measure_period', 103, 1, sysdate(), null, null, '测量时段');
INSERT INTO sys_dict_data VALUES(398, '000000', 1, '晨起',   'MORNING', 'chronic_measure_period', '', '', 'Y', 103, 1, sysdate(), null, null, '晨起');
INSERT INTO sys_dict_data VALUES(399, '000000', 2, '午间',   'NOON',    'chronic_measure_period', '', '', 'N', 103, 1, sysdate(), null, null, '午间');
INSERT INTO sys_dict_data VALUES(400, '000000', 3, '晚间',   'EVENING', 'chronic_measure_period', '', '', 'N', 103, 1, sysdate(), null, null, '晚间');
INSERT INTO sys_dict_data VALUES(401, '000000', 4, '空腹',   'FASTING', 'chronic_measure_period', '', '', 'N', 103, 1, sysdate(), null, null, '空腹');
INSERT INTO sys_dict_data VALUES(402, '000000', 5, '随机',   'RANDOM',  'chronic_measure_period', '', '', 'N', 103, 1, sysdate(), null, null, '随机');

-- 163 chronic_measure_posture 测量体位
INSERT INTO sys_dict_type VALUES(163, '000000', '测量体位', 'chronic_measure_posture', 103, 1, sysdate(), null, null, '血压测量体位');
INSERT INTO sys_dict_data VALUES(403, '000000', 1, '坐位',   'SITTING',  'chronic_measure_posture', '', '', 'Y', 103, 1, sysdate(), null, null, '坐位');
INSERT INTO sys_dict_data VALUES(404, '000000', 2, '卧位',   'LYING',    'chronic_measure_posture', '', '', 'N', 103, 1, sysdate(), null, null, '卧位');
INSERT INTO sys_dict_data VALUES(405, '000000', 3, '立位',   'STANDING', 'chronic_measure_posture', '', '', 'N', 103, 1, sysdate(), null, null, '立位');

-- 164 chronic_measure_scene 测量场景
INSERT INTO sys_dict_type VALUES(164, '000000', '测量场景', 'chronic_measure_scene', 103, 1, sysdate(), null, null, '测量场景');
INSERT INTO sys_dict_data VALUES(406, '000000', 1, '家庭',   'HOME',     'chronic_measure_scene', '', '', 'Y', 103, 1, sysdate(), null, null, '家庭自测');
INSERT INTO sys_dict_data VALUES(407, '000000', 2, '医院',   'HOSPITAL', 'chronic_measure_scene', '', '', 'N', 103, 1, sysdate(), null, null, '医院诊室');
INSERT INTO sys_dict_data VALUES(408, '000000', 3, '社区',   'COMMUNITY','chronic_measure_scene', '', '', 'N', 103, 1, sysdate(), null, null, '社区卫生站');

-- 165 chronic_online_status 设备在线状态
INSERT INTO sys_dict_type VALUES(165, '000000', '在线状态', 'chronic_online_status', 103, 1, sysdate(), null, null, '设备在线状态');
INSERT INTO sys_dict_data VALUES(409, '000000', 1, '在线',   '1', 'chronic_online_status', '', '', 'Y', 103, 1, sysdate(), null, null, '在线');
INSERT INTO sys_dict_data VALUES(410, '000000', 2, '离线',   '0', 'chronic_online_status', '', '', 'N', 103, 1, sysdate(), null, null, '离线');

-- 166 chronic_psychological_status 心理状态
INSERT INTO sys_dict_type VALUES(166, '000000', '心理状态', 'chronic_psychological_status', 103, 1, sysdate(), null, null, '心理状态');
INSERT INTO sys_dict_data VALUES(411, '000000', 1, '正常',   'NORMAL',     'chronic_psychological_status', '', '', 'Y', 103, 1, sysdate(), null, null, '正常');
INSERT INTO sys_dict_data VALUES(412, '000000', 2, '焦虑',   'ANXIOUS',    'chronic_psychological_status', '', '', 'N', 103, 1, sysdate(), null, null, '焦虑');
INSERT INTO sys_dict_data VALUES(413, '000000', 3, '抑郁',   'DEPRESSED',  'chronic_psychological_status', '', '', 'N', 103, 1, sysdate(), null, null, '抑郁');

-- 167 chronic_push_status 报告推送状态
INSERT INTO sys_dict_type VALUES(167, '000000', '推送状态', 'chronic_push_status', 103, 1, sysdate(), null, null, '报告推送状态');
INSERT INTO sys_dict_data VALUES(414, '000000', 1, '未推送', 'UNPUSHED', 'chronic_push_status', '', '', 'N', 103, 1, sysdate(), null, null, '未推送');
INSERT INTO sys_dict_data VALUES(415, '000000', 2, '已推送', 'PUSHED',   'chronic_push_status', '', '', 'Y', 103, 1, sysdate(), null, null, '已推送');

-- 168 chronic_referral_category 转诊类别
INSERT INTO sys_dict_type VALUES(168, '000000', '转诊类别', 'chronic_referral_category', 103, 1, sysdate(), null, null, '转诊类别');
INSERT INTO sys_dict_data VALUES(416, '000000', 1, '急诊转诊', 'EMERGENCY', 'chronic_referral_category', '', '', 'N', 103, 1, sysdate(), null, null, '急诊转诊');
INSERT INTO sys_dict_data VALUES(417, '000000', 2, '病情稳定', 'STABLE',    'chronic_referral_category', '', '', 'N', 103, 1, sysdate(), null, null, '病情稳定转诊');
INSERT INTO sys_dict_data VALUES(418, '000000', 3, '常规转诊', 'ROUTINE',   'chronic_referral_category', '', '', 'Y', 103, 1, sysdate(), null, null, '常规转诊');

-- 169 chronic_report_type 报告类型
INSERT INTO sys_dict_type VALUES(169, '000000', '报告类型', 'chronic_report_type', 103, 1, sysdate(), null, null, '报告模板类型');
INSERT INTO sys_dict_data VALUES(419, '000000', 1, '年度体检',   'ANNUAL_CHECKUP',   'chronic_report_type', '', '', 'N', 103, 1, sysdate(), null, null, '年度体检报告');
INSERT INTO sys_dict_data VALUES(420, '000000', 2, '季度管理',   'QUARTERLY_MANAGE', 'chronic_report_type', '', '', 'N', 103, 1, sysdate(), null, null, '季度管理报告');
INSERT INTO sys_dict_data VALUES(421, '000000', 3, '区域统计',   'AREA_STAT',        'chronic_report_type', '', '', 'N', 103, 1, sysdate(), null, null, '区域统计报告');

-- 170 chronic_route 给药途径
INSERT INTO sys_dict_type VALUES(170, '000000', '给药途径', 'chronic_route', 103, 1, sysdate(), null, null, '给药途径');
INSERT INTO sys_dict_data VALUES(422, '000000', 1, '口服', 'ORAL', 'chronic_route', '', '', 'Y', 103, 1, sysdate(), null, null, '口服');
INSERT INTO sys_dict_data VALUES(423, '000000', 2, '皮下', 'SC',   'chronic_route', '', '', 'N', 103, 1, sysdate(), null, null, '皮下注射');
INSERT INTO sys_dict_data VALUES(424, '000000', 3, '静脉', 'IV',   'chronic_route', '', '', 'N', 103, 1, sysdate(), null, null, '静脉注射');
INSERT INTO sys_dict_data VALUES(425, '000000', 4, '肌注', 'IM',   'chronic_route', '', '', 'N', 103, 1, sysdate(), null, null, '肌肉注射');

-- 171 chronic_screening_status 筛查批次状态
INSERT INTO sys_dict_type VALUES(171, '000000', '筛查批次状态', 'chronic_screening_status', 103, 1, sysdate(), null, null, '筛查批次状态');
INSERT INTO sys_dict_data VALUES(426, '000000', 1, '进行中', 'ACTIVE',    'chronic_screening_status', '', '', 'Y', 103, 1, sysdate(), null, null, '进行中');
INSERT INTO sys_dict_data VALUES(427, '000000', 2, '已完成', 'COMPLETED', 'chronic_screening_status', '', '', 'N', 103, 1, sysdate(), null, null, '已完成');

-- 172 chronic_sender_type 发送者类型
INSERT INTO sys_dict_type VALUES(172, '000000', '发送者类型', 'chronic_sender_type', 103, 1, sysdate(), null, null, '消息发送者类型');
INSERT INTO sys_dict_data VALUES(428, '000000', 1, '医生',   'DOCTOR',  'chronic_sender_type', '', '', 'N', 103, 1, sysdate(), null, null, '医生');
INSERT INTO sys_dict_data VALUES(429, '000000', 2, '患者',   'PATIENT', 'chronic_sender_type', '', '', 'N', 103, 1, sysdate(), null, null, '患者');

-- 173 chronic_source_type 来源类型
INSERT INTO sys_dict_type VALUES(173, '000000', '来源类型', 'chronic_source_type', 103, 1, sysdate(), null, null, '就诊来源类型');
INSERT INTO sys_dict_data VALUES(430, '000000', 1, '医生录入', 'DOCTOR', 'chronic_source_type', '', '', 'Y', 103, 1, sysdate(), null, null, '医生录入');
INSERT INTO sys_dict_data VALUES(431, '000000', 2, '管理员',   'ADMIN',  'chronic_source_type', '', '', 'N', 103, 1, sysdate(), null, null, '管理员录入');
INSERT INTO sys_dict_data VALUES(432, '000000', 3, 'HIS同步',  'HIS',    'chronic_source_type', '', '', 'N', 103, 1, sysdate(), null, null, 'HIS同步');

-- 174 chronic_sync_direction 同步方向
INSERT INTO sys_dict_type VALUES(174, '000000', '同步方向', 'chronic_sync_direction', 103, 1, sysdate(), null, null, '同步方向');
INSERT INTO sys_dict_data VALUES(433, '000000', 1, '入站', 'INBOUND',  'chronic_sync_direction', '', '', 'N', 103, 1, sysdate(), null, null, '外部→本系统');
INSERT INTO sys_dict_data VALUES(434, '000000', 2, '出站', 'OUTBOUND', 'chronic_sync_direction', '', '', 'N', 103, 1, sysdate(), null, null, '本系统→外部');

-- 175 chronic_sync_status 同步状态
INSERT INTO sys_dict_type VALUES(175, '000000', '同步状态', 'chronic_sync_status', 103, 1, sysdate(), null, null, '同步状态');
INSERT INTO sys_dict_data VALUES(435, '000000', 1, '成功', 'SUCCESS', 'chronic_sync_status', '', '', 'Y', 103, 1, sysdate(), null, null, '同步成功');
INSERT INTO sys_dict_data VALUES(436, '000000', 2, '失败', 'FAILED',  'chronic_sync_status', '', '', 'N', 103, 1, sysdate(), null, null, '同步失败');

-- 176 chronic_sync_type 同步类型
INSERT INTO sys_dict_type VALUES(176, '000000', '同步类型', 'chronic_sync_type', 103, 1, sysdate(), null, null, '同步类型');
INSERT INTO sys_dict_data VALUES(437, '000000', 1, '患者',     'PATIENT',    'chronic_sync_type', '', '', 'N', 103, 1, sysdate(), null, null, '患者数据同步');
INSERT INTO sys_dict_data VALUES(438, '000000', 2, '检验',     'LAB_EXAM',   'chronic_sync_type', '', '', 'N', 103, 1, sysdate(), null, null, '检验数据同步');
INSERT INTO sys_dict_data VALUES(439, '000000', 3, '影像',     'IMAGE_EXAM', 'chronic_sync_type', '', '', 'N', 103, 1, sysdate(), null, null, '影像数据同步');
INSERT INTO sys_dict_data VALUES(440, '000000', 4, '转诊',     'REFERRAL',   'chronic_sync_type', '', '', 'N', 103, 1, sysdate(), null, null, '转诊数据同步');

-- 177 chronic_assessment_dimension 风险评估维度
-- 说明：此字典对应评估规则中的 dimension_name 字段（规则引擎匹配 key），
--       与 chronic_metric_type（展示用指标类型）相互独立。
INSERT INTO sys_dict_type VALUES(177, '000000', '风险评估维度', 'chronic_assessment_dimension', 103, 1, sysdate(), null, null, '风险评估规则的维度名称（dimensionName），与规则引擎 key 保持一致');

-- ── 高血压相关维度 ──
INSERT INTO sys_dict_data VALUES(441, '000000', 1,  '收缩压',       'SBP',     'chronic_assessment_dimension', '', '', 'N', 103, 1, sysdate(), null, null, '收缩压 (mmHg)，数值范围匹配');
INSERT INTO sys_dict_data VALUES(442, '000000', 2,  '舒张压',       'DBP',     'chronic_assessment_dimension', '', '', 'N', 103, 1, sysdate(), null, null, '舒张压 (mmHg)，数值范围匹配');
INSERT INTO sys_dict_data VALUES(443, '000000', 3,  '年龄',         'AGE',     'chronic_assessment_dimension', '', '', 'N', 103, 1, sysdate(), null, null, '患者年龄（岁），数值范围匹配');
INSERT INTO sys_dict_data VALUES(444, '000000', 4,  '吸烟',         'SMOKING', 'chronic_assessment_dimension', '', '', 'N', 103, 1, sysdate(), null, null, '吸烟状态，equals 匹配，值为 YES/NO');
-- ── 糖尿病相关维度 ──
INSERT INTO sys_dict_data VALUES(445, '000000', 5,  '空腹血糖',     'FBG',     'chronic_assessment_dimension', '', '', 'N', 103, 1, sysdate(), null, null, '空腹血糖 (mmol/L)，数值范围匹配');
INSERT INTO sys_dict_data VALUES(446, '000000', 6,  '糖化血红蛋白', 'HBA1C',   'chronic_assessment_dimension', '', '', 'N', 103, 1, sysdate(), null, null, '糖化血红蛋白 (%)，数值范围匹配');
INSERT INTO sys_dict_data VALUES(447, '000000', 7,  'BMI',          'BMI',     'chronic_assessment_dimension', '', '', 'N', 103, 1, sysdate(), null, null, '身体质量指数，数值范围匹配');
-- ── 血脂相关维度 ──
INSERT INTO sys_dict_data VALUES(448, '000000', 8,  '总胆固醇',     'TC',      'chronic_assessment_dimension', '', '', 'N', 103, 1, sysdate(), null, null, '总胆固醇 (mmol/L)，数值范围匹配');
INSERT INTO sys_dict_data VALUES(449, '000000', 9,  '低密度脂蛋白', 'LDL',     'chronic_assessment_dimension', '', '', 'N', 103, 1, sysdate(), null, null, '低密度脂蛋白 (mmol/L)，数值范围匹配');
-- ── 心脏/肾脏相关维度 ──
INSERT INTO sys_dict_data VALUES(450, '000000', 10, '心率',         'HR',      'chronic_assessment_dimension', '', '', 'N', 103, 1, sysdate(), null, null, '心率 (bpm)，数值范围匹配');
INSERT INTO sys_dict_data VALUES(451, '000000', 11, 'eGFR',         'EGFR',    'chronic_assessment_dimension', '', '', 'N', 103, 1, sysdate(), null, null, '估算肾小球滤过率，数值范围匹配');
-- ── 风险因子维度（字符串匹配）──
INSERT INTO sys_dict_data VALUES(452, '000000', 12, '饮酒',         'DRINKING','chronic_assessment_dimension', '', '', 'N', 103, 1, sysdate(), null, null, '饮酒状态，equals 匹配，值为 YES/NO');
INSERT INTO sys_dict_data VALUES(453, '000000', 13, '家族史',       'FAMILY_HISTORY', 'chronic_assessment_dimension', '', '', 'N', 103, 1, sysdate(), null, null, '家族病史，equals 匹配，值为 YES/NO');

-- 178 chronic_consent_status 知情同意状态
INSERT INTO sys_dict_type VALUES(178, '000000', '知情同意状态', 'chronic_consent_status', 103, 1, sysdate(), null, null, '慢病知情同意状态');
INSERT INTO sys_dict_data VALUES(454, '000000', 1, '已签署', 'SIGNED', 'chronic_consent_status', '', '', 'Y', 103, 1, sysdate(), null, null, '已签署知情同意书');
INSERT INTO sys_dict_data VALUES(455, '000000', 2, '未签署', 'UNSIGNED', 'chronic_consent_status', '', '', 'N', 103, 1, sysdate(), null, null, '未签署知情同意书');

-- ----------------------------
-- 医疗文档OCR相关字典
-- ----------------------------

-- 179 chronic_ocr_source_type OCR来源类型
INSERT INTO sys_dict_type VALUES(179, '000000', 'OCR来源类型', 'chronic_ocr_source_type', 103, 1, sysdate(), null, null, '慢病医疗文档OCR来源类型');
INSERT INTO sys_dict_data VALUES(456, '000000', 1, '管理端', 'ADMIN', 'chronic_ocr_source_type', '', '', 'N', 103, 1, sysdate(), null, null, '管理端上传');
INSERT INTO sys_dict_data VALUES(457, '000000', 2, '医生端', 'DOCTOR', 'chronic_ocr_source_type', '', '', 'N', 103, 1, sysdate(), null, null, '医生端上传');
INSERT INTO sys_dict_data VALUES(458, '000000', 3, '患者端', 'PATIENT', 'chronic_ocr_source_type', '', '', 'N', 103, 1, sysdate(), null, null, '患者端上传');

-- 180 chronic_ocr_document_type OCR文档类型
INSERT INTO sys_dict_type VALUES(180, '000000', 'OCR文档类型', 'chronic_ocr_document_type', 103, 1, sysdate(), null, null, '慢病医疗文档OCR文档类型');
INSERT INTO sys_dict_data VALUES(459, '000000', 1, '病历首页', 'MEDICAL_RECORD_HOME', 'chronic_ocr_document_type', '', '', 'N', 103, 1, sysdate(), null, null, '病历首页');
INSERT INTO sys_dict_data VALUES(460, '000000', 2, '出院小结', 'DISCHARGE_SUMMARY', 'chronic_ocr_document_type', '', '', 'N', 103, 1, sysdate(), null, null, '出院小结');
INSERT INTO sys_dict_data VALUES(461, '000000', 3, '检验报告', 'LAB_REPORT', 'chronic_ocr_document_type', '', '', 'N', 103, 1, sysdate(), null, null, '检验报告');
INSERT INTO sys_dict_data VALUES(462, '000000', 4, '检查报告', 'EXAM_REPORT', 'chronic_ocr_document_type', '', '', 'N', 103, 1, sysdate(), null, null, '检查报告');
INSERT INTO sys_dict_data VALUES(463, '000000', 5, '诊断报告', 'DIAGNOSIS_REPORT', 'chronic_ocr_document_type', '', '', 'N', 103, 1, sysdate(), null, null, '诊断报告');
INSERT INTO sys_dict_data VALUES(464, '000000', 6, '其他', 'OTHER', 'chronic_ocr_document_type', '', '', 'N', 103, 1, sysdate(), null, null, '其他文档');

-- 181 chronic_ocr_input_type OCR输入类型
INSERT INTO sys_dict_type VALUES(181, '000000', 'OCR输入类型', 'chronic_ocr_input_type', 103, 1, sysdate(), null, null, '慢病医疗文档OCR输入类型');
INSERT INTO sys_dict_data VALUES(465, '000000', 1, '图片Base64', 'IMAGE_BASE64', 'chronic_ocr_input_type', '', '', 'N', 103, 1, sysdate(), null, null, '图片Base64编码');
INSERT INTO sys_dict_data VALUES(466, '000000', 2, '图片URL', 'IMAGE_URL', 'chronic_ocr_input_type', '', '', 'N', 103, 1, sysdate(), null, null, '图片URL地址');
INSERT INTO sys_dict_data VALUES(467, '000000', 3, 'PDF文件', 'PDF_FILE', 'chronic_ocr_input_type', '', '', 'N', 103, 1, sysdate(), null, null, 'PDF文件');
INSERT INTO sys_dict_data VALUES(468, '000000', 4, 'OSS文件', 'OSS_FILE', 'chronic_ocr_input_type', '', '', 'N', 103, 1, sysdate(), null, null, 'OSS存储文件');

-- 182 chronic_ocr_status OCR任务状态
INSERT INTO sys_dict_type VALUES(182, '000000', 'OCR任务状态', 'chronic_ocr_status', 103, 1, sysdate(), null, null, '慢病医疗文档OCR任务状态');
INSERT INTO sys_dict_data VALUES(469, '000000', 1, '待识别', 'PENDING', 'chronic_ocr_status', '', '', 'N', 103, 1, sysdate(), null, null, '等待OCR识别');
INSERT INTO sys_dict_data VALUES(470, '000000', 2, '识别中', 'RECOGNIZING', 'chronic_ocr_status', '', 'primary', 'N', 103, 1, sysdate(), null, null, '正在OCR识别');
INSERT INTO sys_dict_data VALUES(471, '000000', 3, '识别成功', 'SUCCESS', 'chronic_ocr_status', '', 'success', 'N', 103, 1, sysdate(), null, null, 'OCR识别成功');
INSERT INTO sys_dict_data VALUES(472, '000000', 4, '识别失败', 'FAILED', 'chronic_ocr_status', '', 'danger', 'N', 103, 1, sysdate(), null, null, 'OCR识别失败');
INSERT INTO sys_dict_data VALUES(473, '000000', 5, '已确认', 'CONFIRMED', 'chronic_ocr_status', '', 'success', 'N', 103, 1, sysdate(), null, null, '草稿已确认入库');
INSERT INTO sys_dict_data VALUES(474, '000000', 6, '已废弃', 'DISCARDED', 'chronic_ocr_status', '', 'info', 'N', 103, 1, sysdate(), null, null, '草稿已废弃');

-- 183 chronic_ocr_action_type OCR建档动作类型
INSERT INTO sys_dict_type VALUES(183, '000000', 'OCR建档动作', 'chronic_ocr_action_type', 103, 1, sysdate(), null, null, '慢病医疗文档OCR建档动作类型');
INSERT INTO sys_dict_data VALUES(475, '000000', 1, '新建档案', 'CREATE_ARCHIVE', 'chronic_ocr_action_type', '', '', 'N', 103, 1, sysdate(), null, null, '创建新患者档案');
INSERT INTO sys_dict_data VALUES(476, '000000', 2, '更新档案', 'UPDATE_ARCHIVE', 'chronic_ocr_action_type', '', '', 'N', 103, 1, sysdate(), null, null, '更新已有患者档案');

-- ==================== 医疗文档OCR相关字典 ====================

-- 179 chronic_ocr_source_type OCR来源类型
INSERT INTO sys_dict_type VALUES(179, '000000', 'OCR来源类型', 'chronic_ocr_source_type', 103, 1, sysdate(), null, null, '医疗文档OCR来源类型');
INSERT INTO sys_dict_data VALUES(456, '000000', 1, '管理端', 'ADMIN', 'chronic_ocr_source_type', '', '', 'N', 103, 1, sysdate(), null, null, '管理端上传');
INSERT INTO sys_dict_data VALUES(457, '000000', 2, '医生端', 'DOCTOR', 'chronic_ocr_source_type', '', '', 'N', 103, 1, sysdate(), null, null, '医生端上传');
INSERT INTO sys_dict_data VALUES(458, '000000', 3, '患者端', 'PATIENT', 'chronic_ocr_source_type', '', '', 'N', 103, 1, sysdate(), null, null, '患者端上传');

-- 180 chronic_ocr_document_type OCR文档类型
INSERT INTO sys_dict_type VALUES(180, '000000', 'OCR文档类型', 'chronic_ocr_document_type', 103, 1, sysdate(), null, null, '医疗文档OCR文档类型');
INSERT INTO sys_dict_data VALUES(459, '000000', 1, '病历首页', 'MEDICAL_RECORD_HOME', 'chronic_ocr_document_type', '', '', 'N', 103, 1, sysdate(), null, null, '病历首页');
INSERT INTO sys_dict_data VALUES(460, '000000', 2, '出院小结', 'DISCHARGE_SUMMARY', 'chronic_ocr_document_type', '', '', 'N', 103, 1, sysdate(), null, null, '出院小结');
INSERT INTO sys_dict_data VALUES(461, '000000', 3, '检验报告', 'LAB_REPORT', 'chronic_ocr_document_type', '', '', 'N', 103, 1, sysdate(), null, null, '检验报告');
INSERT INTO sys_dict_data VALUES(462, '000000', 4, '检查报告', 'EXAM_REPORT', 'chronic_ocr_document_type', '', '', 'N', 103, 1, sysdate(), null, null, '检查报告');
INSERT INTO sys_dict_data VALUES(463, '000000', 5, '诊断报告', 'DIAGNOSIS_REPORT', 'chronic_ocr_document_type', '', '', 'N', 103, 1, sysdate(), null, null, '诊断报告');
INSERT INTO sys_dict_data VALUES(464, '000000', 6, '其他', 'OTHER', 'chronic_ocr_document_type', '', '', 'N', 103, 1, sysdate(), null, null, '其他文档');

-- 181 chronic_ocr_input_type OCR输入类型
INSERT INTO sys_dict_type VALUES(181, '000000', 'OCR输入类型', 'chronic_ocr_input_type', 103, 1, sysdate(), null, null, '医疗文档OCR输入类型');
INSERT INTO sys_dict_data VALUES(465, '000000', 1, '图片Base64', 'IMAGE_BASE64', 'chronic_ocr_input_type', '', '', 'N', 103, 1, sysdate(), null, null, '图片Base64编码');
INSERT INTO sys_dict_data VALUES(466, '000000', 2, '图片URL', 'IMAGE_URL', 'chronic_ocr_input_type', '', '', 'N', 103, 1, sysdate(), null, null, '图片URL地址');
INSERT INTO sys_dict_data VALUES(467, '000000', 3, 'PDF文件', 'PDF_FILE', 'chronic_ocr_input_type', '', '', 'N', 103, 1, sysdate(), null, null, 'PDF文件');
INSERT INTO sys_dict_data VALUES(468, '000000', 4, 'OSS文件', 'OSS_FILE', 'chronic_ocr_input_type', '', '', 'N', 103, 1, sysdate(), null, null, 'OSS存储文件');

-- 182 chronic_ocr_status OCR任务状态
INSERT INTO sys_dict_type VALUES(182, '000000', 'OCR任务状态', 'chronic_ocr_status', 103, 1, sysdate(), null, null, '医疗文档OCR任务状态');
INSERT INTO sys_dict_data VALUES(469, '000000', 1, '待识别', 'PENDING', 'chronic_ocr_status', '', '', 'N', 103, 1, sysdate(), null, null, '等待OCR识别');
INSERT INTO sys_dict_data VALUES(470, '000000', 2, '识别中', 'RECOGNIZING', 'chronic_ocr_status', '', 'primary', 'N', 103, 1, sysdate(), null, null, '正在OCR识别');
INSERT INTO sys_dict_data VALUES(471, '000000', 3, '识别成功', 'SUCCESS', 'chronic_ocr_status', '', 'success', 'N', 103, 1, sysdate(), null, null, 'OCR识别成功');
INSERT INTO sys_dict_data VALUES(472, '000000', 4, '识别失败', 'FAILED', 'chronic_ocr_status', '', 'danger', 'N', 103, 1, sysdate(), null, null, 'OCR识别失败');
INSERT INTO sys_dict_data VALUES(473, '000000', 5, '已确认', 'CONFIRMED', 'chronic_ocr_status', '', 'success', 'N', 103, 1, sysdate(), null, null, '草稿已确认入库');
INSERT INTO sys_dict_data VALUES(474, '000000', 6, '已废弃', 'DISCARDED', 'chronic_ocr_status', '', 'info', 'N', 103, 1, sysdate(), null, null, '草稿已废弃');

-- 183 chronic_ocr_action_type OCR建档动作类型
INSERT INTO sys_dict_type VALUES(183, '000000', 'OCR建档动作', 'chronic_ocr_action_type', 103, 1, sysdate(), null, null, '医疗文档OCR建档动作类型');
INSERT INTO sys_dict_data VALUES(475, '000000', 1, '新建档案', 'CREATE_ARCHIVE', 'chronic_ocr_action_type', '', '', 'N', 103, 1, sysdate(), null, null, '新建患者档案');
INSERT INTO sys_dict_data VALUES(476, '000000', 2, '更新档案', 'UPDATE_ARCHIVE', 'chronic_ocr_action_type', '', '', 'N', 103, 1, sysdate(), null, null, '更新已有患者档案');

