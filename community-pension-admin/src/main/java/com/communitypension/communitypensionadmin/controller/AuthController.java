package com.communitypension.communitypensionadmin.controller;

import com.communitypension.communitypensionadmin.service.TokenBlacklistService;
import com.communitypension.communitypensionadmin.utils.JwtTokenUtil;
import com.communitypension.communitypensionadmin.utils.Result;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")

@Tag(name = "认证管理")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final JwtTokenUtil jwtTokenUtil;
    private final TokenBlacklistService tokenBlacklistService;

    @Autowired
    public AuthController(JwtTokenUtil jwtTokenUtil, TokenBlacklistService tokenBlacklistService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.tokenBlacklistService = tokenBlacklistService;
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