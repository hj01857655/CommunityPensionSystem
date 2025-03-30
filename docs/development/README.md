# 开发规范文档

## 1. 后端开发规范

### 1.1 项目结构
```
src/
  ├── main/
  │   ├── java/
  │   │   └── com/communitypension/communitypensionadmin/
  │   │       ├── controller/    # 控制器层
  │   │       ├── service/       # 服务层
  │   │       ├── mapper/        # 数据访问层
  │   │       ├── entity/        # 实体类
  │   │       ├── dto/           # 数据传输对象
  │   │       ├── vo/            # 视图对象
  │   │       ├── config/        # 配置类
  │   │       ├── common/        # 公共类
  │   │       └── utils/         # 工具类
  │   └── resources/
  │       ├── mapper/           # MyBatis映射文件
  │       ├── application.yml   # 应用配置
  │       └── application-dev.yml # 开发环境配置
  └── test/                    # 测试目录
```

### 1.2 开发环境规范

#### 1.2.1 JDK 版本规范
- 项目使用 JDK 8 及以上版本
- 使用 `jakarta` 包名替代 `javax`
- 示例：
  ```java
  // 使用 jakarta 包
  import jakarta.servlet.http.HttpServletResponse;
  import jakarta.validation.constraints.NotBlank;
  import jakarta.validation.constraints.NotNull;
  ```

#### 1.2.2 包命名规范
- 基础包名：`com.communitypension.communitypensionadmin`
- 模块包名：按业务模块划分
- 示例：
  ```java
  // 活动模块
  com.communitypension.communitypensionadmin.controller.activity
  com.communitypension.communitypensionadmin.service.activity
  com.communitypension.communitypensionadmin.mapper.activity
  com.communitypension.communitypensionadmin.entity.activity
  com.communitypension.communitypensionadmin.dto.activity
  com.communitypension.communitypensionadmin.vo.activity
  ```

### 1.3 命名规范

#### 1.3.1 包命名
- 使用全小写字母，点号分隔
- 示例：`com.communitypension.communitypensionadmin`

#### 1.3.2 类命名
- 使用大驼峰命名法（PascalCase）
- 类名应该清晰表达其功能
- 示例：
  ```java
  public class UserController{

  }
  
  public interface UserService extends IService<User>{

  }

  @Service
  public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

  }

  @Mapper
  public interface UserMapper extends BaseMapper<User>{

  }

  ```

#### 1.3.3 方法命名
- 使用小驼峰命名法（camelCase）
- 方法名应该清晰表达其功能
- 示例：
  ```java
  public void saveNotice()
  public void updateNotice()
  public void deleteNotice()
  ```

#### 1.3.4 变量命名
- 使用小驼峰命名法（camelCase）
- 变量名应该清晰表达其含义
- 示例：
  ```java
  private String title;
  private String content;
  private Integer status;
  ```

#### 1.3.5 常量命名
- 使用全大写下划线分隔（UPPER_SNAKE_CASE）
- 示例：
  ```java
  public static final String STATUS_DRAFT = "0";
  public static final String STATUS_PUBLISHED = "1";
  public static final String STATUS_REVOKED = "2";
  ```

### 1.4 注释规范

#### 1.4.1 类注释
```java
/**
 * 通知公告控制器
 * 处理通知公告的增删改查、发布、撤回等操作
 *
 * @date 2024-03-26
 */
@RestController
@RequestMapping("/api/notices")
public class NotificationController {
  
}
```

#### 1.4.2 方法注释
```java
/**
 * 获取通知公告列表
 *
 * @param query 查询条件
 * @return 通知公告列表
 */
@GetMapping("/list")
public Result<IPage<Notification>> list(NotificationQuery query) {
}
```

#### 1.4.3 字段注释
```java
/**
 * 通知公告标题
 */
private String title;

/**
 * 通知公告内容
 */
private String content;

/**
 * 状态：0-草稿 1-已发布 2-已撤回
 */
private Integer status;
```

### 1.5 代码格式
- 使用4个空格缩进
- 大括号使用K&R风格（左括号不换行，右括号独占一行）
- 每行代码不超过120个字符
- 示例：
  ```java
  if (condition) {
      // 代码块
  } else {
      // 代码块
  }
  ```

### 1.6 数据库规范

#### 1.6.1 表命名
- 使用小写字母，下划线分隔
- 示例：
  ```sql
  CREATE TABLE notice (
      id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
      title VARCHAR(100) NOT NULL COMMENT '通知标题',
      content TEXT NOT NULL COMMENT '通知内容',
      status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-草稿 1-已发布 2-已撤回',
      publish_time DATETIME COMMENT '发布时间',
      create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
      update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
  )
  ```

