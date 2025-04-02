package com.communitypension.communitypensionadmin.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 字典数据VO
 */
@Data
public class DictDataVO {
    /**
     * 字典编码
     */
    private Long dictCode;
    
    /**
     * 字典排序
     */
    private Integer dictSort;
    
    /**
     * 字典标签
     */
    private String dictLabel;
    
    /**
     * 字典键值
     */
    private String dictValue;
    
    /**
     * 字典类型
     */
    private String dictType;
    
    /**
     * 样式属性（其他样式扩展）
     */
    private String cssClass;
    
    /**
     * 表格回显样式
     */
    private String listClass;
    
    /**
     * 是否默认（Y是 N否）
     */
    private String isDefault;
    
    /**
     * 状态（0正常 1停用）
     */
    private Integer status;
    
    /**
     * 状态名称
     */
    private String statusName;
    
    /**
     * 创建者
     */
    private String createBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新者
     */
    private String updateBy;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 备注
     */
    private String remark;
}
