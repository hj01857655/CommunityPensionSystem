package com.communitypension.communitypensionadmin.controller;

import com.communitypension.communitypensionadmin.dto.UserDTO;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.enums.RoleEnum;
import com.communitypension.communitypensionadmin.service.TokenBlacklistService;
import com.communitypension.communitypensionadmin.service.UserService;
import com.communitypension.communitypensionadmin.utils.JwtTokenUtil;
import com.communitypension.communitypensionadmin.utils.Result;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
/**
 * 认证管理控制器
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    /**
     * 注入JwtTokenUtil
     */
    private final JwtTokenUtil jwtTokenUtil;
    /**
     * 注入UserService
     */
    @Autowired
    private   UserService userService;
    /**
     * 注入TokenBlacklistService
     */
    private final TokenBlacklistService tokenBlacklistService;
    @Autowired
    public AuthController(JwtTokenUtil jwtTokenUtil,TokenBlacklistService tokenBlacklistService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }
    /**
     * 用户登录
     *
     * @param loginData 登录数据
     * @return 登录结果
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> userLogin(@RequestBody Map<String, Object> loginData) {
        // 1. 首先验证请求数据完整性
        if (loginData.get("username") == null || loginData.get("password") == null || loginData.get("roleId") == null) {
            logger.warn("[前台]请求数据不完整，请提供用户名、密码和角色ID");
            return Result.error(404, "[前台]请求数据不完整，请提供用户名、密码和角色ID");
        }

        String username = (String) loginData.get("username");
        String password = (String) loginData.get("password");
        long roleId = Long.parseLong(loginData.get("roleId").toString());

        try {
            // 1. 根据用户名获取用户信息
            User user = userService.getUserByUsername(username);
            // 2. 验证用户是否存在
            if (user == null) {
                logger.warn("[前台]用户不存在: {}", username);
                return Result.error(401, "[前台]用户名或密码错误");
            }

            // 3. 验证密码
            if (!Objects.equals(user.getPassword(), password)) {
                logger.warn("[前台]密码错误: {}", username);
                return Result.error(401, "[前台]用户名或密码错误");
            }

            // 4. 验证用户状态
            if (user.getIsActive() == 0) {
                logger.warn("[前台]用户已被禁用: {}", username);
                return Result.error(403, "[前台]用户已被禁用");
            }

            // 5. 验证角色权限
            if (!RoleEnum.isFrontendRole(roleId)) {
                logger.warn("[前台]非前台角色尝试登录: {} (角色: {})", username, RoleEnum.getDescription(roleId));
                return Result.error(401, "[前台]您没有权限登录前台系统");
            }

            if (RoleEnum.isBackendRole(roleId)) {
                logger.warn("[前台]后台用户尝试前台登录: {}", username);
                return Result.error(401, "[前台]该账号属于后台用户，请使用后台登录");
            }

            // 6. 验证用户是否拥有该角色
            if (!userService.hasRole(user.getUserId(), roleId)) {
                logger.warn("[前台]用户角色不匹配: {} (尝试使用角色: {})", username, RoleEnum.getDescription(roleId));
                return Result.error(401, "[前台]用户角色不匹配");
            }

            // 7. 生成令牌并返回
            JwtTokenUtil.TokenPair tokenPair = jwtTokenUtil.generateTokenPair(username, roleId);
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", tokenPair.accessToken());
            response.put("refreshToken", tokenPair.refreshToken());
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO);
            // 额外设置角色信息
            userDTO.setRoles(userService.getUserRoles(user.getUserId()));
            // 设置角色ID和名称
            userDTO.setRoleIds(userService.getUserRoleIds(user.getUserId()));
            // 设置角色名称
            userDTO.setRoleNames(userService.getUserRoleNames(user.getUserId()));
            response.put("user", userDTO);

            logger.info("[前台]用户登录成功: {} (角色ID: {})", username, roleId);
            return Result.success("[前台]登录成功", response);

        } catch (IllegalArgumentException e) {
            logger.error("[前台]登录过程发生错误: {}", e.getMessage());
            return Result.error(500, "[前台]登录时发生错误: " + e.getMessage());
        }
    }
    /**
     * 管理员登录
     *
     * @param loginData 登录数据
     * @return 登录结果
     */
    @Operation(summary = "管理员登录")
    @PostMapping("/adminLogin")
    public ResponseEntity<Result<Map<String, Object>>> adminLogin(@RequestBody Map<String, Object> loginData) {
        try {
            // 1. 验证请求数据完整性
            if(loginData.get("username") == null || loginData.get("password") == null || loginData.get("roleId") == null) {
                logger.warn("[后台]请求数据不完整");
                return ResponseEntity.status(400).body(Result.error("[后台]请求数据不完整，请提供用户名、密码和角色ID"));
            }

            String username = (String) loginData.get("username");
            String password = (String) loginData.get("password");
            long roleId = Long.parseLong(loginData.get("roleId").toString());

            // 2. 验证是否为后台角色
            if(!RoleEnum.isBackendRole(roleId)){
                logger.warn("[后台]用户 {} 尝试以 {} 身份登录后台", username, RoleEnum.getDescription(roleId));
                return ResponseEntity.status(403).body(Result.error("您没有权限登录后台系统"));
            }

            // 3. 验证用户是否存在
            User user = userService.getUserByUsername(username);
            if(user == null) {
                logger.warn("[后台]用户名不存在: {}", username);
                return ResponseEntity.status(401).body(Result.error("用户名或密码错误"));
            }

            // 4. 验证密码
            if (!user.getPassword().equals(password)) {
                logger.warn("[后台]密码错误: {}", username);
                return ResponseEntity.status(401).body(Result.error("用户名或密码错误"));
            }

            // 5. 验证用户状态
            if (user.getIsActive() == 0) {
                logger.warn("[后台]用户已被禁用: {}", username);
                return ResponseEntity.status(403).body(Result.error("用户已被禁用"));
            }

            // 6. 验证用户是否拥有该角色
            if(!userService.hasRole(user.getUserId(), roleId)){
                logger.warn("[后台]用户角色不匹配: {} (尝试使用角色: {})", username, RoleEnum.getDescription(roleId));
                return ResponseEntity.status(403).body(Result.error("您没有权限使用此角色登录"));
            }

            // 7. 生成令牌并返回
            JwtTokenUtil.TokenPair tokenPair = jwtTokenUtil.generateTokenPair(user.getUsername(), roleId);
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", tokenPair.accessToken());
            response.put("refreshToken", tokenPair.refreshToken());
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO);
            userDTO.setRoles(userService.getUserRoles(user.getUserId()));
            userDTO.setRoleIds(userService.getUserRoleIds(user.getUserId()));
            userDTO.setRoleNames(userService.getUserRoleNames(user.getUserId()));
            response.put("user", userDTO);

            logger.info("[后台]管理员登录成功: 用户名={}, 角色ID={}", username, roleId);
            return ResponseEntity.ok(Result.ok(response));

        } catch (NumberFormatException e) {
            logger.error("[后台]角色ID格式错误: {}", e.getMessage());
            return ResponseEntity.status(400).body(Result.error("[后台]角色ID格式错误"));
        } catch (Exception e) {
            logger.error("[后台]管理员登录失败: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Result.error("[后台]登录时发生错误"));
        }
    }

    /**
     * 管理员退出
     *
     * @param logoutData 退出数据
     * @return 退出结果
     */
    @Operation(summary = "管理员退出")
    @PostMapping("/adminLogout")
    public ResponseEntity<Result<Object>> adminLogout(@RequestBody Map<String, Object> logoutData) {
        String username = (String) logoutData.get("username");
        User user = userService.getUserByUsername(username);
        if(!user.getUsername().equals(username)){
            return ResponseEntity.status(401).body(Result.error("用户名错误"));

        }
        long RoleId = Long.parseLong(logoutData.get("roleId").toString());
        if (RoleId == 3 || RoleId == 4) {
            return ResponseEntity.ok(Result.ok("管理员退出成功"));
        }
        return ResponseEntity.status(401).body(Result.error("权限不足"));
    }
    @Operation(summary = "刷新令牌")
    @PostMapping("/refresh")
    public ResponseEntity<Result<Map<String, String>>> refreshToken(@RequestHeader("Refresh-Token") String refreshToken) {
        try {
            // 验证刷新令牌
            JwtTokenUtil.TokenStatus status = jwtTokenUtil.validateRefreshToken(refreshToken);
            if (!status.valid()) {
                logger.warn("刷新令牌无效: {}", status.error());
                return ResponseEntity.status(401).body(Result.error("刷新令牌无效: " + status.error()));
            }

            // 在refreshToken方法开始处添加检查
            // 检查黑名单, 如果令牌在黑名单中，返回错误响应
            if(tokenBlacklistService.isBlacklisted(refreshToken)) {
                return ResponseEntity.status(401).body(Result.error("令牌已失效"));
            }

            // 从刷新令牌中获取用户信息
            Claims claims = status.claims();
            String username = claims.getSubject();
            Long roleId = claims.get("roleId", Long.class);

            // 生成新的令牌对
            JwtTokenUtil.TokenPair tokenPair = jwtTokenUtil.generateTokenPair(username, roleId);

            // 将旧的刷新令牌加入黑名单
            Date expiration = claims.getExpiration();
            tokenBlacklistService.addToBlacklist(refreshToken, expiration.getTime());

            Map<String, String> response = new HashMap<>();
            response.put("accessToken", tokenPair.accessToken());
            response.put("refreshToken", tokenPair.refreshToken());

            logger.info("令牌刷新成功: 用户={}, 角色ID={}", username, roleId);
            return ResponseEntity.ok(Result.success("令牌刷新成功", response));
        } catch (Exception e) {
            logger.error("刷新令牌失败: {}", e.getMessage(), e);
            return ResponseEntity.status(401).body(Result.error("刷新令牌失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "使令牌失效")
    @PostMapping("/invalidate")
    public ResponseEntity<Result<Object>> invalidateToken(
            @RequestHeader("Authorization") String accessToken,
            @RequestHeader(value = "Refresh-Token", required = false) String refreshToken) {
        try {
            // 清理token前缀
            String cleanedAccessToken = jwtTokenUtil.cleanToken(accessToken);
            
            // 解析访问令牌，获取过期时间
            Claims accessClaims = jwtTokenUtil.getClaimsFromToken(cleanedAccessToken);
            Date accessExpiration = accessClaims.getExpiration();
            
            // 将访问令牌加入黑名单
            tokenBlacklistService.addToBlacklist(cleanedAccessToken, accessExpiration.getTime());
            
            // 如果提供了刷新令牌，也将其加入黑名单
            if (refreshToken != null && !refreshToken.isEmpty()) {
                String cleanedRefreshToken = jwtTokenUtil.cleanToken(refreshToken);
                Claims refreshClaims = jwtTokenUtil.getClaimsFromToken(cleanedRefreshToken);
                Date refreshExpiration = refreshClaims.getExpiration();
                tokenBlacklistService.addToBlacklist(cleanedRefreshToken, refreshExpiration.getTime());
            }
            
            logger.info("令牌已成功失效");
            return ResponseEntity.ok(Result.success("令牌已成功失效"));
        } catch (Exception e) {
            logger.error("使令牌失效失败: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Result.error("使令牌失效失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "用户退出")
    @PostMapping("/logout")
    public ResponseEntity<Result<Object>> logout(
            @RequestHeader("Authorization") String accessToken,
            @RequestHeader(value = "Refresh-Token", required = false) String refreshToken) {
        try {
            // 清理token前缀
            String cleanedAccessToken = jwtTokenUtil.cleanToken(accessToken);
            
            // 解析访问令牌，获取用户信息和过期时间
            Claims accessClaims = jwtTokenUtil.getClaimsFromToken(cleanedAccessToken);
            String username = accessClaims.getSubject();
            Long roleId = accessClaims.get("roleId", Long.class);
            Date accessExpiration = accessClaims.getExpiration();
            
            // 将访问令牌加入黑名单
            tokenBlacklistService.addToBlacklist(cleanedAccessToken, accessExpiration.getTime());
            
            // 如果提供了刷新令牌，也将其加入黑名单
            if (refreshToken != null && !refreshToken.isEmpty()) {
                String cleanedRefreshToken = jwtTokenUtil.cleanToken(refreshToken);
                Claims refreshClaims = jwtTokenUtil.getClaimsFromToken(cleanedRefreshToken);
                Date refreshExpiration = refreshClaims.getExpiration();
                tokenBlacklistService.addToBlacklist(cleanedRefreshToken, refreshExpiration.getTime());
            }
            
            logger.info("用户退出成功: 用户={}, 角色ID={}", username, roleId);
            return ResponseEntity.ok(Result.success("退出成功"));
        } catch (Exception e) {
            logger.error("用户退出失败: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Result.error("退出失败: " + e.getMessage()));
        }
    }
}