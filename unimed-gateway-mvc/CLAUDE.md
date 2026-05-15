[根目录](../CLAUDE.md) > **unimed-gateway-mvc**

# Unimed Gateway MVC 网关模块

## 变更记录 (Changelog)

- **2026-05-15** - 初始化网关 MVC 版文档

## 模块职责

基于 Spring Cloud Gateway Server MVC（同步模型）的 API 网关，作为 unimed-gateway（WebFlux 响应式版）的同步替代方案。适用于不需要响应式编程的场景，使用 Spring MVC + Undertow 容器。

## 入口与启动

### 启动类
- **文件**: `src/main/java/org/dromara/gateway/UnimedGatewayMvcApplication.java`
- **特点**: 基于 Spring MVC 同步模型，使用 Undertow 容器（非 Tomcat）

### 配置文件
- **主配置**: `src/main/resources/application.yml`
- **Nacos 配置**: `script/config/nacos/unimed-gateway-mvc.yml`

## 关键依赖

```xml
spring-cloud-starter-gateway-server-webmvc  -- 网关 MVC 版核心
spring-boot-starter-undertow                -- Undertow 容器
caffeine                                    -- 本地缓存
sa-token-spring-boot3-starter              -- Sa-Token 认证（同步版）
unimed-common-nacos                        -- Nacos 配置
unimed-common-redis                        -- Redis 限流
unimed-common-satoken                      -- Sa-Token 配置
unimed-common-tenant                       -- 多租户
```

## 与 unimed-gateway 的区别

| 特性 | unimed-gateway (WebFlux) | unimed-gateway-mvc (MVC) |
|------|--------------------------|--------------------------|
| 编程模型 | 响应式 (WebFlux) | 同步 (Spring MVC) |
| 容器 | Netty | Undertow |
| Sa-Token | sa-token-reactor-spring-boot3-starter | sa-token-spring-boot3-starter |
| 依赖 | spring-cloud-starter-gateway-server-webflux | spring-cloud-starter-gateway-server-webmvc |
| 适用场景 | 高并发、流式处理 | 传统同步调用、简单路由 |

## 相关文件清单

- `src/main/java/org/dromara/gateway/UnimedGatewayMvcApplication.java` - 启动类
- `src/main/resources/application.yml` - 应用配置
