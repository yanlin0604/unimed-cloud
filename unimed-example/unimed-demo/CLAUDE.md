[根目录](../../CLAUDE.md) > [unimed-example](../) > **unimed-demo**

# Unimed Demo 示例模块

## 变更记录 (Changelog)

- **2026-05-15** - 初始化示例模块文档

## 模块职责

提供各类功能演示和测试用例，涵盖 Redis 缓存/发布订阅/分布式锁、邮件发送、短信、Sa-Token 认证、Elasticsearch CRUD、Excel 导入导出、加密解密、数据脱敏、国际化、消息总线、分片处理等通用功能。

## 入口与启动

### 启动类
- **文件**: `src/main/java/org/dromara/demo/UnimedDemoApplication.java`
- **包名**: `org.dromara.demo`

### 配置文件
- **主配置**: `src/main/resources/application.yml`

## 控制器列表（18 个）

| 控制器 | 职责 |
|--------|------|
| TestDemoController | 基础功能测试 |
| TestTreeController | 树形结构测试 |
| TestBatchController | 批量处理测试 |
| TestShardingController | 分片处理测试 |
| TestBusController | 消息总线测试 |
| TestEncryptController | 加密解密测试 |
| TestSensitiveController | 数据脱敏测试 |
| TestI18nController | 国际化测试 |
| RedisCacheController | Redis 缓存演示 |
| RedisPubSubController | Redis 发布订阅演示 |
| RedisLockController | Redis 分布式锁演示 |
| SaTokenTestController | Sa-Token 认证测试 |
| MailSendController | 邮件发送演示 |
| SmsController | 短信发送演示 |
| Swagger3DemoController | Swagger3 文档演示 |
| EsCrudController | Elasticsearch CRUD 演示 |
| TestExcelController | Excel 导入导出演示 |
| package-info.java | 包说明 |

## 关键依赖

```xml
unimed-common-nacos         -- Nacos 配置
unimed-common-log           -- 操作日志
unimed-common-doc           -- API 文档
unimed-common-security      -- Sa-Token 安全
unimed-common-web           -- Web 框架
unimed-common-mybatis       -- MyBatis-Plus
unimed-common-dubbo         -- Dubbo RPC
unimed-common-idempotent    -- 幂等性
unimed-common-mail          -- 邮件服务
unimed-common-sms           -- 短信服务
unimed-common-encrypt       -- 加密
unimed-common-tenant        -- 多租户
unimed-common-elasticsearch -- Easy-ES
unimed-common-translation   -- 国际化
unimed-common-sensitive     -- 数据脱敏
spring-boot-starter-test    -- 测试
```

## 测试文件（5 个）

- `AssertUnitTest.java` - 断言单元测试
- `DemoUnitTest.java` - 演示单元测试
- `ParamUnitTest.java` - 参数单元测试
- `TagUnitTest.java` - 标签单元测试
- `TOrderTest.java` - 订单测试

## 相关文件清单

- `src/main/java/org/dromara/demo/UnimedDemoApplication.java` - 启动类
- `src/main/java/org/dromara/demo/controller/` - 17 个控制器
- `src/test/java/org/dromara/demo/` - 5 个测试文件
