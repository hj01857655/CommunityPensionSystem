package com.communitypension.communitypensionadmin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 活动签到DTO
 */
@Data
public class ActivityCheckInDTO {
    
    /**
     * 报名ID列表
     */
    @NotNull(message = "报名ID列表不能为空")
    private List<Long> ids;
    
    /**
     * 备注
     */
    private String remark;
}
