package com.communitypension.communitypensionadmin.service;

import java.util.Map;

public interface DashboardService {
    /**
     * 获取仪表盘统计卡片数据
     * @return 统计数据Map
     */
    Map<String, Object> getOverviewStatistics();
} 