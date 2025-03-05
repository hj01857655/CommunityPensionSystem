package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.Role;
import com.communitypension.communitypensionadmin.mapper.RoleMapper;
import com.communitypension.communitypensionadmin.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Override
    public Role getRoleByName(String roleName) {
        return this.lambdaQuery()
                .eq(Role::getRoleName, roleName)
                .one();
    }



    @Override
    public IPage<Role> getRolesByPage(int page, int size) {
        return this.page(new Page<>(page, size));
    }
}
