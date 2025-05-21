package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.constant.DictTypeConstants;
import com.communitypension.communitypensionadmin.entity.Notification;
import com.communitypension.communitypensionadmin.entity.ServiceOrder;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.enums.ServiceOrderStatus;
import com.communitypension.communitypensionadmin.mapper.NotificationMapper;
import com.communitypension.communitypensionadmin.pojo.query.NotificationQuery;
import com.communitypension.communitypensionadmin.service.NotificationService;
import com.communitypension.communitypensionadmin.utils.DictUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.communitypension.communitypensionadmin.utils.SecurityUtils;

/**
 * 通知服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    // 自注入，用于解决事务问题
    @Lazy
    @Autowired
    private NotificationService self;

    private final NotificationMapper notificationMapper;

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

        // 如果没有设置userId，则设置为系统用户ID
        if (notification.getUserId() == null) {
            // 获取当前登录用户ID，如果无法获取则使用系统用户ID
            try {
                Long currentUserId = SecurityUtils.getCurrentUserId();
                notification.setUserId(currentUserId);
            } catch (Exception e) {
                log.warn("无法获取当前用户ID，使用系统用户ID");
                notification.setUserId(1L); // 设置为系统用户ID
            }
        }

        notificationMapper.insert(notification);
    }

    @Override
    public void updateNotification(Notification notification) {
        // 先获取原通知信息
        Notification originalNotification = notificationMapper.selectNotificationById(notification.getId());
        if (originalNotification == null) {
            throw new RuntimeException("通知不存在");
        }

        // 确保更新时间字段被设置
        notification.setUpdateTime(LocalDateTime.now());

        // 记录日志
        log.info("更新通知，ID: {}, 标题: {}, 类型: {}", notification.getId(), notification.getTitle(), notification.getType());

        // 使用UpdateWrapper明确指定要更新的字段，确保type字段被更新
        LambdaQueryWrapper<Notification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notification::getId, notification.getId());

        // 执行更新，确保所有字段都被更新
        int result = notificationMapper.update(notification, queryWrapper);

        if (result <= 0) {
            log.error("更新通知失败，ID: {}", notification.getId());
            throw new RuntimeException("更新通知失败");
        }

        log.info("更新通知成功，影响行数: {}", result);
    }

    @Override
    public void deleteNotification(Long id) {
        notificationMapper.deleteById(id);
    }

    /**
     * 发布通知
     * 注意：在通知管理中，status字段有两种用途：
     * 1. 在通知管理上下文中：0-草稿，1-已发布，2-已撤回
     * 2. 在用户阅读上下文中：0-未读，1-已读
     * 
     * @param id 通知ID
     */
    @Override
    public void publishNotification(Long id) {
        Notification notification = new Notification();
        notification.setId(id);
        notification.setStatus(1); // 设置为已发布状态
        notification.setUpdateTime(LocalDateTime.now());
        notification.setPublishTime(LocalDateTime.now()); // 设置发布时间
        notificationMapper.updateById(notification);
    }

    /**
     * 撤回通知
     * 注意：在通知管理中，status字段有两种用途：
     * 1. 在通知管理上下文中：0-草稿，1-已发布，2-已撤回
     * 2. 在用户阅读上下文中：0-未读，1-已读
     * 
     * @param id 通知ID
     */
    @Override
    public void revokeNotification(Long id) {
        Notification notification = new Notification();
        notification.setId(id);
        notification.setStatus(2); // 设置为已撤回状态（而不是草稿状态）
        notification.setUpdateTime(LocalDateTime.now());
        notificationMapper.updateById(notification);
    }

    /**
     * 发送系统消息
     *
     * @param userId 用户ID
     * @param title 标题
     * @param content 内容
     * @param type 类型
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendSystemMessage(Long userId, String title, String content, String type) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setStatus(0); // 未读状态
        notification.setCreateTime(LocalDateTime.now());
        notificationMapper.insert(notification);
    }

    /**
     * 发送预约相关通知
     */
    @Override
    public void sendOrderNotification(ServiceOrder order, User user, String message) {
        // 实现发送预约相关通知的逻辑
        Notification notification = new Notification();
        notification.setUserId(user.getUserId());
        notification.setTitle("预约通知");
        // 使用buildOrderNotificationContent方法构建更丰富的通知内容
        notification.setContent(buildOrderNotificationContent(order, user, message));
        notification.setType("3"); // 服务通知
        notification.setStatus(0); // 未读状态
        notification.setCreateTime(LocalDateTime.now());
        notificationMapper.insert(notification);
    }

    /**
     * 构建预约通知内容
     */
    private String buildOrderNotificationContent(ServiceOrder order, User user, String message) {
        // 构建更丰富的通知内容
        StringBuilder sb = new StringBuilder();
        sb.append("尊敬的").append(user.getName()).append("用户，\n\n");
        sb.append("您的服务预约（预约号：").append(order.getId()).append("）").append(message).append("。\n\n");
        sb.append("预约详情：\n");
        sb.append("- 服务项目：").append(order.getServiceName()).append("\n");

        // 添加预约时间，防止空指针
        if (order.getScheduleTime() != null) {
            sb.append("- 预约时间：").append(order.getScheduleTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        }

        // 根据订单状态添加不同的提示信息
        if (order.getStatus() != null) {
            if (order.getStatus().equals(ServiceOrderStatus.ASSIGNED.getCode())) { // 已派单
                sb.append("\n您的预约已通过审核，服务人员将按预约时间上门服务，请保持电话畅通。");
            } else if (order.getStatus().equals(ServiceOrderStatus.PENDING.getCode())) { // 待审核
                sb.append("\n您的预约正在审核中，请耐心等待。审核结果将以系统通知的形式告知您。");
            } else if (order.getStatus().equals(ServiceOrderStatus.IN_PROGRESS.getCode())) { // 服务中
                sb.append("\n您的服务正在进行中，如有问题请联系客服。");
            } else if (order.getStatus().equals(ServiceOrderStatus.COMPLETED.getCode())) { // 已完成
                sb.append("\n您的服务已完成，感谢您的使用，欢迎对本次服务进行评价。");

                // 添加实际时长和费用信息（如果有）
                if (order.getActualDuration() != null) {
                    sb.append("\n- 实际服务时长：").append(order.getActualDuration()).append("分钟");
                }
                if (order.getActualFee() != null) {
                    sb.append("\n- 实际服务费用：").append(order.getActualFee()).append("元");
                }
            } else if (order.getStatus().equals(ServiceOrderStatus.CANCELLED.getCode())) { // 已取消
                sb.append("\n您的预约已取消。");
            } else if (order.getStatus().equals(ServiceOrderStatus.REJECTED.getCode())) { // 已拒绝
                sb.append("\n很抱歉，您的预约未能通过审核。");
                if (order.getReviewRemark() != null && !order.getReviewRemark().isEmpty()) {
                    sb.append("原因：").append(order.getReviewRemark());
                }
            }
        }

        // 添加底部提示信息
        sb.append("\n\n如有疑问，请联系客服中心。");

        return sb.toString();
    }

    @Override
    public List<Map<String, Object>> getRecentNotifications(int limit) {
        // 实现获取最新通知的逻辑
        LambdaQueryWrapper<Notification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Notification::getCreateTime);
        queryWrapper.last("LIMIT " + limit);

        List<Notification> notifications = notificationMapper.selectList(queryWrapper);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Notification notification : notifications) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", notification.getId());
            map.put("title", notification.getTitle());
            map.put("content", notification.getContent());
            map.put("createTime", notification.getCreateTime());
            map.put("status", notification.getStatus());
            result.add(map);
        }

        return result;
    }

    /**
     * 标记通知为已读
     * 注意：在用户阅读上下文中，status字段表示：
     * 0-未读，1-已读
     * 
     * 这与通知管理上下文中的status含义不同：
     * 0-草稿，1-已发布，2-已撤回
     * 
     * @param id 通知ID
     */
    @Override
    public void markAsRead(Long id) {
        // 检查通知是否存在
        Notification notification = notificationMapper.selectById(id);
        if (notification == null) {
            throw new RuntimeException("通知不存在");
        }

        // 更新通知状态为已读
        Notification update = new Notification();
        update.setId(id);
        update.setStatus(1); // 1 表示已读
        update.setUpdateTime(LocalDateTime.now());
        notificationMapper.updateById(update);
    }

    /**
     * 标记所有通知为已读
     * 注意：在用户阅读上下文中，status字段表示：
     * 0-未读，1-已读
     * 
     * 这与通知管理上下文中的status含义不同：
     * 0-草稿，1-已发布，2-已撤回
     */
    @Override
    public void markAllNotificationsAsRead() {
        // 获取当前登录用户ID
        Long userId = SecurityUtils.getCurrentUserId();

        // 创建更新条件：用户ID匹配且未读状态
        LambdaUpdateWrapper<Notification> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Notification::getUserId, userId)
                     .eq(Notification::getStatus, 0) // 0表示未读
                     .set(Notification::getStatus, 1) // 1表示已读
                     .set(Notification::getUpdateTime, LocalDateTime.now());

        // 执行批量更新
        update(updateWrapper);
    }

    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "草稿";
            case 1 -> "已发布";
            case 2 -> "已撤回";
            default -> "未知";
        };
    }
}
