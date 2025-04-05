package com.communitypension.communitypensionadmin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 健康监测视图对象
 */
@Data
@Schema(description = "健康监测视图对象")
public class HealthMonitoringVO {

    /**
     * 监测记录ID
     */
    @Schema(description = "监测记录ID")
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
     * 监测类型(血压/血糖/心率等)
     */
    @Schema(description = "监测类型(血压/血糖/心率等)")
    private String monitoringType;

    /**
     * 监测值
     */
    @Schema(description = "监测值")
    private String monitoringValue;

    /**
     * 监测单位
     */
    @Schema(description = "监测单位")
    private String monitoringUnit;

    /**
     * 监测时间
     */
    @Schema(description = "监测时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime monitoringTime;

    /**
     * 设备ID
     */
    @Schema(description = "设备ID")
    private String deviceId;

    /**
     * 监测状态(normal/abnormal)
     */
    @Schema(description = "监测状态(normal/abnormal)")
    private String monitoringStatus;

    /**
     * 异常等级(low/medium/high)
     */
    @Schema(description = "异常等级(low/medium/high)")
    private String abnormalLevel;

    /**
     * 异常描述
     */
    @Schema(description = "异常描述")
    private String abnormalDescription;

    /**
     * 是否已处理
     */
    @Schema(description = "是否已处理")
    private Boolean isProcessed;

    /**
     * 处理时间
     */
    @Schema(description = "处理时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processedTime;

    /**
     * 处理人ID
     */
    @Schema(description = "处理人ID")
    private Long processedBy;

    /**
     * 处理人姓名
     */
    @Schema(description = "处理人姓名")
    private String processedByName;

    /**
     * 处理结果
     */
    @Schema(description = "处理结果")
    private String processedResult;

    /**
     * 关联的健康档案ID
     */
    @Schema(description = "关联的健康档案ID")
    private Long healthRecordId;

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
