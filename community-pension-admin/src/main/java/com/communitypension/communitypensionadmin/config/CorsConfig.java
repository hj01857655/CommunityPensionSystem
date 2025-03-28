package com.communitypension.communitypensionadmin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
/**
 * 跨域配置
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",
            "http://127.0.0.1:5173"

        ));
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "Accept", "X-Requested-With", 
            "Cache-Control", "Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Headers"
        ));
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "Access-Control-Allow-Origin", "Access-Control-Allow-Headers",
            "Refresh-Token", "New-Access-Token"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        // 为登录和刷新令牌接口使用相同的CORS配置，确保能正确处理跨域请求
        source.registerCorsConfiguration("/api/auth/login", configuration);
        source.registerCorsConfiguration("/auth/login", configuration);
        source.registerCorsConfiguration("/api/auth/adminLogin", configuration);
        source.registerCorsConfiguration("/auth/adminLogin", configuration);
        source.registerCorsConfiguration("/api/auth/refresh", configuration);
        source.registerCorsConfiguration("/auth/refresh", configuration);
        return source;
    }
}
