package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.entity.Menu;

import java.util.List;

public interface MenuService extends IService<Menu> {
    /**
     * 查询菜单列表
     * @param menu 查询条件
     * @return 菜单列表
     */
    List<Menu> selectMenuList(Menu menu);

    /**
     * 查询菜单树结构
     * @return 菜单树列表
     */
    List<Menu> selectMenuTree();
} 