#### 1.6.2 字段命名
- 使用小写字母，下划线分隔
- 主键统一使用id
- 创建时间统一使用create_time
- 更新时间统一使用update_time

#### 1.6.3 索引规范
- 主键索引：id
- 普通索引：idx_字段名
- 示例：
  ```sql
  INDEX idx_status (status),
  INDEX idx_publish_time (publish_time)
  ```

### 1.7 MyBatisPlus 注解规范

#### 1.7.1 实体类注解
```java
/**
 * 用户实体类
 *
 * @date 2024-03-26
 */
@Data                   // 生成 getter、setter、toString 等方法
@TableName("sys_user")  // 指定表名
@TableId(type = IdType.AUTO)  // 指定主键生成策略
public class User {
    
    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户名
     */
    @TableField("user_name")  // 指定字段名
    private String userName;
    
    /**
     * 密码
     */
    @TableField(select = false)  // 查询时不返回该字段
    private String password;
    
    /**
     * 状态：0-禁用 1-启用
     */
    @TableField(fill = FieldFill.INSERT)  // 插入时自动填充
    private Integer status;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 角色ID列表（非数据库字段）
     */
    @TableField(exist = false)  // 标记为非数据库字段
    private List<Long> roleIds;

    /**
     * 角色名称列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<String> roleNames;

    /**
     * 部门名称（非数据库字段）
     */
    @TableField(exist = false)
    private String deptName;

    /**
     * 状态名称（非数据库字段）
     */
    @TableField(exist = false)
    private String statusName;

    /**
     * 是否选中（非数据库字段）
     */
    @TableField(exist = false)
    private Boolean selected;
}
```

#### 1.7.2 Mapper 接口注解
```java
/**
 * 用户Mapper接口
 *
 * @date 2024-03-26
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据ID查询用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @Select("SELECT * FROM sys_user WHERE id = #{userId}")
    User selectUserById(@Param("userId") Long userId);

    /**
     * 分页查询用户列表
     *
     * @param page 分页参数
     * @param wrapper 查询条件
     * @return 用户列表
     */
    @Select("SELECT * FROM sys_user ${ew.customSqlSegment}")
    IPage<User> selectUserPage(Page<User> page, @Param("ew") LambdaQueryWrapper<User> wrapper);

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 状态
     * @return 影响行数
     */
    @Update("UPDATE sys_user SET status = #{status} WHERE id = #{userId}")
    int updateStatus(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 批量插入用户
     *
     * @param users 用户列表
     * @return 影响行数
     */
    @Insert("<script>" +
            "INSERT INTO sys_user(user_name, password, status) VALUES " +
            "<foreach collection='users' item='user' separator=','>" +
            "(#{user.userName}, #{user.password}, #{user.status})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("users") List<User> users);
}
```

#### 1.7.3 Mapper 注解使用规范
1. 优先使用 MyBatisPlus 注解和条件构造器：
   - 简单的 CRUD 操作：使用 BaseMapper 提供的方法
   - 单表查询：使用 `@Select` 注解
   - 分页查询：使用 `@Select` 注解 + `LambdaQueryWrapper`
   - 简单更新：使用 `@Update` 注解
   - 示例：
     ```java
     // 使用条件构造器
     LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
         .like(StringUtils.isNotBlank(userName), User::getUserName, userName)
         .eq(status != null, User::getStatus, status)
         .orderByDesc(User::getCreateTime);
     
     // 分页查询
     @Select("SELECT * FROM sys_user ${ew.customSqlSegment}")
     IPage<User> selectUserPage(Page<User> page, @Param("ew") LambdaQueryWrapper<User> wrapper);
     ```

