package com.communitypension.communitypensionadmin.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.communitypension.communitypensionadmin.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.Map;
import java.util.Optional;


public interface UserMapper extends BaseMapper<User> {
    // 根据用户名和角色ID获取用户信息@Select("SELECT u.*, r.role_name FROM user u " +
    @Select("SELECT u.*, r.role_name FROM user u " +
            "LEFT JOIN role r ON u.role_id = r.id " +
            "WHERE u.username = #{username}")
    Optional<User> getUserByUsernameAndRole(@Param("username") String username, @Param("roleId") Long roleId);
    // 添加关联角色信息的查询
    @Select("SELECT u.*, r.role_name FROM user u " +
            "LEFT JOIN role r ON u.role_id = r.id " +
            "WHERE u.id = #{userId}")
    @Results({
            @Result(column = "role_name", property = "role.roleName")
    })
    Optional<User> selectUserWithRoles(@Param("userId") Long userId);

    // 获取用户档案信息
    @Select("SELECT u.*, e.*, s.* FROM user u " +
            "LEFT JOIN elder e ON u.elder_id = e.id " +
            "LEFT JOIN staff s ON u.staff_id = s.id " +
            "WHERE u.username = #{username}")
    Optional<Map<String, Object>> findProfileByUsername(@Param("username") String username);

}
