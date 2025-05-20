package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.entity.ActivityRegister;
import com.communitypension.communitypensionadmin.pojo.vo.ActivityRegisterVO;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

/**
 * 活动报名Service接口
 */
public interface ActivityRegisterService extends IService<ActivityRegister> {

    /**
     * 老人报名活动
     *
     * @param activityId 活动ID
     * @param elderId 老人ID
     * @param registerUserId 报名人ID（老人本人或家属）
     * @param registerType 报名类型：0-老人自己报名，1-家属代报名
     * @return 报名结果
     */
    boolean registerActivity(Long activityId, Long elderId, Long registerUserId, Integer registerType);

    /**
     * 获取老人对特定活动的报名状态
     *
     * @param activityId 活动ID
     * @param elderId 老人ID
     * @return 报名状态，如果未报名则返回null
     */
    Integer getElderActivityStatus(Long activityId, Long elderId);

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
     * 分页查询老人报名记录
     *
     * @param elderId 老人ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<ActivityRegisterVO> getElderRegisterList(Long elderId, Integer pageNum, Integer pageSize);

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
     * @param elderId 老人ID
     * @return 取消结果
     */
    boolean cancelRegister(Long id, Long elderId);

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

    /**
     * 检查老人是否已签到
     *
     * @param activityId 活动ID
     * @param elderId 老人ID
     * @return 是否已签到
     */
    boolean checkElderCheckedIn(Long activityId, Long elderId);

    /**
     * 获取老人对特定活动的报名ID
     *
     * @param activityId 活动ID
     * @param elderId 老人ID
     * @return 报名ID，如果未报名则返回null
     */
    Long getRegisterIdByActivityAndElder(Long activityId, Long elderId);
}
