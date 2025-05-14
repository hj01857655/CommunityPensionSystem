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
    private List<PhysicalExamReportItemDTO> items;

    @Data
    public static class PhysicalExamReportItemDTO {
        private String itemName;
        private String itemValue;
        private String itemUnit;
        private Integer abnormalFlag;
    }
} 