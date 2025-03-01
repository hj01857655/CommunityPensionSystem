package com.communitypension.communitypensionadmin.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.mapper.UserMapper;
import com.communitypension.communitypensionadmin.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User getUserByUsernameAndRole(String username, Long roleId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.eq("role_id", roleId);
        return baseMapper.selectOne(queryWrapper);
    }
    @Override
    public void changePassword(String username, String oldPassword,String newPassword, Long roleId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = baseMapper.selectOne(queryWrapper);
        //如果旧密码正确，修改密码
        if (user.getPassword().equals(oldPassword)&&user.getRoleId().equals(roleId)) {
            user.setPassword(newPassword);
            baseMapper.updateById(user);
        }
    }
}
