package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@TableName("kin")
public class Kin {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 亲属姓名
     */
    @NotBlank(message = "亲属姓名不能为空")
    @Size(max = 50, message = "亲属姓名长度不能超过50字符")
    private String name;

    /**
     * 老人ID
     */
    @NotNull(message = "老人ID不能为空")
    private Long elderId;

    /**
     * 亲属关系
     */
    @NotBlank(message = "亲属关系不能为空")
    @Size(max = 50, message = "亲属关系长度不能超过50字符")
    private String relationship;


}