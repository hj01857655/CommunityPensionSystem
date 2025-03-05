package com.communitypension.communitypensionadmin.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private Key secretKey;

    private Long expiration;

    public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") Long expiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    /**
     * 生成 token
     */
    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        Claims claims = Jwts.claims()
                .subject(username)
                .add("role", role)
                .build();

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 从 token 中获取用户名
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser().verifyWith((SecretKey) secretKey).build().parseSignedClaims(token).getPayload().getSubject();
    }

    /**
     * 从 token 中获取角色信息
     */
    public String getRoleFromToken(String token) {
        return Jwts.parser().verifyWith((SecretKey) secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    /**
     * 验证 token 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) secretKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 添加日志记录
            return false;
        }
    }
}