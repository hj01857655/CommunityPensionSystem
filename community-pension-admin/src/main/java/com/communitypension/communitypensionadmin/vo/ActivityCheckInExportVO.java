package com.communitypension.communitypensionadmin.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动签到导出VO
 */
@Data
public class ActivityCheckInExportVO {
    
    /**
     * 签到ID
     */
    private Long checkInId;
    
    /**
     * 活动标题
     */
    private String activityTitle;
    
    /**
     * 用户姓名
     */
    private String userName;
    
    /**
     * 用户手机号
     */
    private String userPhone;
    
    /**
     * 签到时间
     */
    private LocalDateTime checkInTime;
    
    /**
     * 状态
     */
    private String statusName;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
