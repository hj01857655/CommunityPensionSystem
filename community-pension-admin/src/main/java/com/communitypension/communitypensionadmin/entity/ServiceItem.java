package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 服务项目实体类
 */
@Data
@TableName("service_item")
public class ServiceItem {
    
    /**
     * 服务项目ID
     */
    @TableId(type = IdType.AUTO)
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
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 备注
     */
    private String remark;
}