[根目录](../../CLAUDE.md) > [unimed-api](../) > **unimed-api-resource**

# Unimed API Resource 资源服务接口模块

## 变更记录 (Changelog)

- **2026-05-15** - 初始化资源服务 API 模块文档

## 模块职责

定义资源服务域的远程服务接口，提供文件存储（OSS）的远程访问能力。被 unimed-dh-core、unimed-chronic-biz 等模块通过 Dubbo 调用。

## 主要接口

| 接口 | 职责 |
|------|------|
| RemoteFileService | 文件上传/下载/查询远程服务 |

## 实现位置

接口实现位于 `unimed-modules/unimed-resource` 中（RemoteFileServiceImpl），通过 `@DubboService` 暴露。

## 相关文件清单

- `pom.xml` - 模块定义
