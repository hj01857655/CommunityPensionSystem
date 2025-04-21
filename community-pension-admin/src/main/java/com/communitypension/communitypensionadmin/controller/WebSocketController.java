package com.communitypension.communitypensionadmin.controller;

import com.communitypension.communitypensionadmin.utils.Result;
import com.communitypension.communitypensionadmin.websocket.WebSocketMessage;
import com.communitypension.communitypensionadmin.websocket.WebSocketServer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/websocket")
@Tag(name = "WebSocket管理", description = "WebSocket相关接口")
public class WebSocketController {

    /**
     * 获取在线用户数量
     */
    @GetMapping("/online/count")
    @Operation(summary = "获取在线用户数量")
    public Result<Integer> getOnlineCount() {
        int count = WebSocketServer.getOnlineCount();
        return Result.success("获取成功", count);
    }

    /**
     * 检查用户是否在线
     */
    @GetMapping("/online/check/{userId}")
    @Operation(summary = "检查用户是否在线")
    public Result<Boolean> checkUserOnline(@PathVariable Long userId) {
        boolean isOnline = WebSocketServer.isOnline(userId);
        return Result.success("获取成功", isOnline);
    }

    /**
     * 发送消息给指定用户
     */
    @PostMapping("/send/{userId}")
    @Operation(summary = "发送消息给指定用户")
    public Result<Void> sendMessage(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "消息标题") @RequestParam String title,
            @Parameter(description = "消息内容") @RequestParam String content,
            @Parameter(description = "消息类型") @RequestParam(defaultValue = "SYSTEM") String type) {
        
        try {
            WebSocketMessage.MessageType messageType = WebSocketMessage.MessageType.valueOf(type);
            
            WebSocketMessage message = WebSocketMessage.builder()
                    .type(messageType)
                    .title(title)
                    .content(content)
                    .timestamp(LocalDateTime.now())
                    .source("admin")
                    .build();
            
            boolean isOnline = WebSocketServer.isOnline(userId);
            if (isOnline) {
                WebSocketServer.sendMessage(userId, message);
                return Result.success("发送成功");
            } else {
                return Result.error("用户不在线，发送失败");
            }
        } catch (IllegalArgumentException e) {
            return Result.error("无效的消息类型");
        } catch (Exception e) {
            log.error("发送消息失败", e);
            return Result.error("发送失败：" + e.getMessage());
        }
    }

    /**
     * 广播消息给所有在线用户
     */
    @PostMapping("/broadcast")
    @Operation(summary = "广播消息给所有在线用户")
    public Result<Void> broadcastMessage(
            @Parameter(description = "消息标题") @RequestParam String title,
            @Parameter(description = "消息内容") @RequestParam String content,
            @Parameter(description = "消息类型") @RequestParam(defaultValue = "SYSTEM") String type) {
        
        try {
            WebSocketMessage.MessageType messageType = WebSocketMessage.MessageType.valueOf(type);
            
            WebSocketMessage message = WebSocketMessage.builder()
                    .type(messageType)
                    .title(title)
                    .content(content)
                    .timestamp(LocalDateTime.now())
                    .source("admin")
                    .build();
            
            WebSocketServer.broadcastMessage(message);
            return Result.success("广播成功");
        } catch (IllegalArgumentException e) {
            return Result.error("无效的消息类型");
        } catch (Exception e) {
            log.error("广播消息失败", e);
            return Result.error("广播失败：" + e.getMessage());
        }
    }

    /**
     * 获取WebSocket连接信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取WebSocket连接信息")
    public Result<Map<String, Object>> getWebSocketInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("onlineCount", WebSocketServer.getOnlineCount());
        info.put("websocketUrl", "/websocket/{token}");
        info.put("supportedMessageTypes", WebSocketMessage.MessageType.values());
        
        return Result.success("获取成功", info);
    }
}