package com.communitypension.communitypensionadmin.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.Role;
import com.communitypension.communitypensionadmin.mapper.RoleMapper;
import com.communitypension.communitypensionadmin.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    private RoleMapper roleMapper;

    public RoleServiceImpl(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }
    @Override
    public Role getRoleByName(String roleName) {
        return roleMapper.getRoleByName(roleName);
    }
}
