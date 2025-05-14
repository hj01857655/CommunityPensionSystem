package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.PhysicalExamReport;
import com.communitypension.communitypensionadmin.mapper.PhysicalExamReportMapper;
import com.communitypension.communitypensionadmin.service.PhysicalExamReportService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 体检报告主表 Service 实现类
 */
@Service
public class PhysicalExamReportServiceImpl extends ServiceImpl<PhysicalExamReportMapper, PhysicalExamReport> implements PhysicalExamReportService {
    @Override
    public List<PhysicalExamReport> getReportsByElderId(Long elderId) {
        return lambdaQuery().eq(PhysicalExamReport::getElderId, elderId).list();
    }
} 