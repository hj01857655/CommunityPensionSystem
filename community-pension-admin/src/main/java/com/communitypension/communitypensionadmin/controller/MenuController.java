package com.communitypension.communitypensionadmin.controller;

import com.communitypension.communitypensionadmin.entity.Menu;
import com.communitypension.communitypensionadmin.service.MenuService;
import com.communitypension.communitypensionadmin.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 */
@RestController
@RequestMapping("/api/system/menu")
@RequiredArgsConstructor
@Tag(name = "菜单管理")
public class MenuController {
    private static final Logger logger = LoggerFactory.getLogger(MenuController.class);

    private final MenuService menuService;

    /**
     * 获取菜单列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取菜单列表")
    public Result<List<Menu>> list(Menu menu) {
        try {
            List<Menu> list = menuService.selectMenuList(menu);
            return Result.success(list);
        } catch (Exception e) {
            logger.error("获取菜单列表失败", e);
            return Result.error("获取菜单列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取菜单树结构
     */
    @GetMapping("/tree")
    @Operation(summary = "获取菜单树")
    public Result<List<Menu>> getMenuTree() {
        try {
            List<Menu> menuTree = menuService.selectMenuTree();
            return Result.success(menuTree);
        } catch (Exception e) {
            logger.error("获取菜单树失败", e);
            return Result.error("获取菜单树失败: " + e.getMessage());
        }
    }

    /**
     * 获取菜单详情
     */
    @GetMapping("/{menuId}")
    @Operation(summary = "获取菜单详情")
    public Result<Menu> getInfo(@PathVariable Long menuId) {
        try {
            Menu menu = menuService.getById(menuId);
            return Result.success(menu);
        } catch (Exception e) {
            logger.error("获取菜单详情失败", e);
            return Result.error("获取菜单详情失败: " + e.getMessage());
        }
    }

    /**
     * 新增菜单
     */
    @PostMapping
    @Operation(summary = "新增菜单")
    public Result<String> add(@RequestBody Menu menu) {
        try {
            if (menuService.save(menu)) {
                return Result.success("新增菜单成功");
            }
            return Result.error("新增菜单失败");
        } catch (Exception e) {
            logger.error("新增菜单失败", e);
            return Result.error("新增菜单失败: " + e.getMessage());
        }
    }

    /**
     * 修改菜单
     */
    @PutMapping
    @Operation(summary = "修改菜单")
    public Result<String> edit(@RequestBody Menu menu) {
        try {
            if (menuService.updateById(menu)) {
                return Result.success("修改菜单成功");
            }
            return Result.error("修改菜单失败");
        } catch (Exception e) {
            logger.error("修改菜单失败", e);
            return Result.error("修改菜单失败: " + e.getMessage());
        }
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{menuId}")
    @Operation(summary = "删除菜单")
    public Result<String> remove(@PathVariable Long menuId) {
        try {
            if (menuService.removeById(menuId)) {
                return Result.success("删除菜单成功");
            }
            return Result.error("删除菜单失败");
        } catch (Exception e) {
            logger.error("删除菜单失败", e);
            return Result.error("删除菜单失败: " + e.getMessage());
        }
    }
}