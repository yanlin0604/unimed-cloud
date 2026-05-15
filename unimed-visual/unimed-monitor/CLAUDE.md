[根目录](../../CLAUDE.md) > [unimed-visual](../) > **unimed-monitor**

# Unimed Monitor 监控中心

## 变更记录 (Changelog)

- **2026-05-15** - 初始化监控中心文档

## 模块职责

基于 Spring Boot Admin 的服务监控中心，提供微服务实例的健康检查、性能指标、环境变量、日志级别管理等可视化监控能力。

## 入口与启动

### 启动类
- **文件**: `src/main/java/org/dromara/modules/monitor/UnimedMonitorApplication.java`
- **端口**: 9100
- **特点**: 使用 Undertow 容器，集成 Spring Security 安全认证

### 配置文件
- **主配置**: `src/main/resources/application.yml`
- **Nacos 配置**: `script/config/nacos/unimed-monitor.yml`

## 关键依赖

```xml
spring-boot-admin-starter-server  -- Spring Boot Admin 服务端
unimed-common-nacos              -- Nacos 服务发现
spring-boot-starter-web          -- Spring MVC (Undertow)
spring-boot-starter-security     -- Spring Security
lombok                           -- 代码简化
```

## 访问地址

- 监控面板: http://localhost:9100

## 相关文件清单

- `src/main/java/org/dromara/modules/monitor/UnimedMonitorApplication.java` - 启动类
- `src/main/resources/application.yml` - 应用配置
