package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 活动签到实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("activity_check_in")
public class ActivityCheckIn extends BaseEntity {
    
    /**
     * 签到ID
     */
    @TableId(value = "check_in_id", type = IdType.AUTO)
    private Long checkInId;
    
    /**
     * 活动ID
     */
    private Long activityId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 报名ID
     */
    private Long participateId;
    
    /**
     * 签到时间
     */
    private LocalDateTime checkInTime;
    
    /**
     * 签到状态：0-未签到，1-已签到
     */
    private Integer status;
    
    /**
     * 备注
     */
    private String remark;
}
