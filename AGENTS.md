# AGENTS.md

本文档用于约束在本仓库中工作的开发者和编码代理。项目文档、提交说明和新增注释优先
使用中文；Java 标识符、标准技术名词和公开配置键保持英文。

## 项目结构

本仓库是 Java 17 Maven 多模块项目：

- `persipa-cloud-common-core`：纯 JDK 公共能力。
- `persipa-cloud-common-spring-boot-starter`：Spring Boot 自动配置和可选框架集成。
- 根 `pom.xml`：聚合、版本、插件和发布配置，不承载 Java 源码。

依赖方向只能是 starter 到 core，禁止 core 反向依赖 starter。

## 模块边界

### core

core 面向非 Spring 项目，compile/runtime 依赖树必须保持轻量：

- 禁止引入 Spring、Spring Boot、MyBatis、Springdoc 或 Jackson。
- 禁止在源码中引用上述框架的类型或注解。
- 通用响应模型、分页模型和不依赖框架的工具类放在该模块。
- 测试依赖使用 JUnit Jupiter 和 AssertJ，不使用
  `spring-boot-starter-test`。

### spring-boot-starter

starter 用于 Spring Boot 自动配置：

- 非 optional 地依赖 core，确保 Spring 项目可直接使用公共模型。
- MyBatis-Plus、JSqlParser 和 Springdoc 保持 optional，由业务项目显式引入。
- 自动配置默认关闭，通过 `persipa.cloud.*` 配置键按需开启。
- 自动配置应使用 `@ConditionalOnClass`、`@ConditionalOnProperty` 和
  `@ConditionalOnMissingBean` 保持非侵入性。
- 新增自动配置类时，同步维护
  `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`。

`MybatisPageResponseConverter` 属于 starter，因为其公开 API 使用 MyBatis-Plus 类型。

## 构建与测试

使用 JDK 17 和仓库自带的 Maven Wrapper：

```shell
./mvnw clean verify
```

常用的局部验证命令：

```shell
./mvnw -pl persipa-cloud-common-core test
./mvnw -pl persipa-cloud-common-spring-boot-starter -am test
./mvnw -pl persipa-cloud-common-core dependency:tree
```

提交前至少确认：

- 根工程 Reactor 构建成功。
- 变更模块的测试通过。
- core 的 compile/runtime 依赖树不包含 Spring 或其他框架依赖。
- 新增或修改的自动配置覆盖默认关闭、启用、缺少可选类和自定义 Bean 退让场景。
- README 中的依赖坐标、配置示例与代码一致。

## 编码约定

- 保持现有 `site.persipa.common` 包结构，除非任务明确要求破坏性迁移。
- 公共 API 变更需要补充测试和迁移说明。
- 不在 core 中为框架便利性污染通用模型。
- 优先使用不可变类型；集合入参为空时保持现有规范化行为。
- 配置属性沿用 `persipa.cloud.*` 前缀，未经明确设计不要重命名。
- 可选集成不能因为类缺失导致应用启动失败。

## Issue 协作

本项目使用 [YouTrack PCC](https://youtrack.nuc.persipa.site/projects/PCC) 管理 issue。

- 开始实现前确认任务关联的 `PCC-<编号>` issue（如果已提供）。
- 分支、提交和合并请求按团队约定引用对应 issue 编号。
- README、API 或配置行为发生变化时，在 issue 或合并请求中说明迁移影响。
- 未获得明确授权时，不擅自创建、关闭或修改 YouTrack issue 状态。
