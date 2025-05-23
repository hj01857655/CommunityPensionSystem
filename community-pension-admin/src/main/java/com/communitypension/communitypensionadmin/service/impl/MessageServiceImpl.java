package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.Message;
import com.communitypension.communitypensionadmin.mapper.MessageMapper;
import com.communitypension.communitypensionadmin.pojo.query.MessageQuery;
import com.communitypension.communitypensionadmin.service.MessageService;
import com.communitypension.communitypensionadmin.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 消息服务实现类
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Override
    public IPage<Message> getMessageList(MessageQuery query) {
        // 如果没有指定接收者，则使用当前登录用户
        if (!StringUtils.hasText(query.getReceiver())) {
            query.setReceiver(SecurityUtils.getCurrentUsername());
        }
        
        // 创建分页对象
        Page<Message> page = new Page<>(query.getCurrent(), query.getSize());
        
        // 构建查询条件
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getReceiver, query.getReceiver());
        
        // 根据消息类型过滤
        if ("read".equals(query.getType())) {
            queryWrapper.eq(Message::getIsRead, true);
        } else if ("unread".equals(query.getType())) {
            queryWrapper.eq(Message::getIsRead, false);
        }
        
        // 关键词搜索
        if (StringUtils.hasText(query.getKeyword())) {
            queryWrapper.like(Message::getContent, query.getKeyword());
        }
        
        // 按时间倒序排序
        queryWrapper.orderByDesc(Message::getTime);
        
        // 执行查询
        return page(page, queryWrapper);
    }

    @Override
    public Message getMessageById(Long id) {
        return getById(id);
    }

    @Override
    @Transactional
    public boolean markAsRead(Long id) {
        return baseMapper.markAsRead(id) > 0;
    }

    @Override
    @Transactional
    public boolean markAllMessagesAsRead() {
        String currentUser = SecurityUtils.getCurrentUsername();
        return baseMapper.markAllAsRead(currentUser) > 0;
    }

    @Override
    @Transactional
    public boolean sendMessage(Message message) {
        // 设置消息时间
        LocalDateTime now = LocalDateTime.now();
        message.setTime(now);
        
        // 默认未读
        message.setIsRead(false);
        
        // 如果没有设置备注，根据消息类型设置默认备注
        if (!StringUtils.hasText(message.getRemark())) {
            if ("system".equals(message.getType())) {
                message.setRemark("系统消息");
            } else if ("user".equals(message.getType())) {
                message.setRemark("用户消息");
            }
        }
        
        // createTime, updateTime, createBy, updateBy 字段由 MyMetaObjectHandler 自动填充
        return save(message);
    }

    @Override
    @Transactional
    public boolean deleteMessage(Long id) {
        return removeById(id);
    }

    @Override
    @Transactional
    public boolean clearAllMessages() {
        String currentUser = SecurityUtils.getCurrentUsername();
        
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getReceiver, currentUser);
        
        return remove(queryWrapper);
    }
}
