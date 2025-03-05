package com.communitypension.communitypensionadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.communitypension.communitypensionadmin.entity.Role;
import com.communitypension.communitypensionadmin.service.RoleService;
import com.communitypension.communitypensionadmin.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/api/roles")
    public class RoleController {
        private final RoleService roleService;

        @Autowired
        public RoleController(RoleService roleService) {
            this.roleService = roleService;
        }

        /**
         * 获取角色列表分页获取角色列表
         * @param page 页码
         * @param size 每页数量
         * @return 角色列表
         */
        @GetMapping
        public Result<IPage<Role>> getRolePage(
                @RequestParam(defaultValue = "1") int page,
                @RequestParam(defaultValue = "10") int size) {
            return Result.success(roleService.getRolesByPage(page, size));
        }



        /**
         * 创建角色
         * @param role 角色信息
         * @return 创建结果
         */
        @PostMapping
        public Result<Role> createRole(@RequestBody Role role) {
            if (roleService.getRoleByName(role.getRoleName()) != null) {
                return Result.error(400, "角色名称已存在");
            }
            roleService.save(role);
            return Result.success(role);
        }

        /**
         * 更新角色
         * @param id 角色ID
         * @param role 角色信息
         * @return 更新结果
         */
        @PutMapping("/{id}")
        public Result<Role> updateRole(@PathVariable Long id, @RequestBody Role role) {
            role.setId(id);
            roleService.updateById(role);
            return Result.success(role);
        }

        /**
         * 删除角色
         * @param id 角色ID
         * @return 删除结果
         */
        @DeleteMapping("/{id}")
        public Result<Void> deleteRole(@PathVariable Long id) {
            roleService.removeById(id);
            return Result.deleted();
        }

        /**
         * 根据ID获取角色
         * @param id 角色ID
         * @return 角色信息
         */
        @GetMapping("/{id}")
        public Result<Role> getRoleById(@PathVariable Long id) {
            return Result.success(roleService.getById(id));
        }
    }