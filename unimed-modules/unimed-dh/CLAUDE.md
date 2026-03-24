[根目录](../../CLAUDE.md) > [unimed-modules](../) > **unimed-dh**

# Unimed DH 数字人业务服务模块

## 变更记录 (Changelog)

- **2026-03-04（第三次更新）** - 模块已从骨架阶段全面成长为完整业务模块；补充全量控制器清单（B端12个 + C端8个）；新增资产管理体系（音色/素材/背景）；新增 C 端门户完整接口；更新数据模型（dh_background 新建、dh_material 新增字段、dh_topup_ticket 新增 payment_type）
- **2026-03-04 09:57:40** - 初始化文档：从原 unimed-dh 重构拆分，本模块为数字人业务核心层（dhcore 包名）

## 模块职责

数字人业务服务的核心业务层，包含完整的 B 端管理后台接口和 C 端门户接口。主要业务域：
- 订单生命周期：C 端下单 -> B 端领取 -> 生产制作 -> 交付下载
- 充值审核流程：C 端发起充值申请 -> B 端审核（确认/补充/驳回）
- 资产管理体系：系统级音色、素材、背景的增删改查
- C 端门户：独立注册/登录、钱包管理、创作资产选择
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

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `dh_order` | 订单表 | orderId, userId, status, ossVideoId |
| `dh_order_node` | 订单进度节点 | nodeId, orderId, nodeType, occurTime |
| `dh_topup_ticket` | 充值工单 | ticketId, userId, amount, status, payment_type(新增) |
| `dh_user_profile` | 用户档案 | userId, walletBalance, totalTopup, totalConsume, memberLevel |
| `dh_wallet_log` | 钱包流水 | logId, userId, amount, type |
| `dh_material` | 素材表 | materialId, userId, is_system(新增), sort_order(新增) |
| `dh_background` | 背景资源表（新建） | backgroundId, name, bg_type(IMAGE/VIDEO), oss_id, is_system, sort_order |
| `dh_voice` | 音色表 | voiceId, name, is_system |
| `dh_member_config` | 会员等级配置 | level, videoLimit, redoLimit |
| `dh_payment_price_config` | 充值价格档位 | id, amount, price |
| `dh_qr_upload_config` | 收款码配置 | id, type, qrImageIds, accountNo |
| `dh_sensitive_word` | 敏感词表 | id, word, status |
| `dh_notify_template` | 通知模板 | id, type, content, status |
| `dh_audit_log` | 口播审计日志 | logId, userId, action |

### 近期 DDL 变更

- `dh_material` 新增 `is_system tinyint(1)` 和 `sort_order int` 字段
- 新建 `dh_background` 表（背景资源，支持 IMAGE/VIDEO 类型）
- `dh_topup_ticket` 新增 `payment_type VARCHAR(20)`（WECHAT/ALIPAY/BANK_CARD）

相关脚本：
- `script/sql/update/dh-system-asset-management.sql`
- `script/sql/update/dh-topup-add-payment-type.sql`

## 测试与质量

当前无测试文件。B端接口可通过 Swagger 文档（端口 9206）进行手动测试。

## 常见问题 (FAQ)

### Q1: unimed-dh 和 unimed-dh-relay 的区别？
A: unimed-dh-relay 是纯 API 中转层，对接 Python 后端；unimed-dh 是业务逻辑层，含完整的 C 端门户和 B 端管理后台。

### Q2: 为什么包名是 dhcore 而不是 dh？
A: 为了与 unimed-dh-relay 的 `org.dromara.dh` 包名区分，本模块使用 `org.dromara.dhcore`。

### Q3: C 端充值流程？
A: C 端通过 PortalTopupController 提交充值申请（状态 PENDING），上传支付凭证；B 端通过 DhTopupController 审核（确认/要求补充/驳回）；确认后钱包余额增加。payment_type 字段记录支付方式（WECHAT/ALIPAY/BANK_CARD）。

### Q4: 如何新增系统级资产（音色/素材/背景）？
A: 通过 DhVoiceController / DhMaterialController / DhBackgroundController 的 POST 接口创建，`is_system=1` 表示系统资产对所有 C 端用户可见。

## 相关文件清单

- `src/main/java/org/dromara/dhcore/UnimedDhApplication.java` - 启动类
- `src/main/java/org/dromara/dhcore/controller/` - B端控制器（12个）
- `src/main/java/org/dromara/dhcore/controller/portal/` - C端门户控制器（8个）
- `src/main/java/org/dromara/dhcore/service/` - 服务接口
- `src/main/java/org/dromara/dhcore/service/impl/` - 服务实现
- `src/main/java/org/dromara/dhcore/domain/` - 实体/BO/VO/Convert
- `src/main/java/org/dromara/dhcore/mapper/` - MyBatis Mapper
- `script/sql/update/dh-system-asset-management.sql` - 资产管理 DDL
- `script/sql/update/dh-topup-add-payment-type.sql` - 充值支付类型 DDL
