package com.communitypension.communitypensionadmin.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 健康预警DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "健康预警DTO")
public class HealthAlertDTO {

    /**
     * 主键ID（更新时需要）
     */
    @Schema(description = "主键ID（更新时需要）")
    private Long id;

    /**
     * 老人ID
     */
    @NotNull(message = "老人ID不能为空")
    @Schema(description = "老人ID", required = true)
    private Long elderId;

    /**
     * 预警类型
     * 1: 血压异常
     * 2: 血糖异常
     * 3: 心率异常
     * 4: 体温异常
     * 5: 活动异常
     * 6: 用药提醒
     * 7: 复查提醒
     * 8: 其他异常
     */
    @NotNull(message = "预警类型不能为空")
    @Schema(description = "预警类型", required = true)
    private Integer alertType;

    /**
     * 预警级别
     * 1: 低
     * 2: 中
     * 3: 高
     * 4: 紧急
     */
    @NotNull(message = "预警级别不能为空")
    @Schema(description = "预警级别", required = true)
    private Integer alertLevel;

    /**
     * 预警来源
     * 1: 系统自动监测
     * 2: 穿戴设备
     * 3: 人工录入
     * 4: 定期检查
     */
    @NotNull(message = "预警来源不能为空")
    @Schema(description = "预警来源", required = true)
    private Integer alertSource;

    /**
     * 预警标题
     */
    @NotNull(message = "预警标题不能为空")
    @Schema(description = "预警标题", required = true)
    private String title;

    /**
     * 预警内容
     */
    @NotNull(message = "预警内容不能为空")
    @Schema(description = "预警内容", required = true)
    private String content;

    /**
     * 预警时间
     */
    @Schema(description = "预警时间")
    private LocalDateTime alertTime;

    /**
     * 处理状态
     * 0: 未处理
     * 1: 已处理
     * 2: 已忽略
     */
    @Schema(description = "处理状态")
    private Integer status;

    /**
     * 处理备注
     */
    @Schema(description = "处理备注")
    private String handleNote;

    /**
     * 相关数据ID(如监测记录ID)
     */
    @Schema(description = "相关数据ID")
    private Long relatedDataId;

    /**
     * 是否需要立即发送通知
     */
    @Schema(description = "是否需要立即发送通知")
    private Boolean sendNotification;
} 