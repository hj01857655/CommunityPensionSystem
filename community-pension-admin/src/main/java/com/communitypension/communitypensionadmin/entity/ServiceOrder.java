package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
     * 0-待审核，1-已派单，2-服务中，3-已完成，4-已取消，5-已拒绝
     */
    private Integer status;
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
    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String serviceName;
}