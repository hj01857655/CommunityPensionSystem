package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.communitypension.communitypensionadmin.entity.Notification;
import com.communitypension.communitypensionadmin.query.NotificationQuery;
import com.communitypension.communitypensionadmin.service.NotificationService;
import com.communitypension.communitypensionadmin.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam Long userId) {
        SseEmitter emitter = new SseEmitter(300000L); // 5分钟超时，与configureAsyncSupport中设置一致
        notificationService.addEmitter(userId, emitter);

        // 设置超时和完成时的回调，移除emitter
        emitter.onTimeout(() -> {
            System.out.println("SSE connection timeout for user: " + userId);
            notificationService.removeEmitter(userId, emitter);
        });
        emitter.onCompletion(() -> {
            System.out.println("SSE connection completed for user: " + userId);
            notificationService.removeEmitter(userId, emitter);
        });

        // 发送初始事件，确保连接建立
        try {
            emitter.send(SseEmitter.event().name("CONNECTED").data("连接成功"));
            System.out.println("SSE connection established for user: " + userId);
        } catch (IOException e) {
            System.err.println("Failed to send initial SSE event for user: " + userId + ". Error: " + e.getMessage());
            emitter.complete();
        }

        return emitter;
    }
    
    /**
     * 获取通知公告列表
     */
    @GetMapping("/list")
    public Result<IPage<Notification>> list(NotificationQuery query) {
        return Result.success("查询成功", notificationService.getNotificationList(query));
    }
    
    /**
     * 获取通知公告详情
     */
    @GetMapping("/{id}")
    public Result<Notification> getInfo(@PathVariable Long id) {
        return Result.success("查询成功", notificationService.getNotificationById(id));
    }
    
    /**
     * 新增通知公告
     */
    @PostMapping
    public Result<Void> add(@RequestBody Notification notification) {
        notificationService.saveNotification(notification);
        return Result.created(null);
    }
    
    /**
     * 修改通知公告
     */
    @PutMapping
    public Result<Void> edit(@RequestBody Notification notification) {
        notificationService.updateNotification(notification);
        return Result.success("修改成功");
    }
    
    /**
     * 删除通知公告
     */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return Result.deleted();
    }
    
    /**
     * 发布通知公告
     */
    @PutMapping("/publish/{id}")
    public Result<Void> publish(@PathVariable Long id) {
        notificationService.publishNotification(id);
        return Result.success("发布成功");
    }
    
    /**
     * 撤回通知公告
     */
    @PutMapping("/revoke/{id}")
    public Result<Void> revoke(@PathVariable Long id) {
        notificationService.revokeNotification(id);
        return Result.success("撤回成功");
    }
} 
