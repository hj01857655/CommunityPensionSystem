# 系统设置模块优化方案

## 1. 问题描述

当前系统设置页面存在以下问题：
- 使用静态数据，无法实际修改和持久化系统配置
- 数据备份和恢复功能只是UI展示，没有实际功能
- 安全设置无法真实影响系统的安全策略

## 2. 优化方案

### 2.1 前端优化
1. 创建系统配置相关的API调用模块 `config.js`，包含：
   - 获取系统设置
   - 获取安全设置
   - 更新系统设置
   - 更新安全设置
   - 备份文件管理相关API

2. 改造 `SystemSetting.vue`：
   - 在组件加载时从后端API获取实际配置
   - 添加加载状态指示器，提升用户体验
   - 为所有操作按钮绑定实际的API调用
   - 添加错误处理和用户反馈

### 2.2 后端优化
1. 创建 `ConfigController`：提供系统配置和安全设置相关的API接口
2. 创建 `BackupController`：提供数据库备份和恢复相关的API接口
3. 创建 `ConfigService` 接口：定义配置管理的业务逻辑
4. 创建 `BackupService` 接口：定义备份恢复的业务逻辑
5. 创建 `Config` 实体类：映射数据库中的配置表

## 3. 数据映射关系

### 3.1 前端字段与后端配置的映射关系

| 前端字段名 | 后端配置键名 |
|----------|------------|
| systemName | sys.name |
| logo | sys.logo |
| description | sys.description |
| icp | sys.icp |
| contactPhone | sys.contact.phone |
| contactEmail | sys.contact.email |
| version | sys.version |
| passwordMinLength | security.password.minLength |
| passwordComplexity | security.password.complexity |
| loginFailLockCount | security.login.failLockCount |
| accountLockTime | security.account.lockTime |
| sessionTimeout | security.session.timeout |
| enableCaptcha | sys.account.captchaEnabled |
| enableIpRestriction | security.ip.restriction |
| allowedIps | security.ip.allowedList |

## 4. 实现步骤

### 4.1 实现顺序
1. 创建前端API模块 `config.js`
2. 修改 `SystemSetting.vue` 文件，对接API
3. 创建后端相关的接口和实体类
4. 实现后端服务层

### 4.2 后续完善
1. 实现 `ConfigServiceImpl` 类，完成配置管理的业务逻辑
2. 实现 `BackupServiceImpl` 类，完成备份恢复的业务逻辑
3. 创建配置的Mapper接口，完成数据库操作
4. 编写单元测试，确保功能正常

## 5. 预期效果
1. 系统管理员可以通过界面修改系统配置，配置会实时生效并持久化
2. 安全策略可以通过界面调整，实际影响系统的安全机制
3. 数据备份功能可以实际创建数据库备份文件，并支持恢复操作
4. 整体用户体验更流畅，操作有明确的反馈

## 6. 注意事项
1. 配置更新需要考虑并发情况
2. 敏感操作（如数据恢复）需要二次确认和密码校验
3. 备份恢复功能仅限管理员使用
4. 配置变更应记录日志，便于审计