package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.communitypension.communitypensionadmin.entity.Role;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.service.*;
import com.communitypension.communitypensionadmin.util.Result;
import com.communitypension.communitypensionadmin.utils.JwtUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:8081")
public class UserController {
    // 日志记录器
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(UserController.class);
    // 用户服务
    private final UserService userService;
    // 角色服务
    private final RoleService roleService;
    // 老人服务
    private final ElderService elderService;
    // 亲属服务
    private final KinService kinService;
    // 社区工作人员服务
    private final StaffService staffService;
    // JWT工具类
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, RoleService roleService, ElderService elderService, KinService kinService, StaffService staffService) {
        this.userService = userService;
        this.roleService = roleService;
        this.elderService = elderService;
        this.kinService = kinService;
        this.staffService = staffService;
    }

    @GetMapping("")
    public Result<Object> getUserList(@RequestHeader("Authorization") String token) {
        JwtUtil.TokenStatus status = jwtUtil.validateToken(token);

        if (!status.isValid()) {
            // 明确区分过期和其他错误类型
            String errorMsg = status.getError().contains("expired") ?
                    "登录已过期，请重新登录" : "无效的令牌";
            return Result.error(401, errorMsg);
        }

        Map<String, Object> result = new HashMap<>();
        List<User> users = userService.list();

        if (status.shouldRefresh()) {
            String newToken = jwtUtil.refreshToken(token);
            result.put("newToken", newToken);
            result.put("tokenExpire", true);  // 添加过期标志
            return Result.success("登录已过期，请重新登录", result);
        }
        return Result.success("查询成功", users);
    }

    // 查询用户
    @GetMapping("/{id}")
    public Result<Object> getUser(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        JwtUtil.TokenStatus status = jwtUtil.validateToken(token);
        if (!status.isValid()) {
            String errorMsg = status.getError().contains("expired") ?
                    "登录已过期，请重新登录" : "无效的令牌";
            return Result.error(401, errorMsg);
        }
        return Result.success("查询成功", userService.getById(id));
    }

    // 添加用户
    @PostMapping("/add")
    public Result<Object> add(@RequestHeader("Authorization") String token, @RequestBody User user) {
        JwtUtil.TokenStatus status = jwtUtil.validateToken(token);
        if (!status.isValid()) {
            String errorMsg = status.getError().contains("expired") ?
                    "登录已过期，请重新登录" : "无效的令牌";
            return Result.error(401, errorMsg);
        }
        return Result.success("添加成功", userService.save(user));
    }

    // 修改用户
    @PutMapping("/update")
    public Result<Object> update(@RequestHeader("Authorization") String token, @RequestBody User user) {
        JwtUtil.TokenStatus status = jwtUtil.validateToken(token);
        if (!status.isValid()) {
            String errorMsg = status.getError().contains("expired") ?
                    "登录已过期，请重新登录" : "无效的令牌";
            return Result.error(401, errorMsg);
        }
        return Result.success("修改成功", userService.updateById(user));
    }

    // 删除用户
    @DeleteMapping("/delete/{id}")
    public Result<Object> delete(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        JwtUtil.TokenStatus status = jwtUtil.validateToken(token);
        if (!status.isValid()) {
            String errorMsg = status.getError().contains("expired") ?
                    "登录已过期，请重新登录" : "无效的令牌";
            return Result.error(401, errorMsg);
        }
        boolean flag = userService.removeById(id);
        if (!flag) {
            return Result.error(400, "删除失败");
        } else {
            return Result.error(400, "删除成功");
        }
    }

    // 管理员登录
    @PostMapping("/login")
    public ResponseEntity<Result<Map<String, Object>>> login(@RequestBody Map<String, Object> loginData) {
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

            // 生成令牌
            String token = jwtUtil.generateToken(user.getUsername(), role.getRoleName());
            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            data.put("token", token);
            // 返回用户信息和令牌
            return ResponseEntity.ok(Result.success("登录成功", data));
        } catch (Exception e) {
            logger.error("登录失败", e);
            Map<String, Object> data = new HashMap<>();
            data.put("user", null);
            data.put("token", null);
            return ResponseEntity.status(500).body(Result.error(500, "登录失败，请稍后重试", data));
        }
    }

    // 管理员登录
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
    @PutMapping("/resetPassword")
    public ResponseEntity<Result<Object>> resetPassword(@RequestHeader("Authorization") String token, @RequestParam Long userId) {
        try {
            JwtUtil.TokenStatus status = jwtUtil.validateToken(token);
            if (!status.isValid()) {
                // 统一中文错误提示
                String errorMsg = status.getError().contains("expired") ?
                        "登录已过期，请重新登录" : "无效的令牌";
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
    @GetMapping("/getUserInfo")
    public ResponseEntity<Result<Object>> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            // 验证令牌
            JwtUtil.TokenStatus status = jwtUtil.validateToken(token);
            if (!status.isValid()) {
                String errorMsg = status.getError().contains("expired") ?
                        "登录已过期，请重新登录" : "无效的令牌";
                return ResponseEntity.status(401).body(Result.error(401, errorMsg));
            }

            // 从 token 中获取用户名
            String username = jwtUtil.getUsernameFromToken(token);
            if (username == null) {
                return ResponseEntity.status(401).body(Result.error(401, "无效的令牌"));
            }

            // 根据用户名查询用户
            User user = userService.getOne(new QueryWrapper<User>().eq("username", username));
            if (user == null) {
                return ResponseEntity.ok(Result.error(404, "用户不存在"));
            }

            // 根据角色id查询角色
            Role role = roleService.getById(user.getRoleId());
            if (role == null) {
                return ResponseEntity.ok(Result.error(400, "角色不存在"));
            }

            // 将用户信息和角色信息放入响应体
            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            data.put("role", role);

            // 如果 token 即将过期，生成新的 token
            if (status.shouldRefresh()) {
                String newToken = jwtUtil.refreshToken(token);
                data.put("newToken", newToken);
                data.put("tokenExpire", true);  // 添加过期标志
            }

            return ResponseEntity.ok(Result.success("查询成功", data));
        } catch (Exception e) {
            logger.error("获取用户信息失败", e);
            return ResponseEntity.status(500).body(Result.error(500, "获取用户信息失败，请稍后重试"));
        }
    }
}
