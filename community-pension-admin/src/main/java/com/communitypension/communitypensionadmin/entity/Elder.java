package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("elder")
public class Elder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String gender;
    private Date birthday;
    private String idCard;
    private String phone;
    private String address;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String healthCondition;
    private String medicalHistory;
    private String allergy;
    private String avatar;
    private Date createTime;
    private Date updateTime;
    private String remark;
}