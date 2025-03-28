package com.communitypension.communitypensionadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.communitypension.communitypensionadmin.entity.UserRole;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 用户角色关联数据访问接口
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
    /**
     * 检查用户是否具有指定角色
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否具有指定角色
     */
    @Select("SELECT COUNT(*) > 0 FROM user_role WHERE user_id = #{userId} AND role_id = #{roleId}")
     boolean hasRole(Long userId, Long roleId);


    /**
     * 根据用户ID查询角色ID列表
     * @param userId 用户ID
     * @return 角色ID列表
     */
    @Select("SELECT role_id FROM user_role WHERE user_id = #{userId}")
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID删除用户角色关联
     * @param userId 用户ID
     */
    @Delete("DELETE FROM user_role WHERE user_id = #{userId}")
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * 添加用户角色关联
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    @Insert("INSERT INTO user_role(user_id, role_id) VALUES(#{userId}, #{roleId})")
    void insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
    /**
     * 添加用户角色关联，如果已存在则更新角色ID
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    @Insert("INSERT INTO user_role(user_id, role_id) VALUES(#{userId}, #{roleId}) ON DUPLICATE KEY UPDATE role_id = #{roleId}")
    void addUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 更新用户角色关联
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    @Update("UPDATE user_role SET role_id = #{roleId} WHERE user_id = #{userId} ")
    void updateUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

} 