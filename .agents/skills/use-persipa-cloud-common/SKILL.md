---
name: use-persipa-cloud-common
description: 帮助业务项目按版本接入、配置、使用和排查 Persipa Cloud Common，查询版本历史与已实现功能。处理 core 或 Spring Boot starter 选型、persipa.cloud.* 配置、公共响应模型、MyBatis-Plus、Springdoc、MVC CORS、应用版本接口及旧坐标迁移。
---

# Persipa Cloud Common 调用方集成

## 工作流

1. 读取调用方项目的 Maven 依赖管理，确定最终解析的 Persipa Cloud Common 版本；对照
   [版本历史](references/consumer-integration.md#版本历史)和
   [已实现功能](references/consumer-integration.md#已实现功能)，确认所需能力存在于该版本。
2. 仅在非 Spring 项目中引入 `persipa-cloud-common-core`；在 Spring Boot 项目中引入 `persipa-cloud-common-spring-boot-starter`。不要将聚合父 POM `persipa-cloud-common` 当作 Java 类库依赖。
3. 使用公共 API 时优先保持框架无关：用 `Result<T>` 封装 REST 返回值，用 `PageResponse<T>` 暴露分页结果。
4. 仅在业务确实需要时启用 `persipa.cloud.*` 自动配置；保留业务项目已有 Bean 的优先级。
5. 对 MyBatis-Plus 和 Springdoc，先显式引入调用方所需的可选依赖，再开启对应配置；使用
   乐观锁时还需引入 `mybatis-plus-extension`，并在实体版本字段上使用 `@Version`。
6. 处理 MyBatis-Plus 自动填充时，先确认实体字段带有正确的 `@TableField(fill = ...)`，再启用
   `persipa.cloud.orm.mybatis.auto-fill-time=true`。默认处理器仅严格填充 `Instant` 类型的
   `createTime` 和 `updateTime`；字段名可通过 `create-time-field`、`update-time-field` 调整。
   调用方已有 `MetaObjectHandler` Bean 时自动配置退让，应保留并使用其自定义填充策略。
7. 业务方需要跨域访问时，先确认实际依赖版本包含 MVC CORS 能力，再按
   [调用方参考](references/consumer-integration.md#spring-mvc-cors) 配置可信来源并显式启用；
   已由业务配置、Spring Security 或网关处理 CORS 时，先确认配置归属，避免重复规则。
8. 修改依赖、配置或 API 使用后，运行调用方项目已有的构建和测试命令。

## 参考资料

在做模块选择、写依赖或配置、处理常见冲突和迁移时，读取 [references/consumer-integration.md](references/consumer-integration.md)。

该参考包含模块坐标、版本历史、已实现功能、公共 API、自动配置、排障路径及 source JAR 核验方式。

## 版本与功能查询

用户询问某版本支持哪些能力时，读取参考资料中的版本历史与已实现功能，再按调用方实际解析版本回答。区分当前源码、开发快照和已发布制品；不要将 `4.3.0` 新增的 CORS 能力归入 `4.2.2`。

## 版本行为核验

只在文档、异常堆栈或运行结果与调用方实际解析的版本不一致时下载 matching source JAR。先确认最终版本，再使用参考资料中的 Maven 命令；不要假定最新版本，也不要要求克隆本仓库。

优先依据公开 API 和配置文档完成接入。source JAR 仅用于核验该版本的实现细节、自动配置条件或异常行为。
