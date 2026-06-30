# persipa-cloud-common

Persipa 项目使用的公共 Java 类库与 Spring Boot 自动配置集合。项目采用 Maven
多模块结构，让非 Spring 项目只引入轻量公共模型，Spring Boot 项目按需启用自动配置。

当前版本：`4.2.0-SNAPSHOT`

## 模块说明

| 模块 | 用途 | Spring 依赖 |
| --- | --- | --- |
| `persipa-cloud-common-core` | REST 响应模型、分页响应模型、枚举工具 | 无 |
| `persipa-cloud-common-spring-boot-starter` | Jackson、MyBatis-Plus、Springdoc、应用版本信息自动配置 | 有 |

模块依赖关系为：

```text
persipa-cloud-common-spring-boot-starter
└── persipa-cloud-common-core
```

starter 会传递引入 core，Spring Boot 项目不需要重复声明 core。

## 环境要求

- JDK 17 或更高版本
- 使用仓库自带的 Maven Wrapper 构建

```shell
./mvnw clean verify
```

如果本机没有配置默认 JDK，请先设置 `JAVA_HOME`。

## 依赖引入

### 非 Spring 项目

只引入 core，不会传递 Spring、MyBatis-Plus、Springdoc 或 Jackson：

```xml
<dependency>
    <groupId>site.persipa</groupId>
    <artifactId>persipa-cloud-common-core</artifactId>
    <version>4.2.0-SNAPSHOT</version>
</dependency>
```

core 提供：

- 统一响应模型 `Result<T>`
- 与持久层实现解耦的分页响应模型 `PageResponse<T>`
- 枚举查找工具 `EnumFindHelper`

### Spring Boot 项目

引入 starter 即可同时使用自动配置和 core 中的公共类型：

```xml
<dependency>
    <groupId>site.persipa</groupId>
    <artifactId>persipa-cloud-common-spring-boot-starter</artifactId>
    <version>4.2.0-SNAPSHOT</version>
</dependency>
```

MyBatis-Plus 和 Springdoc 是可选集成。业务项目使用相应能力时，需要显式添加对应依赖。

MyBatis-Plus 示例：

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot4-starter</artifactId>
</dependency>
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-jsqlparser</artifactId>
</dependency>
```

Springdoc 示例：

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-common</artifactId>
</dependency>
```

## 从旧坐标迁移

原坐标：

```xml
<artifactId>persipa-cloud-common</artifactId>
```

重构后该坐标是 Maven 聚合父 POM，不再提供 Java 类。下游项目需要根据用途迁移：

- 非 Spring 项目：改为 `persipa-cloud-common-core`
- Spring Boot 项目：改为 `persipa-cloud-common-spring-boot-starter`

现有 Java 包名和配置键没有变化，一般不需要修改 import 或应用配置。

## 自动配置

自动配置默认关闭，业务项目按需开启：

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

### Jackson

`persipa.cloud.json.jackson.java-time-module=true` 时注册 `JavaTimeModule`，统一
`LocalDateTime` 的序列化和反序列化格式为 `yyyy-MM-dd HH:mm:ss`。业务项目已声明
`JavaTimeModule` Bean 时，公共配置自动退让。

### MyBatis-Plus

- `persipa.cloud.orm.mybatis.auto-fill-time=true`：自动填充实体的 `createTime` 和
  `updateTime` 字段。
- `persipa.cloud.orm.mybatis.pagination.enabled=true`：注册分页拦截器。
- `db-type` 为空时由 MyBatis-Plus 自动识别数据库类型。
- `overflow` 控制页码溢出行为。
- `max-limit` 限制单页最大记录数。

业务项目已声明 `MetaObjectHandler` 或 `MybatisPlusInterceptor` Bean 时，对应公共配置
自动退让。

### Springdoc OpenAPI

`persipa.cloud.openapi.enabled=true` 时注册默认 `OpenAPI` Bean，并应用标题、描述、
版本、服务条款和 server 列表。业务项目已声明 `OpenAPI` Bean 时自动退让。

### 应用版本信息

`persipa.cloud.app-version.print=true` 时，在 Spring 应用 Ready 后打印应用名称和版本。
业务应用需要生成 Spring Boot `build-info.properties` 才能输出真实名称和版本；缺失时
仅打印通用启动完成信息。

## 公共响应模型

`Result<T>` 是不可变 `record`：

```java
return Result.success(data);
return Result.fail("参数错误");
```

字段说明：

- `code`：成功为 `0`，失败为 `-1`
- `message`：文本信息，保证非空
- `payload`：业务数据
- `timestamp`：`Instant` 时间戳，保证非空

`PageResponse<T>` 同样是不依赖持久层框架的不可变 `record`，字段包括 `list`、
`total`、`pageSize`、`pageNumber` 和 `totalPages`。传入空的 `list` 时会规范化为空数组。

## MyBatis-Plus 分页转换

`MybatisPageResponseConverter` 位于 starter，可将 MyBatis-Plus 的 `IPage` 转换为
core 中的 `PageResponse`：

```java
Page<User> page = userMapper.selectPage(new Page<>(pageNumber, pageSize), queryWrapper);

PageResponse<User> response = MybatisPageResponseConverter.from(page);
return Result.success(response);
```

转换为 DTO：

```java
PageResponse<UserResponse> response = MybatisPageResponseConverter.from(
        page,
        user -> new UserResponse(user.getId(), user.getName())
);
```

该转换器的 API 直接使用 MyBatis-Plus 类型，因此使用它的项目必须显式引入
MyBatis-Plus。
