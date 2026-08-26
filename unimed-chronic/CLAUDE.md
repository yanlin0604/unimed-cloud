[根目录](../CLAUDE.md) > **unimed-chronic**

# Unimed Chronic 慢病管理模块

## 字段值定义 (Field Value Definitions)

### 签约相关字段取值表

| 字段名 | 可能取值 | 中文含义 | 业务说明 |
| -------- | ---------- | ---------- | ---------- |
| **contract_type**<br>(签约类型) | PERSONAL<br>FAMILY<br>GROUP<br>COMMUNITY<br>CORPORATE | 个人签约<br>家庭签约<br>团体签约<br>社区签约<br>企业签约 | <ul><li>PERSONAL：患者个人与医疗机构/医生团队的服务协议</li><li>FAMILY：家庭成员（如夫妻、父母子女）统一签约慢病管理服务</li><li>GROUP：企业、学校、机构等为成员统一签约服务（员工福利、学生健康等）</li><li>COMMUNITY：社区卫生中心为辖区居民提供统一的慢病管理服务（公共卫生项目）</li><li>CORPORATE：专门针对企业客户的健康管理服务包（员工健康计划）</li></ul> |
| **renewal_status**<br>(续约状态) | ACTIVE<br>EXPIRING<br>EXPIRED<br>RENEWED | 有效中<br>即将到期<br>已到期<br>已续约 | <ul><li>ACTIVE：合同当前处于有效状态，在有效期内</li><li>EXPIRING：合同即将到达结束日期（通常提前30-60天开始续约提醒）</li><li>EXPIRED：合同已超过结束日期，未及时续约</li><li>RENEWED：合同已经成功续约，重新开始新的服务周期</li></ul> |
| **contract_status**<br>(合同状态) | ACTIVE<br>TERMINATED | 有效中<br>已终止 | <ul><li>ACTIVE：合同当前有效且正在执行中，双方履行义务</li><li>TERMINATED：合同被提前终止，不再执行（可能原因：患者主动退约、机构终止服务、违约等）</li></ul> |
| **package_type**<br>(服务包类型) | BASIC<br>ADVANCED<br>CUSTOM | 基础包<br>高级包<br>自定义包 | <ul><li>BASIC：基础服务包，包含常规慢病管理服务</li><li>ADVANCED：高级服务包，增加专项检查和优先服务</li><li>CUSTOM：自定义服务包，按需配置服务内容</li></ul> |
| **fulfillment_status**<br>(履约状态) | PLANNED<br>DONE<br>MISSED | 已计划<br>已完成<br>已逾期 | <ul><li>PLANNED：计划中的履约项</li><li>DONE：已完成履约</li><li>MISSED：逾期未完成</li></ul> |

### 患者档案字段取值表

| 字段名 | 可能取值 | 中文含义 | 业务说明 |
| -------- | ---------- | ---------- | ---------- |
| **gender**<br>(性别) | 0<br>1<br>2 | 女<br>男<br>未知 | 系统字典 chronic_gender |
| **manage_status**<br>(管理状态) | PENDING_ENTRY<br>MANAGED<br>FOLLOWUP_OVERDUE<br>WARNING_ACTIVE<br>REFERRING<br>PAUSED<br>CLOSED | 待入组<br>管理中<br>随访逾期<br>预警活跃<br>转诊中<br>暂停管理<br>已关闭 | <ul><li>PENDING_ENTRY：筛查通过待建档</li><li>MANAGED：正常管理中</li><li>FOLLOWUP_OVERDUE：有随访任务超期未完成</li><li>WARNING_ACTIVE：存在未处理预警</li><li>REFERRING：正在转诊流程中</li><li>PAUSED：管理暂停（如住院、外出）</li><li>CLOSED：管理终止</li></ul> |
| **source**<br>(患者来源) | OUTPATIENT<br>SCREENING<br>HIS_SYNC<br>TRANSFER | 门诊<br>筛查<br>HIS同步<br>转入 | <ul><li>OUTPATIENT：门诊就诊建档</li><li>SCREENING：社区筛查建档</li><li>HIS_SYNC：HIS系统自动同步</li><li>TRANSFER：其他机构转入</li></ul> |

