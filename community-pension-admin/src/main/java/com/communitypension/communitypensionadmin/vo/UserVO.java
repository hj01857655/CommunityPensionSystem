package com.communitypension.communitypensionadmin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户视图对象
 */
@Data
@Builder
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
    
    /**
     * 社区工作人员所在部门
     */
    @Schema(description = "社区工作人员所在部门")
    private String department;
    
    /**
     * 社区工作人员岗位
     */
    @Schema(description = "社区工作人员岗位")
    private String position;
    
    /**
     * 老人身份证号码
     */
    @Schema(description = "身份证号码")
    private String idCard;
    
    /**
     * 老人出生日期
     */
    @Schema(description = "出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    
    /**
     * 老人年龄
     */
    @Schema(description = "年龄")
    private Integer age;
    
    /**
     * 紧急联系人姓名
     */
    @Schema(description = "紧急联系人姓名")
    private String emergencyContactName;
    
    /**
     * 紧急联系人电话
     */
    @Schema(description = "紧急联系人电话")
    private String emergencyContactPhone;
    
    /**
     * 老人健康状况
     */
    @Schema(description = "健康状况")
    private String healthCondition;
    
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
    
    /**
     * 绑定的老人ID（当用户是家属时）
     */
    @Schema(description = "绑定的老人ID")
    private Long elderId;
    
    /**
     * 绑定的老人姓名（当用户是家属时）
     */
    @Schema(description = "绑定的老人姓名")
    private String elderName;
    
    /**
     * 绑定的家属ID列表（当用户是老人时）
     */
    @Schema(description = "绑定的家属ID列表")
    private List<Long> kinIds;
    
    /**
     * 绑定的家属姓名列表（当用户是老人时）
     */
    @Schema(description = "绑定的家属姓名列表")
    private List<String> kinNames;
    
    /**
     * 与老人/家属的关系类型
     */
    @Schema(description = "与老人/家属的关系类型")
    private String relationType;
    
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
