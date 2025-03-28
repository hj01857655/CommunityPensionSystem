package com.communitypension.communitypensionadmin.config;

import com.communitypension.communitypensionadmin.utils.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * WebMvc配置
 */
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
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")  // 拦截所有请求
                .excludePathPatterns(    // 不拦截的路径
                    "/api/auth/login",
                    "/api/auth/adminLogin",
                    "/api/auth/refresh",
                    "/error",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/static/**",
                    "/public/**"
                );
    }
}