package com.communitypension.communitypensionadmin.interceptor;

import com.communitypension.communitypensionadmin.util.Result;
import com.communitypension.communitypensionadmin.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 排除登录接口和Swagger文档
        String uri = request.getRequestURI();
        if (uri.contains("/adminLogin") || uri.contains("/userLogin")
            || uri.contains("/v3/api-docs") || uri.contains("/swagger-ui")) {
            return true;
        }

        try {
            String token = request.getHeader("Authorization");
            JwtUtil.TokenStatus status = jwtUtil.validateToken(token);

            if (!status.isValid()) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(401);
                response.getWriter().write(new ObjectMapper().writeValueAsString(
                    Result.error(401, status.getError())
                ));
                return false;
            }

            // 将用户信息存入请求属性
            request.setAttribute("username", jwtUtil.getUsernameFromToken(token));
            request.setAttribute("roleId", jwtUtil.getRoleIdFromToken(token));
            request.setAttribute("expiration", jwtUtil.getExpirationDateFromToken(token));
            return true;

        } catch (Exception e) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(500);
            response.getWriter().write(new ObjectMapper().writeValueAsString(
                Result.error(500, "令牌解析异常")
            ));
            return false;
        }
    }
}
