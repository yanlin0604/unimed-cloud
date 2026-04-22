# Unimed-Cloud-Plus — Project Knowledge

> 这是 AI 助手与开发者的项目速查手册。更详尽的架构与变更记录见 [`CLAUDE.md`](./CLAUDE.md) 与 [`README.md`](./README.md)。

## 项目简介

**Unimed-Cloud-Plus** 是基于 Dromara 生态的企业级微服务系统（医疗健康领域），核心栈：

- **Java 21** + **Spring Boot 3.5.12** + **Spring Cloud 2025.0.1**
- Nacos 2.4+（注册/配置）| Spring Cloud Gateway | Apache Dubbo（RPC）| Seata（分布式事务）
- MyBatis-Plus 3.5.16 + MySQL 8 | Redis 7 + Redisson 3.52 | RocketMQ 5.x
- Sa-Token 1.44.0（权限）| Warm-Flow 1.8.4（工作流）| SnailJob 1.9.0（调度）
- 多租户 + 多数据源 + MapStruct-Plus + FastExcel + SpringDoc

根 `pom.xml` 管理所有版本，`${revision}=2.6.0`。

## 代码布局（关键目录）

```
unimed-auth/                     # 认证服务 (9221)
unimed-gateway/                  # API 网关 (9200)
unimed-gateway-mvc/              # 非响应式网关版本
unimed-modules/
  ├─ unimed-system/              # 系统管理 (9201) —— 用户/角色/菜单/字典/租户
  ├─ unimed-gen/                 # 代码生成 (9202)
  ├─ unimed-job/                 # 任务调度 (9203)
  ├─ unimed-resource/            # 资源/OSS/短信邮件 (9204)
  └─ unimed-workflow/            # 工作流 (9207)
unimed-dh/
  ├─ unimed-dh-core/             # 数字人业务 B/C 端 (9206) —— 包名 dhcore
  └─ unimed-dh-relay/            # 数字人 AI/WebRTC 中转 (9205) —— WebFlux
unimed-chronic/                  # 慢病业务域
  ├─ unimed-chronic-api/         # 对外 API 骨架
  └─ unimed-chronic-biz/         # 业务实现
unimed-api/                      # 跨服务 Dubbo 接口定义 (system/resource/workflow/auth)
unimed-common/                   # 32 个公共基础模块（core/web/mybatis/redis/dubbo/...）
unimed-visual/                   # 监控/Nacos/Seata/SnailJob 服务端
unimed-example/                  # 演示模块 (demo + test-mq)
script/
  ├─ sql/                        # 建表脚本（unimed-*.sql）
  ├─ sql/update/                 # 增量变更脚本
  └─ docker/                     # docker-compose 基础设施
config/nacos/                    # 需拷到 Nacos 配置中心使用
```

## 常用命令

### 构建

```bash
# 全量打包（跳过测试）
mvn clean package -DskipTests

# 单模块 + 依赖
mvn clean package -pl unimed-dh/unimed-dh-core -am -DskipTests

# 生产环境打包（profile=prod）
mvn clean package -Pprod -DskipTests

# 常用组合（来自 README）
mvn clean package -DskipTests -am -pl \
  unimed-gateway,unimed-auth,unimed-modules/unimed-system,\
unimed-modules/unimed-resource,unimed-dh/unimed-dh-core,\
unimed-dh/unimed-dh-relay,unimed-visual/unimed-nacos
```

### 测试

- 单元测试：`mvn test -pl <module>`（surefire 按 `@Tag` 过滤，groups 由 `profiles.active` 控制）
- 已有测试：`unimed-dh-relay`（3 个）、`unimed-example/unimed-demo`（5 个）；大部分业务模块无测试
- 默认 `<skipTests>true</skipTests>`

### 运行（本地）

按顺序启动：
1. **基础设施**：Nacos (8848) → MySQL → Redis
2. **核心**：unimed-auth (9221) → unimed-gateway (9200)
3. **业务**：unimed-system (9201) → unimed-resource (9204) → unimed-workflow (9207)
4. **扩展**：unimed-dh-relay (9205) → unimed-dh-core (9206) → unimed-job (9203)
5. **监控**：unimed-monitor (9100)

访问：API 网关 `http://localhost:9200`，Swagger `http://localhost:9200/doc.html`，Nacos `http://localhost:8848/nacos`

### Docker

所有服务 Dockerfile 统一基于 `bellsoft/liberica-openjdk-rocky:21.0.8-cds`（启用 CDS 加速启动）。

```bash
docker build -t unimed-gateway:latest ./unimed-gateway
docker build -t unimed-system:latest ./unimed-modules/unimed-system
docker build -t unimed-dh:latest ./unimed-dh/unimed-dh-core
# ... 其他模块类似，路径按 modules/dh/visual 子目录

docker-compose -f script/docker/docker-compose.yml up -d
```

## 编码约定

### 分层结构

