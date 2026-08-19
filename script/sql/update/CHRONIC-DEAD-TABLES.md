# 慢病库表活跃度盘点与清理清单

> 盘点时间：2026-08-19
> 盘点范围：`unimed-chronic` 库全部 77 张表
> 状态：**仅盘点，未执行任何清理**。清理脚本见 `chronic-drop-dead-tables.sql`（默认不执行）
> 复验命令：`python script/sql/update/_check_dead_tables.py`

## 一、盘点方法

只看"表里有没有数据"会误判——演示数据是手工塞的，有数据的表也可能是废表（`ch_sos_record` 就是典型）。因此按六个维度交叉：

| 维度 | 判据 |
|------|------|
| **D** DDL | `script/sql/unimed-chronic.sql` 的 `CREATE TABLE` |
| **E** 实体 | `domain/entity/` 中的 `@TableName("xxx")` |
| **M** Mapper | `mapper/` 中 `BaseMapperPlus<实体, VO>` |
| **U** 被引用 | Mapper 类名是否出现在 `service` / `manager` / `job` / `controller` / `support` |
| **R** 行数 | 线上 `COUNT(*)` |
| **W** 有无写入 | Mapper 字段上是否存在 `insert` / `update` / `delete` 调用 |

**W 是关键维度**：只有查询没有写入 = 表永远是空的 = 功能等于不存在。

> ⚠️ 脚本输出的"读/写次数"仅作线索，**不是精确值**——字段名正则会命中 `baseMapper` 这类通用名导致过度计数（表现为多张表出现一模一样的 56/34）。可靠信号只有三个：**表名零引用、线上行数、有无实体/Mapper**。任何结论都必须回代码人工确认。

## 二、盘点结果总览

DDL 77 张 / 线上 77 张，**完全一致，无野表**。

数据库层依赖检查（2026-08-19 实测，只读）：整库 **0 个外键、0 个视图、0 个触发器、0 个存储过程** —— 因此不存在"表被 DB 对象隐式依赖"的情况，唯一的依赖来源就是 Java 代码。

| 分类 | 张数 | 处置建议 |
|------|------|----------|
| 在用 | 59 | 保留 |
| **纯建表（代码零引用）** | **8** | **可清理** |
| 只读不写（字典/配置表，设计如此） | 4 | 保留 |
| 只读不写（业务表，异常） | 1 | 见第四节，需产品决策 |
| 代码齐全但线上无数据 | 5 | 保留，但需验证功能可跑通 |

## 三、可清理的 8 张表（代码零引用）

这 8 张表的表名在整个 `unimed-chronic-biz` 的 java / xml 里**一次都没出现**，删除对代码零影响。

| 表 | 行数 | 为什么是废表（已逐一回代码确认） |
|----|------|----------------------------------|
| `ch_sos_record` | 4 | **SOS 改用 `ch_warning_event` 实现**。`PatientSosController` 调 `warningEventService.createEvent()` + `SosNotificationManager` 通知，全程不碰此表。4 行是手工塞的演示数据 |
| `ch_webhook_subscription` | 3 | `OpenapiWebhookController` 有 `/webhook/subscribe` 端点，但只注入 `IChReferralService`——**订阅请求收下了不落库**，订阅能力实际不存在 |
| `ch_ops_health_check` | 0 | `OpsController./ops/health-check` 实时计算，不落库 |
| `ch_ops_rerun_ticket` | 0 | `/ops/task-rerun` 走 `RemoteWorkflowService` 审批流，不落库 |
| `ch_stat_disease_day` | 9 | 统计日表族 5 张里**只有 `ch_stat_area_day` 接了代码**（`DashboardManager`），其余 4 张无实体无 Mapper |
| `ch_stat_followup_day` | 4 | 同上 |
| `ch_stat_org_day` | 5 | 同上 |
| `ch_stat_warning_day` | 6 | 同上 |

### 与看板问题的关联

统计表族的废弃不是孤立现象，它和「4 个看板页全是硬编码假数据」是同一个根因链：

```
5 张统计日表  →  4 张无代码 + ch_stat_area_day 仅 10 行（2026-04-26~28 演示数据）
      ↑
填充它们的「统计日报 23:50」定时任务（chronic-snailjob-jobs.sql）
      ↑
SnailJob 服务端未部署 → 任务从未运行
      ↓
DashboardManager.bigScreenSummary() 只能返回 5 个字段且值是 4 个月前的
      ↓
前端 4 个看板页干脆不调接口，全部写死假数据
```

**清理统计表前必须先定方向**：如果后续要做真实看板，`ch_stat_disease_day` / `ch_stat_followup_day` / `ch_stat_org_day` / `ch_stat_warning_day` 是**预留的设计**而非垃圾，删了将来还得重建。建议：

- 若走**预聚合**方案（部署 SnailJob + 跑统计任务）→ **保留 4 张统计表**，只清理 `ch_sos_record` / `ch_webhook_subscription` / `ch_ops_*` 这 4 张
- 若走**实时 count 查询**方案（不要预聚合）→ 5 张统计表全部可清理（含 `ch_stat_area_day`，但它有代码，需同步改 `DashboardManager`）

## 四、`ch_clinical_pathway_status`：有接口没写入（需产品决策）

**这张表不能简单删，也不能简单留** —— 它有完整的代码骨架但功能没做完。

### 现状

