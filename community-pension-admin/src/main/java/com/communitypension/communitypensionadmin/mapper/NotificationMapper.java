package com.communitypension.communitypensionadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.entity.Notification;
import com.communitypension.communitypensionadmin.model.query.NotificationQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
    
    /**
     * 获取通知公告列表
     */
    IPage<Notification> selectNotificationList(Page<Notification> page, @Param("query") NotificationQuery query);
    
    /**
     * 获取通知公告详情
     */
    Notification selectNotificationById(@Param("id") Long id);
} 