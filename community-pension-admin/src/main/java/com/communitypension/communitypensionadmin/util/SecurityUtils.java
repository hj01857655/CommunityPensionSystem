package com.communitypension.communitypensionadmin.util;

import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.service.UserService;
import com.communitypension.communitypensionadmin.utils.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 安全工具类，用于获取当前登录用户信息
 */
@Component
public class SecurityUtils {
    private static final Logger logger = LoggerFactory.getLogger(SecurityUtils.class);

    private static JwtTokenUtil jwtTokenUtil;
    private static UserService userService;

    /**
     * 获取当前登录用户ID
     *
     * @return 当前登录用户ID，如果未登录则返回null
     */
    public static Long getCurrentUserId() {
        String username = getCurrentUsername();
        if (username == null) {
            return null;
        }

        // 根据用户名获取用户信息
        User user = userService.getUserByUsername(username);
        return user != null ? user.getUserId() : null;
    }

    /**
     * 获取当前登录用户名
     *
     * @return 当前登录用户名，如果未登录则返回null
     */
    public static String getCurrentUsername() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String token = request.getHeader("Authorization");
                if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7);
                    return jwtTokenUtil.getUsernameFromToken(token);
                }
            }
        } catch (Exception e) {
            logger.error("获取当前用户信息失败", e);
        }
        return null;
    }

    /**
     * 获取当前登录用户
     *
     * @return 当前登录用户，如果未登录则返回null
     */
    public static User getCurrentUser() {
        String username = getCurrentUsername();
        if (username == null) {
            return null;
        }
        return userService.getUserByUsername(username);
    }

    @Autowired
    public void setJwtTokenUtil(JwtTokenUtil jwtTokenUtil) {
        SecurityUtils.jwtTokenUtil = jwtTokenUtil;
    }

    @Autowired
    public void setUserService(UserService userService) {
        SecurityUtils.userService = userService;
    }
}
