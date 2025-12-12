# Unimed-Cloud-Plus 微服务系统

<div align="center">

![Unimed-Cloud-Plus](https://img.shields.io/badge/Unimed--Cloud--Plus-2.5.1-brightgreen.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-blue.svg)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.0-blue.svg)
![Java](https://img.shields.io/badge/Java-17+-orange.svg)
![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)

**基于 Spring Cloud 2025 & Spring Boot 3.5 的企业级微服务架构**

[在线文档](https://gitee.com/dromara/Unimed-Cloud-Plus) | [快速开始](#快速开始) | [更新日志](#更新日志) | [问题反馈](https://gitee.com/dromara/Unimed-Cloud-Plus/issues)

</div>

## 📖 项目简介

Unimed-Cloud-Plus 是基于 Dromara 开源组织开发的企业级微服务架构系统，采用最新的 Spring Cloud 2025 和 Spring Boot 3.5 技术栈，集成了众多优秀的开源组件，为企业提供完整的微服务解决方案。

### 🎯 设计理念

- **现代化技术栈**：基于 Java 17+、Spring Boot 3.5、Spring Cloud 2025
- **微服务架构**：模块化设计，服务独立部署，易于扩展
- **开箱即用**：集成常用功能模块，快速搭建企业应用
- **高性能**：采用 Redis、RocketMQ 等高性能组件
- **安全可靠**：集成 Sa-Token 权限框架，支持多租户
- **云原生**：支持 Docker 容器化部署，适配 Kubernetes

## ✨ 核心特性

### 🏗️ 架构特性
- **微服务架构**：基于 Spring Cloud 的分布式微服务架构
- **服务治理**：集成 Nacos 服务注册与发现、配置管理
- **API 网关**：Spring Cloud Gateway 统一网关入口
- **负载均衡**：支持多种负载均衡策略
- **熔断降级**：集成 Sentinel 流量控制和熔断降级
- **分布式事务**：集成 Seata 分布式事务解决方案

### 🔐 安全特性
- **权限认证**：Sa-Token 轻量级权限认证框架
- **多租户**：完善的多租户数据隔离方案
- **数据加密**：敏感数据加密存储和传输
- **接口限流**：基于 Redis 的分布式限流
- **幂等性**：接口幂等性保证机制
- **社交登录**：集成第三方社交平台登录

### 💾 数据特性
- **多数据源**：动态数据源切换，读写分离
- **数据权限**：基于注解的数据权限控制
- **缓存管理**：Redis 多级缓存策略
- **搜索引擎**：集成 Elasticsearch 全文检索
- **文件存储**：支持本地、OSS、S3 等多种存储方式

### 🛠️ 开发特性
- **代码生成**：基于模板的代码生成器
- **API 文档**：集成 SpringDoc 自动生成 API 文档
- **数据导入导出**：Excel 数据批量处理
- **定时任务**：集成 SnailJob 分布式任务调度
- **工作流**：集成 Warm-Flow 国产工作流引擎
- **消息队列**：支持 RocketMQ 异步消息处理

### 📊 监控特性
- **链路追踪**：集成 SkyWalking 分布式链路追踪
- **日志收集**：支持 ELK、Logstash 日志收集
- **性能监控**：集成 Prometheus + Grafana 监控
- **健康检查**：Spring Boot Actuator 健康检查

## 🏛️ 系统架构

### 模块结构

```
unimed-Cloud-Plus
├─ unimed-api             // api模块
│  └─ unimed-api-bom                // api模块依赖管理
│  └─ unimed-api-resource           // 资源api模块
│  └─ unimed-api-system             // 系统api模块
│  └─ unimed-api-workflow           // 工作流api模块
├─ unimed-auth            // 认证服务 [9210]
├─ unimed-common          // 通用模块
│  └─ unimed-common-alibaba-bom     // alibaba 依赖管理
│  └─ unimed-common-bom             // common 依赖管理
│  └─ unimed-common-bus             // 消息总线模块
│  └─ unimed-common-core            // 核心功能模块
│  └─ unimed-common-dict            // 字典集成模块
│  └─ unimed-common-doc             // 文档集成模块
│  └─ unimed-common-dubbo           // dubbo集成模块
│  └─ unimed-common-elasticsearch   // ES集成模块
│  └─ unimed-common-encrypt         // 数据加解密模块
│  └─ unimed-common-excel           // excel集成模块
│  └─ unimed-common-idempotent      // 幂等功能模块
│  └─ unimed-common-job             // job定时任务集成模块
│  └─ unimed-common-json            // json集成模块
│  └─ unimed-common-loadbalancer    // 团队负载均衡集成模块
│  └─ unimed-common-log             // 日志集成模块
│  └─ unimed-common-logstash        // elk日志集成模块
│  └─ unimed-common-mail            // 邮件集成模块
│  └─ unimed-common-mybatis         // mybatis数据库相关集成模块
│  └─ unimed-common-oss             // oss相关集成模块
│  └─ unimed-common-prometheus      // prometheus监控
│  └─ unimed-common-redis           // redis集成模块
│  └─ unimed-common-satoken         // satoken集成模块
│  └─ unimed-common-seata           // seata分布式事务集成模块
│  └─ unimed-common-security        // 框架权限鉴权集成模块
│  └─ unimed-common-sensitive       // 脱敏功能模块
│  └─ unimed-common-sentinel        // sentinel集成模块
│  └─ unimed-common-skylog          // skywalking日志收集模块
│  └─ unimed-common-sms             // 短信集成模块
│  └─ unimed-common-social          // 社交三方功能模块
│  └─ unimed-common-sse             // sse流推送模块
│  └─ unimed-common-tenant          // 租户功能模块
│  └─ unimed-common-translation     // 通用翻译功能
│  └─ unimed-common-web             // web服务集成模块
│  └─ unimed-common-websocket       // websocket服务集成模块
├─ unimed-example        // 例子模块
│  └─ unimed-demo        // 演示模块 [9401]
│  └─ unimed-test-mq     // mq演示模块 [9402]
├─ unimed-gateway        // 网关模块 [8080]
├─ unimed-modules        // 功能模块
│  └─ unimed-gen                    // 代码生成模块 [9202]
│  └─ unimed-job                    // 任务调度模块 [9203,9901]
│  └─ unimed-resource               // 资源模块 [9204]
│  └─ unimed-system                 // 系统模块 [9201]
│  └─ unimed-workflow               // 工作流模块 [9205]
├─ unimed-visual         // 可视化模块
│  └─ unimed-monitor                // 服务监控模块 [9100]
│  └─ unimed-nacos                  // nacos服务模块 [8848,9848,9849]
│  └─ unimed-seata-server           // seata服务模块 [7091,8091]
│  └─ unimed-sentinel-dashboard     // sentinel控制台模块 [8718]
│  └─ unimed-snailjob-server        // 任务调度控制台模块 [8800,17888]
├─ plus-ui              // 前端框架 [80]
├─ config/nacos         // nacos配置文件(需复制到nacos配置中心使用)
│  └─ sentinel-unimed-gateway.json  // sentinel对接gateway限流配置文件
│  └─ seata-server.properties      // seata服务配置文件
│  └─ application-common.yml              // 所有应用主共享配置文件
│  └─ datasource.yml               // 所有应用共享数据源配置文件
│  └─ unimed-auth.yml               // auth 模块配置文件
│  └─ unimed-gateway.yml            // gateway 模块配置文件
│  └─ unimed-gen.yml                // gen 模块配置文件
│  └─ unimed-job.yml                // job 模块配置文件
│  └─ unimed-monitor.yml            // monitor 模块配置文件
│  └─ unimed-resource.yml           // resource 模块配置文件
│  └─ unimed-sentinel-dashboard.yml // sentinel 控制台 模块配置文件
│  └─ unimed-snailjob-server.yml    // snailjob 控制台 模块配置文件
│  └─ unimed-system.yml             // systen 模块配置文件
│  └─ unimed-workflow.yml           // workflow 模块配置文件
├─ config/grafana       // grafana配置文件(需复制到grafana使用)
│  └─ Nacos.json                        // Nacos监控页面
│  └─ SLS JVM监控大盘.json               // JVM监控页面
│  └─ Spring Boot 2.1 Statistics.json   // SpringBoot监控页面
├─ sql                  // sql脚本
├─ docker               // docker 配置脚本
├─ .run                 // 执行脚本文件
├─ .editorconfig        // 编辑器编码格式配置
├─ LICENSE              // 开源协议
├─ pom.xml              // 公共依赖
├─ README.md            // 框架说明文件

```

### 技术架构图

```
┌─────────────────────────────────────────────────────────────┐
│                        前端应用层                              │
│  Vue3 + TypeScript + Element Plus + Vite                   │
└─────────────────────────────────────────────────────────────┘
                                │
┌─────────────────────────────────────────────────────────────┐
│                      API 网关层                              │
│           Spring Cloud Gateway + Sa-Token                   │
└─────────────────────────────────────────────────────────────┘
                                │
┌─────────────────────────────────────────────────────────────┐
│                      微服务层                                │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────┐ │
│  │  认证服务    │ │  系统服务    │ │  资源服务    │ │  工作流  │ │
│  │             │ │             │ │             │ │  服务   │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────┘ │
└─────────────────────────────────────────────────────────────┘
                                │
┌─────────────────────────────────────────────────────────────┐
│                      基础设施层                              │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────┐ │
│  │    Nacos    │ │    Redis    │ │   MySQL     │ │RocketMQ │ │
│  │  注册中心    │ │    缓存     │ │   数据库     │ │ 消息队列 │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────┘ │
└─────────────────────────────────────────────────────────────┘
```

## 🚀 快速开始

### 环境要求

| 组件 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 17+ | 推荐使用 OpenJDK 17 或 21 |
| Maven | 3.9.0+ | 项目构建工具 |
| MySQL | 8.0+ | 主数据库 |
| Redis | 7.0+ | 缓存和会话存储 |
| Nacos | 2.4.0+ | 服务注册与配置中心 |
| RocketMQ | 5.2.0+ | 消息队列（可选） |

### 本地开发环境搭建

#### 1. 克隆项目

```bash
git clone https://gitee.com/dromara/Unimed-Cloud-Plus.git
cd Unimed-Cloud-Plus
```

#### 2. 启动基础服务

```bash
# 启动 Nacos（单机模式）
cd script/nacos
startup.cmd -m standalone

# 启动 Redis
redis-server

# 启动 MySQL
# 导入数据库脚本：script/sql/
```

#### 3. 修改配置

编辑 `application-dev.yml` 配置文件，修改数据库和 Redis 连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/unimed_cloud?useUnicode=true&characterEncoding=utf8mb4
    username: root
    password: your_password
  
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password
```

#### 4. 启动服务

按以下顺序启动各个服务：

```bash
# 1. 启动认证服务
cd unimed-auth
mvn spring-boot:run

# 2. 启动网关服务
cd unimed-gateway
mvn spring-boot:run

# 3. 启动系统服务
cd unimed-modules/unimed-system
mvn spring-boot:run

# 4. 启动其他业务服务...
```

#### 5. 访问系统

- **API 网关**: http://localhost:8080
- **认证服务**: http://localhost:9200
- **系统服务**: http://localhost:9201
- **API 文档**: http://localhost:8080/doc.html

### Docker 部署

#### 1. 构建镜像

```bash
# 构建所有服务镜像
mvn clean package -DskipTests
docker-compose build
```

#### 2. 启动服务

```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps
```

#### 3. 停止服务

```bash
docker-compose down
```

## 📚 技术栈

### 后端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.7 | 基础框架 |
| Spring Cloud | 2025.0.0 | 微服务框架 |
| Spring Cloud Gateway | - | API 网关 |
| Spring Cloud Alibaba | - | 阿里巴巴微服务组件 |
| Nacos | 2.4.0+ | 服务注册与配置中心 |
| Sa-Token | 1.44.0 | 权限认证框架 |
| MyBatis-Plus | 3.5.14 | ORM 框架 |
| Redisson | 3.51.0 | Redis 客户端 |
| RocketMQ | 2.3.4 | 消息队列 |
| Seata | - | 分布式事务 |
| Sentinel | - | 流量控制 |
| SkyWalking | 9.3.0 | 链路追踪 |
| Warm-Flow | 1.8.2 | 工作流引擎 |
| SnailJob | 1.8.0 | 分布式任务调度 |

### 工具组件

| 组件 | 版本 | 说明 |
|------|------|------|
| Hutool | 5.8.40 | Java 工具类库 |
| MapStruct Plus | 1.5.0 | 对象映射工具 |
| Lombok | 1.18.40 | 代码生成工具 |
| SpringDoc | 2.8.13 | API 文档生成 |
| FastExcel | 1.3.0 | Excel 处理 |
| JustAuth | 1.16.7 | 第三方登录 |
| IP2Region | 2.7.0 | IP 地址定位 |
| SMS4J | 3.3.4 | 短信发送 |

## 🔧 开发指南

### 代码规范

项目遵循以下代码规范：

- **Java 编码规范**：遵循阿里巴巴 Java 开发手册
- **注释规范**：使用中文注释，方法和类必须添加 Javadoc
- **命名规范**：使用驼峰命名法，常量使用大写下划线
- **包结构规范**：按功能模块划分包结构

### 开发流程

1. **创建功能分支**：从 `develop` 分支创建功能分支
2. **编写代码**：按照代码规范编写功能代码
3. **单元测试**：编写对应的单元测试用例
4. **代码审查**：提交 Pull Request 进行代码审查
5. **合并代码**：审查通过后合并到 `develop` 分支

### 新增模块

1. **创建模块**：在对应目录下创建新的 Maven 模块
2. **添加依赖**：在父 POM 中添加模块依赖
3. **编写代码**：按照项目结构编写业务代码
4. **配置文件**：添加必要的配置文件
5. **测试验证**：编写测试用例验证功能

### API 开发规范

```java
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {
    
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户信息")
    public R<UserVo> getUser(@PathVariable Long id) {
        // 实现逻辑
    }
    
    @PostMapping
    @Operation(summary = "创建用户")
    public R<Void> createUser(@Valid @RequestBody UserCreateRequest request) {
        // 实现逻辑
    }
}
```

## 📋 功能模块

### 系统管理
- **用户管理**：用户信息维护、角色分配
- **角色管理**：角色权限配置、数据权限
- **菜单管理**：系统菜单配置、权限控制
- **部门管理**：组织架构管理
- **岗位管理**：岗位信息维护
- **字典管理**：系统字典配置
- **参数管理**：系统参数配置
- **通知公告**：系统通知管理
- **日志管理**：操作日志、登录日志

### 系统监控
- **在线用户**：当前在线用户监控
- **数据监控**：数据库监控
- **服务监控**：微服务状态监控
- **缓存监控**：Redis 缓存监控
- **系统信息**：服务器信息监控

### 系统工具
- **表单构建**：拖拽式表单设计器
- **代码生成**：前后端代码生成
- **系统接口**：Swagger 接口文档
- **定时任务**：Quartz 定时任务管理

### 多租户
- **租户管理**：租户信息维护
- **租户套餐**：租户套餐配置
- **数据隔离**：完善的数据隔离方案

## 🔒 安全特性

### 认证授权
- **JWT Token**：基于 JWT 的无状态认证
- **权限控制**：基于 RBAC 的权限控制模型
- **数据权限**：支持部门、个人等数据权限
- **接口权限**：细粒度的接口权限控制

### 数据安全
- **数据加密**：敏感数据加密存储
- **传输加密**：HTTPS 传输加密
- **SQL 注入防护**：MyBatis 参数化查询
- **XSS 防护**：前端 XSS 攻击防护

### 系统安全
- **登录限制**：登录失败次数限制
- **会话管理**：会话超时控制
- **IP 白名单**：IP 访问控制
- **操作审计**：完整的操作日志记录

## 📊 性能优化

### 缓存策略
- **多级缓存**：本地缓存 + Redis 分布式缓存
- **缓存预热**：系统启动时预加载热点数据
- **缓存更新**：基于事件的缓存更新机制
- **缓存穿透**：布隆过滤器防止缓存穿透

### 数据库优化
- **读写分离**：主从数据库读写分离
- **分库分表**：支持水平分库分表
- **连接池**：HikariCP 高性能连接池
- **SQL 优化**：慢查询监控和优化

### 系统优化
- **异步处理**：消息队列异步处理
- **限流降级**：接口限流和服务降级
- **负载均衡**：多种负载均衡策略
- **资源压缩**：静态资源压缩和 CDN

## 🚀 部署运维

### 环境部署

#### 开发环境
```bash
# 使用 Docker Compose 快速搭建开发环境
docker-compose -f docker-compose.dev.yml up -d
```

#### 测试环境
```bash
# 使用 Docker Compose 部署测试环境
docker-compose -f docker-compose.test.yml up -d
```

#### 生产环境
```bash
# 使用 Kubernetes 部署生产环境
kubectl apply -f k8s/
```

### 监控告警

#### 系统监控
- **Prometheus + Grafana**：系统性能监控
- **SkyWalking**：分布式链路追踪
- **ELK Stack**：日志收集和分析

#### 告警配置
```yaml
# Prometheus 告警规则示例
groups:
  - name: unimed-alerts
    rules:
      - alert: HighCPUUsage
        expr: cpu_usage > 80
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "CPU 使用率过高"
```

### 备份恢复

#### 数据备份
```bash
# MySQL 数据备份
mysqldump -u root -p unimed_cloud > backup.sql

# Redis 数据备份
redis-cli --rdb dump.rdb
```

#### 配置备份
```bash
# Nacos 配置备份
curl -X GET "http://nacos:8848/nacos/v1/cs/configs?export=true&group=DEFAULT_GROUP"
```

## 📈 更新日志

### v2.5.1 (2024-12-10)
- 🆕 升级 Spring Boot 到 3.5.7
- 🆕 升级 Spring Cloud 到 2025.0.0
- 🆕 集成 Warm-Flow 工作流引擎
- 🆕 新增 SSE 服务端推送功能
- 🔧 优化微服务架构设计
- 🐛 修复已知问题

### v2.5.0 (2024-11-15)
- 🆕 新增多租户功能
- 🆕 集成 SnailJob 分布式任务调度
- 🆕 新增 WebSocket 实时通信
- 🔧 优化权限控制机制
- 🔧 完善 API 文档

### v2.4.0 (2024-10-20)
- 🆕 集成 SkyWalking 链路追踪
- 🆕 新增 Elasticsearch 全文检索
- 🆕 支持多种文件存储方式
- 🔧 优化系统性能
- 🐛 修复安全漏洞



### 代码规范

- 遵循项目现有的代码风格
- 添加必要的注释和文档
- 编写相应的测试用例
- 确保所有测试通过