package com.communitypension.communitypensionadmin.controller;

import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.service.UserService;
import com.communitypension.communitypensionadmin.util.Result;
import org.slf4j.Logger;
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

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody Map<String, Object> loginData) {
        String username = (String) loginData.get("username");
        String password = (String) loginData.get("password");
        Integer roleId = (Integer) loginData.get("roleId");
        try {
            User user = userService.getUserByUsernameAndRole(username, Long.valueOf(roleId));
            logger.info("user: {}", user);
            Map<String, Object> data = new HashMap<>();
            if (Objects.equals(user.getPassword(), password)) {
                data.put("user", user);
                return Result.success("登录成功", user);
            } else {
                return Result.error(401, "用户名或密码错误");
            }
        } catch (Exception e) {
            return Result.error(500, "登录失败，请稍后重试");
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
}