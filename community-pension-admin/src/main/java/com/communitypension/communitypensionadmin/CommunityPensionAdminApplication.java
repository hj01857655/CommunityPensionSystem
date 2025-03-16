package com.communitypension.communitypensionadmin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.communitypension.communitypensionadmin.mapper")
@ComponentScan(basePackages = {"com.communitypension.communitypensionadmin.entity", "com.communitypension.communitypensionadmin.controller", "com.communitypension.communitypensionadmin.service", "com.communitypension.communitypensionadmin.config", "com.communitypension.communitypensionadmin.utils"})
public class CommunityPensionAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommunityPensionAdminApplication.class, args);
    }
}