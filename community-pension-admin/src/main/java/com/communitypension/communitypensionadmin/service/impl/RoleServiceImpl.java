package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.Role;
import com.communitypension.communitypensionadmin.mapper.RoleMapper;
import com.communitypension.communitypensionadmin.mapper.RoleMenuMapper;
import com.communitypension.communitypensionadmin.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 角色服务实现类
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleMenuMapper roleMenuMapper;
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public Page<Role> selectRoleList(Role role, Page<Role> page) {
        return baseMapper.selectRoleList(role, page);
    }

    @Override
    public List<Role> selectRoleAll() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getStatus, "0")  // 状态正常的角色
               .orderByAsc(Role::getRoleSort);  // 按照角色排序号升序
        return list(wrapper);
    }

    @Override
    public List<String> selectRolesByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return roleMapper.selectRolesByUserId(userId);
    }

    @Override
    public Role selectRoleById(Long roleId) {
        if (roleId == null) {
            throw new IllegalArgumentException("角色ID不能为空");
        }
        return getById(roleId);
    }

    @Override
    public boolean checkRoleNameUnique(String roleName) {
        if (!StringUtils.hasText(roleName)) {
            return false;
        }
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleName, roleName);
        return count(wrapper) == 0;
    }

    @Override
    public boolean checkRoleKeyUnique(String roleKey) {
        if (!StringUtils.hasText(roleKey)) {
            return false;
        }
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleKey, roleKey);
        return count(wrapper) == 0;
    }

    @Override
    public void checkRoleAllowed(Role role) {
        if (role == null || role.getRoleId() == null) {
            return;
        }
        // 假设角色ID为1的是超级管理员角色
        if (role.getRoleId() == 1L) {
            throw new RuntimeException("不允许操作超级管理员角色");
        }
    }

    @Override
    public int countUserRoleByRoleId(Long roleId) {
        if (roleId == null) {
            throw new IllegalArgumentException("角色ID不能为空");
        }
        // 这里需要调用userRoleMapper查询关联数量
        return roleMenuMapper.countUserRoleByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("角色信息不能为空");
        }
        // 检查角色名称是否唯一
        if (!checkRoleNameUnique(role.getRoleName())) {
            throw new RuntimeException("角色名称已存在");
        }
        // 检查权限字符是否唯一
        if (!checkRoleKeyUnique(role.getRoleKey())) {
            throw new RuntimeException("角色权限已存在");
        }
        return save(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(Role role) {
        if (role == null || role.getRoleId() == null) {
            throw new IllegalArgumentException("角色信息不能为空");
        }
        // 检查是否允许操作
        checkRoleAllowed(role);
        
        // 检查角色名称是否唯一
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleName, role.getRoleName())
               .ne(Role::getRoleId, role.getRoleId());
        if (count(wrapper) > 0) {
            throw new RuntimeException("角色名称已存在");
        }
        
        // 检查权限字符是否唯一
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleKey, role.getRoleKey())
               .ne(Role::getRoleId, role.getRoleId());
        if (count(wrapper) > 0) {
            throw new RuntimeException("角色权限已存在");
        }
        
        return updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRoleStatus(Role role) {
        if (role == null || role.getRoleId() == null) {
            throw new IllegalArgumentException("角色信息不能为空");
        }
        // 检查是否允许操作
        checkRoleAllowed(role);
        return updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRoleByIds(String[] roleIds) {
        if (roleIds == null || roleIds.length == 0) {
            throw new IllegalArgumentException("角色ID不能为空");
        }
        
        // 转换为Long类型的roleId列表
        List<Long> roleIdList = Arrays.stream(roleIds)
                .map(Long::valueOf)
                .toList();
        
        // 检查是否允许删除
        roleIdList.forEach(roleId -> {
            Role role = new Role();
            role.setRoleId(roleId);
            checkRoleAllowed(role);
            
            // 检查角色是否已分配
            if (countUserRoleByRoleId(roleId) > 0) {
                throw new RuntimeException("角色已分配,不能删除");
            }
        });
        
        // 删除角色
        return removeBatchByIds(roleIdList);
    }
}
