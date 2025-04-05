package com.communitypension.communitypensionadmin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 活动报名DTO
 */
@Data
public class ActivityRegisterDTO {

    /**
     * 报名ID
     */
    private Long id;

    /**
     * 活动ID
     */
    @NotNull(message = "活动ID不能为空")
    private Long activityId;

    /**
     * 老人ID
     */
    @NotNull(message = "老人ID不能为空")
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
     * 状态：0-待审核，1-已通过，2-已拒绝，3-已取消
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