### 医生团队字段取值表

| 字段名 | 可能取值 | 中文含义 | 业务说明 |
|--------|----------|----------|----------|
| **member_role**<br>(成员角色) | LEADER<br>MEMBER | 团队负责人<br>团队成员 | 字典 doctor_group_type |
| **team_status**<br>(团队状态) | ACTIVE<br>DISSOLVED | 活跃<br>已解散 | <ul><li>ACTIVE：团队正常运作</li><li>DISSOLVED：团队已解散</li></ul> |

### 用药管理字段取值表

| 字段名 | 可能取值 | 中文含义 | 业务说明 |
| -------- | ---------- | ---------- | ---------- |
| **status**<br>(用药状态) | ACTIVE<br>STOPPED | 使用中<br>已停药 | <ul><li>ACTIVE：当前正在使用</li><li>STOPPED：已停用</li></ul> |
| **adjust_type**<br>(调整类型) | ADD<br>REDUCE<br>SWITCH<br>DOSE_CHANGE | 加药<br>减药<br>换药<br>调量 | <ul><li>ADD：新增药品</li><li>REDUCE：减少药品</li><li>SWITCH：替换药品</li><li>DOSE_CHANGE：调整剂量</li></ul> |
| **interaction_level**<br>(相互作用等级) | CONTRAINDICATED<br>MAJOR_RISK<br>MONITOR | 禁忌<br>重大风险<br>需监测 | <ul><li>CONTRAINDICATED：禁止联用</li><li>MAJOR_RISK：联用风险高，需医生确认</li><li>MONITOR：联用需常规监测</li></ul> |

### 风险与评估字段取值表

| 字段名 | 可能取值 | 中文含义 | 业务说明 |
| -------- | ---------- | ---------- | ---------- |
| **risk_level**<br>(风险等级) | LOW<br>MEDIUM<br>HIGH<br>VERY_HIGH | 低风险<br>中风险<br>高风险<br>极高风险 | 筛查记录和风险评估共用此字段 |
| **plan_status**<br>(管理计划状态) | DRAFT<br>ACTIVE<br>DISABLED<br>HISTORY | 草稿<br>生效中<br>已停用<br>历史 | <ul><li>DRAFT：草稿状态</li><li>ACTIVE：当前生效方案</li><li>DISABLED：已停用</li><li>HISTORY：历史方案（被新方案替代）</li></ul> |
| **item_type**<br>(计划项类型) | MEDICATION<br>DIET<br>EXERCISE<br>PSYCHOLOGY<br>FOLLOWUP<br>MONITOR | 用药<br>饮食<br>运动<br>心理<br>随访<br>监测 | 管理计划和随访计划共用此字段 |

### 随访字段取值表

| 字段名 | 可能取值 | 中文含义 | 业务说明 |
|--------|----------|----------|----------|
| **task_status**<br>(任务状态) | PENDING<br>REMINDING<br>DONE<br>OVERDUE<br>CANCELLED | 待执行<br>提醒中<br>已完成<br>已逾期<br>已取消 | <ul><li>PENDING：等待执行</li><li>REMINDING：已发送提醒</li><li>DONE：已完成随访</li><li>OVERDUE：超过计划日期未完成</li><li>CANCELLED：已取消</li></ul> |
| **visit_type**<br>(随访方式) | PHONE<br>VIDEO<br>OFFLINE<br>SELF_FILL<br>ADMIN_PROXY | 电话<br>视频<br>线下<br>患者自填<br>管理员代填 | <ul><li>PHONE：电话随访</li><li>VIDEO：视频随访</li><li>OFFLINE：上门/门诊随访</li><li>SELF_FILL：患者自行填写问卷</li><li>ADMIN_PROXY：管理员代填</li></ul> |

### 健康指标字段取值表

| 字段名 | 可能取值 | 中文含义 | 业务说明 |
|--------|----------|----------|----------|
| **data_source**<br>(数据来源) | MANUAL<br>DEVICE<br>HIS_LIS | 手动录入<br>设备采集<br>HIS/LIS同步 | <ul><li>MANUAL：医生或患者手动录入</li><li>DEVICE：IoT设备自动上报</li><li>HIS_LIS：从HIS/LIS系统同步</li></ul> |

