package com.communitypension.communitypensionadmin.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 老人家属绑定DTO
 */
@Data
public class ElderKinDTO {
    /**
     * 老人用户ID
     */
    @NotNull(message = "老人ID不能为空")
    private Long elderId;

    /**
     * 家属用户ID
     */
    @NotNull(message = "家属ID不能为空")
    private Long kinId;

    /**
     * 关系类型(父子/母女等)
     */
    @NotBlank(message = "关系类型不能为空")
    private String relationType;

    /**
     * 老人信息（可选，用于创建新用户时使用）
     */
    private String elderName;
    private String elderPhone;
    private String elderIdCard;
    private String elderHealthCondition;
    
    /**
     * 家属信息（可选，用于创建新用户时使用）
     */
    private String kinName;
    private String kinPhone;
    private String kinEmail;
    private String kinAddress;
}
