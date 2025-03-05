package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("elder")
public class Elder implements Serializable {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 老人姓名 */
    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名长度不能超过50字符")
    private String name;

    /** 老人性别 */
    @NotNull(message = "性别不能为空")
    @TableField("gender")
    private Gender gender;

    /** 老人出生日期 */
    @NotNull(message = "出生日期不能为空")
    private LocalDate birthday;

    /** 年龄 */
    private Integer age;

    /** 老人身份证号码 */
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^\\d{17}[\\dXx]$", message = "身份证格式错误")
    private String idCard;

    /** 老人联系电话 */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;

    /** 老人家庭住址 */
    @Size(max = 200, message = "地址长度不能超过200字符")
    private String address;

    /** 紧急联系人姓名 */
    @Size(max = 50, message = "紧急联系人姓名过长")
    private String emergencyContactName;

    /** 紧急联系人电话 */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "紧急联系电话格式错误")
    private String emergencyContactPhone;

    /** 老人健康状况 */
    @Size(max = 500, message = "健康状况描述过长")
    private String healthCondition;



    /** 老人头像URL */
    private String avatar;

    /** 记录创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 记录更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 备注信息 */
    @Size(max = 1000, message = "备注信息过长")
    private String remark;
    public enum Gender {
        male, female
    }
}