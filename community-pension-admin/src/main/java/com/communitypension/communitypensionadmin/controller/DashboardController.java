package com.communitypension.communitypensionadmin.controller;

import com.communitypension.communitypensionadmin.service.*;
import com.communitypension.communitypensionadmin.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 后台仪表盘数据Controller
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "后台仪表盘管理", description = "后台仪表盘相关接口")
public class DashboardController {

    private final UserService userService;
    private final ActivityService activityService;
    private final ServiceOrderService serviceOrderService;
    private final DictDataService dictDataService;
    private final DashboardService dashboardService;
    private final NotificationService notificationService;
    private final WarningRecordService warningRecordService;

    /**
     * 获取首页统计数据
     */
    @GetMapping("/stats")
    @Operation(summary = "获取首页统计数据", description = "获取首页统计数据")
    public Result<List<Map<String, Object>>> getStatistics() {
        List<Map<String, Object>> statistics = new ArrayList<>();
        
        try {
            // 总用户数
            Long totalUserCount = userService.count();
            Long lastWeekUserCount = userService.getCountBeforeTime(LocalDateTime.now().minusWeeks(1));
            double userGrowthRate = 0;
            if (lastWeekUserCount > 0) {
                userGrowthRate = (totalUserCount - lastWeekUserCount) * 100.0 / lastWeekUserCount;
            }
            
            Map<String, Object> userStat = new HashMap<>();
            userStat.put("title", "总用户数");
            userStat.put("value", totalUserCount);
            userStat.put("trend", userGrowthRate);
            userStat.put("icon", "User");
            statistics.add(userStat);
            
            // 活跃用户 - 最近7天有登录记录的用户数
            Long activeUserCount = userService.getActiveUserCount(7);
            Long lastWeekActiveUserCount = userService.getActiveUserCount(14, 7);
            double activeGrowthRate = 0;
            if (lastWeekActiveUserCount > 0) {
                activeGrowthRate = (activeUserCount - lastWeekActiveUserCount) * 100.0 / lastWeekActiveUserCount;
            }
            
            Map<String, Object> activeStat = new HashMap<>();
            activeStat.put("title", "活跃用户");
            activeStat.put("value", activeUserCount);
            activeStat.put("trend", activeGrowthRate);
            activeStat.put("icon", "Timer");
            statistics.add(activeStat);
            
            // 总活动数
            Long totalActivityCount = activityService.count();
            Long lastWeekActivityCount = activityService.getCountBeforeTime(LocalDateTime.now().minusWeeks(1));
            double activityGrowthRate = 0;
            if (lastWeekActivityCount > 0) {
                activityGrowthRate = (totalActivityCount - lastWeekActivityCount) * 100.0 / lastWeekActivityCount;
            }
            
            Map<String, Object> activityStat = new HashMap<>();
            activityStat.put("title", "总活动数");
            activityStat.put("value", totalActivityCount);
            activityStat.put("trend", activityGrowthRate);
            activityStat.put("icon", "List");
            statistics.add(activityStat);
            
            // 进行中活动
            Long ongoingActivityCount = activityService.getOngoingActivityCount();
            Long lastWeekOngoingCount = activityService.getOngoingActivityCountByTime(LocalDateTime.now().minusWeeks(1));
            double ongoingGrowthRate = 0;
            if (lastWeekOngoingCount > 0) {
                ongoingGrowthRate = (ongoingActivityCount - lastWeekOngoingCount) * 100.0 / lastWeekOngoingCount;
            }
            
            Map<String, Object> ongoingStat = new HashMap<>();
            ongoingStat.put("title", "进行中活动");
            ongoingStat.put("value", ongoingActivityCount);
            ongoingStat.put("trend", ongoingGrowthRate);
            ongoingStat.put("icon", "Bell");
            statistics.add(ongoingStat);
            
            return Result.success(statistics);
            
        } catch (Exception e) {
            return Result.error("获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户趋势数据
     */
    @GetMapping("/user-trend")
    @Operation(summary = "获取用户趋势数据", description = "获取用户趋势数据")
    public Result<Map<String, Object>> getUserTrend(@RequestParam(defaultValue = "week") String type) {
        try {
            int days = "week".equals(type) ? 7 : 30;
            List<String> dateList = new ArrayList<>();
            List<Long> newUsersList = new ArrayList<>();
            List<Long> activeUsersList = new ArrayList<>();
            
            LocalDate endDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
            
            for (int i = days - 1; i >= 0; i--) {
                LocalDate date = endDate.minusDays(i);
                dateList.add(date.format(formatter));
                
                // 获取当天新增用户数
                Long newUsers = userService.getNewUserCountByDay(date);
                newUsersList.add(newUsers);
                
                // 获取当天活跃用户数
                Long activeUsers = userService.getActiveUserCountByDay(date);
                activeUsersList.add(activeUsers);
            }
            
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("dates", dateList);
            resultMap.put("newUsers", newUsersList);
            resultMap.put("activeUsers", activeUsersList);
            
            return Result.success(resultMap);
            
        } catch (Exception e) {
            return Result.error("获取用户趋势数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取活动类型分布数据
     */
    @GetMapping("/activity-types")
    @Operation(summary = "获取活动类型分布数据", description = "获取活动类型分布数据")
    public Result<List<Map<String, Object>>> getActivityTypes() {
        try {
            // 从活动表中获取各类型活动数量
            Map<String, Long> activityTypeCounts = activityService.getActivityCountByType();
            
            List<Map<String, Object>> resultList = activityTypeCounts.entrySet().stream()
                    .map(entry -> {
                        Map<String, Object> item = new HashMap<>();
                        item.put("name", entry.getKey());
                        item.put("value", entry.getValue());
                        return item;
                    })
                    .collect(Collectors.toList());
            
            return Result.success(resultList);
            
        } catch (Exception e) {
            return Result.error("获取活动类型分布数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取最新活动列表
     */
    @GetMapping("/recent-activities")
    @Operation(summary = "获取最新活动列表", description = "获取最新活动列表")
    public Result<List<Map<String, Object>>> getRecentActivities(@RequestParam(defaultValue = "3") Integer limit) {
        try {
            List<Map<String, Object>> activities = activityService.getRecentActivities(limit);
            return Result.success(activities);
        } catch (Exception e) {
            return Result.error("获取最新活动列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有仪表盘数据（一次性返回所有数据）
     */
    @GetMapping("/all")
    @Operation(summary = "获取所有仪表盘数据", description = "一次性返回所有仪表盘数据")
    public Result<Map<String, Object>> getAllDashboardData() {
        try {
            Map<String, Object> resultMap = new HashMap<>();
            
            // 获取统计数据
            Result<List<Map<String, Object>>> statsResult = this.getStatistics();
            if (statsResult.getCode() == 200) {
                resultMap.put("stats", statsResult.getData());
            }
            
            // 获取用户趋势
            Result<Map<String, Object>> trendResult = this.getUserTrend("week");
            if (trendResult.getCode() == 200) {
                resultMap.put("userTrend", trendResult.getData());
            }
            
            // 获取活动类型分布
            Result<List<Map<String, Object>>> typesResult = this.getActivityTypes();
            if (typesResult.getCode() == 200) {
                resultMap.put("activityTypes", typesResult.getData());
            }
            
            // 获取最新活动
            Result<List<Map<String, Object>>> activitiesResult = this.getRecentActivities(3);
            if (activitiesResult.getCode() == 200) {
                resultMap.put("recentActivities", activitiesResult.getData());
            }
            
            return Result.success(resultMap);
            
        } catch (Exception e) {
            return Result.error("获取仪表盘数据失败: " + e.getMessage());
        }
    }

    /**
     * 仪表盘统计卡片数据
     */
    @GetMapping("/overview")
    public Map<String, Object> getOverviewStatistics() {
        return dashboardService.getOverviewStatistics();
    }

    /**
     * 获取最新通知公告列表
     */
    @GetMapping("/recent-notifications")
    @Operation(summary = "获取最新通知公告", description = "获取最新通知公告列表")
    public Result<List<Map<String, Object>>> getRecentNotifications(@RequestParam(defaultValue = "5") Integer limit) {
        try {
            // 假设有NotificationService，返回List<Map<String, Object>>
            List<Map<String, Object>> notifications = notificationService.getRecentNotifications(limit);
            return Result.success(notifications);
        } catch (Exception e) {
            return Result.error("获取最新通知公告失败: " + e.getMessage());
        }
    }

    /**
     * 获取最新健康预警列表
     */
    @GetMapping("/warnings")
    @Operation(summary = "获取最新健康预警", description = "获取最新健康预警列表")
    public Result<List<Map<String, Object>>> getWarnings(@RequestParam(defaultValue = "5") Integer limit) {
        try {
            // 假设有WarningRecordService，返回List<Map<String, Object>>
            List<Map<String, Object>> warnings = warningRecordService.getRecentWarnings(limit);
            return Result.success(warnings);
        } catch (Exception e) {
            return Result.error("获取最新健康预警失败: " + e.getMessage());
        }
    }
} 