### 生活方式字段取值表

| 字段名 | 可能取值 | 中文含义 | 业务说明 |
| -------- | ---------- | ---------- | ---------- |
| **smoking_status**<br>(吸烟状态) | NEVER<br>FORMER<br>CURRENT | 从不吸烟<br>已戒烟<br>当前吸烟 | 字典 chronic_smoking_status |
| **drinking_status**<br>(饮酒状态) | NEVER<br>FORMER<br>CURRENT | 从不饮酒<br>已戒酒<br>当前饮酒 | 字典 chronic_drinking_status |
| **compliance_level**<br>(依从性等级) | GOOD<br>FAIR<br>POOR | 良好<br>一般<br>差 | 字典 chronic_compliance_level |

### 体检字段取值表

| 字段名 | 可能取值 | 中文含义 | 业务说明 |
|--------|----------|----------|----------|
| **exam_type**<br>(体检类型) | ANNUAL_CHECKUP<br>REGULAR_TEST<br>SPECIAL_TEST | 年度体检<br>常规检查<br>专项检查 | <ul><li>ANNUAL_CHECKUP：年度健康体检</li><li>REGULAR_TEST：定期复查</li><li>SPECIAL_TEST：专项检查</li></ul> |
| **special_category**<br>(专项类别) | FUNDUS_PHOTO<br>ABI<br>NERVE_CONDUCTION<br>ECG<br>ECHO<br>CT | 眼底照相<br>踝肱指数<br>神经传导<br>心电图<br>心脏超声<br>CT | 慢病专项筛查项目 |

### 预警字段取值表

| 字段名 | 可能取值 | 中文含义 | 业务说明 |
| -------- | ---------- | ---------- | ---------- |
| **warning_level**<br>(预警等级) | LOW<br>MEDIUM<br>HIGH<br>CRITICAL | 低<br>中<br>高<br>危急 | <ul><li>LOW：轻微偏离，关注即可</li><li>MEDIUM：中度偏离，需干预</li><li>HIGH：严重偏离，需立即干预</li><li>CRITICAL：危急值，需紧急处理</li></ul> |
| **event_status**<br>(事件状态) | NEW<br>CONFIRMED<br>PROCESSING<br>ESCALATED<br>RESOLVED<br>ARCHIVED | 新建<br>已确认<br>处理中<br>已升级<br>已解决<br>已归档 | <ul><li>NEW：系统自动生成</li><li>CONFIRMED：医生已确认</li><li>PROCESSING：正在处理</li><li>ESCALATED：已升级处理</li><li>RESOLVED：已解决</li><li>ARCHIVED：已归档</li></ul> |
| **action_type**<br>(处置类型) | CONFIRM<br>HANDLE<br>ESCALATE<br>RESOLVE | 确认<br>处理<br>升级<br>解决 | <ul><li>CONFIRM：确认预警有效</li><li>HANDLE：执行处理措施</li><li>ESCALATE：升级到上级处理</li><li>RESOLVE：标记为已解决</li></ul> |

### 转诊字段取值表

| 字段名 | 可能取值 | 中文含义 | 业务说明 |
|--------|----------|----------|----------|
| **referral_status**<br>(转诊状态) | PENDING<br>APPROVED<br>ACCEPTED<br>REJECTED<br>COMPLETED | 待审核<br>已批准<br>已接收<br>已拒绝<br>已完成 | <ul><li>PENDING：提交申请待审核</li><li>APPROVED：转出方已批准</li><li>ACCEPTED：接收方已接收</li><li>REJECTED：申请被拒绝</li><li>COMPLETED：转诊流程完成</li></ul> |
| **referral_type**<br>(转诊类型) | UPWARD<br>DOWNWARD<br>TOWNSHIP | 上转<br>下转<br>乡镇转诊 | <ul><li>UPWARD：下级转上级</li><li>DOWNWARD：上级转下级</li><li>TOWNSHIP：乡镇卫生院转诊</li></ul> |

### 就诊字段取值表

