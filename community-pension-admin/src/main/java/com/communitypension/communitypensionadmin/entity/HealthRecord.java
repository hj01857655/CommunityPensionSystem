package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 健康档案实体类
 */
@Data
@TableName("health_records")
public class HealthRecord {

    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 老人ID
     */
    private Long elderId;

    /**
     * 血压值
     */
    private String bloodPressure;

    /**
     * 心率
     */
    private Integer heartRate;

    /**
     * 血糖值
     */
    private BigDecimal bloodSugar;

    /**
     * 体温
     */
    private BigDecimal temperature;

    /**
     * 体重(kg)
     */
    private BigDecimal weight;

    /**
     * 身高(cm)
     */
    private BigDecimal height;

    /**
     * BMI指数
     */
    private BigDecimal bmi;

    /**
     * 病史
     */
    private String medicalHistory;

    /**
     * 过敏史
     */
    private String allergy;

    /**
     * 症状描述
     */
    private String symptoms;

    /**
     * 症状记录时间
     */
    private LocalDateTime symptomsRecordTime;

    /**
     * 用药情况
     */
    private String medication;

    /**
     * 记录时间
     */
    private LocalDateTime recordTime;

    /**
     * 记录人ID
     */
    private Long recorderId;

    /**
     * 记录类型
     */
    private String recordType;

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
