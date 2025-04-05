package com.communitypension.communitypensionadmin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户查询参数DTO
 */
@Data
@Schema(description = "用户查询参数")
public class UserQueryDTO {
    
    @Schema(description = "当前页码", defaultValue = "1")
    private Integer current = 1;
    
    @Schema(description = "每页条数", defaultValue = "10")
    private Integer size = 10;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "是否激活")
    private Integer isActive;
    
    @Schema(description = "开始时间")
    private String startTime;
    
    @Schema(description = "结束时间")
    private String endTime;
} 