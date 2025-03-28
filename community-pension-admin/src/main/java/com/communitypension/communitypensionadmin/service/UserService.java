package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.dto.PasswordDTO;
import com.communitypension.communitypensionadmin.dto.UserDTO;
import com.communitypension.communitypensionadmin.entity.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    /**
     * 正常分页查询用户基础信息
     * @param page 分页对象
     * @param queryWrapper 查询条件
     * @return 用户基础信息分页
     */
    Page<User> getUserPage(Page<User> page, QueryWrapper<User> queryWrapper);

    /**
     * 根据用户ID查询用户角色
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> getUserRoleIds(Long userId);
    /**
     * 根据用户ID查询
     * @param userId 用户ID
     * @return 角色名称列表
     */
    List<String> getUserRoleNames(Long userId);
    /**
     * 根据用户ID查询
     * @param userId 用户ID
     * @return 角色列表
     */
    List<String> getUserRoles(Long userId);

    UserDTO getUserByUsername(String username);

    boolean hasRole(Long userId, Long roleId);

    /**
     * 新增用户并分配角色
     * @param userDTO 用户信息
     */
    void addUserWithRoles(UserDTO userDTO);

    /**
     * 更新用户
     */
    @Transactional(rollbackFor = Exception.class)
    boolean updateUser(UserDTO userDTO);

    /**
     * 删除用户
     */
    void deleteUser(Long userId);

    /**
     * 批量删除用户
     */
    void deleteUsers(List<Long> userIds);

    /**
     * 重置用户密码
     */
    void resetPassword(Long userId);

    /**
     * 修改用户密码
     */
    void updatePassword(Long userId, PasswordDTO passwordDTO);

    /**
     * 上传用户头像
     */
    String uploadAvatar(MultipartFile file);

    /**
     * 分配用户角色
     */
    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * 更新用户状态
     */
    void updateStatus(Long userId, byte status);


    /**
     * 获取未绑定家属的老人列表
     * @return 老人用户列表
     */
    List<User> getUnboundElders();

    /**
     * 验证角色组合是否合法
     * 例如，老人不能同时拥有老人和家属两种角色
     * @param roleIds 角色ID列表
     */
    void validateRoleCombination(List<Long> roleIds);

    /**
     * 绑定老人和家属关系
     *
     * @param elderId 老人ID
     * @param kinId 家属ID
     * @param relationType 关系类型
     * @return 是否绑定成功
     */
    boolean bindElderKinRelation(Long elderId, Long kinId, String relationType);
    
    /**
     * 解绑老人和家属关系
     *
     * @param elderId 老人ID
     * @param kinId 家属ID
     * @return 是否解绑成功
     */
    boolean unbindElderKinRelation(Long elderId, Long kinId, String relationType);
    
    /**
     * 获取老人的所有家属ID
     *
     * @param elderId 老人ID
     * @return 家属ID列表
     */
    List<Long> getKinIdsByElderId(Long elderId);
    
    /**
     * 获取家属的所有老人ID
     *
     * @param kinId 家属ID
     * @return 老人ID列表
     */
    List<Long> getElderIdsByKinId(Long kinId);

    /**
     * 获取未绑定老人的家属列表
     * @return 家属用户列表
     */
    List<User> getUnboundKins();


}
