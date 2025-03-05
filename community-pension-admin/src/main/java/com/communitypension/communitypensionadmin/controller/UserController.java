package com.communitypension.communitypensionadmin.controller;

import com.communitypension.communitypensionadmin.entity.Role;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.service.RoleService;
import com.communitypension.communitypensionadmin.service.UserService;
import com.communitypension.communitypensionadmin.util.Result;
import com.communitypension.communitypensionadmin.utils.JwtUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/user/")
@CrossOrigin(origins = "http://localhost:8081")
public class UserController {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final RoleService roleService;
    @Autowired
    private JwtUtil jwtUtil;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    /**
     * 登录
     * @param loginData：{username: 用户名, password: 密码, roleId: 角色id}
     * @return {user: 用户信息, token: 令牌}
     */
    @PostMapping("/login")
    public ResponseEntity<Result<Map<String, Object>>> login(@RequestBody Map<String, Object> loginData) {
        String username = (String) loginData.get("username");
        String password = (String) loginData.get("password");
        Long roleId = Long.valueOf((Integer) loginData.get("roleId"));
        logger.info("username: {}, roleId: {}", username, roleId);
        try {
            User user = userService.getUserByUsernameAndRole(username, roleId);
            Role role = roleService.getById(roleId);
            logger.info("user: {}", user);
            Map<String, Object> data = new HashMap<>();
            if (user != null && Objects.equals(user.getPassword(), password)) {
                String token = jwtUtil.generateToken(user.getUsername(), role.getRoleName());
                data.put("user", user);
                data.put("token", token);
                return ResponseEntity.ok(Result.success("登录成功", data));
            } else {
                data.put("user", null);
                data.put("token", null);
                return ResponseEntity.status(401).body(Result.error(401, "用户名或密码错误", data));
            }
        } catch (Exception e) {
            logger.error("登录失败", e);
            Map<String, Object> data = new HashMap<>();
            data.put("user", null);
            data.put("token", null);
            return ResponseEntity.status(500).body(Result.error(500, "登录失败，请稍后重试", data));
        }
    }

    @PostMapping("/changePassword")
    public Result<String> changePassword(@RequestBody Map<String, Object> changeData) {
        String username = (String) changeData.get("username");
        String oldPassword = (String) changeData.get("oldPassword");
        String newPassword = (String) changeData.get("newPassword");
        Integer roleId = (Integer) changeData.get("roleId");
        try {
            User user = userService.getUserByUsernameAndRole(username, Long.valueOf(roleId));
            if (Objects.equals(user.getPassword(), oldPassword)) {
                user.setPassword(newPassword);
                userService.changePassword(username, oldPassword, newPassword, Long.valueOf(roleId));
                return Result.success("修改密码成功");
            } else {
                return Result.error(401, "原密码错误");
            }
        } catch (Exception e) {
            return Result.error(500, "修改密码失败，请稍后重试");
        }
    }

    @GetMapping("/UserInfo")
    public ResponseEntity<Result<User>> getUserInfo(@RequestHeader("Authorization") String token, @RequestParam Long roleId) {
        try {
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(Result.error(401, "无效的令牌"));
            }

            String roleName = jwtUtil.getRoleFromToken(token);
            Role role = roleService.getRoleByName(roleName);

            if (role != null && role.getId().equals(roleId)) {
                User user = userService.getUserByRoleId(roleId);
                return ResponseEntity.ok(Result.success("查询成功", user));
            } else {
                return ResponseEntity.status(401).body(Result.error(401, "角色不匹配"));
            }
        } catch (Exception e) {
            logger.error("查询失败", e);
            return ResponseEntity.status(500).body(Result.error(500, "查询失败，请稍后重试"));
        }
    }



}