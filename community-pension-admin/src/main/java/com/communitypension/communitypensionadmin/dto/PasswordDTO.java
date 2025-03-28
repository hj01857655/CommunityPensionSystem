package com.communitypension.communitypensionadmin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "密码修改DTO")
public class PasswordDTO {
    
    @Schema(description = "旧密码")
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;
    
    @Schema(description = "新密码")
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
    
    @Schema(description = "确认密码")
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
} 