package com.communitypension.communitypensionadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.communitypension.communitypensionadmin.entity.Role;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface RoleMapper extends BaseMapper<Role> {
    // 使用@Param注解明确参数
    @Select("SELECT * FROM role WHERE role_name = #{roleName}")
    Role getRoleByName(@Param("roleName") String roleName);

}
