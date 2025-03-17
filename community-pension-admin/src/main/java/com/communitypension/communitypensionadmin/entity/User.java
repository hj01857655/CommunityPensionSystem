package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 密码（加密存储）
     */
    private String password;

    /**
     * 是否为管理员，0-否，1-是
     */
    private Integer isAdmin;

    /**
     * 用户状态：1-正常，0-禁用，2-锁定
     */
    private Integer isActive;

    /**
     * 是否被删除
     */
    private Integer isDeleted;

    private String phone;
    /**
     *
     * 邮箱
     */
    private String email;

    /**
     * 用户地址
     */
    private String address;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 角色ID（关联role表）
     */
    private Long roleId;

    /**
     * 老人ID（关联elder表）
     */
    private Long elderId;

    /**
     * 家属ID（关联kin表）
     */
    private Long kinId;

    /**
     * 员工ID（关联staff表）
     */
    private Long staffId;

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

    @TableField(exist = false)
    private Elder elder;

    @TableField(exist = false)
    private Kin kin;

    @TableField(exist = false)
    private Staff staff;

    @TableField(exist = false)
    private Role role;
}