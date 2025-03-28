package com.communitypension.communitypensionadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

// 使用Lombok注解@EqualsAndHashCode生成equals()和hashCode()方法，callSuper = true表示包含父类的属性
@EqualsAndHashCode(callSuper = true)
@Data
// 定义一个名为ElderDTO的类，继承自UserDTO类
public class ElderDTO  extends UserDTO {
    // 定义一个私有的String类型的属性idCard，用于存储老人的身份证号
    private String idCard;
    // 使用Jackson注解@JsonFormat指定日期格式为"yyyy-MM-dd"
    @JsonFormat(pattern = "yyyy-MM-dd")
    // 定义一个私有的String类型的属性birthday，用于存储老人的生日
    private LocalDate birthday;
    // 定义一个私有的long类型的属性age，用于存储老人的年龄
    private Integer age;
    // 定义一个私有的String类型的属性emergencyContactName，用于存储老人的紧急联系人姓名
    private String emergencyContactName;
    // 定义一个私有的String类型的属性emergencyContactPhone，用于存储老人的紧急联系人电话
    private String emergencyContactPhone;
    // 定义一个私有的String类型的属性healthCondition，用于存储老人的健康状况
    private String healthCondition;
}