```
<module>/
  controller/             # B 端 REST（@SaCheckPermission("mod:entity:action")）
    portal/               # C 端门户接口（@SaCheckLogin）
  domain/
    ├─ entity/            # DO（继承 BaseEntity 或 TenantEntity）
    ├─ bo/                # 业务参数对象（入参）
    ├─ vo/                # 视图对象（出参）
    ├─ dto/
    └─ convert/           # MapStruct-Plus 转换器
  mapper/                 # MyBatis-Plus Mapper（xml 在 resources/mapper/**）
  service/ + service/impl/
  config/
```

### 数据库约定

- 表名/字段：小写下划线
- 主键：`id BIGINT`
- 审计字段：`create_by / create_time / update_by / update_time / del_flag`
- 多租户字段：`tenant_id VARCHAR(20)` 或 `BIGINT`
- 软删除：`del_flag CHAR(1) DEFAULT '0'`
- 实体继承 `BaseEntity`（审计）或 `TenantEntity`（审计 + 租户 + `create_dept`）

### API 约定

- 统一响应：`R<T>`（`unimed-common-core`）
- 分页参数：`PageQuery`；分页返回：`TableDataInfo<T>`
- 操作日志：`@Log(title="xxx", businessType=BusinessType.INSERT)`
- 防重复提交：`@RepeatSubmit()`
- 权限：B 端 `@SaCheckPermission("mod:entity:action")`；C 端 `@SaCheckLogin`
- 跨服务调用：接口定义在 `unimed-api/*`；提供方 `@DubboService`，调用方 `@DubboReference`

### 命名 / 其他

- 包前缀：`org.dromara.<domain>`（如 `org.dromara.dhcore`、`org.dromara.chronic`）
- Lombok + MapStruct-Plus + SpringDoc 注解常用
- WebFlux 风格（WebClient + Mono）仅见于 `unimed-dh-relay`

## Gotchas / 注意事项

- **JDK 21 必需**：pom `java.version=21`，Dockerfile 全部升级到 JDK 21；低版本会编译失败。
- **默认 profile = `chronic-dev`**（见根 pom），非 `dev`；切换环境时需检查 `profiles.active`。
- **根 pom 的 `modules` 含 `unimed-gateway-mvc`**（README/CLAUDE.md 未列出），二者互斥部署。
- **Nacos 配置**：`config/nacos/*.yml` 必须手动导入到 Nacos 配置中心，否则服务启动失败。
- **多租户**：带 `tenant_id` 的表写入时框架自动填充；查询也会自动加租户过滤（基于 `unimed-common-tenant`）。新建表若需跨租户查询需加 `@InterceptorIgnore(tenantLine = "true")`。
- **不要在 pom 中手写包版本**，都走 `dependencyManagement`（根 pom / 各 bom）。
- **`dh-*.sql` 增量脚本**：历史增量脚本已被合并到主 `unimed-dh.sql`（见当前 git status 显示的已删除文件）；新增 DDL 请在 `script/sql/update/` 放单独文件。
- **不要混用 `Mono`/阻塞 API**：`unimed-dh-relay` 是完全响应式，调用 Dubbo/JDBC 需走 `Schedulers.boundedElastic()` 包装。
- **`R<T>` 里不要返回 null 当成功**：用 `R.ok()` / `R.ok(data)`。
- **菜单/按钮权限入库**：新增后端功能时请在 `sys_menu` 补齐权限记录，否则前端按钮不可见。
- **Windows 开发**：bash 可用（git bash / WSL），但 `docker-compose` 路径分隔符和 `mkdir -p` 写法需小心；shell 命令见根 `CLAUDE.md`。

## 快速参考：新增功能

- **新增 B 端 CRUD**：参考 `DhOrderController` / `DhMaterialController`
- **新增 C 端门户接口**：放在 `controller/portal/`，参考 `PortalOrderController` / `PortalTopupController`
- **方言采集样例**：`DhDialectPromptController`（B）+ `PortalDialectController`（C）
- **跨服务接口**：`unimed-api/unimed-api-<domain>` 定义；实现 `@DubboService`；调用 `@DubboReference`
- **定时任务**：SnailJob 客户端模块（`unimed-common-job`）+ 控制台 `unimed-snailjob-server`
- **工作流**：Warm-Flow 集成在 `unimed-modules/unimed-workflow`（参考 `http://warm-flow.cn/`）

## 重要文件

| 路径 | 用途 |
|------|------|
| `pom.xml` | 根依赖管理（revision / java / spring 版本） |
| `CLAUDE.md` | 详细架构与变更日志（主索引） |
| `README.md` | 官方介绍 + 部署说明 |
| `config/nacos/application-common.yml` | 所有服务共享配置 |
| `config/nacos/datasource.yml` | 所有服务共享数据源 |
| `script/sql/unimed-*.sql` | 各业务域建表 DDL |
| `script/sql/update/` | 增量变更 SQL |
| `script/docker/docker-compose.yml` | 基础设施编排 |

## 外部链接

- 框架文档：[plus-doc](https://plus-doc.dromara.org/) / [ruoyi-cloud-plus](https://gitee.com/dromara/RuoYi-Cloud-Plus)
- Warm-Flow：http://warm-flow.cn/
- Sa-Token：https://sa-token.cc/
- MyBatis-Plus：https://baomidou.com/
