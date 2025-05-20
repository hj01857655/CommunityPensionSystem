package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.pojo.dto.PasswordDTO;
import com.communitypension.communitypensionadmin.pojo.dto.UserDTO;
import com.communitypension.communitypensionadmin.pojo.dto.UserRoleInfo;
import com.communitypension.communitypensionadmin.entity.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    /**
     * 正常分页查询用户基础信息
     *
     * @param page         分页对象
     * @param queryWrapper 查询条件
     * @return 用户基础信息分页
     */
    Page<User> getUserPage(Page<User> page, QueryWrapper<User> queryWrapper);

    /**
     * 根据用户ID查询用户角色ID
     *
     * @param userId 用户ID
     * @return 角色ID
     */
    Long getUserRoleId(Long userId); // 单角色

    /**
     * 根据用户ID查询用户角色名称
     *
     * @param userId 用户ID
     * @return 角色名称
     */
    String getUserRoleName(Long userId); // 单角色

    /**
     * 根据用户ID查询用户角色
     *
     * @param userId 用户ID
     * @return 角色
     */
    String getUserRole(Long userId); // 单角色

    /**
     * 获取用户的角色信息，包含角色ID、角色名称和角色标识
     *
     * @param userId 用户ID
     * @return 用户角色信息对象
     */
    UserRoleInfo getUserRoleInfo(Long userId); // 单角色

    User getUserByUsername(String username);

    boolean hasRole(Long userId, Long roleId); // 单角色

    /**
     * 新增用户并分配角色
     *
     * @param userDTO 用户信息
     */
    void addUserWithRole(UserDTO userDTO); // 单角色

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
    void assignRole(Long userId, Long roleId); // 单角色

    /**
     * 更新用户状态
     */
    void updateStatus(Long userId, byte status);


    /**
     * 获取未绑定家属的老人列表
     *
     * @return 老人用户列表
     */
    List<User> getUnboundElders();

    /**
     * 验证角色是否合法
     *
     * @param roleId 角色ID
     */
    void validateRole(Long roleId);

    /**
     * 绑定老人和家属关系
     *
     * @param elderId      老人ID
     * @param kinId        家属ID
     * @param relationType 关系类型
     * @return 是否绑定成功
     */
    boolean bindElderKinRelation(Long elderId, Long kinId, String relationType);

    /**
     * 解绑老人和家属关系
     *
     * @param elderId 老人ID
     * @param kinId   家属ID
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
     *
     * @return 家属用户列表
     */
    List<User> getUnboundKins();

    /**
     * 获取老人的家属列表
     *
     * @param elderId 老人ID
     * @return 家属用户列表
     */
    List<User> getKinsByElderId(Long elderId);

    /**
     * 获取家属的老人列表
     *
     * @param kinId 家属ID
     * @return 老人用户列表
     */
    List<User> getEldersByKinId(Long kinId);

    /**
     * 获取老人和家属的关系类型
     *
     * @param elderId 老人ID
     * @param kinId   家属ID
     * @return 关系类型
     */
    String getRelationType(Long elderId, Long kinId);

    /**
     * 获取所有老人列表
     *
     * @return 老人用户列表
     */
    List<User> getAllElders();

    /**
     * 获取指定时间之前创建的用户数量
     *
     * @param time 指定时间
     * @return 用户数量
     */
    Long getCountBeforeTime(LocalDateTime time);

    /**
     * 获取指定天数内的活跃用户数量
     *
     * @param days 天数
     * @return 活跃用户数量
     */
    Long getActiveUserCount(int days);

    /**
     * 获取指定时间范围内的活跃用户数量
     *
     * @param endDaysBefore   结束时间距今天数
     * @param startDaysBefore 开始时间距今天数
     * @return 活跃用户数量
     */
    Long getActiveUserCount(int endDaysBefore, int startDaysBefore);

    /**
     * 获取指定日期的新增用户数量
     *
     * @param date 日期
     * @return 新增用户数量
     */
    Long getNewUserCountByDay(LocalDate date);

    /**
     * 获取指定日期的活跃用户数量
     *
     * @param date 日期
     * @return 活跃用户数量
     */
    Long getActiveUserCountByDay(LocalDate date);

    /**
     * 统计指定时间范围内创建的用户数量
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    long countByCreateTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取所有管理员用户
     *
     * @return 管理员用户列表
     */
    List<User> getAdminUsers();

}
