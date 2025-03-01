package com.communitypension.communitypensionadmin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@MapperScan("com.communitypension.communitypensionadmin.mapper")

@SpringBootApplication
@CrossOrigin(origins = "http://localhost:8081")
public class CommunityPensionAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityPensionAdminApplication.class, args);
    }

}
