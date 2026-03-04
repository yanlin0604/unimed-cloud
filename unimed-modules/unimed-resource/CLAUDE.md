[根目录](../../CLAUDE.md) > [unimed-modules](../) > **unimed-resource**

# Unimed Resource 资源服务模块

## 变更记录 (Changelog)

- **2026-03-04 09:57:40** - 初始化资源服务模块文档，完成控制器、服务和数据模型分析

## 模块职责

提供统一的文件存储（OSS）、邮件发送和短信发送服务，支持多种存储后端和通信渠道，通过 Dubbo RPC 为其他微服务提供远程调用能力。

## 入口与启动

### 启动类
- **文件**: `src/main/java/org/dromara/resource/UnimedResourceApplication.java`（推测）
- **端口**: 9204
- **Nacos 服务名**: `unimed-resource`

### 配置文件
- **主配置**: `src/main/resources/application.yml`
- **Nacos 配置**: `script/config/nacos/unimed-resource.yml`

## 对外接口

### REST 控制器

#### SysOssController - 文件存储管理
- **路径**: `/oss`
- **接口**:
  - `GET /list` - 查询 OSS 对象列表（需权限 `system:oss:list`）
  - `POST /upload` - 上传文件
  - `GET /download/{ossId}` - 下载文件
  - `DELETE /{ossIds}` - 删除文件

#### SysOssConfigController - 存储配置管理
- **路径**: `/oss/config`（推测）
- **功能**: OSS 存储配置的增删改查

#### SysEmailController - 邮件服务
- **功能**: 邮件发送管理

#### SysSmsController - 短信服务
- **功能**: 短信发送管理

### Dubbo 远程服务
| 实现类 | 对应接口 | 职责 |
|-------|---------|------|
| RemoteFileServiceImpl | RemoteFileService | 远程文件上传/下载 |
| RemoteMailServiceImpl | RemoteMailService | 远程邮件发送 |
| RemoteSmsServiceImpl | RemoteSmsService | 远程短信发送 |
| RemoteMessageServiceImpl | RemoteMessageService | 远程消息推送 |

## 数据模型

### 核心实体
- **SysOss**: OSS 对象存储记录
- **SysOssConfig**: OSS 存储配置（存储类型、访问密钥等）
- **SysOssExt**: OSS 扩展信息

### 业务对象 / 视图对象
- **SysOssBo**: OSS 业务对象
- **SysOssConfigBo**: 配置业务对象
- **SysOssVo**: OSS 视图对象
- **SysOssUploadVo**: 上传结果视图
- **SysOssConfigVo**: 配置视图对象
- **SysOssVoConvert**: MapStruct 转换器

### Mapper 映射
- `mapper/resource/SysOssMapper.xml`
- `mapper/resource/SysOssConfigMapper.xml`

## 关键依赖

- unimed-common-oss - 对象存储核心（AWS S3 SDK）
- unimed-common-sms - 短信服务（SMS4J）
- unimed-common-mail - 邮件服务
- unimed-common-mybatis - 数据库访问
- unimed-common-dubbo - Dubbo RPC

## 相关文件清单

- `src/main/java/org/dromara/resource/controller/` - 4 个控制器
- `src/main/java/org/dromara/resource/domain/` - 3 个实体 + bo/vo/convert
- `src/main/java/org/dromara/resource/mapper/` - 2 个 Mapper
- `src/main/java/org/dromara/resource/service/` - 2 个服务接口 + 2 个实现
- `src/main/java/org/dromara/resource/dubbo/` - 4 个 Dubbo 远程服务实现
- `src/main/java/org/dromara/resource/runner/ResourceApplicationRunner.java` - 启动运行器
- `Dockerfile` - Docker 构建文件
