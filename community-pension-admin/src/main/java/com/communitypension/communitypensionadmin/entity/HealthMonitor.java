package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 健康监测实体类
 */
@Data
@TableName("health_monitor")
public class HealthMonitor {

    /**
     * 监测ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 老人ID
     */
    private Long elderId;

    /**
     * 监测类型：1-血压，2-血糖，3-体温，4-心率，5-血氧，6-体重，7-其他
     */
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
    private String otherValue;

    /**
     * 监测时间
     */
    private LocalDateTime monitorTime;

    /**
     * 监测结果：1-正常，2-偏高，3-偏低，4-异常
     */
    private Integer monitorResult;

    /**
     * 备注
     */
    private String remarks;

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

    /**
     * 是否已删除
     */
    @TableLogic
    private Integer isDeleted;
}
