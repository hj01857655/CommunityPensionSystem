package com.communitypension.communitypensionadmin.vo;

import com.communitypension.communitypensionadmin.constant.DictTypeConstants;
import com.communitypension.communitypensionadmin.utils.DictUtils;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通知VO
 *
 * @date 2024-03-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationVO {
    
    /**
     * 通知ID
     */
    private Long id;
    
    /**
     * 接收用户ID
     */
    private Long userId;
    
    /**
     * 接收用户名称
     */
    private String userName;
    
    /**
     * 通知标题
     */
    private String title;
    
    /**
     * 通知内容
     */
    private String content;
    
    /**
     * 通知类型：1-系统通知 2-活动通知 3-服务通知
     */
    private Integer type;
    
    /**
     * 通知类型名称
     */
    private String typeName;
    
    /**
     * 状态：0-未读 1-已读
     */
    private Integer status;
    
    /**
     * 状态名称
     */
    private String statusName;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 获取通知类型名称
     */
    public String getTypeName() {
        if (type != null) {
            return DictUtils.getDictLabel(DictTypeConstants.NOTIFICATION_TYPE, String.valueOf(type));
        }
        return null;
    }
} 