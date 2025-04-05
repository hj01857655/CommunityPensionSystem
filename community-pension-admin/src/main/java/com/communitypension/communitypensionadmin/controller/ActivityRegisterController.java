package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.utils.Result;
import com.communitypension.communitypensionadmin.dto.AuditDTO;
import com.communitypension.communitypensionadmin.dto.BatchAuditDTO;
import com.communitypension.communitypensionadmin.service.ActivityRegisterService;
import com.communitypension.communitypensionadmin.vo.ActivityRegisterVO;
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

    private final ActivityRegisterService activityRegisterService;

    /**
     * 分页查询活动报名列表
     */
    @GetMapping("/list")
    public Result<Page<ActivityRegisterVO>> getParticipateList(
            @RequestParam(required = false) Long activityId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(activityRegisterService.getActivityRegisterList(activityId, pageNum, pageSize));
    }

    /**
     * 获取老人报名列表
     */
    @GetMapping("/user/list")
    public Result<Page<ActivityRegisterVO>> getUserRegisterList(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(activityRegisterService.getElderRegisterList(userId, pageNum, pageSize));
    }

    /**
     * 报名活动
     */
    @PostMapping("/{activityId}")
    public Result<Void> registerActivity(
            @PathVariable Long activityId,
            @RequestParam Long elderId,
            @RequestParam(required = false) Long registerUserId,
            @RequestParam(defaultValue = "0") Integer registerType) {
        // 如果没有提供报名人ID，则使用老人ID
        if (registerUserId == null) {
            registerUserId = elderId;
        }
        boolean result = activityRegisterService.registerActivity(activityId, elderId, registerUserId, registerType);
        return result ? Result.success("报名成功") : Result.error("报名失败");
    }

    /**
     * 取消报名
     */
    @PostMapping("/cancel/{id}")
    public Result<Void> cancelRegister(
            @PathVariable Long id,
            @RequestParam Long elderId) {
        boolean result = activityRegisterService.cancelRegister(id, elderId);
        return result ? Result.success("取消成功") : Result.error("取消失败");
    }

    /**
     * 审核报名
     */
    @PutMapping("/{id}/audit")
    public Result<Void> auditRegister(@PathVariable Long id, @Valid @RequestBody AuditDTO auditDTO) {
        boolean result = activityRegisterService.auditRegister(id, auditDTO.getStatus(), auditDTO.getRemark());
        return result ? Result.success("审核成功") : Result.error("审核失败");
    }

    /**
     * 批量审核报名
     */
    @PutMapping("/batch-audit")
    public Result<Void> batchAudioRegister(@Valid @RequestBody BatchAuditDTO batchAuditDTO) {
        boolean result = activityRegisterService.batchAuditRegister(
                batchAuditDTO.getIds(),
                batchAuditDTO.getStatus(),
                batchAuditDTO.getRemark()
        );
        return result ? Result.success("批量审核成功") : Result.error("批量审核失败");
    }

    /**
     * 获取老人活动报名状态
     */
    @GetMapping("/status/{activityId}")
    public Result<Map<String, Object>> getElderActivityStatus(
            @PathVariable Long activityId,
            @RequestParam Long elderId) {
        Integer status = activityRegisterService.getElderActivityStatus(activityId, elderId);
        return Result.success(Map.of("status", status));
    }

    /**
     * 获取活动报名统计
     */
    @GetMapping("/stats/{activityId}")
    public Result<Map<String, Object>> getRegisterStats(@PathVariable Long activityId) {
        Map<String, Object> stats = activityRegisterService.getRegisterStats(activityId);
        return Result.success(stats);
    }

    /**
     * 导出活动报名列表
     */
    @GetMapping("/export")
    public void exportRegisterList(@RequestParam Long activityId, HttpServletResponse response) {
        activityRegisterService.exportRegisterList(activityId, response);
    }
}
