package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.constant.DictTypeConstants;
import com.communitypension.communitypensionadmin.dto.NotificationDTO;
import com.communitypension.communitypensionadmin.entity.Notification;
import com.communitypension.communitypensionadmin.entity.ServiceOrder;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.mapper.NotificationMapper;
import com.communitypension.communitypensionadmin.service.NotificationService;
import com.communitypension.communitypensionadmin.utils.DictUtils;
import com.communitypension.communitypensionadmin.query.NotificationQuery;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 通知服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {
    
    private final JavaMailSender mailSender;
    private final NotificationMapper notificationMapper;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
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
     * 将通知类型字符串转换为整数
     *
     * @param type 类型字符串
     * @return 类型整数
     */
    private Integer convertTypeToInteger(String type) {
        switch (type.toLowerCase()) {
            case "system":
                return 1;
            case "activity":
                return 2;
            case "service":
                return 3;
            default:
                return 1;  // 默认为系统通知
        }
    }

    /**
     * 发送邮件
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    @Override
    public void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            
            mailSender.send(message);
            log.info("邮件发送成功: {}", to);
        } catch (Exception e) {
            log.error("发送邮件失败: {}", e.getMessage(), e);
            throw new RuntimeException("发送邮件失败", e);
        }
    }
    /**
     * 发送预约相关通知
     */
    @Override
    public void sendOrderNotification(ServiceOrder order, User user, String message) {
        // 构建通知内容
        String title = "服务预约通知";
        String content = buildOrderNotificationContent(order, user, message);
        
        // 发送系统消息
        sendSystemMessage(user.getUserId(), title, content, DictTypeConstants.NOTIFICATION_TYPE_SYSTEM);
        
        // 如果用户有邮箱,发送邮件通知
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            String emailSubject = "社区养老服务预约通知";
            String emailContent = buildOrderEmailContent(order, user, message);
            sendEmail(user.getEmail(), emailSubject, emailContent);
        }
    }
    
    /**
     * 构建预约通知内容
     */
    private String buildOrderNotificationContent(ServiceOrder order, User user, String message) {
        return String.format("尊敬的%s用户，您的服务预约（预约号：%s）%s。预约时间：%s，服务项目：%s。",
                user.getName(),
                order.getId(),
                message,
                order.getScheduleTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                order.getServiceName());
    }
    
    /**
     * 构建预约邮件内容
     */
    private String buildOrderEmailContent(ServiceOrder order, User user, String message) {
        try {
            // 读取Vue模板
            ClassPathResource resource = new ClassPathResource("templates/email/OrderNotification.vue");
            String template = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            
            // 准备模板数据
            Map<String, Object> data = Map.of(
                "userName", user.getName(),
                "orderId", order.getId(),
                "message", message,
                "scheduleTime", order.getScheduleTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                "serviceName", order.getServiceName()
            );
            
            // 使用Vue模板引擎渲染内容
            return renderVueTemplate(template, data);
        } catch (Exception e) {
            log.error("生成邮件内容失败: {}", e.getMessage(), e);
            return buildOrderNotificationContent(order, user, message);
        }
    }
    
    /**
     * 渲染Vue模板
     */
    private String renderVueTemplate(String template, Map<String, Object> data) {
        // 这里需要集成Vue的模板引擎
        // 可以使用vue-server-renderer或其他Vue SSR工具
        // 暂时返回简单的HTML内容
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>社区养老服务预约通知</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .header { background-color: #f8f9fa; padding: 20px; text-align: center; }
                    .content { padding: 20px; }
                    .footer { text-align: center; color: #6c757d; }
                    .highlight { color: #007bff; font-weight: bold; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h2>社区养老服务预约通知</h2>
                </div>
                <div class="content">
                    <p>尊敬的 <span class="highlight">%s</span>：</p>
                    <p>您的服务预约信息如下：</p>
                    <ul>
                        <li>预约编号：<span class="highlight">%s</span></li>
                        <li>预约状态：<span class="highlight">%s</span></li>
                        <li>预约时间：<span class="highlight">%s</span></li>
                        <li>服务项目：<span class="highlight">%s</span></li>
                    </ul>
                    <p>如有任何疑问，请联系我们的客服人员。</p>
                </div>
                <div class="footer">
                    <p>此邮件由系统自动发送，请勿回复。</p>
                </div>
            </body>
            </html>
            """,
            data.get("userName"),
            data.get("orderId"),
            data.get("message"),
            data.get("scheduleTime"),
            data.get("serviceName")
        );
    }
} 