```
表      ch_clinical_pathway_status（0 行）
实体    ChClinicalPathwayStatus（含 milestoneJson 字段）
Mapper  ChClinicalPathwayStatusMapper
Service IClinicalPathwayService / ClinicalPathwayServiceImpl
VO      PathwayProgressVo（含 StageInfo 列表）
端点    GET /chronic/doctor/patient/{patientId}/pathway-progress
权限    chronic:doctor:patient:pathway（菜单 200905 已承载）
```

`pathwayMapper` 在全仓**只有一处 `selectOne`，零写入调用** → 表永远是空的 → 该接口永远返回 `stages: []` 的空进度。

### 判定：不是"漏了一行"，是"没做完"，且做的那部分还与设计不一致

三方阶段定义互相冲突：

| 维度 | 设计文档 §2.29 | DDL 表注释 | 后端 `STANDARD_STAGES` |
|------|----------------|-----------|------------------------|
| 阶段数 | **5** | 举例 4 个 | **4** |
| 枚举 | `ARCHIVE`/`ASSESS`/**`SIGN`**/`FOLLOWUP`/`ACHIEVE` | `SCREENING`/`FIRST_EVAL`/`PLAN_EXECUTING`/`RE_EVAL` | `SCREENING`/`FIRST_EVAL`/`PLAN_EXECUTING`/`RE_EVAL` |
| 归属层 | admin 菜单 `chronic:pathway:list` | — | doctor 层 `chronic:doctor:patient:pathway` |

**设计里的"③签约"阶段在实现中整个消失了** —— 而签约是慢病管理的核心环节（一个患者、一份档案、签约家庭医生），漏掉很可疑。

### 完整缺口清单

1. **零阶段推进逻辑**（最核心）—— 4 个业务事件都没有回写路径阶段：
   - `SCREENING` ← 应由筛查入组 / 建档触发（`ScreeningManager.enroll()` / `PatientProfileManager.createArchive()`）
   - `FIRST_EVAL` ← 应由风险评估 + 管理定级触发（`RiskAssessmentService` / `ChManageLevelRecord`）
   - `PLAN_EXECUTING` ← 应由管理方案生效 / 随访任务生成触发（`ManagePlanService` / `FollowupManager`）
   - `RE_EVAL` ← 应由周期再评估触发
2. **`milestone_json` 完全没用** —— 设计里泳道卡片要显示"完成于 01-15 / 评估分 85 / 服务包 高级 / 随访 6/10"，这些明细本该存在此字段，实体有字段、服务层不读不写
3. **VO 缺 3 个设计要求的字段** —— `stageProgress`（当前阶段进度 %）、`planName`（关联方案）、`responsibleDoctor`（责任医生）
4. **无列表接口** —— 设计要"左侧患者列表 + 阶段筛选"，后端只有按单个 `patientId` 查
5. **admin 层缺失** —— 设计规划为 admin 菜单，实现只落在 doctor 层

### 三个选项

| 选项 | 工作量 | 说明 |
|------|--------|------|
| **A. 做完** | 大 | 先统一阶段枚举（建议采纳设计的 5 阶段含签约），再在 4~5 个业务 Manager 里加 `advanceStage()` 调用（或改用领域事件解耦），补 `milestone_json` 写入、补 VO 字段、补 admin 列表接口。前端泳道 UI 骨架可从 `git show a6bb949b:apps/web-antd/src/views/assessment/pathway/` 取回 |
| **B. 砍掉** | 小 | 删表 + 删实体/Mapper/Service/VO/端点 + 删菜单 200905 + 删前端空态页。理由：4 个阶段的信息其实已分散存在于 `ch_screening_record`（筛查）、`ch_risk_assessment`（评估）、`ch_patient_contract`（签约）、`ch_followup_task`（随访）、`ch_patient_timeline`（时间线）里，路径表属于冗余聚合层 |
| **C. 维持现状** | 0 | 留着空表和空接口。风险：医生端如果有页面调它，永远显示空进度，看起来像 bug |

**倾向 B**：`ch_patient_timeline`（患者时间线，10 行、在用）已经承担了"患者经历了哪些环节"的职责，路径表与它职责重叠。若只是想要泳道可视化，基于 timeline + 各业务表实时算比维护一张同步状态表更不容易失真。但这是产品决策，需你定。

## 五、代码齐全但线上无数据的 5 张（保留，建议验证）

| 表 | 说明 |
|----|------|
| `ch_doctor_custom_group` | 医生端自定义分组，管理端是空态页（后端仅 doctor 层） |
| `ch_doctor_group_member` | 同上，且后端**只写不读**（无成员查询端点） |
| `ch_encounter_diagnosis` | 诊疗诊断明细，演示数据只有 2 条诊疗记录且未填诊断 |
| `ch_ocr_draft` | OCR 草稿（3 个实体共用此表：`ChOcrArchiveDraft` / `ChOcrReportItem` / `ChOcrMetricItem`） |
| `ch_patient_close_apply` | 结案申请，前端有页面有按钮，**建议造一条数据端到端验证** |

## 六、只读不写但属正常的 4 张

`ch_area_dict`(13) / `ch_icd_dict`(9) / `ch_kpi_definition`(5) / `ch_org_area_mapping`(5)

均为字典/配置表，靠 SQL 脚本维护。后端刻意只提供查询（如 `AreaController` 只有 `chronic:area:query`，无增删改），设计如此，**不是缺陷**。
