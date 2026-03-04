[根目录](../../CLAUDE.md) > [unimed-modules](../) > **unimed-job**

# Unimed Job 任务调度模块

## 变更记录 (Changelog)

- **2026-03-04 09:57:40** - 初始化任务调度模块文档，完成任务执行器分析

## 模块职责

提供分布式定时任务调度功能，基于 SnailJob 框架，支持注解式任务定义、分片任务、Map/MapReduce 任务和广播任务。

## 入口与启动

### 启动类
- **文件**: `src/main/java/org/dromara/job/UnimedJobApplication.java`（推测）
- **端口**: 9203
- **Nacos 服务名**: `unimed-job`

### 配置文件
- **主配置**: `src/main/resources/application.yml`
- **Nacos 配置**: `script/config/nacos/unimed-job.yml`

## 任务执行器（`snailjob/`）

| 类名 | 任务类型 | 说明 |
|------|---------|------|
| TestAnnoJobExecutor | 注解任务 | SnailJob 注解式任务示例 |
| TestClassJobExecutor | 类任务 | SnailJob 类式任务示例 |
| TestBroadcastJob | 广播任务 | 所有节点同时执行 |
| TestStaticShardingJob | 分片任务 | 静态分片执行 |
| TestMapJobAnnotation | Map 任务 | 分布式 Map 任务 |
| TestMapReduceAnnotation1 | MapReduce 任务 | 分布式 MapReduce |
| AlipayBillTask | 业务任务 | 支付宝账单处理 |
| WechatBillTask | 业务任务 | 微信账单处理 |
| SummaryBillTask | 业务任务 | 账单汇总 |

## 数据模型

- **BillDto**: 账单数据传输对象

## 关键依赖

- snail-job-client-starter - SnailJob 客户端
- snail-job-client-job-core - SnailJob 任务核心

## 相关文件清单

- `src/main/java/org/dromara/job/snailjob/` - 9 个任务执行器
- `src/main/java/org/dromara/job/entity/BillDto.java` - 账单 DTO
- `Dockerfile` - Docker 构建文件
