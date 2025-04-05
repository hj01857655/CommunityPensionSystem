package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.dto.ServiceReviewAuditDTO;
import com.communitypension.communitypensionadmin.dto.ServiceReviewDTO;
import com.communitypension.communitypensionadmin.dto.ServiceReviewReplyDTO;
import com.communitypension.communitypensionadmin.entity.ServiceReview;
import com.communitypension.communitypensionadmin.vo.ServiceReviewVO;

import java.util.List;
import java.util.Map;

/**
 * 服务评价Service接口
 */
public interface ServiceReviewService extends IService<ServiceReview> {

    /**
     * 添加服务评价
     *
     * @param reviewDTO 评价DTO
     * @return 评价ID
     */
    Long addServiceReview(ServiceReviewDTO reviewDTO);

    /**
     * 更新服务评价
     *
     * @param reviewDTO 评价DTO
     * @return 是否成功
     */
    boolean updateServiceReview(ServiceReviewDTO reviewDTO);

    /**
     * 删除服务评价
     *
     * @param id 评价ID
     * @return 是否成功
     */
    boolean deleteServiceReview(Long id);

    /**
     * 获取服务评价详情
     *
     * @param id 评价ID
     * @return 评价详情
     */
    ServiceReviewVO getServiceReviewDetail(Long id);

    /**
     * 分页查询服务评价列表
     *
     * @param serviceId 服务ID
     * @param status 状态
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<ServiceReviewVO> getServiceReviewList(Long serviceId, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 获取老人的评价列表
     *
     * @param elderId 老人ID
     * @return 评价列表
     */
    List<ServiceReviewVO> getElderReviewList(Long elderId);

    /**
     * 获取服务评价统计
     *
     * @param serviceId 服务ID
     * @return 统计结果
     */
    Map<String, Object> getServiceReviewStats(Long serviceId);

    /**
     * 回复服务评价
     *
     * @param replyDTO 回复DTO
     * @return 是否成功
     */
    boolean replyServiceReview(ServiceReviewReplyDTO replyDTO);

    /**
     * 审核服务评价
     *
     * @param auditDTO 审核DTO
     * @return 是否成功
     */
    boolean auditServiceReview(ServiceReviewAuditDTO auditDTO);

    /**
     * 检查用户是否已评价服务预约
     *
     * @param serviceAppointmentId 服务预约ID
     * @param elderId 老人ID
     * @return 是否已评价
     */
    boolean checkReviewExists(Long serviceAppointmentId, Long elderId);
}
