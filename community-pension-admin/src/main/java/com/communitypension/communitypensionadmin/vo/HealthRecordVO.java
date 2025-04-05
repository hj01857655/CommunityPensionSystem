package com.communitypension.communitypensionadmin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 健康档案视图对象
 */
@Data
@Schema(description = "健康档案视图对象")
public class HealthRecordVO {

    /**
     * 档案ID
     */
    @Schema(description = "档案ID")
    private Long id;

    /**
     * 老人ID
     */
    @Schema(description = "老人ID")
    private Long elderId;

    /**
     * 老人姓名
     */
    @Schema(description = "老人姓名")
    private String elderName;

    /**
     * 老人年龄
     */
    @Schema(description = "老人年龄")
    private Integer elderAge;

    /**
     * 老人性别
     */
    @Schema(description = "老人性别")
    private String elderGender;

    /**
     * 血型：A型、B型、AB型、O型、其他
     */
    @Schema(description = "血型：A型、B型、AB型、O型、其他")
    private String bloodType;

    /**
     * 身高（cm）
     */
    @Schema(description = "身高（cm）")
    private Double height;

    /**
     * 体重（kg）
     */
    @Schema(description = "体重（kg）")
    private Double weight;

    /**
     * BMI指数
     */
    @Schema(description = "BMI指数")
    private Double bmi;

    /**
     * 过敏史
     */
    @Schema(description = "过敏史")
    private String allergies;

    /**
     * 家族病史
     */
    @Schema(description = "家族病史")
    private String familyHistory;

    /**
     * 既往病史
     */
    @Schema(description = "既往病史")
    private String medicalHistory;

    /**
     * 手术史
     */
    @Schema(description = "手术史")
    private String surgicalHistory;

    /**
     * 长期用药情况
     */
    @Schema(description = "长期用药情况")
    private String medicationHistory;

    /**
     * 生活习惯
     */
    @Schema(description = "生活习惯")
    private String lifestyle;

    /**
     * 饮食习惯
     */
    @Schema(description = "饮食习惯")
    private String dietaryHabits;

    /**
     * 紧急联系人姓名
     */
    @Schema(description = "紧急联系人姓名")
    private String emergencyContactName;

    /**
     * 紧急联系人电话
     */
    @Schema(description = "紧急联系人电话")
    private String emergencyContactPhone;

    /**
     * 紧急联系人关系
     */
    @Schema(description = "紧急联系人关系")
    private String emergencyContactRelation;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remarks;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
