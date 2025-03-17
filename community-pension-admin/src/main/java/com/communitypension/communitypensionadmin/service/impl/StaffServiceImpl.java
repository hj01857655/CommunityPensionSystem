package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.dto.StaffUserDTO;
import com.communitypension.communitypensionadmin.entity.Staff;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.mapper.StaffMapper;
import com.communitypension.communitypensionadmin.service.StaffService;
import com.communitypension.communitypensionadmin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl extends ServiceImpl<StaffMapper, Staff> implements StaffService {

    private final StaffMapper staffMapper;
    private final UserService userService;

    @Override
    public Page<Map<String, Object>> getStaffWithUserInfo(Page<Staff> page, String name) {
        return staffMapper.getStaffWithUserInfo(page, name);
    }

    @Override
    public Page<StaffUserDTO> queryStaffWithUserInfo(Page<Staff> page, String name) {
        // 查询工作人员基本信息
        Page<Staff> staffPage = page(page, new LambdaQueryWrapper<Staff>()
                .like(StringUtils.hasText(name), Staff::getName, name)
                .orderByAsc(Staff::getId));
        
        // 创建结果页
        Page<StaffUserDTO> resultPage = new Page<>(
                staffPage.getCurrent(), 
                staffPage.getSize(), 
                staffPage.getTotal());
        
        // 批量查询关联的用户信息（减少数据库查询次数）
        if (!staffPage.getRecords().isEmpty()) {
            List<Long> staffIds = staffPage.getRecords().stream()
                    .map(Staff::getId)
                    .collect(Collectors.toList());
                    
            // 一次性查询所有关联用户，提高性能
            List<User> users = userService.list(new LambdaQueryWrapper<User>()
                    .in(User::getStaffId, staffIds));
                    
            // 创建staffId -> User的映射，方便快速查找
            Map<Long, User> userMap = users.stream()
                    .collect(Collectors.toMap(User::getStaffId, user -> user));
            
            // 组装最终结果
            List<StaffUserDTO> dtoList = staffPage.getRecords().stream()
                    .map(staff -> {
                        StaffUserDTO dto = new StaffUserDTO();
                        // 复制Staff属性
                        dto.setId(staff.getId());
                        dto.setName(staff.getName());
                        dto.setDepartment(staff.getDepartment());
                        dto.setPosition(staff.getPosition());
                        
                        // 复制关联的User属性
                        User user = userMap.get(staff.getId());
                        if (user != null) {
                            dto.setPhone(user.getPhone());
                            dto.setEmail(user.getEmail());
                            dto.setAddress(user.getAddress());
                            dto.setAvatar(user.getAvatar());
                            
                            // 修复类型或字段名称问题
                            // 假设User类中是is_active字段，类型为Integer
                            dto.setIsActive(user.getIsActive());
                            
                            // 处理日期时间类型转换
                            // 如果User类中使用的是java.util.Date而不是LocalDateTime
                            if (user.getCreateTime() != null) {
                                // 如果是Date类型，需要转换为LocalDateTime
                                // dto.setCreateTime(user.getCreateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                                // 如果已经是LocalDateTime，直接设置
                                dto.setCreateTime(user.getCreateTime());
                            }
                            
                            if (user.getUpdateTime() != null) {
                                dto.setUpdateTime(user.getUpdateTime());
                            }
                            
                            dto.setRemark(user.getRemark());
                        }
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            resultPage.setRecords(dtoList);
        }
        
        return resultPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStaffWithUser(StaffUserDTO dto) {
        // 更新Staff表
        Staff staff = new Staff();
        staff.setId(dto.getId());
        staff.setName(dto.getName());
        staff.setDepartment(dto.getDepartment());
        staff.setPosition(dto.getPosition());
        boolean staffUpdated = updateById(staff);
        
        // 更新关联的User表
        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getStaffId, dto.getId()));
        
        if (user != null) {
            user.setPhone(dto.getPhone());
            user.setEmail(dto.getEmail());
            user.setAddress(dto.getAddress());
            user.setRemark(dto.getRemark());
            
            boolean userUpdated = userService.updateById(user);
            return staffUpdated && userUpdated;
        }
        
        return staffUpdated;
    }
}
