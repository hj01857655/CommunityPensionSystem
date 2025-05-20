package com.communitypension.communitypensionadmin.pojo.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 活动VO
 */
@Data
public class ActivityVO {
    
    /**
     * 活动ID
     */
    private Long id;
    
    /**
     * 活动标题
     */
    private String title;
    
    /**
     * 活动类型
     */
    private String type;
    
    /**
     * 活动类型名称
     */
    private String typeName;
    
    /**
     * 活动描述
     */
    private String description;
    
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
     * 当前参与人数
     */
    private Integer currentParticipants;
    
    /**
     * 组织者ID
     */
    private Long organizerId;
    
    /**
     * 组织者姓名
     */
    private String organizerName;
    
    /**
     * 状态：0-筹备中，1-报名中，2-进行中，3-已结束，4-已取消
     */
    private Integer status;
    
    /**
     * 状态名称
     */
    private String statusName;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 