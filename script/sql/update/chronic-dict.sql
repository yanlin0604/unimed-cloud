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
