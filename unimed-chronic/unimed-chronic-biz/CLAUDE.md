[根目录](../../CLAUDE.md) > [unimed-chronic](../) > **unimed-chronic-biz**

# Unimed Chronic Biz 慢病业务实现模块

## 变更记录 (Changelog)

- **2026-05-15** - 新增 PatientTagController/PatientTagDictController（患者标签管理）；LabTestController/MedicalExamController（检验/检查记录）；OcrController/DoctorOcrController/PatientOcrController（医疗文档OCR）；DoctorCustomGroupController（医生自定义分组）；PatientManagePlanController（患者端管理方案）；PatientContractController（患者端签约）；PatientConsentController（患者端知情同意）；PatientSosController（患者端SOS一键求助）；新增百度OCR SDK和微信小程序SDK依赖
- **2026-04-22** - 初始化慢病模块文档；双数据源架构；62个控制器覆盖4层管控（admin/doctor/patient/openapi）；55张业务表
- **2026-04-17** - 添加 unimed-chronic 慢病管理模块初始代码

## 模块职责

慢病管理业务实现层，基于"一个患者、一份档案、签约家庭医生、终身健康管理"的业务理念，提供慢病患者全生命周期健康管理服务。支持多层级管控：平台管理端（Admin）、医生端（Doctor）、患者端（Patient）、对外开放接口（OpenAPI）。

## 入口与启动

- **启动类**: `src/main/java/org/dromara/chronic/UnimedChronicApplication.java`
- **包名**: `org.dromara.chronic`
- **端口**: 9208
- **Nacos 服务名**: `unimed-chronic`
- **主配置**: `src/main/resources/application.yml`

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

### B 端管理后台接口 (controller/admin/) - 33 个

| 控制器 | 职责 | 权限前缀 |
|--------|------|----------|
| PatientProfileController | 患者档案管理 | chronic:patient:* |
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
| PatientTagController | 患者标签管理 | chronic:patientTag:* |
| PatientTagDictController | 患者标签字典管理 | chronic:patientTagDict:* |
| LabTestController | 检验记录管理 | chronic:lab-test:* |
| MedicalExamController | 检查记录管理 | chronic:medical-exam:* |
| OcrController | 医疗文档OCR管理 | chronic:medical-document-ocr:* |

### 医生端接口 (controller/doctor/) - 14 个

| 控制器 | 职责 |
|--------|------|
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
| DoctorCustomGroupController | 医生自定义分组 |
| DoctorOcrController | 医生端医疗文档OCR |

### 患者端接口 (controller/patient/) - 16 个

| 控制器 | 职责 |
|--------|------|
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
| PatientSosController | 紧急求助(SOS) |
| PatientManagePlanController | 患者端管理方案 |
| PatientContractController | 患者端签约 |
| PatientConsentController | 患者端知情同意 |
| PatientOcrController | 患者端医疗文档OCR |

### 开放接口 (controller/openapi/) - 8 个

