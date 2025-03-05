package com.communitypension.communitypensionadmin.controller;

import com.communitypension.communitypensionadmin.entity.HealthRecords;
import com.communitypension.communitypensionadmin.service.HealthRecordsService;
import com.communitypension.communitypensionadmin.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health-records/")
@CrossOrigin(origins = "http://localhost:8081")
public class HealthRecordsController {
    // 注入健康档案服务
    @Autowired
    private HealthRecordsService healthRecordsService;
    // 注入JWT工具类
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/getHealthRecords")
    public String getHealthRecords(@RequestHeader("Authorization") String token, @RequestParam("elderId") Long elderId) {
        try{
            JwtUtil.TokenStatus tokenStatus = jwtUtil.validateToken(token);
            if (!tokenStatus.isValid()) {
                // 统一中文错误提示
                String errorMsg = tokenStatus.getError().contains("expired") ?
                        "登录已过期，请重新登录" : "无效的令牌";
                return "{\"code\":401,\"msg\":\"" + errorMsg + "\"}";
            }
            HealthRecords healthRecords = healthRecordsService.getHealthRecordsWithElderInfoByElderId(elderId);
            if(healthRecords == null){
                return "{\"code\":404,\"msg\":\"未找到健康档案\"}";
            }else{
                return "{\"code\":200,\"msg\":\"查询成功\",\"data\":" + healthRecords + "}";
            }

        }catch (Exception e){
            return "{\"code\":401,\"msg\":\"无效的令牌\"}";
        }
    }

}
