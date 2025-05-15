package com.communitypension.communitypensionadmin.pojo.vo;

import com.communitypension.communitypensionadmin.entity.PhysicalExamReportItem;
import lombok.Data;

import java.util.List;

/**
 * 体检报告分类VO
 * 用于返回按分类分组的体检报告项目
 */
@Data
public class PhysicalExamCategoryVO {
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 该分类下的体检项目列表
     */
    private List<PhysicalExamReportItem> items;
    
    public PhysicalExamCategoryVO() {
    }
    
    public PhysicalExamCategoryVO(String categoryName, List<PhysicalExamReportItem> items) {
        this.categoryName = categoryName;
        this.items = items;
    }
} 