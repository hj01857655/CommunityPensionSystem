package com.communitypension.communitypensionadmin.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 活动查询参数
 */
@Data
public class ActivityQuery {
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 10;
    
    /**
     * 活动标题
     */
    private String title;
    
    /**
     * 活动类型
     */
    private String type;
    
    /**
     * 活动状态
     */
    private Integer status;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
} 