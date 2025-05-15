package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 健康预警实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("health_alert")
public class HealthAlert {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 老人ID
     */
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
    private Integer alertType;

    /**
     * 预警级别
     * 1: 低
     * 2: 中
     * 3: 高
     * 4: 紧急
     */
    private Integer alertLevel;

    /**
     * 预警来源
     * 1: 系统自动监测
     * 2: 穿戴设备
     * 3: 人工录入
     * 4: 定期检查
     */
    private Integer alertSource;

    /**
     * 预警标题
     */
    private String title;

    /**
     * 预警内容
     */
    private String content;

    /**
     * 预警时间
     */
    private LocalDateTime alertTime;

    /**
     * 处理状态
     * 0: 未处理
     * 1: 已处理
     * 2: 已忽略
     */
    private Integer status;

    /**
     * 处理人ID
     */
    private Long handlerId;

    /**
     * 处理时间
     */
    private LocalDateTime handleTime;

    /**
     * 处理备注
     */
    private String handleNote;

    /**
     * 相关数据ID(如监测记录ID)
     */
    private Long relatedDataId;

    /**
     * 是否已通知
     * 0: 未通知
     * 1: 已通知
     */
    private Integer notified;

    /**
     * 通知时间
     */
    private LocalDateTime notifyTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 