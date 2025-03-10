package com.communitypension.communitypensionadmin.config;

import com.communitypension.communitypensionadmin.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
            .addPathPatterns("/api/users/**")
            .excludePathPatterns(
                "/api/users/adminLogin",
                "/api/users/userLogin",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-resources/**",
                "/webjars/**",
                    "/doc.html"
            );
    }
}
