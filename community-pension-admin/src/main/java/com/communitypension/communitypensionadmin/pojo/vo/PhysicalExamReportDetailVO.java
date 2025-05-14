package com.communitypension.communitypensionadmin.pojo.vo;

import com.communitypension.communitypensionadmin.entity.PhysicalExamReport;
import com.communitypension.communitypensionadmin.entity.PhysicalExamReportItem;
import lombok.Data;
import java.util.List;

/**
 * 体检报告详情VO
 */
@Data
public class PhysicalExamReportDetailVO {
    /** 主表信息 */
    private PhysicalExamReport report;
    /** 指标子表列表 */
    private List<PhysicalExamReportItem> items;
} 