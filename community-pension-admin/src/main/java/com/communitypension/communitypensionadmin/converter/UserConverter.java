package com.communitypension.communitypensionadmin.converter;

import com.communitypension.communitypensionadmin.dto.UserDTO;
import com.communitypension.communitypensionadmin.entity.User;
import com.communitypension.communitypensionadmin.vo.ElderUserVO;
import com.communitypension.communitypensionadmin.vo.KinUserVO;
import com.communitypension.communitypensionadmin.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

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
     * @param roles 角色列表
     * @param roleIds 角色ID列表
     * @param roleNames 角色名称列表
     * @return 包含角色信息的用户VO
     */
    default UserVO toUserVOWithRoles(User user, List<String> roles, List<Long> roleIds, List<String> roleNames) {
        UserVO userVO = toUserVO(user);
        userVO.setRoles(roles);
        userVO.setRoleIds(roleIds);
        userVO.setRoleNames(roleNames);
        return userVO;
    }

    /**
     * 将 User 实体转换为 UserVO，并一次性设置所有角色信息
     * @param user 用户实体
     * @param userId 用户ID
     * @param userService 用户服务
     * @return 包含角色信息的用户VO
     */
    default UserVO toUserVOWithAllRoleInfo(User user, Long userId, com.communitypension.communitypensionadmin.service.UserService userService) {
        // 一次性查询所有角色信息
        List<String> roles = userService.getUserRoles(userId);
        List<Long> roleIds = userService.getUserRoleIds(userId);
        List<String> roleNames = userService.getUserRoleNames(userId);

        return toUserVOWithRoles(user, roles, roleIds, roleNames);
    }

    /**
     * 根据用户角色返回对应的VO对象
     * @param user 用户实体
     * @param roleId 角色ID
     * @param userService 用户服务
     * @return 根据角色返回的VO对象
     */
    default Object toRoleSpecificVO(User user, Long roleId, com.communitypension.communitypensionadmin.service.UserService userService) {
        // 一次性查询所有角色信息
        List<String> roles = userService.getUserRoles(user.getUserId());
        List<Long> roleIds = userService.getUserRoleIds(user.getUserId());
        List<String> roleNames = userService.getUserRoleNames(user.getUserId());

        // 根据角色ID返回对应的VO
        if (roleId == 1L) { // 老人角色
            ElderUserVO elderUserVO = toElderUserVO(user);
            elderUserVO.setRoles(roles);
            elderUserVO.setRoleIds(roleIds);
            elderUserVO.setRoleNames(roleNames);
            return elderUserVO;
        } else if (roleId == 2L) { // 家属角色
            KinUserVO kinUserVO = toKinUserVO(user);
            kinUserVO.setRoles(roles);
            kinUserVO.setRoleIds(roleIds);
            kinUserVO.setRoleNames(roleNames);
            return kinUserVO;
        } else { // 其他角色
            UserVO userVO = toUserVO(user);
            userVO.setRoles(roles);
            userVO.setRoleIds(roleIds);
            userVO.setRoleNames(roleNames);
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
        if (isActive == null) {
            return "未知";
        }
        return switch (isActive) {
            case 0 -> "禁用";
            case 1 -> "正常";
            case 2 -> "锁定";
            default -> "未知";
        };
    }
}
