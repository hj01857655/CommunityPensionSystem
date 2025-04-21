package com.communitypension.communitypensionadmin.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * WebSocket消息模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {

    /**
     * 消息类型
     */
    private MessageType type;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息发送时间
     */
    private LocalDateTime timestamp;

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 消息来源
     */
    private String source;

    /**
     * 消息链接
     */
    private String link;

    /**
     * 消息类型枚举
     */
    public enum MessageType {
        /**
         * 系统通知
         */
        SYSTEM,

        /**
         * 健康提醒
         */
        HEALTH,

        /**
         * 服务预约
         */
        SERVICE,

        /**
         * 活动通知
         */
        ACTIVITY,

        /**
         * 紧急通知
         */
        EMERGENCY,

        /**
         * 心跳检测
         */
        HEARTBEAT
    }
}