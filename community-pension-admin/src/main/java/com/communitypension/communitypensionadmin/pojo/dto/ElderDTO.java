package com.communitypension.communitypensionadmin.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

/**
 * 老人用户数据传输对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "老人用户DTO")
public class ElderDTO extends UserDTO {
    /**
     * 身份证号码，唯一
     */
    @NotBlank(message = "身份证号码不能为空")
    @Size(min = 18, max = 18, message = "身份证号码必须为18位")
    @Schema(description = "身份证号码", required = true)
    private String idCard;

    /**
     * 出生日期
     */
    @NotNull(message = "出生日期不能为空")
    @Past(message = "出生日期必须是过去的日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "出生日期", required = true)
    private LocalDate birthday;

    /**
     * 年龄
     */
    @Schema(description = "年龄")
    private Integer age;

    /**
     * 紧急联系人姓名
     */
    @NotBlank(message = "紧急联系人姓名不能为空")
    @Schema(description = "紧急联系人姓名", required = true)
    private String emergencyContactName;

    /**
     * 紧急联系人电话
     */
    @NotBlank(message = "紧急联系人电话不能为空")
    @Size(min = 11, max = 11, message = "紧急联系人电话必须为11位")
    @Schema(description = "紧急联系人电话", required = true)
    private String emergencyContactPhone;

    /**
     * 健康状况
     */
    @Schema(description = "健康状况")
    private String healthCondition;

    /**
     * 要绑定的家属ID列表
     */
    @Schema(description = "要绑定的家属ID列表")
    private List<Long> bindKinIds;
}
