package org.dromara.chronic.common.constant;

/**
 * 慢病管理模块字典类型常量
 * <p>
 * 统一管理慢病模块使用的字典类型，避免魔法值硬编码
 *
 * @author unimed
 */
public final class ChronicDictTypeConstant {

    private ChronicDictTypeConstant() {
    }

    // ==================== 患者档案 ====================

    /** 性别 */
    public static final String CHRONIC_GENDER = "chronic_gender";
    /** 民族 */
    public static final String CHRONIC_NATION = "chronic_nation";
    /** 职业 */
    public static final String CHRONIC_OCCUPATION = "chronic_occupation";
    /** 文化程度 */
    public static final String CHRONIC_EDUCATION_LEVEL = "chronic_education_level";
    /** 残疾类型 */
    public static final String CHRONIC_DISABILITY_TYPE = "chronic_disability_type";
    /** 残疾等级 */
    public static final String CHRONIC_DISABILITY_LEVEL = "chronic_disability_level";
    /** 管理状态 (PENDING_ENTRY/MANAGED/FOLLOWUP_OVERDUE/WARNING_ACTIVE/REFERRING/PAUSED/CLOSED) */
    public static final String CHRONIC_MANAGE_STATUS = "chronic_manage_status";
    /** 患者来源 (OUTPATIENT/SCREENING/HIS_SYNC/TRANSFER) */
    public static final String CHRONIC_PATIENT_SOURCE = "chronic_patient_source";
    /** 血型 (A/B/AB/O/RH_NEGATIVE/UNKNOWN) */
    public static final String CHRONIC_BLOOD_TYPE = "chronic_blood_type";
    /** 婚姻状况 (UNMARRIED/MARRIED/DIVORCED/WIDOWED/OTHER) */
    public static final String CHRONIC_MARITAL_STATUS = "chronic_marital_status";

    // ==================== 签约 ====================

    /** 签约类型 (PERSONAL/FAMILY/GROUP/COMMUNITY/CORPORATE) */
    public static final String CHRONIC_CONTRACT_TYPE = "chronic_contract_type";
    /** 续约状态 (ACTIVE/EXPIRING/EXPIRED/RENEWED) */
    public static final String CHRONIC_RENEWAL_STATUS = "chronic_renewal_status";
    /** 合同状态 (ACTIVE/TERMINATED) */
    public static final String CHRONIC_CONTRACT_STATUS = "chronic_contract_status";
    /** 服务包类型 (BASIC/ADVANCED/CUSTOM) */
    public static final String CHRONIC_PACKAGE_TYPE = "chronic_package_type";
    /** 履约状态 (PLANNED/DONE/MISSED) */
    public static final String CHRONIC_FULFILLMENT_STATUS = "chronic_fulfillment_status";

    // ==================== 医生团队 ====================

    /** 医生团队成员角色 (LEADER/MEMBER) */
    public static final String DOCTOR_GROUP_TYPE = "doctor_group_type";
    /** 团队状态 (ACTIVE/DISSOLVED) */
    public static final String CHRONIC_TEAM_STATUS = "chronic_team_status";

    // ==================== 用药 ====================

    /** 用药状态 (ACTIVE/STOPPED) */
    public static final String CHRONIC_MEDICATION_STATUS = "chronic_medication_status";
    /** 用药调整类型 (ADD/REDUCE/SWITCH/DOSE_CHANGE) */
    public static final String CHRONIC_ADJUST_TYPE = "chronic_adjust_type";
    /** 用药频次 */
    public static final String CHRONIC_FREQUENCY = "chronic_frequency";
    /** 给药途径 */
    public static final String CHRONIC_ROUTE = "chronic_route";
    /** 药物相互作用等级 (CONTRAINDICATED/MAJOR_RISK/MONITOR) */
    public static final String CHRONIC_INTERACTION_LEVEL = "chronic_interaction_level";

    // ==================== 风险与管理 ====================

    /** 风险等级 (LOW/MEDIUM/HIGH/VERY_HIGH) */
    public static final String CHRONIC_RISK_LEVEL = "chronic_risk_level";
    /** 管理等级 */
    public static final String CHRONIC_MANAGE_LEVEL = "chronic_manage_level";
    /** 管理计划状态 (DRAFT/ACTIVE/DISABLED/HISTORY) */
    public static final String CHRONIC_PLAN_STATUS = "chronic_plan_status";
    /** 管理计划项类型 (MEDICATION/DIET/EXERCISE/PSYCHOLOGY/FOLLOWUP/MONITOR) */
    public static final String CHRONIC_PLAN_ITEM_TYPE = "chronic_plan_item_type";

    // ==================== 随访 ====================

