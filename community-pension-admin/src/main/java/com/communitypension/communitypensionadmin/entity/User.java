package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;
    private String password;
    private Long elderId;
    private Long kinId;
    private Long staffId;
    private Long roleId;
    private int status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private Elder elder;

    @TableField(exist = false)
    private Kin kin;

    @TableField(exist = false)
    private Staff staff;

    @TableField(exist = false)
    private Role role;
}