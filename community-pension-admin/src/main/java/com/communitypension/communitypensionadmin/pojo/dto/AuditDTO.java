package com.communitypension.communitypensionadmin.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 审核DTO
 */
@Data
public class AuditDTO {
    
    /**
     * 审核状态：1-通过，2-拒绝
     */
    @NotNull(message = "审核状态不能为空")
    private Integer status;
    
    /**
     * 审核备注
     */
    private String remark;
}
