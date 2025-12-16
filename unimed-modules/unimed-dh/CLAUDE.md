[根目录](../../CLAUDE.md) > [unimed-modules](../) > **unimed-dh**

# Unimed DH 数字人服务模块

## 模块职责

提供数字人配置管理、WebRTC 实时通信、AI 对话服务等数字人相关功能，支持外部系统集成和 API 密钥认证。

## 入口与启动

### 启动类
- **文件**: `src/main/java/org/dromara/dh/UnimedDhApplication.java`
- **端口**: 9205
- **特点**:
  - 本地版本（无 Nacos 依赖）
  - 使用 `@EnableDubbo` 启用 Dubbo 服务
  - 排除 `DataSourceAutoConfiguration`（无数据库依赖）
  - 支持 WebRTC 实时通信

### 配置文件
- **主配置**: `src/main/resources/application.yml`
- **运行模式**: 本地模式，独立部署
- **关键配置**:
  ```yaml
  server:
    port: 9205
  spring:
    application:
      name: unimed-dh
  ```

## 对外接口

### 外部 API 控制器 (`ExternalApiController`)

#### 数字人配置管理
- **路径**: `/api/v1/dh/external`
- **认证方式**: API Key 鉴权
- **主要接口**:
  - `POST /config` - 保存数字人配置
  - `GET /config/{id}` - 获取数字人配置
  - `PUT /config/{id}` - 更新数字人配置
  - `DELETE /config/{id}` - 删除数字人配置
  - `GET /config/list` - 获取配置列表

#### WebRTC 通信
- `POST /webrtc/offer` - WebRTC Offer 处理
- `POST /webrtc/answer` - WebRTC Answer 处理
- `POST /webrtc/candidate` - ICE Candidate 处理
- `DELETE /webrtc/session/{sessionId}` - 关闭会话

#### AI 对话服务
- `POST /chat/send` - 发送对话消息
- `GET /chat/history/{sessionId}` - 获取对话历史
- `DELETE /chat/history/{sessionId}` - 清空对话历史
- `POST /chat/voice` - 语音消息处理

#### 视频处理
- `POST /video/upload` - 视频文件上传
- `GET /video/{id}/stream` - 视频流获取
- `POST /video/train` - 数字人训练
- `GET /video/train/status/{taskId}` - 训练状态查询

### 内部管理接口

#### 数字人管理
- **路径**: `/dh/internal`
- **功能**: 内部管理和监控接口
- **接口**:
  - `GET /health` - 健康检查
  - `GET /metrics` - 性能指标
  - `GET /sessions` - 活跃会话列表

## 关键依赖与配置

### 核心依赖
```xml
<!-- Web 相关 -->
<dependency>
    <groupId>org.dromara</groupId>
    <artifactId>unimed-common-web</artifactId>
</dependency>

<!-- WebRTC 支持 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>

<!-- API 文档 -->
<dependency>
    <groupId>org.dromara</groupId>
    <artifactId>unimed-common-doc</artifactId>
</dependency>
```

### 外部服务集成
- **视频处理服务**: FFmpeg 集成
- **AI 对话服务**: 第三方 AI API
- **存储服务**: 对象存储集成
- **推送服务**: WebSocket 实时推送

## 数据模型

### 请求对象 (DTO)
- **DigitalHumanConfigDto**: 数字人配置
  ```java
  public class DigitalHumanConfigDto {
      private Long id;
      private String name;
      private String avatar;
      private String voice;
      private String personality;
      private String background;
      private Map<String, Object> customSettings;
  }
  ```

- **WebRtcOfferDto**: WebRTC Offer
- **WebRtcAnswerDto**: WebRTC Answer
- **IceCandidateDto**: ICE Candidate
- **ChatMessageDto**: 对话消息
- **VideoUploadDto**: 视频上传
- **TrainingTaskDto**: 训练任务

### 响应对象 (VO)
- **DigitalHumanVo**: 数字人视图对象
- **SessionVo**: 会话信息
- **ChatHistoryVo**: 对话历史
- **TrainingStatusVo**: 训练状态

### 枚举类型
- **SessionStatus**: 会话状态 (CONNECTING, CONNECTED, DISCONNECTED)
- **TrainingStatus**: 训练状态 (PENDING, PROCESSING, COMPLETED, FAILED)
- **MessageType**: 消息类型 (TEXT, VOICE, IMAGE, VIDEO)

## 测试与质量

### 测试策略
- **单元测试**: WebRTC 信令逻辑测试
- **集成测试**: API 接口测试
- **性能测试**: 并发会话压力测试
- **兼容性测试**: 浏览器 WebRTC 兼容性

### 质量工具
- **API 文档**: SpringDoc OpenAPI 自动生成
- **健康检查**: Spring Boot Actuator
- **监控指标**: Micrometer + Prometheus

### 监控指标
- 活跃会话数量
- WebRTC 连接成功率
- 对话响应时间
- 视频处理队列长度
- API 调用频率统计

## 常见问题 (FAQ)

### Q1: 如何配置 API Key 认证？
A: 在配置文件中添加 API 密钥列表，通过 `ApiKeyAuthFilter` 进行验证。

### Q2: WebRTC 连接失败如何处理？
A: 检查防火墙设置、STUN/TURN 服务器配置、浏览器权限等。

### Q3: 数字人训练需要多长时间？
A: 根据视频长度和服务器性能，通常需要 5-30 分钟。

### Q4: 如何支持更多 AI 模型？
A: 实现 `IChatService` 接口，添加对应的 AI 服务适配器。

## 相关文件清单

### 核心文件
- `src/main/java/org/dromara/dh/UnimedDhApplication.java` - 启动类
- `src/main/java/org/dromara/dh/controller/ExternalApiController.java` - 外部 API 控制器
- `src/main/java/org/dromara/dh/service/IDigitalHumanApiService.java` - 数字人 API 服务
- `src/main/java/org/dromara/dh/service/IWebRtcService.java` - WebRTC 服务
- `src/main/java/org/dromara/dh/service/IChatService.java` - 对话服务

### 过滤器和配置
- `src/main/java/org/dromara/dh/filter/ApiKeyAuthFilter.java` - API 密钥认证过滤器
- `src/main/java/org/dromara/dh/config/WebRtcConfig.java` - WebRTC 配置

### 配置文件
- `src/main/resources/application.yml` - 主配置文件

### 数据传输对象
- `src/main/java/org/dromara/dh/domain/dto/` - 请求对象
- `src/main/java/org/dromara/dh/domain/vo/` - 响应对象

## 服务地址

- **数字人服务**: http://localhost:9205
- **API 文档**: http://localhost:9205/doc.html
- **健康检查**: http://localhost:9205/actuator/health
- **性能指标**: http://localhost:9205/actuator/metrics

## 变更记录 (Changelog)

- **2025-12-16 09:30:24** - 初始化数字人模块文档，完成 API 接口和服务架构分析