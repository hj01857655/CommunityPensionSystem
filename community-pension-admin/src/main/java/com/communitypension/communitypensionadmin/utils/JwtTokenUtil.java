package com.communitypension.communitypensionadmin.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration; // 访问令牌过期时间，单位毫秒

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration; // 刷新令牌过期时间，单位毫秒

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        logger.info("JwtTokenUtil 初始化完成，访问令牌有效期: {} 毫秒, 刷新令牌有效期: {} 毫秒", 
                accessTokenExpiration, refreshTokenExpiration);
    }

    /**
     * 清理token字符串
     */
    public String cleanToken(String token) {
        if (token == null || token.isEmpty()) return null;
        return token.replaceFirst("^Bearer\\s*", "").replaceAll("\\s+", "");
    }

    /**
     * 生成访问令牌
     */
    public String generateAccessToken(String username,Long roleId) {
        return generateToken(username ,roleId,accessTokenExpiration, "access");
    }

    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(String username,Long roleId) {
        return generateToken(username, roleId,refreshTokenExpiration, "refresh");
    }

    /**
     * 生成令牌
     */
    private String generateToken(String username, Long roleId,long expiration, String tokenType) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        logger.info("生成{}令牌: 用户={},  过期时间={}", tokenType, username, expiryDate);

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", username);
        claims.put("type", tokenType);
        claims.put("roleId", roleId);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 生成令牌对
     */
    public TokenPair generateTokenPair(String username,Long roleId) {
        String accessToken = generateAccessToken(username, roleId);
        String refreshToken = generateRefreshToken(username,roleId);
        return new TokenPair(accessToken, refreshToken);
    }

    /**
     * 验证访问令牌
     */
    public TokenStatus validateAccessToken(String token) {
        return validateToken(token, "access");
    }

    /**
     * 验证刷新令牌
     */
    public TokenStatus validateRefreshToken(String token) {
        return validateToken(token, "refresh");
    }

    /**
     * 验证令牌
     */
    private TokenStatus validateToken(String token, String expectedType) {
        if (token == null || token.isEmpty()) {
            logger.warn("验证令牌: 空令牌");
            return new TokenStatus(false, "空令牌", null);
        }
        try {
            String cleanedToken = cleanToken(token);
            Claims claims = getClaimsFromToken(cleanedToken);
            String username = claims.getSubject();
            String tokenType = claims.get("type", String.class);
            Date expirationDate = claims.getExpiration();
            Date now = new Date();

            // 检查令牌类型
            if (!expectedType.equals(tokenType)) {
                logger.warn("令牌类型不匹配: 期望={}, 实际={}", expectedType, tokenType);
                return new TokenStatus(false, "令牌类型不匹配", null);
            }

            // 检查令牌是否过期
            if (expirationDate.before(now)) {
                long expiredMillis = now.getTime() - expirationDate.getTime();
                logger.warn("令牌已过期: 用户={}, 过期了 {} 秒", username, expiredMillis / 1000);
                return new TokenStatus(false, "令牌已过期", null);
            }

            logger.info("令牌通过验证: 用户={}, 类型={}", username, tokenType);
            return new TokenStatus(true, null, claims);
        } catch (ExpiredJwtException ex) {
            logger.warn("令牌已过期: {}", ex.getMessage());
            return new TokenStatus(false, "令牌已过期", null);
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("令牌验证失败: {}", e.getMessage(), e);
            return new TokenStatus(false, e.getMessage(), null);
        }
    }

    /**
     * 从刷新令牌生成新的访问令牌
     */
    public String generateNewAccessTokenFromRefreshToken(String refreshToken) {
        TokenStatus status = validateRefreshToken(refreshToken);
        if (!status.valid()) {
            throw new SecurityException("无效的刷新令牌: " + status.error());
        }
        
        Claims claims = status.claims();
        String username = claims.getSubject();
        Long roleId=getRoleIdFromToken(refreshToken);
        return generateAccessToken(username,roleId);
    }

    /**
     * 从令牌中获取Claims
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(cleanToken(token))
                .getPayload();
    }

    /**
     * 从令牌中获取用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(cleanToken(token));
            return claims.getSubject();
        } catch (Exception e) {
            logger.error("从令牌获取用户名失败: {}", e.getMessage(), e);
            return null;
        }
    }
    public Long getRoleIdFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(cleanToken(token));
            return claims.get("roleId", Long.class);
        } catch (Exception e) {
            logger.error("从令牌获取用户名失败: {}", e.getMessage(), e);
            return null;
        }

    }

    /**
     * 令牌状态记录
     */
    public record TokenStatus(boolean valid, String error, Claims claims) {}

    /**
     * 令牌对记录
     */
    public record TokenPair(String accessToken, String refreshToken) {}
}