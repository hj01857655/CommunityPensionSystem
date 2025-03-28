package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("service_review")
public class ServiceReview {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Long orderId;
    
    private Long userId;
    
    /**
     * 评分(1-5星)
     */
    private Integer rating;
    
    private String content;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime reviewTime;
    
    private String replyContent;
    
    private LocalDateTime replyTime;
    
    @TableField(exist = false)
    private String userName;
    
    @TableField(exist = false)
    private String serviceName;
    
    @TableField(exist = false)
    private ServiceOrder order;
}