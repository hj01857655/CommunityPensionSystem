package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.converter.ServiceReviewConverter;
import com.communitypension.communitypensionadmin.dto.ServiceReviewAuditDTO;
import com.communitypension.communitypensionadmin.dto.ServiceReviewDTO;
import com.communitypension.communitypensionadmin.dto.ServiceReviewReplyDTO;
import com.communitypension.communitypensionadmin.entity.ServiceItem;
import com.communitypension.communitypensionadmin.entity.ServiceOrder;
import com.communitypension.communitypensionadmin.entity.ServiceReview;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.exception.BusinessException;
import com.communitypension.communitypensionadmin.mapper.ServiceReviewMapper;
import com.communitypension.communitypensionadmin.service.ServiceItemService;
import com.communitypension.communitypensionadmin.service.ServiceOrderService;
import com.communitypension.communitypensionadmin.service.ServiceReviewService;
import com.communitypension.communitypensionadmin.service.UserService;
import com.communitypension.communitypensionadmin.vo.ServiceReviewVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 服务评价Service实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceReviewServiceImpl extends ServiceImpl<ServiceReviewMapper, ServiceReview> implements ServiceReviewService {

    private final ServiceItemService serviceItemService;
    private final ServiceOrderService serviceOrderService;
    private final UserService userService;

    /**
     * 添加服务评价
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addServiceReview(ServiceReviewDTO reviewDTO) {
        // 检查服务预约是否存在
        ServiceOrder order = serviceOrderService.getById(reviewDTO.getServiceAppointmentId());
        if (order == null) {
            throw new BusinessException("服务预约不存在");
        }

        // 检查服务预约状态是否为已完成
        if (order.getStatus() != 3) { // 假设3表示已完成
            throw new BusinessException("只能评价已完成的服务");
        }

        // 检查是否已评价
        if (checkReviewExists(reviewDTO.getServiceAppointmentId(), reviewDTO.getElderId())) {
            throw new BusinessException("您已评价过此服务，不能重复评价");
        }

        // 设置服务ID
        reviewDTO.setServiceId(order.getServiceItemId());

        // 转换为实体并保存
        ServiceReview review = ServiceReviewConverter.toEntity(reviewDTO);
        save(review);

        return review.getId();
    }

    /**
     * 更新服务评价
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateServiceReview(ServiceReviewDTO reviewDTO) {
        // 检查评价是否存在
        ServiceReview review = getById(reviewDTO.getId());
        if (review == null) {
            throw new BusinessException("评价不存在");
        }

        // 检查是否是本人的评价
        if (!review.getElderId().equals(reviewDTO.getElderId())) {
            throw new BusinessException("只能修改自己的评价");
        }

        // 检查评价状态是否为待审核
        if (review.getStatus() != 0) {
            throw new BusinessException("只能修改待审核的评价");
        }

        // 更新评价内容
        review.setRating(reviewDTO.getRating());
        review.setContent(reviewDTO.getContent());
        review.setIsAnonymous(reviewDTO.getIsAnonymous());
        review.setReviewTime(LocalDateTime.now());

        return updateById(review);
    }

    /**
     * 删除服务评价
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteServiceReview(Long id) {
        return removeById(id);
    }

    /**
     * 获取服务评价详情
     */
    @Override
    public ServiceReviewVO getServiceReviewDetail(Long id) {
        // 获取评价
        ServiceReview review = getById(id);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }

        // 获取服务名称
        ServiceItem serviceItem = serviceItemService.getById(review.getServiceId());
        String serviceName = serviceItem != null ? serviceItem.getServiceName() : null;

        // 获取用户信息
        User elder = userService.getById(review.getElderId());
        User reviewUser = userService.getById(review.getReviewUserId());
        User replyAdmin = review.getReplyAdminId() != null ? userService.getById(review.getReplyAdminId()) : null;

        // 转换为VO
        return ServiceReviewConverter.toVO(review, serviceName, elder, reviewUser, replyAdmin);
    }

    /**
     * 分页查询服务评价列表
     */
    @Override
    public Page<ServiceReviewVO> getServiceReviewList(Long serviceId, Integer status, Integer pageNum, Integer pageSize) {
        // 分页查询
        Page<ServiceReview> page = new Page<>(pageNum, pageSize);

        // 构建查询条件
        LambdaQueryWrapper<ServiceReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceReview::getServiceId, serviceId);
        if (status != null) {
            wrapper.eq(ServiceReview::getStatus, status);
        }
        wrapper.eq(ServiceReview::getIsDeleted, 0);
        wrapper.orderByDesc(ServiceReview::getReviewTime);

        // 执行查询
        page = page(page, wrapper);

        // 获取服务名称
        ServiceItem serviceItem = serviceItemService.getById(serviceId);
        String serviceName = serviceItem != null ? serviceItem.getServiceName() : null;
        Map<Long, String> serviceNameMap = new HashMap<>();
        serviceNameMap.put(serviceId, serviceName);

        // 获取用户ID列表
        List<Long> userIds = new ArrayList<>();
        for (ServiceReview review : page.getRecords()) {
            userIds.add(review.getElderId());
            userIds.add(review.getReviewUserId());
            if (review.getReplyAdminId() != null) {
                userIds.add(review.getReplyAdminId());
            }
        }

        // 获取用户信息
        Map<Long, User> userMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getUserId, user -> user));

        // 转换为VO
        List<ServiceReviewVO> voList = ServiceReviewConverter.toVOList(page.getRecords(), serviceNameMap, userMap);

        // 构建返回结果
        Page<ServiceReviewVO> resultPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        resultPage.setRecords(voList);

        return resultPage;
    }

    /**
     * 获取老人的评价列表
     */
    @Override
    public List<ServiceReviewVO> getElderReviewList(Long elderId) {
        // 查询评价列表
        LambdaQueryWrapper<ServiceReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceReview::getElderId, elderId);
        wrapper.eq(ServiceReview::getIsDeleted, 0);
        wrapper.orderByDesc(ServiceReview::getReviewTime);
        List<ServiceReview> reviews = list(wrapper);

        // 获取服务ID列表
        List<Long> serviceIds = reviews.stream()
                .map(ServiceReview::getServiceId)
                .distinct()
                .collect(Collectors.toList());

        // 获取服务名称
        Map<Long, String> serviceNameMap = new HashMap<>();
        if (!serviceIds.isEmpty()) {
            List<ServiceItem> serviceItems = serviceItemService.listByIds(serviceIds);
            serviceNameMap = serviceItems.stream()
                    .collect(Collectors.toMap(ServiceItem::getServiceId, ServiceItem::getServiceName));
        }

        // 获取用户ID列表
        List<Long> userIds = new ArrayList<>();
        for (ServiceReview review : reviews) {
            userIds.add(review.getElderId());
            userIds.add(review.getReviewUserId());
            if (review.getReplyAdminId() != null) {
                userIds.add(review.getReplyAdminId());
            }
        }

        // 获取用户信息
        Map<Long, User> userMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getUserId, user -> user));

        // 转换为VO
        return ServiceReviewConverter.toVOList(reviews, serviceNameMap, userMap);
    }

    /**
     * 获取服务评价统计
     */
    @Override
    public Map<String, Object> getServiceReviewStats(Long serviceId) {
        // 查询已通过的评价
        LambdaQueryWrapper<ServiceReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceReview::getServiceId, serviceId);
        wrapper.eq(ServiceReview::getStatus, 1); // 1-已通过
        wrapper.eq(ServiceReview::getIsDeleted, 0);
        List<ServiceReview> reviews = list(wrapper);

        // 统计数据
        int totalCount = reviews.size();
        double avgRating = reviews.stream().mapToInt(ServiceReview::getRating).average().orElse(0);
        long fiveStarCount = reviews.stream().filter(r -> r.getRating() == 5).count();
        long fourStarCount = reviews.stream().filter(r -> r.getRating() == 4).count();
        long threeStarCount = reviews.stream().filter(r -> r.getRating() == 3).count();
        long twoStarCount = reviews.stream().filter(r -> r.getRating() == 2).count();
        long oneStarCount = reviews.stream().filter(r -> r.getRating() == 1).count();

        // 构建结果
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_count", totalCount);
        stats.put("avg_rating", avgRating);
        stats.put("five_star_count", fiveStarCount);
        stats.put("four_star_count", fourStarCount);
        stats.put("three_star_count", threeStarCount);
        stats.put("two_star_count", twoStarCount);
        stats.put("one_star_count", oneStarCount);

        return stats;
    }

    /**
     * 回复服务评价
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean replyServiceReview(ServiceReviewReplyDTO replyDTO) {
        // 检查评价是否存在
        ServiceReview review = getById(replyDTO.getReviewId());
        if (review == null) {
            throw new BusinessException("评价不存在");
        }

        // 更新回复信息
        review.setAdminReply(replyDTO.getReply());
        review.setReplyTime(LocalDateTime.now());
        review.setReplyAdminId(replyDTO.getAdminId());

        return updateById(review);
    }

    /**
     * 审核服务评价
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditServiceReview(ServiceReviewAuditDTO auditDTO) {
        // 检查评价ID列表是否为空
        if (auditDTO.getIds() == null || auditDTO.getIds().isEmpty()) {
            throw new BusinessException("评价ID列表不能为空");
        }

        // 检查审核状态是否有效
        if (auditDTO.getStatus() != 1 && auditDTO.getStatus() != 2) {
            throw new BusinessException("无效的审核状态");
        }

        // 批量更新评价状态
        List<ServiceReview> reviews = listByIds(auditDTO.getIds());
        for (ServiceReview review : reviews) {
            review.setStatus(auditDTO.getStatus());
            // 如果拒绝，可以设置拒绝原因
            if (auditDTO.getStatus() == 2 && auditDTO.getRemark() != null) {
                review.setAdminReply(auditDTO.getRemark());
                review.setReplyTime(LocalDateTime.now());
            }
        }

        return updateBatchById(reviews);
    }

    /**
     * 检查用户是否已评价服务预约
     */
    @Override
    public boolean checkReviewExists(Long serviceAppointmentId, Long elderId) {
        LambdaQueryWrapper<ServiceReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceReview::getServiceAppointmentId, serviceAppointmentId);
        wrapper.eq(ServiceReview::getElderId, elderId);
        wrapper.eq(ServiceReview::getIsDeleted, 0);
        return count(wrapper) > 0;
    }
}
