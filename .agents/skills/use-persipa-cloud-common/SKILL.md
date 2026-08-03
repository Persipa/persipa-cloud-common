---
name: use-persipa-cloud-common
description: 帮助业务项目接入、配置、使用和排查 Persipa Cloud Common。处理选择 core 或 Spring Boot starter、配置 persipa.cloud.*、使用 Result 或 PageResponse、接入 MyBatis-Plus 或 Springdoc、排查应用版本接口、从旧聚合坐标迁移，或需要核验依赖版本实际行为时使用。
---

# Persipa Cloud Common 调用方集成

## 工作流

1. 读取调用方项目的 Maven 依赖管理，确定最终解析的 Persipa Cloud Common 版本。
2. 仅在非 Spring 项目中引入 `persipa-cloud-common-core`；在 Spring Boot 项目中引入 `persipa-cloud-common-spring-boot-starter`。不要将聚合父 POM `persipa-cloud-common` 当作 Java 类库依赖。
3. 使用公共 API 时优先保持框架无关：用 `Result<T>` 封装 REST 返回值，用 `PageResponse<T>` 暴露分页结果。
4. 仅在业务确实需要时启用 `persipa.cloud.*` 自动配置；保留业务项目已有 Bean 的优先级。
5. 对 MyBatis-Plus 和 Springdoc，先显式引入调用方所需的可选依赖，再开启对应配置。
6. 修改依赖、配置或 API 使用后，运行调用方项目已有的构建和测试命令。

## 参考资料

在做模块选择、写依赖或配置、处理常见冲突和迁移时，读取 [references/consumer-integration.md](references/consumer-integration.md)。

该参考包含当前发布坐标、公共 API、自动配置、排障路径及 source JAR 核验方式。

## 版本行为核验

只在文档、异常堆栈或运行结果与调用方实际解析的版本不一致时下载 matching source JAR。先确认最终版本，再使用参考资料中的 Maven 命令；不要假定最新版本，也不要要求克隆本仓库。

优先依据公开 API 和配置文档完成接入。source JAR 仅用于核验该版本的实现细节、自动配置条件或异常行为。
