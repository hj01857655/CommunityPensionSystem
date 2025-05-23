package com.communitypension.communitypensionadmin.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 活动列表视图对象
 * <p>
 * 用于展示活动列表，包含列表页面需要的简要信息
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityListVO {
    
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
     * 活动封面图片URL
     */
    private String coverImage;
    
    /**
     * 开始时间
     * <p>格式化为"yyyy-MM-dd HH:mm:ss"格式</p>
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     * <p>格式化为"yyyy-MM-dd HH:mm:ss"格式</p>
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;
    
    /**
     * 活动地点
     */
    private String location;
    
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
}
