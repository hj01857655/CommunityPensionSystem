package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.Menu;
import com.communitypension.communitypensionadmin.mapper.MenuMapper;
import com.communitypension.communitypensionadmin.service.MenuService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    
    @Override
    public List<Menu> selectMenuList(Menu menu) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        
        // 构建查询条件
        wrapper.like(StringUtils.hasText(menu.getMenuName()), Menu::getMenuName, menu.getMenuName())
                .eq(menu.getStatus() != null, Menu::getStatus, menu.getStatus())
                .orderByAsc(Menu::getParentId)
                .orderByAsc(Menu::getOrderNum);
                
        return list(wrapper);
    }

    @Override
    public List<Menu> selectMenuTree() {
        return baseMapper.selectMenuTree();
    }
} 