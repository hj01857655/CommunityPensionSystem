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
@Mapper(componentModel = "spring")
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
     * 根据用户状态获取状态名称
     * @param isActive 用户状态
     * @return 状态名称
     */
    @Named("getStatusName")
    default String getStatusName(Byte isActive) {
        if (isActive == null) {
            return "未知";
        }
        switch (isActive) {
            case 0:
                return "禁用";
            case 1:
                return "正常";
            case 2:
                return "锁定";
            default:
                return "未知";
        }
    }
}
