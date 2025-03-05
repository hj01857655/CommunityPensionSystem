package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("health_records")
public class HealthRecords {

    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 老人ID
     */
    @NotNull(message = "老人ID不能为空")
    private Long elderId;

    /**
     * 血压值
     */
    @NotBlank(message = "血压值不能为空")
    private String bloodPressure;

    /**
     * 心率
     */
    @NotNull(message = "心率不能为空")
    @Positive(message = "心率必须是正数")
    private Integer heartRate;

    /**
     * 血糖值 (mmol/L)
     */
    @NotNull(message = "血糖值不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "血糖值必须是正数")
    private BigDecimal bloodSugar;

    /**
     * 体温 (°C)
     */
    @NotNull(message = "体温不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "体温必须是正数")
    private BigDecimal temperature;

    /**
     * 体重 (kg)
     */
    @NotNull(message = "体重不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "体重必须是正数")
    private BigDecimal weight;

    /**
     * 身高 (cm)
     */
    @NotNull(message = "身高不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "身高必须是正数")
    private BigDecimal height;

    /**
     * 症状描述
     */
    private String symptoms;

    /**
     * 用药情况
     */
    private String medication;

    /**
     * 记录时间
     */
    @NotNull(message = "记录时间不能为空")
    private LocalDateTime recordTime;

    /**
     * 记录人ID
     */
    @NotNull(message = "记录人ID不能为空")
    private Long recorderId;

    /**
     * 记录类型 (体检记录, 用药记录, 症状记录)
     */
    private String recordType;

    /**
     * 症状记录时间
     */
    private LocalDateTime symptomsRecordTime;

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