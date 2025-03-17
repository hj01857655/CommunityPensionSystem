package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("kin")
public class Kin implements Serializable {

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
     * 亲属关系
     */
    @NotBlank(message = "亲属关系不能为空")
    @Size(max = 50, message = "亲属关系长度不能超过50字符")
    private String relationship;
    
    /**
     * 老人ID（外键）
     */
    @NotNull(message = "老人ID不能为空")
    @TableField("elder_id")
    private Long elderId;


    /**
     * 关联的老人对象
     */
    @JsonIgnore
    @TableField(exist = false)
    private Elder elder;
}