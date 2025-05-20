package com.communitypension.communitypensionadmin.pojo.vo;

import com.communitypension.communitypensionadmin.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 家属用户视图对象
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "家属用户信息VO")
public class KinUserVO extends UserVO {

    /**
     * 绑定的老人ID
     */
    @Schema(description = "绑定的老人ID")
    private Long elderId;

    /**
     * 绑定的老人姓名
     */
    @Schema(description = "绑定的老人姓名")
    private String elderName;

    /**
     * 与老人的关系类型
     */
    @Schema(description = "与老人的关系类型")
    private String relationType;

    // 转换方法已移至 UserConverter 接口
}