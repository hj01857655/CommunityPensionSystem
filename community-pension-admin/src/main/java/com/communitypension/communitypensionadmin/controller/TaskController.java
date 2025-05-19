package com.communitypension.communitypensionadmin.controller;

import com.communitypension.communitypensionadmin.task.ActivityScheduledTask;
import com.communitypension.communitypensionadmin.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 定时任务控制器（仅用于测试）
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final ActivityScheduledTask activityScheduledTask;

    /**
     * 手动触发活动自动结束任务
     */
    @PostMapping("/activity/auto-end")
    public Result<String> triggerAutoEndActivities() {
        activityScheduledTask.autoEndExpiredActivities();
        return Result.success("活动自动结束任务已触发");
    }

    /**
     * 手动触发活动自动开始任务
     */
    @PostMapping("/activity/auto-start")
    public Result<String> triggerAutoStartActivities() {
        activityScheduledTask.autoStartActivities();
        return Result.success("活动自动开始任务已触发");
    }
}