    /** 随访计划状态 (DRAFT/ACTIVE/DISABLED/HISTORY) */
    public static final String CHRONIC_FOLLOWUP_PLAN_STATUS = "chronic_followup_plan_status";
    /** 随访计划类型 (CHRONIC/POST_OP/REGULAR) - 用于患者端展示随访计划分类 */
    public static final String CHRONIC_FOLLOWUP_PLAN_TYPE = "chronic_followup_plan_type";
    /** 随访计划项类型 */
    public static final String CHRONIC_FOLLOWUP_ITEM_TYPE = "chronic_followup_item_type";
    /** 随访方式 (PHONE/VIDEO/OFFLINE/SELF_FILL/ADMIN_PROXY) */
    public static final String CHRONIC_VISIT_TYPE = "chronic_visit_type";
    /** 随访任务状态 (PENDING/PROCESSING/COMPLETED/CANCELED/OVERDUE) */
    public static final String CHRONIC_FOLLOWUP_TASK_STATUS = "chronic_followup_task_status";


    // ==================== 健康指标 ====================

    /** 健康指标类型 */
    public static final String CHRONIC_METRIC_TYPE = "chronic_metric_type";
    /** 测量场景 */
    public static final String CHRONIC_MEASURE_SCENE = "chronic_measure_scene";
    /** 测量时段 */
    public static final String CHRONIC_MEASURE_PERIOD = "chronic_measure_period";
    /** 测量体位 */
    public static final String CHRONIC_MEASURE_POSTURE = "chronic_measure_posture";
    /** 数据来源 (MANUAL/DEVICE/HIS_LIS) */
    public static final String CHRONIC_DATA_SOURCE = "chronic_data_source";

    // ==================== 设备 ====================

    /** 设备类型 */
    public static final String CHRONIC_DEVICE_TYPE = "chronic_device_type";
    /** 在线状态 */
    public static final String CHRONIC_ONLINE_STATUS = "chronic_online_status";

    // ==================== 生活方式 ====================

    /** 吸烟状态 */
    public static final String CHRONIC_SMOKING_STATUS = "chronic_smoking_status";
    /** 饮酒状态 */
    public static final String CHRONIC_DRINKING_STATUS = "chronic_drinking_status";
    /** 运动频率 */
    public static final String CHRONIC_EXERCISE_FREQ = "chronic_exercise_freq";
    /** 饮食习惯 */
    public static final String CHRONIC_DIET_HABIT = "chronic_diet_habit";
    /** 心理状态 */
    public static final String CHRONIC_PSYCHOLOGICAL_STATUS = "chronic_psychological_status";
    /** 依从性等级 */
    public static final String CHRONIC_COMPLIANCE_LEVEL = "chronic_compliance_level";

    // ==================== 体检 ====================

    /** 体检类型 (ANNUAL_CHECKUP/REGULAR_TEST/SPECIAL_TEST) */
    public static final String CHRONIC_EXAM_TYPE = "chronic_exam_type";
    /** 专项类别 (FUNDUS_PHOTO/ABI/NERVE_CONDUCTION/ECG/ECHO/CT) */
    public static final String CHRONIC_SPECIAL_CATEGORY = "chronic_special_category";

    // ==================== 预警 ====================

    /** 预警等级 (LOW/MEDIUM/HIGH/CRITICAL) */
    public static final String CHRONIC_WARNING_LEVEL = "chronic_warning_level";
    /** 预警事件状态 (NEW/CONFIRMED/PROCESSING/ESCALATED/RESOLVED/ARCHIVED) */
    public static final String CHRONIC_EVENT_STATUS = "chronic_warning_event_status";
    /** 预警处置类型 (CONFIRM/HANDLE/ESCALATE/RESOLVE) */
    public static final String CHRONIC_ACTION_TYPE = "chronic_action_type";

    // ==================== 转诊 ====================

    /** 转诊类别 */
    public static final String CHRONIC_REFERRAL_CATEGORY = "chronic_referral_category";
    /** 转诊状态 (PENDING/APPROVED/ACCEPTED/REJECTED/COMPLETED) */
    public static final String CHRONIC_REFERRAL_STATUS = "chronic_referral_status";
    /** 转诊类型 (UPWARD/DOWNWARD/TOWNSHIP) */
    public static final String CHRONIC_REFERRAL_TYPE = "chronic_referral_type";

    // ==================== 就诊 ====================

    /** 就诊类型 (INITIAL/FOLLOWUP) */
    public static final String CHRONIC_ENCOUNTER_TYPE = "chronic_encounter_type";
    /** 来源类型 (DOCTOR/ADMIN/HIS) */
    public static final String CHRONIC_SOURCE_TYPE = "chronic_source_type";
    /** 提交状态 (DRAFT/SUBMITTED) */
    public static final String CHRONIC_SUBMIT_STATUS = "chronic_submit_status";
    /** 诊断类型 (PRIMARY/SECONDARY) */
    public static final String CHRONIC_DIAGNOSIS_TYPE = "chronic_diagnosis_type";

    // ==================== 知情同意 ====================

    /** 同意类型 (SIGN_CONTRACT/DATA_SHARE/REFERRAL) */
    public static final String CHRONIC_CONSENT_TYPE = "chronic_consent_type";
    /** 知情同意状态 (SIGNED/UNSIGNED) */
    public static final String CHRONIC_CONSENT_STATUS = "chronic_consent_status";

    // ==================== 消息 ====================

