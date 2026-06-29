# persipa-cloud-common

私有项目可复用的 Spring Boot 公共依赖库，提供以下能力：

- 统一 REST 响应模型 `Result<T>`
- 与持久层实现解耦的分页响应模型 `PageResponse<T>`
- Jackson `LocalDateTime` 序列化/反序列化格式支持
- MyBatis-Plus 自动填充 `createTime` / `updateTime`
- MyBatis-Plus 分页插件自动配置
- Springdoc OpenAPI 通用配置
- Spring 应用启动后打印应用版本信息

本项目默认遵循“非侵入式”原则：自动配置默认关闭，按需开启。

## 版本

当前项目版本：`4.2.0-SNAPSHOT`

## 依赖引入

```xml
<dependency>
    <groupId>site.persipa</groupId>
    <artifactId>persipa-cloud-common</artifactId>
    <version>4.2.0-SNAPSHOT</version>
</dependency>
```

使用 MyBatis-Plus 分页能力的服务还需显式引入分页解析模块：

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-jsqlparser</artifactId>
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
        pagination:
          enabled: true
          db-type: mysql
          overflow: false
          max-limit: 500
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
- `persipa.cloud.orm.mybatis.pagination.enabled=true`
  启用后自动注册 MyBatis-Plus 分页拦截器。`db-type` 留空时自动识别数据库类型，
  `overflow` 控制页码溢出行为，`max-limit` 限制单页最大记录数。业务方声明自己的
  `MybatisPlusInterceptor` Bean 后，公共库自动退让。
- `persipa.cloud.openapi.enabled=true`  
  启用后注册默认 `OpenAPI` Bean；业务方可自定义同类型 Bean 覆盖。
- `persipa.cloud.app-version.print=true`  
  启用后在应用 Ready 后向控制台打印应用名称与版本。业务应用需要生成 Spring Boot
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

## 分页查询与响应

数据访问层可以正常使用 MyBatis-Plus 的 `Page` 执行分页查询，但 REST API 返回
`PageResponse<T>`，避免向调用方暴露 MyBatis-Plus 的排序、SQL 优化等内部字段：

```java
Page<User> page = userMapper.selectPage(new Page<>(pageNumber, pageSize), queryWrapper);

PageResponse<User> response = MybatisPageResponseConverter.from(page);
return Result.success(response);
```

需要将实体转换为 DTO 时，可以在分页转换时同步映射记录：

```java
PageResponse<UserResponse> response = MybatisPageResponseConverter.from(
        page,
        user -> new UserResponse(user.getId(), user.getName())
);
```

`MybatisPageResponseConverter` 依赖 MyBatis-Plus，但 `PageResponse` 本身不包含任何
MyBatis-Plus 类型，因此 REST API 的响应契约仍与持久层实现解耦。

`PageResponse<T>` 的字段为 `list`、`total`、`pageSize`、`pageNumber` 和
`totalPages`；传入空的 `list` 时会统一输出空数组。

## 兼容与覆盖策略

- Jackson、MyBatis、OpenAPI 配置都使用 `@ConditionalOnMissingBean`，支持下游按需覆盖。
- MyBatis、MyBatis 分页解析器与 OpenAPI 相关依赖在 `pom.xml` 中为 `optional`，
  避免无关服务被强耦合。
