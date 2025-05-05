package com.communitypension.communitypensionadmin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@MapperScan("com.communitypension.communitypensionadmin.mapper")
@EntityScan("com.communitypension.communitypensionadmin.entity")
public class CommunityPensionAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommunityPensionAdminApplication.class, args);
    }
}
