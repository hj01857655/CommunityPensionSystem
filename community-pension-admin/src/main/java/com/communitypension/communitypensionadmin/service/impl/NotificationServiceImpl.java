package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.constant.DictTypeConstants;
import com.communitypension.communitypensionadmin.dto.NotificationDTO;
import com.communitypension.communitypensionadmin.entity.Notification;
import com.communitypension.communitypensionadmin.entity.ServiceOrder;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.enums.ServiceOrderStatus;
import com.communitypension.communitypensionadmin.mapper.NotificationMapper;
import com.communitypension.communitypensionadmin.query.NotificationQuery;
import com.communitypension.communitypensionadmin.service.NotificationService;
import com.communitypension.communitypensionadmin.utils.DictUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    /**
     * 发布通知
     * @param id 通知ID
     */
    @Override
    public void publishNotification(Long id) {
        Notification notification = new Notification();
        notification.setId(id);
        notification.setStatus(1); // 设置为已发布状态
        notification.setUpdateTime(LocalDateTime.now());
        notificationMapper.updateById(notification);
    }

    @Override
    public void revokeNotification(Long id) {
        Notification notification = new Notification();
        notification.setId(id);
        notification.setStatus(2); // 设置为已撤回状态
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
        // 获取通知类型标签
        String typeLabel = DictUtils.getDictLabel(DictTypeConstants.NOTIFICATION_TYPE, type);

        // 构建通知DTO
        NotificationDTO notificationDTO = NotificationDTO.builder()
            .userId(userId)
            .title(title)
            .content(content)
            .status(0)  // 未读状态
            .build();

        // 转换为实体
        Notification notification = Notification.builder()
            .userId(notificationDTO.getUserId())
            .title(notificationDTO.getTitle())
            .content(notificationDTO.getContent())
            .status(notificationDTO.getStatus())
            .createTime(LocalDateTime.now())
            .build();

        // 保存通知
        this.save(notification);

        log.info("发送{}通知给用户{}: {}", typeLabel, userId, title);
    }

    /**
     * 发送邮件 - 已经废弃，改为使用系统内通知
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    @Override
    @Deprecated
    public void sendEmail(String to, String subject, String content) {
        // 不再发送邮件，改为使用系统内通知
        log.info("邮件发送功能已经废弃，改为使用系统内通知");
    }
    /**
     * 发送预约相关通知
     */
    @Override
    public void sendOrderNotification(ServiceOrder order, User user, String message) {
        // 构建通知内容
        String title = "服务预约通知";
        String content = buildOrderNotificationContent(order, user, message);

        // 使用self调用事务方法，确保事务生效
        self.sendSystemMessage(user.getUserId(), title, content, DictTypeConstants.NOTIFICATION_TYPE_SYSTEM);

        // 不再发送邮件通知
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


}