package com.communitypension.communitypensionadmin.service.impl;

import com.communitypension.communitypensionadmin.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final UserService userService;
    private final ActivityService activityService;
    private final ServiceOrderService serviceOrderService;
    private final WarningRecordService warningRecordService;

    @Override
    public Map<String, Object> getOverviewStatistics() {
        Map<String, Object> stats = new HashMap<>();
        // 用户总数
        long userTotal = userService.count();
        // 本月新增用户
        YearMonth now = YearMonth.now();
        LocalDateTime monthStart = now.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = now.atEndOfMonth().atTime(23, 59, 59);
        long userMonthlyIncrease = userService.countByCreateTimeBetween(monthStart, monthEnd);
        // 活动总数
        long activityTotal = activityService.count();
        // 进行中活动数
        long activityOngoing = activityService.getOngoingActivityCount();
        // 服务单总数
        long serviceOrderTotal = serviceOrderService.count();
        // 异常/预警数
        long warningTotal = warningRecordService.count();

        stats.put("userTotal", userTotal);
        stats.put("userMonthlyIncrease", userMonthlyIncrease);
        stats.put("activityTotal", activityTotal);
        stats.put("activityOngoing", activityOngoing);
        stats.put("serviceOrderTotal", serviceOrderTotal);
        stats.put("warningTotal", warningTotal);
        return stats;
    }
} 