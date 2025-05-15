package com.communitypension.communitypensionadmin.constant;

import java.util.List;

/**
 * 体检报告项目分类常量
 */
public class PhysicalExamCategories {
    public static final String GENERAL = "一般检查";
    public static final String BLOOD = "抽血检查";
    public static final String XRAY = "X光检查";
    public static final String ULTRASOUND = "B超检查";
    public static final String SPECIAL = "特殊检查";
    public static final String GYNECOLOGY = "妇科检查";
    public static final String OTHER = "其他";
    
    /**
     * 获取所有分类
     * @return 分类列表
     */
    public static List<String> getAllCategories() {
        return List.of(GENERAL, BLOOD, XRAY, ULTRASOUND, SPECIAL, GYNECOLOGY, OTHER);
    }
} 