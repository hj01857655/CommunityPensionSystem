package com.communitypension.communitypensionadmin.pojo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 服务预约DTO
 */
@Data
public class ServiceOrderDTO {
    
    /**
     * 预约ID（修改时必填）
     */
    private Long id;
    
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Min(value = 1, message = "用户ID必须大于0")
    private Long userId;
    
    /**
     * 服务项目ID
     */
    @NotNull(message = "服务项目ID不能为空")
    @Min(value = 1, message = "服务项目ID必须大于0")
    private Long serviceItemId;
    
    /**
     * 预约时间
     */
    @NotNull(message = "预约时间不能为空")
    @Future(message = "预约时间必须大于当前时间")
    private LocalDateTime scheduleTime;
    
    /**
     * 申请原因
     */
    @NotBlank(message = "申请原因不能为空")
    @Size(min = 5, max = 500, message = "申请原因长度必须在5-500个字符之间")
    private String applyReason;
    
    /**
     * 状态(0-待审核 1-已派单 2-服务中 3-已完成 4-已取消)
     */
    @Min(value = 0, message = "状态值不能小于0")
    @Max(value = 4, message = "状态值不能大于4")
    private Integer status;
    
    /**
     * 审核备注
     */
    @Size(max = 500, message = "审核备注长度不能超过500个字符")
    private String reviewRemark;
    
    /**
     * 实际时长（分钟）
     */
    @Min(value = 1, message = "实际时长必须大于0")
    @Max(value = 480, message = "实际时长不能超过8小时")
    private Integer actualDuration;
    
    /**
     * 实际费用
     */
    @DecimalMin(value = "0.0", message = "实际费用不能小于0")
    @DecimalMax(value = "99999.99", message = "实际费用不能超过99999.99")
    private Double actualFee;
    
    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
} 