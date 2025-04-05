package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.entity.ActivityRegister;
import com.communitypension.communitypensionadmin.vo.ActivityRegisterVO;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

/**
 * 活动报名Service接口
 */
public interface ActivityRegisterService extends IService<ActivityRegister> {

    /**
     * 用户报名活动
     *
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 报名结果
     */
    boolean registerActivity(Long activityId, Long userId);

    /**
     * 获取用户对特定活动的报名状态
     *
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 报名状态，如果未报名则返回null
     */
    Integer getUserActivityStatus(Long activityId, Long userId);

    /**
     * 分页查询活动报名记录
     *
     * @param activityId 活动ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<ActivityRegisterVO> getActivityRegisterList(Long activityId, Integer pageNum, Integer pageSize);

    /**
     * 分页查询用户报名记录
     *
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<ActivityRegisterVO> getUserRegisterList(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 更新报名状态
     *
     * @param id 报名ID
     * @param status 新状态
     * @return 更新结果
     */
    boolean updateRegisterStatus(Long id, Integer status);

    /**
     * 取消报名
     *
     * @param id 报名ID
     * @param userId 用户ID
     * @return 取消结果
     */
    boolean cancelRegister(Long id, Long userId);

    /**
     * 审核报名
     *
     * @param id 报名ID
     * @param status 审核状态（1-通过，2-拒绝）
     * @param remark 审核备注
     * @return 审核结果
     */
    boolean auditRegister(Long id, Integer status, String remark);

    /**
     * 批量审核报名
     *
     * @param ids 报名ID列表
     * @param status 审核状态（1-通过，2-拒绝）
     * @param remark 审核备注
     * @return 审核结果
     */
    boolean batchAuditRegister(List<Long> ids, Integer status, String remark);

    /**
     * 签到
     *
     * @param id 报名ID
     * @return 签到结果
     */
    boolean checkIn(Long id);

    /**
     * 批量签到
     *
     * @param ids 报名ID列表
     * @return 签到结果
     */
    boolean batchCheckIn(List<Long> ids);

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
    Page<ActivityRegisterVO> getCheckInList(Long activityId, Integer pageNum, Integer pageSize);

    /**
     * 导出活动签到记录
     *
     * @param activityId 活动ID
     * @param response HTTP响应
     */
    void exportCheckInList(Long activityId, HttpServletResponse response);

    /**
     * 获取活动报名统计
     *
     * @param activityId 活动ID
     * @return 统计结果
     */
    Map<String, Object> getRegisterStats(Long activityId);

    /**
     * 导出活动报名列表
     *
     * @param activityId 活动ID
     * @param response HTTP响应
     */
    void exportRegisterList(Long activityId, HttpServletResponse response);
}
