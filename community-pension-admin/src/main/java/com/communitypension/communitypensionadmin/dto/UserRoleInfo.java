package com.communitypension.communitypensionadmin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户角色信息DTO
 * 用于一次性获取用户的所有角色相关信息，减少数据库查询次数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleInfo {
    /**
     * 角色列表
     */
    private List<String> roles;
    
    /**
     * 角色ID列表
     */
    private List<Long> roleIds;
    
    /**
     * 角色名称列表
     */
    private List<String> roleNames;
}
