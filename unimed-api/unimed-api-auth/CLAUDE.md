[根目录](../../CLAUDE.md) > [unimed-api](../) > **unimed-api-auth**

# Unimed API Auth 认证服务接口模块

## 变更记录 (Changelog)

- **2026-05-15** - 初始化认证 API 模块文档

## 模块职责

定义认证授权域的远程服务接口，提供 API Token 验证等远程调用能力。被 unimed-dh-relay 等模块通过 Dubbo 调用。

## 主要接口

| 接口 | 职责 |
|------|------|
| RemoteTokenService | Token 验证远程服务 |

## 实现位置

接口实现位于 `unimed-auth` 中（RemoteAuthServiceImpl），通过 `@DubboService` 暴露。

## 相关文件清单

- `pom.xml` - 模块定义
