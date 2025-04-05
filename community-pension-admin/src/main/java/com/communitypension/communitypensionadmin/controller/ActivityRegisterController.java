package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.common.Result;
import com.communitypension.communitypensionadmin.dto.AuditDTO;
import com.communitypension.communitypensionadmin.dto.BatchAuditDTO;
import com.communitypension.communitypensionadmin.service.ActivityParticipateService;
import com.communitypension.communitypensionadmin.vo.ActivityParticipateVO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 活动报名管理Controller
 */
@RestController
@RequestMapping("/api/activity/register")
@RequiredArgsConstructor
public class ActivityRegisterController {
    
    private final ActivityParticipateService activityParticipateService;
    
    /**
     * 分页查询活动报名列表
     */
    @GetMapping("/list")
    public Result<Page<ActivityParticipateVO>> getParticipateList(
            @RequestParam(required = false) Long activityId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(activityParticipateService.getActivityParticipateList(activityId, pageNum, pageSize));
    }
    
    /**
     * 获取用户报名列表
     */
    @GetMapping("/user/list")
    public Result<Page<ActivityParticipateVO>> getUserParticipateList(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(activityParticipateService.getUserParticipateList(userId, pageNum, pageSize));
    }
    
    /**
     * 报名活动
     */
    @PostMapping("/{activityId}")
    public Result<Void> registerActivity(
            @PathVariable Long activityId,
            @RequestParam Long userId) {
        boolean result = activityParticipateService.registerActivity(activityId, userId);
        return result ? Result.success("报名成功") : Result.error("报名失败");
    }
    
    /**
     * 取消报名
     */
    @PostMapping("/cancel/{id}")
    public Result<Void> cancelParticipate(
            @PathVariable Long id,
            @RequestParam Long userId) {
        boolean result = activityParticipateService.cancelParticipate(id, userId);
        return result ? Result.success("取消成功") : Result.error("取消失败");
    }
    
    /**
     * 审核报名
     */
    @PutMapping("/{id}/audit")
    public Result<Void> auditParticipate(@PathVariable Long id, @Valid @RequestBody AuditDTO auditDTO) {
        boolean result = activityParticipateService.auditParticipate(id, auditDTO.getStatus(), auditDTO.getRemark());
        return result ? Result.success("审核成功") : Result.error("审核失败");
    }
    
    /**
     * 批量审核报名
     */
    @PutMapping("/batch-audit")
    public Result<Void> batchAuditParticipate(@Valid @RequestBody BatchAuditDTO batchAuditDTO) {
        boolean result = activityParticipateService.batchAuditParticipate(
                batchAuditDTO.getIds(), 
                batchAuditDTO.getStatus(), 
                batchAuditDTO.getRemark()
        );
        return result ? Result.success("批量审核成功") : Result.error("批量审核失败");
    }
    
    /**
     * 获取用户活动报名状态
     */
    @GetMapping("/status/{activityId}")
    public Result<Map<String, Object>> getUserActivityStatus(
            @PathVariable Long activityId,
            @RequestParam Long userId) {
        Integer status = activityParticipateService.getUserActivityStatus(activityId, userId);
        return Result.success(Map.of("status", status));
    }
    
    /**
     * 获取活动报名统计
     */
    @GetMapping("/stats/{activityId}")
    public Result<Map<String, Object>> getParticipateStats(@PathVariable Long activityId) {
        Map<String, Object> stats = activityParticipateService.getParticipateStats(activityId);
        return Result.success(stats);
    }
    
    /**
     * 导出活动报名列表
     */
    @GetMapping("/export")
    public void exportParticipateList(@RequestParam Long activityId, HttpServletResponse response) {
        activityParticipateService.exportParticipateList(activityId, response);
    }
}
