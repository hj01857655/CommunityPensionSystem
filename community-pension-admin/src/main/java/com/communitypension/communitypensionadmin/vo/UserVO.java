package com.communitypension.communitypensionadmin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户视图对象
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户信息VO")
public class UserVO {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    private String name;

    /**
     * 性别
     */
    @Schema(description = "性别")
    private String gender;

    /**
     * 电话号码
     */
    @Schema(description = "电话号码")
    private String phone;

    /**
     * 电子邮件
     */
    @Schema(description = "电子邮件")
    private String email;

    /**
     * 用户地址
     */
    @Schema(description = "用户地址")
    private String address;

    /**
     * 头像
     */
    @Schema(description = "头像URL")
    private String avatar;

    /**
     * 用户状态：1-正常，0-禁用，2-锁定
     */
    @Schema(description = "用户状态：1-正常，0-禁用，2-锁定")
    private Byte isActive;

    /**
     * 用户状态名称
     */
    @Schema(description = "用户状态名称")
    private String statusName;

    // 以下字段已移至对应的子类中

    /**
     * 角色ID列表
     */
    @Schema(description = "角色ID列表")
    private List<Long> roleIds;

    /**
     * 用户角色列表
     */
    @Schema(description = "用户角色列表")
    private List<String> roles;

    /**
     * 用户角色名列表
     */
    @Schema(description = "用户角色名列表")
    private List<String> roleNames;

    // 以下字段已移至对应的子类中

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 创建者
     */
    @Schema(description = "创建者")
    private String createBy;

    /**
     * 更新者
     */
    @Schema(description = "更新者")
    private String updateBy;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

}
