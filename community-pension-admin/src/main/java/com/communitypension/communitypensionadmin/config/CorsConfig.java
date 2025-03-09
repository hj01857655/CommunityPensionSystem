package com.communitypension.communitypensionadmin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 允许的源
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:8081")); // 允许来自 http://localhost:8081 的请求

        // 允许的 HTTP 方法
        configuration.setAllowedMethods(Collections.singletonList("*")); // 允许所有 HTTP 方法

        // 允许的请求头
        configuration.setAllowedHeaders(Collections.singletonList("*")); // 允许所有请求头

        // 允许发送凭证（如 Cookies）
        configuration.setAllowCredentials(true);

        // 注册 CORS 配置
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 允许所有路径的 CORS 请求

        return source;
    }
}
