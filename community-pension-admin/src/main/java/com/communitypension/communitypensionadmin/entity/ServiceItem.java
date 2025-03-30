package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
     * 服务ID
     */
    @TableId(type = IdType.ASSIGN_ID)
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
     * 状态（0下架/1上架）
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 创建者
     */
    private String createBy;
    
    /**
     * 更新者
     */
    private String updateBy;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}