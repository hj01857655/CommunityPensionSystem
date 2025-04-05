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
     * 签到
     *
     * @param registerId 报名ID
     * @param checkInUserId 签到人ID（老人本人或家属）
     * @param isProxyCheckIn 是否代签：0-本人签到，1-他人代签
     * @return 签到结果
     */
    boolean checkIn(Long registerId, Long checkInUserId, Integer isProxyCheckIn);

    /**
     * 批量签到
     *
     * @param registerIds 报名ID列表
     * @param checkInUserId 签到人ID（老人本人或家属）
     * @param isProxyCheckIn 是否代签：0-本人签到，1-他人代签
     * @return 签到结果
     */
    boolean batchCheckIn(List<Long> registerIds, Long checkInUserId, Integer isProxyCheckIn);

    /**
     * 签退
     *
     * @param registerId 报名ID
     * @return 签退结果
     */
    boolean signOut(Long registerId);

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

    /**
     * 根据报名ID获取签到记录
     *
     * @param registerId 报名ID
     * @return 签到记录
     */
    ActivityCheckInVO getCheckInByRegisterId(Long registerId);

    /**
     * 检查老人是否已签到
     *
     * @param activityId 活动ID
     * @param elderId 老人ID
     * @return 是否已签到
     */
    boolean checkElderCheckedIn(Long activityId, Long elderId);
}
