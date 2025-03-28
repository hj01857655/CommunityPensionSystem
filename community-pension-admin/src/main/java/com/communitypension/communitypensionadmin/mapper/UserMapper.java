package com.communitypension.communitypensionadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.communitypension.communitypensionadmin.dto.UserDTO;
import com.communitypension.communitypensionadmin.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户数据访问接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 用户信息
     */
    @Select("select u.*," +
            "CAST(GROUP_CONCAT(r.role_id ORDER BY r.role_id SEPARATOR ',') AS CHAR) AS roleIds," +
            "CAST(GROUP_CONCAT(r.role_name ORDER BY r.role_id SEPARATOR ',') AS CHAR) AS roleNames," +
            "CAST(GROUP_CONCAT(r.role_key ORDER BY r.role_id SEPARATOR ',') AS CHAR) AS roles" +
            " FROM user u" +
            " LEFT JOIN user_role ur ON u.user_id = ur.user_id" +
            " LEFT JOIN role r ON ur.role_id = r.role_id" +
            " WHERE u.username = #{username}" +
            " GROUP BY u.user_id")
    UserDTO selectUserByUsername(@Param("username") String username);
    /**
     * 查询未绑定家属的老人列表
     */
    @Select("SELECT DISTINCT u.* FROM user u " +
            "LEFT JOIN user_role ur ON u.user_id = ur.user_id " +
            "LEFT JOIN elder_kin_relation ek ON u.user_id = ek.elder_id " +
            "WHERE ur.role_id = 1 " +  // 老人角色ID
            "AND ek.kin_id IS NULL")  // 没有绑定家属
    List<User> selectUnboundElders();

    /**
     * 查询未绑定老人的家属列表
     */
    @Select("SELECT DISTINCT u.* FROM user u " +
            "LEFT JOIN user_role ur ON u.user_id = ur.user_id " +
            "LEFT JOIN elder_kin_relation ek ON u.user_id = ek.kin_id " +
            "WHERE ur.role_id =2 " +  // 家属角色ID
            "AND ek.elder_id IS NULL")  // 没有绑定老人
    List<User> selectUnboundKins();
}


