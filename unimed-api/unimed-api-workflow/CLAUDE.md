[根目录](../../CLAUDE.md) > [unimed-api](../) > **unimed-api-workflow**

# Unimed API Workflow 工作流服务接口模块

## 变更记录 (Changelog)

- **2026-05-15** - 初始化工作流 API 模块文档

## 模块职责

定义工作流域的远程服务接口，提供流程发起、任务完成等远程调用能力。被 unimed-chronic-biz 等模块通过 Dubbo 调用。

## 主要接口

| 接口 | 职责 |
|------|------|
| RemoteWorkflowService | 工作流远程服务（发起流程、完成任务等） |

## 实现位置

接口实现位于 `unimed-modules/unimed-workflow` 中（RemoteWorkflowServiceImpl），通过 `@DubboService` 暴露。

## 相关文件清单

- `pom.xml` - 模块定义
