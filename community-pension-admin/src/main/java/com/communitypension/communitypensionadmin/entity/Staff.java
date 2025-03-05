package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("staff")
public class Staff {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 员工姓名 */
    @NotBlank(message = "员工姓名不能为空")
    @Size(max = 50, message = "员工姓名长度不能超过50字符")
    private String name;

    /** 员工电话 */
    @NotBlank(message = "员工电话不能为空")
    @Size(max = 20, message = "员工电话长度不能超过20字符")
    private String phone;

    /** 员工地址 */
    @Size(max = 200, message = "员工地址长度不能超过200字符")
    private String address;

    /** 员工部门 */
    @NotBlank(message = "员工部门不能为空")
    @Size(max = 50, message = "员工部门长度不能超过50字符")
    private String department;

    /** 员工职位 */
    @NotBlank(message = "员工职位不能为空")
    @Size(max = 50, message = "员工职位长度不能超过50字符")
    private String position;

    /** 创建时间 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 备注信息 */
    @Size(max = 1000, message = "备注信息长度不能超过1000字符")
    private String remark;
}