package com.communitypension.communitypensionadmin.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 登录请求DTO
 */
@Data
@Schema(description = "登录请求DTO")
public class LoginDTO {
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @NotNull(message = "角色ID不能为空")
    @Schema(description = "角色ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long roleId;
}