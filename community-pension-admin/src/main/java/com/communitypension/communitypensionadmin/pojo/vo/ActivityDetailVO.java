package com.communitypension.communitypensionadmin.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 活动详情视图对象
 * <p>
 * 用于展示活动详细信息，包含详情页面需要的完整信息
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDetailVO {
    
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
     * 活动封面图片URL
     */
    private String coverImage;
    
    /**
     * 开始时间
     * <p>格式化为"yyyy-MM-dd HH:mm:ss"格式</p>
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8", shape = JsonFormat.Shape.STRING)
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     * <p>格式化为"yyyy-MM-dd HH:mm:ss"格式</p>
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8", shape = JsonFormat.Shape.STRING)
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
     * <p>格式化为"yyyy-MM-dd HH:mm:ss"格式</p>
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     * <p>格式化为"yyyy-MM-dd HH:mm:ss"格式</p>
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedAt;
}
