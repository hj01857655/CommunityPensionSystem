package com.communitypension.communitypensionadmin.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 健康档案数据传输对象
 */
@Data
public class HealthRecordDTO {

    /**
     * 档案ID（更新时使用）
     */
    private Long id;

    /**
     * 老人ID
     */
    @NotNull(message = "老人ID不能为空")
    private Long elderId;

    /**
     * 血型：A型、B型、AB型、O型、其他
     */
    @Size(max = 10, message = "血型长度不能超过10个字符")
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
    @Size(max = 500, message = "过敏史长度不能超过500个字符")
    private String allergies;

    /**
     * 家族病史
     */
    @Size(max = 500, message = "家族病史长度不能超过500个字符")
    private String familyHistory;

    /**
     * 既往病史
     */
    @Size(max = 500, message = "既往病史长度不能超过500个字符")
    private String medicalHistory;

    /**
     * 手术史
     */
    @Size(max = 500, message = "手术史长度不能超过500个字符")
    private String surgicalHistory;

    /**
     * 长期用药情况
     */
    @Size(max = 500, message = "长期用药情况长度不能超过500个字符")
    private String medicationHistory;

    /**
     * 生活习惯
     */
    @Size(max = 500, message = "生活习惯长度不能超过500个字符")
    private String lifestyle;

    /**
     * 饮食习惯
     */
    @Size(max = 500, message = "饮食习惯长度不能超过500个字符")
    private String dietaryHabits;

    /**
     * 紧急联系人姓名
     */
    @Size(max = 50, message = "紧急联系人姓名长度不能超过50个字符")
    private String emergencyContactName;

    /**
     * 紧急联系人电话
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "紧急联系人电话格式不正确")
    private String emergencyContactPhone;

    /**
     * 紧急联系人关系
     */
    @Size(max = 50, message = "紧急联系人关系长度不能超过50个字符")
    private String emergencyContactRelation;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remarks;
}
