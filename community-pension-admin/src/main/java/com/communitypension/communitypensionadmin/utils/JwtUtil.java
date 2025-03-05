package com.communitypension.communitypensionadmin.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    // 密钥
    private final Key secretKey;
    // 过期时间
    private final Long expiration;
    // 新增刷新阈值（剩余时间小于总有效期的1/4时刷新）
    private static final double REFRESH_THRESHOLD = 0.25;
    @Getter
    @AllArgsConstructor
    public static class TokenStatus {
        private final boolean valid;
        private final boolean shouldRefresh;
        @Getter
        private final String error;


        public boolean shouldRefresh() {
            return shouldRefresh;
        }

    }
    public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") Long expiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    // 新增 token 清理方法
    private String cleanToken(String token) {
        if (token == null) return null;
        // 移除 Bearer 前缀和所有空格
        return token.replaceFirst("^Bearer\\s*", "").replaceAll("\\s+", "");
    }

    /**
     * 生成 token
     */
    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration*1000);

        Claims claims = Jwts.claims().subject(username).add("role", role).build();

        return "Bearer " + Jwts.builder()  // 确保 Bearer 前缀后没有额外空格
                .claims(claims).issuedAt(now).expiration(expiryDate).signWith(secretKey).compact();
    }

    /**
     * 从 token 中获取用户名
     */
    public String getUsernameFromToken(String token) {
        String cleanedToken = cleanToken(token);
        return Jwts.parser().verifyWith((SecretKey) secretKey).build().parseSignedClaims(cleanedToken).getPayload().getSubject();
    }

    /**
     * 从 token 中获取角色信息
     */
    public String getRoleFromToken(String token) {
        String cleanedToken = cleanToken(token);
        return Jwts.parser().verifyWith((SecretKey) secretKey).build().parseSignedClaims(cleanedToken).getPayload().get("role", String.class);
    }

    public boolean isTokenExpired(String token) {
        String cleanedToken = cleanToken(token);
        Date expirationDate = Jwts.parser().verifyWith((SecretKey) secretKey).build().parseSignedClaims(cleanedToken).getPayload().getExpiration();
        return expirationDate.before(new Date());
    }

    /**
     * 验证 token 是否有效
     */
    public TokenStatus validateToken(String token) {
        try {
            String cleanedToken = cleanToken(token);
            Claims claims = parseClaims(cleanedToken);

            Date expirationDate = claims.getExpiration();
            Date now = new Date();
            long remainingTime = expirationDate.getTime() - now.getTime();

            // 修正计算方式：使用配置的总时长替代Date对象
            long refreshThreshold = (long) (this.expiration * 1000 * REFRESH_THRESHOLD);
            boolean shouldRefresh = remainingTime < refreshThreshold;

            return new TokenStatus(true, shouldRefresh, null);
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("Token验证失败: " + e.getMessage());
            return new TokenStatus(false, false, e.getMessage());
        }
    }

    // 统一解析方法
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    // 新增刷新方法
    public String refreshToken(String token) {
        Claims claims = parseClaims(cleanToken(token));
        return generateToken(claims.getSubject(), claims.get("role", String.class));
    }



}