2. XML 配置使用场景：
   - 复杂动态 SQL：使用 `<script>` 标签
   - 特别复杂的查询：多表关联、子查询等
   - 复杂的条件判断：使用 `<if>`、`<choose>` 标签
   - 示例：
     ```xml
     <!-- UserMapper.xml -->
     <?xml version="1.0" encoding="UTF-8"?>
     <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
     <mapper namespace="com.communitypension.communitypensionadmin.mapper.UserMapper">
         
         <!-- 复杂动态SQL -->
         <select id="selectUserByCondition" resultType="com.communitypension.communitypensionadmin.entity.User">
             <script>
                 SELECT * FROM sys_user
                 <where>
                     <if test="userName != null and userName != ''">
                         AND user_name LIKE CONCAT('%', #{userName}, '%')
                     </if>
                     <if test="status != null">
                         AND status = #{status}
                     </if>
                     <if test="startTime != null">
                         AND create_time >= #{startTime}
                     </if>
                     <if test="endTime != null">
                         AND create_time <= #{endTime}
                     </if>
                     <choose>
                         <when test="type == 1">
                             AND type = 1
                         </when>
                         <when test="type == 2">
                             AND type = 2
                         </when>
                         <otherwise>
                             AND type = 0
                         </otherwise>
                     </choose>
                 </where>
                 ORDER BY create_time DESC
             </script>
         </select>
         
         <!-- 复杂多表关联查询 -->
         <select id="selectUserWithRoles" resultType="com.communitypension.communitypensionadmin.vo.UserVO">
             SELECT 
                 u.id,
                 u.user_name,
                 u.status,
                 GROUP_CONCAT(r.role_name) as role_names,
                 d.dept_name
             FROM sys_user u
             LEFT JOIN sys_user_role ur ON u.id = ur.user_id
             LEFT JOIN sys_role r ON ur.role_id = r.id
             LEFT JOIN sys_dept d ON u.dept_id = d.id
             WHERE u.id = #{userId}
             GROUP BY u.id
         </select>
     </mapper>
     ```

#### 1.7.4 Service 接口注解
```java
/**
 * 用户服务接口
 *
 * @date 2024-03-26
 */
public interface UserService extends IService<User> {
    
    /**
     * 根据用户名查询用户
     *
     * @param userName 用户名
     * @return 用户信息
     */
    User getUserByUserName(String userName);
}
```

#### 1.7.5 Controller 注解
```java
/**
 * 用户控制器
 *
 * @date 2024-03-26
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * 获取用户列表
     *
     * @param query 查询条件
     * @return 用户列表
     */
    @GetMapping("/list")
    public Result<IPage<User>> list(UserQuery query) {
        return Result.success(userService.page(
            new Page<>(query.getPageNum(), query.getPageSize()),
            new LambdaQueryWrapper<User>()
                .like(StringUtils.isNotBlank(query.getUserName()), 
                    User::getUserName, query.getUserName())
                .eq(query.getStatus() != null, 
                    User::getStatus, query.getStatus())
        ));
    }
}
```

### 1.8 Lombok 注解规范

#### 1.8.1 常用注解
```java
/**
 * 用户实体类
 *
 * @date 2024-03-26
 */
@Data                   // 生成 getter、setter、toString 等方法
@Builder                // 生成建造者模式
@NoArgsConstructor      // 生成无参构造器
@AllArgsConstructor     // 生成全参构造器
@RequiredArgsConstructor // 生成包含 final 字段的构造器
@EqualsAndHashCode      // 生成 equals 和 hashCode 方法
@ToString              // 生成 toString 方法
@Slf4j                 // 生成日志对象
public class User {
    // 字段定义
}
```

#### 1.8.2 注解使用规范
1. `@Data` 注解：
   - 用于实体类，自动生成 getter、setter、toString 等方法
   - 不建议在 DTO 和 VO 类上使用，应该明确指定需要的方法

2. `@Builder` 注解：
   - 用于需要构建者模式的类
   - 通常与 `@NoArgsConstructor` 和 `@AllArgsConstructor` 一起使用

3. `@RequiredArgsConstructor` 注解：
   - 用于依赖注入的类
   - 只生成包含 final 字段的构造器

4. `@Slf4j` 注解：
   - 用于需要日志记录的类
   - 自动生成 `private static final Logger log = LoggerFactory.getLogger(XXX.class)`

5. `@EqualsAndHashCode` 注解：
   - 用于需要比较对象的类
   - 建议指定 `callSuper = true`，以包含父类字段

#### 1.8.3 示例
```java
/**
 * 用户DTO
 *
 * @date 2024-03-26
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO extends BaseDTO {
    
    /**
     * 用户名
     */
    private String userName;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 状态
     */
    private Integer status;
}

/**
 * 用户服务实现类
 *
 * @date 2024-03-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public User getUserByUserName(String userName) {
        log.info("根据用户名查询用户: {}", userName);
        return userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getUserName, userName)
        );
    }
}
```

### 1.9 数据传输对象规范

#### 1.9.1 DTO（Data Transfer Object）规范
1. 使用场景：
   - 接收前端请求参数
   - 服务层之间的数据传输
   - 复杂的数据转换场景

2. 命名规范：
   - 类名以 `DTO` 结尾
   - 示例：`UserDTO`、`NoticeDTO`、`LoginDTO`

