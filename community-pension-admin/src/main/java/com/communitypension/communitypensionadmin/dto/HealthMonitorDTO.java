package com.communitypension.communitypensionadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 健康监测数据传输对象
 */
@Data
public class HealthMonitorDTO {

    /**
     * 监测ID（更新时使用）
     */
    private Long id;

    /**
     * 老人ID
     */
    @NotNull(message = "老人ID不能为空")
    private Long elderId;

    /**
     * 监测类型：1-血压，2-血糖，3-体温，4-心率，5-血氧，6-体重，7-其他
     */
    @NotNull(message = "监测类型不能为空")
    @Min(value = 1, message = "监测类型不正确")
    @Max(value = 7, message = "监测类型不正确")
    private Integer monitorType;

    /**
     * 收缩压（mmHg）
     */
    private Integer systolicPressure;

    /**
     * 舒张压（mmHg）
     */
    private Integer diastolicPressure;

    /**
     * 血糖值（mmol/L）
     */
    private Double bloodSugar;

    /**
     * 血糖类型：1-空腹，2-餐后，3-随机
     */
    private Integer bloodSugarType;

    /**
     * 体温（℃）
     */
    private Double temperature;

    /**
     * 心率（次/分）
     */
    private Integer heartRate;

    /**
     * 血氧饱和度（%）
     */
    private Integer bloodOxygen;

    /**
     * 体重（kg）
     */
    private Double weight;

    /**
     * 其他监测值
     */
    @Size(max = 100, message = "其他监测值长度不能超过100个字符")
    private String otherValue;

    /**
     * 监测时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime monitorTime;

    /**
     * 监测结果：1-正常，2-偏高，3-偏低，4-异常
     */
    @Min(value = 1, message = "监测结果不正确")
    @Max(value = 4, message = "监测结果不正确")
    private Integer monitorResult;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remarks;
}
