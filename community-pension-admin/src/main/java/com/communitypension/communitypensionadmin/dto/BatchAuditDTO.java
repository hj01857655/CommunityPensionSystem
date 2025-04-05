package com.communitypension.communitypensionadmin.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 批量审核DTO
 */
@Data
public class BatchAuditDTO {
    
    /**
     * 报名ID列表
     */
    @NotEmpty(message = "报名ID列表不能为空")
    private List<Long> ids;
    
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
