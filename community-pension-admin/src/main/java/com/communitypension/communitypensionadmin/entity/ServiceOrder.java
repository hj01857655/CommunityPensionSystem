package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.communitypension.communitypensionadmin.enums.ServiceOrderStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("service_order")


public class ServiceOrder extends BaseEntity {
    /**
     * 服务工单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 服务项目ID
     */
    private Long serviceItemId;
    
    /**
     * 状态
     */
    private ServiceOrderStatus status;
    /**
     * 申请原因
     */
    private String applyReason;
    /**
     * 审核备注
     */
    private String reviewRemark;
    /**
     * 预约时间
     */
    private LocalDateTime scheduleTime;
    /**
     * 实际时长
     */
    @TableField("actual_duration")
    private Integer actualDuration;
    /**
     * 实际费用
     */
    @TableField("actual_fee")
    private Double actualFee;
    /**
     * 实际费用
     */
    @TableField(exist = false)
    private String userName;
    
    @TableField(exist = false)
    private String serviceName;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField("create_by")
    private String createBy;
} 