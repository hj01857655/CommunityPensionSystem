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
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /**
     * 状态：0-待审核，1-已通过，2-已拒绝，3-已取消
     */
    private Integer status;
    
    /**
     * 备注
     */
    private String remark;
}
