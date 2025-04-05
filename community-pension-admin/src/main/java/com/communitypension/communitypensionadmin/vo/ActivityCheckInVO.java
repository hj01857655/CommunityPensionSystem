package com.communitypension.communitypensionadmin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动签到视图对象
 */
@Data
@Schema(description = "活动签到视图对象")
public class ActivityCheckInVO {
    
    /**
     * 签到ID
     */
    @Schema(description = "签到ID")
    private Long checkInId;
    
    /**
     * 活动ID
     */
    @Schema(description = "活动ID")
    private Long activityId;
    
    /**
     * 活动标题
     */
    @Schema(description = "活动标题")
    private String activityTitle;
    
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;
    
    /**
     * 用户姓名
     */
    @Schema(description = "用户姓名")
    private String userName;
    
    /**
     * 报名ID
     */
    @Schema(description = "报名ID")
    private Long participateId;
    
    /**
     * 签到时间
     */
    @Schema(description = "签到时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkInTime;
    
    /**
     * 签到状态：0-未签到，1-已签到
     */
    @Schema(description = "签到状态：0-未签到，1-已签到")
    private Integer status;
    
    /**
     * 签到状态名称
     */
    @Schema(description = "签到状态名称")
    private String statusName;
    
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
