package com.communitypension.communitypensionadmin.websocket;

import com.communitypension.communitypensionadmin.utils.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket服务
 */
@Slf4j
@Component
@ServerEndpoint("/websocket/{token}")
public class WebSocketServer {

    /**
     * 用于存储在线连接的用户，key为用户ID，value为WebSocket连接
     */
    private static final Map<Long, Session> ONLINE_SESSIONS = new ConcurrentHashMap<>();

    /**
     * 用于存储用户ID与Session的映射关系
     */
    private static final Map<Session, Long> SESSION_USER_MAP = new ConcurrentHashMap<>();

    /**
     * 用于存储用户类型，key为用户ID，value为用户类型（ADMIN或USER）
     */
    private static final Map<Long, String> USER_TYPE_MAP = new ConcurrentHashMap<>();

    /**
     * JSON转换器
     */
    private static ObjectMapper objectMapper;

    /**
     * JWT工具类
     */
    private static JwtTokenUtil jwtTokenUtil;

    /**
     * 注入ObjectMapper
     *
     * @param mapper 配置好的ObjectMapper
     */
    public static void setObjectMapper(ObjectMapper mapper) {
        WebSocketServer.objectMapper = mapper;
    }

    /**
     * 发送消息给指定用户
     *
     * @param userId  用户ID
     * @param message 消息内容
     */
    public static void sendMessage(Long userId, Object message) {
        Session session = ONLINE_SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                if (objectMapper == null) {
                    log.error("ObjectMapper未初始化，无法发送消息");
                    return;
                }
                String messageText = objectMapper.writeValueAsString(message);
                session.getBasicRemote().sendText(messageText);
                log.info("发送消息给用户{}\uff1a{}", userId, messageText);
            } catch (Exception e) {
                log.error("发送消息给用户{}失败", userId, e);
            }
        } else {
            log.warn("用户{}不在线，无法发送消息", userId);
        }
    }

    /**
     * 广播消息给所有在线用户
     *
     * @param message 消息内容
     */
    public static void broadcastMessage(Object message) {
        try {
            if (objectMapper == null) {
                log.error("ObjectMapper未初始化，无法广播消息");
                return;
            }
            String messageText = objectMapper.writeValueAsString(message);
            for (Map.Entry<Long, Session> entry : ONLINE_SESSIONS.entrySet()) {
                Session session = entry.getValue();
                if (session.isOpen()) {
                    try {
                        session.getBasicRemote().sendText(messageText);
                    } catch (IOException e) {
                        log.error("广播消息给用户{}失败", entry.getKey(), e);
                    }
                }
            }
            log.info("广播消息：{}，当前在线人数：{}", messageText, ONLINE_SESSIONS.size());
        } catch (Exception e) {
            log.error("广播消息失败", e);
        }
    }

    /**
     * 广播紧急消息给所有在线管理员
     *
     * @param message 紧急消息内容
     */
    public static void broadcastEmergencyMessage(Object message) {
        try {
            if (objectMapper == null) {
                log.error("ObjectMapper未初始化，无法广播紧急消息");
                return;
            }

            // 添加消息类型
            if (message instanceof Map) {
                ((Map) message).put("type", "EMERGENCY");
            }

            String messageText = objectMapper.writeValueAsString(message);
            int adminCount = 0;

            for (Map.Entry<Long, Session> entry : ONLINE_SESSIONS.entrySet()) {
                Long userId = entry.getKey();
                Session session = entry.getValue();

                // 只向管理员发送紧急消息
                String userType = USER_TYPE_MAP.get(userId);
                if ("ADMIN".equals(userType) && session.isOpen()) {
                    try {
                        session.getBasicRemote().sendText(messageText);
                        adminCount++;
                    } catch (IOException e) {
                        log.error("广播紧急消息给管理员{}失败", userId, e);
                    }
                }
            }

            log.info("广播紧急消息：{}，当前在线管理员数：{}", messageText, adminCount);
        } catch (Exception e) {
            log.error("广播紧急消息失败", e);
        }
    }

    /**
     * 判断用户是否在线
     *
     * @param userId 用户ID
     * @return 是否在线
     */
    public static boolean isOnline(Long userId) {
        Session session = ONLINE_SESSIONS.get(userId);
        return session != null && session.isOpen();
    }

    /**
     * 获取当前在线人数
     *
     * @return 在线人数
     */
    public static int getOnlineCount() {
        return ONLINE_SESSIONS.size();
    }

    /**
     * 注入JwtTokenUtil
     *
     * @param util 配置好的JwtTokenUtil
     */
    @Autowired
    public void setJwtTokenUtil(JwtTokenUtil util) {
        WebSocketServer.jwtTokenUtil = util;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        try {
            // 验证token并获取用户信息
            if (jwtTokenUtil == null) {
                log.error("WebSocket连接失败：JwtTokenUtil未注入");
                session.close();
                return;
            }

            String username = jwtTokenUtil.getUsernameFromToken(token);
            if (username == null) {
                log.warn("WebSocket连接失败：无效的token");
                session.close();
                return;
            }

            // 获取用户角色ID
            Long roleId = null;
            try {
                roleId = jwtTokenUtil.getRoleIdFromToken(token);
                log.info("WebSocket连接：用户 {} 的角色ID为 {}", username, roleId);
            } catch (Exception e) {
                log.warn("WebSocket连接：无法获取用户角色ID", e);
            }

            // 使用用户名的哈希码作为用户ID
            // 这样可以处理非数字格式的用户名，如"admin"
            Long userId = (long) username.hashCode();
            log.info("WebSocket连接：用户名 {} 转换为用户ID {}", username, userId);

            // 根据角色ID判断用户类型
            // 角色ID为4的是管理员，其他是普通用户
            String userType = (roleId != null && roleId == 4) ? "ADMIN" : "USER";
            USER_TYPE_MAP.put(userId, userType);
            log.info("WebSocket连接：用户 {} 的类型为 {}", username, userType);

            // 存储连接
            ONLINE_SESSIONS.put(userId, session);
            SESSION_USER_MAP.put(session, userId);
            log.info("WebSocket连接成功，用户ID：{}，当前在线人数：{}", userId, ONLINE_SESSIONS.size());
        } catch (Exception e) {
            log.error("WebSocket连接异常", e);
            try {
                session.close();
            } catch (IOException ex) {
                log.error("关闭WebSocket连接异常", ex);
            }
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        Long userId = SESSION_USER_MAP.get(session);
        if (userId != null) {
            ONLINE_SESSIONS.remove(userId);
            SESSION_USER_MAP.remove(session);
            log.info("WebSocket连接关闭，用户ID：{}，当前在线人数：{}", userId, ONLINE_SESSIONS.size());
        }
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        Long userId = SESSION_USER_MAP.get(session);
        log.info("收到用户{}的消息：{}", userId, message);
        // 这里可以处理客户端发送的消息，例如心跳检测等
    }

    /**
     * 发生错误时调用的方法
     */
    @OnError
    public void onError(Session session, Throwable error) {
        Long userId = SESSION_USER_MAP.get(session);
        log.error("WebSocket发生错误，用户ID：{}", userId, error);
    }
}