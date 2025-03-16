package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.entity.Permission;

import java.util.List;
import java.util.Map;

public interface PermissionService extends IService<Permission> {
    /**
     * 分页获取权限列表
     * @param page 页码
     * @param size 每页数量
     * @param query 查询条件
     * @return 权限列表分页数据
     */
    IPage<Permission> getPermissionsByPage(int page, int size, String query);
    
    /**
     * 添加权限
     * @param permission 权限信息
     * @return 添加后的权限信息
     */
    Permission addPermission(Permission permission);
    
    /**
     * 更新权限
     * @param permission 权限信息
     * @return 更新后的权限信息
     */
    Permission updatePermission(Permission permission);
    
    /**
     * 删除权限
     * @param id 权限ID
     * @return 是否删除成功
     */
    boolean deletePermission(Long id);
    
    /**
     * 获取权限树
     * @return 权限树结构
     */
    List<Map<String, Object>> getPermissionTree();
    
    /**
     * 获取权限分类列表
     * @return 权限分类列表
     */
    List<Map<String, Object>> getPermissionCategories();
    
    /**
     * 获取角色的权限列表
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<Permission> getRolePermissions(Long roleId);
    
    /**
     * 为角色分配权限
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @return 是否分配成功
     */
    boolean assignRolePermissions(Long roleId, List<Long> permissionIds);
}