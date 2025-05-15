package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 体检报告指标子表实体类
 */
@Data
@TableName("physical_exam_report_item")
public class PhysicalExamReportItem {
    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 体检报告ID */
    private Long reportId;

    /** 指标名称 */
    private String itemName;

    /** 指标值 */
    private String itemValue;

    /** 单位 */
    private String itemUnit;

    /** 是否异常(0-正常 1-异常) */
    private Integer abnormalFlag;

    /**
     * 检查项目分类
     */
    @TableField("category")
    private String category;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
    
    /**
     * 正常参考范围
     */
    @TableField("normal_range")
    private String normalRange;

    // 为新增字段添加getter和setter方法
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public String getNormalRange() {
        return normalRange;
    }
    
    public void setNormalRange(String normalRange) {
        this.normalRange = normalRange;
    }
} 