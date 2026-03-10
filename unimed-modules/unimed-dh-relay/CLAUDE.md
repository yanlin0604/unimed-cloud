[根目录](../../CLAUDE.md) > [unimed-modules](../) > **unimed-dh-relay**

# Unimed DH Relay 数字人中转服务模块

## 变更记录 (Changelog)

- **2026-03-04 09:57:40** - 重构后完整更新：确认为从原 unimed-dh 拆分的中转层，修正面包屑路径、服务名、端口说明；补充完整 API 接口列表、DTO 模型、测试文件清单
- **2025-12-16 09:30:24** - 初始化数字人模块文档，完成 API 接口和服务架构分析

## 模块职责

作为数字人服务的 **API 中转层**，负责接收外部系统请求并转发到后端 Python 数字人引擎。提供 WebRTC 实时通信代理、AI 对话中转、数字人配置管理、训练任务管理等功能，支持 API Key 鉴权和响应式编程。

## 入口与启动

### 启动类

- **文件**: `src/main/java/org/dromara/dh/UnimedDhRelayApplication.java`
- **端口**: 9205
- **Nacos 服务名**: `unimed-dh-relay`
- **特点**:
  - 使用 `@EnableDubbo` 启用 Dubbo 服务
  - 排除 `DataSourceAutoConfiguration`（无数据库依赖）
  - 集成 WebFlux + WebClient 响应式 HTTP 客户端
  - 集成 Sentinel 熔断限流

### 配置文件

- **主配置**: `src/main/resources/application.yml`
- **Nacos 配置**: `script/config/nacos/unimed-dh-relay.yml`
- **关键配置项**:
  - `digital-human.python-api.base-url` - 数字人配置服务地址（:8011）
  - `digital-human.webrtc-api.base-url` - WebRTC 服务地址（:8010）
  - `digital-human.list-api.base-url` - 数字人列表服务地址（:8009）
  - `digital-human.api-filter.enabled` - API Token 过滤器开关
  - `digital-human.circuit-breaker` - 熔断器配置
  - `digital-human.rate-limiter` - 限流配置
  - `digital-human.cache` - 缓存配置

## 对外接口

### 外部 API 控制器 (`ExternalApiController`)

- **基路径**: `/api/v1/dh/external`
- **认证方式**: API Key（Bearer Token），通过 `ApiKeyAuthFilter` 校验
- **返回风格**: 响应式 `Mono<R<T>>` 封装

#### 数字人配置管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/digital-humans/config` | 保存数字人配置 |

#### WebRTC 通信

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/digital-humans/webrtc/offer` | 建立 WebRTC 连接（SDP Offer） |
| GET | `/digital-humans/webrtc/status` | 获取 WebRTC 连接状态统计 |

#### AI 对话

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/digital-humans/chat` | 发送文本消息（直播播报/AI 对话） |
| POST | `/digital-humans/interrupt` | 打断数字人当前说话 |
| POST | `/digital-humans/speaking-status` | 查询数字人说话状态 |

#### 数字人形象

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/digital-humans/avatars` | 获取数字人形象列表（含预览图完整URL） |

#### 数字人管理

| 方法 | 路径 | 说明 |
|------|------|------|
| DELETE | `/digital-humans/{digitalHumanId}` | 删除数字人 |

#### 训练任务

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/digital-humans/upload-and-train` | 启动训练任务 |
| GET | `/digital-humans/training/progress/{taskId}` | 查询训练进度 |

#### TTS 音色试听

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/digital-humans/preview-tts` | TTS 音色试听（透传至后端 /preview_tts，返回 base64 MP3） |

## 关键依赖与配置

### 核心依赖

```xml
unimed-common-nacos          -- Nacos 服务发现与配置
unimed-common-log            -- 操作日志
unimed-common-service-impl   -- 公共服务实现
unimed-common-doc            -- API 文档
unimed-common-web            -- Web 框架
unimed-common-security       -- 安全框架
unimed-common-dubbo          -- Dubbo RPC
unimed-common-redis          -- Redis 缓存
unimed-api-auth              -- 认证服务 API（Token 校验）
spring-boot-starter-webflux  -- 响应式 WebClient
spring-cloud-starter-alibaba-sentinel -- 熔断限流
```

### 外部服务集成（Python 后端）

- **配置服务**: `http://192.168.2.43:8011` - 数字人配置接口
- **WebRTC 服务**: `http://192.168.2.43:8010` - WebRTC 信令服务
- **列表服务**: `http://192.168.1.35:8009` - 数字人列表查询

## 数据模型

### 请求/响应 DTO（`domain/dto/`）

