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
    private Long id;

    /**
     * 报名ID
     */
    @Schema(description = "报名ID")
    private Long registerId;

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
     * 老人ID
     */
    @Schema(description = "老人ID")
    private Long elderId;

    /**
     * 老人姓名
     */
    @Schema(description = "老人姓名")
    private String elderName;

    /**
     * 签到人ID
     */
    @Schema(description = "签到人ID")
    private Long checkInUserId;

    /**
     * 签到人姓名
     */
    @Schema(description = "签到人姓名")
    private String checkInUserName;

    /**
     * 是否代签：0-本人签到，1-他人代签
     */
    @Schema(description = "是否代签：0-本人签到，1-他人代签")
    private Integer isProxyCheckIn;

    /**
     * 签到时间
     */
    @Schema(description = "签到时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime signInTime;

    /**
     * 签退时间
     */
    @Schema(description = "签退时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime signOutTime;

    /**
     * 备注信息
     */
    @Schema(description = "备注信息")
    private String remarks;

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
