[根目录](../CLAUDE.md) > **unimed-gateway**

# Unimed Gateway API 网关

## 变更记录 (Changelog)

- **2026-03-04 09:57:40** - 增量更新：补充完整过滤器列表（7 个）、GatewayConfig 和 GatewayExceptionHandler；补充 BlackListUrlFilter 和 WebI18nFilter；修正面包屑路径和路由映射
- **2025-12-16 09:30:24** - 初始化网关模块文档，完成路由配置和过滤器分析

## 模块职责

作为整个微服务系统的统一入口，负责请求路由、负载均衡、限流熔断、安全认证、跨域处理、请求日志和国际化支持。

## 入口与启动

### 启动类
- **文件**: `src/main/java/org/dromara/gateway/UnimedGatewayApplication.java`
- **端口**: 9200
- **特点**: 基于 Spring Cloud WebFlux 响应式编程

### 配置文件
- **主配置**: `src/main/resources/application.yml`
- **Nacos 配置**: `script/config/nacos/unimed-gateway.yml`

## 核心组件

### 过滤器（7 个）
| 过滤器 | 职责 |
|-------|------|
| AuthFilter | Sa-Token 统一令牌验证和权限检查 |
| ForwardAuthFilter | 转发认证过滤器 |
| GlobalLogFilter | 请求响应日志记录 |
| BlackListUrlFilter | URL 黑名单拦截 |
| WebCacheRequestFilter | 请求缓存 |
| WebCorsFilter | 跨域处理 |
| WebI18nFilter | 国际化支持 |

### 配置类
| 类 | 职责 |
|----|------|
| GatewayConfig | 网关核心配置 |
| IgnoreWhiteProperties | 白名单路径配置 |
| CustomGatewayProperties | 自定义网关属性 |
| ApiDecryptProperties | API 解密配置 |
| StripPrefixGatewayFilterFactory | 路径前缀剥离（扩展实现） |

### 异常处理
| 类 | 职责 |
|----|------|
| GatewayExceptionHandler | 全局异常处理 |

### 工具类
| 类 | 职责 |
|----|------|
| WebFluxUtils | WebFlux 工具方法 |

## 路由规则

通过 Nacos 配置中心动态管理路由规则：
| 路由 | 目标服务 | 端口 |
|------|---------|------|
| `/auth/**` | unimed-auth | 9221 |
| `/system/**` | unimed-system | 9201 |
| `/gen/**` | unimed-gen | 9202 |
| `/job/**` | unimed-job | 9203 |
| `/resource/**` | unimed-resource | 9204 |
| `/dh-relay/**` | unimed-dh-relay | 9205 |
| `/dh/**` | unimed-dh | 9206 |
| `/workflow/**` | unimed-workflow | 9207 |

## 关键依赖

```xml
spring-cloud-starter-gateway-server-webflux  -- 网关核心
spring-cloud-starter-loadbalancer            -- 负载均衡
caffeine                                      -- 本地缓存
sa-token-reactor-spring-boot3-starter        -- Sa-Token 响应式
unimed-common-nacos                          -- Nacos 配置
unimed-common-redis                          -- Redis 限流
unimed-common-satoken                        -- Sa-Token 配置
unimed-common-tenant                         -- 多租户
```

## 常见问题 (FAQ)

### Q1: 如何新增路由规则？
A: 在 Nacos 配置中心的 `unimed-gateway.yml` 中添加路由配置，支持热更新无需重启。

### Q2: 如何配置白名单？
A: 修改 `IgnoreWhiteProperties` 配置，添加不需要认证的路径。

## 相关文件清单

- `src/main/java/org/dromara/gateway/UnimedGatewayApplication.java` - 启动类
- `src/main/java/org/dromara/gateway/filter/` - 7 个过滤器
- `src/main/java/org/dromara/gateway/config/` - 2 个配置类 + 3 个属性类
- `src/main/java/org/dromara/gateway/handler/GatewayExceptionHandler.java` - 异常处理
- `src/main/java/org/dromara/gateway/utils/WebFluxUtils.java` - 工具类
- `Dockerfile` - Docker 构建文件