3. 示例：
```java
/**
 * 用户登录DTO
 *
 * @date 2024-03-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String userName;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
    
    /**
     * 验证码
     */
    private String captcha;
    
    /**
     * 验证码key
     */
    private String captchaKey;
}

/**
 * 用户查询DTO
 *
 * @date 2024-03-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserQueryDTO {
    
    /**
     * 用户名
     */
    private String userName;
    
    /**
     * 状态
     */
    private Integer status;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页数量
     */
    private Integer pageSize = 10;
}
```

#### 1.9.2 VO（View Object）规范
1. 使用场景：
   - 返回给前端的数据对象
   - 展示层的数据转换
   - 复杂的数据聚合场景

2. 命名规范：
   - 类名以 `VO` 结尾
   - 示例：`UserVO`、`NoticeVO`、`MenuVO`

3. 示例：
```java
/**
 * 用户信息VO
 *
 * @date 2024-03-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String userName;
    
    /**
     * 昵称
     */
    private String nickName;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
    
    /**
     * 状态名称
     */
    private String statusName;
    
    /**
     * 角色列表
     */
    private List<String> roles;
    
    /**
     * 权限列表
     */
    private List<String> permissions;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

/**
 * 通知公告VO
 *
 * @date 2024-03-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeVO {
    
    /**
     * 通知ID
     */
    private Long id;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 状态：0-草稿 1-已发布 2-已撤回
     */
    private Integer status;
    
    /**
     * 状态名称
     */
    private String statusName;
    
    /**
     * 发布人
     */
    private String publisher;
    
    /**
     * 发布时间
     */
    private LocalDateTime publishTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
```

#### 1.9.3 使用规范
1. 禁止直接使用实体类与前端交互：
   - 实体类可能包含敏感信息
   - 实体类可能包含不必要的字段
   - 实体类结构可能与前端需求不匹配

2. 数据转换规范：
   - 使用 MapStruct 进行对象转换
   - 在 Service 层进行数据转换
   - 保持转换逻辑的一致性

3. 示例：
```java
/**
 * 用户服务实现类
 *
 * @date 2024-03-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    private final UserMapper userMapper;
    private final UserConverter userConverter;
    
    @Override
    public IPage<UserVO> getUserPage(UserQueryDTO query) {
        // 构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
            .like(StringUtils.isNotBlank(query.getUserName()), 
                User::getUserName, query.getUserName())
            .eq(query.getStatus() != null, 
                User::getStatus, query.getStatus())
            .ge(query.getStartTime() != null, 
                User::getCreateTime, query.getStartTime())
            .le(query.getEndTime() != null, 
                User::getCreateTime, query.getEndTime())
            .orderByDesc(User::getCreateTime);
        
        // 分页查询
        IPage<User> userPage = userMapper.selectPage(
            new Page<>(query.getPageNum(), query.getPageSize()),
            wrapper
        );
        
        // 转换为VO
        return userPage.convert(userConverter::toVO);
    }
    
    @Override
    public UserVO getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        return userConverter.toVO(user);
    }
}
```

### 1.10 RESTful 接口开发规范

#### 1.10.1 Result 类使用规范
1. 基本使用规范：
   - 所有接口返回值必须使用 Result 包装类
   - 根据操作类型选择合适的结果方法
   - 示例：
     ```java
     // 查询成功
     return Result.success(data);
     return Result.success("查询成功", data);
     
     // 创建成功
     return Result.created(data);
     
     // 删除成功
     return Result.deleted();
     
     // 操作失败
     return Result.error("操作失败");
     return Result.error(500, "系统异常");
     ```

2. 状态码使用规范：
   - 200：操作成功
   - 201：创建成功
   - 204：删除成功
   - 400：请求参数错误
   - 401：未授权
   - 403：禁止访问
   - 404：资源不存在
   - 500：系统异常

3. 分页查询结果：
   ```java
   @GetMapping("/list")
   public Result<IPage<NoticeVO>> list(NoticeQueryDTO query) {
       IPage<NoticeVO> page = noticeService.getNoticePage(query);
       return Result.success("查询成功", page);
   }
   ```

4. 单个对象查询结果：
   ```java
   @GetMapping("/{id}")
   public Result<NoticeVO> getById(@PathVariable Long id) {
       NoticeVO notice = noticeService.getNoticeById(id);
       return Result.success(notice);
   }
   ```

5. 创建操作结果：
   ```java
   @PostMapping
   public Result<Void> create(@RequestBody @Validated NoticeDTO notice) {
       noticeService.createNotice(notice);
       return Result.created(null);
   }
   ```

