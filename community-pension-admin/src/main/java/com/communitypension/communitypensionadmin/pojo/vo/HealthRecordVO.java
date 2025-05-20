package com.communitypension.communitypensionadmin.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 健康档案视图对象
 */
@Data
@Schema(description = "健康档案视图对象")
public class HealthRecordVO {

    /**
     * 记录ID
     */
    @Schema(description = "记录ID")
    private Long id;

    /**
     * 老人ID
     */
    @Schema(description = "老人ID")
    private Long elderId;

    /**
     * 老人姓名
     */
    @Schema(description = "老人姓名")
    private String elderName;

    /**
     * 老人年龄
     */
    @Schema(description = "老人年龄")
    private Integer elderAge;

    /**
     * 老人性别
     */
    @Schema(description = "老人性别")
    private String elderGender;

    /**
     * 血压值
     */
    @Schema(description = "血压值")
    private String bloodPressure;

    /**
     * 心率
     */
    @Schema(description = "心率")
    private Integer heartRate;

    /**
     * 血糖值
     */
    @Schema(description = "血糖值")
    private BigDecimal bloodSugar;

    /**
     * 体温
     */
    @Schema(description = "体温")
    private BigDecimal temperature;

    /**
     * 体重(kg)
     */
    @Schema(description = "体重(kg)")
    private BigDecimal weight;

    /**
     * 身高(cm)
     */
    @Schema(description = "身高(cm)")
    private BigDecimal height;

    /**
     * BMI指数
     */
    @Schema(description = "BMI指数")
    private BigDecimal bmi;

    /**
     * 病史
     */
    @Schema(description = "病史")
    private String medicalHistory;

    /**
     * 过敏史
     */
    @Schema(description = "过敏史")
    private String allergy;

    /**
     * 症状描述
     */
    @Schema(description = "症状描述")
    private String symptoms;

    /**
     * 症状记录时间
     */
    @Schema(description = "症状记录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime symptomsRecordTime;

    /**
     * 用药情况
     */
    @Schema(description = "用药情况")
    private String medication;

    /**
     * 记录时间
     */
    @Schema(description = "记录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recordTime;

    /**
     * 记录人ID
     */
    @Schema(description = "记录人ID")
    private Long recorderId;

    /**
     * 记录人姓名
     */
    @Schema(description = "记录人姓名")
    private String recorderName;

    /**
     * 记录类型
     */
    @Schema(description = "记录类型")
    private String recordType;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remarks;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
