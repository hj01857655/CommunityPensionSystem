package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.common.Result;
import com.communitypension.communitypensionadmin.service.ActivityParticipateService;
import com.communitypension.communitypensionadmin.vo.ActivityParticipateVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 活动签到管理Controller
 */
@RestController
@RequestMapping("/api/activity/check-in")
@RequiredArgsConstructor
public class ActivityCheckInController {
    
    private final ActivityParticipateService activityParticipateService;
    
    /**
     * 单个签到
     */
    @PutMapping("/{id}")
    public Result<Void> checkIn(@PathVariable Long id) {
        boolean result = activityParticipateService.checkIn(id);
        return result ? Result.success("签到成功") : Result.error("签到失败");
    }
    
    /**
     * 批量签到
     */
    @PutMapping("/batch")
    public Result<Void> batchCheckIn(@RequestBody List<Long> ids) {
        boolean result = activityParticipateService.batchCheckIn(ids);
        return result ? Result.success("批量签到成功") : Result.error("批量签到失败");
    }
    
    /**
     * 获取活动签到统计
     */
    @GetMapping("/stats/{activityId}")
    public Result<Map<String, Object>> getCheckInStats(@PathVariable Long activityId) {
        Map<String, Object> stats = activityParticipateService.getCheckInStats(activityId);
        return Result.success(stats);
    }
    
    /**
     * 分页查询活动签到记录
     */
    @GetMapping("/list")
    public Result<Page<ActivityParticipateVO>> getCheckInList(
            @RequestParam Long activityId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(activityParticipateService.getCheckInList(activityId, pageNum, pageSize));
    }
    
    /**
     * 导出活动签到记录
     */
    @GetMapping("/export")
    public void exportCheckInList(@RequestParam Long activityId, HttpServletResponse response) {
        activityParticipateService.exportCheckInList(activityId, response);
    }
}
