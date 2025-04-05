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
     * 老人ID
     */
    private Long elderId;

    /**
     * 老人姓名
     */
    private String elderName;

    /**
     * 报名人ID（老人本人或家属）
     */
    private Long registerUserId;

    /**
     * 报名人姓名
     */
    private String registerUserName;

    /**
     * 报名类型：0-老人自己报名，1-家属代报名
     */
    private Integer registerType;

    /**
     * 报名类型名称
     */
    private String registerTypeName;

    /**
     * 报名时间
     */
    private LocalDateTime registerTime;

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

    /**
     * 是否已签到
     */
    private Boolean hasCheckedIn;
}
