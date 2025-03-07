package com.communitypension.communitypensionadmin.controller;

import com.communitypension.communitypensionadmin.entity.HealthRecords;
import com.communitypension.communitypensionadmin.service.HealthRecordsService;
import com.communitypension.communitypensionadmin.util.Result;
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
    public Result<HealthRecords> getHealthRecords(@RequestHeader("Authorization") String token, @RequestParam("elderId") Long elderId) {
        try{
            JwtUtil.TokenStatus tokenStatus = jwtUtil.validateToken(token);
            if (!tokenStatus.isValid()) {
                // 统一中文错误提示
                String errorMsg = tokenStatus.getError().contains("expired") ?
                        "登录已过期，请重新登录" : "无效的令牌";
                return Result.error(401, errorMsg);
            }
            HealthRecords healthRecords = healthRecordsService.getHealthRecordsWithElderInfoByElderId(elderId);
            if(healthRecords == null){
                return Result.error(404, "健康档案不存在");

            }else{
                return Result.success("查询成功", healthRecords);

            }

        }catch (Exception e){
            return Result.error(401, "无效的令牌");
        }
    }

    @PostMapping("/addHealthRecords")
    public Result<HealthRecords> addHealthRecords(@RequestHeader("Authorization") String token, @RequestBody HealthRecords healthRecords) {
        try{
            JwtUtil.TokenStatus tokenStatus = jwtUtil.validateToken(token);
            if (!tokenStatus.isValid()) {
                // 统一中文错误提示
                String errorMsg = tokenStatus.getError().contains("expired") ?
                        "登录已过期，请重新登录" : "无效的令牌";
                return Result.error(401, errorMsg);
            }
            HealthRecords healthRecords1 = healthRecordsService.getById(healthRecords.getId());
            if(healthRecords1 != null){
                return Result.success("添加成功", healthRecords);
            }else{
                return Result.error(500, "添加失败");
            }

        }catch (Exception e){
            return Result.error(401, "无效的令牌");
        }
    }


}