    /** 会话类型 (DOCTOR_PATIENT/TEAM_PATIENT) */
    public static final String CHRONIC_SESSION_TYPE = "chronic_session_type";
    /** 发送者类型 (DOCTOR/PATIENT) */
    public static final String CHRONIC_SENDER_TYPE = "chronic_sender_type";
    /** 消息内容类型 (TEXT/IMAGE/VOICE) */
    public static final String CHRONIC_CONTENT_TYPE = "chronic_content_type";
    /** 消息类型 (doctor/system/service) - 用于患者端消息Tab分类 */
    public static final String CHRONIC_MESSAGE_TYPE = "chronic_message_type";

    // ==================== 健康教育 ====================

    /** 触发类型 (RULE_ENGINE/MANUAL/WEATHER/SEASONAL) */
    public static final String CHRONIC_TRIGGER_TYPE = "chronic_trigger_type";
    /** 推送渠道 (WECHAT/SMS/IVR/PAPER) */
    public static final String CHRONIC_PUSH_CHANNEL = "chronic_push_channel";
    /** 推送状态 */
    public static final String CHRONIC_DELIVERY_STATUS = "chronic_delivery_status";

    // ==================== 报告 ====================

    /** 报告类型 */
    public static final String CHRONIC_REPORT_TYPE = "chronic_report_type";
    /** 报告推送状态 */
    public static final String CHRONIC_PUSH_STATUS = "chronic_push_status";

    // ==================== 标签/时间线 ====================

    /** 标签类型 (RISK/CUSTOM/COMORBIDITY) */
    public static final String CHRONIC_TAG_TYPE = "chronic_tag_type";
    /** 时间线事件类型 (ARCHIVE/SIGN/FOLLOWUP/MEDICATION_ADJUST/WARNING/REFERRAL/PLAN_CHANGE) */
    public static final String CHRONIC_EVENT_TYPE = "chronic_event_type";

    // ==================== 病种 ====================

    /** 疾病分类 */
    public static final String CHRONIC_DISEASE_CATEGORY = "chronic_disease_category";
    /** 病种关联类型 */
    public static final String CHRONIC_RELATION_TYPE = "chronic_relation_type";

    // ==================== 筛查 ====================

    /** 审批状态 */
    public static final String CHRONIC_APPROVAL_STATUS = "chronic_approval_status";
    /** 入组状态 (PENDING/ENROLLED/REJECTED) */
    public static final String CHRONIC_ENROLL_STATUS = "chronic_enroll_status";
    /** 筛查批次状态 */
    public static final String CHRONIC_SCREENING_STATUS = "chronic_screening_status";

    // ==================== 医疗文档OCR ====================

    /** OCR来源类型 (ADMIN/DOCTOR/PATIENT) */
    public static final String CHRONIC_OCR_SOURCE_TYPE = "chronic_ocr_source_type";
    /** OCR文档类型 (MEDICAL_RECORD_HOME/DISCHARGE_SUMMARY/LAB_REPORT/EXAM_REPORT/DIAGNOSIS_REPORT/OTHER) */
    public static final String CHRONIC_OCR_DOCUMENT_TYPE = "chronic_ocr_document_type";
    /** OCR输入类型 (IMAGE_BASE64/IMAGE_URL/PDF_FILE/OSS_FILE) */
    public static final String CHRONIC_OCR_INPUT_TYPE = "chronic_ocr_input_type";
    /** OCR任务状态 (PENDING/PROCESSING/SUCCESS/FAILED/CONFIRMED/DISCARDED) */
    public static final String CHRONIC_OCR_STATUS = "chronic_ocr_status";
    /** OCR建档动作类型 (CREATE_ARCHIVE/UPDATE_ARCHIVE) */
    public static final String CHRONIC_OCR_ACTION_TYPE = "chronic_ocr_action_type";

    // ==================== 患者端专用 ====================

    /** 健康页Tab类型 (trend/input/lifestyle) - 用于患者端健康页Tab分类 */
    public static final String CHRONIC_HEALTH_TAB_TYPE = "chronic_health_tab_type";

    // ==================== 其他 ====================

    /** 附件业务类型 (REPORT_PDF/SIGN_IMAGE/FUNDUS_PHOTO/ECG/OTHER) */
    public static final String CHRONIC_BIZ_TYPE = "chronic_biz_type";
    /** KPI分类 (MANAGEMENT_RATE/COMPLIANCE_RATE/CONTROL_RATE) */
    public static final String CHRONIC_KPI_CATEGORY = "chronic_kpi_category";
    /** ICD版本 (ICD10/ICD11) */
    public static final String CHRONIC_ICD_VERSION = "chronic_icd_version";
    /** 同步类型 */
    public static final String CHRONIC_SYNC_TYPE = "chronic_sync_type";
    /** 同步方向 */
    public static final String CHRONIC_SYNC_DIRECTION = "chronic_sync_direction";
    /** 同步状态 */
    public static final String CHRONIC_SYNC_STATUS = "chronic_sync_status";
    /** 评估维度 */
    public static final String CHRONIC_ASSESSMENT_DIMENSION = "chronic_assessment_dimension";
}
