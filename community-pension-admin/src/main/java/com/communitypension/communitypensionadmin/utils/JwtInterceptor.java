package com.communitypension.communitypensionadmin.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component

public class JwtInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JwtInterceptor.class);
    
    private final JwtTokenUtil jwtTokenUtil;
    
    public JwtInterceptor(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }
    
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // 放行登录接口
        logger.info("拦截请求: {}", request.getRequestURI());
        String requestURI = request.getRequestURI();
        // 精确匹配登录接口路径
        if (requestURI.equals("/api/auth/login") ||requestURI.equals(("/auth/login")) ||
                requestURI.equals("/api/auth/adminLogin") ||
                requestURI.equals("/auth/adminLogin") ||
                requestURI.equals("/api/auth/refresh") ||
                requestURI.equals("/auth/refresh") ||
                requestURI.startsWith("/static/") ||
                requestURI.startsWith("/public/") ||
                requestURI.startsWith("/upload/") ||
                requestURI.equals("/api/physicalExamReport/upload")) {
            logger.info("放行请求: {}", requestURI);
            return true;  // 放行这些请求
        }
        logger.info("拦截请求: {}", request.getRequestURI());
        // 获取访问令牌
        String accessToken = request.getHeader("Authorization");
        logger.info("访问令牌: {}", accessToken);

        // 判断访问令牌是否存在
        if (accessToken == null || accessToken.isEmpty()) {
            // 尝试从请求中获取刷新令牌
            String refreshToken = request.getHeader("Refresh-Token");
            // 判断刷新令牌是否存在
            if (refreshToken != null && !refreshToken.isEmpty()) {
                return handleRefreshToken(refreshToken, request, response);
            }
            
            logger.warn("访问被拒绝: 未提供访问令牌");
            sendErrorResponse(response, "未授权，请登录");
            return false;
        }
        
        // 清理访问令牌（移除Bearer前缀）
        String clearToken = jwtTokenUtil.cleanToken(accessToken);
        
        // 验证访问令牌
        JwtTokenUtil.TokenStatus tokenStatus = jwtTokenUtil.validateAccessToken(clearToken);
        
        if (!tokenStatus.valid()) {
            logger.warn("访问令牌无效: {}", tokenStatus.error());
            
            // 尝试从请求中获取刷新令牌
            String refreshToken = request.getHeader("Refresh-Token");
            if (refreshToken != null && !refreshToken.isEmpty()) {
                return handleRefreshToken(refreshToken, request, response);
            }
            
            sendErrorResponse(response, "令牌无效或已过期");
            return false;
        }
        
        // 令牌有效，将用户信息存储在请求属性中
        String username = jwtTokenUtil.getUsernameFromToken(accessToken);
        request.setAttribute("username", username);
        
        logger.info("用户 {}  的请求已通过认证", username);
        return true;
    }
    
    /**
     * 处理刷新令牌
     */
    private boolean handleRefreshToken(String refreshToken, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            // 清理刷新令牌（移除Bearer前缀）
            refreshToken = jwtTokenUtil.cleanToken(refreshToken);
            
            // 验证刷新令牌
            JwtTokenUtil.TokenStatus refreshTokenStatus = jwtTokenUtil.validateRefreshToken(refreshToken);
            
            if (!refreshTokenStatus.valid()) {
                logger.warn("刷新令牌无效: {}", refreshTokenStatus.error());
                sendErrorResponse(response, "刷新令牌无效或已过期，请重新登录");
                return false;
            }
            
            // 生成新的访问令牌
            String newAccessToken = jwtTokenUtil.generateNewAccessTokenFromRefreshToken(refreshToken);
            
            // 将新的访问令牌放入响应头
            response.setHeader("New-Access-Token", newAccessToken);
            
            // 从新的访问令牌中获取用户信息
            String username = jwtTokenUtil.getUsernameFromToken(newAccessToken);
            request.setAttribute("username", username);
            
            logger.info("用户 {} 的请求通过刷新令牌认证，已生成新的访问令牌", username);
            return true;
        } catch (Exception e) {
            logger.error("处理刷新令牌时发生错误: {}", e.getMessage(), e);
            sendErrorResponse(response, "刷新令牌处理失败，请重新登录");
            return false;
        }
    }
    
    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, String message) throws Exception {
        response.setStatus(401);
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        
        Result<?> result = Result.error(message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
