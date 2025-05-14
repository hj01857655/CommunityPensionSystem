package com.communitypension.communitypensionadmin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.communitypension.communitypensionadmin.entity.PhysicalExamReportItem;
import com.communitypension.communitypensionadmin.mapper.PhysicalExamReportItemMapper;
import com.communitypension.communitypensionadmin.service.PhysicalExamReportItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 体检报告指标子表 Service 实现类
 */
@Service
public class PhysicalExamReportItemServiceImpl extends ServiceImpl<PhysicalExamReportItemMapper, PhysicalExamReportItem> implements PhysicalExamReportItemService {
    @Override
    public List<PhysicalExamReportItem> getItemsByReportId(Long reportId) {
        return lambdaQuery().eq(PhysicalExamReportItem::getReportId, reportId).list();
    }
} 