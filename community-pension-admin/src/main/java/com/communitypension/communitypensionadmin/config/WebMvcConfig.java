package com.communitypension.communitypensionadmin.config;

import com.communitypension.communitypensionadmin.utils.JwtInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * WebMvc配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    // 注入JwtInterceptor
    private final JwtInterceptor jwtInterceptor;
    
    @Value("${file.upload.path}")
    private String uploadPath;
    
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
                    "/api/auth/**",
                    "/error",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/static/**",
                    "/public/**",
                    "/upload/**",        // 不拦截上传文件访问路径
                     "/actuator/**"
                );
    }
    // 配置静态资源映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将文件上传路径映射到/upload/**，方便前端访问
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
    // 启用异步支持
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(300000); // 设置默认超时时间为5分钟
    }
}