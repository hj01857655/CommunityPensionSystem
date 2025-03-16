package com.communitypension.communitypensionadmin.config;

import com.communitypension.communitypensionadmin.utils.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    // 注入JwtInterceptor
    private final JwtInterceptor jwtInterceptor;
    // 构造函数注入JwtInterceptor
    public WebMvcConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }
    // 注册拦截器
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        // 注册拦截器
        registry.addInterceptor(jwtInterceptor)
                .excludePathPatterns(
                        "/api/**",
                        "/api/auth/refresh",  // 排除刷新令牌接口
                        "/api/users/adminLogin",  // 排除后台登录接口
                        "/api/users/userLogin" // 排除前台登录接口
                );
    }
}