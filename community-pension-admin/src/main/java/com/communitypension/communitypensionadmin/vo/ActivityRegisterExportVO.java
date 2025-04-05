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
     * 老人姓名
     */
    private String elderName;

    /**
     * 老人手机号
     */
    private String elderPhone;

    /**
     * 报名人姓名
     */
    private String registerUserName;

    /**
     * 报名类型名称
     */
    private String registerTypeName;

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
