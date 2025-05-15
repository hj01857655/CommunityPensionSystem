package com.communitypension.communitypensionadmin.pojo.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

/**
 * 新增体检报告DTO
 */
@Data
public class PhysicalExamReportCreateDTO {
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
    
    private List<PhysicalExamReportItemDTO> items;

    @Data
    public static class PhysicalExamReportItemDTO {
        private String itemName;
        private String itemValue;
        private String itemUnit;
        private Integer abnormalFlag;
        /**
         * 检查项目分类
         */
        private String category;
        /**
         * 备注
         */
        private String remark;
        /**
         * 正常参考范围
         */
        private String normalRange;
    }
} 