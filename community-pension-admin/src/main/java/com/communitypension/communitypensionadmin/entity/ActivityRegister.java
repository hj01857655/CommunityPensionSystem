package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 活动报名实体类
 */
@Data
@TableName("activity_register")
public class ActivityRegister {

    /**
     * 报名ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 老人ID
     */
    private Long elderId;

    /**
     * 报名人ID（老人本人或家属）
     */
    private Long registerUserId;

    /**
     * 报名类型：0-老人自己报名，1-家属代报名
     */
    private Integer registerType;

    /**
     * 报名时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime registerTime;

    /**
     * 状态：0-待审核，1-已通过，2-已拒绝，3-已取消，4-已签到
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

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
