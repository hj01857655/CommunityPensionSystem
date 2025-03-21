package com.communitypension.communitypensionadmin.controller;

import com.communitypension.communitypensionadmin.entity.Elder;
import com.communitypension.communitypensionadmin.entity.Kin;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.service.ElderService;
import com.communitypension.communitypensionadmin.service.KinService;
import com.communitypension.communitypensionadmin.service.UserService;
import com.communitypension.communitypensionadmin.utils.JwtTokenUtil;
import com.communitypension.communitypensionadmin.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/users")

@Tag(name = "用户管理")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    // 用户服务
    private final UserService userService;
    //家属服务
    private final KinService kinService;
    //老人服务
    private final ElderService elderService;

    // JWT工具
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public UserController(UserService userService,KinService kinService,ElderService elderService, JwtTokenUtil jwtTokenUtil) {
        this.userService = userService;
        this.elderService = elderService;
        this.kinService = kinService;
        this.jwtTokenUtil = jwtTokenUtil;
    }
    
    @Operation(summary = "重置密码为123456")
    @PutMapping("/resetPassword")
    public Result<Object> resetPassword(@RequestParam Long userId) {
        logger.info("重置用户密码: 用户ID={}", userId);
        if (userService.resetPassword(userId)) {
            return Result.ok("密码重置成功");
        } else {
            return Result.error("密码重置失败");
        }
    }

    @Operation(summary = "分页查询用户列表")
    @GetMapping
    public Result<Object> getUserList(
        @RequestParam(defaultValue = "1") Integer current,
        @RequestParam(defaultValue = "10") Integer size,
        @RequestParam(required = false) String username
    ) {
        logger.info("分页查询用户列表，参数：current = {}, size = {}, username = {}", current, size, username);
        
        if (current <= 0 || size <= 0) {
            return Result.error("分页参数不合法");
        }

        Page<User> page = userService.page(new Page<>(current, size), new LambdaQueryWrapper<User>()
            .like(StringUtils.hasText(username), User::getUsername, username)
            .orderByAsc(User::getId)
        );

        return Result.success("查询用户成功", page);
    }

    @Operation(summary = "查询单个用户")
    @GetMapping("/{id}")
    public Result<Object> getUser(@PathVariable Long id) {
        logger.info("查询用户: 用户ID={}", id);
        User user = userService.getById(id);
        if (user != null) {
            return Result.ok(user);
        } else {
            return Result.error("用户不存在");
        }
    }

    @Operation(summary = "添加用户")
    @PostMapping("/add")
    public Result<Object> add(@RequestBody User user) {
        if (userService.save(user)) {
            return Result.created(user);
        } else {
            return Result.error("用户名已存在");
        }
    }

    @Operation(summary = "修改用户")
    @PutMapping("/{id}")
    public Result<Object> update(@PathVariable Long id, @RequestBody User user) {
        logger.info("更新用户: 用户ID={}", id);
        logger.info("更新用户: 用户信息={}", user);

        if (userService.updateById(user)) {
            return Result.ok(user);
        } else {
            return Result.error("更新用户失败");
        }
    }
    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Object> delete(@PathVariable Long id) {
        logger.info("删除用户: 用户ID={}", id);
        if (userService.removeById(id)) {
            return Result.ok("用户删除成功");
        } else {
            return Result.error("用户删除失败");
        }
    }

    @Operation(summary = "用户登录")
    @PostMapping("/userLogin")
    public Result<Map<String, Object>> userLogin(@RequestBody Map<String, Object> loginData) {
        // 验证请求数据是否正确
        if (loginData == null ||
                loginData.get("username") == null ||
                loginData.get("password") == null ||
                loginData.get("roleId") == null) {
            logger.warn("Incomplete login data provided");
            return Result.error(404, "请求数据不完整");
        }

        String username = (String) loginData.get("username");
        String password = (String) loginData.get("password");
        long roleId = Long.parseLong(loginData.get("roleId").toString());

        // 通过用户名查询用户
        User user = userService.findByUsername(username);
        if (user == null) {
            logger.warn("用户{}不存在", username);
            return Result.error(404, "用户不存在");}

        // 验证密码是否匹配
        if (!Objects.equals(user.getPassword(), password)) {
            logger.warn("用户名{}或密码错误", username);
            return Result.error(401,"用户名或密码错误");
        }

        // 判断角色Id,如果是管理员或者社区工作人员，则不能登录前台系统
        if (roleId == 4 || roleId == 3) {
            logger.warn("{}用户不允许登录此系统", username);
            return Result.error(401,"用户名或密码错误");
        }

        if (!Objects.equals(user.getRoleId(), roleId)) {
            logger.warn("Role mismatch for user: {}", username);
            return  Result.error(401,"用户角色不匹配");
        }
        if(user.getIsActive() == 0){
            logger.warn("用户已被禁用: {}", username);
            return Result.error(403,"用户已被禁用");
        }

        // 如果角色Id为2，则查询关联的老人信息
        if (roleId == 1) {
            Elder elder = elderService.getById(user.getElderId());
            if (elder == null) {
                return Result.error(404, "老人信息不存在，请联系管理员");
            }
        } else if (roleId == 2) {
            Kin kin = kinService.getById(user.getKinId());
            if (kin == null) {
                logger.warn("未查询用户信息: {}", username);
                return Result.error(404, "家属信息不存在，请联系管理员");
            }
            user.setKin(kin);
            Elder elder = elderService.getById(kin.getElderId());
            if (elder == null) {
                logger.warn("Elder information not found for user: {}", username);
                return Result.error(404, "未绑定老人账号");
            }
            user.setElder(elder);
        }

        JwtTokenUtil.TokenPair tokenPair = jwtTokenUtil.generateTokenPair(user.getUsername(), user.getRoleId());
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", tokenPair.accessToken());
        response.put("refreshToken", tokenPair.refreshToken());
        response.put("user", user);

        logger.info("用户 {} 登录成功", username);
        return Result.success("登录成功", response);

    }

    @Operation(summary = "管理员登录")
    @PostMapping("/adminLogin")
    public ResponseEntity<Result<Map<String, Object>>> adminLogin(@RequestBody Map<String, Object> loginData) {
        String username = (String) loginData.get("username");
        String password = (String) loginData.get("password");
        long RoleId=Long.parseLong(loginData.get("roleId").toString());
        User user = userService.findByUsername(username);
        logger.info("管理员登录: 用户名={}", username);
        if (user == null || !user.getPassword().equals(password)) {
            return ResponseEntity.status(401).body(Result.error("用户名或密码错误"));
        }
        if (user.getRoleId() == 3|| user.getRoleId() == 4) {
            JwtTokenUtil.TokenPair tokenPair = jwtTokenUtil.generateTokenPair(user.getUsername(), user.getRoleId());
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", tokenPair.accessToken());
            response.put("refreshToken", tokenPair.refreshToken());
            response.put("user", user);
            logger.info("管理员登录成功: 用户名={}, 角色ID={}", username, user.getRoleId());
            return ResponseEntity.ok(Result.ok(response));
        }
        return ResponseEntity.status(401).body(Result.error("权限不足"));
    }

    //管理员退出
    @Operation(summary = "管理员退出")
    @PostMapping("/adminLogout")
    public ResponseEntity<Result<Object>> adminLogout(@RequestBody Map<String, Object> logoutData) {
        String username = (String) logoutData.get("username");
        long RoleId=Long.parseLong(logoutData.get("roleId").toString());
        if (RoleId == 3|| RoleId == 4) {
            return ResponseEntity.ok(Result.ok("管理员退出成功"));
        }
        return ResponseEntity.status(401).body(Result.error("权限不足"));
    }

    @PutMapping("/profile")
    @Operation(summary = "更新个人信息")
    public Result<User> updateProfile(@RequestBody User user, @RequestAttribute("userId") Long userId) {
        // 确保只能更新自己的信息
        user.setId(userId);
        
        // 限制只能更新安全字段
        User currentUser = userService.getById(userId);
        if (currentUser != null) {
            // 只更新允许的字段
            currentUser.setPhone(user.getPhone());
            currentUser.setEmail(user.getEmail());
            currentUser.setAddress(user.getAddress());
            currentUser.setRemark(user.getRemark());
            
            // 用户名更新需要特殊处理
            if (!currentUser.getUsername().equals(user.getUsername())) {
                // 检查用户名是否已存在
                User existingUser = userService.findByUsername(user.getUsername());
                if (existingUser != null && !existingUser.getId().equals(userId)) {
                    return Result.error("用户名已存在");
                }
                currentUser.setUsername(user.getUsername());
            }
            
            boolean success = userService.updateById(currentUser);
            if (success) {
                return Result.success("个人信息更新成功", currentUser);
            }
        }
        
        return Result.error("个人信息更新失败");
    }

    @PostMapping("/change-password")
    @Operation(summary = "修改用户密码")
    public Result<Object> changePassword(@RequestBody Map<String, String> passwordData, @RequestAttribute("userId") Long userId) {
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");
        
        if (oldPassword == null || newPassword == null) {
            return Result.error("密码数据不完整");
        }
        
        // 验证旧密码是否正确
        User currentUser = userService.getById(userId);
        if (currentUser == null) {
            return Result.error("用户不存在");
        }
        
        if (!currentUser.getPassword().equals(oldPassword)) {
            return Result.error("原密码不正确");
        }
        
        // 更新密码
        currentUser.setPassword(newPassword);
        boolean success = userService.updateById(currentUser);
        
        if (success) {
            return Result.success("密码修改成功");
        } else {
            return Result.error("密码修改失败");
        }
    }
}