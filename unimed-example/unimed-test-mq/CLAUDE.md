[根目录](../../CLAUDE.md) > [unimed-example](../) > **unimed-test-mq**

# Unimed Test MQ 消息队列测试模块

## 变更记录 (Changelog)

- **2026-05-15** - 初始化 MQ 测试模块文档

## 模块职责

提供多种消息队列（RocketMQ、RabbitMQ、Kafka）的测试和演示功能，用于验证和演示不同 MQ 中间件在项目中的集成方式。

## 入口与启动

### 启动类
- **文件**: `src/main/java/org/dromara/stream/UnimedTestMqApplication.java`
- **包名**: `org.dromara.stream`

### 配置文件
- **主配置**: `src/main/resources/application.yml`

## 控制器

| 控制器 | 职责 |
|--------|------|
| PushMessageController | 消息推送测试 |

## 关键依赖

```xml
unimed-common-nacos              -- Nacos 配置
unimed-common-security           -- Sa-Token 安全
unimed-common-doc                -- API 文档
unimed-common-web                -- Web 框架
unimed-common-tenant             -- 多租户
spring-boot-starter-amqp         -- RabbitMQ (AMQP)
rocketmq-spring-boot-starter     -- RocketMQ
spring-kafka                     -- Kafka
```

## 支持的消息队列

| MQ 类型 | 依赖 | 说明 |
|---------|------|------|
| RocketMQ | rocketmq-spring-boot-starter | 阿里云 RocketMQ |
| RabbitMQ | spring-boot-starter-amqp | AMQP 协议 |
| Kafka | spring-kafka | Apache Kafka |

## 相关文件清单

- `src/main/java/org/dromara/stream/UnimedTestMqApplication.java` - 启动类
- `src/main/java/org/dromara/stream/controller/PushMessageController.java` - 消息推送控制器
