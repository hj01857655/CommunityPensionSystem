package com.communitypension.communitypensionadmin.converter;

import com.communitypension.communitypensionadmin.pojo.dto.UserDTO;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.pojo.vo.ElderUserVO;
import com.communitypension.communitypensionadmin.pojo.vo.KinUserVO;
import com.communitypension.communitypensionadmin.pojo.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import com.communitypension.communitypensionadmin.utils.DictUtils;
import com.communitypension.communitypensionadmin.constant.DictTypeConstants;

import java.util.List;

/**
 * 用户对象转换器
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserConverter {

    /**
     * 将 User 实体转换为 UserVO
     * @param user 用户实体
     * @return 用户VO
     */
    @Mapping(target = "statusName", source = "isActive", qualifiedByName = "getStatusName")
    UserVO toUserVO(User user);

    /**
     * 将 User 实体转换为 ElderUserVO
     * @param user 用户实体
     * @return 老人用户VO
     */
    @Mapping(target = "statusName", source = "isActive", qualifiedByName = "getStatusName")
    ElderUserVO toElderUserVO(User user);

    /**
     * 将 User 实体转换为 KinUserVO
     * @param user 用户实体
     * @return 家属用户VO
     */
    @Mapping(target = "statusName", source = "isActive", qualifiedByName = "getStatusName")
    KinUserVO toKinUserVO(User user);

    /**
     * 将 UserDTO 转换为 User 实体
     * @param userDTO 用户DTO
     * @return 用户实体
     */
    User toEntity(UserDTO userDTO);

    /**
     * 将 User 实体列表转换为 UserVO 列表
     * @param userList 用户实体列表
     * @return 用户VO列表
     */
    List<UserVO> toUserVOList(List<User> userList);

    /**
     * 将 User 实体列表转换为 ElderUserVO 列表
     * @param userList 用户实体列表
     * @return 老人用户VO列表
     */
    List<ElderUserVO> toElderUserVOList(List<User> userList);

    /**
     * 将 User 实体列表转换为 KinUserVO 列表
     * @param userList 用户实体列表
     * @return 家属用户VO列表
     */
    List<KinUserVO> toKinUserVOList(List<User> userList);

    /**
     * 将 User 实体转换为 UserVO，并设置角色信息
     * @param user 用户实体
     * @param role 角色
     * @param roleId 角色ID
     * @param roleName 角色名称
     * @return 包含角色信息的用户VO
     */
    default UserVO toUserVOWithRole(User user, String role, Long roleId, String roleName) {
        UserVO userVO = toUserVO(user);
        userVO.setRole(role);
        userVO.setRoleId(roleId);
        userVO.setRoleName(roleName);
        return userVO;
    }

    /**
     * 将 User 实体转换为 UserVO，并一次性设置角色信息
     * @param user 用户实体
     * @param userId 用户ID
     * @param userService 用户服务
     * @return 包含角色信息的用户VO
     */
    default UserVO toUserVOWithAllRoleInfo(User user, Long userId, com.communitypension.communitypensionadmin.service.UserService userService) {
        // 一次性查询角色信息
        String role = userService.getUserRole(userId);
        Long roleId = userService.getUserRoleId(userId);
        String roleName = userService.getUserRoleName(userId);

        return toUserVOWithRole(user, role, roleId, roleName);
    }

    /**
     * 根据用户角色返回对应的VO对象
     * @param user 用户实体
     * @param roleId 角色ID
     * @param userService 用户服务
     * @return 根据角色返回的VO对象
     */
    default Object toRoleSpecificVO(User user, Long roleId, com.communitypension.communitypensionadmin.service.UserService userService) {
        // 获取角色信息（单角色系统，只传userId）
        String role = userService.getUserRole(user.getUserId());
        String roleName = userService.getUserRoleName(user.getUserId());

        // 根据角色ID返回对应的VO
        if (roleId == 1L) { // 老人角色
            ElderUserVO elderUserVO = toElderUserVO(user);
            elderUserVO.setRole(role);
            elderUserVO.setRoleId(roleId);
            elderUserVO.setRoleName(roleName);
            return elderUserVO;
        } else if (roleId == 2L) { // 家属角色
            KinUserVO kinUserVO = toKinUserVO(user);
            kinUserVO.setRole(role);
            kinUserVO.setRoleId(roleId);
            kinUserVO.setRoleName(roleName);
            return kinUserVO;
        } else { // 其他角色
            UserVO userVO = toUserVO(user);
            userVO.setRole(role);
            userVO.setRoleId(roleId);
            userVO.setRoleName(roleName);
            return userVO;
        }
    }

    /**
     * 根据用户状态获取状态名称
     * @param isActive 用户状态
     * @return 状态名称
     */
    @Named("getStatusName")
    default String getStatusName(Byte isActive) {
        return DictUtils.getDictLabel(
            DictTypeConstants.USER_STATUS,
            isActive == null ? null : String.valueOf(isActive)
        );
    }
}
