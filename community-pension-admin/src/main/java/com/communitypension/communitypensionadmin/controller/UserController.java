package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.communitypension.communitypensionadmin.entity.Role;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.service.*;
import com.communitypension.communitypensionadmin.util.Result;
import com.communitypension.communitypensionadmin.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:8081")
@Tag(name = "用户管理")
public class UserController {
    // 日志记录器
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(UserController.class);
    // 用户服务
    private final UserService userService;
    // 角色服务
    private final RoleService roleService;
    // JWT工具类
    @Autowired
    private JwtUtil jwtUtil;

    // 构造函数
    @Autowired
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }
    @Operation(summary = "刷新token")
    @PostMapping("/refreshToken") //
    public ResponseEntity<Result<Map<String, Object>>> refreshToken(@RequestHeader("Authorization") String token) {
        // 如果token为空，返回错误信息
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Result.error(400, "Authorization header is missing or empty"));
        }
        try {
            JwtUtil.TokenStatus status = jwtUtil.validateToken(token);
            // 检查令牌是否有效
            if (status.isValid()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(Result.success("token有效，无需刷新", null));
            }
            // 如果当前令牌无效，则生成新令牌
            String newToken = jwtUtil.refreshToken(token);
            Map<String, Object> data = new HashMap<>();
            data.put("token", newToken);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(Result.success("token刷新成功", data));

        } catch (Exception e) {
            logger.error("刷新token失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.error(500, "刷新token失败，请稍后重试"));
        }
    }


    // 查询所有用户
    @Operation(summary = "查询所有用户")
    @GetMapping("")
    public Result<Object> getUserList(@RequestHeader("Authorization") String token) {
        JwtUtil.TokenStatus status = jwtUtil.validateToken(token);

        if (!status.isValid()) {
            // 明确区分过期和其他错误类型
            String errorMsg = status.getError().contains("expired") ? "登录已过期，请重新登录" : "无效的令牌";
            return Result.error(401, errorMsg);
        }

        Map<String, Object> result = new HashMap<>();
        List<User> users = userService.list();
        // 如果 token 即将过期，生成新的 token
        if (status.shouldRefresh()) {
            String newToken = jwtUtil.refreshToken(token);
            result.put("newToken", newToken);
            result.put("tokenExpire", true);  // 添加过期标志
            return Result.success("登录已过期，请重新登录", result);
        }
        return Result.success("查询成功", users);
    }

    // 查询单个用户
    @Operation(summary = "查询单个用户")
    @GetMapping("/{id}")
    public Result<Object> getUser(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        JwtUtil.TokenStatus status = jwtUtil.validateToken(token);
        if (!status.isValid()) {
            String errorMsg = status.getError().contains("expired") ? "登录已过期，请重新登录" : "无效的令牌";
            return Result.error(401, errorMsg);
        }
        Map<String, Object> result = new HashMap<>();
        User user = userService.getOne(new QueryWrapper<User>().eq("username", jwtUtil.getUsernameFromToken(token)));
        if (status.shouldRefresh()) {
            String newToken = jwtUtil.refreshToken(token);
            result.put("newToken", newToken);
            result.put("tokenExpire", true);  // 添加过期标志
            return Result.success("登录已过期，请重新登录", result);
        }
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        return Result.success("查询成功", user);
    }

    // 添加用户
    @Operation(summary = "添加用户")
    @PostMapping("")
    public Result<Object> add(@RequestHeader("Authorization") String token, @RequestBody User user) {
        JwtUtil.TokenStatus status = jwtUtil.validateToken(token);
        if (!status.isValid()) {
            String errorMsg = status.getError().contains("expired") ? "登录已过期，请重新登录" : "无效的令牌";
            return Result.error(401, errorMsg);
        }
        return Result.success("添加成功", userService.save(user));
    }

    // 更新用户
    @Operation(summary = "更新用户")
    @PutMapping("")
    public Result<Object> update(@RequestHeader("Authorization") String token, @RequestBody User user) {
        JwtUtil.TokenStatus status = jwtUtil.validateToken(token);
        if (!status.isValid()) {
            String errorMsg = status.getError().contains("expired") ? "登录已过期，请重新登录" : "无效的令牌";
            return Result.error(401, errorMsg);
        }
        return Result.success("修改成功", userService.updateById(user));
    }

    // 删除用户
    @Operation(summary = "删除用户")
    @DeleteMapping("{id}")
    public Result<Object> delete(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        JwtUtil.TokenStatus status = jwtUtil.validateToken(token);
        if (!status.isValid()) {
            String errorMsg = status.getError().contains("expired") ? "登录已过期，请重新登录" : "无效的令牌";
            return Result.error(401, errorMsg);
        }
        boolean flag = userService.removeById(id);
        if (!flag) {
            return Result.error(400, "删除失败");
        } else {
            return Result.success("删除成功");
        }
    }

    // 用户登录
    @Operation(summary = "用户登录")
    @PostMapping("/userLogin")
    public ResponseEntity<Map<String, Object>> userLogin(@RequestBody Map<String, Object> loginData) {
        String username = (String) loginData.get("username");
        String password = (String) loginData.get("password");
        Long roleId = Long.parseLong(loginData.get("roleId").toString());
        Map<String, Object> data = new HashMap<>();
        try {
            //如果用户名或密码为空，返回参数错误
            if (username == null || password == null) {
                return ResponseEntity.badRequest().body(null);
            }

            // 根据用户名、密码和角色id查询用户，如果用户不存在，响应401：用户名或密码错误
            User user = userService.getOne(new QueryWrapper<User>().eq("username", username).eq("password", password).eq("role_id", roleId));
            // 根据角色id查询角色
            Role role = roleService.getById(roleId);
            if (user == null) {
                return ResponseEntity.badRequest().body(null);
            }
            if (user.getStatus() != 1) {
                return ResponseEntity.badRequest().body(null);
            }
            if (role == null) {
                return ResponseEntity.badRequest().body(null);
            }
            // 生成令牌
            String token = jwtUtil.generateToken(user.getUsername(), role.getId());
            data.put("user", user);
            data.put("token", token);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            logger.error("登录失败", e);
            data.put("user", null);
            data.put("token", null);
            return ResponseEntity.badRequest().body(data);
        }
    }

    // 管理员登录
    @Operation(summary = "管理员登录")
    @PostMapping("/adminLogin")
    public ResponseEntity<Result<Map<String, Object>>> adminLogin(@RequestBody Map<String, Object> loginData) {
        String username = (String) loginData.get("username");
        String password = (String) loginData.get("password");
        Long roleId = Long.parseLong(loginData.get("roleId").toString());

        try {
            // 根据用户名、密码和角色id查询用户
            User user = userService.getOne(new QueryWrapper<User>().eq("username", username).eq("password", password).eq("role_id", roleId));
            if (user == null) {
                return ResponseEntity.status(401).body(Result.error(401, "用户名或密码错误"));
            }

            // 根据角色id查询角色
            Role role = roleService.getById(roleId);
            if (role == null) {
                return ResponseEntity.ok(Result.error(400, "角色不存在"));
            }

            // 验证角色是否为管理员
            if (role.getRoleName().equals("admin")) {
                Map<String, Object> data = new HashMap<>();
                data.put("user", user);
                data.put("token", jwtUtil.generateToken(user.getUsername(), role.getId()));
                return ResponseEntity.ok(Result.success("登录成功", data));
            } else {
                return ResponseEntity.ok(Result.error(400, "角色不是管理员"));
            }
        } catch (Exception e) {
            logger.error("登录失败", e);
            Map<String, Object> data = new HashMap<>();
            data.put("user", null);
            data.put("token", null);
            return ResponseEntity.status(500).body(Result.error(500, "登录失败，请稍后重试", data));
        }
    }

    // 重置密码为123456
    @Operation(summary = "重置密码为123456")
    @PutMapping("/resetPassword")
    public ResponseEntity<Result<Object>> resetPassword(@RequestHeader("Authorization") String token, @RequestParam Long userId) {
        try {
            JwtUtil.TokenStatus status = jwtUtil.validateToken(token);
            if (!status.isValid()) {
                // 统一中文错误提示
                String errorMsg = status.getError().contains("expired") ? "登录已过期，请重新登录" : "无效的令牌";
                return ResponseEntity.status(401).body(Result.error(401, errorMsg));
            }
            // 根据用户id查询用户
            User user = userService.getById(userId);
            if (user != null) {
                // 重置密码为123456
                user.setPassword("123456");
                userService.updateById(user);
                return ResponseEntity.ok(Result.success("重置成功"));
            }
            return ResponseEntity.status(404).body(Result.error(404, "User not found"));

        } catch (Exception e) {
            logger.error("Reset failed", e);
            return ResponseEntity.status(500).body(Result.error(500, "重置失败，请稍后重试"));
        }
    }

    /**
     * 获取用户信息
     *
     * @param token：令牌
     * @return {message: 查询成功, data: 用户信息}
     */
    @GetMapping("/userInfo")
    @Operation(summary = "获取用户信息")
    @Parameters(value = {@Parameter(name = "token", description = "令牌", required = true)})
    public ResponseEntity<Result<User>> getInfoWithUser(@RequestHeader("Authorization") String token) {
        try {
            // 验证令牌
            JwtUtil.TokenStatus status = jwtUtil.validateToken(token);
            if (!status.isValid()) {
                String errorMsg = status.getError().contains("expired") ? "登录已过期，请重新登录" : "无效的令牌";
                return ResponseEntity.status(401).body(Result.error(401, errorMsg));
            }
            String username = jwtUtil.getUsernameFromToken(token);
            logger.info("username: {}", username);
            Long roleId = jwtUtil.getRoleIdFromToken(token);
            // 根据用户名查询用户
            User user = userService.getOne(new QueryWrapper<User>().eq("username", username).eq("role_id", roleId));
            if (user == null) {
                return ResponseEntity.ok(Result.error(404, "用户不存在"));
            }

            // 从 token 中获取用户名
            Role role = roleService.getById(roleId);
            if (role == null) {
                return ResponseEntity.ok(Result.error(400, "角色不存在"));
            }




            User user1 = userService.getInfoWithUser(user);
            return ResponseEntity.ok(Result.success("查询成功", user1));

        } catch (Exception e) {
            logger.error("获取用户信息失败", e);
            return ResponseEntity.status(500).body(Result.error(500, "获取用户信息失败，请稍后重试"));
        }
    }






}
