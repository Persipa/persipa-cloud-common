# Persipa Cloud Common 调用方参考

## 目录

- [模块与坐标](#模块与坐标)
- [公共 API](#公共-api)
- [Spring Boot 自动配置](#spring-boot-自动配置)
- [常见问题与迁移](#常见问题与迁移)
- [使用 source JAR 核验版本行为](#使用-source-jar-核验版本行为)

## 模块与坐标

当前版本为 `4.2.2`，运行环境要求 JDK 17 或更高版本。本版本完善 MyBatis-Plus 自动填充集成指南。始终以调用方的 Maven 最终解析版本为准；不要在代码、配置或排障命令中假定某个版本一定存在。

### 非 Spring 项目

只引入轻量、无 Spring 依赖的 core：

```xml
<dependency>
    <groupId>site.persipa</groupId>
    <artifactId>persipa-cloud-common-core</artifactId>
    <version>${persipa-cloud-common.version}</version>
</dependency>
```

### Spring Boot 项目

引入 starter。它会传递引入 core，因此不要重复声明 core，除非调用方有明确的依赖管理需求。

```xml
<dependency>
    <groupId>site.persipa</groupId>
    <artifactId>persipa-cloud-common-spring-boot-starter</artifactId>
    <version>${persipa-cloud-common.version}</version>
</dependency>
```

`persipa-cloud-common` 是 Maven 聚合父 POM，不提供 Java 类。不要在业务代码依赖中使用该旧聚合坐标。

## 公共 API

- 使用不可变 `Result<T>` 作为统一 REST 响应：`Result.success(payload)`、`Result.success()` 和 `Result.fail(message)`。成功状态码为 `0`，失败状态码为 `-1`；`message` 和 `timestamp` 会被规范化为非空值。
- 使用框架无关的不可变 `PageResponse<T>` 暴露列表、总数、页大小、页码和总页数。传入的 `list` 为 `null` 时会规范化为空列表。
- 使用 `EnumFindHelper<E, T>` 为枚举构建键到枚举值的查找，并显式提供默认值。
- 仅在项目已显式引入 MyBatis-Plus 时，使用 starter 中的 `MybatisPageResponseConverter` 将 `IPage` 转换为 `PageResponse`；可选择传入 mapper 将记录转为 DTO。

## Spring Boot 自动配置

所有公共自动配置默认关闭。仅为调用方需要的能力设置对应 `persipa.cloud.*` 配置。业务项目已有同类 Bean 时，公共自动配置应退让，不要为了覆盖默认行为移除业务 Bean。

### Jackson 时间类型

启用 `persipa.cloud.json.jackson.java-time-module=true` 注册 `JavaTimeModule`，并使用 `yyyy-MM-dd HH:mm:ss` 格式处理 `LocalDateTime`。调用方已有 `JavaTimeModule` Bean 时自动配置退让。

### MyBatis-Plus

MyBatis-Plus 是可选集成。启用前显式引入调用方适用的 MyBatis-Plus starter；使用分页时还显式引入 `mybatis-plus-jsqlparser`，使用乐观锁时还显式引入 `mybatis-plus-extension`。

```yaml
persipa:
  cloud:
    orm:
      mybatis:
        auto-fill-time: true
        create-time-field: createTime
        update-time-field: updateTime
        optimistic-lock:
          enabled: true
        pagination:
          enabled: true
          db-type: mysql
          overflow: false
          max-limit: 500
```

- `pagination.enabled=true` 注册分页拦截器；`db-type` 留空时交给 MyBatis-Plus 自动识别。
- `optimistic-lock.enabled=true` 注册乐观锁拦截器。实体版本字段必须使用 `@Version`，支持 `int`、`Integer`、`long`、`Long`、`Date`、`Timestamp` 和 `LocalDateTime`；整数版本会递增，新版本会回写实体。
- 乐观锁更新返回 `false` 通常表示版本不匹配；业务方应重新读取数据后决定重试或向用户提示冲突。`update(entity, wrapper)` 的 wrapper 不可复用。
- 同时启用乐观锁和分页时，公共配置按“乐观锁、分页”顺序组装同一个拦截器链，分页位于链尾。
- 调用方已有 `MetaObjectHandler` 或 `MybatisPlusInterceptor` Bean 时，使用其自定义实现；后者需由调用方自行加入乐观锁和分页插件。

#### 自动填充时间字段

`auto-fill-time` 默认关闭。调用方显式引入适用的 MyBatis-Plus starter 后，设置
`persipa.cloud.orm.mybatis.auto-fill-time=true`，starter 才会注册默认的
`MetaObjectHandler`。该处理器以 `Instant.now()` 严格填充默认名为 `createTime`、`updateTime`
的字段；可分别用 `create-time-field`、`update-time-field` 覆盖字段名。

实体字段必须通过 `@TableField` 声明填充策略，且字段类型应为 `Instant`，例如：

```java
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

import java.time.Instant;

public class AuditEntity {

    @TableField(fill = FieldFill.INSERT)
    private Instant createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Instant updateTime;
}
```

插入时会尝试填充创建时间和更新时间，更新时只会尝试填充更新时间。严格填充不会覆盖实体中已有的非空值，也不会以 `null` 填充字段。调用方已声明 `MetaObjectHandler` Bean 时，starter 自动配置会退让；需要用户、租户等审计字段或其他类型时，应由调用方实现自定义处理器。

更新操作必须传入实体才能触发自动填充：`update(entity, wrapper)` 中的 `entity` 不能为
`null`；仅调用 `update(wrapper)` 不会触发填充，应改为传入实体或手动设置更新时间字段。

### Springdoc OpenAPI

Springdoc 是可选集成。先显式引入 `springdoc-openapi-starter-common`，再启用：

```yaml
persipa:
  cloud:
    openapi:
      enabled: true
      title: My Service API
      description: Internal API docs
      version: v1
```

需要时再补充 `terms-of-service` 和 `servers`。调用方已有 `OpenAPI` Bean 时，使用该 Bean。

### 应用版本信息

```yaml
persipa:
  cloud:
    app-version:
      print: true
      endpoint:
        enabled: true
        path: /_version
```

- `print=true` 会在应用 Ready 后打印应用名称和版本。
- 版本接口仅在 Servlet Web 应用中注册；默认路径为 `/_version`。
- 调用方必须生成 Spring Boot `build-info.properties`。缺失时只会输出通用启动信息，版本接口会返回 `503 Service Unavailable`。
- 由调用方的 Spring Security 配置或网关控制版本接口访问权限。

## 常见问题与迁移

| 现象 | 处理方式 |
| --- | --- |
| 找不到 `Result`、`PageResponse` 或其他 Java 类 | 将旧的 `persipa-cloud-common` 聚合坐标改为 `persipa-cloud-common-core` 或 `persipa-cloud-common-spring-boot-starter`。 |
| starter 启动后没有 MyBatis-Plus 能力 | 显式添加匹配 Spring Boot 版本的 MyBatis-Plus starter；使用分页时同时添加 `mybatis-plus-jsqlparser`，使用乐观锁时同时添加 `mybatis-plus-extension`，然后启用所需配置。 |
| OpenAPI 自动配置没有生效 | 显式添加 Springdoc 依赖并设置 `persipa.cloud.openapi.enabled=true`；检查是否已有自定义 `OpenAPI` Bean。 |
| `/_version` 返回 503 | 生成 `build-info.properties`，确认 endpoint 已启用，并检查调用方最终解析的 starter 版本。 |
| 自动配置未替换业务自定义实现 | 这是预期的退让行为。调整调用方自定义 Bean 或配置，而不是依赖公共自动配置覆盖它。 |

## 使用 source JAR 核验版本行为

每次发布会与二进制 JAR 同步部署 matching source JAR 到 Maven 仓库，因此调用方可以按其实际解析的模块和版本获取源码。

仅在调用方的版本行为需要核验时执行以下步骤：

1. 使用 `mvn dependency:tree` 或调用方的依赖管理确认最终模块和版本。
2. 优先下载所有已声明依赖的 source JAR：

   ```shell
   ./mvnw dependency:sources
   # 没有 Maven Wrapper 时使用：mvn dependency:sources
   ```

3. 仅需核验一个模块时，按已解析的准确版本下载其 source JAR：

   ```shell
   mvn dependency:get \
     -Dartifact=site.persipa:persipa-cloud-common-spring-boot-starter:<version>:jar:sources
   ```

   将 artifactId 替换为 `persipa-cloud-common-core` 或 `persipa-cloud-common-spring-boot-starter`，并将 `<version>` 替换为调用方最终解析的版本。

4. 在本地 Maven 缓存或 IDE 附加的 source JAR 中核验实现、条件注解和异常路径。不要为了核验而克隆或依赖本项目工作区源码。
