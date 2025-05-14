package com.communitypension.communitypensionadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.communitypension.communitypensionadmin.entity.PhysicalExamReport;
import java.util.List;

/**
 * 体检报告主表 Service 接口
 */
public interface PhysicalExamReportService extends IService<PhysicalExamReport> {
    /**
     * 根据老人ID查询体检报告列表
     */
    List<PhysicalExamReport> getReportsByElderId(Long elderId);
} 