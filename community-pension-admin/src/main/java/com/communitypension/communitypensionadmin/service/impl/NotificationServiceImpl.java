package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.Notification;
import com.communitypension.communitypensionadmin.mapper.NotificationMapper;
import com.communitypension.communitypensionadmin.service.NotificationService;
import com.communitypension.communitypensionadmin.vo.query.NotificationQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(rollbackFor = Exception.class)
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {
    
    @Autowired
    private NotificationMapper notificationMapper;
    
    @Override
    public IPage<Notification> getNotificationList(NotificationQuery query) {
        Page<Notification> page = new Page<>(query.getPageNum(), query.getPageSize());
        return notificationMapper.selectNotificationList(page, query);
    }
    
    @Override
    public Notification getNotificationById(Long id) {
        return notificationMapper.selectNotificationById(id);
    }
    
    @Override
    public void saveNotification(Notification notification) {
        notification.setStatus(0); // 设置为草稿状态
        notificationMapper.insert(notification);
    }
    
    @Override
    public void updateNotification(Notification notification) {
        notificationMapper.updateById(notification);
    }
    
    @Override
    public void deleteNotification(Long id) {
        notificationMapper.deleteById(id);
    }
    
    @Override
    public void publishNotification(Long id) {
        Notification notification = new Notification();
        notification.setId(id);
        notification.setStatus(1); // 设置为已发布状态
        notification.setPublishTime(LocalDateTime.now());
        notificationMapper.updateById(notification);
    }
    
    @Override
    public void revokeNotification(Long id) {
        Notification notification = new Notification();
        notification.setId(id);
        notification.setStatus(2); // 设置为已撤回状态
        notificationMapper.updateById(notification);
    }
} 