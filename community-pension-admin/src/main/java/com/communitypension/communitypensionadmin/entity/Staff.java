package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@TableName("staff")
public class Staff {

    /** 主键ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 员工姓名 */
    @NotBlank(message = "员工姓名不能为空")
    @Size(max = 50, message = "员工姓名长度不能超过50字符")
    private String name;



    /** 员工部门 */
    @NotBlank(message = "员工部门不能为空")
    @Size(max = 50, message = "员工部门长度不能超过50字符")
    private String department;

    /** 员工职位 */
    @NotBlank(message = "员工职位不能为空")
    @Size(max = 50, message = "员工职位长度不能超过50字符")
    private String position;


}