6. 更新操作结果：
   ```java
   @PutMapping("/{id}")
   public Result<Void> update(@PathVariable Long id, @RequestBody @Validated NoticeDTO notice) {
       noticeService.updateNotice(id, notice);
       return Result.success("更新成功");
   }
   ```

7. 删除操作结果：
   ```java
   @DeleteMapping("/{id}")
   public Result<Void> delete(@PathVariable Long id) {
       noticeService.deleteNotice(id);
       return Result.deleted();
   }
   ```

8. 批量操作结果：
   ```java
   @DeleteMapping("/batch")
   public Result<Void> batchDelete(@RequestBody List<Long> ids) {
       noticeService.batchDeleteNotices(ids);
       return Result.success("批量删除成功");
   }
   ```

9. 异常处理结果：
   ```java
   @ExceptionHandler(Exception.class)
   public Result<Void> handleException(Exception e) {
       log.error("系统异常", e);
       return Result.error("系统异常，请联系管理员");
   }
   ```

#### 1.10.2 Controller 层规范
1. 接口命名规范：
   - 使用复数名词表示资源
   - 使用 HTTP 方法表示操作
   - 示例：
     ```java
     @RestController
     @RequestMapping("/api/notices")
     public class NoticeController {
         // 获取列表
         @GetMapping
         public Result<IPage<NoticeVO>> list(NoticeQueryDTO query) {}
         
         // 获取详情
         @GetMapping("/{id}")
         public Result<NoticeVO> getById(@PathVariable Long id) {}
         
         // 创建
         @PostMapping
         public Result<Void> create(@RequestBody NoticeDTO notice) {}
         
         // 更新
         @PutMapping("/{id}")
         public Result<Void> update(@PathVariable Long id, @RequestBody NoticeDTO notice) {}
         
         // 删除
         @DeleteMapping("/{id}")
         public Result<Void> delete(@PathVariable Long id) {}
         
         // 批量删除
         @DeleteMapping("/batch")
         public Result<Void> batchDelete(@RequestBody List<Long> ids) {}
     }
     ```

2. 接口参数规范：
   - 查询参数：使用 DTO 对象
   - 创建/更新：使用 DTO 对象
   - 路径参数：使用 @PathVariable
   - 示例：
     ```java
     /**
      * 获取通知公告列表
      *
      * @param query 查询条件
      * @return 通知公告列表
      */
     @GetMapping
     public Result<IPage<NoticeVO>> list(NoticeQueryDTO query) {
         return Result.success(noticeService.getNoticePage(query));
     }
     ```

3. 接口返回值规范：
   - 统一使用 Result 包装类
   - 分页数据使用 IPage
   - 示例：
     ```java
     /**
      * 创建通知公告
      *
      * @param notice 通知公告数据
      * @return 操作结果
      */
     @PostMapping
     public Result<Void> create(@RequestBody @Validated NoticeDTO notice) {
         noticeService.createNotice(notice);
         return Result.success();
     }
     ```

#### 1.10.3 Service 层规范
1. 接口定义规范：
   - 继承 IService 接口
   - 方法命名语义化
   - 示例：
     ```java
     public interface NoticeService extends IService<Notice> {
  /**
   * 获取通知公告列表
          *
          * @param query 查询条件
          * @return 通知公告列表
          */
         IPage<NoticeVO> getNoticePage(NoticeQueryDTO query);
         
         /**
          * 创建通知公告
          *
          * @param notice 通知公告数据
          */
         void createNotice(NoticeDTO notice);
     }
     ```

2. 实现类规范：
   - 继承 ServiceImpl
   - 使用构造器注入依赖
   - 示例：
     ```java
     @Slf4j
     @Service
     @RequiredArgsConstructor
     public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {
         
         private final NoticeMapper noticeMapper;
         private final NoticeConverter noticeConverter;
         
         @Override
         public IPage<NoticeVO> getNoticePage(NoticeQueryDTO query) {
             // 构建查询条件
             LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<Notice>()
                 .like(StringUtils.isNotBlank(query.getTitle()), 
                     Notice::getTitle, query.getTitle())
                 .eq(query.getStatus() != null, 
                     Notice::getStatus, query.getStatus())
                 .orderByDesc(Notice::getCreateTime);
             
             // 分页查询
             IPage<Notice> noticePage = noticeMapper.selectPage(
                 new Page<>(query.getPageNum(), query.getPageSize()),
                 wrapper
             );
             
             // 转换为VO
             return noticePage.convert(noticeConverter::toVO);
         }
         
         @Override
         public void createNotice(NoticeDTO notice) {
             // 转换为实体
             Notice noticeEntity = noticeConverter.toEntity(notice);
             // 保存数据
             noticeMapper.insert(noticeEntity);
         }
     }
     ```

