[根目录](../../CLAUDE.md) > [unimed-modules](../) > **unimed-system**

# Unimed System 系统管理模块

## 模块职责

提供完整的后台管理功能，包括用户管理、角色权限、菜单管理、部门管理、字典管理、系统配置、操作日志、租户管理等核心系统功能。

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
- **环境配置**: 通过 Nacos 配置中心管理
- **数据库**: MySQL 主从架构，支持读写分离

## 对外接口

### 系统管理控制器 (`system/`)

#### 用户管理
- **SysUserController**: 用户增删改查、密码重置、状态管理
  - `GET /system/user/list` - 用户列表
  - `POST /system/user` - 新增用户
  - `PUT /system/user` - 修改用户
  - `DELETE /system/user/{ids}` - 删除用户
  - `PUT /system/user/resetPwd` - 重置密码
  - `PUT /system/user/changeStatus` - 修改状态

#### 角色管理
- **SysRoleController**: 角色权限分配、数据权限设置
  - `GET /system/role/list` - 角色列表
  - `POST /system/role` - 新增角色
  - `PUT /system/role` - 修改角色
  - `DELETE /system/role/{ids}` - 删除角色
  - `GET /system/role/{roleId}/authUser` - 查询已分配用户

#### 菜单管理
- **SysMenuController**: 菜单树形管理、权限标识
  - `GET /system/menu/list` - 菜单列表
  - `POST /system/menu` - 新增菜单
  - `PUT /system/menu` - 修改菜单
  - `DELETE /system/menu/{menuId}` - 删除菜单
  - `GET /system/menu/treeselect` - 菜单树选择

#### 部门管理
- **SysDeptController**: 组织架构树形管理
  - `GET /system/dept/list` - 部门列表
  - `POST /system/dept` - 新增部门
  - `PUT /system/dept` - 修改部门
  - `DELETE /system/dept/{deptId}` - 删除部门

#### 岗位管理
- **SysPostController**: 岗位信息维护
  - `GET /system/post/list` - 岗位列表
  - `POST /system/post` - 新增岗位
  - `PUT /system/post` - 修改岗位
  - `DELETE /system/post/{ids}` - 删除岗位

#### 字典管理
- **SysDictTypeController**: 字典类型管理
- **SysDictDataController**: 字典数据管理
  - `GET /system/dict/type/list` - 字典类型列表
  - `GET /system/dict/data/list` - 字典数据列表
  - `GET /system/dict/data/type/{dictType}` - 根据类型查询字典数据

#### 参数设置
- **SysConfigController**: 系统参数配置
  - `GET /system/config/list` - 参数列表
  - `GET /system/config/configKey/{configKey}` - 根据键名查询参数值
  - `PUT /system/config` - 修改参数
  - `PUT /system/config/resetCache` - 刷新缓存

#### 通知公告
- **SysNoticeController**: 系统公告发布
  - `GET /system/notice/list` - 公告列表
  - `POST /system/notice` - 新增公告
  - `PUT /system/notice` - 修改公告
  - `DELETE /system/notice/{noticeId}` - 删除公告

#### 租户管理
- **SysTenantController**: 多租户管理
- **SysTenantPackageController**: 租户套餐管理
  - `GET /system/tenant/list` - 租户列表
  - `POST /system/tenant` - 新增租户
  - `PUT /system/tenant` - 修改租户
  - `DELETE /system/tenant/{tenantId}` - 删除租户

#### 客户端管理
- **SysClientController**: OAuth 客户端管理
  - `GET /system/client/list` - 客户端列表
  - `POST /system/client` - 新增客户端
  - `PUT /system/client` - 修改客户端
  - `PUT /system/client/resetSecret` - 重置密钥

#### 社交登录
- **SysSocialController**: 第三方登录管理
  - `GET /system/social/list` - 社交账户列表
  - `DELETE /system/social/{socialId}` - 删除社交账户

#### 数字人管理
- **DigitalHumanController**: 数字人配置管理
  - `GET /system/digitalHuman/list` - 数字人列表
  - `POST /system/digitalHuman` - 新增数字人
  - `PUT /system/digitalHuman` - 修改数字人

### 监控管理控制器 (`monitor/`)

#### 操作日志
- **SysOperlogController**: 操作日志查询和清理
  - `GET /monitor/operlog/list` - 操作日志列表
  - `DELETE /monitor/operlog/{operIds}` - 删除操作日志
  - `DELETE /monitor/operlog/clean` - 清空操作日志

