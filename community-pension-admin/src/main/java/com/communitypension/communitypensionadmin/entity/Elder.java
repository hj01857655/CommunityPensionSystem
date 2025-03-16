package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

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



    /** 紧急联系人姓名 */
    @Size(max = 50, message = "紧急联系人姓名过长")
    private String emergencyContactName;

    /** 紧急联系人电话 */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "紧急联系电话格式错误")
    private String emergencyContactPhone;

    /** 老人健康状况 */
    @Size(max = 500, message = "健康状况描述过长")
    private String healthCondition;

    /** 备注信息 */
    @Size(max = 1000, message = "备注信息过长")
    private String remark;

    public enum Gender {
        male, female
    }
    @TableField(exist = false)
    private HealthRecords healthRecords;
}