package com.communitypension.communitypensionadmin.vo.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.communitypension.communitypensionadmin.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

/**
 * 用户数据传输对象
 */
@Transactional
@Data
@EqualsAndHashCode(callSuper = true)
public class UserDTO extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（加密存储）
     */
    private String password;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别
     */
    private String gender;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 电子邮件
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
     * 用户状态：1-正常，0-禁用，2-锁定
     */
    private Byte isActive;

    /**
     * 社区工作人员所在部门
     */
    private String department;

    /**
     * 社区工作人员岗位
     */
    private String position;

    /**
     * 身份证号码，唯一
     */
    private String idCard;

    /**
     * 出生日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 紧急联系人姓名
     */
    private String emergencyContactName;

    /**
     * 紧急联系人电话
     */
    private String emergencyContactPhone;

    /**
     * 健康状况
     */
    private String healthCondition;

    /**
     * 角色ID列表
     */
    @TableField(exist = false, typeHandler = JacksonTypeHandler.class)
    private List<Long> roleIds;

    /**
     * 用户角色列表
     */
    @TableField(exist = false, typeHandler = JacksonTypeHandler.class)
    private List<String> roles;

    /**
     * 角色名称列表
     */
    @TableField(exist = false, typeHandler = JacksonTypeHandler.class)
    private List<String> roleNames;

    /**
     * 要绑定的老人ID列表（当用户是家属时使用）
     */
    @TableField(exist = false, typeHandler = JacksonTypeHandler.class)
    private List<Long> bindElderIds;

    /**
     * 要绑定的家属ID列表（当用户是老人时使用）
     */
    @TableField(exist = false, typeHandler = JacksonTypeHandler.class)
    private List<Long> bindKinIds;

    /**
     * 与老人/家属的关系类型
     */
    @TableField(exist = false)
    private String relationType;
}