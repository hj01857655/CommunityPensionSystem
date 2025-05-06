package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 服务评价实体类
 */
@Data
@TableName("service_review")
public class ServiceReview {

    /**
     * 评价ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 服务预约ID
     */
    private Long serviceAppointmentId;

    /**
     * 服务ID
     */
    private Long serviceId;

    /**
     * 老人ID
     */
    private Long elderId;

    /**
     * 评价人ID（老人本人或家属）
     */
    private Long reviewUserId;

    /**
     * 评价类型：0-老人自己评价，1-家属代评价
     */
    private Integer reviewType;

    /**
     * 评分（1-5星）
     */
    private Integer rating;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评价时间
     */
    private LocalDateTime reviewTime;

    /**
     * 是否匿名：0-否，1-是
     */
    private Integer isAnonymous;

    /**
     * 管理员回复
     */
    private String adminReply;

    /**
     * 回复时间
     */
    private LocalDateTime replyTime;

    /**
     * 回复管理员ID
     */
    private Long replyAdminId;

    /**
     * 状态：0-待审核，1-已通过，2-已拒绝
     */
    private Integer status;

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
     * 是否已删除 (逻辑删除字段)
     */
    @TableLogic
    private Integer deleted;
}
