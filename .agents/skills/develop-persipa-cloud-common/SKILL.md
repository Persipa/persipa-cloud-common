---
name: develop-persipa-cloud-common
description: 在 Persipa Cloud Common 仓库内开发、审查和维护 core 公共 API、Spring Boot starter 自动配置、可选集成及项目文档。面向库的维护者；业务项目接入和使用该库时使用 use-persipa-cloud-common。
---

# Persipa Cloud Common 项目开发

本 skill 用于维护公共依赖包本身。先读取仓库 `AGENTS.md`、目标代码、相关测试和 README；以实际模块边界和当前版本为准。调用方接入指导属于 `use-persipa-cloud-common`。

## 选择模块和维护契约

- 不依赖框架的模型、枚举和工具能力放在 core。保持 core 的 compile/runtime 依赖树不含 Spring、MyBatis、Springdoc、Jackson 等框架。
- 使用 Spring Boot、MyBatis-Plus、Springdoc 或其他框架类型的实现放在 starter。starter 依赖 core，框架集成保持可选，不让缺少可选类的应用启动失败。
- 改动公开 API 时检查调用方兼容性、空值与集合规范化行为；同步 README 的用法和迁移说明。避免只为 starter 的便利性污染 core 模型。
- 沿用 `site.persipa.common` 包结构和 `persipa.cloud.*` 配置前缀；确需破坏性变更时先明确迁移影响。

## 自动配置

新增或修改自动配置时检查默认关闭、显式启用、`@ConditionalOnClass`、`@ConditionalOnProperty` 和 `@ConditionalOnMissingBean` 的适用性。新增自动配置类时同步维护 `AutoConfiguration.imports`。检查配置属性、README 示例与实际行为一致，避免覆盖调用方已有 Bean。

## 调用方文档同步

维护 README 或其他调用方文档时，根据本次变更检查 `use-persipa-cloud-common/SKILL.md` 及其 `references/consumer-integration.md`。如果依赖坐标、版本适用范围、公共 API、配置项、默认行为、可选依赖或迁移方式影响调用方，更新对应指导，保持与代码和 README 一致。区分开发分支能力与已发布版本；只调整受影响的内容。纯内部实现或测试调整不改变调用方行为时，无需改写调用方技能。

## 测试和验证

代码变更先使用 `persipa-cloud-common-testing` 判断需要保护的风险与最小测试范围；只有确实编写 Spring Boot 4 测试代码时才进一步使用 `spring-boot-4-testing-code`。按 `AGENTS.md` 运行相应模块测试、Reactor 构建及必要的 core 依赖树核验。不能运行的验证明确说明原因。
