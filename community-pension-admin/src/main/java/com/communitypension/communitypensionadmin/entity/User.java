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
    private Long id;    // 用户ID

    private String username;// 用户名
    private String password;// 密码
    private String phone;// 手机号
    private Long roleId;    // 角色ID
    private int status; // 状态
    private Long elderId;// 老人ID
    private Long kinId; // 亲属ID
    private Long staffId;   // 工作人员ID

    private LocalDateTime createTime;    // 创建时间
    private LocalDateTime updateTime;    // 更新时间

    @TableField(exist = false)
    private Elder elder;

    @TableField(exist = false)
    private Kin kin;

    @TableField(exist = false)
    private Staff staff;

    @TableField(exist = false)
    private Role role;
}