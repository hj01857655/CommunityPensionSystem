package com.communitypension.communitypensionadmin.config;

import com.communitypension.communitypensionadmin.service.ServiceOrderService;
import com.communitypension.communitypensionadmin.service.impl.ServiceOrderServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 服务配置类
 * 用于解决服务Bean注册问题
 */
@Configuration
public class ServiceConfig {

    /**
     * 确保ServiceOrderService被正确注册为Spring Bean
     */
    @Bean
    @Primary
    public ServiceOrderService serviceOrderService(
            ServiceOrderServiceImpl serviceOrderServiceImpl) {
        return serviceOrderServiceImpl;
    }
}
