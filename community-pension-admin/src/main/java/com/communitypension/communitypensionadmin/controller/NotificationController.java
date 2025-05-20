package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.communitypension.communitypensionadmin.entity.Notification;
import com.communitypension.communitypensionadmin.pojo.query.NotificationQuery;
import com.communitypension.communitypensionadmin.service.NotificationService;
import com.communitypension.communitypensionadmin.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notice")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;

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
     * 修改通知公告（POST方法）
     */
    @PostMapping("/update")
    public Result<Void> editByPost(@RequestBody Notification notification) {
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
