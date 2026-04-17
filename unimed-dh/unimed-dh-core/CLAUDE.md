[根目录](../../CLAUDE.md) > [unimed-dh](../) > **unimed-dh-core**

# Unimed DH Core 数字人业务服务模块

## 变更记录 (Changelog)

- **2026-04-07** - 方言采集模块上线：新增 DhDialectPromptController、DhDialectRecordController、DhDialectInviteController 三个 B 端控制器 + PortalDialectController C 端入口；3 张新建表（dh_dialect_prompt/dh_dialect_record/dh_dialect_invite）；支持匿名提交、录音文件上传（通过 RemoteFileService）、邀请码管理、批量导入排序、动态租户（Portal 租户回退）
- **2026-03-04（第三次更新）** - 模块已从骨架阶段全面成长为完整业务模块；补充全量控制器清单、资产管理体系（音色/素材/背景）、C 端门户完整接口
- **2026-03-04 09:57:40** - 初始化文档：从原 unimed-dh 重构拆分，本模块为数字人业务核心层（dhcore 包名）

## 模块职责

数字人业务服务的核心业务层，包含完整的 B 端管理后台接口和 C 端门户接口。主要业务域：
- 订单生命周期：C 端下单 -> B 端领取 -> 生产制作 -> 交付下载
- 充值审核流程：C 端发起充值申请 -> B 端审核（确认/补充/驳回）
- 资产管理体系：系统级音色、素材、背景的增删改查
- 方言采集管理：提示文字管理、录音审核、邀请码配置、C 端匿名提交
- C 端门户：独立注册/登录、钱包管理、创作资产选择、方言采集
- 配置管理：会员等级、支付价格、视频上传、收款码、敏感词、通知模板

## 入口与启动

- 启动类: `src/main/java/org/dromara/dhcore/UnimedDhApplication.java`
- 包名: `org.dromara.dhcore`（与 relay 模块的 `org.dromara.dh` 区分）
- 端口: 9206
- Nacos 服务名: `unimed-dh`
- 主配置: `src/main/resources/application.yml`

## 对外接口

### B 端管理接口（需 Sa-Token 权限）

| 控制器 | 基路径 | 主要接口 | 权限前缀 |
|--------|--------|---------|----------|
| DhDashboardController | `/dh/dashboard` | GET `/metrics?range=` 看板指标 | `dh:dashboard` |
| DhOrderController | `/dh/order` | 分页查询/详情/领取/取消/拒绝 | `dh:order` |
| DhProductionController | `/dh/production` | 开始制作/资产查询/保存元信息/上传视频/提交交付 | `dh:order:produce` |
| DhTopupController | `/dh/topup` | 分页查询/确认到账/标记需补充/驳回 | `dh:topup` |
| DhFinanceController | `/dh/finance` | GET `/summary` 财务汇总，GET `/detail/list` 财务明细 | `dh:finance` |
| DhAuditController | `/dh/audit` | GET `/list` 分页查询口播审计日志 | `dh:audit` |
| DhVoiceController | `/dh/voice` | 系统音色增删改查、切换状态 | `dh:voice` |
| DhMaterialController | `/dh/material` | 系统素材增删改查、切换状态 | `dh:material` |
| DhBackgroundController | `/dh/background` | 背景资源增删改查、切换状态 | `dh:background` |
| DhConfigController | `/dh/config` | 会员配置/价格配置/视频上传配置/收款码/敏感词/通知模板 | `dh:config` |
| DhUserController | `/dh/user` | 用户管理（B端） | `dh:user` |
| DhReportController | `/dh/report` | 报表统计 | `dh:report` |
| DhDialectPromptController | `/dh/dialect` | 提示文字增删改查、状态切换、批量导入、排序调整 | `dh:dialect` |
| DhDialectRecordController | `/dh/dialectRecord` | 录音记录分页查询、审核、删除、导出 | `dh:dialectRecord` |
| DhDialectInviteController | `/dh/dialectInvite` | 邀请码配置分页查询/新增/编辑/删除 | `dh:dialectInvite` |

### C 端门户接口（@SaCheckLogin 或公开）

