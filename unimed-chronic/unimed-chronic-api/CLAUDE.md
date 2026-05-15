[根目录](../../CLAUDE.md) > [unimed-chronic](../) > **unimed-chronic-api**

# Unimed Chronic API 慢病接口模块

## 变更记录 (Changelog)

- **2026-05-15** - 初始化慢病 API 模块文档

## 模块职责

慢病域 API 定义层，仅包含接口定义和数据传输对象，无业务实现。供其他模块通过 Dubbo 调用慢病域服务。

## 模块结构

```
unimed-chronic-api/
  └── pom.xml    -- 仅声明依赖，无源代码
```

## 依赖关系

作为慢病域 API 骨架，被 unimed-chronic-biz 依赖：
```xml
<dependency>
    <groupId>org.dromara</groupId>
    <artifactId>unimed-chronic-api</artifactId>
</dependency>
```

## 相关文件清单

- `pom.xml` - 模块定义
