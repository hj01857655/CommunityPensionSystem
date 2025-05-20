package com.communitypension.communitypensionadmin.pojo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 服务评价审核数据传输对象
 */
@Data
public class ServiceReviewAuditDTO {

    /**
     * 评价ID列表
     */
    @NotNull(message = "评价ID列表不能为空")
    private List<Long> ids;

    /**
     * 审核状态：1-通过，2-拒绝
     */
    @NotNull(message = "审核状态不能为空")
    private Integer status;

    /**
     * 审核备注
     */
    @Size(max = 200, message = "审核备注不能超过200个字符")
    private String remark;
}
