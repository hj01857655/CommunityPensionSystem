package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.entity.Notification;
import com.communitypension.communitypensionadmin.query.NotificationQuery;
import com.communitypension.communitypensionadmin.entity.ServiceOrder;
import com.communitypension.communitypensionadmin.entity.User;

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
    
    /**
     * 发送系统消息
     *
     * @param userId 用户ID
     * @param title 消息标题
     * @param content 消息内容
     * @param type 消息类型
     */
    void sendSystemMessage(Long userId, String title, String content, String type);
    
    /**
     * 发送邮件
     *
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    void sendEmail(String to, String subject, String content);
    
    /**
     * 发送预约相关通知
     *
     * @param order 预约信息
     * @param user 用户信息
     * @param message 通知消息
     */
    void sendOrderNotification(ServiceOrder order, User user, String message);
} 