| 控制器 | 基路径 | 主要接口 |
|--------|--------|----------|
| PortalAuthController | `/dh/portal/auth` | POST `/sms/code`、`/login/sms`、`/login/password`；GET `/profile` |
| PortalMemberController | `/dh/portal/member` | GET `/config` 会员配置 |
| PortalWalletController | `/dh/portal/wallet` | GET `/balance` 余额，GET `/log/list` 流水 |
| PortalTopupController | `/dh/portal/topup` | GET `/plans` 充值档位，GET `/channels` 支付渠道，POST `/apply` 提交申请，POST `/supplement` 补充凭证，GET `/records` 历史记录 |
| PortalOrderController | `/dh/portal/order` | POST 下单，GET `/list` 列表，GET `/{orderId}` 详情，GET `/{orderId}/download` 下载，DELETE `/{orderId}` 删除 |
| PortalCreationController | `/dh/portal/creation` | GET `/voices` 可用音色，GET `/materials` 可用素材，GET `/backgrounds` 可用背景 |
| PortalVoiceCloneController | `/dh/portal/voice-clone` | 声音克隆相关接口 |
| PortalUserController | `/dh/portal/user` | C端用户信息管理 |
| PortalDialectController | `/dh/portal/dialect` | GET `/prompts` 获取提示文字列表（随机打乱），POST `/upload` 上传录音文件，POST `/record` 提交录音记录 |

## 关键依赖与配置

```
unimed-common-core       -- 核心工具
unimed-common-web        -- Web 框架
unimed-common-security   -- Sa-Token 安全
unimed-common-mybatis    -- MyBatis-Plus
unimed-common-redis      -- Redis 缓存
unimed-common-nacos      -- Nacos 配置
unimed-common-log        -- 操作日志
unimed-common-doc        -- API 文档
unimed-api-resource      -- 远程文件服务（Dubbo）
```

### 与 relay 模块的关系

```
C端请求 --> unimed-dh (9206, 业务逻辑/C端门户)
外部系统 --> unimed-dh-relay (9205, API中转)
              |
              +--> Python 数字人引擎（WebRTC/配置/训练）
```

## 数据模型

### 核心实体表（15+12=27 张）

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `dh_order` | 订单表 | orderId, userId, status, ossVideoId |
| `dh_order_node` | 订单进度节点 | nodeId, orderId, nodeType, occurTime |
| `dh_order_material` | 订单素材关联 | orderId, materialId |
| `dh_order_process_log` | 生产日志 | logId, orderId, action |
| `dh_order_production_asset` | 生产资产 | assetId, orderId, assetType |
| `dh_order_qc_snapshot` | 质检快照 | snapshotId, orderId |
| `dh_topup_ticket` | 充值工单 | ticketId, userId, amount, status, payment_type |
| `dh_user_profile` | 用户档案 | userId, walletBalance, totalTopup, totalConsume, memberLevel |
| `dh_wallet_log` | 钱包流水 | logId, userId, amount, type |
| `dh_material` | 素材表 | materialId, userId, is_system, sort_order |
| `dh_background` | 背景资源表 | backgroundId, name, bg_type(IMAGE/VIDEO), oss_id, is_system |
| `dh_voice` | 音色表 | voiceId, name, is_system |
| `dh_avatar` | 头像表 | avatarId, name, oss_id |
| `dh_audit_log` | 口播审计日志 | logId, userId, action |
| `dh_dialect_prompt` | 方言提示文字 | promptId, content, status, sort_order |
| `dh_dialect_record` | 方言录音记录 | recordId, promptId, userId, inviteCode, audioUrl, ossId, duration, audit_status |
| `dh_dialect_invite` | 方言邀请码 | inviteId, inviteCode, maxUses, usedCount, expireTime |
| `dh_member_config` | 会员等级配置 | level, videoLimit, redoLimit |
| `dh_payment_price_config` | 充值价格档位 | id, amount, price |
| `dh_qr_upload_config` | 收款码配置 | id, type, qrImageIds, accountNo |
| `dh_sensitive_word` | 敏感词表 | id, word, status |
| `dh_notify_template` | 通知模板 | id, type, content, status |
| `dh_video_upload_config` | 视频上传配置 | id, title, maxSize |
| `dh_report_ticket` | 举报工单 | ticketId, orderId, reason |

### 近期 DDL 变更

- **2026-04-07 方言采集模块**
  - 新建 `dh_dialect_prompt` 表（提示文字：content, status, sort_order）
  - 新建 `dh_dialect_record` 表（录音记录：promptId, userId, inviteCode, audioUrl, ossId, duration, audit_status）
  - 新建 `dh_dialect_invite` 表（邀请码配置：inviteCode, maxUses, usedCount, expireTime）
  - 相关脚本：
    - `script/sql/update/dh-dialect-collection.sql`
    - `script/sql/update/dh-dialect-anonymous-submit.sql`
    - `script/sql/update/dh-dialect-record-tenant-backfill.sql`
    - `script/sql/update/dh-dialect-invite-link.sql`
    - `script/sql/update/dh-dialect-sort-order.sql`

