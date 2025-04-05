package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Transactional
@NoArgsConstructor
@TableName("user")
public class User extends BaseEntity {
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
     * 家属绑定的老人Id
     */
    @TableField(exist = false)
    private Long elderId;

    /**
     * 家属与老人关系
     */
    @TableField(exist = false)
    private String relationType;

    /**
     * 社区工作人员所在部门
     */
    private String department;

    /**
     * 社区工作人员岗位
     */
    private String position;

    /**
     * 老人身份证号码，唯一
     */
    private String idCard;

    /**
     * 老人出生日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    /**
     * 老人年龄
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
     * 老人健康状况
     */
    private String healthCondition;



    /**
     * 角色ID列表，不直接映射到数据库表
     */
    @TableField(exist = false,typeHandler = JacksonTypeHandler.class)
    private List<Long> roleIds;
    /**
     * 用户角色列表，不直接映射到数据库表
     */
    @TableField(exist = false,typeHandler =JacksonTypeHandler.class)
    private List<String> roles;
    /**
     * 用户角色名列表，不直接映射到数据库表
     */
    @TableField(exist = false,typeHandler = JacksonTypeHandler.class)
    private List<String> roleNames;






}