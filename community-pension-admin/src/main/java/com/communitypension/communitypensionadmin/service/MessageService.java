package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.communitypension.communitypensionadmin.entity.Message;
import com.communitypension.communitypensionadmin.pojo.query.MessageQuery;

/**
 * 消息服务接口
 */
public interface MessageService {
    
    /**
     * 获取消息列表
     * @param query 查询参数
     * @return 分页消息列表
     */
    IPage<Message> getMessageList(MessageQuery query);
    
    /**
     * 获取消息详情
     * @param id 消息ID
     * @return 消息详情
     */
    Message getMessageById(Long id);
    
    /**
     * 标记消息为已读
     * @param id 消息ID
     * @return 是否成功
     */
    boolean markAsRead(Long id);
    
    /**
     * 标记所有消息为已读
     * @return 是否成功
     */
    boolean markAllMessagesAsRead();
    
    /**
     * 发送消息
     * @param message 消息对象
     * @return 是否成功
     */
    boolean sendMessage(Message message);
    
    /**
     * 删除消息
     * @param id 消息ID
     * @return 是否成功
     */
    boolean deleteMessage(Long id);
    
    /**
     * 清空所有消息
     * @return 是否成功
     */
    boolean clearAllMessages();
}
