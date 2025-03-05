package com.communitypension.communitypensionadmin.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("role")
public class Role {
    @TableId(type = IdType.AUTO)
    private Long id;

    // 明确指定数据库字段名
    @TableField("role_name")
    private String roleName;

    @TableField("role_description")
    private String roleDescription;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
