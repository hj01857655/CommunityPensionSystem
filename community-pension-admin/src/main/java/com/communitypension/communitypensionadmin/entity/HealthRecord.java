package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 健康档案实体类
 */
@Data
@TableName("health_record")
public class HealthRecord {

    /**
     * 档案ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 老人ID
     */
    private Long elderId;

    /**
     * 血型：A型、B型、AB型、O型、其他
     */
    private String bloodType;

    /**
     * 身高（cm）
     */
    private Double height;

    /**
     * 体重（kg）
     */
    private Double weight;

    /**
     * 过敏史
     */
    private String allergies;

    /**
     * 家族病史
     */
    private String familyHistory;

    /**
     * 既往病史
     */
    private String medicalHistory;

    /**
     * 手术史
     */
    private String surgicalHistory;

    /**
     * 长期用药情况
     */
    private String medicationHistory;

    /**
     * 生活习惯
     */
    private String lifestyle;

    /**
     * 饮食习惯
     */
    private String dietaryHabits;

    /**
     * 紧急联系人姓名
     */
    private String emergencyContactName;

    /**
     * 紧急联系人电话
     */
    private String emergencyContactPhone;

    /**
     * 紧急联系人关系
     */
    private String emergencyContactRelation;

    /**
     * 备注
     */
    private String remarks;

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
     * 是否已删除
     */
    @TableLogic
    private Integer isDeleted;
}
