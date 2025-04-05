package com.communitypension.communitypensionadmin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

/**
 * 老人用户视图对象
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "老人用户信息VO")
public class ElderUserVO extends UserVO {

    /**
     * 身份证号码
     */
    @Schema(description = "身份证号码")
    private String idCard;

    /**
     * 出生日期
     */
    @Schema(description = "出生日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    /**
     * 年龄
     */
    @Schema(description = "年龄")
    private Integer age;

    /**
     * 健康状况
     */
    @Schema(description = "健康状况")
    private String healthCondition;

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
     * 绑定的家属ID列表
     */
    @Schema(description = "绑定的家属ID列表")
    private List<Long> kinIds;

    /**
     * 绑定的家属姓名列表
     */
    @Schema(description = "绑定的家属姓名列表")
    private List<String> kinNames;

    // 转换方法已移至 UserConverter 接口
}