package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.EmergencyCall;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.mapper.EmergencyCallMapper;
import com.communitypension.communitypensionadmin.query.EmergencyCallQuery;
import com.communitypension.communitypensionadmin.service.EmergencyCallService;
import com.communitypension.communitypensionadmin.service.NotificationService;
import com.communitypension.communitypensionadmin.service.UserService;
import com.communitypension.communitypensionadmin.websocket.WebSocketMessage;
import com.communitypension.communitypensionadmin.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * 紧急呼叫服务实现类
 */
@Slf4j
@Service
public class EmergencyCallServiceImpl extends ServiceImpl<EmergencyCallMapper, EmergencyCall> implements EmergencyCallService {

    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UserService userService;

    @Override
    public IPage<EmergencyCall> getEmergencyCallList(EmergencyCallQuery query) {
        LambdaQueryWrapper<EmergencyCall> wrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (query.getUserId() != null) {
            wrapper.eq(EmergencyCall::getUserId, query.getUserId());
        }
        
        if (StringUtils.hasText(query.getUserName())) {
            wrapper.like(EmergencyCall::getUserName, query.getUserName());
        }
        
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(EmergencyCall::getStatus, query.getStatus());
        }
        
        // 按创建时间降序排序
        wrapper.orderByDesc(EmergencyCall::getCreatedAt);
        
        // 分页查询
        Page<EmergencyCall> page = new Page<>(query.getPageNum(), query.getPageSize());
        return page(page, wrapper);
    }

    @Override
    public EmergencyCall getEmergencyCallById(Long id) {
        return getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveEmergencyCall(EmergencyCall emergencyCall) {
        // 设置默认值
        emergencyCall.setStatus("submitted");
        emergencyCall.setCreatedAt(LocalDateTime.now());
        emergencyCall.setUpdatedAt(LocalDateTime.now());
        
        boolean saved = save(emergencyCall);
        
        if (saved) {
            // 发送紧急通知给所有管理员
            sendEmergencyNotificationToAdmins(emergencyCall);
        }
        
        return saved;
    }
    
    /**
     * 发送紧急呼叫消息给所有管理员和社区工作人员
     * @param emergencyCall 紧急呼叫信息
     */
    private void sendEmergencyNotificationToAdmins(EmergencyCall emergencyCall) {
        try {
            // 1. 构建紧急消息内容
            String title = "紧急呼叫警报";
            String content = String.format("收到来自用户 %s 的紧急呼叫！\n请立即处理！", 
                    emergencyCall.getUserName());
            
            // 2. 获取所有管理员和社区工作人员
            List<User> adminUsers = userService.getAdminUsers();
            
            // 3. 向每个管理员发送系统消息（保存到消息中心）
            for (User admin : adminUsers) {
                notificationService.sendSystemMessage(
                        admin.getUserId(), 
                        title, 
                        content, 
                        "emergency");
            }
            
            // 4. 发送WebSocket实时通知，使用EMERGENCY类型触发后台弹窗
            WebSocketMessage message = WebSocketMessage.builder()
                    .type(WebSocketMessage.MessageType.EMERGENCY)
                    .title(title)
                    .content(content)
                    .timestamp(LocalDateTime.now())
                    .source("system")
                    .build();
            
            // 向所有管理员和社区工作人员发送WebSocket消息
            for (User admin : adminUsers) {
                if (WebSocketServer.isOnline(admin.getUserId())) {
                    WebSocketServer.sendMessage(admin.getUserId(), message);
                }
            }
            
            // 同时广播紧急消息给所有在线管理员
            WebSocketServer.broadcastMessage(message);
            
        } catch (Exception e) {
            // 记录错误但不影响主流程
            log.error("发送紧急呼叫通知失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateEmergencyCall(EmergencyCall emergencyCall) {
        emergencyCall.setUpdatedAt(LocalDateTime.now());
        return updateById(emergencyCall);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelEmergencyCall(Long id) {
        EmergencyCall emergencyCall = getById(id);
        if (emergencyCall == null) {
            return false;
        }
        
        // 只有submitted状态的呼叫才能取消
        if (!"submitted".equals(emergencyCall.getStatus())) {
            return false;
        }
        
        emergencyCall.setStatus("cancelled");
        emergencyCall.setUpdatedAt(LocalDateTime.now());
        
        return updateById(emergencyCall);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean processEmergencyCall(Long id, String processedBy) {
        EmergencyCall emergencyCall = getById(id);
        if (emergencyCall == null) {
            return false;
        }
        
        // 只有submitted状态的呼叫才能处理
        if (!"submitted".equals(emergencyCall.getStatus())) {
            return false;
        }
        
        emergencyCall.setStatus("processing");
        emergencyCall.setProcessedBy(processedBy);
        emergencyCall.setProcessedAt(LocalDateTime.now());
        emergencyCall.setUpdatedAt(LocalDateTime.now());
        
        return updateById(emergencyCall);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean completeEmergencyCall(Long id) {
        EmergencyCall emergencyCall = getById(id);
        if (emergencyCall == null) {
            return false;
        }
        
        // 只有processing状态的呼叫才能完成
        if (!"processing".equals(emergencyCall.getStatus())) {
            return false;
        }
        
        emergencyCall.setStatus("completed");
        emergencyCall.setUpdatedAt(LocalDateTime.now());
        
        return updateById(emergencyCall);
    }

    @Override
    public IPage<EmergencyCall> getUserEmergencyCallHistory(Long userId, EmergencyCallQuery query) {
        if (userId == null) {
            return Page.of(query.getPageNum(), query.getPageSize());
        }
        
        query.setUserId(userId);
        return getEmergencyCallList(query);
    }
}