| 字段名 | 可能取值 | 中文含义 | 业务说明 |
| -------- | ---------- | ---------- | ---------- |
| **encounter_type**<br>(就诊类型) | INITIAL<br>FOLLOWUP | 首诊<br>复诊 | <ul><li>INITIAL：首次就诊</li><li>FOLLOWUP：复诊随访</li></ul> |
| **submit_status**<br>(提交状态) | DRAFT<br>SUBMITTED | 草稿<br>已提交 | <ul><li>DRAFT：暂存草稿</li><li>SUBMITTED：已正式提交</li></ul> |
| **diagnosis_type**<br>(诊断类型) | PRIMARY<br>SECONDARY | 主诊断<br>次诊断 | <ul><li>PRIMARY：主要诊断</li><li>SECONDARY：次要/伴随诊断</li></ul> |

### 其他字段取值表

| 字段名 | 可能取值 | 中文含义 | 业务说明 |
| -------- | ---------- | ---------- | ---------- |
| **tag_type**<br>(标签类型) | RISK<br>CUSTOM<br>COMORBIDITY | 风险标签<br>自定义标签<br>合并症标签 | 字典 chronic_tag_type |
| **event_type**<br>(时间线事件类型) | ARCHIVE<br>SIGN<br>FOLLOWUP<br>MEDICATION_ADJUST<br>WARNING<br>REFERRAL<br>PLAN_CHANGE | 建档<br>签约<br>随访<br>用药调整<br>预警<br>转诊<br>方案变更 | 字典 chronic_event_type |
| **enroll_status**<br>(入组状态) | PENDING<br>ENROLLED<br>REJECTED | 待入组<br>已入组<br>已拒绝 | <ul><li>PENDING：筛查通过待入组</li><li>ENROLLED：已入组管理</li><li>REJECTED：拒绝入组</li></ul> |
| **consent_type**<br>(同意类型) | SIGN_CONTRACT<br>DATA_SHARE<br>REFERRAL | 签约同意<br>数据共享同意<br>转诊同意 | 字典 chronic_consent_type |
| **session_type**<br>(会话类型) | DOCTOR_PATIENT<br>TEAM_PATIENT | 医患对话<br>团队对话 | 字典 chronic_session_type |
| **trigger_type**<br>(触发类型) | RULE_ENGINE<br>MANUAL<br>WEATHER<br>SEASONAL | 规则引擎<br>手动触发<br>天气触发<br>季节触发 | 字典 chronic_trigger_type |
| **push_channel**<br>(推送渠道) | WECHAT<br>SMS<br>IVR<br>PAPER | 微信<br>短信<br>语音<br>纸质 | 字典 chronic_push_channel |
| **icd_version**<br>(ICD版本) | ICD10<br>ICD11 | ICD-10<br>ICD-11 | 字典 chronic_icd_version |
| **biz_type**<br>(附件业务类型) | REPORT_PDF<br>SIGN_IMAGE<br>FUNDUS_PHOTO<br>ECG<br>OTHER | 报告PDF<br>签名图片<br>眼底照片<br>心电图<br>其他 | 字典 chronic_biz_type |
| **kpi_category**<br>(KPI分类) | MANAGEMENT_RATE<br>COMPLIANCE_RATE<br>CONTROL_RATE | 管理率<br>依从率<br>控制率 | 字典 chronic_kpi_category |

**说明：**

- `renewal_status` 和 `contract_status` 的取值直接来自 `script/sql/unimed-chronic.sql` 表注释
- `contract_type` 的取值基于业务场景合理推断（数据库中仅示例 `PERSONAL`，但结合慢病管理系统需求补充了其他常见类型）
- 所有字段取值采用全大写+下划线命名 convention，便于在代码中定义枚举或常量
- 业务逻辑中需注意：`renewal_status` 和 `contract_status` 是独立维度，一个合同可能同时处于如 `ACTIVE`（有效中）+ `EXPIRING`（即将到期） 的组合状态

## 变更记录 (Changelog)

