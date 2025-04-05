# 社区养老系统开发规范

## 目录
1. [代码风格规范](#代码风格规范)
2. [注释规范](#注释规范)
3. [分层架构规范](#分层架构规范)
4. [DTO/VO 使用规范](#dto-vo-使用规范)
5. [接口规范](#接口规范)
6. [数据库规范](#数据库规范)
7. [安全规范](#安全规范)
8. [性能规范](#性能规范)
9. [测试规范](#测试规范)
10. [错误处理规范](#错误处理规范)
11. [Git 工作流规范](#git-工作流规范)
12. [部署规范](#部署规范)
13. [代码审查规范](#代码审查规范)
14. [版本发布规范](#版本发布规范)

## 代码风格规范

### 1. 命名规范
- 类名：大驼峰命名（PascalCase）
- 方法名：小驼峰命名（camelCase）
- 变量名：小驼峰命名（camelCase）
- 常量名：全大写下划线分隔（UPPER_SNAKE_CASE）
- 包名：全小写点分隔（lowercase.with.dots）

### 2. 代码格式化
- 使用统一的代码格式化工具
- 保持一致的缩进风格
- 适当的空行分隔
- 合理的代码分组

## 注释规范

### 1. 类注释
```java
/**
 * 用户管理控制器
 *
 * @author xxx
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
}
```

### 2. 方法注释
```java
/**
 * 创建用户
 *
 * @param dto 用户创建参数
 * @return 创建结果
 */
@PostMapping
public Result<Void> createUser(@Validated @RequestBody UserCreateDTO dto) {
}
```

### 3. 关键代码注释
```java
// 1. 参数校验
if (!dto.isValid()) {
    return Result.error("参数无效");
}

// 2. 业务处理
userService.createUser(dto);

// 3. 返回结果
return Result.success();
```

## 分层架构规范

### 1. 分层职责

#### 1.1 Controller 层职责
- 只处理 HTTP 请求/响应
- 参数校验使用 @Valid
- 返回统一包装结果(Result<T>)
- 调用 Service 处理业务
- 不包含业务逻辑

#### 1.2 Service 层职责
- 接口(Service)定义用 DTO 作为参数/返回值
- 实现类(ServiceImpl)处理 Entity 转换
- 业务逻辑只存在于 Service 层
- 事务注解在实现类
- 不负责参数校验

#### 1.3 Mapper 层职责
- 使用 MapStruct 替代 BeanUtils
- 复杂转换使用 @Mapping 配置
- 默认方法处理特殊转换逻辑
- 保持接口纯净不包含业务逻辑

#### 1.4 Entity 层职责
- 只包含与数据库表对应的字段
- 使用 JPA/MyBatisPlus 注解
- 不包含业务逻辑方法
- 字段类型与数据库类型严格对应

### 2. 数据流转规范

#### 2.1 请求流程
```
HTTP Request
  → Controller (@RequestBody DTO)
  → Service (DTO/Entity转换)
  → Mapper   (Entity)
```

#### 2.2 响应流程
```
Mapper  (Entity)
  → Service (Entity/DTO转换)
  → Controller (返回VO)
  → HTTP Response
```

### 3. 依赖注入规范
- 使用构造器注入
- 避免字段注入
- 合理使用单例

## DTO/VO 使用规范

### 1. DTO 规范

#### 1.1 基本规范
- 只包含数据传输需要的字段
- 使用 JSR-303 验证注解
- 可包含多个 Entity 的组合字段
- 字段命名与接口文档一致
- 禁止出现业务方法

#### 1.2 命名规范
- DTO 类名以 `DTO` 结尾
- 查询 DTO 以 `Query` 结尾
- 创建 DTO 以 `Create` 结尾
- 更新 DTO 以 `Update` 结尾

#### 1.3 使用场景
- 接收前端请求参数
- 接收外部接口参数
- 复杂查询条件封装
- 批量操作参数封装

#### 1.4 示例
```java
@Data
@Schema(description = "用户创建参数")
public class UserCreateDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度必须在2-20之间")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20之间")
    private String password;
    
    @Email(message = "邮箱格式不正确")
    private String email;
}
```

### 2. VO 规范

#### 2.1 基本规范
- 专为前端展示设计的数据结构
- 可包含格式化的日期/金额等字段
- 允许有数据转换方法
- 字段命名符合前端需求

#### 2.2 使用场景
- 返回给前端的数据
- 外部接口返回数据
- 复杂查询结果封装
- 需要脱敏的数据

#### 2.3 示例
```java
@Data
@Schema(description = "用户信息")
public class UserVO {
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "角色列表")
    private List<RoleVO> roles;
    
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
```

### 3. 转换规范

#### 3.1 MapStruct 使用
```java
@Mapper(componentModel = "spring")
public interface UserConverter {
    // DTO 转 Entity
    User toEntity(UserDTO dto);
    
    // Entity 转 VO
    UserVO toVO(User user);
    
    // 批量转换
    List<UserVO> toVOList(List<User> users);
    
    // 复杂对象转换
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "role.name", target = "roleName")
    UserDetailVO toDetailVO(User user, Role role);
}
```

#### 3.2 转换最佳实践
- 使用 MapStruct 替代 BeanUtils
- 避免手动编写转换代码
- 转换逻辑集中在 Converter 类中
- 合理使用 @Mapping 配置
- 使用默认方法处理特殊转换

## 接口规范

### 1. RESTful API 设计
- 使用 HTTP 方法表示操作
- 使用复数名词表示资源
- 使用嵌套表示关系
- 使用查询参数表示过滤

### 2. 统一响应格式
```java
@Data
public class Result<T> {
    private Integer code;      // 状态码
    private String message;    // 提示信息
    private T data;           // 数据
    
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }
}
```

## 数据库规范

### 1. 命名规范
- 表名：小写下划线分隔
- 字段名：小写下划线分隔
- 主键统一使用 `id`
- 创建时间字段：`create_time`
- 更新时间字段：`update_time`
- 软删除字段：`is_deleted`

### 2. 字段规范
- 必须指定字段类型和长度
- 必须添加字段注释
- 合理使用索引
- 避免使用保留字

## 安全规范

### 1. 密码安全
- 密码加密存储
- 密码强度验证
- 密码定期更换

### 2. 数据安全
- 敏感数据加密
- XSS 防护
- CSRF 防护
- SQL 注入防护

## 性能规范

### 1. 查询优化
- 合理使用索引
- 避免全表扫描
- 使用分页查询
- 避免循环调用

### 2. 缓存使用
- 合理使用缓存
- 及时更新缓存
- 避免缓存穿透
- 避免缓存雪崩

## 测试规范

### 1. 单元测试
- 测试核心业务逻辑
- 测试边界条件
- 测试异常情况
- 保持测试独立性

### 2. 接口测试
- 测试所有接口
- 测试参数验证
- 测试权限控制
- 测试异常处理

## 错误处理规范

### 1. 全局异常处理
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error("系统异常");
    }
}
```

### 2. 自定义业务异常
```java
public class BusinessException extends RuntimeException {
    private Integer code;
    
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }
}
```

## Git 工作流规范

### 1. 分支管理
- 主分支：main
- 开发分支：dev
- 功能分支：feature/功能名称
- 修复分支：hotfix/问题描述

### 2. 提交信息规范
格式：<type>(<scope>): <subject>
type类型：
- feat: 新功能
- fix: 修复
- docs: 文档
- style: 格式
- refactor: 重构
- test: 测试
- chore: 其他

## 部署规范

### 1. 环境配置
- 开发环境
- 测试环境
- 预发布环境
- 生产环境

### 2. 部署流程
- 代码审查
- 测试验证
- 打包部署
- 监控告警

## 代码审查规范

### 1. 审查重点
- 代码质量
- 性能问题
- 安全问题
- 测试覆盖

### 2. 审查流程
- 提交代码
- 审查代码
- 修改问题
- 合并代码

## 版本发布规范

### 1. 版本号管理
- 主版本号：重大更新
- 次版本号：功能更新
- 修订号：问题修复

### 2. 发布流程
- 版本规划
- 功能开发
- 测试验证
- 发布上线 