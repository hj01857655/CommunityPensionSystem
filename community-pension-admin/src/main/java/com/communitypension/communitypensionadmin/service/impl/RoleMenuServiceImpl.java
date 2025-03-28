package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.Menu;
import com.communitypension.communitypensionadmin.entity.RoleMenu;
import com.communitypension.communitypensionadmin.mapper.MenuMapper;
import com.communitypension.communitypensionadmin.mapper.RoleMenuMapper;
import com.communitypension.communitypensionadmin.service.RoleMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {

    private final MenuMapper menuMapper;
    private final RoleMenuMapper roleMenuMapper;

    @Override
    public List<Long> selectMenuIdsByRoleId(Long roleId) {
        if (roleId == null) {
            return new ArrayList<>();
        }
        return roleMenuMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleMenus(Long roleId, List<Long> menuIds) {
        if (roleId == null) {
            throw new IllegalArgumentException("角色ID不能为空");
        }
        
        // 删除原有关联
        roleMenuMapper.deleteByRoleId(roleId);

        // 建立新的关联
        if (menuIds != null && !menuIds.isEmpty()) {
            List<RoleMenu> roleMenus = new ArrayList<>();
            for (Long menuId : menuIds) {
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                roleMenus.add(roleMenu);
            }
            roleMenuMapper.batchInsert(roleId, menuIds);
        }
    }

    @Override
    public List<Menu> selectMenuTree() {
        return menuMapper.selectMenuTree();
    }

    @Override
    public boolean checkMenuExistRole(Long menuId) {
        if (menuId == null) {
            return false;
        }
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getMenuId, menuId);
        return this.count(wrapper) > 0;
    }
}