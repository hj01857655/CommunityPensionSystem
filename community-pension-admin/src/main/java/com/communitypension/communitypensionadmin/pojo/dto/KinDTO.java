package com.communitypension.communitypensionadmin.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 家属用户数据传输对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "家属用户DTO")
public class KinDTO extends UserDTO {
    /**
     * 要绑定的老人ID列表
     */
    @Schema(description = "要绑定的老人ID列表")
    private List<Long> bindElderIds;

    /**
     * 与老人的关系类型
     */
    @NotBlank(message = "与老人的关系类型不能为空")
    @Schema(description = "与老人的关系类型", required = true)
    private String relationType;
}
