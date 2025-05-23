package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 社区活动实体类
 */
@Data
@TableName("activities")
public class Activity {
    
    /**
     * 活动ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 活动标题
     */
    private String title;
    
    /**
     * 活动类型（字典类型：activity_type）
     */
    private String type;
    
    /**
     * 活动描述
     */
    private String description;
    
    /**
     * 活动封面图片URL
     */
    @TableField("cover_image")
    private String coverImage;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 活动地点
     */
    private String location;
    
    /**
     * 最大参与人数
     */
    private Integer maxParticipants;
    
    /**
     * 组织者ID
     */
    private Long organizerId;
    
    /**
     * 状态：0-筹备中，1-报名中，2-进行中，3-已结束，4-已取消
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    

} 