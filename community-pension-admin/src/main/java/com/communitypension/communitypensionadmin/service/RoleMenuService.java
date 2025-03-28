package com.communitypension.communitypensionadmin.service;

import com.communitypension.communitypensionadmin.entity.Menu;
import com.communitypension.communitypensionadmin.entity.RoleMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface RoleMenuService extends IService<RoleMenu> {
    
    /**
     * 查询角色的菜单ID列表
     */
    List<Long> selectMenuIdsByRoleId(Long roleId);
    
    /**
     * 更新角色菜单关联
     */
    void updateRoleMenus(Long roleId, List<Long> menuIds);
    
    /**
     * 获取菜单树
     */
    List<Menu> selectMenuTree();
    
    /**
     * 检查菜单是否有角色在使用
     */
    boolean checkMenuExistRole(Long menuId);
}