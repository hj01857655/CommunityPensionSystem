package com.communitypension.communitypensionadmin.utils;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

/**
 * Refresh Token Cookie 工具
 *
 * 安全设计：将 refresh token 写入 HttpOnly cookie，JS 无法读取，可防 XSS 窃取。
 * - HttpOnly：脚本不可访问
 * - Secure：仅 HTTPS 传输（开发环境为 HTTP，须配为 false，否则浏览器不保存）
 * - SameSite：防 CSRF，默认 Lax
 * - Path 限定 /api/auth：refresh token 仅随认证类请求发送，不随其他 API 外泄
 */
@Component
public class RefreshTokenCookieUtil {

    /** Cookie 名称 */
    public static final String COOKIE_NAME = "refresh-token";

    /** Cookie 作用路径：仅认证相关接口 */
    private static final String COOKIE_PATH = "/api/auth";

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration; // 单位：毫秒

    /** 是否仅 HTTPS 传输；开发(HTTP)须为 false，生产应配 true */
    @Value("${cookie.secure:false}")
    private boolean secure;

    /** SameSite 策略：Lax / Strict / None（None 时必须 secure=true） */
    @Value("${cookie.same-site:Lax}")
    private String sameSite;

    /**
     * 写入 refresh token 到 HttpOnly cookie
     */
    public void write(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path(COOKIE_PATH)
                .maxAge(refreshTokenExpiration / 1000) // 秒
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * 清除 refresh token cookie（登出时调用）
     */
    public void clear(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path(COOKIE_PATH)
                .maxAge(0) // 立即过期
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}
