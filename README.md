# persipa-cloud-common

私有项目可复用的 Spring Boot 公共依赖库，提供以下能力：

- 统一 REST 响应模型 `Result<T>`
- Jackson `LocalDateTime` 序列化/反序列化格式支持
- MyBatis-Plus 自动填充 `createTime` / `updateTime`
- Springdoc OpenAPI 通用配置
- Spring 应用启动后打印应用版本信息

本项目默认遵循“非侵入式”原则：自动配置默认关闭，按需开启。

## 版本

当前项目版本：`4.1.3`

## 依赖引入

```xml
<dependency>
    <groupId>site.persipa</groupId>
    <artifactId>persipa-cloud-common</artifactId>
    <version>4.1.3</version>
</dependency>
```

## 自动配置开关

```yaml
persipa:
  cloud:
    json:
      jackson:
        java-time-module: true
    orm:
      mybatis:
        auto-fill-time: true
    openapi:
      enabled: true
      title: My Service API
      description: Internal API docs
      version: v1
      terms-of-service: https://example.com/tos
      servers:
        - url: https://api.example.com
          description: Production
        - url: https://staging-api.example.com
          description: Staging
    app-version:
      print: true
```

说明：

- `persipa.cloud.json.jackson.java-time-module=true`  
  启用后统一 `LocalDateTime` 格式为 `yyyy-MM-dd HH:mm:ss`。
- `persipa.cloud.orm.mybatis.auto-fill-time=true`  
  启用后自动填充实体字段 `createTime`、`updateTime`。
- `persipa.cloud.openapi.enabled=true`  
  启用后注册默认 `OpenAPI` Bean；业务方可自定义同类型 Bean 覆盖。
- `persipa.cloud.app-version.print=true`  
  启用后在应用 Ready 后通过日志打印应用名称与版本。业务应用需要生成 Spring Boot
  `build-info.properties` 才能打印真实名称和版本；缺失时打印通用启动完成信息。

## REST 响应模型

`Result<T>` 是不可变 `record`，用于 REST 响应：

- `code`: 状态码（当前成功为 `0`，失败为 `-1`）
- `message`: 文本信息（保证非空）
- `payload`: 业务数据
- `timestamp`: `Instant` 时间戳（保证非空）

使用示例：

```java
return Result.success(data);
return Result.fail("参数错误");
```

## 兼容与覆盖策略

- Jackson、MyBatis、OpenAPI 配置都使用 `@ConditionalOnMissingBean`，支持下游按需覆盖。
- MyBatis 与 OpenAPI 相关依赖在 `pom.xml` 中为 `optional`，避免无关服务被强耦合。
