package com.communitypension.communitypensionadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.communitypension.communitypensionadmin.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;


public interface UserMapper extends BaseMapper<User> {
    // 根据用户名和角色ID获取用户信息@Select("SELECT u.*, r.role_name FROM user u " +
    @Select("SELECT u.*, r.role_name FROM user u " +
            "LEFT JOIN role r ON u.role_id = r.id " +
            "WHERE u.username = #{username}")
    Optional<User> getUserByUsernameAndRoleId(@Param("username") String username, @Param("roleId") Long roleId);





//
}
