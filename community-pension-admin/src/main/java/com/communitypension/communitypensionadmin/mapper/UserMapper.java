package com.communitypension.communitypensionadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.communitypension.communitypensionadmin.entity.User;
import org.apache.ibatis.annotations.Select;


public interface UserMapper extends BaseMapper<User> {
    //findByUsername
    @Select("select * from user where username = #{username}")
    User findByUsername(String username);

    boolean resetPassword(Long userId);


//
}
