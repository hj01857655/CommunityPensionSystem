package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.entity.Role;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService extends IService<Role> {

    /**
     * 超级管理员角色ID
     */
    Long ADMIN_ROLE_ID = 4L;

    /**
     * 根据条件分页查询角色列表
     * @param role 角色
     * @return 角色列表
     */
    Page<Role> selectRoleList(Role role, Page<Role> page);
    /**
     * 查询所有角色
     * @return 角色列表
     */
    List<Role> selectRoleAll();

    /**
     * 根据用户ID查询角色选择框列表
     * @param userId 用户id
     * @return 角色
     */
    List<String> selectRoleListByUserId(Long userId);

    /**
     * 根据角色ID查询角色
     * @param roleId 角色ID
     * @return 角色
     */
    Role selectRoleById(Long roleId);

    /**
     * 检查角色名称是否可用
     * @param roleName 角色名称
     * @return true表示名称可用（不存在），false表示名称已存在
     */
    boolean checkRoleNameUnique(String roleName);

    /**
     * 校验角色权限是否唯一
     * @param roleKey 角色权限
     * @return 结果
     */
    boolean checkRoleKeyUnique(String roleKey);

    /**
     * 校验角色是否允许操作
     * @param role 角色信息
     */
    void checkRoleAllowed(Role role);

    /**
     * 通过角色ID查询角色使用数量
     * @param roleId 角色ID
     * @return 结果
     */
    int countUserRoleByRoleId(Long roleId);

    /**
     * 新增保存角色信息
     * @param role 角色信息
     * @return 结果
     */
    boolean insertRole(Role role);

    /**
     * 修改保存角色信息
     * @param role 角色信息
     * @return 结果
     */
    boolean updateRole(Role role);

    /**
     * 修改角色状态
     * @param role 角色信息
     * @return 结果
     */
    boolean updateRoleStatus(Role role);

    /**
     * 批量删除角色信息
     * @param roleIdList 需要删除的角色ID
     * @return 结果
     */
    boolean deleteRoleByIdList(String[] roleIdList);




}
