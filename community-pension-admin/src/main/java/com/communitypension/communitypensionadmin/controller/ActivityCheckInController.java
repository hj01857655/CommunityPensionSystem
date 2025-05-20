package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.utils.Result;
import com.communitypension.communitypensionadmin.pojo.dto.ActivityCheckInDTO;
import com.communitypension.communitypensionadmin.service.ActivityCheckInService;
import com.communitypension.communitypensionadmin.service.ActivityRegisterService;
import com.communitypension.communitypensionadmin.pojo.vo.ActivityCheckInVO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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

    private final ActivityCheckInService activityCheckInService;
    private final ActivityRegisterService activityRegisterService;

    /**
     * 签到
     */
    @PostMapping
    public Result<Void> checkIn(@Valid @RequestBody ActivityCheckInDTO checkInDTO) {
        boolean result = activityCheckInService.checkIn(
                checkInDTO.getRegisterId(),
                checkInDTO.getCheckInUserId(),
                checkInDTO.getIsProxyCheckIn());
        return result ? Result.success("签到成功") : Result.error("签到失败");
    }

    /**
     * 批量签到
     */
    @PostMapping("/batch")
    public Result<Void> batchCheckIn(
            @RequestBody List<Long> registerIds,
            @RequestParam Long checkInUserId,
            @RequestParam(defaultValue = "0") Integer isProxyCheckIn) {
        boolean result = activityCheckInService.batchCheckIn(registerIds, checkInUserId, isProxyCheckIn);
        return result ? Result.success("批量签到成功") : Result.error("批量签到失败");
    }

    /**
     * 签退
     */
    @PostMapping("/signout/{registerId}")
    public Result<Void> signOut(@PathVariable Long registerId) {
        boolean result = activityCheckInService.signOut(registerId);
        return result ? Result.success("签退成功") : Result.error("签退失败");
    }

    /**
     * 获取活动签到统计
     */
    @GetMapping("/stats/{activityId}")
    public Result<Map<String, Object>> getCheckInStats(@PathVariable Long activityId) {
        Map<String, Object> stats = activityCheckInService.getCheckInStats(activityId);
        return Result.success(stats);
    }

    /**
     * 分页查询活动签到记录
     */
    @GetMapping("/list")
    public Result<Page<ActivityCheckInVO>> getCheckInList(
            @RequestParam Long activityId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(activityCheckInService.getCheckInList(activityId, pageNum, pageSize));
    }

    /**
     * 导出活动签到记录
     */
    @GetMapping("/export")
    public void exportCheckInList(@RequestParam Long activityId, HttpServletResponse response) {
        activityCheckInService.exportCheckInList(activityId, response);
    }

    /**
     * 根据报名ID获取签到记录
     */
    @GetMapping("/register/{registerId}")
    public Result<ActivityCheckInVO> getCheckInByRegisterId(@PathVariable Long registerId) {
        return Result.success(activityCheckInService.getCheckInByRegisterId(registerId));
    }

    /**
     * 检查老人是否已签到
     */
    @GetMapping("/check")
    public Result<Boolean> checkElderCheckedIn(
            @RequestParam Long activityId,
            @RequestParam Long elderId) {
        return Result.success(activityCheckInService.checkElderCheckedIn(activityId, elderId));
    }

    /**
     * 获取老人对特定活动的报名ID
     */
    @GetMapping("/register-id")
    public Result<Long> getRegisterIdByActivityAndElder(
            @RequestParam Long activityId,
            @RequestParam Long elderId) {
        return Result.success(activityRegisterService.getRegisterIdByActivityAndElder(activityId, elderId));
    }
}
