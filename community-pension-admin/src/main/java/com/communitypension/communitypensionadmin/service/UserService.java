package com.communitypension.communitypensionadmin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.entity.User;

public interface UserService extends IService<User> {


    //
    User getInfoWithUser(User user);
}
