[根目录](../../CLAUDE.md) > **unimed-auth**

# Unimed Auth 认证授权中心

## 模块职责

负责整个微服务系统的用户认证、权限管理、租户管理和第三方登录集成，是系统的安全核心模块。

## 入口与启动

### 启动类
- **文件**: `src/main/java/org/dromara/auth/UnimedAuthApplication.java`
- **端口**: 9210
- **特点**:
  - 使用 `@EnableDubbo` 启用 Dubbo 服务
  - 排除 `DataSourceAutoConfiguration`（无数据库依赖）
  - 支持多种认证策略

### 配置文件
- **主配置**: `src/main/resources/application.yml`
- **环境配置**: 通过 Nacos 配置中心管理
- **关键配置**:
  ```yaml
  server:
    port: 9210
  spring:
    application:
      name: unimed-auth
    cloud:
      nacos:
        server-addr: @nacos.server@
        discovery:
          group: @nacos.discovery.group@
        config:
          group: @nacos.config.group@
  ```

## 对外接口

### 核心控制器

#### TokenController - 令牌管理
- **路径**: `/`
- **主要接口**:
  - `POST /login` - 用户登录
  - `POST /logout` - 用户登出
  - `POST /register` - 用户注册
  - `GET /tenant/list` - 租户列表
  - `GET /binding/{source}` - 第三方登录绑定
  - `POST /social/callback` - 第三方登录回调
  - `DELETE /unlock/{socialId}` - 取消第三方授权

#### CaptchaController - 验证码
- **路径**: `/captcha`
- **功能**: 图形验证码生成和校验

#### ApiTokenController - API令牌
- **路径**: `/api-token`
- **功能**: API访问令牌管理

#### PublicApiTokenController - 公开API令牌
- **路径**: `/public-api-token`
- **功能**: 公开API访问令牌管理

### 认证策略
支持多种认证方式，通过 `IAuthStrategy` 接口实现：
- **密码认证**: `PasswordAuthStrategy`
- **短信认证**: `SmsAuthStrategy`
- **邮件认证**: `EmailAuthStrategy`
- **社交认证**: `SocialAuthStrategy`
- **小程序认证**: `XcxAuthStrategy`

## 关键依赖与配置

### 核心依赖
```xml
<!-- Spring Cloud -->
<dependency>
    <groupId>org.dromara</groupId>
    <artifactId>unimed-common-nacos</artifactId>
</dependency>

<!-- 安全相关 -->
<dependency>
    <groupId>org.dromara</groupId>
    <artifactId>unimed-common-security</artifactId>
</dependency>
<dependency>
    <groupId>org.dromara</groupId>
    <artifactId>unimed-common-social</artifactId>
</dependency>

<!-- API服务 -->
<dependency>
    <groupId>org.dromara</groupId>
    <artifactId>unimed-api-system</artifactId>
</dependency>
<dependency>
    <groupId>org.dromara</groupId>
    <artifactId>unimed-api-auth</artifactId>
</dependency>
```

### 外部服务依赖
- **RemoteUserService**: 用户信息服务
- **RemoteTenantService**: 租户管理服务
- **RemoteClientService**: 客户端管理服务
- **RemoteConfigService**: 配置管理服务
- **RemoteSocialService**: 社交登录服务
- **RemoteMessageService**: 消息推送服务

## 数据模型

### 请求对象 (Form)
- **LoginBody**: 登录请求体
- **PasswordLoginBody**: 密码登录
- **SmsLoginBody**: 短信登录
- **EmailLoginBody**: 邮箱登录
- **SocialLoginBody**: 社交登录
- **XcxLoginBody**: 小程序登录
- **RegisterBody**: 注册请求体

### 响应对象 (VO)
- **LoginVo**: 登录响应
- **TenantListVo**: 租户列表项
- **LoginTenantVo**: 登录租户信息
- **CaptchaVo**: 验证码信息

### 枚举类型
- **CaptchaType**: 验证码类型 (MATH、CHAR)
- **CaptchaCategory**: 验证码分类 (LOGIN、REGISTER)

## 测试与质量

### 测试策略
- **单元测试**: 认证策略测试
- **集成测试**: 登录流程测试
- **安全测试**: 令牌安全测试

### 质量工具
- **限流**: 使用 `@RateLimiter` 注解
- **加密**: 使用 `@ApiEncrypt` 注解
- **日志**: 使用 `@Log` 注解记录操作日志

### 监控指标
- 登录成功/失败次数
- 令牌生成/销毁数量
- 第三方登录使用情况
- 租户访问统计

## 常见问题 (FAQ)

### Q1: 如何新增认证方式？
A: 实现 `IAuthStrategy` 接口，添加对应的认证策略类和请求对象。

### Q2: 如何配置第三方登录？
A: 在 Nacos 配置中心添加社交登录配置，支持微信、QQ、GitHub 等平台。

### Q3: 租户隔离是如何实现的？
A: 通过 `TenantHelper` 工具类实现，在认证时校验租户有效性。

### Q4: 如何处理令牌刷新？
A: 登录成功返回的 `LoginVo` 包含刷新令牌，客户端可定期刷新访问令牌。

## 相关文件清单

### 核心文件
- `src/main/java/org/dromara/auth/UnimedAuthApplication.java` - 启动类
- `src/main/java/org/dromara/auth/controller/TokenController.java` - 令牌控制器
- `src/main/java/org/dromara/auth/service/SysLoginService.java` - 登录服务
- `src/main/java/org/dromara/auth/service/IAuthStrategy.java` - 认证策略接口

### 配置文件
- `src/main/resources/application.yml` - 主配置文件
- `src/main/resources/logback-plus.xml` - 日志配置

### 工具类
- `src/main/java/org/dromara/auth/properties/UserPasswordProperties.java` - 密码策略配置
- `src/main/java/org/dromara/auth/listener/UserActionListener.java` - 用户行为监听器

## 变更记录 (Changelog)

- **2025-12-16 09:30:24** - 初始化认证模块文档，完成接口分析和架构梳理