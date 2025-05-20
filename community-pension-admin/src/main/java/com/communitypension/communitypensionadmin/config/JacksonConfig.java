package com.communitypension.communitypensionadmin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson配置类
 * 用于处理Java日期时间类型的序列化
 */
@Configuration
public class JacksonConfig {

    /**
     * 默认日期时间格式
     */
    private static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 配置ObjectMapper以支持Java日期时间类型
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 创建JavaTimeModule模块
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        
        // 配置LocalDateTime序列化器
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_PATTERN);
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        
        // 注册模块
        objectMapper.registerModule(javaTimeModule);
        
        return objectMapper;
    }
}
