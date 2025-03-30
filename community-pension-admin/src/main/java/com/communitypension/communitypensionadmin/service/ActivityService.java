package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.dto.ActivityDTO;
import com.communitypension.communitypensionadmin.dto.ActivityQuery;
import com.communitypension.communitypensionadmin.entity.Activity;
import com.communitypension.communitypensionadmin.vo.ActivityVO;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 活动Service接口
 */
public interface ActivityService extends IService<Activity> {
    
    /**
     * 分页查询活动列表
     */
    Page<ActivityVO> getActivityList(ActivityQuery query);
    
    /**
     * 获取活动详情
     */
    ActivityVO getActivityDetail(Long id);
    
    /**
     * 创建活动
     */
    void createActivity(ActivityDTO dto);
    
    /**
     * 更新活动
     */
    void updateActivity(Long id, ActivityDTO dto);
    
    /**
     * 删除活动
     */
    void deleteActivity(Long id);
    
    /**
     * 更新活动状态
     */
    void updateActivityStatus(Long id, Integer status);
    
    /**
     * 获取活动统计信息
     */
    Map<String, Object> getActivityStats(Long id);
    
    /**
     * 导出活动列表
     */
    void exportActivityList(String title, Integer status, String startTime, String endTime, HttpServletResponse response);
}