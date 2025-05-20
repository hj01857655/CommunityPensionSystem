package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 紧急呼叫实体类
 */
@Data
@TableName("emergency_calls")
public class EmergencyCall {
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户姓名
     */
    private String userName;
    
    /**
     * 用户电话
     */
    private String userPhone;
    
    /**
     * 紧急呼叫描述
     */
    private String description;
    
    /**
     * 位置信息
     */
    private String location;
    
    /**
     * 呼叫时间
     */
    private LocalDateTime callTime;
    
    /**
     * 状态：submitted(已提交)、processing(处理中)、completed(已完成)、cancelled(已取消)
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 处理人
     */
    private String processedBy;
    
    /**
     * 处理时间
     */
    private LocalDateTime processedAt;
}
