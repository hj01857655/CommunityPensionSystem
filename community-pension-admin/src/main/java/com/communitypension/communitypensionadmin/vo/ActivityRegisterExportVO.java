package com.communitypension.communitypensionadmin.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动报名导出VO
 */
@Data
public class ActivityRegisterExportVO {
    
    /**
     * 报名ID
     */
    private Long id;
    
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
     * 报名时间
     */
    private LocalDateTime registerTime;
    
    /**
     * 状态
     */
    private String statusName;
    
    /**
     * 签到时间
     */
    private LocalDateTime checkInTime;
    
    /**
     * 备注
     */
    private String remark;
}
