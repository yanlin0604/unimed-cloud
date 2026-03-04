[根目录](../../CLAUDE.md) > [unimed-modules](../) > **unimed-system**

# Unimed System 系统管理模块

## 变更记录 (Changelog)

- **2026-03-04 09:57:40** - 增量更新：补充 SysTenantPackageController、DigitalHumanController；修正面包屑路径
- **2025-12-16 09:30:24** - 初始化系统模块文档，完成接口分析和数据模型梳理

## 模块职责

提供完整的后台管理功能，包括用户管理、角色权限、菜单管理、部门管理、字典管理、系统配置、操作日志、租户管理、数字人配置管理等核心系统功能。

## 入口与启动

### 启动类
- **文件**: `src/main/java/org/dromara/system/UnimedSystemApplication.java`
- **端口**: 9201
- **特点**:
  - 使用 `@EnableDubbo` 启用 Dubbo 服务
  - 集成完整的数据库访问层
  - 支持数据权限和租户隔离

### 配置文件
- **主配置**: `src/main/resources/application.yml`
- **Nacos 配置**: `script/config/nacos/unimed-system.yml`

## 对外接口

### 系统管理控制器 (`controller/system/`，13 个）

| 控制器 | 路径 | 主要功能 |
|-------|------|---------|
| SysUserController | `/system/user` | 用户增删改查、密码重置、状态管理 |
| SysRoleController | `/system/role` | 角色权限分配、数据权限设置 |
| SysMenuController | `/system/menu` | 菜单树形管理、权限标识 |
| SysDeptController | `/system/dept` | 组织架构树形管理 |
| SysPostController | `/system/post` | 岗位信息维护 |
| SysDictTypeController | `/system/dict/type` | 字典类型管理 |
| SysDictDataController | `/system/dict/data` | 字典数据管理 |
| SysConfigController | `/system/config` | 系统参数配置 |
| SysNoticeController | `/system/notice` | 系统公告发布 |
| SysTenantController | `/system/tenant` | 多租户管理 |
| SysTenantPackageController | `/system/tenant/package` | 租户套餐管理 |
| SysClientController | `/system/client` | OAuth 客户端管理 |
| SysSocialController | `/system/social` | 第三方登录管理 |
| DigitalHumanController | `/system/digitalHuman` | 数字人配置管理 |

### 监控管理控制器 (`controller/monitor/`，4 个）

| 控制器 | 路径 | 主要功能 |
|-------|------|---------|
| SysOperlogController | `/monitor/operlog` | 操作日志查询和清理 |
| SysLogininforController | `/monitor/logininfor` | 登录日志管理 |
| SysUserOnlineController | `/monitor/online` | 在线用户监控 |
| CacheController | `/monitor/cache` | Redis 缓存监控 |

## 数据模型

### 核心实体（16 个）
SysUser、SysRole、SysMenu、SysDept、SysPost、SysDictData、SysDictType、SysConfig、SysNotice、SysOperLog、SysLogininfor、SysTenant、SysTenantPackage、SysClient、SysSocial、SysRoleDept、SysRoleMenu、SysUserRole、SysUserPost

### 业务对象 (BO)
SysUserBo、SysRoleBo、SysMenuBo、SysDeptBo、SysPostBo、SysDictDataBo、SysDictTypeBo、SysConfigBo、SysNoticeBo、SysOperLogBo、SysLogininforBo、SysTenantBo、SysTenantPackageBo、SysClientBo、SysSocialBo、SysUserPasswordBo、SysUserProfileBo

### Mapper 映射（16 个 XML）
位于 `src/main/resources/mapper/system/` 下，覆盖所有核心实体。

## 关键依赖

```xml
unimed-common-mybatis    -- 数据库访问、数据权限
unimed-common-web        -- Web 框架
unimed-common-satoken    -- Sa-Token 认证
unimed-common-tenant     -- 多租户
unimed-common-dubbo      -- Dubbo RPC
unimed-api-system        -- 系统服务 API 定义
unimed-api-resource      -- 资源服务 API
```

## 常见问题 (FAQ)

### Q1: 如何实现数据权限？
A: 使用 `@DataPermission` 注解，支持部门级、用户级数据权限控制。

### Q2: 如何扩展新的业务实体？
A: 参考现有实体结构，添加对应的 Entity、Mapper、Service、Controller 层。

### Q3: 多租户数据隔离是如何实现的？
A: 通过 `TenantHelper` 自动在 SQL 中添加租户条件，实现数据隔离。

## 相关文件清单

- `src/main/java/org/dromara/system/controller/system/` - 14 个系统管理控制器
- `src/main/java/org/dromara/system/controller/monitor/` - 4 个监控控制器
- `src/main/java/org/dromara/system/domain/` - 16+ 个实体
- `src/main/java/org/dromara/system/domain/bo/` - 17 个业务对象
- `src/main/java/org/dromara/system/domain/convert/` - 5 个 MapStruct 转换器
- `src/main/resources/mapper/system/` - 16 个 Mapper XML
- `Dockerfile` - Docker 构建文件
