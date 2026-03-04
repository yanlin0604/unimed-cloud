[根目录](../../CLAUDE.md) > [unimed-modules](../) > **unimed-workflow**

# Unimed Workflow 工作流模块

## 变更记录 (Changelog)

- **2026-03-04 09:57:40** - 初始化工作流模块文档，完成控制器、服务、数据模型和 Dubbo 接口分析

## 模块职责

基于 Warm-Flow 国产工作流引擎，提供完整的流程定义管理、流程实例管理、任务管理和审批功能，支持动态表单、SpEL 规则引擎、多种审批模式和自定义权限控制。

## 入口与启动

### 启动类
- **文件**: `src/main/java/org/dromara/workflow/UnimedWorkflowApplication.java`
- **端口**: 配置文件中查看
- **Nacos 服务名**: `unimed-workflow`

### 配置文件
- **主配置**: `src/main/resources/application.yml`
- **Nacos 配置**: `script/config/nacos/unimed-workflow.yml`

## 对外接口

### REST 控制器

#### FlwDefinitionController - 流程定义管理
- **路径**: `/definition`
- **条件注解**: `@ConditionalOnEnable`（可通过配置开关）
- **接口**:
  - `GET /list` - 查询流程定义列表（分页）
  - `POST /import` - 导入流程定义（XML）
  - `GET /export/{id}` - 导出流程定义
  - `POST /save` - 保存流程定义
  - `PUT /publish/{id}` - 发布流程
  - `DELETE /{ids}` - 删除流程定义

#### FlwInstanceController - 流程实例管理
- **路径**: `/instance`
- **接口**:
  - `GET /list` - 查询流程实例列表
  - `GET /{id}` - 查询实例详情
  - `POST /cancel` - 取消流程
  - `POST /invalid` - 作废流程
  - `POST /termination` - 终止流程

#### FlwTaskController - 任务管理
- **路径**: `/task`
- **接口**:
  - `POST /startWorkFlow` - 启动工作流（`@RepeatSubmit`）
  - `POST /completeTask` - 完成任务
  - `POST /backProcess` - 退回任务
  - `GET /pageByTaskTodo` - 待办任务列表
  - `GET /pageByTaskDone` - 已办任务列表
  - `GET /pageByTaskCopy` - 抄送任务列表
  - `POST /urge` - 催办任务
  - `POST /delegateTask` - 委派任务
  - `POST /transferTask` - 转办任务

#### FlwCategoryController - 流程分类管理
- **路径**: `/category`
- **接口**: 分类的增删改查

#### FlwSpelController - SpEL 规则管理
- **路径**: `/spel`（推测）
- **接口**: SpEL 表达式规则的管理

#### TestLeaveController - 请假审批示例
- **路径**: `/testLeave`
- **接口**: 请假流程的完整示例（启动、审批、查询等）

### Dubbo 远程服务
- **RemoteWorkflowServiceImpl**: 实现 `RemoteWorkflowService` 接口，供其他微服务发起/完成流程

## 数据模型

### 核心实体
| 实体 | 说明 |
|------|------|
| FlowCategory | 流程分类 |
| FlowInstanceBizExt | 流程实例业务扩展 |
| FlowSpel | SpEL 规则 |
| TestLeave | 请假审批示例 |

### 业务对象 (BO)
StartProcessBo、CompleteTaskBo、BackProcessBo、FlowCancelBo、FlowInvalidBo、FlowTerminationBo、FlowUrgeTaskBo、FlowCopyBo、FlowInstanceBo、FlowTaskBo、FlowNextNodeBo、FlowVariableBo、FlowCategoryBo、FlowSpelBo、TestLeaveBo、TaskOperationBo

### 视图对象 (VO)
FlowDefinitionVo、FlowInstanceVo、FlowTaskVo、FlowHisTaskVo、FlowCategoryVo、FlowCopyVo、FlowSpelVo、ButtonPermissionVo、NodeExtVo、TestLeaveVo

### 枚举
ButtonPermissionEnum、CopySettingEnum、MessageTypeEnum、NodeExtEnum、TaskAssigneeEnum、TaskAssigneeType、TaskStatusEnum、VariablesEnum

### Mapper 映射
- `mapper/workflow/FlwCategoryMapper.xml`
- `mapper/workflow/FlwInstanceMapper.xml`
- `mapper/workflow/FlwInstanceBizExtMapper.xml`
- `mapper/workflow/FlwSpelMapper.xml`
- `mapper/workflow/FlwTaskMapper.xml`
- `mapper/workflow/TestLeaveMapper.xml`

## 服务层

### 服务接口
| 接口 | 职责 |
|------|------|
| IFlwDefinitionService | 流程定义管理 |
| IFlwInstanceService | 流程实例管理 |
| IFlwTaskService | 任务管理 |
| IFlwCategoryService | 分类管理 |
| IFlwSpelService | SpEL 规则管理 |
| IFlwCommonService | 公共服务 |
| IFlwNodeExtService | 节点扩展 |
| IFlwTaskAssigneeService | 任务指派 |
| ITestLeaveService | 请假示例 |
| WorkflowService | 工作流核心服务 |

### 处理器和监听器
| 类名 | 职责 |
|------|------|
| FlowProcessEventHandler | 流程事件处理 |
| WorkflowPermissionHandler | 权限控制处理 |
| WorkflowGlobalListener | 全局流程监听 |
| SpelRuleComponent | SpEL 规则组件 |

## 关键依赖

- warm-flow-mybatis-plus-sb3-starter - Warm-Flow 工作流引擎
- warm-flow-plugin-ui-sb-web - Warm-Flow UI 插件
- unimed-common-mybatis - 数据库访问
- unimed-common-dubbo - Dubbo RPC
- unimed-api-system - 系统服务 API
- unimed-api-workflow - 工作流 API 定义

## 相关文件清单

- `src/main/java/org/dromara/workflow/controller/` - 6 个控制器
- `src/main/java/org/dromara/workflow/service/` - 10 个服务接口
- `src/main/java/org/dromara/workflow/service/impl/` - 13 个服务实现
- `src/main/java/org/dromara/workflow/domain/` - 4 个实体 + 16 个 BO + 10 个 VO
- `src/main/java/org/dromara/workflow/mapper/` - 6 个 Mapper
- `src/main/java/org/dromara/workflow/common/enums/` - 8 个枚举
- `src/main/java/org/dromara/workflow/dubbo/RemoteWorkflowServiceImpl.java` - Dubbo 服务
- `Dockerfile` - Docker 构建文件
