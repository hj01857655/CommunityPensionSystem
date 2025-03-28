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
  │   │       │   └── impl/     # 服务实现
  │   │       ├── mapper/       # 数据访问层
  │   │       ├── entity/       # 实体类
  │   │       ├── dto/          # 数据传输对象
  │   │       ├── vo/           # 视图对象
  │   │       ├── config/       # 配置类
  |   |       |── exception/# 异常
  |   |       |── utils/    # 工具类
  │   │       └── CommunityPensionAdminApplication.java
  │   └── resources/
  │       ├── mapper/          # MyBatisPlus映射文件
  │       ├── static/          # 静态资源
  │       └── application.yml  # 配置文件
  └── test/                    # 测试目录
```

### 1.2 命名规范

#### 1.2.1 包命名
- 使用全小写字母，点号分隔
- 示例：`com.communitypension.communitypensionadmin`

#### 1.2.2 类命名
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

#### 1.2.3 方法命名
- 使用小驼峰命名法（camelCase）
- 方法名应该清晰表达其功能
- 示例：
  ```java
  public void saveNotice()
  public void updateNotice()
  public void deleteNotice()
  ```

#### 1.2.4 变量命名
- 使用小驼峰命名法（camelCase）
- 变量名应该清晰表达其含义
- 示例：
  ```java
  private String title;
  private String content;
  private Integer status;
  ```

#### 1.2.5 常量命名
- 使用全大写下划线分隔（UPPER_SNAKE_CASE）
- 示例：
  ```java
  public static final String STATUS_DRAFT = "0";
  public static final String STATUS_PUBLISHED = "1";
  public static final String STATUS_REVOKED = "2";
  ```

### 1.3 注释规范

#### 1.3.1 类注释
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

#### 1.3.2 方法注释
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

#### 1.3.3 字段注释
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

### 1.4 代码格式
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

### 1.5 数据库规范

#### 1.5.1 表命名
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

#### 1.5.2 字段命名
- 使用小写字母，下划线分隔
- 主键统一使用id
- 创建时间统一使用create_time
- 更新时间统一使用update_time

#### 1.5.3 索引规范
- 主键索引：id
- 普通索引：idx_字段名
- 示例：
  ```sql
  INDEX idx_status (status),
  INDEX idx_publish_time (publish_time)
  ```

### 1.6 MyBatisPlus 注解规范

#### 1.6.1 实体类注解
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

#### 1.6.2 Mapper 接口注解
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

#### 1.6.3 Mapper 注解使用规范
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

#### 1.6.4 Service 接口注解
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

#### 1.6.5 Controller 注解
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

### 1.7 Lombok 注解规范

#### 1.7.1 常用注解
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

#### 1.7.2 注解使用规范
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

#### 1.7.3 示例
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

### 1.8 数据传输对象规范

#### 1.8.1 DTO（Data Transfer Object）规范
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

#### 1.8.2 VO（View Object）规范
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

#### 1.8.3 使用规范
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

### 1.9 RESTful 接口开发规范

#### 1.9.1 Result 类使用规范
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

#### 1.9.2 Controller 层规范
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

#### 1.9.3 Service 层规范
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

#### 1.9.4 Mapper 层规范
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

#### 1.9.4 代码重复检查
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

## 2. 前端开发规范

### 2.1 项目结构
```
src/
  ├── api/                # API接口
  │   ├── back/          # 后台接口
  │   │   └── notice.js  # 通知公告接口
  │   └── fore/          # 前台接口
  ├── assets/            # 静态资源
  │   ├── images/        # 图片资源
  │   └── styles/        # 样式文件
  ├── components/        # 公共组件
  │   └── common/        # 通用组件
  ├── stores/            # 状态管理
  │   └── back/          # 后台状态
  │       └── noticeStore.js  # 通知公告状态
  ├── utils/             # 工具函数
  │   ├── axios.js     # 请求封装
  │   └── validate.js    # 验证工具
  ├── views/             # 页面组件
  │   └── notice/        # 通知公告页面
  └── router/            # 路由配置
      └── modules/       # 路由模块
          └── notice.js  # 通知公告路由
```

### 2.2 命名规范

#### 2.2.1 文件命名
- 组件文件：使用大驼峰命名法（PascalCase）
  ```
  NoticeList.vue
  NoticeForm.vue
  NoticeDetail.vue
  ```
- 工具文件：使用小驼峰命名法（camelCase）
  ```
  request.js
  validate.js
  ```
- 样式文件：使用小写中划线命名法（kebab-case）
  ```
  common.scss
  variables.scss
  ```

#### 2.2.2 组件命名
- 组件名使用大驼峰命名法
- 基础组件以 Base 开头
- 示例：
  ```
  BaseButton.vue
  BaseInput.vue
  BaseTable.vue
  ```

#### 2.2.3 变量命名
- 使用小驼峰命名法
- 布尔类型变量使用 is/has/can 等前缀
- 示例：
  ```javascript
  const userName = 'admin'
  const isVisible = true
  const hasPermission = false
  ```

### 2.3 组件规范

#### 2.3.1 组件结构
```vue
<!--
 * @description: 通知公告列表组件
 * @author: 作者名
 * @date: 2024-03-26
