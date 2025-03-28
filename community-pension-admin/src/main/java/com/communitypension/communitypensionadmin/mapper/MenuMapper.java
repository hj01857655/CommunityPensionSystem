package com.communitypension.communitypensionadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.communitypension.communitypensionadmin.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {
    /**
     * 查询菜单列表
     * @return 菜单列表
     */
    List<Menu> listMenu();

    /**
     * 查询菜单树结构
     * @return 菜单列表
     */
    List<Menu> selectMenuTree();
}
