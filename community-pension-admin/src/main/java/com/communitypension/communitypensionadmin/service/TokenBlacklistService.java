package com.communitypension.communitypensionadmin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Token黑名单服务
 * 用于管理已失效的token，确保即使token未过期也无法使用
 */
@Service
public class TokenBlacklistService {
    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistService.class);
    
    // 使用ConcurrentHashMap存储黑名单token及其过期时间
    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();
    
    // 定时清理过期的黑名单token
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    
    public TokenBlacklistService() {
        // 每小时清理一次过期的黑名单token
        scheduler.scheduleAtFixedRate(this::cleanupExpiredTokens, 1, 1, TimeUnit.HOURS);
    }
    
    /**
     * 将token加入黑名单
     * @param token 需要加入黑名单的token
     * @param expirationTime token的过期时间（毫秒时间戳）
     */
    public void addToBlacklist(String token, long expirationTime) {
        if (token == null || token.isEmpty()) {
            return;
        }
        
        // 清理token前缀
        String cleanedToken = token.replaceFirst("^Bearer\\s*", "").replaceAll("\\s+", "");
        blacklist.put(cleanedToken, expirationTime);
        logger.info("Token已加入黑名单，将在{}失效", new java.util.Date(expirationTime));
    }
    
    /**
     * 检查token是否在黑名单中
     * @param token 需要检查的token
     * @return 如果token在黑名单中返回true，否则返回false
     */
    public boolean isBlacklisted(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        
        // 清理token前缀
        String cleanedToken = token.replaceFirst("^Bearer\\s*", "").replaceAll("\\s+", "");
        return blacklist.containsKey(cleanedToken);
    }
    
    /**
     * 清理过期的黑名单token
     */
    private void cleanupExpiredTokens() {
        long now = System.currentTimeMillis();
        int count = 0;
        
        for (Map.Entry<String, Long> entry : blacklist.entrySet()) {
            if (entry.getValue() < now) {
                blacklist.remove(entry.getKey());
                count++;
            }
        }
        
        if (count > 0) {
            logger.info("已清理{}个过期的黑名单token", count);
        }
    }
}