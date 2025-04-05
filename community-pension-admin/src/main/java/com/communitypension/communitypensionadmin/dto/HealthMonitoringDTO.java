package com.communitypension.communitypensionadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 健康监测数据传输对象
 */
@Data
public class HealthMonitoringDTO {

    /**
     * 监测记录ID（更新时使用）
     */
    private Long id;

    /**
     * 老人ID
     */
    @NotNull(message = "老人ID不能为空")
    private Long elderId;

    /**
     * 监测类型(血压/血糖/心率等)
     */
    @NotBlank(message = "监测类型不能为空")
    @Size(max = 50, message = "监测类型长度不能超过50个字符")
    private String monitoringType;

    /**
     * 监测值
     */
    @NotBlank(message = "监测值不能为空")
    @Size(max = 50, message = "监测值长度不能超过50个字符")
    private String monitoringValue;

    /**
     * 监测单位
     */
    @Size(max = 20, message = "监测单位长度不能超过20个字符")
    private String monitoringUnit;

    /**
     * 监测时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime monitoringTime;

    /**
     * 设备ID
     */
    @Size(max = 50, message = "设备ID长度不能超过50个字符")
    private String deviceId;

    /**
     * 监测状态(normal/abnormal)
     */
    @Size(max = 20, message = "监测状态长度不能超过20个字符")
    private String monitoringStatus;

    /**
     * 异常等级(low/medium/high)
     */
    @Size(max = 20, message = "异常等级长度不能超过20个字符")
    private String abnormalLevel;

    /**
     * 异常描述
     */
    @Size(max = 500, message = "异常描述长度不能超过500个字符")
    private String abnormalDescription;

    /**
     * 是否已处理
     */
    private Boolean isProcessed;

    /**
     * 处理时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processedTime;

    /**
     * 处理人ID
     */
    private Long processedBy;

    /**
     * 处理结果
     */
    @Size(max = 500, message = "处理结果长度不能超过500个字符")
    private String processedResult;

    /**
     * 关联的健康档案ID
     */
    private Long healthRecordId;
}
