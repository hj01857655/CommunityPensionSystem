package com.communitypension.communitypensionadmin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.entity.User;

public interface UserService extends IService<User> {
    /**
     * 根据用户名和角色获取用户
     *
     * @param username 用户名
     * @param role     角色
     * @return 用户
     */
    User getUserByUsernameAndRole(String username, Long role);

    /**
     * 修改密码
     *
     * @param username    用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @param role        角色
     */
    void changePassword(String username, String oldPassword, String newPassword, Long role);
}
