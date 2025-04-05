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
public class HealthMonitorVO {

    /**
     * 监测ID
     */
    @Schema(description = "监测ID")
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
     * 监测类型：1-血压，2-血糖，3-体温，4-心率，5-血氧，6-体重，7-其他
     */
    @Schema(description = "监测类型：1-血压，2-血糖，3-体温，4-心率，5-血氧，6-体重，7-其他")
    private Integer monitorType;

    /**
     * 监测类型名称
     */
    @Schema(description = "监测类型名称")
    private String monitorTypeName;

    /**
     * 收缩压（mmHg）
     */
    @Schema(description = "收缩压（mmHg）")
    private Integer systolicPressure;

    /**
     * 舒张压（mmHg）
     */
    @Schema(description = "舒张压（mmHg）")
    private Integer diastolicPressure;

    /**
     * 血压值（格式化）
     */
    @Schema(description = "血压值（格式化）")
    private String bloodPressureFormatted;

    /**
     * 血糖值（mmol/L）
     */
    @Schema(description = "血糖值（mmol/L）")
    private Double bloodSugar;

    /**
     * 血糖类型：1-空腹，2-餐后，3-随机
     */
    @Schema(description = "血糖类型：1-空腹，2-餐后，3-随机")
    private Integer bloodSugarType;

    /**
     * 血糖类型名称
     */
    @Schema(description = "血糖类型名称")
    private String bloodSugarTypeName;

    /**
     * 体温（℃）
     */
    @Schema(description = "体温（℃）")
    private Double temperature;

    /**
     * 心率（次/分）
     */
    @Schema(description = "心率（次/分）")
    private Integer heartRate;

    /**
     * 血氧饱和度（%）
     */
    @Schema(description = "血氧饱和度（%）")
    private Integer bloodOxygen;

    /**
     * 体重（kg）
     */
    @Schema(description = "体重（kg）")
    private Double weight;

    /**
     * 其他监测值
     */
    @Schema(description = "其他监测值")
    private String otherValue;

    /**
     * 监测时间
     */
    @Schema(description = "监测时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime monitorTime;

    /**
     * 监测结果：1-正常，2-偏高，3-偏低，4-异常
     */
    @Schema(description = "监测结果：1-正常，2-偏高，3-偏低，4-异常")
    private Integer monitorResult;

    /**
     * 监测结果名称
     */
    @Schema(description = "监测结果名称")
    private String monitorResultName;

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
