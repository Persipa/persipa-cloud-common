---
name: persipa-cloud-common-testing
description: 为 Persipa Cloud Common 的代码变更判断是否需要测试、选择最小充分的契约与自动配置验证，并审查或精简现有测试。仅在确实需要 Spring Boot 4 测试设施时组合使用 spring-boot-4-testing-code。
---

# Persipa Cloud Common 测试决策

本 skill 决定本仓库是否新增测试、保护哪些风险以及何时停止。以 `AGENTS.md` 的模块边界和构建要求为准。`spring-boot-4-testing-code` 只在需要 Spring 测试设施时负责具体 API 用法，不决定测试数量。

## 判断是否需要测试

先阅读本次差异、目标实现、相邻测试和公开文档。仅针对现有测试未保护的可观察行为补测试：

- core 的公开 API、结果或分页语义、空集合规范化、异常行为发生变化；
- starter 的配置默认值、属性绑定、条件装配、可选类缺失时的行为或自定义 Bean 退让发生变化；
- 修复了可能复发的缺陷，且能用稳定的行为断言保护；
- 依赖或配置变更可能破坏 core 的轻量依赖边界，或使 starter 在缺少可选依赖时无法启动。

纯文档、注释、格式、机械重命名和不改变行为的接线通常不新增测试。现有测试已覆盖变化时，维护必要的断言即可。公开 API 变化仍须确认已有或新增测试保护其契约，并说明迁移影响。

## 选择最小验证

- core 优先使用普通 JUnit Jupiter 和 AssertJ；不引入 Spring 测试依赖。
- starter 仅在装配行为是验证目标时使用 `ApplicationContextRunner`、`WebApplicationContextRunner` 等最小上下文。需要 Spring Boot 4 测试设施的选择或 API 指导时，再使用 `spring-boot-4-testing-code`。
- 对自动配置按实际适用性验证默认关闭、显式启用、可选类缺失和自定义 Bean 退让。先复用现有测试，不为每个配置类机械复制四套测试。
- 断言公开结果、Bean 是否存在、有效配置或外部可观察行为；避免穷举内部调用和所有属性字段。
- 一个测试保护一个独立回归风险。关键成功、失败或边界行为得到保护后停止扩展，不以覆盖率或测试方法数量设配额。

## 验证与交付

先运行受影响模块的测试；提交前按 `AGENTS.md` 验证 Reactor 构建和 core 依赖边界。测试应离线、确定、可重复。未新增测试时，在交付说明中简述现有保障或变更不涉及行为的依据。
