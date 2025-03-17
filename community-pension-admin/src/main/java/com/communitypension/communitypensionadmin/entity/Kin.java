package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
    
    // 电话、地址和紧急联系人字段已移至user表
    
    /**
     * 备注信息
     */
    @Size(max = 500, message = "备注信息长度不能超过500字符")
    private String remark;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 关联的老人对象
     */
    @JsonIgnore
    @TableField(exist = false)
    private Elder elder;
}