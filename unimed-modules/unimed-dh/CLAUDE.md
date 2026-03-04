[根目录](../../CLAUDE.md) > [unimed-modules](../) > **unimed-dh**

# Unimed DH 数字人业务服务模块

## 变更记录 (Changelog)

- **2026-03-04 09:57:40** - 初始化文档：从原 unimed-dh 重构拆分，本模块为数字人业务核心层（dhcore 包名），目前仅有启动类骨架

## 模块职责

数字人业务服务的**核心业务层**，负责数字人业务逻辑处理。本模块在架构重构中从原 unimed-dh 拆分而来，API 中转功能移至 unimed-dh-relay，本模块聚焦于数字人业务数据处理和业务规则。

当前状态：**骨架阶段**，仅包含启动类，业务功能待开发。

## 入口与启动

### 启动类
- **文件**: `src/main/java/org/dromara/dhcore/UnimedDhApplication.java`
- **包名**: `org.dromara.dhcore`（注意：与 relay 模块的 `org.dromara.dh` 不同）
- **端口**: 9206
- **Nacos 服务名**: `unimed-dh`
- **特点**:
  - 排除 `DataSourceAutoConfiguration`（当前无数据库依赖）
  - 未启用 Dubbo（与 relay 模块不同）
  - 轻量级启动

### 配置文件
- **主配置**: `src/main/resources/application.yml`（端口 9206，通过 Nacos 管理）
- **Nacos 配置**: `script/config/nacos/unimed-dh.yml`
- **关键配置项**:
  - `dh.module.enabled` - 模块启用开关
  - `dh.feature.bootstrap-check` - 启动检查开关

## 关键依赖与配置

### 核心依赖
```xml
unimed-common-nacos      -- Nacos 服务发现与配置
unimed-common-log        -- 操作日志
unimed-common-doc        -- API 文档
unimed-common-web        -- Web 框架
unimed-common-security   -- 安全框架
```

### 与 relay 模块的关系
```
外部请求 --> unimed-dh-relay (9205, API中转)
                |
                +--> Python 数字人引擎（WebRTC/配置/训练）
                |
                +--> unimed-dh (9206, 业务逻辑) [规划中]
```

## 数据模型

当前无数据模型，预期后续将包含：
- 数字人业务实体
- 会话管理模型
- 训练任务持久化模型

## 测试与质量

当前无测试文件。

## 相关文件清单

### 核心文件
- `src/main/java/org/dromara/dhcore/UnimedDhApplication.java` - 启动类
- `src/main/resources/application.yml` - 应用配置（端口 9206）
- `src/main/resources/banner.txt` - 启动横幅
- `src/main/resources/logback-plus.xml` - 日志配置
- `pom.xml` - Maven 配置
- `Dockerfile` - Docker 构建文件（暴露端口 9206）

## 常见问题 (FAQ)

### Q1: unimed-dh 和 unimed-dh-relay 的区别？
A: unimed-dh-relay 是 API 中转层，负责接收外部请求并转发到 Python 后端；unimed-dh 是业务逻辑层，负责数字人相关的业务处理（当前为骨架阶段）。

### Q2: 为什么包名是 dhcore 而不是 dh？
A: 为了与 unimed-dh-relay 的 `org.dromara.dh` 包名区分，避免类路径冲突。
