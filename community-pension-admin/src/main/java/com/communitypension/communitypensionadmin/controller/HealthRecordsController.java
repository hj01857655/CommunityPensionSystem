package com.communitypension.communitypensionadmin.controller;

import com.communitypension.communitypensionadmin.entity.HealthRecords;
import com.communitypension.communitypensionadmin.service.HealthRecordsService;
import com.communitypension.communitypensionadmin.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health-records/")
@CrossOrigin(origins = "*")
public class HealthRecordsController {
    // 注入健康档案服务
    @Autowired
    private HealthRecordsService healthRecordsService;

    @GetMapping("/getHealthRecords")
    public Result<HealthRecords> getHealthRecords(@RequestParam("elderId") Long elderId) {
        try{
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
    public Result<HealthRecords> addHealthRecords(@RequestBody HealthRecords healthRecords) {
        try {
            boolean success = healthRecordsService.save(healthRecords);
            if (success) {
                return Result.success("添加成功", healthRecords);
            } else {
                return Result.error(500, "添加失败");
            }
        } catch (Exception e) {
            return Result.error(500, "添加健康档案失败：" + e.getMessage());
        }
    }

    @PutMapping("/updateHealthRecords")
    public Result<HealthRecords> updateHealthRecords(@RequestBody HealthRecords healthRecords) {
        try {
            boolean success = healthRecordsService.updateById(healthRecords);
            if (success) {
                return Result.success("更新成功", healthRecords);
            } else {
                return Result.error(500, "更新失败");
            }
        } catch (Exception e) {
            return Result.error(500, "更新健康档案失败：" + e.getMessage());
        }
    }
}
