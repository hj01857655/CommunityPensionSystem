package com.communitypension.communitypensionadmin.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 角色菜单权限分配的数据传输对象
 */
@Data
@Schema(description = "角色菜单权限分配DTO")
public class RoleMenuDTO {
    
    @Schema(description = "角色ID")
    private Long roleId;
    
    @Schema(description = "菜单ID列表")
    private List<Long> menuIds;
}