#### 登录日志
- **SysLogininforController**: 登录日志管理
  - `GET /monitor/logininfor/list` - 登录日志列表
  - `DELETE /monitor/logininfor/{infoIds}` - 删除登录日志

#### 在线用户
- **SysUserOnlineController**: 在线用户监控
  - `GET /monitor/online/list` - 在线用户列表
  - `DELETE /monitor/online/{tokenId}` - 强退用户

#### 缓存监控
- **CacheController**: Redis 缓存监控
  - `GET /monitor/cache` - 缓存信息
  - `DELETE /monitor/cache/refresh` - 刷新缓存
  - `GET /monitor/cache/{cacheName}` - 缓存详情

## 关键依赖与配置

### 核心依赖
```xml
<!-- 数据库相关 -->
<dependency>
    <groupId>org.dromara</groupId>
    <artifactId>unimed-common-mybatis</artifactId>
</dependency>

<!-- Web 相关 -->
<dependency>
    <groupId>org.dromara</groupId>
    <artifactId>unimed-common-web</artifactId>
</dependency>

<!-- 权限相关 -->
<dependency>
    <groupId>org.dromara</groupId>
    <artifactId>unimed-common-satoken</artifactId>
</dependency>

<!-- 多租户 -->
<dependency>
    <groupId>org.dromara</groupId>
    <artifactId>unimed-common-tenant</artifactId>
</dependency>
```

### API 服务依赖
- **unimed-api-system**: 系统服务 API 定义
- **unimed-api-resource**: 资源服务 API

## 数据模型

### 核心实体
- **SysUser**: 用户实体
- **SysRole**: 角色实体
- **SysMenu**: 菜单实体
- **SysDept**: 部门实体
- **SysPost**: 岗位实体
- **SysDict**: 字典实体
- **SysConfig**: 配置实体
- **SysTenant**: 租户实体

### 业务对象 (BO)
- **SysUserBo**: 用户业务对象
- **SysRoleBo**: 角色业务对象
- **SysMenuBo**: 菜单业务对象

### 视图对象 (VO)
- **SysUserVo**: 用户视图对象
- **SysRoleVo**: 角色视图对象
- **MenuTreeVo**: 菜单树视图对象

### 数据库表结构
```sql
-- 用户表
sys_user (id, username, password, nickname, email, phone, status, dept_id, create_time, update_time)

-- 角色表
sys_role (id, role_name, role_key, role_sort, status, create_time, update_time)

-- 菜单表
sys_menu (id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms)

-- 部门表
sys_dept (id, dept_name, parent_id, order_num, leader, phone, email, status, create_time, update_time)
```

## 测试与质量

### 测试策略
- **单元测试**: Service 层业务逻辑测试
- **集成测试**: Controller 层 API 测试
- **数据权限测试**: 多租户数据隔离测试

### 质量工具
- **数据权限**: `@DataPermission` 注解
- **操作日志**: `@Log` 注解
- **重复提交**: `@RepeatSubmit` 注解
- **参数校验**: Jakarta Validation

### 监控指标
- 用户操作频率统计
- 权限变更审计
- 数据访问量监控
- 租户资源使用情况

## 常见问题 (FAQ)

### Q1: 如何实现数据权限？
A: 使用 `@DataPermission` 注解，支持部门级、用户级数据权限控制。

### Q2: 如何扩展新的业务实体？
A: 参考现有实体结构，添加对应的 Entity、Mapper、Service、Controller 层。

### Q3: 多租户数据隔离是如何实现的？
A: 通过 `TenantHelper` 自动在 SQL 中添加租户条件，实现数据隔离。

### Q4: 如何优化大数据量查询性能？
A: 使用分页查询、索引优化、缓存策略等方式提升性能。

## 相关文件清单

### 核心文件
- `src/main/java/org/dromara/system/UnimedSystemApplication.java` - 启动类
- `src/main/java/org/dromara/system/controller/system/` - 系统管理控制器
- `src/main/java/org/dromara/system/service/` - 业务服务层
- `src/main/java/org/dromara/system/mapper/` - 数据访问层

### 配置文件
- `src/main/resources/application.yml` - 主配置文件
- `src/main/resources/mapper/system/` - MyBatis 映射文件

### 数据库脚本
- 数据库表结构和初始数据通过 SQL 脚本管理

## 变更记录 (Changelog)

- **2025-12-16 09:30:24** - 初始化系统模块文档，完成接口分析和数据模型梳理