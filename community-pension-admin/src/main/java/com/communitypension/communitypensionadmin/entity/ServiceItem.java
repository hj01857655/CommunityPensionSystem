package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 *  服务项目表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("service_item")
public class ServiceItem extends BaseEntity {
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
     * 服务描述
     */
    private String serviceDescription;
    /**
     * 服务价格（单位：元）
     */
    private BigDecimal servicePrice;
    /**
     * 服务时长（单位：小时）
     */
    private Integer serviceDuration;

    
    /**
     * 状态（0正常 1停用）
     */
    private String status;

    

}