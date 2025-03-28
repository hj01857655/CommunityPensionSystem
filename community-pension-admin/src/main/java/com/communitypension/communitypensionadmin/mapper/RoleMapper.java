package com.communitypension.communitypensionadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.communitypension.communitypensionadmin.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色Mapper接口
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    @Select("SELECT r.role_name " +
            "FROM role r " +
            "LEFT JOIN user_role ur ON r.role_id = ur.role_id " +
            "LEFT JOIN user u ON ur.user_id = u.user_id " +
            "WHERE ur.user_id = #{userId}")
    List<String> selectRoleNamesByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询角色
     * @param userId 用户ID
     * @return 角色
     */
    @Select("SELECT r.role_key" +
            " FROM role r " +
            "LEFT JOIN user_role ur ON r.role_id = ur.role_id " +
            "LEFT JOIN user u ON ur.user_id = u.user_id " +
            "WHERE ur.user_id = #{userId}")
    List<String> selectRolesByUserId(@Param("userId") Long userId);
    /**
     * 根据条件分页查询角色列表
     * @param role 角色
     * @param page 分页对象
     * @return 角色列表分页对象
     */
    Page<Role> selectRoleList(@Param("role") Role role, @Param("page") Page<Role> page);


}