- **随访统计看板重设计** - 后端 `ChFollowupStatVo` 新增 随访结论/康复评级/任务状态/任务来源/失访原因 五个分布口径与 `controlledTrend` 控制趋势,`IChFollowupStatService`/`ChFollowupStatServiceImpl`/`FollowupStatController` 新增 6 个 `/chronic/admin/followup-stat/*` 端点并让 `getFullStatDashboard` 全量返回;前端 `stat/index.vue` 由 antdv 表格重写为 ECharts 看板(KPI 指标卡 + 趋势/方式/病种/结论/复康/状态漏斗/来源/失访图表 + 底表),`days` 时间范围筛选驱动趋势。约束:`areaCode`/`orgId` 仅透传不参与过滤。纯前端渲染+Java 内存统计,无 DB/SQL 变更。
- **2026-04-22** - 初始化慢病模块文档;双数据源架构(独立慢病库 + 系统库只读兜底);62个控制器覆盖4层管控(admin/doctor/patient/openapi);55张业务表
- **2026-04-20** - slow病管理服务增强（R4-R12）
- **2026-04-18** - 端口调整及测试优化
- **2026-04-17** - 添加 unimed-chronic 慢病管理模块初始代码

## 模块职责

慢病管理业务聚合模块，基于"一个患者、一份档案、签约家庭医生、终身健康管理"的业务理念，提供慢病患者全生命周期健康管理服务。支持多层级管控：平台管理端（Admin）、医生端（Doctor）、患者端（Patient）、对外开放接口（OpenAPI）。

## 模块结构

```
unimed-chronic/
├── unimed-chronic-api        # 慢病域 API 定义（接口层，无实现）
├── unimed-chronic-biz        # 慢病域业务实现（核心代码与启动入口）
│   ├── controller/
│   │   ├── admin/           # B 端管理后台控制器（26个）
│   │   ├── doctor/          # 医生端控制器（13个）
│   │   ├── patient/         # 患者端控制器（13个）
│   │   └── openapi/         # 对外开放接口（10个）
│   ├── domain/
│   │   ├── entity/          # 55个实体类
│   │   ├── bo/              # 业务对象
│   │   └── vo/              # 视图对象
│   ├── service/
│   ├── mapper/              # MyBatis Mapper 接口
│   └── manager/             # 复杂业务编排层
```

## 入口与启动

- **启动类**: `unimed-chronic-biz/src/main/java/org/dromara/chronic/UnimedChronicApplication.java`
- **包名**: `org.dromara.chronic`
- **端口**: 9208
- **Nacos 服务名**: `unimed-chronic`
- **主配置**: `unimed-chronic-biz/src/main/resources/application.yml`

### 双数据源配置

```yaml
chronic:
  datasource:
    primary-db: unimed-chronic          # 默认主数据源使用独立慢病库
    system-read-enabled: true           # 系统库只允许通过只读方式兜底查询
    required-configs:
      - datasource.chronic              # 慢病业务数据库
      - datasource.system-master        # 系统主数据库（只读）
```

## 对外接口

### B 端管理后台接口 (controller/admin/)

| 控制器 | 职责 | 权限前缀 |
| -------- | ------ | ---------- |
| PatientProfileController | 患者档案管理（分页/新增/编辑/详情/时间线） | chronic:patient:* |
| ScreeningBatchController | 筛查批次管理 | chronic:screening:* |
| DiseaseConfigController | 病种配置管理 | chronic:disease:* |
| IcdDictController | ICD 编码字典 | chronic:icdDict:* |
| DiseaseRelationController | 病种关联配置 | chronic:diseaseRelation:* |
| TeamController | 医生团队管理 | chronic:team:* |
| ManagePlanController | 管理计划 | chronic:managePlan:* |
| FollowupPlanController | 随访计划管理 | chronic:followupPlan:* |
| FollowupTaskController | 随访任务管理 | chronic:followupTask:* |
| WarningRuleController | 预警规则配置 | chronic:warningRule:* |
| WarningController | 预警事件处理 | chronic:warning:* |
| MedicationController | 用药管理 | chronic:medication:* |
| HealthMetricController | 健康指标管理 | chronic:healthMetric:* |
| HealthExamController | 健康体检管理 | chronic:healthExam:* |
| LifestyleController | 生活方式记录 | chronic:lifestyle:* |
| RiskAssessmentController | 风险评估管理 | chronic:riskAssessment:* |
| AssessmentRuleController | 评估规则配置 | chronic:assessmentRule:* |
| ReportController | 报告管理 | chronic:report:* |
| ReportTemplateController | 报告模板管理 | chronic:reportTemplate:* |
| ContractController | 服务签约管理 | chronic:contract:* |
| EducationContentController | 健康科普内容 | chronic:education:* |
| EducationPushController | 科普推送管理 | chronic:education:* |
| FollowupQuestionnaireController | 随访问卷管理 | chronic:questionnaire:* |
| AreaController | 区域管理 | chronic:area:* |
| ArchiveShareController | 档案共享管理 | chronic:archiveShare:* |
| ConsentController | 知情同意管理 | chronic:consent:* |
| AuditLogController | 审计日志 | chronic:audit:* |
| DashboardController | 数据看板 | chronic:dashboard:* |
| OpsController | 运营操作接口 | chronic:ops:* |
| EncounterController | 就诊记录 | chronic:encounter:* |
| ReferralController | 转诊管理 | chronic:referral:* |

