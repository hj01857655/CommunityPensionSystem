package com.communitypension.communitypensionadmin.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.entity.Staff;
import com.communitypension.communitypensionadmin.dto.StaffUserDTO;

import java.util.Map;

public interface StaffService extends IService<Staff> {
    /**
     * 获取社区工作人员信息（联表查询user表）
     * @param page 分页对象
     * @param name 姓名（可选）
     * @return 分页数据
     */
    Page<Map<String, Object>> getStaffWithUserInfo(Page<Staff> page, String name);

    /**
     * 分页查询社区工作人员及其用户信息
     * @param page 分页参数
     * @param name 姓名（可选）
     * @return 包含联表结果的分页对象
     */
    Page<StaffUserDTO> queryStaffWithUserInfo(Page<Staff> page, String name);

    /**
     * 更新工作人员及关联的用户信息
     * @param dto 包含Staff和User信息的DTO
     * @return 是否更新成功
     */
    boolean updateStaffWithUser(StaffUserDTO dto);
}
