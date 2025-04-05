package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.entity.ActivityCheckIn;
import com.communitypension.communitypensionadmin.vo.ActivityCheckInVO;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

/**
 * 活动签到Service接口
 */
public interface ActivityCheckInService extends IService<ActivityCheckIn> {
    
    /**
     * 用户签到
     *
     * @param participateId 报名ID
     * @return 签到结果
     */
    boolean checkIn(Long participateId);
    
    /**
     * 批量签到
     *
     * @param participateIds 报名ID列表
     * @return 签到结果
     */
    boolean batchCheckIn(List<Long> participateIds);
    
    /**
     * 获取用户签到状态
     *
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 签到状态，如果未签到则返回null
     */
    Integer getUserCheckInStatus(Long activityId, Long userId);
    
    /**
     * 获取活动签到统计
     *
     * @param activityId 活动ID
     * @return 统计结果
     */
    Map<String, Object> getCheckInStats(Long activityId);
    
    /**
     * 分页查询活动签到记录
     *
     * @param activityId 活动ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<ActivityCheckInVO> getCheckInList(Long activityId, Integer pageNum, Integer pageSize);
    
    /**
     * 导出活动签到记录
     *
     * @param activityId 活动ID
     * @param response HTTP响应
     */
    void exportCheckInList(Long activityId, HttpServletResponse response);
}
