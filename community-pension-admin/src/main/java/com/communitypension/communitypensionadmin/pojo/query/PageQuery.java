package com.communitypension.communitypensionadmin.pojo.query;

import lombok.Data;

/**
 * 分页查询基类
 */
@Data
public class PageQuery {
    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 排序方式（asc/desc）
     */
    private String orderType;
} 