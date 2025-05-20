package com.communitypension.communitypensionadmin.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 健康预警VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "健康预警VO")
public class HealthAlertVO {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
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
     * 预警类型
     */
    @Schema(description = "预警类型")
    private Integer alertType;

    /**
     * 预警类型名称
     */
    @Schema(description = "预警类型名称")
    private String alertTypeName;

    /**
     * 预警级别
     */
    @Schema(description = "预警级别")
    private Integer alertLevel;

    /**
     * 预警级别名称
     */
    @Schema(description = "预警级别名称")
    private String alertLevelName;

    /**
     * 预警来源
     */
    @Schema(description = "预警来源")
    private Integer alertSource;

    /**
     * 预警来源名称
     */
    @Schema(description = "预警来源名称")
    private String alertSourceName;

    /**
     * 预警标题
     */
    @Schema(description = "预警标题")
    private String title;

    /**
     * 预警内容
     */
    @Schema(description = "预警内容")
    private String content;

    /**
     * 预警时间
     */
    @Schema(description = "预警时间")
    private LocalDateTime alertTime;

    /**
     * 处理状态
     */
    @Schema(description = "处理状态")
    private Integer status;

    /**
     * 处理状态名称
     */
    @Schema(description = "处理状态名称")
    private String statusName;

    /**
     * 处理人ID
     */
    @Schema(description = "处理人ID")
    private Long handlerId;

    /**
     * 处理人姓名
     */
    @Schema(description = "处理人姓名")
    private String handlerName;

    /**
     * 处理时间
     */
    @Schema(description = "处理时间")
    private LocalDateTime handleTime;

    /**
     * 处理备注
     */
    @Schema(description = "处理备注")
    private String handleNote;

    /**
     * 相关数据ID
     */
    @Schema(description = "相关数据ID")
    private Long relatedDataId;

    /**
     * 是否已通知
     */
    @Schema(description = "是否已通知")
    private Integer notified;

    /**
     * 通知时间
     */
    @Schema(description = "通知时间")
    private LocalDateTime notifyTime;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
} 