-->
<template>
  <div class="notice-list">
    <!-- 搜索区域 -->
    <div class="notice-list__search">
      <el-form :model="searchForm" inline>
        <!-- 表单内容 -->
      </el-form>
    </div>

    <!-- 表格区域 -->
    <div class="notice-list__table">
      <el-table :data="tableData">
        <!-- 表格内容 -->
      </el-table>
    </div>

    <!-- 分页区域 -->
    <div class="notice-list__pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
// 导入依赖
import { ref, onMounted } from 'vue'
import { useNoticeStore } from '@/stores/back/noticeStore'

// 状态定义
const noticeStore = useNoticeStore()
const searchForm = ref({})
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 方法定义
const loadData = async () => {
  try {
    const res = await noticeStore.loadNoticeList({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      ...searchForm.value
    })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取通知列表失败:', error)
  }
}

// 生命周期钩子
onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.notice-list {
  &__search {
    margin-bottom: 16px;
  }

  &__table {
    margin-bottom: 16px;
  }

  &__pagination {
    text-align: right;
  }
}
</style>
```

#### 2.3.2 组件通信
- Props 定义：
  ```javascript
  const props = defineProps({
    title: {
      type: String,
      required: true
    },
    status: {
      type: Number,
      default: 0
    }
  })
  ```

- 事件定义：
  ```javascript
  const emit = defineEmits(['update', 'delete'])
  ```

- 插槽定义：
  ```vue
  <template>
    <div class="notice-card">
      <slot name="header" />
      <slot />
      <slot name="footer" />
    </div>
  </template>
  ```

### 2.4 状态管理规范

#### 2.4.1 Store 文件命名
- 使用小驼峰命名法，以 `Store` 结尾
- 示例：
  ```
  noticeStore.js
  userStore.js
  roleStore.js
  ```

#### 2.4.2 Store 结构
```javascript
import { defineStore } from 'pinia'
import { 
  getNoticeList, 
  getNoticeDetail, 
  addNotice, 
  updateNotice, 
  deleteNotice 
} from '@/api/back/notice/notice'

export const useNoticeStore = defineStore('notice', {
  // 状态定义
  state: () => ({
    noticeList: [],
    total: 0,
    currentNotice: null
  }),

  // 计算属性
  getters: {
    getActiveNotices: (state) => {
      return state.noticeList.filter(notice => notice.status === 1)
    }
  },

  // 操作方法
  actions: {
    async loadNoticeList(params) {
      try {
        const res = await getNoticeList(params)
        this.noticeList = res.data.records
        this.total = res.data.total
        return res
      } catch (error) {
        console.error('获取通知公告列表失败:', error)
        throw error
      }
    },

    async createNotice(data) {
      try {
        const res = await addNotice(data)
        return res
      } catch (error) {
        console.error('新增通知公告失败:', error)
        throw error
      }
    }
  }
})
```

### 2.5 API 接口规范

#### 2.5.1 接口文件规范
1. 必须使用封装的 axios 实例：
   ```javascript
   // 正确示例
   import axios from '@/utils/axios'
   
   /**
    * 获取通知公告列表
    * @param {Object} params - 查询参数
    * @param {number} params.pageNum - 页码
    * @param {number} params.pageSize - 每页数量
    * @param {string} [params.title] - 标题关键字
    * @returns {Promise<{code: number, data: {records: Array, total: number}, msg: string}>}
    */
   export const getNoticeList = (params) => {
     return axios.get('/api/notices/list', { params })
   }
   
   /**
    * 新增通知公告
    * @param {Object} data - 通知公告数据
    * @returns {Promise<{code: number, msg: string}>}
    */
   export const addNotice = (data) => {
     return axios.post('/api/notices', data)
   }
   
   // 错误示例 - 禁止直接使用 axios
   import axios from 'axios'  // ❌ 错误
   ```

2. 接口文件命名规范：
   - 使用小驼峰命名法
   - 以功能模块命名
   - 示例：`notice.js`、`user.js`、`role.js`

3. 接口文件目录结构：
   ```
   src/api/
   ├── back/           # 后台接口
   │   ├── notice.js   # 通知公告接口
   │   ├── user.js     # 用户接口
   │   └── role.js     # 角色接口
   └── fore/           # 前台接口
       ├── notice.js   # 通知公告接口
       └── user.js     # 用户接口
   ```

4. 接口函数命名规范：
   - 使用动词 + 名词的形式
   - 使用小驼峰命名法
   - 示例：
     ```javascript
     // 获取数据
     getNoticeList()
     getUserInfo()
     
     // 创建数据
     createNotice()
     addUser()
     
     // 更新数据
     updateNotice()
     modifyUser()
     
     // 删除数据
     deleteNotice()
     removeUser()
     ```

5. 接口注释规范：
   ```javascript
   /**
    * 获取通知公告列表
    * @param {Object} params - 查询参数
    * @param {number} params.pageNum - 页码
    * @param {number} params.pageSize - 每页数量
    * @param {string} [params.title] - 标题关键字
    * @returns {Promise<{code: number, data: {records: Array, total: number}, msg: string}>}
    */
   export const getNoticeList = (params) => {
     return axios.get('/api/notices/list', { params })
   }
   ```

#### 2.5.2 请求方法规范
- GET：获取数据
- POST：创建数据
- PUT：更新数据
- DELETE：删除数据

#### 2.5.3 响应格式
```javascript
{
  code: 200,          // 状态码
  message: "成功",     // 提示信息
  data: {            // 响应数据
    records: [],     // 列表数据
    total: 0         // 总记录数
  },
  timestamp: 1711440000000  // 时间戳
}
```

### 2.6 样式规范

#### 2.6.1 命名规范
- 使用 BEM 命名规范
- 示例：
  ```scss
  .notice-list {
    &__search {
      margin-bottom: 16px;
    }

    &__table {
      margin-bottom: 16px;
    }

    &__pagination {
      text-align: right;
    }
  }
  ```

#### 2.6.2 样式结构
```scss
// 变量定义
$primary-color: #409EFF;
$success-color: #67C23A;
$warning-color: #E6A23C;
$danger-color: #F56C6C;
$info-color: #909399;

