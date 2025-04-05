package com.communitypension.communitypensionadmin.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.communitypension.communitypensionadmin.utils.JwtTokenUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * MyBatis Plus 自动填充处理器
 * 用于自动填充创建时间、创建者、更新时间、更新者等字段
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public void insertFill(MetaObject metaObject) {
        // 创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);
        // 更新时间
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
        // 审核时间
        this.strictInsertFill(metaObject, "reviewTime", LocalDateTime::now, LocalDateTime.class);
        // 发布时间
        this.strictInsertFill(metaObject, "publishTime", LocalDateTime::now, LocalDateTime.class);
        // 创建时间（createdAt）
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime::now, LocalDateTime.class);
        // 更新时间（updatedAt）
        this.strictInsertFill(metaObject, "updatedAt", LocalDateTime::now, LocalDateTime.class);
        // 参与时间
        this.strictInsertFill(metaObject, "participateTime", LocalDateTime::now, LocalDateTime.class);

        // 获取当前登录用户
        String username = getCurrentUsername();
        if (username != null) {
            // 创建者
            this.strictInsertFill(metaObject, "createBy", () -> username, String.class);
            // 更新者
            this.strictInsertFill(metaObject, "updateBy", () -> username, String.class);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
        // 更新时间（updatedAt）
        this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime::now, LocalDateTime.class);

        // 获取当前登录用户
        String username = getCurrentUsername();
        if (username != null) {
            // 更新者
            this.strictUpdateFill(metaObject, "updateBy", () -> username, String.class);
        }
    }

    /**
     * 获取当前登录用户名
     */
    private String getCurrentUsername() {
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
            // 忽略异常，返回null
        }
        return null;
    }
}
