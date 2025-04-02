package com.communitypension.communitypensionadmin.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 服务预约VO
 */
@Data
public class ServiceOrderVO {
    
    /**
     * 预约ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户姓名
     */
    private String userName;
    
    /**
     * 服务项目ID
     */
    private Long serviceItemId;
    
    /**
     * 服务项目名称
     */
    private String serviceName;
    
    /**
     * 服务类型
     */
    private String serviceType;
    
    /**
     * 服务类型名称
     */
    private String serviceTypeName;
    
    /**
     * 预约时间
     */
    private LocalDateTime scheduleTime;
    
    /**
     * 申请原因
     */
    private String applyReason;
    
    /**
     * 状态(0-待审核 1-已派单 2-服务中 3-已完成 4-已取消)
     */
    private Integer status;
    
    /**
     * 状态名称
     */
    private String statusName;
    
    /**
     * 审核备注
     */
    private String reviewRemark;
    
    /**
     * 实际服务时长(分钟)
     */
    private Integer actualDuration;
    
    /**
     * 实际费用
     */
    private Double actualFee;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 