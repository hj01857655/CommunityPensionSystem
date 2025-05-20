package com.communitypension.communitypensionadmin.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 社区工作人员用户数据传输对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "社区工作人员用户DTO")
public class StaffDTO extends UserDTO {
    /**
     * 社区工作人员所在部门
     */
    @NotBlank(message = "部门不能为空")
    @Schema(description = "社区工作人员所在部门", required = true)
    private String department;

    /**
     * 社区工作人员岗位
     */
    @NotBlank(message = "岗位不能为空")
    @Schema(description = "社区工作人员岗位", required = true)
    private String position;
}
