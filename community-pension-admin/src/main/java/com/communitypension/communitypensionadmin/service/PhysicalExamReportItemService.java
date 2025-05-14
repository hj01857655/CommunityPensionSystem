package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.entity.PhysicalExamReportItem;
import java.util.List;

/**
 * 体检报告指标子表 Service 接口
 */
public interface PhysicalExamReportItemService extends IService<PhysicalExamReportItem> {
    /**
     * 根据体检报告ID查询指标列表
     */
    List<PhysicalExamReportItem> getItemsByReportId(Long reportId);
} 