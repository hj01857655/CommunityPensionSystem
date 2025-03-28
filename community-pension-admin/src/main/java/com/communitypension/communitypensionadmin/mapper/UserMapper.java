package com.communitypension.communitypensionadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.communitypension.communitypensionadmin.entity.User;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    //findByUsername
    @Select("select * from user where username = #{username}")
    User findByUsername(String username);

    /**
     * 查询用户总数
     *
     * @return 用户总数
     */
    @Select("SELECT COUNT(*) FROM user")
    int countTotalUsers();


//
}