| 类名 | 用途 |
|------|------|
| DhConfigRequest / DhConfigResponse | 数字人配置 |
| DhServiceRequest | 通用服务请求 |
| WebRtcOfferRequest / WebRtcOfferResponse | WebRTC SDP Offer |
| WebRtcStatusResponse | WebRTC 连接状态 |
| ChatRequest / ChatResponse | AI 对话消息 |
| InterruptRequest | 打断说话 |
| SpeakingStatusRequest / SpeakingStatusResponse | 说话状态查询 |
| DigitalHumanListRequest / DigitalHumanListResponse | 数字人列表 |
| DigitalHumanInfo | 数字人信息 |
| GetAvatarsResponse | Python /get_avatars 原始响应 |
| AvatarInfo | 数字人形象信息（含完整预览图URL） |
| DigitalHumanDeleteRequest / DigitalHumanDeleteResponse | 删除数字人 |
| VideoUploadTrainRequest / VideoUploadTrainResponse | 视频上传训练 |
| TrainingProgressRequest / TrainingProgressResponse | 训练进度 |
| StatusUpdateRequest / StatusUpdateResponse | 状态更新 |
| PreviewTtsRequest / PreviewTtsResponse | TTS 音色试听 |

### 视图对象（`domain/vo/`）

| 类名 | 用途 |
|------|------|
| TokenValidationVo | Token 验证结果 |

## 服务层

### 接口

| 接口 | 实现 | 职责 |
|------|------|------|
| IDigitalHumanApiService | DigitalHumanApiServiceImpl | 数字人 CRUD、训练任务管理 |
| IWebRtcService | WebRtcServiceImpl | WebRTC 连接建立与状态管理 |
| IChatService | ChatServiceImpl | 文本对话、打断控制、状态查询 |
| IAuthClientService | AuthClientServiceImpl | API Token 验证（Dubbo 调用 auth 服务） |

### 配置类

| 类名 | 职责 |
|------|------|
| DhSecurityConfiguration | 安全配置、接口放行 |
| DhWebConfiguration | Web 配置 |
| WebClientConfig | WebClient 连接池和超时配置 |

### 过滤器

| 类名 | 职责 |
|------|------|
| ApiKeyAuthFilter | API Key 认证过滤器，拦截 `/api/v1/dh/external/**` |

### 工具类

| 类名 | 职责 |
|------|------|
| ApiFormatConverter | API 格式转换工具 |

## 测试与质量

### 测试文件

- `src/test/java/org/dromara/dh/service/WebRtcServiceTest.java` - WebRTC 服务测试
- `src/test/java/org/dromara/dh/service/ChatServiceTest.java` - 对话服务测试
- `src/test/java/org/dromara/dh/service/DigitalHumanDeleteServiceTest.java` - 删除服务测试

### 质量工具

- **API 文档**: SpringDoc OpenAPI 自动生成
- **操作日志**: `@Log` 注解
- **熔断限流**: Sentinel

## 常见问题 (FAQ)

### Q1: 如何配置 API Key 认证？

A: 在 Nacos 的 `unimed-dh-relay.yml` 中配置 `digital-human.api-filter` 部分，通过 auth 模块管理 API Token。

### Q2: WebRTC 连接失败如何处理？

A: 检查后端 Python 服务地址是否可达（`digital-human.webrtc-api.base-url`），确认防火墙和 STUN/TURN 配置。

### Q3: 如何新增数字人 API 转发？

A: 在 ExternalApiController 添加新接口，在对应 Service 中使用 WebClient 调用后端 Python 服务。

### Q4: Mono 响应式编程注意事项？

A: 所有 WebClient 调用返回 Mono，不要在 Mono 链中使用阻塞操作（如 `.block()`），使用 `.map()` / `.flatMap()` 进行链式处理。

## 相关文件清单

### 核心文件

- `src/main/java/org/dromara/dh/UnimedDhRelayApplication.java` - 启动类
- `src/main/java/org/dromara/dh/controller/ExternalApiController.java` - 外部 API 控制器（8 个接口）
- `src/main/java/org/dromara/dh/service/` - 4 个服务接口
- `src/main/java/org/dromara/dh/service/impl/` - 4 个服务实现
- `src/main/java/org/dromara/dh/domain/dto/` - 20+ 个 DTO 类
- `src/main/java/org/dromara/dh/filter/ApiKeyAuthFilter.java` - API 鉴权过滤器
- `src/main/java/org/dromara/dh/config/` - 3 个配置类
- `src/main/resources/application.yml` - 应用配置
- `Dockerfile` - Docker 构建文件（暴露端口 9205）
