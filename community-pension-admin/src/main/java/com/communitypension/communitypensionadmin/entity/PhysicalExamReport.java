package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 体检报告主表实体类
 */
@Data
@TableName("physical_exam_report")
public class PhysicalExamReport {
    /** 体检报告ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 老人ID */
    private Long elderId;

    /** 上传人ID */
    private Long recorderId;

    /** 体检日期 */
    private LocalDate date;

    /** 体检医院 */
    private String hospital;

    /** 主要结论 */
    private String mainResult;

    /** 报告文件URL */
    private String fileUrl;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 体检报告编号
     */
    @TableField("report_no")
    private String reportNo;

    /**
     * 体检人员年龄
     */
    @TableField("age")
    private Integer age;

    /**
     * 体检人员性别
     */
    @TableField("gender")
    private String gender;

    /**
     * 体检人员姓名
     */
    @TableField("elder_name")
    private String elderName;

    // 为新增字段添加getter和setter方法
    public String getReportNo() {
        return reportNo;
    }

    public void setReportNo(String reportNo) {
        this.reportNo = reportNo;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getElderName() {
        return elderName;
    }

    public void setElderName(String elderName) {
        this.elderName = elderName;
    }
} 