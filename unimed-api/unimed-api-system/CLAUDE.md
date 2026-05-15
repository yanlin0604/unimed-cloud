[根目录](../../CLAUDE.md) > [unimed-api](../) > **unimed-api-system**

# Unimed API System 系统服务接口模块

## 变更记录 (Changelog)

- **2026-05-15** - 初始化系统服务 API 模块文档

## 模块职责

定义系统管理域的远程服务接口，供其他微服务通过 Dubbo RPC 调用。提供用户、角色、租户、字典等核心系统服务的远程访问能力。

## 主要接口

| 接口 | 职责 |
|------|------|
| RemoteUserService | 用户信息远程服务 |
| RemoteRoleService | 角色信息远程服务 |
| RemoteTenantService | 租户信息远程服务 |
| RemoteDictService | 字典信息远程服务 |

## 关键依赖

```xml
unimed-common-core  -- 核心工具（R<T> 等）
unimed-common-excel -- Excel 支持
```

## 实现位置

接口实现位于 `unimed-modules/unimed-system` 中，通过 `@DubboService` 注解暴露服务。

## 相关文件清单

- `pom.xml` - 模块定义
- `src/main/java/org/dromara/system/api/` - 接口定义