### 医生端接口 (controller/doctor/)

| 控制器 | 职责 |
| -------- | ------ |
| DoctorPatientController | 医生视角患者管理 |
| DoctorTeamController | 医生团队管理 |
| DoctorAuthController | 医生认证 |
| DoctorScreeningController | 医生端筛查 |
| DoctorMedicationController | 医生端用药 |
| DoctorMetricController | 医生端指标 |
| DoctorWarningController | 医生端预警 |
| DoctorEncounterController | 医生端就诊 |
| DoctorReferralController | 医生端转诊 |
| DoctorFollowupTaskController | 医生端随访任务 |
| DoctorEducationController | 医生端健康科普 |
| DoctorArchiveShareController | 医生档案共享 |

### 患者端接口 (controller/patient/)

| 控制器 | 职责 |
| -------- | ------ |
| PatientCenterController | 个人中心 |
| PatientAuthController | 患者认证 |
| PatientHealthExamController | 健康体检 |
| PatientMedicationController | 用药记录 |
| PatientMetricController | 健康指标 |
| PatientLifestyleController | 生活方式 |
| PatientEducationController | 健康科普 |
| PatientFollowupController | 随访管理 |
| PatientReportController | 报告查看 |
| PatientFamilyController | 家庭档案 |
| PatientMessageController | 消息通知 |
| PatientSosController | 紧急求助 |

### 开放接口 (controller/openapi/)

| 控制器 | 职责 |
| -------- | ------ |
| OpenapiHisController | HIS 系统对接 |
| OpenapiLisController | LIS 检验系统对接 |
| OpenapiPacsController | PACS 影像系统对接 |
| OpenapiPhsController | 公卫系统对接 |
| OpenapiDeviceController | IoT 设备对接 |
| OpenapiReferralController | 转诊开放接口 |
| OpenapiRiskAssessmentController | 风险评估开放接口 |
| OpenapiWebhookController | Webhook 订阅/推送 |

## 关键依赖与配置

### 核心依赖

```xml
unimed-chronic-api          # 本域 API 定义
unimed-common-core          # 核心工具
unimed-common-web           # Web 框架
unimed-common-security      # Sa-Token 安全
unimed-common-mybatis       # MyBatis-Plus
unimed-common-redis         # Redis 缓存
unimed-common-nacos         # Nacos 配置
unimed-common-log           # 操作日志
unimed-common-doc           # API 文档
unimed-common-dubbo         # Dubbo RPC
unimed-api-system           # 系统服务远程接口
unimed-api-resource         # 资源服务远程接口
unimed-api-workflow         # 工作流远程接口
```

### Dubbo 远程服务消费

- RemoteUserService - 用户服务
- RemoteDictService - 字典服务
- RemoteTenantService - 租户服务
- RemoteFileService - 文件服务（OSS）
- RemoteWorkflowService - 工作流服务

## 数据模型

### 核心业务表（55张）

