package com.communitypension.communitypensionadmin.pojo.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class PhysicalExamReportUpdateDTO {
    private Long id;
    private Long elderId;
    private Long recorderId;
    private LocalDate date;
    private String hospital;
    private String mainResult;
    private String fileUrl;
    /**
     * 体检报告编号
     */
    private String reportNo;
    /**
     * 体检人员年龄
     */
    private Integer age;
    /**
     * 体检人员性别
     */
    private String gender;
    /**
     * 体检人员姓名
     */
    private String elderName;
    private List<PhysicalExamReportCreateDTO.PhysicalExamReportItemDTO> items;
} 