// 混入定义
@mixin flex-center {
  display: flex;
  align-items: center;
  justify-content: center;
}

// 样式定义
.notice-list {
  @include flex-center;
  color: $primary-color;
}
```

## 3. 通用规范

### 3.1 Git 规范

#### 3.1.1 分支管理
- 主分支：main
- 开发分支：dev
- 功能分支：feature/功能名称
- 修复分支：hotfix/问题描述
- 示例：
  ```
  feature/notification-management
  hotfix/notification-publish-bug
  ```

#### 3.1.2 提交信息规范
- 格式：<type>(<scope>): <subject>
- type类型：
  - feat: 新功能
  - fix: 修复bug
  - docs: 文档更新
  - style: 代码格式调整
  - refactor: 重构
  - test: 测试相关
  - chore: 构建过程或辅助工具的变动
- 示例：
  ```
  feat(notice): 添加通知公告管理功能
  fix(notice): 修复通知发布状态更新问题
  ```

### 3.2 安全规范

#### 3.2.1 密码安全
1. 密码存储：
   - 使用BCrypt加密
   - 禁止明文存储
   - 禁止使用MD5

2. 密码策略：
   - 最小长度8位
   - 必须包含大小写字母和数字
   - 定期更换密码

#### 3.2.2 接口安全
1. 认证：
   - 使用JWT token
   - token有效期控制
   - 支持token刷新

2. 授权：
   - 基于RBAC模型
   - 细粒度的权限控制
   - 角色继承关系

3. 数据安全：
   - 敏感数据加密
   - 防止SQL注入
   - 防止XSS攻击
   - 防止CSRF攻击

### 3.3 测试规范

#### 3.3.1 单元测试
1. 测试覆盖率要求：
   - 核心业务代码：>80%
   - 工具类：>90%
   - 整体覆盖率：>70%

2. 测试命名：
   ```java
   @Test
   public void shouldReturnUserInfoWhenUserIdIsValid() {}
   
   @Test
   public void shouldThrowExceptionWhenUserIdIsInvalid() {}
   ```

#### 3.3.2 接口测试
1. 测试用例：
   - 正常场景
   - 异常场景
   - 边界条件
   - 性能测试

2. 测试工具：
   - Postman
   - JMeter
   - Swagger

### 3.4 文档规范

#### 3.4.1 必要文档
1. 项目文档：
   - README.md
   - 部署文档
   - API文档
   - 数据库设计文档

2. 代码文档：
   - 类注释
   - 方法注释
   - 关键代码注释

#### 3.4.2 文档更新
1. 及时性：
   - 代码变更同步更新文档
   - 定期审查文档准确性
   - 版本发布更新文档

2. 完整性：
   - 包含所有必要信息
   - 示例完整
   - 说明清晰 