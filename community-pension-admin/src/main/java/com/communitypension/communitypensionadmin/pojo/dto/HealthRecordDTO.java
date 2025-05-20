package com.communitypension.communitypensionadmin.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 健康档案数据传输对象
 */
@Data
public class HealthRecordDTO {

    /**
     * 记录ID（更新时使用）
     */
    private Long id;

    /**
     * 老人ID
     */
    @NotNull(message = "老人ID不能为空")
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
    @Size(max = 500, message = "病史长度不能超过500个字符")
    private String medicalHistory;

    /**
     * 过敏史
     */
    @Size(max = 500, message = "过敏史长度不能超过500个字符")
    private String allergy;

    /**
     * 症状描述
     */
    @Size(max = 500, message = "症状描述长度不能超过500个字符")
    private String symptoms;

    /**
     * 症状记录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime symptomsRecordTime;

    /**
     * 用药情况
     */
    @Size(max = 500, message = "用药情况长度不能超过500个字符")
    private String medication;

    /**
     * 记录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remarks;
}
