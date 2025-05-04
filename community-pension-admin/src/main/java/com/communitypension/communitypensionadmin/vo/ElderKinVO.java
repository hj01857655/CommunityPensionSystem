package com.communitypension.communitypensionadmin.vo;

import lombok.Data;

import java.time.LocalDate;

/**
 * 老人家属关联VO
 */
@Data
public class ElderKinVO {
    /**
     * 老人信息
     */
    private Long elderId;
    private String elderUsername;
    private String elderName;
    private String elderGender;
    private String elderPhone;
    private String elderIdCard;
    private LocalDate elderBirthday;
    private Integer elderAge;
    private String elderAddress;
    private String elderHealthCondition;
    private String elderEmergencyContactName;
    private String elderEmergencyContactPhone;

    /**
     * 家属信息
     */
    private Long kinId;
    private String kinUsername;
    private String kinName;
    private String kinGender;
    private String kinPhone;
    private String kinEmail;
    private String kinAddress;

    /**
     * 关系信息
     */
    private String relationType;
} 