| 控制器 | 职责 |
|--------|------|
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
unimed-common-idempotent    # 幂等性
unimed-common-job           # SnailJob 任务调度
unimed-common-excel         # Excel 导入导出
unimed-common-translation   # 国际化
unimed-common-sensitive     # 数据脱敏
unimed-common-encrypt       # 加密
unimed-common-service-impl  # 公共服务实现
unimed-api-system           # 系统服务远程接口
unimed-api-resource         # 资源服务远程接口
unimed-api-workflow         # 工作流远程接口
com.baidu.aip:java-sdk      # 百度OCR SDK (4.16.25)
weixin-java-miniapp         # 微信小程序 SDK (4.7.0)
```

### Dubbo 远程服务消费
- RemoteUserService - 用户服务
- RemoteDictService - 字典服务
- RemoteTenantService - 租户服务
- RemoteFileService - 文件服务（OSS）
- RemoteWorkflowService - 工作流服务

## 数据模型

### 核心业务表（55+ 张）
主要包含：患者档案、病种配置、筛查记录、管理计划、随访计划/任务/记录、健康指标、健康体检、生活方式、用药记录、预警规则/事件、风险评估、医生团队、服务签约、转诊记录、就诊记录、报告模板/实例、科普内容、审计日志、区域字典、消息会话、文件附件、患者账户、知情同意、档案共享、KPI定义、区域统计等。

### 数据源分布
- **慢病库（chronic）**: 业务表，存储慢病管理专属数据
- **系统库（system-master，只读）**: 读取用户、字典、租户等共享数据

## 服务层架构

### Manager 层复杂业务编排
| Manager | 职责 |
|---------|------|
| PatientProfileManager | 患者档案创建、完整生命周期处理 |
| TeamManager | 团队分配、医生管理 |
| FollowupManager | 随访计划生成、任务调度 |
| HealthExamManager | 体检报告聚合、异常检测 |
| ContractHistoryManager | 签约变更追溯 |
| ArchiveShareManager | 档案共享审核、权限控制 |
| OcrManager | OCR 任务识别与确认路由 |
| SosNotificationManager | SOS 紧急通知 |
| ScreeningManager | 筛查管理 |

### OCR 子系统
- **OcrController** (admin) / **DoctorOcrController** (doctor) / **PatientOcrController** (patient) - 三层 OCR 入口
- **OcrManager** - OCR 任务编排
- **OcrConfirmRouter** - 确认路由
- **OcrParser** - OCR 解析器
- **OcrDraftDataConverter** - 草稿数据转换
- **OcrArchiveMapper** - 档案映射
- **OcrMetricMapper** - 指标映射

## 测试与质量

### 测试文件
- `OcrParserTest.java` - OCR 解析器测试

## 编码规范

### 分层规范
```
controller/          # 按角色分包（admin/doctor/patient/openapi）
  ├── admin/        # @SaCheckPermission 权限控制
  ├── doctor/       # 医生端接口
  ├── patient/      # 患者端接口 (@SaCheckLogin)
  └── openapi/      # 开放接口（需 API 鉴权）
domain/
  ├── entity/       # Entity 继承 BaseEntity
  ├── bo/           # 业务对象（入参）
  └── vo/           # 视图对象（出参）
service/            # 服务接口
mapper/             # MyBatis Mapper
manager/            # 复杂业务编排（事务边界）
support/ocr/        # OCR 支持类
```

### 权限码规范
- Admin: `chronic:{resource}:list/add/edit/remove/status`
- Doctor: `chronic:doctor:{resource}:*`
- Patient: `@SaCheckLogin` + `LoginHelper.getUserId()`

## 常见问题 (FAQ)

### Q1: 如何进行患者档案建档？
A: 调用 PatientProfileController.add()，由 PatientProfileManager.createArchive() 完成。

### Q2: 双数据源如何配置？
A: 在 Nacos 配置中心定义 datasource.chronic 和 datasource.system-master，通过 DynamicDataSource 自动路由。

### Q3: 随访任务如何生成？
A: FollowupManager 根据管理计划自动生成随访任务，支持定时任务（SnailJob）调度触发。

### Q4: OCR 功能如何使用？
A: 通过 OcrController/DoctorOcrController/PatientOcrController 创建 OCR 任务，调用百度 OCR SDK 解析医疗文档，支持确认后自动写入健康指标/检验记录。

### Q5: 患者标签体系如何工作？
A: PatientTagDictController 管理标签字典（权威来源），PatientController 管理患者-标签绑定关系（ch_patient_tag.tag_code 引用字典）。

## 相关文件清单

### 核心文件
- `src/main/java/org/dromara/chronic/UnimedChronicApplication.java` - 启动类
- `src/main/resources/application.yml` - 应用配置
- `pom.xml` - 模块依赖

### SQL 变更脚本
- `script/sql/unimed-chronic.sql` - 慢病模块初始 DDL
- `script/sql/update/chronic-*.sql` - 慢病模块 DDL 变更
- `script/sql/update/chronic-mock-data.sql` - 模拟数据
- `script/sql/update/chronic-patient-tag-dict.sql` - 患者标签字典 DDL
