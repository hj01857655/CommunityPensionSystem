package com.communitypension.communitypensionadmin.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 服务评价视图对象
 */
@Data
@Schema(description = "服务评价视图对象")
public class ServiceReviewVO {

    /**
     * 评价ID
     */
    @Schema(description = "评价ID")
    private Long id;

    /**
     * 服务预约ID
     */
    @Schema(description = "服务预约ID")
    private Long serviceAppointmentId;

    /**
     * 服务ID
     */
    @Schema(description = "服务ID")
    private Long serviceId;

    /**
     * 服务名称
     */
    @Schema(description = "服务名称")
    private String serviceName;

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
     * 评价人ID
     */
    @Schema(description = "评价人ID")
    private Long reviewUserId;

    /**
     * 评价人姓名
     */
    @Schema(description = "评价人姓名")
    private String reviewUserName;

    /**
     * 评价类型：0-老人自己评价，1-家属代评价
     */
    @Schema(description = "评价类型：0-老人自己评价，1-家属代评价")
    private Integer reviewType;

    /**
     * 评价类型名称
     */
    @Schema(description = "评价类型名称")
    private String reviewTypeName;

    /**
     * 评分（1-5星）
     */
    @Schema(description = "评分（1-5星）")
    private Integer rating;

    /**
     * 评价内容
     */
    @Schema(description = "评价内容")
    private String content;

    /**
     * 评价时间
     */
    @Schema(description = "评价时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewTime;

    /**
     * 是否匿名：0-否，1-是
     */
    @Schema(description = "是否匿名：0-否，1-是")
    private Integer isAnonymous;

    /**
     * 管理员回复
     */
    @Schema(description = "管理员回复")
    private String adminReply;

    /**
     * 回复时间
     */
    @Schema(description = "回复时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime replyTime;

    /**
     * 回复管理员ID
     */
    @Schema(description = "回复管理员ID")
    private Long replyAdminId;

    /**
     * 回复管理员姓名
     */
    @Schema(description = "回复管理员姓名")
    private String replyAdminName;

    /**
     * 状态：0-待审核，1-已通过，2-已拒绝
     */
    @Schema(description = "状态：0-待审核，1-已通过，2-已拒绝")
    private Integer status;

    /**
     * 状态名称
     */
    @Schema(description = "状态名称")
    private String statusName;

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
