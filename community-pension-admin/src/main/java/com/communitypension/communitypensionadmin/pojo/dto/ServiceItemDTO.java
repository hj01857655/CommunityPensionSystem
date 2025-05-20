package com.communitypension.communitypensionadmin.pojo.dto;

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
     * 服务项目ID（修改时必填）
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
    @NotBlank(message = "服务描述不能为空")
    private String description;
    
    /**
     * 服务价格
     */
    @NotNull(message = "服务价格不能为空")
    @Positive(message = "服务价格必须大于0")
    private BigDecimal price;
    
    /**
     * 服务时长（分钟）
     */
    @NotNull(message = "服务时长不能为空")
    @Positive(message = "服务时长必须大于0")
    private Integer duration;
    
    /**
     * 状态（0正常 1停用）
     */
    private String status;
    
    /**
     * 备注
     */
    private String remark;
} 