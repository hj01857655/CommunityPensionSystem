package com.communitypension.communitypensionadmin.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 服务项目VO
 */
@Data
public class ServiceItemVO {
    
    /**
     * 服务项目ID
     */
    private Long serviceId;
    
    /**
     * 服务名称
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
     * 服务描述
     */
    private String description;
    
    /**
     * 服务价格
     */
    private BigDecimal price;
    
    /**
     * 服务时长（分钟）
     */
    private Integer duration;
    
    /**
     * 状态（0正常 1停用）
     */
    private String status;
    
    /**
     * 状态名称
     */
    private String statusName;
    
    /**
     * 创建者
     */
    private String createBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新者
     */
    private String updateBy;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 备注
     */
    private String remark;
} 