#### 1.10.4 Mapper 层规范
1. 接口定义规范：
   - 继承 BaseMapper
   - 自定义方法命名语义化
   - 示例：
     ```java
     @Mapper
     public interface NoticeMapper extends BaseMapper<Notice> {
         /**
          * 根据条件查询通知公告列表
          *
          * @param wrapper 查询条件
          * @return 通知公告列表
          */
         List<Notice> selectNoticeList(@Param("ew") LambdaQueryWrapper<Notice> wrapper);
     }
     ```

2. XML 配置规范：
   - 复杂查询使用 XML 配置
   - 使用动态 SQL
   - 示例：
     ```xml
     <!-- NoticeMapper.xml -->
     <?xml version="1.0" encoding="UTF-8"?>
     <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
     <mapper namespace="com.communitypension.communitypensionadmin.mapper.NoticeMapper">
         
         <!-- 根据条件查询通知公告列表 -->
         <select id="selectNoticeList" resultType="com.communitypension.communitypensionadmin.entity.Notice">
             SELECT * FROM notice
             ${ew.customSqlSegment}
         </select>
         
         <!-- 复杂查询示例 -->
         <select id="selectNoticeWithPublisher" resultType="com.communitypension.communitypensionadmin.vo.NoticeVO">
             SELECT 
                 n.id,
                 n.title,
                 n.content,
                 n.status,
                 n.publish_time,
                 u.user_name as publisher_name
             FROM notice n
             LEFT JOIN sys_user u ON n.publisher_id = u.id
             <where>
                 <if test="title != null and title != ''">
                     AND n.title LIKE CONCAT('%', #{title}, '%')
                 </if>
                 <if test="status != null">
                     AND n.status = #{status}
                 </if>
                 <if test="startTime != null">
                     AND n.publish_time >= #{startTime}
                 </if>
                 <if test="endTime != null">
                     AND n.publish_time <= #{endTime}
                 </if>
             </where>
             ORDER BY n.publish_time DESC
         </select>
     </mapper>
     ```

#### 1.10.4 代码重复检查
1. 检查点：
   - 重复的查询条件构建
   - 重复的数据转换逻辑
   - 重复的异常处理
   - 重复的权限验证

2. 优化方案：
   - 抽取公共查询条件构建方法
   - 使用 MapStruct 统一处理对象转换
   - 统一异常处理
   - 统一权限验证注解
   - 示例：
     ```java
     // 公共查询条件构建
     public class QueryWrapperBuilder {
         public static <T> LambdaQueryWrapper<T> buildBaseWrapper(String keyword, Integer status) {
             return new LambdaQueryWrapper<T>()
                 .like(StringUtils.isNotBlank(keyword), 
                     T::getTitle, keyword)
                 .eq(status != null, 
                     T::getStatus, status)
                 .orderByDesc(T::getCreateTime);
         }
     }
     
     // 统一异常处理
     @RestControllerAdvice
     public class GlobalExceptionHandler {
         @ExceptionHandler(Exception.class)
         public Result<Void> handleException(Exception e) {
             log.error("系统异常", e);
             return Result.error("系统异常，请联系管理员");
         }
     }
     
     // 统一权限验证
     @Target(ElementType.METHOD)
     @Retention(RetentionPolicy.RUNTIME)
     public @interface RequirePermission {
         String value();
     }
     ```

### 1.11 前后端接口规范

#### 1.11.1 接口命名规范
1. 后端接口：
   - 使用 RESTful 风格
   - 使用复数名词表示资源
   - 示例：
     ```java
     @RestController
     @RequestMapping("/api/activity")
     public class ActivityController {
         @GetMapping("/list")           // 获取列表
         @GetMapping("/{id}")           // 获取详情
         @PostMapping                   // 创建
         @PutMapping("/{id}")           // 更新
         @DeleteMapping("/{id}")        // 删除
         @PutMapping("/{id}/status")    // 更新状态
         @GetMapping("/{id}/stats")     // 获取统计
     }
     ```

2. 前端 API：
   - 方法名语义化
   - 与后端接口一一对应
