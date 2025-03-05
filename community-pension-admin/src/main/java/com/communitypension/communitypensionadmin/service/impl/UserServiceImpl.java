package com.communitypension.communitypensionadmin.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.*;
import com.communitypension.communitypensionadmin.mapper.*;
import com.communitypension.communitypensionadmin.service.UserService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private ElderMapper elderMapper;
    @Autowired
    private KinMapper kinMapper;
    @Autowired
    private StaffMapper staffMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserByUsernameAndRole(String username, Long roleId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.eq("role_id", roleId);
        return baseMapper.selectOne(queryWrapper);
    }
    @Override
    public User getUserByRoleId(Long roleId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
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
