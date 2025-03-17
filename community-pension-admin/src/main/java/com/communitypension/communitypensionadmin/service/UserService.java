package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.entity.User;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    boolean resetPassword(Long userId);

    User findByUsername(String username);

    Page<User> page(Page<User> page, LambdaQueryWrapper<User> queryWrapper);
}
