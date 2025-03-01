package com.communitypension.communitypensionadmin.utils;

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
    private static Key secretKey;

    static {
        // 从配置文件中读取密钥
        secretKey = Jwts.SIG.HS256.key().build();
    }
    public void setSecretKey(String secret) {
        // 自动校验密钥长度并转换为 Key 对象
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * token 过期时间，单位毫秒
     */
    @Value("${jwt.expiration}")
    private static Long expiration;
    /**
     * 生成 token
     */
    public static String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder().subject(username).issuedAt(now).expiration(expiryDate).signWith(secretKey) // 使用新 API
                .compact();
    }
    /**
     * 从 token 中获取用户名
     */
    public static String getUsernameFromToken(String token) {
        return Jwts.parser().verifyWith((SecretKey) secretKey) // 同步使用新 API
                .build().parseSignedClaims(token).getPayload().getSubject();
    }
    /**
     * 验证 token 是否有效
     */
    public static  boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) secretKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {

            return false;
        }
    }

}