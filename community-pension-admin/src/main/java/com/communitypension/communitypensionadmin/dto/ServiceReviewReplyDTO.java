package com.communitypension.communitypensionadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 服务评价回复数据传输对象
 */
@Data
public class ServiceReviewReplyDTO {

    /**
     * 评价ID
     */
    @NotNull(message = "评价ID不能为空")
    private Long reviewId;

    /**
     * 回复内容
     */
    @NotBlank(message = "回复内容不能为空")
    @Size(max = 500, message = "回复内容不能超过500个字符")
    private String reply;

    /**
     * 回复管理员ID
     */
    @NotNull(message = "回复管理员ID不能为空")
    private Long adminId;
}