| 表名 | 说明 | 关键字段 |
| ------ | ------ | ---------- |
| ch_patient_profile | 患者档案 | patientId, name, idCard, phone, gender |
| ch_patient_disease | 患者病种 | patientId, diseaseCode, enableStatus |
| ch_patient_timeline | 患者时间线 | patientId, eventType, content |
| ch_patient_tag | 患者标签 | patientId, tagCode |
| ch_disease_config | 病种配置 | diseaseCode, diseaseName,enableStatus |
| ch_disease_relation | 病种关联 | configId, diseaseCode, relatedDiseaseCode |
| ch_icd_dict | ICD 编码字典 | icdCode, icdName, parentCode |
| ch_screening_batch | 筛查批次 | batchId, batchName, status |
| ch_screening_record | 筛查记录 | screeningId, batchId, patientId |
| ch_manage_plan | 管理计划 | planId, patientId, status |
| ch_manage_plan_item | 计划条目 | itemId, planId, itemType |
| ch_followup_plan | 随访计划 | planId, patientId, doctorId |
| ch_followup_task | 随访任务 | taskId, planId, dueTime |
| ch_followup_record | 随访记录 | recordId, taskId, result |
| ch_followup_questionnaire | 随访问卷 | questionId, content |
| ch_followup_answer | 问卷答案 | answerId, questionId, response |
| ch_health_metric_record | 健康指标记录 | recordId, patientId, metricType, value |
| ch_device_bind | 设备绑定 | deviceId, patientId, bindType |
| ch_device_raw_record | 设备原始数据 | rawId, deviceId, content |
| ch_health_exam | 健康体检 | examId, patientId, examDate |
| ch_health_exam_item | 体检项目 | itemId, examId, itemName, result |
| ch_lifestyle_record | 生活方式记录 | recordId, patientId, lifestyleType, content |
| ch_medication_record | 用药记录 | recordId, patientId, drugName, dosage |
| ch_medication_adjust | 用药调整 | adjustId, recordId, adjustType |
| ch_drug_interaction | 药物相互作用 | interactionId, drugA, drugB, riskLevel |
| ch_warning_rule | 预警规则 | ruleId, ruleType, threshold |
| ch_warning_event | 预警事件 | eventId, patientId, ruleId, status |
| ch_risk_assessment | 风险评估 | assessmentId, patientId, score |
| ch_assessment_rule | 评估规则 | ruleId, assessmentType, formula |
| ch_manager_level_record | 管理等级记录 | recordId, patientId, level |
| ch_doctor_team | 医生团队 | teamId, name, areaCode |
| ch_doctor_team_member | 团队成员 | memberId, teamId, doctorId |
| ch_patient_contract | 服务签约 | contractId, patientId, teamId |
| ch_contract_fulfillment | 履约记录 | fulfillmentId, contractId |
| ch_contract_service_package | 服务套餐 | packageId, name, serviceList |
| ch_referral_record | 转诊记录 | referralId, patientId, fromOrg, toOrg |
| ch_encounter_record | 就诊记录 | encounterId, patientId, visitTime |
| ch_report_template | 报告模板 | templateId, name, content |
| ch_report_instance | 报告实例 | instanceId, templateId, patientId |
| ch_education_content | 科普内容 | contentId, title, contentType |
| ch_health_education_delivery | 推送记录 | deliveryId, patientId, contentId |
| ch_audit_log | 慢病审计日志 | logId, action, detail |
| ch_area_dict | 区域字典 | areaCode, areaName, parentCode |
| ch_org_area_mapping | 机构区域映射 | mappingId, orgId, areaCode |
| ch_warning_action | 预警处置 | actionId, eventId, actionType |
| ch_health_education_content | 健康知识库 | contentId, category, title |
| ch_education_rule | 教育规则 | ruleId, condition, action |
| ch_notification_template | 通知模板 | templateId, type, content |
| ch_message_session | 消息会话 | sessionId, patientId, doctorId |
| ch_message_content | 消息内容 | contentId, sessionId, content |
| ch_file_attachment | 文件附件 | fileId, bizType, bizId, ossId |
| ch_patient_account | 患者账户 | accountId, patientId, balance |
| ch_consent_record | 同意记录 | consentId, patientId, consentType |
| ch_external_sync_log | 外部同步日志 | logId, syncType, source |
| ch_archive_share_apply | 档案共享申请 | applyId, patientId, targetOrg |
| ch_kpi_definition | KPI 定义 | kpiId, name, formula |
| ch_stat_area_day | 区域统计日表 | statDate, areaCode, metrics |

