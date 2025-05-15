package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.entity.Menu;
import com.communitypension.communitypensionadmin.entity.Role;
import com.communitypension.communitypensionadmin.entity.RoleMenuDTO;
import com.communitypension.communitypensionadmin.service.RoleMenuService;
import com.communitypension.communitypensionadmin.service.RoleService;
import com.communitypension.communitypensionadmin.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色管理接口
 */
@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
@Tag(name = "角色管理")
public class RoleController {
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    private final RoleService roleService;
    private final RoleMenuService roleMenuService;

    /**
     * 获取角色列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取角色列表")
    public Result<Page<Role>> list(Role role, 
                                 @RequestParam(defaultValue = "1") int current,
                                 @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Role> page = new Page<>(current, size);
            Page<Role> list = roleService.selectRoleList(role, page);
            return Result.success(list);
        } catch (Exception e) {
            logger.error("获取角色列表失败", e);
            return Result.error("获取角色列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有角色
     */
    @GetMapping("/all")
    @Operation(summary = "获取所有角色")
    public Result<List<Role>> all() {
        try {
            List<Role> roles = roleService.selectRoleAll();
            return Result.success(roles);
        } catch (Exception e) {
            logger.error("获取所有角色失败", e);
            return Result.error("获取所有角色失败: " + e.getMessage());
        }
    }

    /**
     * 根据角色ID获取角色信息
     */
    @GetMapping("/{roleId}")
    @Operation(summary = "获取角色详情")
    public Result<Role> getInfo(@PathVariable Long roleId) {
        try {
            Role role = roleService.selectRoleById(roleId);
            return Result.success(role);
        } catch (Exception e) {
            logger.error("获取角色信息失败", e);
            return Result.error("获取角色信息失败: " + e.getMessage());
        }
    }

    /**
     * 新增角色
     */
    @PostMapping
    @Operation(summary = "新增角色")
    public Result<String> add(@RequestBody Role role) {
        try {
            if (roleService.checkRoleNameUnique(role.getRoleName())) {
                return Result.error("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
            }
            if (roleService.checkRoleKeyUnique(role.getRoleKey())) {
                return Result.error("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
            }
            
            if (roleService.save(role)) {
                return Result.success("新增角色成功");
            }
            return Result.error("新增角色失败");
        } catch (Exception e) {
            logger.error("新增角色失败", e);
            return Result.error("新增角色失败: " + e.getMessage());
        }
    }

    /**
     * 修改角色
     */
    @PutMapping
    @Operation(summary = "修改角色")
    public Result<String> edit(@RequestBody Role role) {
        try {
            roleService.checkRoleAllowed(role);
            
            if (roleService.updateById(role)) {
                return Result.success("修改角色成功");
            }
            return Result.error("修改角色失败");
        } catch (Exception e) {
            logger.error("修改角色失败", e);
            return Result.error("修改角色失败: " + e.getMessage());
        }
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{roleId}")
    @Operation(summary = "删除角色")
    public Result<String> remove(@PathVariable Long roleId) {
        try {
            Role role = new Role();
            role.setRoleId(roleId);
            roleService.checkRoleAllowed(role);
            
            // 判断是否有用户使用该角色
            int count = roleService.countUserRoleByRoleId(roleId);
            if (count > 0) {
                return Result.error("角色已分配给用户，不能删除");
            }
            
            if (roleService.removeById(roleId)) {
                return Result.success("删除角色成功");
            }
            return Result.error("删除角色失败");
        } catch (Exception e) {
            logger.error("删除角色失败", e);
            return Result.error("删除角色失败: " + e.getMessage());
        }
    }

    /**
     * 修改角色状态
     */
    @PutMapping("/changeStatus")
    @Operation(summary = "修改角色状态")
    public Result<String> changeStatus(@RequestBody Role role) {
        try {
            // 先检查是否是超级管理员角色
            if (role.getRoleId() != null && role.getRoleId().equals(roleService.ADMIN_ROLE_ID)) {
                logger.warn("尝试修改超级管理员角色状态被拒绝：{}", role);
                return Result.error("超级管理员角色状态不允许修改");
            }
            
            roleService.checkRoleAllowed(role);
            
            if (roleService.updateById(role)) {
                return Result.success("修改角色状态成功");
            }
            return Result.error("修改角色状态失败");
        } catch (Exception e) {
            logger.error("修改角色状态失败", e);
            return Result.error("修改角色状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户的角色
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户角色")
    public Result<List<String>> getUserRole(@PathVariable Long userId) {
        try {
            List<String> role = roleService.selectRolesByUserId(userId);
            return Result.success(role);
        } catch (Exception e) {
            logger.error("获取用户角色失败", e);
            return Result.error("获取用户角色失败: " + e.getMessage());
        }
    }

    /**
     * 获取角色菜单权限
     */
    @GetMapping("/roleMenu/{roleId}")
    @Operation(summary = "获取角色菜单权限")
    public Result<List<Long>> getRoleMenuList(@PathVariable("roleId") Long roleId) {
        try {
            List<Long> menuIds = roleMenuService.selectMenuIdsByRoleId(roleId);
            return Result.success(menuIds);
        } catch (Exception e) {
            logger.error("获取角色菜单权限失败", e);
            return Result.error("获取角色菜单权限失败: " + e.getMessage());
        }
    }

    /**
     * 分配角色菜单权限
     */
    @PutMapping("/roleMenu")
    @Operation(summary = "分配角色菜单权限")
    public Result<String> assignRoleMenu(@RequestBody RoleMenuDTO roleMenu) {
        try {
            // 检查角色是否允许操作
            Role role = new Role();
            role.setRoleId(roleMenu.getRoleId());
            roleService.checkRoleAllowed(role);
            
            // 更新角色菜单关联
            roleMenuService.updateRoleMenus(roleMenu.getRoleId(), roleMenu.getMenuIds());
            return Result.success("分配菜单权限成功");
        } catch (Exception e) {
            logger.error("分配角色菜单权限失败", e);
            return Result.error("分配角色菜单权限失败: " + e.getMessage());
        }
    }

    /**
     * 获取角色的菜单树
     */
    @GetMapping("/roleMenuTree/{roleId}")
    @Operation(summary = "获取角色菜单树")
    public Result<Map<String, Object>> getRoleMenuTree(@PathVariable("roleId") Long roleId) {
        try {
            // 获取所有菜单树
            List<Menu> menus = roleMenuService.selectMenuTree();
            // 获取角色已有菜单ID
            List<Long> checkedKeys = roleMenuService.selectMenuIdsByRoleId(roleId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("menus", menus);
            result.put("checkedKeys", checkedKeys);
            
            return Result.success(result);
        } catch (Exception e) {
            logger.error("获取角色菜单树失败", e);
            return Result.error("获取角色菜单树失败: " + e.getMessage());
        }
    }
}
