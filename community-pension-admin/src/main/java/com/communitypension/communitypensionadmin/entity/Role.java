package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableField;

import java.util.List;

/**
 * 角色信息表实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@TableName("role")
public class Role extends BaseEntity {

    /**
     * 角色ID
     */
    @TableId(value = "role_id", type = IdType.AUTO)
    private Long roleId;
    
    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(min = 1, max = 30, message = "角色名称长度不能超过30个字符")
    private String roleName;

    /**
     * 角色权限字符串
     */
    @NotBlank(message = "权限字符不能为空")
    @Size(min = 1, max = 100, message = "权限字符长度不能超过100个字符")
    private String roleKey;

    /**
     * 显示顺序
     */
    @NotNull(message = "显示顺序不能为空")
    private Integer roleSort;

    /**
     * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）
     */
    @Pattern(regexp = "^[1-4]$", message = "数据范围值必须是1-4之间的数字")
    private Character dataScope;

    /**
     * 菜单树选择项是否关联显示（true=关联显示 false=不关联显示）
     */
    private Boolean menuCheckStrictly;

    /**
     * 角色状态（0正常 1停用）
     */
    @Pattern(regexp = "^[01]$", message = "角色状态只能是0或1")
    private Character status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @Pattern(regexp = "^[02]$", message = "删除标志只能是0或2")
    private Character delFlag;

    /**
     * 角色菜单列表（非数据库字段，用于前端交互）
     */
    @TableField(exist = false)
    private List<Long> menuIds;
}