- 示例：
```javascript
     // 活动管理接口
     export const getList = (params) => axios.get('/api/activity/list', { params })
     export const getDetail = (id) => axios.get(`/api/activity/${id}`)
     export const create = (data) => axios.post('/api/activity', data)
     export const update = (id, data) => axios.put(`/api/activity/${id}`, data)
     export const deleteActivity = (id) => axios.delete(`/api/activity/${id}`)
     export const updateStatus = (id, status) => axios.put(`/api/activity/${id}/status`, { status })
     export const getStats = (id) => axios.get(`/api/activity/${id}/stats`)
     ```

#### 1.11.2 接口参数规范
1. 查询参数：
   - 使用 `@RequestParam` 注解
   - 设置默认值和是否必填
   - 示例：
     ```java
     @GetMapping("/list")
     public Result<Page<ActivityVO>> list(
         @RequestParam(defaultValue = "1") Integer pageNum,
         @RequestParam(defaultValue = "10") Integer pageSize,
         @RequestParam(required = false) String title
     )
     ```

2. 路径参数：
   - 使用 `@PathVariable` 注解
   - 示例：
     ```java
     @GetMapping("/{id}")
     public Result<ActivityVO> getDetail(@PathVariable Long id)
     ```

3. 请求体参数：
   - 使用 `@RequestBody` 注解
   - 使用 DTO 对象接收
   - 示例：
     ```java
     @PostMapping
     public Result<Void> create(@Validated @RequestBody ActivityDTO activityDTO)
     ```

#### 1.11.3 接口返回值规范
1. 统一返回格式：
   ```java
   public class Result<T> {
       private int code;          // 状态码
       private String message;    // 消息
       private T data;           // 数据
       private long timestamp;    // 时间戳
   }
   ```

2. 状态码使用：
   - 200：操作成功
   - 201：创建成功
   - 204：删除成功
   - 400：请求参数错误
   - 401：未授权
   - 403：禁止访问
   - 404：资源不存在
   - 500：系统异常

3. 返回值方法使用：
   ```java
   // 查询成功
   return Result.success(data);
   return Result.success("查询成功", data);
   
   // 创建成功
   return Result.created(data);
   
   // 删除成功
   return Result.deleted();
   
   // 操作失败
   return Result.error("操作失败");
   return Result.error(500, "系统异常");
   ```

4. 分页数据格式：
   ```java
   public class Page<T> {
       private List<T> records;  // 数据列表
       private Long total;       // 总记录数
       private Long size;        // 每页大小
       private Long current;     // 当前页码
   }
   ```

## 2. 前端开发规范

