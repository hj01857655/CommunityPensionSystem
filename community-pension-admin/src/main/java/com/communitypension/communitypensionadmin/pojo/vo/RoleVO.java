package com.communitypension.communitypensionadmin.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色视图对象
 */
@Data
public class RoleVO implements Serializable {
    
    /**
     * 角色ID
     */
    private Long roleId;
    
    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色权限字符串
     */
    private String roleKey;

    /**
     * 显示顺序
     */
    private Integer roleSort;

    /**
     * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）
     */
    private Character dataScope;
    
    /**
     * 数据范围描述
     */
    private String dataScopeDesc;

    /**
     * 菜单树选择项是否关联显示
     */
    private Boolean menuCheckStrictly;

    /**
     * 角色状态（0正常 1停用）
     */
    private Character status;
    
    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 菜单ID列表
     */
    private List<Long> menuIds;
    
    /**
     * 菜单名称列表（用于展示）
     */
    private List<String> menuNames;
}
