package com.communitypension.communitypensionadmin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户简单信息DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSimpleDTO {
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 关系类型（可选）
     */
    private String relationType;
}
