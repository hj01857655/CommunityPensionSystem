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
    private List<PhysicalExamReportCreateDTO.PhysicalExamReportItemDTO> items;
} 