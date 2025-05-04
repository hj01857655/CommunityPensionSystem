package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.dto.PasswordDTO;
import com.communitypension.communitypensionadmin.dto.UserDTO;
import com.communitypension.communitypensionadmin.dto.UserRoleInfo;
import com.communitypension.communitypensionadmin.entity.Role;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.entity.UserRole;
import com.communitypension.communitypensionadmin.enums.RoleEnum;
import com.communitypension.communitypensionadmin.exception.BusinessException;
import com.communitypension.communitypensionadmin.mapper.RoleMapper;
import com.communitypension.communitypensionadmin.mapper.UserMapper;
import com.communitypension.communitypensionadmin.mapper.UserRoleMapper;
import com.communitypension.communitypensionadmin.service.ElderKinRelationService;
import com.communitypension.communitypensionadmin.service.UserService;
import com.communitypension.communitypensionadmin.utils.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    // 注入依赖
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final FileUploadUtil fileUploadUtil;
    private final ElderKinRelationService elderKinRelationService;

    /**
     * 根据条件分页查询用户信息
     *
     * @param page         分页对象
     * @param queryWrapper 查询条件
     * @return 用户信息
     */
    @Override
    public Page<User> getUserPage(Page<User> page, QueryWrapper<User> queryWrapper) {
        return baseMapper.selectPage(page, queryWrapper);
    }

    /**
     * 根据用户ID查询用户角色
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    @Override
    public List<Long> getUserRoleIds(Long userId) {
        return userRoleMapper.selectRoleIdsByUserId(userId);
    }
    /**
     * 根据用户ID查询
     *
     * @param userId 用户ID
     * @return 角色名称列表
     */
    @Override
    public List<String> getUserRoleNames(Long userId) {
        return roleMapper.selectRoleNamesByUserId(userId);
    }
    @Override
    public List<String> getUserRoles(Long userId) {
        return roleMapper.selectRolesByUserId(userId);
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    public User getUserByUsername(String username) {
        log.info("根据用户名查询用户信息");
        User user = userMapper.getUserByUsername(username);
        if (user != null) {
            System.out.println(user);
        }
        return user;

    }

    @Override
    public boolean hasRole(Long userId, Long roleId) {
        return userRoleMapper.hasRole(userId, roleId);
    }

    /**
     * 新增用户并分配角色
     *
     * @param userDTO 用户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUserWithRoles(UserDTO userDTO) {
        // 检查用户名是否已存在
        if (userMapper.selectCount(new QueryWrapper<User>().eq("username", userDTO.getUsername())) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建用户实体
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);

        // 加密密码
        user.setPassword(userDTO.getPassword());

        // 保存用户基本信息
        if (userMapper.insert(user) <= 0) {
            throw new RuntimeException("保存用户信息失败");
        }

        // 分配角色前验证
        if (userDTO.getRoleIds() != null && !userDTO.getRoleIds().isEmpty()) {
            validateRoleCombination(userDTO.getRoleIds());
            assignRoles(user.getUserId(), userDTO.getRoleIds());
        }

        // 如果是家属，且指定了要绑定的老人ID，则建立关系
        if (userDTO.getRoleIds() != null && userDTO.getBindElderIds() != null && !userDTO.getBindElderIds().isEmpty()
                && userDTO.getRoleIds().contains(RoleEnum.KIN.getId())) {
            for (Long elderId : userDTO.getBindElderIds()) {
                bindElderKinRelation(elderId, user.getUserId(), userDTO.getRelationType());
            }
        }

        // 如果是老人，且指定了要绑定的家属ID，则建立关系
        if (userDTO.getRoleIds() != null && userDTO.getBindKinIds() != null && !userDTO.getBindKinIds().isEmpty()
                && userDTO.getRoleIds().contains(RoleEnum.ELDER.getId())) {
            for (Long kinId : userDTO.getBindKinIds()) {
                bindElderKinRelation(user.getUserId(), kinId, userDTO.getRelationType());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(UserDTO userDTO) {
        if (userDTO == null || userDTO.getUserId() == null) {
            throw new BusinessException("用户信息不能为空");
        }

        // 检查用户是否存在
        User existingUser = userMapper.selectById(userDTO.getUserId());
        if (existingUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 将DTO转换为Entity
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);

        // 确保roleIds不为null
        if (userDTO.getRoleIds() == null) {
            userDTO.setRoleIds(new ArrayList<>());
        }

        // 检查角色是否有效
        if (!userDTO.getRoleIds().isEmpty()) {
            long validCount = roleMapper.selectCount(new LambdaQueryWrapper<Role>()
                    .in(Role::getRoleId, userDTO.getRoleIds()));
            if (validCount != userDTO.getRoleIds().size()) {
                throw new BusinessException("存在无效的角色ID");
            }
        }

        // 更新用户基本信息
        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new BusinessException("更新用户基本信息失败");
        }

        // 更新用户角色关系
        updateUserRoles(userDTO.getUserId(), userDTO.getRoleIds());

        // 更新绑定信息
        if (userDTO.getBindElderIds() != null && !userDTO.getBindElderIds().isEmpty()
                && userDTO.getRoleIds().contains(RoleEnum.KIN.getId())) {
            for (Long elderId : userDTO.getBindElderIds()) {
                bindElderKinRelation(elderId, userDTO.getUserId(), userDTO.getRelationType());
            }
        }

        if (userDTO.getBindKinIds() != null && !userDTO.getBindKinIds().isEmpty()
                && userDTO.getRoleIds().contains(RoleEnum.ELDER.getId())) {
            for (Long kinId : userDTO.getBindKinIds()) {
                bindElderKinRelation(userDTO.getUserId(), kinId, userDTO.getRelationType());
            }
        }

        return true;
    }



    @Override
    public void deleteUser(Long userId) {
        // 检查用户是否存在
        if (userMapper.selectById(userId) == null) {
            throw new RuntimeException("用户不存在");
        }

        // 删除用户角色关联
        userRoleMapper.delete(new QueryWrapper<UserRole>().eq("user_id", userId));

        // 删除用户
        userMapper.deleteById(userId);
    }

    @Override
    public void deleteUsers(List<Long> userIds) {
        // 批量删除用户角色关联
        userRoleMapper.delete(new QueryWrapper<UserRole>().in("user_id", userIds));

        // 批量删除用户
        userMapper.deleteByIds(userIds);
    }

    @Override
    public void resetPassword(Long userId) {
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 重置为默认密码
//        user.setPassword(passwordUtil.encrypt("123456"));
        user.setPassword("123456");
        userMapper.updateById(user);

    }

    @Override
    public void updatePassword(Long userId, PasswordDTO passwordDTO) {
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!passwordDTO.getOldPassword().equals(user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }
        // 更新密码
//        user.setPassword(passwordUtil.encrypt(passwordDTO.getNewPassword()));
        user.setPassword(passwordDTO.getNewPassword());
        userMapper.updateById(user);

    }

    @Override
    public String uploadAvatar(MultipartFile file) {
        // 上传文件
        String avatarUrl = fileUploadUtil.uploadFile(file, "avatar");

        // 更新用户头像
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            currentUser.setAvatar(avatarUrl);
            userMapper.updateById(currentUser);
        }

        return avatarUrl;
    }

    @Override
    public void assignRoles(Long userId, List<Long> roleIds) {
        // 检查用户是否存在
        if (userMapper.selectById(userId) == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证角色组合
        validateRoleCombination(roleIds);

        // 删除原有角色
        userRoleMapper.delete(new QueryWrapper<UserRole>().eq("user_id", userId));

        // 添加新角色
        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        }
    }

    @Override
    public void updateStatus(Long userId, byte isActive) {
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }


        // 更新状态
        user.setIsActive(isActive);
        userMapper.updateById(user);
    }

    /**
     * 获取当前登录用户
     */
    private User getCurrentUser() {
        // 获取当前登录用户
        return null;
    }

    @Override
    public List<User> getUnboundElders() {
        return userMapper.selectUnboundElders();
    }

    @Override
    public List<User> getUnboundKins() {
        return userMapper.selectUnboundKins();
    }
    /**
     * 验证角色组合是否合法
     * 例如，老人不能同时拥有老人和家属两种角色
     * @param roleIds 角色ID列表
     */
    public void validateRoleCombination(List<Long> roleIds) {
        boolean hasElderRole = roleIds.contains(RoleEnum.ELDER.getId()); // 假设老人角色ID为1
        boolean hasFamilyRole = roleIds.contains(RoleEnum.KIN.getId()); // 假设家属角色ID为2

        if (hasElderRole && hasFamilyRole) {
            throw new RuntimeException("一个用户不能同时拥有老人和家属角色");
        }
    }

    /**
     * 绑定老人和家属关系
     * @param elderId 老人ID
     * @param kinId 家属ID
     * @param relationType 关系类型
     * @return 是否绑定成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindElderKinRelation(Long elderId, Long kinId, String relationType) {
        // 验证用户角色
        User elder = this.getById(elderId);
        User kin = this.getById(kinId);
        if (elder == null || kin == null) {
            throw new BusinessException("用户不存在");
        }

        // 获取用户角色列表
        List<Long> elderRoleIds = getUserRoleIds(elderId);
        List<Long> kinRoleIds = getUserRoleIds(kinId);

        // 验证角色
        if (elderRoleIds == null || !elderRoleIds.contains(RoleEnum.ELDER.getId())) {
            throw new BusinessException("第一个用户必须是老人角色");
        }
        if (kinRoleIds == null || !kinRoleIds.contains(RoleEnum.KIN.getId())) {
            throw new BusinessException("第二个用户必须是家属角色");
        }

        return elderKinRelationService.bindRelation(elderId, kinId, relationType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unbindElderKinRelation(Long elderId, Long kinId, String relationType) {
        return elderKinRelationService.unbindRelation(elderId, kinId, relationType);
    }

    @Override
    public List<Long> getKinIdsByElderId(Long elderId) {
        return elderKinRelationService.getKinIdsByElderId(elderId);
    }

    @Override
    public List<Long> getElderIdsByKinId(Long kinId) {
        return elderKinRelationService.getElderIdsByKinId(kinId);
    }

    @Override
    public List<User> getKinsByElderId(Long elderId) {
        return elderKinRelationService.getKinsByElderId(elderId);
    }

    @Override
    public List<User> getEldersByKinId(Long kinId) {
        return elderKinRelationService.getEldersByKinId(kinId);
    }

    @Override
    public String getRelationType(Long elderId, Long kinId) {
        return elderKinRelationService.getRelationType(elderId, kinId);
    }

    /**
     * 更新用户角色关系
     */
    private void updateUserRoles(Long userId, List<Long> roleIds) {
        // 删除原有角色关系
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId));

        if (!roleIds.isEmpty()) {
            // 逐条插入新角色关系（替代 insertBatch）
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole(userId, roleId);
                int result = userRoleMapper.insert(userRole);
                if (result <= 0) {
                    throw new BusinessException("更新用户角色关系失败");
                }
            }
        }
    }

    @Override
    public UserRoleInfo getUserRoleInfo(Long userId) {
        // 一次性查询所有角色信息，减少数据库查询次数
        List<String> roles = getUserRoles(userId);
        List<Long> roleIds = getUserRoleIds(userId);
        List<String> roleNames = getUserRoleNames(userId);

        // 构建并返回 UserRoleInfo 对象
        return UserRoleInfo.builder()
                .roles(roles)
                .roleIds(roleIds)
                .roleNames(roleNames)
                .build();
    }

    @Override
    public List<User> getAllElders() {
        return userMapper.selectAllElders();
    }
}