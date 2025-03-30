package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.entity.Notification;
import com.communitypension.communitypensionadmin.vo.query.NotificationQuery;

public interface NotificationService extends IService<Notification> {
    
    /**
     * 获取通知公告列表
     */
    IPage<Notification> getNotificationList(NotificationQuery query);
    
    /**
     * 获取通知公告详情
     */
    Notification getNotificationById(Long id);
    
    /**
     * 保存通知公告
     */
    void saveNotification(Notification notification);
    
    /**
     * 更新通知公告
     */
    void updateNotification(Notification notification);
    
    /**
     * 删除通知公告
     */
    void deleteNotification(Long id);
    
    /**
     * 发布通知公告
     */
    void publishNotification(Long id);
    
    /**
     * 撤回通知公告
     */
    void revokeNotification(Long id);
} 