- **2026-03-04 资产管理模块**
  - `dh_material` 新增 `is_system tinyint(1)` 和 `sort_order int` 字段
  - 新建 `dh_background` 表（背景资源，支持 IMAGE/VIDEO 类型）
  - `dh_topup_ticket` 新增 `payment_type VARCHAR(20)`
  - 相关脚本：
    - `script/sql/update/dh-system-asset-management.sql`
    - `script/sql/update/dh-topup-add-payment-type.sql`

### 数据层统计
- **Mapper**: 23 个 MyBatis Mapper 接口
- **实体类**: 核心 domain/ 下 19+ 个实体
- **BO/VO**: 100+ 个业务对象/视图对象
- **转换器**: DhConvertUtils + MapStruct 转换器

## 服务层

### 主要服务接口
| 接口 | 职责 |
|------|------|
| IDhOrderService | 订单管理（领取/取消/拒绝/质检） |
| IDhTopupService | 充值工单审核 |
| IDhProductionService | 生产制作流程 |
| IDhConfigService | 配置管理 |
| IDhFinanceService | 财务汇总/明细 |
| IDhDashboardService | 看板指标统计 |
| IDhAuditService | 审计日志查询 |
| IDhDialectPromptService | 提示文字管理（含批量导入/排序） |
| IDhDialectRecordService | 录音记录管理（含审核/导出） |
| IDhDialectInviteService | 邀请码配置管理 |

### 方言采集服务特性
- **匿名提交**: PortalDialectController 支持未登录用户提交录音（userId 为 null）
- **动态租户**: `executeWithPortalTenant()` 方法动态设置租户上下文，默认回退到 DEFAULT_TENANT_ID
- **文件上传**: 通过 Dubbo 调用 `RemoteFileService.upload()` 上传至 OSS，返回 ossId 和 URL
- **随机展示**: SHUFFLE_PROMPTS=true，每次获取提示文字列表时随机打乱顺序

## 测试与质量

当前无测试文件。B端接口可通过 Swagger 文档（端口 9206）进行手动测试。

## 常见问题 (FAQ)

### Q1: unimed-dh 和 unimed-dh-relay 的区别？
A: unimed-dh-relay 是纯 API 中转层，对接 Python 后端；unimed-dh 是业务逻辑层，含完整的 C 端门户和 B 端管理后台。

### Q2: 为什么包名是 dhcore 而不是 dh？
A: 为了与 unimed-dh-relay 的 `org.dromara.dh` 包名区分，本模块使用 `org.dromara.dhcore`。

### Q3: C 端充值流程？
A: C 端通过 PortalTopupController 提交充值申请（状态 PENDING），上传支付凭证；B 端通过 DhTopupController 审核（确认/要求补充/驳回）；确认后钱包余额增加。

### Q4: 如何新增系统级资产（音色/素材/背景）？
A: 通过 DhVoiceController / DhMaterialController / DhBackgroundController 的 POST 接口创建，`is_system=1` 表示系统资产对所有 C 端用户可见。

### Q5: 方言采集的匿名提交是如何实现的？
A: PortalDialectController 通过 `LoginHelper.isLogin()` 判断，未登录时 userId 为 null，租户上下文通过动态租户机制回退到默认租户。录音提交后状态为 PENDING，需 B 端审核。

### Q6: 方言提示文字的排序机制是什么？
A: `sort_order` 字段控制排序，DhDialectPromptController 提供 `/sort` 接口支持上移/下移调整，通过相邻记录交换 sort_order 实现。

## 相关文件清单

### 核心文件
- `src/main/java/org/dromara/dhcore/UnimedDhApplication.java` - 启动类
- `src/main/java/org/dromara/dhcore/controller/` - B端控制器（15个，含方言采集3个）
- `src/main/java/org/dromara/dhcore/controller/portal/` - C端门户控制器（9个，含方言采集1个）
- `src/main/java/org/dromara/dhcore/service/` - 服务接口
- `src/main/java/org/dromara/dhcore/service/impl/` - 服务实现
- `src/main/java/org/dromara/dhcore/domain/` - 实体/BO/VO/Convert
- `src/main/java/org/dromara/dhcore/mapper/` - MyBatis Mapper（23个）
- `src/main/java/org/dromara/dhcore/domain/entity/` - 方言采集实体（DhDialectRecord/DhDialectPrompt/DhDialectInvite）
- `script/sql/update/dh-dialect-*.sql` - 方言采集 DDL 变更脚本（5个文件）
- `script/sql/update/dh-system-asset-management.sql` - 资产管理 DDL
