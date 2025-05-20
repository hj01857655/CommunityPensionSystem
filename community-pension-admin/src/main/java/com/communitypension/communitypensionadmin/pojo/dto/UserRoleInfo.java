package com.communitypension.communitypensionadmin.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户角色信息DTO
 * 用于一次性获取用户的角色相关信息，减少数据库查询次数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleInfo {
    /**
     * 角色
     */
    private String role;
    
    /**
     * 角色ID
     */
    private Long roleId;
    
    /**
     * 角色名称
     */
    private String roleName;
}
