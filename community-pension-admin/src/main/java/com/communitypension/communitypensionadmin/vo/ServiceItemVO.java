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
     * 服务ID
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
     * 状态（0下架/1上架）
     */
    private Integer status;
    
    /**
     * 状态名称
     */
    private String statusName;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 创建者
     */
    private String createBy;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 更新者
     */
    private String updateBy;
    
    /**
     * 备注
     */
    private String remark;
} 