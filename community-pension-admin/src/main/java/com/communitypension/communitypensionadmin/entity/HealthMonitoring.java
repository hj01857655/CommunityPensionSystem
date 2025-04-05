package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 健康监测实体类
 */
@Data
@TableName("health_monitoring")
public class HealthMonitoring {

    /**
     * 监测记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 老人ID
     */
    private Long elderId;

    /**
     * 监测类型(血压/血糖/心率等)
     */
    private String monitoringType;

    /**
     * 监测值
     */
    private String monitoringValue;

    /**
     * 监测单位
     */
    private String monitoringUnit;

    /**
     * 监测时间
     */
    private LocalDateTime monitoringTime;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 监测状态(normal/abnormal)
     */
    private String monitoringStatus;

    /**
     * 异常等级(low/medium/high)
     */
    private String abnormalLevel;

    /**
     * 异常描述
     */
    private String abnormalDescription;

    /**
     * 是否已处理
     */
    private Boolean isProcessed;

    /**
     * 处理时间
     */
    private LocalDateTime processedTime;

    /**
     * 处理人ID
     */
    private Long processedBy;

    /**
     * 处理结果
     */
    private String processedResult;

    /**
     * 关联的健康档案ID
     */
    private Long healthRecordId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
