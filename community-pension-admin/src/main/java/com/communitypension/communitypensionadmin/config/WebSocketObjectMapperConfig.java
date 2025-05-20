package com.communitypension.communitypensionadmin.config;

import com.communitypension.communitypensionadmin.websocket.WebSocketServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import jakarta.annotation.PostConstruct;

/**
 * WebSocket ObjectMapper配置类
 * 用于将配置好的ObjectMapper注入到WebSocketServer中
 */
@Configuration
@DependsOn("jacksonConfig")
public class WebSocketObjectMapperConfig {

    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 初始化WebSocketServer的ObjectMapper
     */
    @PostConstruct
    public void init() {
        WebSocketServer.setObjectMapper(objectMapper);
    }
}