### 数据源分布

- **慢病库（chronic）**: 55张业务表，存储慢病管理专属数据
- **系统库（system-master，只读）**: 读取用户、字典、租户等共享数据

## 服务层架构

### Manager 层复杂业务编排

| Manager | 职责 |
| --------- | ------ |
| PatientProfileManager | 患者档案创建、完整生命周期处理 |
| TeamManager | 团队分配、医生管理 |
| FollowupManager | 随访计划生成、任务调度 |
| HealthExamManager | 体检报告聚合、异常检测 |
| ContractHistoryManager | 签约变更追溯 |
| ArchiveShareManager | 档案共享审核、权限控制 |

## 测试与质量

### 测试文件

- `ChronicLoopIntegrationTest.java` - 业务流程闭环集成测试
- `ChronicGapClosureIntegrationTest.java` - 缝隙填补集成测试
- `ArchiveShareManagerTest.java` - 档案共享管理测试
- `HealthMetricManagerTest.java` - 健康指标管理测试
- `ContractHistoryManagerTest.java` - 签约历史测试
- `DashboardManagerTest.java` - 数据看板测试
- `ReportPdfHelperTest.java` - 报告 PDF 生成测试
- `ReportGenerateManagerTest.java` - 报告生成管理测试
- `WarningStatusTransitionValidatorTest.java` - 预警状态流转校验测试

## 编码规范

### 分层规范

```
controller/          # 按角色分包（admin/doctor/patient/openapi）
  ├── admin/        # @SaCheckPermission 权限控制
  ├── doctor/       # 医生端接口
  ├── patient/      # 患者端接口
  └── openapi/      # 开放接口（需 API 鉴权）
domain/
  ├── entity/       # Entity 继承 BaseEntity
  ├── bo/           # 业务对象（入参）
  └── vo/           # 视图对象（出参）
service/            # 服务接口
mapper/             # MyBatis Mapper
manager/            # 复杂业务编排（事务边界）
```

### 权限码规范

- Admin: `chronic:{resource}:list/add/edit/remove/status`
- Doctor: `chronic:doctor:{resource}:*`
- Patient: `@SaCheckPermission("chronic:patient:{action}")`

## 常见问题 (FAQ)

### Q1: 如何进行患者档案建档？

A: 调用 PatientProfileController.add()，由 PatientProfileManager.createArchive() 完成：

1. 校验身份证唯一性
2. 创建基础档案
3. 初始化病种关联（如果有）
4. 创建时间线首节点

### Q2: 医生团队如何分配？

A: 在签约时通过 TeamManager.assignTeam() 自动分配或手动指定，支持按区域匹配。

### Q3: 双数据源如何配置？

A: 在 Nacos 配置中心定义 datasource.chronic 和 datasource.system-master，通过 DynamicDataSource 自动路由。

### Q4: 随访任务如何生成？

A: FollowupManager 根据管理计划自动生成随访任务，支持定时任务（SnailJob）调度触发。

### Q5: 如何扩展新的健康设备类型？

A: 1. 新增 ch_device_bind 记录；2. 实现 DeviceDataParser 解析数据；3. 存入 ch_device_raw_record；4. 转换后写入 ch_health_metric_record。

## 相关文件清单

### 核心文件

- `unimed-chronic-biz/src/main/java/org/dromara/chronic/UnimedChronicApplication.java` - 启动类
- `unimed-chronic-biz/src/main/resources/application.yml` - 应用配置
- `unimed-chronic-biz/pom.xml` - 模块依赖

### SQL 变更脚本

- `script/sql/update/chronic-*.sql` - 慢病模块 DDL 变更
- `script/sql/update/chronic-mock-data.sql` - 模拟数据

### 子模块

- `unimed-chronic-api/` - API 定义层
- `unimed-chronic-biz/` - 业务实现层

## 变更记录 (Changelog)

- **2026-04-22** - 初始化慢病模块文档
