package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("elder")
public class Elder implements Serializable {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 老人姓名
     */
    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名长度不能超过50字符")
    private String name;

    /**
     * 性别（M: 男性, F: 女性）
     */
    @Pattern(regexp = "^[MF]$", message = "性别格式错误")
    private String gender;

    /**
     * 出生日期
     */
    @NotNull(message = "出生日期不能为空")
    private Date birthday;

    /**
     * 身份证号码
     */
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^\\d{17}[\\dXx]$", message = "身份证格式错误")
    private String idCard;

    /**
     * 联系电话
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;

    /**
     * 联系地址
     */
    @Size(max = 200, message = "地址长度不能超过200字符")
    private String address;

    /**
     * 紧急联系人
     */
    @Size(max = 50, message = "紧急联系人姓名过长")
    private String emergencyContactName;

    /**
     * 紧急联系电话
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "紧急联系电话格式错误")
    private String emergencyContactPhone;

    /**
     * 健康状况
     */
    @Size(max = 500, message = "健康状况描述过长")
    private String healthCondition;

    /**
     * 病史记录
     */
    @Size(max = 1000, message = "病史记录过长")
    private String medicalHistory;

    /**
     * 过敏史
     */
    @Size(max = 500, message = "过敏史描述过长")
    private String allergy;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 创建时间（自动填充）
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间（自动填充）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 备注信息
     */
    @Size(max = 1000, message = "备注信息过长")
    private String remark;
}
