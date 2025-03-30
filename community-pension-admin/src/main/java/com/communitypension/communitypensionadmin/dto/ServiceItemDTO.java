package com.communitypension.communitypensionadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 服务项目DTO
 */
@Data
public class ServiceItemDTO {
    
    /**
     * 服务ID
     */
    private Long serviceId;
    
    /**
     * 服务名称
     */
    @NotBlank(message = "服务名称不能为空")
    private String serviceName;
    
    /**
     * 服务类型
     */
    @NotBlank(message = "服务类型不能为空")
    private String serviceType;
    
    /**
     * 服务描述
     */
    private String description;
    
    /**
     * 服务价格
     */
    @NotNull(message = "服务价格不能为空")
    @Positive(message = "服务价格必须大于0")
    private BigDecimal price;
    
    /**
     * 状态（0下架/1上架）
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
    
    /**
     * 备注
     */
    private String remark;
} 