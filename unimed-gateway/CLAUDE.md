[根目录](../../CLAUDE.md) > **unimed-gateway**

# Unimed Gateway API 网关

## 模块职责

作为整个微服务系统的统一入口，负责请求路由、负载均衡、限流熔断、安全认证和跨域处理等核心网关功能。

## 入口与启动

### 启动类
- **文件**: `src/main/java/org/dromara/gateway/UnimedGatewayApplication.java`
- **端口**: 9200
- **特点**:
  - 基于 Spring Cloud WebFlux 响应式编程
  - 集成 Sa-Token 权限认证
  - 支持 Redis 缓存和限流

### 配置文件
- **主配置**: `src/main/resources/application.yml`
- **环境配置**: 通过 Nacos 配置中心管理
- **关键配置**:
  ```yaml
  server:
    port: 9200
  spring:
    application:
      name: unimed-gateway
    cloud:
      nacos:
        server-addr: @nacos.server@
        discovery:
          group: @nacos.discovery.group@
        config:
          group: @nacos.config.group@
  ```

## 对外接口

### 网关路由配置
通过 Nacos 配置中心动态管理路由规则：

#### 核心服务路由
- **认证服务**: `/auth/**` → `unimed-auth:9221`
- **系统服务**: `/system/**` → `unimed-system:9201`
- **代码生成**: `/gen/**` → `unimed-gen:9202`
- **任务调度**: `/job/**` → `unimed-job:9203`
- **资源服务**: `/resource/**` → `unimed-resource:9204`
- **数字人服务**: `/dh/**` → `unimed-dh:9205`
- **工作流服务**: `/workflow/**` → `unimed-workflow:9206`

#### 监控和管理路由
- **监控中心**: `/monitor/**` → `unimed-monitor:9100`
- **健康检查**: `/actuator/**` → 各服务 Actuator 端点

### 核心过滤器

#### Sa-Token 认证过滤器
- **功能**: 统一令牌验证和权限检查
- **配置路径**: 白名单路径无需认证
- **特点**: 支持响应式编程模型

#### 限流过滤器
- **技术**: Redis + Lua 脚本实现
- **策略**: 基于 IP、用户、接口的多维度限流
- **配置**: 通过 Nacos 动态调整限流规则

#### 日志过滤器
- **功能**: 请求响应日志记录
- **内容**: 请求路径、参数、响应状态、耗时
- **格式**: 结构化 JSON 日志

## 关键依赖与配置

### 核心依赖
```xml
<!-- Spring Cloud Gateway -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway-server-webflux</artifactId>
</dependency>

<!-- 负载均衡 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>

<!-- 本地缓存 -->
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>

<!-- Sa-Token 权限认证 -->
<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-reactor-spring-boot3-starter</artifactId>
</dependency>
```

### 外部服务依赖
- **unimed-common-nacos**: 服务发现和配置管理
- **unimed-common-redis**: Redis 缓存和限流
- **unimed-common-satoken**: Sa-Token 权限认证
- **unimed-common-tenant**: 多租户支持

## 数据模型

### 路由配置模型
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: unimed-auth
          uri: lb://unimed-auth
          predicates:
            - Path=/auth/**
          filters:
            - StripPrefix=1
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
```

### 限流配置模型
- **replenishRate**: 令牌补充速率
- **burstCapacity**: 令牌桶容量
- **key-resolver**: 限流键解析器

## 测试与质量

### 测试策略
- **单元测试**: 过滤器逻辑测试
- **集成测试**: 路由转发测试
- **压力测试**: 网关性能测试

### 质量工具
- **健康检查**: Spring Boot Actuator
- **指标监控**: Micrometer + Prometheus
- **链路追踪**: SkyWalking 集成

### 监控指标
- 请求 QPS 和响应时间
- 路由转发成功率
- 限流触发次数
- 认证失败统计
- 服务可用性监控

## 常见问题 (FAQ)

### Q1: 如何新增路由规则？
A: 在 Nacos 配置中心添加路由配置，支持热更新无需重启。

### Q2: 如何配置跨域访问？
A: 通过网关统一配置跨域策略，支持动态调整允许的域名和方法。

### Q3: 如何实现服务降级？
A: 配置 Hystrix 或 Sentinel 熔断规则，当服务不可用时返回默认响应。

### Q4: 如何优化网关性能？
A: 使用本地缓存、连接池调优、异步处理等方式提升性能。

## 相关文件清单

### 核心文件
- `src/main/java/org/dromara/gateway/UnimedGatewayApplication.java` - 启动类
- `src/main/resources/application.yml` - 主配置文件

### 配置文件
- `src/main/resources/application.yml` - 应用配置
- Nacos 配置中心:
  - `application-gateway.yml` - 网关专属配置
  - `gateway-routes.yml` - 路由规则配置
  - `gateway-rate-limiter.yml` - 限流配置

### 日志配置
- 日志通过 logback 配置输出到文件和控制台
- 支持按日期和大小滚动

## 变更记录 (Changelog)

- **2025-12-16 09:30:24** - 初始化网关模块文档，完成路由配置和过滤器分析