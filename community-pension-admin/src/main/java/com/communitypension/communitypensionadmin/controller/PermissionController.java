package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.communitypension.communitypensionadmin.entity.Permission;
import com.communitypension.communitypensionadmin.service.PermissionService;
import com.communitypension.communitypensionadmin.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 权限管理控制器
 */
@RestController
@RequestMapping("/api/system/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 分页获取权限列表
     * @param page 页码
     * @param size 每页数量
     * @param query 查询条件
     * @return 权限列表分页数据
     */
    @GetMapping("/list")
    public Result<IPage<Permission>> getPermissionList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "query", required = false) String query) {
        IPage<Permission> permissionPage = permissionService.getPermissionsByPage(page, size, query);
        return Result.success("获取权限列表成功", permissionPage);
    }

    /**
     * 添加权限
     * @param permission 权限信息
     * @return 添加后的权限信息
     */
    @PostMapping("/add")
    public Result<Permission> addPermission(@RequestBody Permission permission) {
        Permission savedPermission = permissionService.addPermission(permission);
        return Result.created(savedPermission);
    }

    /**
     * 更新权限
     * @param id 权限ID
     * @param permission 权限信息
     * @return 更新后的权限信息
     */
    @PutMapping("/update/{id}")
    public Result<Permission> updatePermission(@PathVariable Long id, @RequestBody Permission permission) {
        permission.setId(id);
        Permission updatedPermission = permissionService.updatePermission(permission);
        return Result.success("更新权限成功", updatedPermission);
    }

    /**
     * 删除权限
     * @param id 权限ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> deletePermission(@PathVariable Long id) {
        try {
            boolean result = permissionService.deletePermission(id);
            if (result) {
                return Result.deleted();
            } else {
                return Result.error("删除权限失败");
            }
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取权限树
     * @return 权限树结构
     */
    @GetMapping("/tree")
    public Result<List<Map<String, Object>>> getPermissionTree() {
        List<Map<String, Object>> permissionTree = permissionService.getPermissionTree();
        return Result.success("获取权限树成功", permissionTree);
    }

    /**
     * 获取权限分类列表
     * @return 权限分类列表
     */
    @GetMapping("/categories")
    public Result<List<Map<String, Object>>> getPermissionCategories() {
        List<Map<String, Object>> categories = permissionService.getPermissionCategories();
        return Result.success("获取权限分类成功", categories);
    }
}