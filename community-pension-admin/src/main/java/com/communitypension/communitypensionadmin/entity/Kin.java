package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("kin")
public class Kin {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 亲属姓名 */
    @NotBlank(message = "亲属姓名不能为空")
    @Size(max = 50, message = "亲属姓名长度不能超过50字符")
    private String name;

    /** 亲属电话 */
    @NotBlank(message = "亲属电话不能为空")
    @Size(max = 20, message = "亲属电话长度不能超过20字符")
    private String phone;

    /** 亲属地址 */
    @Size(max = 200, message = "亲属地址长度不能超过200字符")
    private String address;

    /** 亲属关系 */
    @NotBlank(message = "亲属关系不能为空")
    @Size(max = 50, message = "亲属关系长度不能超过50字符")
    private String relationship;

    /** 老人ID */
    @NotNull(message = "老人ID不能为空")
    private Long elderId;

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