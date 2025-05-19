package com.communitypension.communitypensionadmin.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.communitypension.communitypensionadmin.entity.Activity;
import com.communitypension.communitypensionadmin.service.ActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 活动相关定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityScheduledTask {

    private final ActivityService activityService;

    /**
     * 自动结束已过期的活动
     * 每5分钟执行一次
     */
    @Scheduled(fixedRate = 300000) // 5分钟 = 300,000毫秒
    @Transactional(rollbackFor = Exception.class)
    public void autoEndExpiredActivities() {
        log.info("开始执行活动自动结束任务...");
        LocalDateTime now = LocalDateTime.now();
        
        try {
            // 查询所有进行中且结束时间已过的活动
            LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Activity::getStatus, 2) // 状态为进行中(2)
                   .lt(Activity::getEndTime, now); // 结束时间小于当前时间
            
            List<Activity> expiredActivities = activityService.list(wrapper);
            
            if (expiredActivities.isEmpty()) {
                log.info("没有需要自动结束的活动");
                return;
            }
            
            log.info("找到 {} 个需要自动结束的活动", expiredActivities.size());
            
            // 更新活动状态为已结束(3)
            for (Activity activity : expiredActivities) {
                activity.setStatus(3); // 更新为已结束状态(3)
                activityService.updateById(activity);
                log.info("自动结束活动: ID={}, 标题={}", activity.getId(), activity.getTitle());
            }
            
            log.info("活动自动结束任务执行完成，共处理 {} 个活动", expiredActivities.size());
        } catch (Exception e) {
            log.error("活动自动结束任务执行失败", e);
        }
    }
    
    /**
     * 自动开始已到开始时间的活动
     * 每5分钟执行一次
     */
    @Scheduled(fixedRate = 300000) // 5分钟 = 300,000毫秒
    @Transactional(rollbackFor = Exception.class)
    public void autoStartActivities() {
        log.info("开始执行活动自动开始任务...");
        LocalDateTime now = LocalDateTime.now();
        
        try {
            // 查询所有报名中且开始时间已到的活动
            LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Activity::getStatus, 1) // 状态为报名中(1)
                   .lt(Activity::getStartTime, now); // 开始时间小于当前时间
            
            List<Activity> activitiesToStart = activityService.list(wrapper);
            
            if (activitiesToStart.isEmpty()) {
                log.info("没有需要自动开始的活动");
                return;
            }
            
            log.info("找到 {} 个需要自动开始的活动", activitiesToStart.size());
            
            // 更新活动状态为进行中(2)
            for (Activity activity : activitiesToStart) {
                activity.setStatus(2); // 更新为进行中状态(2)
                activityService.updateById(activity);
                log.info("自动开始活动: ID={}, 标题={}", activity.getId(), activity.getTitle());
            }
            
            log.info("活动自动开始任务执行完成，共处理 {} 个活动", activitiesToStart.size());
        } catch (Exception e) {
            log.error("活动自动开始任务执行失败", e);
        }
    }
}
