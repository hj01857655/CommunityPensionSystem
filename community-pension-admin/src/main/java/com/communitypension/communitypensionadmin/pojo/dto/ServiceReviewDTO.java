package com.communitypension.communitypensionadmin.pojo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 服务评价数据传输对象
 */
@Data
public class ServiceReviewDTO {

    /**
     * 评价ID（更新时使用）
     */
    private Long id;

    /**
     * 关联订单ID (服务预约ID)
     */
    @NotNull(message = "预约ID不能为空")
    private Long orderId;

    /**
     * 服务ID
     */
    private Long serviceId;

    /**
     * 老人ID
     */
    @NotNull(message = "老人ID不能为空")
    private Long elderId;

    /**
     * 评价人ID（老人本人或家属）
     */
    @NotNull(message = "评价人ID不能为空")
    private Long reviewUserId;

    /**
     * 评价类型：0-老人自己评价，1-家属代评价
     */
    @NotNull(message = "评价类型不能为空")
    private Integer reviewType;

    /**
     * 评分（1-5星）
     */
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最小为1星")
    @Max(value = 5, message = "评分最大为5星")
    private Integer rating;

    /**
     * 评价内容
     */
    @Size(max = 500, message = "评价内容不能超过500个字符")
    private String content;

    /**
     * 是否匿名：0-否，1-是
     */
    private Integer isAnonymous = 0;
}
