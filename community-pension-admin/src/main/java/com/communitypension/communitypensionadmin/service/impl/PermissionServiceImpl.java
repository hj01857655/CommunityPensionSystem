package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.Permission;
import com.communitypension.communitypensionadmin.mapper.PermissionMapper;
import com.communitypension.communitypensionadmin.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public IPage<Permission> getPermissionsByPage(int page, int size, String query) {
        Page<Permission> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(query)) {
            queryWrapper.like(Permission::getName, query)
                    .or()
                    .like(Permission::getCode, query)
                    .or()
                    .like(Permission::getCategory, query);
        }
        
        queryWrapper.orderByAsc(Permission::getSort);
        return page(pageParam, queryWrapper);
    }

    @Override
    @Transactional
    public Permission addPermission(Permission permission) {
        permission.setCreatedAt(Instant.now());
        permission.setUpdatedAt(Instant.now());
        save(permission);
        return permission;
    }

    @Override
    @Transactional
    public Permission updatePermission(Permission permission) {
        permission.setUpdatedAt(Instant.now());
        updateById(permission);
        return permission;
    }

    @Override
    @Transactional
    public boolean deletePermission(Long id) {
        // 检查是否有子权限
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Permission::getParentId, id);
        long count = count(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("该权限下有子权限，无法删除");
        }
        
        return removeById(id);
    }

    @Override
    public List<Map<String, Object>> getPermissionTree() {
        // 获取所有权限
        List<Permission> allPermissions = list();
        
        // 构建树形结构
        List<Map<String, Object>> result = buildTree(allPermissions, 0L);
        
        return result;
    }
    
    private List<Map<String, Object>> buildTree(List<Permission> permissions, Long parentId) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        List<Permission> children = permissions.stream()
                .filter(p -> (p.getParentId() == null && parentId == 0) || 
                        (p.getParentId() != null && p.getParentId().equals(parentId)))
                .collect(Collectors.toList());
        
        for (Permission permission : children) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", permission.getId());
            node.put("name", permission.getName());
            node.put("code", permission.getCode());
            node.put("category", permission.getCategory());
            node.put("description", permission.getDescription());
            node.put("status", permission.getStatus());
            
            List<Map<String, Object>> childNodes = buildTree(permissions, permission.getId());
            if (!childNodes.isEmpty()) {
                node.put("children", childNodes);
            }
            
            result.add(node);
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getPermissionCategories() {
        // 获取所有权限分类
        List<String> categories = list().stream()
                .map(Permission::getCategory)
                .distinct()
                .collect(Collectors.toList());
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (String category : categories) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", category);
            item.put("value", category);
            result.add(item);
        }
        
        return result;
    }

    @Override
    public List<Permission> getRolePermissions(Long roleId) {
        // 这里需要根据实际的数据库设计来实现
        // 假设有一个角色-权限关联表，可以通过roleId查询对应的权限
        // 由于没有看到相关表结构，这里只是提供一个示例实现
        return new ArrayList<>();
    }

    @Override
    @Transactional
    public boolean assignRolePermissions(Long roleId, List<Long> permissionIds) {
        // 这里需要根据实际的数据库设计来实现
        // 假设有一个角色-权限关联表，可以先删除原有关联，再添加新的关联
        // 由于没有看到相关表结构，这里只是提供一个示例实现
        return true;
    }
}