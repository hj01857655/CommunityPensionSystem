package com.communitypension.communitypensionadmin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "状态更新DTO")
public class StatusDTO {
    
    @Schema(description = "状态值")
    @NotNull(message = "状态值不能为空")
    private byte isActive;
} 