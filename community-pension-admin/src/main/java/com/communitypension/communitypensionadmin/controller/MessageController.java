package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.communitypension.communitypensionadmin.entity.Message;
import com.communitypension.communitypensionadmin.utils.Result;
import com.communitypension.communitypensionadmin.pojo.query.MessageQuery;
import com.communitypension.communitypensionadmin.service.MessageService;
import com.communitypension.communitypensionadmin.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 消息中心控制器
 */
@RestController
@RequestMapping("/api/message")
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    /**
     * 获取消息列表
     */
    @GetMapping("/list")
    public Result<IPage<Message>> list(MessageQuery query) {
        return Result.success("查询成功", messageService.getMessageList(query));
    }
    
    /**
     * 获取消息详情
     */
    @GetMapping("/{id}")
    public Result<Message> getDetail(@PathVariable Long id) {
        Message message = messageService.getMessageById(id);
        if (message == null) {
            return Result.fail("消息不存在");
        }
        return Result.success("查询成功", message);
    }
    
    /**
     * 标记消息为已读
     */
    @PostMapping("/mark-read/{id}")
    public Result<Void> markAsRead(@PathVariable Long id) {
        if (messageService.markAsRead(id)) {
            return Result.success("标记已读成功");
        } else {
            return Result.fail("标记已读失败");
        }
    }
    
    /**
     * 标记所有消息为已读
     */
    @PostMapping("/mark-read-all")
    public Result<Void> markAllAsRead() {
        if (messageService.markAllMessagesAsRead()) {
            return Result.success("所有消息已标记为已读");
        } else {
            return Result.fail("标记已读失败");
        }
    }
    
    /**
     * 删除消息
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteMessage(@PathVariable Long id) {
        if (messageService.deleteMessage(id)) {
            return Result.success("删除成功");
        } else {
            return Result.fail("删除失败");
        }
    }
    
    /**
     * 清空所有消息
     */
    @DeleteMapping("/clear-all")
    public Result<Void> clearAllMessages() {
        if (messageService.clearAllMessages()) {
            return Result.success("清空成功");
        } else {
            return Result.fail("清空失败");
        }
    }
    
    /**
     * 发送消息
     */
    @PostMapping("/send")
    public Result<Void> sendMessage(@RequestBody Message message) {
        if (messageService.sendMessage(message)) {
            // 通过WebSocket发送实时消息通知
            try {
                WebSocketServer.sendMessage(Long.valueOf(message.getReceiver()), message);
            } catch (Exception e) {
                // WebSocket发送失败不影响API结果
                e.printStackTrace();
            }
            return Result.success("发送成功");
        } else {
            return Result.fail("发送失败");
        }
    }
}
