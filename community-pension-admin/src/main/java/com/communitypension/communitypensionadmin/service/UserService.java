package com.communitypension.communitypensionadmin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.entity.User;

import java.util.Map;

public interface UserService extends IService<User> {
    Map<String, Object> login(String username, String password, Long roleId);
    void resetPassword(Long userId, String oldPassword, String newPassword);

}
