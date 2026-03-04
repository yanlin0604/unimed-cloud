[根目录](../CLAUDE.md) > **unimed-auth**

# Unimed Auth 认证授权中心

## 变更记录 (Changelog)

- **2026-03-04 09:57:40** - 增量更新：补充 ApiTokenController、PublicApiTokenController、RemoteAuthServiceImpl（Dubbo）、ApiTokenService、ApiKeyProperties 等新增文件；修正面包屑路径
- **2025-12-16 09:30:24** - 初始化认证模块文档，完成接口分析和架构梳理

## 模块职责

负责整个微服务系统的用户认证、权限管理、租户管理、API Token 管理和第三方登录集成，是系统的安全核心模块。

## 入口与启动

### 启动类
- **文件**: `src/main/java/org/dromara/auth/UnimedAuthApplication.java`
- **端口**: 9221
- **特点**:
  - 使用 `@EnableDubbo` 启用 Dubbo 服务
  - 排除 `DataSourceAutoConfiguration`（无数据库依赖）
  - 支持多种认证策略

### 配置文件
- **主配置**: `src/main/resources/application.yml`
- **Nacos 配置**: `script/config/nacos/unimed-auth.yml`

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

#### ApiTokenController - API 令牌管理
- **路径**: `/api-token`
- **功能**: API 访问令牌的创建、验证、刷新（内部认证后使用）

#### PublicApiTokenController - 公开 API 令牌
- **路径**: `/public-api-token`
- **功能**: 公开的 API 访问令牌管理接口

### 认证策略
支持多种认证方式，通过 `IAuthStrategy` 接口实现：
- **密码认证**: `PasswordAuthStrategy`
- **短信认证**: `SmsAuthStrategy`
- **邮件认证**: `EmailAuthStrategy`
- **社交认证**: `SocialAuthStrategy`
- **小程序认证**: `XcxAuthStrategy`

### Dubbo 远程服务
- **RemoteAuthServiceImpl**: 实现 `RemoteAuthService`，为其他微服务（如 unimed-dh-relay）提供 API Token 验证

## 关键依赖与配置

### 核心依赖
```xml
unimed-common-nacos      -- Nacos 配置
unimed-common-security   -- 安全框架
unimed-common-social     -- 社交登录（JustAuth）
unimed-common-dubbo      -- Dubbo RPC
unimed-api-system        -- 系统服务 API
unimed-api-auth          -- 认证服务 API 定义
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
- **PasswordLoginBody**: 密码登录
- **SmsLoginBody**: 短信登录
- **EmailLoginBody**: 邮箱登录
- **SocialLoginBody**: 社交登录
- **XcxLoginBody**: 小程序登录
- **RegisterBody**: 注册请求体
- **ApiTokenRequest**: API Token 请求

### 响应对象 (VO)
- **LoginVo**: 登录响应
- **TenantListVo**: 租户列表项
- **LoginTenantVo**: 登录租户信息
- **CaptchaVo**: 验证码信息
- **ApiTokenValidationVo**: API Token 验证结果

### DTO
- **ApiTokenDto**: API Token 数据传输
- **ApiTokenRequest**: API Token 请求

### 枚举类型
- **CaptchaType**: 验证码类型 (MATH、CHAR)
- **CaptchaCategory**: 验证码分类 (LOGIN、REGISTER)

### 配置属性
- **ApiKeyProperties**: API 密钥配置
- **CaptchaProperties**: 验证码配置
- **UserPasswordProperties**: 密码策略配置

## 测试与质量

### 质量工具
- **限流**: 使用 `@RateLimiter` 注解
- **加密**: 使用 `@ApiEncrypt` 注解
- **日志**: 使用 `@Log` 注解记录操作日志

## 常见问题 (FAQ)

### Q1: 如何新增认证方式？
A: 实现 `IAuthStrategy` 接口，添加对应的认证策略类和请求对象。

### Q2: 如何配置第三方登录？
A: 在 Nacos 配置中心添加社交登录配置，支持微信、QQ、GitHub 等平台。

### Q3: API Token 与 Sa-Token 的区别？
A: Sa-Token 用于用户会话认证；API Token 用于外部系统 API 调用鉴权（如数字人服务），通过 `ApiTokenService` 管理，Dubbo 远程验证。

## 相关文件清单

### 核心文件
- `src/main/java/org/dromara/auth/UnimedAuthApplication.java` - 启动类
- `src/main/java/org/dromara/auth/controller/TokenController.java` - 令牌控制器
- `src/main/java/org/dromara/auth/controller/CaptchaController.java` - 验证码控制器
- `src/main/java/org/dromara/auth/controller/ApiTokenController.java` - API Token 控制器
- `src/main/java/org/dromara/auth/controller/PublicApiTokenController.java` - 公开 Token 控制器
- `src/main/java/org/dromara/auth/service/SysLoginService.java` - 登录服务
- `src/main/java/org/dromara/auth/service/IAuthStrategy.java` - 认证策略接口
- `src/main/java/org/dromara/auth/service/ApiTokenService.java` - API Token 服务
- `src/main/java/org/dromara/auth/service/impl/` - 5 个认证策略实现
- `src/main/java/org/dromara/auth/dubbo/RemoteAuthServiceImpl.java` - Dubbo 远程认证服务
- `src/main/java/org/dromara/auth/properties/ApiKeyProperties.java` - API 密钥配置
- `src/main/java/org/dromara/auth/listener/UserActionListener.java` - 用户行为监听器
- `Dockerfile` - Docker 构建文件
