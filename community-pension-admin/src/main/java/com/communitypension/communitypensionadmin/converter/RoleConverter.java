package com.communitypension.communitypensionadmin.converter;

import com.communitypension.communitypensionadmin.entity.Role;
import com.communitypension.communitypensionadmin.pojo.dto.RoleDTO;
import com.communitypension.communitypensionadmin.pojo.vo.RoleVO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色数据转换器
 */
@Component
public class RoleConverter {

    /**
     * 将 DTO 转换为实体
     * @param roleDTO 角色DTO
     * @return 角色实体
     */
    public Role toEntity(RoleDTO roleDTO) {
        if (roleDTO == null) {
            return null;
        }
        
        Role role = new Role();
        role.setRoleId(roleDTO.getRoleId());
        role.setRoleName(roleDTO.getRoleName());
        role.setRoleKey(roleDTO.getRoleKey());
        role.setRoleSort(roleDTO.getRoleSort());
        
        // 处理字符类型字段
        if (roleDTO.getDataScope() != null) {
            role.setDataScope(roleDTO.getDataScope().charAt(0));
        }
        if (roleDTO.getStatus() != null) {
            role.setStatus(roleDTO.getStatus().charAt(0));
        }
        
        role.setMenuCheckStrictly(roleDTO.getMenuCheckStrictly());
        role.setRemark(roleDTO.getRemark());
        
        return role;
    }
    
    /**
     * 将实体转换为 VO
     * @param role 角色实体
     * @return 角色VO
     */
    public RoleVO toVO(Role role) {
        if (role == null) {
            return null;
        }
        
        RoleVO roleVO = new RoleVO();
        roleVO.setRoleId(role.getRoleId());
        roleVO.setRoleName(role.getRoleName());
        roleVO.setRoleKey(role.getRoleKey());
        roleVO.setRoleSort(role.getRoleSort());
        roleVO.setDataScope(role.getDataScope());
        roleVO.setMenuCheckStrictly(role.getMenuCheckStrictly());
        roleVO.setStatus(role.getStatus());
        roleVO.setCreateTime(role.getCreateTime());
        roleVO.setUpdateTime(role.getUpdateTime());
        roleVO.setRemark(role.getRemark());
        roleVO.setMenuIds(role.getMenuIds());
        
        // 添加状态描述
        if (role.getStatus() != null) {
            roleVO.setStatusDesc(role.getStatus() == '0' ? "正常" : "停用");
        }
        
        // 添加数据范围描述
        if (role.getDataScope() != null) {
            switch (role.getDataScope()) {
                case '1':
                    roleVO.setDataScopeDesc("全部数据权限");
                    break;
                case '2':
                    roleVO.setDataScopeDesc("自定数据权限");
                    break;
                case '3':
                    roleVO.setDataScopeDesc("本部门数据权限");
                    break;
                case '4':
                    roleVO.setDataScopeDesc("本部门及以下数据权限");
                    break;
                default:
                    roleVO.setDataScopeDesc("未知");
            }
        }
        
        return roleVO;
    }
    
    /**
     * 将实体列表转换为 VO 列表
     * @param roleList 角色实体列表
     * @return 角色VO列表
     */
    public List<RoleVO> toVOList(List<Role> roleList) {
        if (roleList == null) {
            return new ArrayList<>();
        }
        return roleList.stream().map(this::toVO).collect(Collectors.toList());
    }
    
    /**
     * 将 DTO 列表转换为实体列表
     * @param roleDTOList 角色DTO列表
     * @return 角色实体列表
     */
    public List<Role> toEntityList(List<RoleDTO> roleDTOList) {
        if (roleDTOList == null) {
            return new ArrayList<>();
        }
        return roleDTOList.stream().map(this::toEntity).collect(Collectors.toList());
    }
}