### 1. 项目结构
```
src/
  ├── api/                # API接口目录
  │   ├── back/          # 后台接口
  │   │   ├── system/    # 系统管理
  │   │   │   ├── user.js        # 用户管理
  │   │   │   ├── role.js        # 角色管理
  │   │   │   ├── menu.js        # 菜单管理
  │   │   │   └── dept.js        # 部门管理
  │   │   ├── notice/    # 通知管理
  │   │   │   ├── index.js       # 通知基础接口
  │   │   │   ├── category.js    # 通知分类
  │   │   │   └── comment.js     # 通知评论
  │   │   ├── activity/  # 活动管理
  │   │   │   ├── index.js       # 活动基础接口
  │   │   │   ├── type.js        # 活动类型
  │   │   │   ├── participate.js # 活动参与
  │   │   │   └── checkin.js     # 活动签到
  │   │   └── elder/     # 老人管理
  │   │       ├── index.js       # 老人基础接口
  │   │       ├── health.js      # 健康档案
  │   │       └── visit.js       # 探访记录
  │   └── fore/          # 前台接口
  │       ├── user.js    # 用户相关
  │       ├── notice.js  # 通知相关
  │       ├── activity.js # 活动相关
  │       └── elder.js   # 老人相关
  ├── assets/            # 静态资源
  │   ├── images/        # 图片资源
  │   │   ├── logo/      # Logo图片
  │   │   ├── icons/     # 图标图片
  │   │   └── common/    # 通用图片
  │   └── styles/        # 样式文件
  │       ├── variables.scss  # 变量定义
  │       ├── mixins.scss     # 混入定义
  │       ├── common.scss     # 通用样式
  │       └── themes/         # 主题样式
  ├── components/        # 公共组件
  │   ├── common/        # 通用组件
  │   │   ├── BaseButton.vue    # 基础按钮
  │   │   ├── BaseInput.vue     # 基础输入框
  │   │   ├── BaseTable.vue     # 基础表格
  │   │   └── BaseForm.vue      # 基础表单
  │   └── business/      # 业务组件
  │       ├── notice/    # 通知相关组件
  │       ├── activity/  # 活动相关组件
  │       └── elder/     # 老人相关组件
  ├── stores/            # 状态管理
  │   ├── back/          # 后台状态
  │   │   ├── system/    # 系统管理
  │   │   │   ├── user.js       # 用户状态
  │   │   │   ├── role.js       # 角色状态
  │   │   │   └── menu.js       # 菜单状态
  │   │   ├── notice/    # 通知管理
  │   │   │   ├── index.js      # 通知状态
  │   │   │   └── category.js   # 分类状态
  │   │   ├── activity/  # 活动管理
  │   │   │   ├── index.js      # 活动状态
  │   │   │   └── type.js       # 类型状态
  │   │   └── elder/     # 老人管理
  │   │       ├── index.js      # 老人状态
  │   │       └── health.js     # 健康状态
  │   └── fore/          # 前台状态
  │       ├── user.js    # 用户状态
  │       ├── notice.js  # 通知状态
  │       ├── activity.js # 活动状态
  │       └── elder.js   # 老人状态
  ├── utils/             # 工具函数
  │   ├── axios.js       # 请求封装
  │   ├── validate.js    # 验证工具
  │   ├── storage.js     # 存储工具
  │   ├── date.js        # 日期工具
  │   └── permission.js  # 权限工具
  ├── views/             # 页面组件
  │   ├── back/          # 后台页面
  │   │   ├── system/    # 系统管理
  │   │   │   ├── user/          # 用户管理
  │   │   │   ├── role/          # 角色管理
  │   │   │   └── menu/          # 菜单管理
  │   │   ├── notice/    # 通知管理
  │   │   │   ├── list/          # 通知列表
  │   │   │   └── publish/       # 通知发布
  │   │   ├── activity/  # 活动管理
  │   │   │   ├── list/          # 活动列表
  │   │   │   └── type/          # 活动类型
  │   │   └── elder/     # 老人管理
  │   │       ├── list/          # 老人列表
  │   │       └── health/        # 健康档案
  │   └── fore/          # 前台页面
  │       ├── Home.vue           # 首页
  │       ├── Login.vue          # 登录页
  │       ├── Notice.vue         # 通知页
  │       ├── Activity.vue       # 活动页
  │       └── Profile.vue        # 个人中心
  ├── router/            # 路由配置
  │   └── index.js       # 路由入口(统一管理前后台路由)
  ├── constants/         # 常量定义
  │   ├── status.js      # 状态常量
  │   ├── types.js       # 类型常量
  │   └── config.js      # 配置常量
  ├── hooks/             # 组合式函数
  │   ├── useTable.js    # 表格相关
  │   ├── useForm.js     # 表单相关
  │   └── usePermission.js # 权限相关
  ├── locales/           # 国际化
  │   ├── zh-CN/         # 中文
  │   └── en-US/         # 英文
  ├── App.vue            # 根组件
  ├── main.js            # 入口文件
  └── env.js             # 环境配置
```

### 2.2 目录说明

#### 2.2.1 API 目录
- `api/back/`: 后台接口
  - 按业务模块划分目录
  - 每个模块下按功能划分文件
  - 基础接口使用 index.js
  - 相关功能使用独立文件

- `api/fore/`: 前台接口
  - 直接按功能模块划分文件
  - 不创建子目录
  - 文件命名清晰表达功能

#### 2.2.2 组件目录
- `components/common/`: 通用组件
  - 基础UI组件
  - 业务无关的通用组件

- `components/business/`: 业务组件
  - 按业务模块划分目录
  - 包含特定业务逻辑的组件

#### 2.2.3 状态管理
- `stores/back/`: 后台状态
  - 按业务模块划分目录
  - 每个模块下按功能划分文件
  - 基础状态使用 index.js
  - 相关状态使用独立文件

- `stores/fore/`: 前台状态
  - 直接按功能模块划分文件
  - 不创建子目录
  - 文件命名清晰表达功能

#### 2.2.4 工具函数
- `utils/`: 工具函数目录
  - 按功能类型划分文件
  - 保持单一职责
  - 避免重复代码

#### 2.2.5 页面组件
- `views/back/`: 后台页面
  - 按业务模块划分目录
  - 每个模块下按功能划分子目录
  - 保持目录结构清晰

- `views/fore/`: 前台页面
  - 直接按功能模块划分文件
  - 不创建子目录
  - 文件命名清晰表达功能

#### 2.2.6 路由配置
- `router/`: 路由配置目录
  - 统一使用 index.js 管理所有路由
  - 包含前后台路由配置

// ... 其他规范内容保持不变 ...