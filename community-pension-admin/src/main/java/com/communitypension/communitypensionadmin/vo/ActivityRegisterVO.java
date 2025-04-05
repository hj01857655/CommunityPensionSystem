package com.communitypension.communitypensionadmin.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 活动报名VO
 */
@Data
public class ActivityRegisterVO {

    /**
     * 报名ID
     */
    private Long id;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 活动标题
     */
    private String activityTitle;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 报名时间
     */
    private LocalDateTime registerTime;

    /**
     * 签到时间
     */
    private LocalDateTime checkInTime;

    /**
     * 状态：0-待审核，1-已通过，2-已拒绝，3-已取消，4-已签到
     */
    private Integer status;

    /**
     * 状态名称
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
