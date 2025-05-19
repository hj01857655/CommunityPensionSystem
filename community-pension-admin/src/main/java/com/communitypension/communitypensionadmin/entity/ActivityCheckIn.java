package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动签到实体类
 */
@Data
@TableName("activity_check_in")
public class ActivityCheckIn {

    /**
     * 签到ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 报名ID
     */
    private Long registerId;
    
    /**
     * 活动ID
     */
    private Long activityId;
    
    /**
     * 老人ID
     */
    private Long elderId;

    /**
     * 签到人ID（老人本人或家属）
     */
    private Long checkInUserId;

    /**
     * 是否代签：0-本人签到，1-他人代签
     */
    private Integer isProxyCheckIn;

    /**
     * 签到时间
     */
    private LocalDateTime signInTime;

    /**
     * 签退时间
     */
    private LocalDateTime signOutTime;

    /**
     * 备注信息
     */
    private String remarks;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 是否已删除
     */
    @TableLogic
    private Integer isDeleted;
}
