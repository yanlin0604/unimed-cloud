[根目录](../../CLAUDE.md) > [unimed-modules](../) > **unimed-gen**

# Unimed Gen 代码生成模块

## 变更记录 (Changelog)

- **2026-03-04 09:57:40** - 初始化代码生成模块文档，完成接口和服务分析

## 模块职责

提供自动化代码生成功能，支持从数据库表结构自动生成 Entity、Mapper、Service、Controller 等标准代码，支持多数据源和自定义模板。

## 入口与启动

### 启动类
- **文件**: `src/main/java/org/dromara/gen/UnimedGenApplication.java`（推测，需确认）
- **端口**: 9202
- **Nacos 服务名**: `unimed-gen`

### 配置文件
- **主配置**: `src/main/resources/application.yml`
- **Nacos 配置**: `script/config/nacos/unimed-gen.yml`

## 对外接口

### IGenTableService 主要功能
| 方法 | 说明 |
|------|------|
| selectPageGenTableList | 查询业务表列表（分页） |
| selectPageDbTableList | 查询数据库表列表（分页） |
| selectGenTableById | 查询业务表详情 |
| importGenTable | 导入数据库表结构 |
| updateGenTable | 修改代码生成配置 |
| deleteGenTableByIds | 删除代码生成配置 |
| previewCode | 预览生成代码 |
| downloadCode | 下载生成代码 |
| generatorCode | 生成代码到自定义路径 |
| synchDb | 同步数据库表结构 |

## 关键依赖与配置

### 核心依赖
- unimed-common-mybatis - 数据库访问
- Velocity 模板引擎

## 数据模型

### 核心实体
- **GenTable**: 代码生成业务表（表名、模块名、包路径、生成类型等）
- **GenTableColumn**: 代码生成业务表字段（字段名、类型、Java 类型、注释等）

### Mapper 映射
- `mapper/generator/GenTableMapper.xml`
- `mapper/generator/GenTableColumnMapper.xml`

## 相关文件清单

- `src/main/java/org/dromara/gen/config/MyBatisDataSourceMonitor.java` - 多数据源监控
- `src/main/java/org/dromara/gen/constant/GenConstants.java` - 代码生成常量
- `src/main/java/org/dromara/gen/domain/GenTable.java` - 业务表实体
- `src/main/java/org/dromara/gen/domain/GenTableColumn.java` - 字段实体
- `src/main/java/org/dromara/gen/mapper/GenTableMapper.java` - 表 Mapper
- `src/main/java/org/dromara/gen/mapper/GenTableColumnMapper.java` - 字段 Mapper
- `src/main/java/org/dromara/gen/service/IGenTableService.java` - 服务接口
- `src/main/java/org/dromara/gen/service/GenTableServiceImpl.java` - 服务实现
- `Dockerfile` - Docker 构建文件
