package com.communitypension.communitypensionadmin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.entity.Role;

public interface RoleService extends IService<Role> {
    Role getRoleByName(String roleName);
}
